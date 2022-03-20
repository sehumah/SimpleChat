package com.lab5.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.parse.ParseAnonymousUtils
import com.parse.ParseUser

class ChatActivity : AppCompatActivity() {
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

    companion object {
        private const val TAG: String = "ChatActivity"
    }
}
