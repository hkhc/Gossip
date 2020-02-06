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

import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractIHLog(protected var defaultTag: String = "LOG") : IHLog {

    // TODO we shall not resolve the lambda here. It may cause unnecessary lambda execution if log is filtered

    abstract override fun log(priority: Priority, tag: String?, message: String)

    override fun getLogTag(): String = defaultTag

    override fun exception(tag: String?, message: String?, throwable: Throwable) {

        message?.let {
            log(Priority.Error, tag, it)
        }

        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter, false)
        throwable.printStackTrace(printWriter)
        printWriter.flush()
        log(Priority.Error, tag, stringWriter.toString())
    }
}
