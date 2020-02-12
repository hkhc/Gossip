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

import io.hkhc.gossip.message.MessageFactory
import io.hkhc.log.l
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder

class MessageDecoder :
    ReplayingDecoder<MessageDecoder.State>(State.READ_LENGTH) {

    private var len = 0
    private var type = 0
    private var timestamp: Long = 0
    private var oData: ByteBuf? = null

    enum class State {
        READ_LENGTH,
        READ_TYPE,
        READ_TIMESTAMP,
        READ_BODY
    }

    private fun outputMessage(output: MutableList<Any>) {
        var oMsg = MessageFactory.create(type, timestamp)
        oMsg?.let { msg ->
            oData?.let { data -> msg.decode(data) }
            output.add(msg)
        }
    }

    override fun decode(
        optCtx: ChannelHandlerContext?,
        optInBuf: ByteBuf?,
        optOut: MutableList<Any>?
    ) {

        io.hkhc.utils.nonNullLet(optInBuf, optOut) { inBuf, out ->
            when (state()) {
                State.READ_LENGTH -> {
                    len = inBuf.readShort().toInt()
                    checkpoint(State.READ_TYPE)
                }
                State.READ_TYPE -> {
                    type = inBuf.readByte().toInt()
                    checkpoint(State.READ_TIMESTAMP)
                }
                State.READ_TIMESTAMP -> {
                    timestamp = inBuf.readLong()
                    if (len>0)
                        checkpoint(State.READ_BODY)
                    else {
                        checkpoint(State.READ_LENGTH)
                        outputMessage(out)
                    }
                }
                State.READ_BODY -> {
                    oData = inBuf.readBytes(len-7)
                    checkpoint(State.READ_LENGTH)
                    outputMessage(out)
                }
                else -> {
                    l.err("Error in decoding HelloRequest")
                }
            }
        }
    }
}
