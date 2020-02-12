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

package io.hkhc.gossip.message

import io.hkhc.gossip.model.Member
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset

class JoinGroupMessage(): Message(ID) {

    companion object {
        const val ID = 1
    }

    constructor(m: Member): this() {
        member = m
    }

    lateinit var member: Member

    override fun decode(data: ByteBuf) {
        val id = data.readShort().toInt()
        val nameLen = data.readShort().toInt()
        val name = data.readCharSequence(nameLen, Charset.defaultCharset()).toString()
        val readIndex = data.readInt()

        member = Member(id, name, readIndex)
    }

    override fun encode(): ByteBuf {

        val buf = Unpooled.buffer(64)

        buf.writeShort(member.id)
        buf.writeShort(member.name.length)
        buf.writeCharSequence(member.name, Charset.defaultCharset())
        buf.writeInt(member.lastReadMessageIndex)
        return buf

    }

    override fun toString()
        = "JoinGroupMessage(${type}, ${timestamp}, ${member})"

}
