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

package io.hkhc.gossip.testbed

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.hkhc.gossip.R
import io.hkhc.gossip.chatclient.ChatClient
import io.hkhc.gossip.chatserver.ChatServer
import io.hkhc.gossip.databinding.FragmentTestbedBinding
import io.hkhc.gossip.message.ChatMessage
import io.hkhc.gossip.message.JoinGroupMessage
import io.hkhc.gossip.model.ChatRoom
import io.hkhc.gossip.model.Member
import io.hkhc.gossip.nds.NSDHelper
import io.hkhc.log.l
import io.hkhc.viewmodel.ViewModelFactory
import io.hkhc.viewmodel.resolve
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.security.SecureRandom
import javax.inject.Inject

class TestbedFragment: DaggerFragment() {

    companion object {
        private const val DEFAULT_PORT = 12340
    }


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: FragmentTestbedBinding
    private lateinit var viewModel: TestbedViewModel

    private val chatroom = ChatRoom()

    private var client: ChatClient = ChatClient()
    private val server = ChatServer(chatroom, DEFAULT_PORT)

    private val nsdHelper = NSDHelper()

    private var serviceInfo: NsdServiceInfo? = null

    private lateinit var args: TestbedFragmentArgs

    private var clientMemberId: Int = 0
    
    private var messageDisposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val secureRandom = SecureRandom()
        clientMemberId = secureRandom.nextInt(Short.MAX_VALUE.toInt())

        chatroom.clientMemberId = clientMemberId

        val _args: TestbedFragmentArgs by navArgs()
        args = _args

        // activity should not be null in onCreateView
        val act = activity!!

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_testbed,
            container,
            false)
        viewModel = viewModelFactory.resolve(this)

        /*
        Setup Data Binding
         */
        binding.lifecycleOwner = this
        binding.vm = viewModel

        binding.send.setOnClickListener() {
            if (!binding.entry.text.isEmpty()) {
                sendMessage(binding.entry.text.toString())
                binding.entry.setText("")
            }

        }

        messageDisposable = chatroom.messagesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                (binding.list.adapter as MessageListAdapter?)?.updateList()
                Handler().post {
                    binding.list.scrollToPosition(chatroom.messages.size-1);
                }
            }

        binding.list.apply {
            adapter = MessageListAdapter(chatroom)
            layoutManager = LinearLayoutManager(activity!!, VERTICAL, false)
        }

        return binding.root

    }

    fun sendMessage(msg: String) {
        client.send(ChatMessage(msg))
    }

    private var serverDisposable: Disposable? = null
    private var clientDisposable: Disposable? = null

    override fun onDestroyView() {
        super.onDestroyView()
        nsdHelper.tearDown()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // for client mode after service is discovered
        nsdHelper.init(activity!!) { serviceInfo ->
            this@TestbedFragment.serviceInfo = serviceInfo
            client.config(serviceInfo.host, serviceInfo.port)
            client.let { c ->
                clientDisposable = c.start{ message ->
                    when(message) {
                        is JoinGroupMessage -> {
                            if (message.member.id!=clientMemberId &&
                                chatroom.members[message.member.id]==null) {
                                Snackbar.make(
                                    binding.root,
                                    resources.getString(
                                        R.string.member_joined,
                                        message.member.name
                                    ),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    chatroom.clientBlock(client).invoke(message)


                }.subscribe {
                    l.debug("client shutdown")
                }
            }
        }

        // for host mode
        if (args.hostMode) {
            nsdHelper.registerService(DEFAULT_PORT)
            serverDisposable = server.start(chatroom.serverBlock(server)).subscribe {
                l.debug("Server shutdown")
            }
            server.startFinished.andThen {
                client.config("localhost", DEFAULT_PORT)
                clientDisposable = client.start(chatroom.clientBlock(client)).subscribe {
                    l.debug("client shutdown")
                }
            }
                .subscribe()
        }
        else {
            nsdHelper.discoverService()
        }

        @Suppress("UnusedReturnValue")
        client.startFinished.subscribe {
            val bar = Snackbar.make(
                binding.root,
                resources.getString(R.string.connection_success),
                Snackbar.LENGTH_SHORT
            ).show()
            client.send(
                JoinGroupMessage(Member(clientMemberId, args.memberName, 1))
            )
            binding.send.isEnabled = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        nsdHelper.unregisterService()
        clientDisposable?.dispose()
        serverDisposable?.dispose()
        messageDisposable?.dispose()
    }
}
