package com.lab5.simplechat

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * This is a custom list adapter class which provides data to RecyclerView.
 * It renders the message_incoming.xml or message_outgoing.xml in list by pre-filling appropriate information.
 */

class ChatAdapter(private val context: Context, private val userId: String, private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private val MESSAGE_OUTGOING: Int = 123
    private val MESSAGE_INCOMING: Int = 321

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ??? {
        // TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ???, position: Int) {
        // TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    // return view type (incoming or outgoing) based on the message position
    override fun getItemViewType(position: Int): Int {
        if (isMe(position)) {
            return MESSAGE_OUTGOING
        }
        else {
            return MESSAGE_INCOMING
        }
    }

    private fun isMe(position: Int): Boolean {
        val message = messages.get(position)
        return message.getUserId() != null && message.getUserId().equals(userId)
    }
}
