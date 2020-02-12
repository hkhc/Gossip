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
import io.hkhc.utils.nonNullLet
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ServerMessageHandler(
    val channelMap: ChannelMap,
    val msgBlock: (Int, Message) -> (Unit)
): ChannelInboundHandlerAdapter() {

    override fun channelRead(oCtx: ChannelHandlerContext?, oMsg: Any?) {
        nonNullLet(oCtx, oMsg) { ctx, msg ->

            l.debug("ServiceMessageHandler read ${msg}")

            if (msg is Message) {
                val memberId = channelMap.getMember(ctx.channel())
                memberId?.let { msgBlock.invoke(it, msg) }
            }

        }
    }
}
