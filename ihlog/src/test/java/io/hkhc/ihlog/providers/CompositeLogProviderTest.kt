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
import io.hkhc.log.providers.CompositeLogProvider
import io.hkhc.log.providers.PrintWriterLogProvider
import io.hkhc.test.assertj.isKInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter

class CompositeLogProviderTest {

    private lateinit var logProvider1: Pair<PrintWriterLogProvider, StringWriter>
    private lateinit var logProvider2: Pair<PrintWriterLogProvider, StringWriter>
    lateinit var provider: IHLogProvider

    @Before
    fun setUp() {
        logProvider1 = newPrintWriterLogProvider()
        logProvider2 = newPrintWriterLogProvider()
        provider = newCompositeLogProvider()
    }

    private fun newPrintWriterLogProvider() = StringWriter().let {
        Pair(
            PrintWriterLogProvider(PrintWriter(it, true), MockTimeSource(1)),
            it
        )
    }

    private fun newCompositeLogProvider() = CompositeLogProvider(logProvider1.first, logProvider2.first)

    @Test
    fun `getLog shall return an instance of IHLog`() {

        // when
        val log = provider.getLog("LOG")

        assertThat(log).isKInstanceOf(CompositeLogProvider.CompositeLog::class)
    }

    @Test
    fun `PrintWriterLog shall print the log to writer`() {

        PrintWriterLogProviderTest().`assert print log`(provider) { logProvider1.second.toString() }
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())
    }

    @Test
    fun `PrintWriterLog shall use the tag provided`() {

        // when
        PrintWriterLogProviderTest().`assert log with tag provided`(provider, "LOG") { logProvider1.second.toString() }
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())

        setUp()

        PrintWriterLogProviderTest().`assert log with tag provided`(provider, "LOG2") { logProvider1.second.toString() }
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())
    }

    @Test
    fun `PrintWriterLog shall be able to use overrided tag`() {

        // when

        PrintWriterLogProviderTest().`assert handle overrided tag`(provider) { logProvider1.second.toString() }

        // then
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())
    }

    @Test
    fun `PrintWriterLog shall be able to handle different priority`() {

        PrintWriterLogProviderTest().`PrintWriterLog shall be able to handle priorities`(provider) { logProvider1.second.toString() }

        // then
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())
    }

    @Test
    fun `PrintWriterLog shall be able to handle multiline messages`() {

        // then
        PrintWriterLogProviderTest().`assert logging multiline string`(provider) { logProvider1.second.toString() }
        assertThat(logProvider1.second.toString()).isEqualTo(logProvider2.second.toString())
    }

    @Test
    fun `PrintWriterLog shall be able to handle exception`() {

        PrintWriterLogProviderTest().`assert logging exception`(provider) { logProvider1.second.toString() }
    }
}
