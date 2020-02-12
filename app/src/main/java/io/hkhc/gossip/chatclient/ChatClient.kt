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

package io.hkhc.gossip.chatclient

import io.hkhc.gossip.chatserver.ChannelDisposable
import io.hkhc.gossip.chatserver.MessageDecoder
import io.hkhc.gossip.chatserver.MessageEncoder
import io.hkhc.gossip.message.Message
import io.hkhc.log.l
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.net.InetAddress

class ChatClient() {

    private var mInitCompletable: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private var host: InetAddress? = null
    private var hostStr: String? = null
    private var port: Int = -1

    val startFinished: Completable = mInitCompletable
        .observeOn(AndroidSchedulers.mainThread())
        .filter { it==true }.firstOrError().ignoreElement()

    fun config(host: InetAddress, port:Int) {
        this.host = host
        this.port = port
    }

    fun config(hostStr: String, port: Int) {
        this.hostStr = hostStr
        this.port = port
    }

    fun send(msg: Message) {

        channel?.let {
            val future = it.writeAndFlush(msg)
            future.addListener { ChannelFutureListener.CLOSE_ON_FAILURE }
            l.debug("client message sent")

        }

    }

    private var channel: Channel? = null

    fun start(msgBlock: (Message) -> (Unit)): Completable {

        return  Completable.create { emitter ->

            l.debug("Client starting...")

            val workerGroup: EventLoopGroup = NioEventLoopGroup()

            try {
                Bootstrap().apply {
                    group(workerGroup)
                    channel(NioSocketChannel::class.java)
                    option(ChannelOption.SO_KEEPALIVE, true)
                    handler(object: ChannelInitializer<SocketChannel>() {
                        override fun initChannel(optCh: SocketChannel?) {
                            optCh?.let { ch ->
                                ch.pipeline()
                                    .addLast(MessageEncoder())
                                    .addLast(MessageDecoder())
                                    .addLast(
                                        ClientMessageHandler(
                                            msgBlock,
                                            { c -> channel = c }
                                        )
                                    )
                            }
                        }
                    })

                    if (hostStr!=null && host==null) {
                        host = InetAddress.getByName(hostStr)
                    }

                    val future = connect(host, port).sync()

                    val clientChannel = future.channel()
                    emitter.setDisposable(ChannelDisposable(clientChannel))

                    l.debug("Client started.")
                    // notify initialization finished
                    mInitCompletable.onNext(true)


                    clientChannel.closeFuture().sync()

                }
            }
            finally {
                workerGroup.shutdownGracefully()
                emitter.onComplete()
            }

        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    }


}
