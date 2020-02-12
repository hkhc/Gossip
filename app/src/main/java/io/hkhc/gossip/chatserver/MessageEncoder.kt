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

package io.hkhc.gossip.chatserver

import io.hkhc.gossip.message.Message
import io.hkhc.log.l
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MessageEncoder : MessageToByteEncoder<Message>(){

    override fun encode(oCtx: ChannelHandlerContext?, oMsg: Message?, oOut: ByteBuf?) {

        io.hkhc.utils.nonNullLet(oCtx, oMsg, oOut) { ctx, msg, out ->

            val type = msg.getMessageType()
            val data = msg.encode()
            val dataLen = data.writerIndex()

            val packetLen = dataLen+7

            val buf = ctx.alloc().buffer(packetLen)
            buf.writeShort(packetLen)
            buf.writeByte(type)
            buf.writeLong(msg.timestamp)
            if (dataLen>0)
                buf.writeBytes(data)
            out.writeBytes(buf)

        } ?: l.debug("Null value")
    }

}
