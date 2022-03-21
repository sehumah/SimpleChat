package com.lab5.simplechat

import com.parse.ParseClassName
import com.parse.ParseObject


/**
 * This model class will provide message data for the RecyclerView, and will be used to retrieve and save messages to Parse.
 */

@ParseClassName("Message")
class Message : ParseObject () {
    val USER_ID_KEY: String = "userId"
    val BODY_KEY: String = "body"

    fun setUserId(userId: String) {
        put(USER_ID_KEY, userId)
    }

    fun setBody(body: String) {
        put(BODY_KEY, body)
    }

    fun getUserId(): String? {
        return getString(USER_ID_KEY)
    }

    fun getBody(): String? {
        return getString(BODY_KEY)
    }
}
