package com.lab5.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parse.*
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity() {

    private val MAX_CHAT_MESSAGES_TO_SHOW: Int = 50

    private lateinit var etMessageCompose: EditText
    private lateinit var ibSend: ImageButton
    private lateinit var rvChats: RecyclerView
    private lateinit var mMessages: ArrayList<Message>
    private var mFirstLoad = false
    private lateinit var mAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser()
        }
        else { // If not logged in, login as a new anonymous user
            login()
        }

        /* configure to listen for any newly created Message object */

        // load existing messages to begin with
        refreshMessages()

        // Make sure the Parse server is setup to configured for live queries
        val webSocketURL: String = "https://parseapi.back4app.com"  // "wss://PASTE_SERVER_WEBSOCKET_URL_HERE" TYPE IN A VALID WSS:// URL HERE
        var parseLiveQueryClient: ParseLiveQueryClient? = null
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(URI(webSocketURL))
        }
        catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        val parseQuery: ParseQuery<Message> = ParseQuery.getQuery(Message::class.java)
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // connect to Parse server
        val subscriptionHandling: SubscriptionHandling<Message> = parseLiveQueryClient?.subscribe(parseQuery) as SubscriptionHandling<Message>

        // listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE) { query: ParseQuery<Message>, `object`: Message ->

            mMessages.add(0, `object`)

            // RecyclerView updates need to be run on the UI thread
            runOnUiThread(object: Runnable{
                override fun run() {
                    mAdapter.notifyDataSetChanged()
                    rvChats.scrollToPosition(0)
                }
            })
        }

        /* insert everything here */



        /* end of insertion */
    }

    // Get the userId from the cached currentUser object
    private fun startWithCurrentUser() {
        setUpMessagePosting()
    }


    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private fun login() {
        ParseAnonymousUtils.logIn { user, e ->
            if (e != null) {
                Log.e(TAG, "Anonymous login failed: ", e)
            } else {
                startWithCurrentUser()
            }
        }
    }


    // Set up button event handler which posts the entered message to Parse
    private fun setUpMessagePosting() {
        // reference the text field and button
        etMessageCompose = findViewById<View>(R.id.et_message_compose) as EditText
        ibSend = findViewById<View>(R.id.ib_send) as ImageButton
        rvChats = findViewById(R.id.rv_chats)
        mMessages = ArrayList()
        mFirstLoad = true
        val userId: String = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(this, userId, mMessages)
        rvChats.adapter = mAdapter

        // associate the layout manager with the recycler view
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        rvChats.layoutManager = linearLayoutManager

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val data: String = etMessageCompose.getText().toString()

                /* below 3 lines not needed anymore */
                // val message = ParseObject.create("Message")
                // message.put(USER_ID_KEY, ParseUser.getCurrentUser().objectId)
                // message.put(BODY_KEY, data)

                if (data.isNotEmpty()) {
                    // Using new `Message.kt` Parse-backed model now
                    val message = Message()
                    message.setUserId(ParseUser.getCurrentUser().objectId)
                    message.setBody(data)

                    message.saveInBackground(object : SaveCallback {
                        override fun done(e: ParseException?) {
                            Toast.makeText(this@ChatActivity, "Successfully created message on Parse!", Toast.LENGTH_SHORT).show()
                            refreshMessages()
                            // if (e == null) {  } else { Log.e(TAG, "Failed to save message.", e) }
                        }
                    })
                    etMessageCompose.setText(null)
                }
                else {
                    Toast.makeText(this@ChatActivity, "Empty body. Type a message!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Query messages from Parse so we can load them into the chat adapter
    fun refreshMessages() {
        // construct query to execute
        val query: ParseQuery<Message> = ParseQuery.getQuery(Message::class.java)

        // configure limit and sort order
        query.limit = MAX_CHAT_MESSAGES_TO_SHOW

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt")

        // Execute query to fetch all messages from Parse asynchronously (This is equivalent to a SELECT query with SQL)
        query.findInBackground { messages, e ->
            if (e == null) {
                mMessages.clear()
                mMessages.addAll(messages)
                mAdapter.notifyDataSetChanged()  // update adapter

                // scroll to the bottom of the list on initial load
                if (mFirstLoad) {
                    rvChats.scrollToPosition(0)
                    mFirstLoad = false
                }
            }
            else {
                Log.e(TAG, "Error loading messages: $e")
            }
        }
    }

    // create a handler which can run code periodically
    private val POLL_INTERVAL: Long = TimeUnit.SECONDS.toMillis(3)
    val handler: Handler = android.os.Handler()
    val mRefreshMessagesRunnable: Runnable = object : Runnable {
        override fun run() {
            refreshMessages()
            handler.postDelayed(this, POLL_INTERVAL)
        }
    }

    // onResume gets called when the Activity is ready to be resumed and about to be displayed to the user
    override fun onResume() {
        super.onResume()
        // handler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL)  // Only start checking for new messages when the app becomes active in foreground
    }

    // onPause gets called when a current Activity is about to go into background
    override fun onPause() {
        // Stop background task from refreshing messages, to avoid unnecessary traffic & battery drain
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }



    companion object {
        private const val TAG: String = "ChatActivity"
        private const val USER_ID_KEY = "userId"
        private const val BODY_KEY = "body"
    }
}
