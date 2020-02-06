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

@Suppress("TooManyFunctions")
interface IHLog {

    /* IHLog class are required to implemented the following */

    fun exception(tag: String?, message: String?, throwable: Throwable)

    /**
     * Implement the provider specific way for logging.
     * @param priority - Priority of log
     * @param tag - optional tag string. If it is null, the provider shall use its own default tag
     * @param message - the log message
     */
    fun log(priority: Priority, tag: String?, message: String)
    fun log(priority: Priority, tag: String?, lambda: () -> String) = log(priority, tag, lambda())

    fun getLogTag(): String

    /* helpers */

    fun debug(tag: String, message: String) = log(Priority.Debug, tag, message)
    fun debug(message: String) = log(Priority.Debug, null, message)
    fun debug(tag: String, lambda: () -> String) = log(Priority.Debug, tag, lambda)
    fun debug(lambda: () -> String) = log(Priority.Debug, null, lambda)

    fun info(tag: String, message: String) = log(Priority.Info, tag, message)
    fun info(message: String) = log(Priority.Info, null, message)
    fun info(tag: String, lambda: () -> String) = log(Priority.Info, tag, lambda)
    fun info(lambda: () -> String) = log(Priority.Info, null, lambda)

    fun warn(tag: String, message: String) = log(Priority.Warn, tag, message)
    fun warn(message: String) = log(Priority.Warn, null, message)
    fun warn(tag: String, lambda: () -> String) = log(Priority.Warn, tag, lambda)
    fun warn(lambda: () -> String) = log(Priority.Warn, null, lambda)

    fun trace(tag: String, message: String) = log(Priority.Trace, tag, message)
    fun trace(message: String) = log(Priority.Trace, null, message)
    fun trace(tag: String, lambda: () -> String) = log(Priority.Trace, tag, lambda)
    fun trace(lambda: () -> String) = log(Priority.Trace, null, lambda)

    fun fatal(tag: String, message: String) = log(Priority.Fatal, tag, message)
    fun fatal(message: String) = log(Priority.Fatal, null, message)
    fun fatal(tag: String, lambda: () -> String) = log(Priority.Fatal, tag, lambda)
    fun fatal(lambda: () -> String) = log(Priority.Fatal, null, lambda)

    fun err(tag: String, message: String) = log(Priority.Error, tag, message)
    fun err(message: String) = log(Priority.Error, null, message)
    fun err(tag: String, lambda: () -> String) = log(Priority.Error, tag, lambda)
    fun err(lambda: () -> String) = log(Priority.Error, null, lambda)
    fun err(tag: String, throwable: Throwable) = exception(tag, null, throwable)
}
