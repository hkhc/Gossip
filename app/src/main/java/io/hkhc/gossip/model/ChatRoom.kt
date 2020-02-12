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

package io.hkhc.gossip.model

import io.hkhc.gossip.chatclient.ChatClient
import io.hkhc.gossip.chatserver.ChatServer
import io.hkhc.gossip.message.ChatMessage
import io.hkhc.gossip.message.JoinGroupMessage
import io.hkhc.gossip.message.Message
import io.hkhc.gossip.message.QuitGroupMessage
import io.hkhc.log.l
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ChatRoom {

    var messages: List<ChatMessage> = mutableListOf()
    var members: MutableMap<Int, Member> = mutableMapOf()

    var clientMemberId: Int = 0

    private var messagesEmitter: ObservableEmitter<List<ChatMessage>>? = null

    val messagesObservable: Observable<List<ChatMessage>> = Observable.create { emitter ->
        messagesEmitter = emitter
    }

    fun removeMemberById(memberId: Int) {

        members.remove(memberId)

    }

    fun clientBlock(client: ChatClient) : (Message) -> (Unit) {

        return { message ->

            l.debug("Client Received message $message")

            when(message) {
                is JoinGroupMessage -> {
                    members[message.member.id] = message.member
                }
                is QuitGroupMessage -> {
                    removeMemberById(message.memberId)
                }
                is ChatMessage -> {
                    messages += message
                    messagesEmitter?.onNext(messages)
                }
            }
        }
    }

    fun serverBlock(server: ChatServer) : (Int, Message) -> (Unit) {

        return { memberId , message ->
            l.debug("Server Received message $message")

            when(message) {
                is JoinGroupMessage -> {
                    members[message.member.id] = message.member
                    server.sendAll(message)
                    members.forEach {
                        if (it.key!=message.member.id) {
                            server.send(message.member.id, JoinGroupMessage(it.value))
                        }
                    }
                }
                is QuitGroupMessage -> {
                    removeMemberById(message.memberId)
                    server.sendAll(QuitGroupMessage(message.memberId))
                }
                is ChatMessage -> {
                    server.sendAll(ChatMessage(message, memberId))
                }
            }

        }

    }


}
