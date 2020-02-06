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

import android.os.Build
import android.util.Log
import io.hkhc.log.AbstractIHLog
import io.hkhc.log.IHLog
import io.hkhc.log.IHLogProvider
import io.hkhc.log.Priority

class AndroidLogProvider(private var currentOSVersion: Int = Build.VERSION.SDK_INT) : IHLogProvider {

    private fun isAndroidN() = currentOSVersion >= Build.VERSION_CODES.N

    override fun getLog(defaultTag: String): IHLog {

        return if (isAndroidN())
            NAndroidIHLog(defaultTag)
        else
            PreNAndroidIHLog(defaultTag)
    }

    open class NAndroidIHLog(defaultTag: String) : AbstractIHLog(defaultTag) {

        override fun log(priority: Priority, tag: String?, message: String) {

            val level = when (priority) {
                Priority.Info -> Log.INFO
                Priority.Trace -> Log.INFO
                Priority.Debug -> Log.DEBUG
                Priority.Error -> Log.ERROR
                Priority.Fatal -> Log.ASSERT
                Priority.Warn -> Log.WARN
            }

            message.lineSequence().forEach { line ->
                Log.println(level, tag ?: defaultTag, line)
            }
        }
    }

    class PreNAndroidIHLog(tag: String) : NAndroidIHLog(tag) {

        private val preNLogTagSize = 23
        private val primaryTag: String
        private val secondaryTag: String

        init {

            if (tag.length>preNLogTagSize) {
                primaryTag = tag.substring(0, preNLogTagSize)
                secondaryTag = tag.substring(preNLogTagSize)
            } else {
                primaryTag = tag
                secondaryTag = ""
            }
        }

        override fun log(priority: Priority, tag: String?, message: String) {

            if (tag == null)
                log(priority, defaultTag, message)
            else {
                if (tag.length>preNLogTagSize) {
                    val pTag = tag.substring(0, preNLogTagSize)
                    val sTag = tag.substring(preNLogTagSize)
                    message.lineSequence().forEach { line ->
                        super.log(priority, pTag, "$sTag: $line")
                    }
                } else
                    super.log(priority, tag, message)
            }
        }
    }
}
