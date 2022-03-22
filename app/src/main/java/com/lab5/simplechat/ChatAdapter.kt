package com.lab5.simplechat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.security.MessageDigest


/**
 * This is a custom list adapter class which provides data to RecyclerView.
 * It renders the message_incoming.xml or message_outgoing.xml in list by pre-filling appropriate information.
 */

class ChatAdapter(
    context: Context,
    private val userId: String,
    private val messages: List<Message>
    ):
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val mContext: Context = context
    private val mUserId: String = userId
    private val mMessages: List<Message> = messages

    // Create a gravatar image based on the hash value obtained from userId
    fun getProfileURL(userId: String): String {  // responsible for decoding user ID and creating an image URL that can be passed into the Glide library
        var hex: String = ""
        try {
            val digest: MessageDigest = MessageDigest.getInstance("MD5")
            val hash: ByteArray = digest.digest(userId.toByteArray())
            val bigInt: BigInteger = BigInteger(hash)
            hex = bigInt.abs().toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "https://www.gravatar.com/avatar/$hex?d=identicon"
    }

    // define the class as abstract meaning it can't have any instances
    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {

        fun MessageViewHolder(itemView: View) {
            super.itemView
        }

        abstract fun bindMessage(message: Message)
    }

    /*   Create different view layouts to easily distinguish the messages in the chat   */

    inner class IncomingMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {  // incoming messages view holder
        private var ivProfileOther: ImageView
        private var tvBody: TextView
        private var tvName: TextView

        init {
            super.itemView
            ivProfileOther = itemView.findViewById(R.id.iv_profile_other)
            tvBody = itemView.findViewById(R.id.tv_body)
            tvName = itemView.findViewById(R.id.tv_name)
        }

        override fun bindMessage(message: Message) {
            Glide.with(mContext)
                .load(message.getUserId()?.let { getProfileURL(it) })
                .circleCrop()  // create an effect of a round profile picture
                .into(ivProfileOther)
            tvBody.text = message.getBody()
            tvName.text = message.getUserId()  // in addition to message, show userId
        }
    }


    inner class OutgoingMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {  // outgoing messages view holder
        private var ivProfileMe: ImageView
        private var tvBody: TextView

        init {
            super.itemView
            ivProfileMe = itemView.findViewById(R.id.iv_profile_me)
            tvBody = itemView.findViewById(R.id.tv_body)
        }

        override fun bindMessage(message: Message) {
            Glide.with(mContext)
                .load(message.getUserId()?.let { getProfileURL(it) })
                .circleCrop()  // create an effect of a round profile picture
                .into(ivProfileMe)
            tvBody.text = message.getBody()
        }
    }



    /*   Now, assign correct view holders based on a view type   */

    private val MESSAGE_OUTGOING: Int = 123
    private val MESSAGE_INCOMING: Int = 321

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)

        if (viewType == MESSAGE_INCOMING) {
            val contactView: View = inflater.inflate(R.layout.message_incoming, parent, false)
            return IncomingMessageViewHolder(contactView)
        }
        else if (viewType == MESSAGE_OUTGOING) {
            val contactView: View = inflater.inflate(R.layout.message_outgoing, parent, false)
            return OutgoingMessageViewHolder(contactView)
        }
        else {
            throw IllegalArgumentException("Unknown view type")
        }
    }


    // tell ChatAdapter how and when to bind different message types to different view types
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message: Message = mMessages.get(position)
        holder.bindMessage(message)
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
