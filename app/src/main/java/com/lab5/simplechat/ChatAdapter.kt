package com.lab5.simplechat

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * This is a custom list adapter class which provides data to RecyclerView.
 * It renders the message_incoming.xml or message_outgoing.xml in list by pre-filling appropriate information.
 */

class ChatAdapter(private val context: Context, private val userId: String, private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ??? {
        // TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ???, position: Int) {
        // TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
