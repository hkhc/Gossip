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

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TagMakerTest {

    @Test
    fun `single character class names`() {
        assertThat(TagMaker.getClassNameAbbr("A")).isEqualTo("A")

        // OK it is not normal to have all small letter class name, but we have to be generic
        assertThat(TagMaker.getClassNameAbbr("a")).isEqualTo("A")
    }

    @Test
    fun `two-character class names`() {
        TagMaker.apply {
            assertThat(getClassNameAbbr("AB")).isEqualTo("AB")
            assertThat(getClassNameAbbr("Ab")).isEqualTo("Ab")
            assertThat(getClassNameAbbr("aB")).isEqualTo("AB")
            assertThat(getClassNameAbbr("ab")).isEqualTo("Ab")
            assertThat(getClassNameAbbr("A1")).isEqualTo("A1")
            assertThat(getClassNameAbbr("a1")).isEqualTo("A1")
            assertThat(getClassNameAbbr("1A")).isEqualTo("1A")
            assertThat(getClassNameAbbr("1a")).isEqualTo("1a")
        }
    }

    @Test
    fun `three-character class names`() {
        TagMaker.apply {
            assertThat(getClassNameAbbr("ABC")).isEqualTo("ABC")
            assertThat(getClassNameAbbr("AbC")).isEqualTo("AbC")
            assertThat(getClassNameAbbr("Abc")).isEqualTo("Ab")
            assertThat(getClassNameAbbr("ABc")).isEqualTo("ABc")
            assertThat(getClassNameAbbr("aBC")).isEqualTo("ABC")
            assertThat(getClassNameAbbr("abC")).isEqualTo("AbC")
            assertThat(getClassNameAbbr("aBc")).isEqualTo("ABc")
            assertThat(getClassNameAbbr("abC")).isEqualTo("AbC")
            assertThat(getClassNameAbbr("a1C")).isEqualTo("A1C")
            assertThat(getClassNameAbbr("a1C")).isEqualTo("A1C")
            assertThat(getClassNameAbbr("1AC")).isEqualTo("1AC")
        }
    }

    @Test
    fun `four-character class names`() {
        TagMaker.apply {
            assertThat(getClassNameAbbr("ABCD")).isEqualTo("ABCD")
            assertThat(getClassNameAbbr("ABCd")).isEqualTo("ABC")
            assertThat(getClassNameAbbr("ABcD")).isEqualTo("ABD")
            assertThat(getClassNameAbbr("ABcd")).isEqualTo("ABc")
            assertThat(getClassNameAbbr("AbCD")).isEqualTo("ACD")
            assertThat(getClassNameAbbr("AbCd")).isEqualTo("AbCd")
            assertThat(getClassNameAbbr("AbcD")).isEqualTo("AbD")
            assertThat(getClassNameAbbr("Abcd")).isEqualTo("Ab")
            assertThat(getClassNameAbbr("aBCD")).isEqualTo("ABCD")
            assertThat(getClassNameAbbr("aBCd")).isEqualTo("ABC")
            assertThat(getClassNameAbbr("aBdC")).isEqualTo("ABC")
            assertThat(getClassNameAbbr("aBdc")).isEqualTo("ABd")
            assertThat(getClassNameAbbr("abCD")).isEqualTo("ACD")
            assertThat(getClassNameAbbr("abCd")).isEqualTo("AbCd")
            assertThat(getClassNameAbbr("abcD")).isEqualTo("AbD")
            assertThat(getClassNameAbbr("abcd")).isEqualTo("Ab")
        }
    }

    @Test
    fun `longer class names`() {
        TagMaker.apply {
            assertThat(getClassNameAbbr("AbcDef")).isEqualTo("AbDe")
            assertThat(getClassNameAbbr("AbcDefGhi")).isEqualTo("ADG")
            assertThat(getClassNameAbbr("abcDef")).isEqualTo("AbDe")
            assertThat(getClassNameAbbr("abcDefGhi")).isEqualTo("ADG")
        }
    }
}
