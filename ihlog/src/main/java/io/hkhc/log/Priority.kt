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

sealed class Priority(val value: Int) {

    fun shouldBeFilteredBy(p: Priority) =
        p.value > value

    object Trace : Priority(1)
    object Debug : Priority(2)
    object Info : Priority(3)
    object Warn : Priority(4)
    object Error : Priority(5)
    object Fatal : Priority(6)
}
