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
import io.hkhc.log.SystemTimeSource
import io.hkhc.log.TimeSource
import java.io.PrintWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Locale

open class PrintWriterLogProvider(
    private var printWriter: PrintWriter,
    private var timeSource: TimeSource = SystemTimeSource()
) : IHLogProvider {

    constructor(writer: Writer) :
            this(PrintWriter(writer, true))

    constructor(writer: Writer, timeSource: TimeSource) :
            this(PrintWriter(writer, true), timeSource)

    override fun getLog(defaultTag: String): IHLog =
        PrintWriterLog(defaultTag, printWriter, timeSource)

    fun getLogWriter(): PrintWriter {
        return printWriter
    }

    class PrintWriterLog(
        defaultTag: String,
        var writer: PrintWriter,
        private var timeSource: TimeSource
    ) : AbstractIHLog(defaultTag) {

        private var dateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
        private var pidStr: String? = null
        private val useTimestamp = true

        init {

            val pid = getProcessID()
            pidStr = if (pid == 0) {
                "-"
            } else {
                pid.toString()
            }
        }

        private fun getProcessID(): Int {
            return try {
                val clazz = Class.forName("android.os.Process")
                val m = clazz.getMethod("myPid")
                m.invoke(null) as Int
            }
            catch (t: ClassNotFoundException) { 0 }
            catch (t: NoSuchMethodException) { 0 }
            catch (t: SecurityException) { 0 }
        }

        private fun getVerbosityChar(priority: Priority) =
            when (priority) {
                Priority.Info -> 'i'
                Priority.Warn -> 'w'
                Priority.Debug -> 'd'
                Priority.Fatal -> 'f'
                Priority.Error -> 'e'
                Priority.Trace -> 't'
            }

        // TODO decouple login for log one line and log string in general
        private fun logImpl(priority: Priority, tag: String?, msg: String) {
//            if (!filter(tag)) return
            val timestamp =
                if (useTimestamp)
                    dateFormat.format(timeSource.getTime()) + ' '
                else
                    ""
            val prefix = "$timestamp $pidStr/${tag ?: defaultTag} ${getVerbosityChar(priority)}/"
            writer.println("$prefix$msg")
        }

        override fun log(priority: Priority, tag: String?, message: String) {
            message.lineSequence().forEach { line ->
                logImpl(priority, tag, line)
            }
        }
    }
}
