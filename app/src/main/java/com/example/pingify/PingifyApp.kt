package com.example.pingify

import android.app.Application
import com.example.pingify.data.network.NetworkClient
import com.example.pingify.data.network.STREAM_API_KEY
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel

class PingifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(this)
        ChatClient.Builder(STREAM_API_KEY, this)
            .logLevel(ChatLogLevel.NOTHING)
            .build()
    }
}
