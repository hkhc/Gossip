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

package io.hkhc.ihlog.providers

import io.hkhc.log.IHLogProvider
import io.hkhc.log.Priority
import io.hkhc.log.providers.PrintWriterLogProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter

class PrintWriterLogProviderTest {

    lateinit var timeSource: MockTimeSource

    @Before
    fun setUp() {
        timeSource = MockTimeSource(1)
    }

    private fun newLogProvider() = StringWriter().let {
        Pair(
            PrintWriterLogProvider(PrintWriter(it, true), timeSource),
            it
        )
    }

    @Test
    fun `getLog shall return an instance of IHLog`() {

        // given
        val (provider, _) = newLogProvider()

        // when
        val log = provider.getLog("LOG")

        Assert.assertNotNull(log)
    }

    @Test
    fun `PrintWriterLog shall print the log to writer`() {

        // given
        val (provider, writer) = newLogProvider()

        // when
        `assert print log`(provider) { writer.toString() }
    }

    fun `assert print log`(provider: IHLogProvider, getLogString: () -> String) {

        val log = provider.getLog("LOG")
        log.log(Priority.Debug,null, "Hello")

        Assert.assertEquals("01-01 08:00:00.001  -/LOG d/Hello\n", getLogString())
    }

    @Test
    fun `PrintWriterLog shall use the tag provided`() {

        newLogProvider().also { (provider, writer) ->
            `assert log with tag provided`(provider, "LOG") { writer.toString() }
        }

        setUp()

        newLogProvider().also { (provider, writer) ->
            `assert log with tag provided`(provider, "LOG2") { writer.toString() }
        }
    }

    fun `assert log with tag provided`(provider: IHLogProvider, tag: String, getLogString: ()->String) {

        val log = provider.getLog(tag)
        log.log(Priority.Debug,null, "Hello")
        Assert.assertEquals("01-01 08:00:00.001  -/$tag d/Hello\n", getLogString())
    }

    @Test
    fun `PrintWriterLog shall be able to use overrided tag`() {

        newLogProvider().also { (provider, writer) ->
            `assert handle overrided tag`(provider) { writer.toString() }
        }
    }

    fun `assert handle overrided tag`(provider: IHLogProvider, getLogString: ()->String) {

        val log = provider.getLog("LOG")

        log.log(Priority.Debug,null, "Hello")
        log.log(Priority.Debug,"LOG2", "World")
        log.log(Priority.Debug,null, "Hello World")
        Assert.assertEquals(
            """
                    01-01 08:00:00.001  -/LOG d/Hello
                    01-01 08:00:00.002  -/LOG2 d/World
                    01-01 08:00:00.003  -/LOG d/Hello World

                """.trimIndent(), getLogString())
    }

    @Test
    fun `PrintWriterLog shall be able to handle different priority`() {

        newLogProvider().also { (provider, writer) ->
            `PrintWriterLog shall be able to handle priorities`(provider) { writer.toString() }
        }
    }

    fun `PrintWriterLog shall be able to handle priorities`(logProvider: IHLogProvider, getLogString: ()->String) {

        val log = logProvider.getLog("LOG")

        log.log(Priority.Debug,null, "** DEBUG")
        log.log(Priority.Info, null, "** INFO")
        log.log(Priority.Warn, null, "** WARN")
        log.log(Priority.Fatal, null, "** FATAL")
        log.log(Priority.Error, null, "** ERROR")
        log.log(Priority.Trace, null, "** TRACE")

        Assert.assertEquals(
            """
                    01-01 08:00:00.001  -/LOG d/** DEBUG
                    01-01 08:00:00.002  -/LOG i/** INFO
                    01-01 08:00:00.003  -/LOG w/** WARN
                    01-01 08:00:00.004  -/LOG f/** FATAL
                    01-01 08:00:00.005  -/LOG e/** ERROR
                    01-01 08:00:00.006  -/LOG t/** TRACE

                """.trimIndent(), getLogString())
    }

    @Test
    fun `test null tag`() {

        // given
        newLogProvider().also { (provider, writer) ->

            // when
            provider.getLog("LOG").log(Priority.Debug,null, "Hello")

            // then
            Assert.assertEquals(
                """
                    01-01 08:00:00.001  -/LOG d/Hello

                """.trimIndent(), writer.toString())
        }
    }

    @Test
    fun `PrintWriterLog shall be able to handle multiline messages`() {

        newLogProvider().also { (provider, writer) ->
            `assert logging multiline string`(provider) { writer.toString() }
        }
    }

    fun `assert logging multiline string`(provider: IHLogProvider, getLogString: ()->String) {

        val log = provider.getLog("LOG")

        log.log(Priority.Debug,null, """
                Hello
                World
                Hello World
            """.trimIndent())

        Assert.assertEquals(
            """
                    01-01 08:00:00.001  -/LOG d/Hello
                    01-01 08:00:00.002  -/LOG d/World
                    01-01 08:00:00.003  -/LOG d/Hello World

                """.trimIndent(), getLogString())
    }

    @Test
    fun `PrintWriterLog shall be able to handle exception`() {

        newLogProvider().also { (provider, writer) ->

            `assert logging exception`(provider) { writer.toString() }
        }
    }

    fun `assert logging exception`(provider: IHLogProvider, getLogString: () -> String) {

        val log = provider.getLog("LOG")

        val ex = Exception()

        log.exception(null, null, ex)

        // Then compare the log output and a stacktrace line by line

        val logIterator = getLogString().lineSequence().iterator()

        StringWriter().also { ex.printStackTrace(PrintWriter(it, true)) }
            .toString()
            .lineSequence()
            .forEach {
                val logLine = logIterator.next()
                Assert.assertTrue("expect \"${it}\" actual \"${logLine}\"", logLine.contains(it))
            }
    }
}
