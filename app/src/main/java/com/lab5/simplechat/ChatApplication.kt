package com.lab5.simplechat

import android.app.Application
import com.parse.Parse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class ChatApplication : Application () {

    override fun onCreate() {
        super.onCreate()

        // use for monitoring Parse network traffic
        val builder = OkHttpClient.Builder ()
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        // can be Level.BASIC, Level.HEADERS, Level.BODY
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // any network interceptors must be added with the Configuration Builder given this syntax
        builder.networkInterceptors().add(httpLoggingInterceptor)

        // set applicationId and server based on the values in the Back4App settings
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(R.string.applicationId.toString())
                .clientKey(R.string.clientKey.toString())
                .server("https://parseapi.back4app.com")
                .clientBuilder(builder)
                .build()
        )
    }
}
