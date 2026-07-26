package com.example.smart_watch.presentation

import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {

    private const val URL = "http://10.0.2.2:3000"

    val socket: Socket = IO.socket(URL)

}