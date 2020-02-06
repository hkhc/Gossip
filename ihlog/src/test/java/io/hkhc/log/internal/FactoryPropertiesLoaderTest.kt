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

import io.hkhc.log.providers.StringLogProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FactoryPropertiesLoaderTest {

    @Test
    fun `test loading defaultResource`() {

        // given
        val loader = FactoryPropertiesLoader()

        // when
        val inputStream = loader.getStreamFromResource("/ihlog-valid.properties")

        // then
        assertThat(inputStream).isNotNull()

    }

    @Test
    fun `test loading non-existence resource`() {

        // given
        val loader = FactoryPropertiesLoader()

        // when
        val inputStream = loader.getStreamFromResource("/ihlog-not-exist.properties")

        // then
        assertThat(inputStream).isNull()

    }

    @Test
    fun `test getting provider class name`() {

        // given
        val loader = FactoryPropertiesLoader()

        // when
        val providerClassName = loader.getProviderClassName("/ihlog-valid.properties")

        // then
        assertThat(providerClassName).isEqualTo("io.hkhc.log.providers.StringLogProvider")

    }


    @Test
    fun `test loading provider class`() {

        // given
        val loader = FactoryPropertiesLoader()

        // when
        val provider = loader.loadProvider("/ihlog-valid.properties")

        // then
        assertThat(provider).isInstanceOf(StringLogProvider::class.java)

    }


}
