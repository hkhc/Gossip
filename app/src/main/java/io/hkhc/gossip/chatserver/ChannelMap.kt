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

import io.hkhc.log.l
import io.netty.channel.Channel

class ChannelMap {

    val memberToChannel = mutableMapOf<Int, Channel>()
    val channelToMember = mutableMapOf<Channel, Int>()

    fun registerMember(memberId: Int, channel: Channel) {

        if (memberToChannel[memberId]==channel) return

        unregisterMember(memberId)

        memberToChannel[memberId] = channel
        channelToMember[channel] = memberId

        l.info("Register new member ${memberId}")

    }

    fun getMember(channel: Channel) = channelToMember[channel]
    fun getChannel(memberId: Int) = memberToChannel[memberId]
    fun getAllChannels() = channelToMember.keys

    fun unregisterMember(memberId: Int) {
        val channel = memberToChannel.remove(memberId)
        channelToMember.remove(channel)
    }

}
