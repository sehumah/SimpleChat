package com.lab5.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.parse.*

class ChatActivity : AppCompatActivity() {

    private var etMessageCompose: EditText? = null
    private var ibSend: ImageButton? = null

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
    }


    // Get the userId from the cached currentUser object
    private fun startWithCurrentUser() {
        // TODO: implement functionality
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

        // When send button is clicked, create message object on Parse
        ibSend!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val data: String = etMessageCompose!!.getText().toString()
                val message = ParseObject.create("Message")
                message.put(USER_ID_KEY, ParseUser.getCurrentUser().objectId)
                message.put(BODY_KEY, data)
                message.saveInBackground(object : SaveCallback {
                    override fun done(e: ParseException?) {
                        if (e == null) {
                            Toast.makeText(this@ChatActivity, "Successfully created message on Parse!", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Log.e(TAG, "Failed to save message.", e)
                        }
                    }
                })
                etMessageCompose!!.setText(null)
            }
        })
    }


    companion object {
        private const val TAG: String = "ChatActivity"
        private val USER_ID_KEY = "userId"
        private val BODY_KEY = "body"
    }
}
