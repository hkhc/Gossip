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
import io.hkhc.gossip.model.ChatRoom
import io.hkhc.log.l
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class ChatServer(val chatroom: ChatRoom, val port:Int = 8123) {

    private var mInitCompletable: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    val startFinished: Completable = mInitCompletable
        .observeOn(AndroidSchedulers.mainThread())
        .filter { it==true }.firstOrError().ignoreElement()

    private val channelMap = ChannelMap()

    fun send(memberId: Int, msg: Message) {

        val channel = channelMap.getChannel(memberId)
        channel?.let {
            val future = it.writeAndFlush(msg)
            future.addListener { ChannelFutureListener.CLOSE_ON_FAILURE }
        }

    }

    fun sendAll(msg: Message) {

        channelMap.getAllChannels().forEach { channel ->
            l.debug("send message ${msg} to member ${channelMap.channelToMember[channel]}")
            val future = channel.writeAndFlush(msg)
            future.addListener { ChannelFutureListener.CLOSE_ON_FAILURE }
        }


    }

    fun start(block: (Int, Message) -> (Unit)): Completable {

        return  Completable.create { emitter ->

            l.debug("Server starting...")

            val bossGroup = NioEventLoopGroup()
            val workerGroup = NioEventLoopGroup()
            try {

                ServerBootstrap().apply {

                    group(bossGroup, workerGroup)
                    channel(NioServerSocketChannel::class.java)
                    childHandler(object: ChannelInitializer<SocketChannel>() {
                        override fun initChannel(oCh: SocketChannel?) {
                            oCh?.let { ch ->
                                ch.pipeline()
                                    .addLast(MessageDecoder())
                                    .addLast(MessageEncoder())
                                    .addLast(ServerGroupHandler(channelMap))
                                    .addLast(ServerMessageHandler(channelMap, block))
                            }
                        }
                    })
                    option(ChannelOption.SO_BACKLOG, 128)
                    childOption(ChannelOption.SO_KEEPALIVE, true)

                    val future = bind(port).sync()

                    val serverChannel = future.channel()
                    emitter.setDisposable(ChannelDisposable(serverChannel))

                    l.debug("Server started.")
                    // notify initialization finished
                    mInitCompletable.onNext(true)

                    serverChannel.closeFuture().sync()
                }

            }
            finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
                emitter.onComplete()
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    }

}
