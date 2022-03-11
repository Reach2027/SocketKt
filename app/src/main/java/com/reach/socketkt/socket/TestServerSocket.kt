package com.reach.socketkt.socket

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class TestServerSocket {

    private val port = 9077

    private val cachedExecutor = Executors.newCachedThreadPool()

    @Volatile
    private var isWaiting = false

    fun awaitConnect() {
        if (isWaiting) {
            return
        }
        cachedExecutor.execute {
            try {
                val serverSocket = ServerSocket(port)
                isWaiting = true
                while (isWaiting) {
                    val socket = serverSocket.accept()
                    cachedExecutor.execute { receiving(socket) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                isWaiting = false
            }
        }

    }

    private fun receiving(socket: Socket) {
        val input = socket.getInputStream()
        val output = socket.getOutputStream()

        val buffer = ByteArray(5)
        var receivedLen: Int

        try {
            while (true) {
                receivedLen = input.read(buffer)
                if (receivedLen == -1) {
                    socket.close()
                    return
                }
                val received = buffer.copyOf(receivedLen)
                if (received[2] == 0x36.toByte()) {
                    Thread.sleep(1000)
                } else if (received[2] == 0x39.toByte()) {
                    Thread.sleep(2000)
                }
                output.write(received)
                output.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}