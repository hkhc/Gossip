/*
 * Copyright (c) 2019. Herman Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package io.hkhc.providers

import android.util.Log
import io.hkhc.log.IHLogProvider
import io.hkhc.log.Priority
import io.hkhc.log.providers.AndroidLogProvider
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter

class AndroidLogProviderTest {

    init {
        mockkStatic(Log::class)
    }
    private fun newLogProvider() = AndroidLogProvider()

    // TODO handle tag for preN and N implementations

//    @Test
//    fun `test determining max tag size for pre N OS`() {
//
//        // given
//        val provider = AndroidIHLogProvider(23)
//
//        // then
//        assertThat(provider.isTagTooLong("01234567890123456789012")).isFalse()
//        assertThat(provider.isTagTooLong("012345678901234567890123")).isTrue()
////        }
////        else if (apiLevel==24) {
////            assertThat(provider.isTagTooLong("01234567890123456789012")).isFalse()
////            assertThat(provider.isTagTooLong("012345678901234567890123")).isFalse()
////        }
////        else {
////            fail("Unexpected api level ${apiLevel}")
////        }
//    }
//
//    @Test
//    fun `test determining max tag size for N OS and after`() {
//
//        // given
//        val provider = AndroidIHLogProvider(24)
//
//        // then
//        assertThat(provider.isTagTooLong("01234567890123456789012")).isFalse()
//        assertThat(provider.isTagTooLong("012345678901234567890123")).isFalse()
//
//    }

    @Test
    fun `test null tag`() {

        // given
        val provider = AndroidLogProvider(24)

        // when
        provider.getLog("LOG").log(Priority.Debug,null, "Hello")

        // then
        verify { Log.println(Log.DEBUG,"LOG" , "Hello") }
    }

    @Test
    fun `test extra tag for pre N OS`() {

        // given
        val provider = AndroidLogProvider(23)

        // when
        provider.getLog("012345678901234567890123456789" /* 30 char */).log(Priority.Debug,null,"Hello")

        // then
        verify { Log.println(Log.DEBUG,"01234567890123456789012" /* 23 char */ , "3456789: Hello") }
    }

    @Test
    fun `test extra tag for pre N OS and multi-line message`() {

        // given
        val provider = AndroidLogProvider(23)

        // when
        provider.getLog("012345678901234567890123456789" /* 30 char */).log(Priority.Debug,null,"Hello\nWorld")

        // then
        verify { Log.println(Log.DEBUG,"01234567890123456789012" /* 23 char */ , "3456789: Hello") }
        verify { Log.println(Log.DEBUG,"01234567890123456789012" /* 23 char */ , "3456789: World") }
    }

    @Test
    fun `getLog shall return an instance of IHLog`() {

        // given
        val provider = newLogProvider()

        // when
        val log = provider.getLog("LOG")

        // then
        Assert.assertNotNull(log)
    }


    @Test
    fun `Android Log shall print the log to logcat`() {

        // given
        val provider= newLogProvider()

        // when
        provider.getLog("LOG").log(Priority.Debug,null,"Hello")

        // then
        verify { Log.println(Log.DEBUG,"LOG", "Hello") }
    }

    fun `assert print log`(provider: IHLogProvider, getLogString: () -> String) {

        val log = provider.getLog("LOG")
        log.log(Priority.Debug,null, "Hello")

        verify { Log.println(Log.DEBUG, "LOG", getLogString()) }

//        Assert.assertEquals("01-01 08:00:00.001  -/LOG d/Hello\n", getLogString())
    }

    @Test
    fun `Android Log shall use the tag provided`() {

        newLogProvider().also { provider ->
            `assert log with tag provided`(provider, "LOG")
        }

        newLogProvider().also { provider ->
            `assert log with tag provided`(provider, "LOG2")
        }
    }

    fun `assert log with tag provided`(provider: IHLogProvider, tag: String) {

        val log = provider.getLog(tag)
        val logMessage = "Hello"
        log.log(Priority.Debug,null, logMessage)

        verify { Log.println(Log.DEBUG, "LOG", logMessage) }
    }

    @Test
    fun `PrintWriterLog shall be able to use overrided tag`() {

        newLogProvider().also { provider->
            `assert handle overrided tag`(provider)

        }
    }

    fun `assert handle overrided tag`(provider: IHLogProvider) {

        val log = provider.getLog("LOG")

        log.log(Priority.Debug,null, "Hello")
        log.log(Priority.Debug,"LOG2", "World")
        log.log(Priority.Debug,null, "Hello World")
        verify {
            Log.println(Log.DEBUG, "LOG", "Hello")
            Log.println(Log.DEBUG, "LOG2", "World")
            Log.println(Log.DEBUG, "LOG", "Hello World")
        }
    }

    @Test
    fun `PrintWriterLog shall be able to handle different priority`() {

        newLogProvider().also { provider ->
            `assert handle priorities`(provider)
        }
    }

    fun `assert handle priorities`(logProvider: IHLogProvider) {

        val log = logProvider.getLog("LOG")

        log.log(Priority.Debug,null, "** DEBUG")
        log.log(Priority.Info, null, "** INFO")
        log.log(Priority.Warn, null, "** WARN")
        log.log(Priority.Fatal, null, "** FATAL")
        log.log(Priority.Error, null, "** ERROR")
        log.log(Priority.Trace, null, "** TRACE")

        verify {
            Log.println(Log.DEBUG, "LOG", "** DEBUG")
            Log.println(Log.INFO, "LOG", "** INFO")
            Log.println(Log.WARN, "LOG", "** WARN")
            Log.println(Log.ASSERT, "LOG", "** FATAL")
            Log.println(Log.ERROR, "LOG", "** ERROR")
            Log.println(Log.INFO, "LOG", "** TRACE")
        }
    }

    @Test
    fun `PrintWriterLog shall be able to handle multiline messages`() {

        newLogProvider().also { provider ->
            `assert logging multiline string`(provider)
        }
    }

    fun `assert logging multiline string`(provider: IHLogProvider) {

        val log = provider.getLog("LOG")

        log.log(Priority.Debug,null, """
                Hello
                World
                Hello World
            """.trimIndent())

        verify {
            Log.println(Log.DEBUG, "LOG", "Hello")
            Log.println(Log.DEBUG,"LOG", "World")
            Log.println(Log.DEBUG,"LOG", "Hello World")
        }
    }

    @Test
    fun `AndroidIHLog shall be able to handle exception`() {

        newLogProvider().also { provider ->
            `assert logging exception`(provider)
        }
    }

    fun `assert logging exception`(provider: IHLogProvider) {

        val log = provider.getLog("LOG")

        val ex = Exception()

        log.exception(null, null, ex)

        // Then compare the log output and a stacktrace line by line

        StringWriter().also { ex.printStackTrace(PrintWriter(it, true)) }
            .toString()
            .lineSequence()
            .forEach {
                verify {
                    Log.println(Log.ERROR, "LOG", it)
                }
            }
    }
}
