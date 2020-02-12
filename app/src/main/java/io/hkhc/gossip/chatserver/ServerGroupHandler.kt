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

import io.hkhc.gossip.message.JoinGroupMessage
import io.hkhc.gossip.message.QuitGroupMessage
import io.hkhc.utils.nonNullLet
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ServerGroupHandler(val channelMap: ChannelMap) : ChannelInboundHandlerAdapter() {

    override fun channelRead(oCtx: ChannelHandlerContext?, oMsg: Any?) {

        nonNullLet(oCtx, oMsg) { ctx, msg ->
            when(msg) {
                is JoinGroupMessage -> channelMap.registerMember(msg.member.id, ctx.channel())
                is QuitGroupMessage -> {
                    channelMap.getChannel(msg.memberId)?.let { it.close() }
                    channelMap.unregisterMember(msg.memberId)
                }
            }
        }

        oCtx!!.fireChannelRead(oMsg)


    }
}
