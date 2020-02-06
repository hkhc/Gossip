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

package io.hkhc.log.internal

import io.hkhc.log.providers.NullLogProvider
import io.hkhc.log.providers.PrintWriterLogProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class LogFactoryTest {

    @Before
    fun setUp() {
        LogFactory.clear()
    }

    @Test
    fun `create tag`() {
        // given
        LogFactory.metaTag=""
        // then
        assertThat(LogFactory.createTag(LogFactoryTest::class.java)).isEqualTo("LFT")
        // when
        LogFactory.metaTag="TEST"
        assertThat(LogFactory.createTag(LogFactoryTest::class.java)).isEqualTo("TEST_LFT")
    }

    @Test
    fun `log meta tag`() {

        // given
        LogFactory.metaTag = "TEST"

        // when
        val log1 = LogFactory.getLog(LogFactoryTest::class.java)

        // then
        assertThat(log1.getLogTag()).isEqualTo("TEST_LFT")

    }

    @Test
    fun `log meta tag change is active immediately`() {

        // given
        LogFactory.metaTag = "TEST"

        // when
        var log1 = LogFactory.getLog(LogFactoryTest::class.java)

        // then
        assertThat(log1.getLogTag()).isEqualTo("TEST_LFT")

        // when
        LogFactory.metaTag = "APPLE"
        log1 = LogFactory.getLog(LogFactoryTest::class.java)

        // then
        assertThat(log1.getLogTag()).isEqualTo("APPLE_LFT")

    }

    @Test
    fun `log is cached`() {

        // given
        val log1 = LogFactory.getLog(LogFactoryTest::class.java)
        val log2 = LogFactory.getLog(LogFactoryTest::class.java)

        // then
        assertThat(log1).isSameAs(log2)

    }

    @Test
    fun `log with different tag`() {

        // given
        val log1 = LogFactory.getLog(LogFactoryTest::class.java)
        val log2 = LogFactory.getLog(TagMakerTest::class.java)

        // then
        assertThat(log1).isNotSameAs(log2)

    }

    @Test
    fun `change default provider`() {

        // given
        var log1 = LogFactory.getLog(LogFactoryTest::class.java)

        // then
        assertThat(log1).isInstanceOf(PrintWriterLogProvider.PrintWriterLog::class.java)

        // when
        LogFactory.defaultProvider = NullLogProvider()
        log1 = LogFactory.getLog(TagMakerTest::class.java)

        // then
        assertThat(log1).isInstanceOf(NullLogProvider.NullIHLog::class.java)

    }

}