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

import io.hkhc.ihlog.providers.MockTimeSource
import io.hkhc.log.Priority
import io.hkhc.log.providers.StringLogProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Test
import java.io.StringWriter

class StringLogProviderTest {

    private fun newLogProvider(time: Long) = StringLogProvider(StringWriter(), MockTimeSource(time))


    @Test
    fun `getLog shall return an instance of IHLog`() {

        // given
        val provider = StringLogProvider()

        // when
        val log = provider.getLog("LOG")

        // then
        Assert.assertNotNull(log)

    }


    @Test
    fun `String Log shall print the log to logcat`() {

        // given
        val provider= newLogProvider(0)

        // when
        provider.getLog("LOG").log(Priority.Debug,null, "Hello")

        assertThat(provider.getLogString()).isEqualTo("01-01 08:00:00.000  -/LOG d/Hello\n")

    }

}