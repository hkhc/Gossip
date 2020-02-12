/*
 * Copyright (c) 2020. Herman Cheung
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

package io.hkhc.utils

inline fun <A1, A2, R> nonNullLet(a1: A1?, a2: A2?, block: (A1, A2) -> (R)): R? {
    return if (a1!=null && a2!=null)
        block(a1, a2)
    else
        null
}

inline fun <A1, A2, R> Pair<A1,A2>.nonNullLet(block: (A1, A2) -> (R)) =
    nonNullLet(first, second, block)

inline fun <A1, A2, A3, R> nonNullLet(a1: A1?, a2: A2?, a3: A3?, block: (A1, A2, A3) -> (R)): R? {
    return if (a1!=null && a2!=null && a3!=null)
        block(a1, a2, a3)
    else
        null
}

inline fun <A1, A2, A3, R> Triple<A1,A2,A3>.nonNullLet(block: (A1, A2, A3) -> (R)) =
    nonNullLet(first, second, third, block)
