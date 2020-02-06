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

import io.hkhc.log.Priority
import io.hkhc.log.providers.NullLogProvider
import org.junit.Assert
import org.junit.Test

class NullLogProviderTest {

    @Test
    fun `getLog shall return an instance of IHLog`() {

        // given
        val provider = NullLogProvider()

        // when
        val log = provider.getLog("LOG")

        Assert.assertNotNull(log)

    }

    @Test
    fun `NullIHLog shall do nothing with log method`() {

        // given
        val log = NullLogProvider().getLog("LOG")

        log.log(Priority.Debug,null,  "Hello")

    }


}
