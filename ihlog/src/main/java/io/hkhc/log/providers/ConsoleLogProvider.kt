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

package io.hkhc.log.providers

import io.hkhc.log.AbstractIHLog
import io.hkhc.log.IHLog
import io.hkhc.log.IHLogProvider
import io.hkhc.log.Priority

class ConsoleLogProvider(
    var outLogger: PrintWriterLogProvider,
    var errLogger: PrintWriterLogProvider
) : IHLogProvider {

    override fun getLog(defaultTag: String): IHLog =
            ConsoleLog(defaultTag, outLogger.getLog(defaultTag), errLogger.getLog(defaultTag))

    class ConsoleLog(defaultTag: String, var outLogger: IHLog, var errLogger: IHLog)
        : AbstractIHLog(defaultTag) {

        override fun log(priority: Priority, tag: String?, message: String) {
            outLogger.log(priority, tag, message)
        }
    }
}
