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

import io.hkhc.log.MetaTag
import io.hkhc.log.providers.SimpleMetaTag
import java.util.StringTokenizer

object TagMaker {

    var metaTagPolicy: MetaTag = SimpleMetaTag("")
        set(value) {
            field = value
            mMetaTag = field.getTag()
        }

    private var mMetaTag: String = ""

    val metaTag: String
        get() = mMetaTag

    fun getPackageNameAbbr(packageName: String): String {

        val builder = StringBuilder()

        val token = StringTokenizer(packageName, ".")
        while (token.hasMoreTokens()) {
            val t = token.nextToken()
            builder.append(t[0].toUpperCase())
        }

        return builder.toString()
    }

    /**
     * Given a class object, create a abbreviate string that represent it.
     * 1. package name is ignored
     * 2. take all capital letters and digits
     * 3. if there are less than two capital letters, the letter after those capital letters will
     * also be included. For example,
     * `
     * getClassNameAbbr("OrangeBananaApple").equals("OBA")
     * getClassNameAbbr("OrangeBanana").equals("OrBa")
     * getClassNameAbbr("AOrange").equals("AOr")
     *
     * @param className class name to be abbreviated
     * @return abbreviated string
     */
    fun getClassNameAbbr(className: String): String {

        val name = className
                .substringAfterLast(".")
                .substringBefore("\$Companion")
                .substringBefore("\$\$")

        // TODO room to optimize: reduce number of stringbuilder
        // Shortgroup can only have at most char. So it can be a char array
        val longBuilder = StringBuilder()
        val shortBuilder = StringBuilder()

        val nameArray = name.toCharArray()
        nameArray[0] = nameArray[0].toUpperCase() // always treat the first as capital
        var isLastCap = false
        var shortGroup = 0
        for (c in nameArray) {
            val isCap = Character.isUpperCase(c) || Character.isDigit(c)
            // long version
            if (isCap) {
                longBuilder.append(c)
            }
            // short version
            if (shortGroup <= 2) {
                if (isCap) {
                    shortBuilder.append(c)
                    shortGroup++
                } else if (isLastCap) {
                    shortBuilder.append(c)
                }
            }

            isLastCap = isCap
        }

        return if (shortGroup > 2) {
            longBuilder.toString()
        } else {
            shortBuilder.toString()
        }
    }

    /**
     * Create log tag from class name by abbreviation, and trim the result tag name
     * to 23 characters max. It is also the tag size limitation to Android Log.
     * @param clazz
     * @return
     */
    fun getLogTag(clazz: Class<*>): String {
        val delimiter = if (metaTag == "") "" else "_"
        return "${metaTag}${delimiter}${getClassNameAbbr(clazz.name)}"
    }
}
