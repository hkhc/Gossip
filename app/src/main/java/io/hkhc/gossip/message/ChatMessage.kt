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

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset

class ChatMessage(): Message(ID) {

    companion object {
        const val ID = 4
    }

    constructor(text: String): this() {
        this.text = text
        this.memberId = memberId
    }

    constructor(originalMessage: ChatMessage, memberId: Int): this() {
        this.text = originalMessage.text
        this.timestamp = originalMessage.timestamp
        this.memberId = memberId
    }

    // memberId is ignored by server.
    // Server will use the channel map to determine the sender
    // Client uses memberId to determine sender
    var memberId: Int = -1
    var text: String = ""

    override fun decode(data: ByteBuf) {
        memberId = data.readShort().toInt()
        val textLen = data.readShort().toInt()
        text = data.readCharSequence(textLen, Charset.defaultCharset()).toString()
    }

    override fun encode(): ByteBuf {

        val buf = Unpooled.buffer(64)

        buf.writeShort(memberId.toInt())
        buf.writeShort(text.length)
        buf.writeCharSequence(text, Charset.defaultCharset())
        return buf

    }

    override fun toString()
            = "ChatMessage(${type}, ${timestamp}, ${text})"


    override fun equals(other: Any?): Boolean {

        if (other is ChatMessage?) {

            if (other==null) return false

            if (text!=other?.text) return false
            if (memberId!=other?.memberId) return false
            if (timestamp!=other?.timestamp) return false

            return true

        }

        return false


    }

}
