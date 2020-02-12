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

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.hkhc.gossip.R
import io.hkhc.gossip.model.ChatRoom
import io.hkhc.log.l

class MessageListAdapter(val chatRoom: ChatRoom): RecyclerView.Adapter<MessageItemViewHolder>() {

    fun updateList() {
        notifyItemInserted(chatRoom.messages.size-1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false) as ViewGroup

        return MessageItemViewHolder(view)

    }

    override fun getItemCount(): Int {
        return chatRoom.messages.size
    }

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) {

        with(chatRoom.messages[position]) {
            holder.memberName.text = chatRoom.members[memberId]?.name ?: "[Unknown member]"
            holder.message.text = text

            if (memberId==chatRoom.clientMemberId) {
                holder.memberName.gravity = Gravity.END
                holder.message.gravity = Gravity.END
            }
            else {
                holder.memberName.gravity = Gravity.START
                holder.message.gravity = Gravity.START
            }



        }

    }
}
