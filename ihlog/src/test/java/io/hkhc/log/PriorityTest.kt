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

package io.hkhc.log

import io.hkhc.ihlog.providers.MockTimeSource
import io.hkhc.log.internal.LogFactory
import io.hkhc.log.providers.StringLogProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.StringWriter

class PriorityTest {

    @Test
    fun `test shouldBeFiltered`() {

        assertThat(Priority.Warn.shouldBeFilteredBy(Priority.Error)).isTrue()
        assertThat(Priority.Warn.shouldBeFilteredBy(Priority.Trace)).isFalse()

    }

    @Test
    fun `test log filtering`() {

        // given
        val provider = StringLogProvider(StringWriter(), MockTimeSource(0))
        val log = provider.getLog("HELLO")

        // when
        LogFactory.logLevel = Priority.Warn
        log.trace("Log trace") // should not logged
        log.warn("Log warn") // should be logged
        log.err("Log err") // should be logged

        // then
        assertThat(provider.getLogString()).isEqualTo(
            """
                01-01 08:00:00.000  -/HELLO w/Log warn
                01-01 08:00:00.001  -/HELLO e/Log err

            """.trimIndent())


    }

}