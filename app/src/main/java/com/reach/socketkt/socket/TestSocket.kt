package com.reach.socketkt.socket

import android.util.Log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class TestSocket {

    private companion object {

        private const val SEND_READY = 0

        private const val SEND_SUCCESS = 1

        private const val SEND_FAIL = 2

        private const val SEND_TIMEOUT = 3

        private const val DEFAULT_TIMEOUT = 1500L

    }

    private val read = Executors.newSingleThreadExecutor()

    private val write = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private lateinit var socket: Socket

    private lateinit var output: OutputStream

    private val test1 = MutableStateFlow(SEND_READY)

    private val test2 = MutableStateFlow(SEND_READY)

    private val test3 = MutableStateFlow(SEND_READY)

    @Volatile
    private var isConnected = false

    suspend fun sendCommand1() = sendCommand(byteArrayOf(0x11, 0x22, 0x33, 0x22, 0x11), test1)

    suspend fun sendCommand2() = sendCommand(byteArrayOf(0x11, 0x22, 0x36, 0x22, 0x11), test2)

    suspend fun sendCommand3() = sendCommand(byteArrayOf(0x11, 0x22, 0x39, 0x22, 0x11), test3)

    fun connect(host: String, port: Int) {
        read.execute {
            if (isConnected) {
                return@execute
            }
            try {
                socket = Socket()
                val address = InetSocketAddress(host, port)
                socket.connect(address, 5000)
                output = socket.getOutputStream()

                isConnected = true
                receiving(socket.getInputStream())
            } catch (e: IOException) {
                e.printStackTrace()
                disconnect()
            }
        }
    }

    fun disconnect() {
        socket.close()
        isConnected = false
    }

    private suspend fun sendCommand(
        command: ByteArray,
        receiveFlow: MutableStateFlow<Int>,
        timeout: Long = DEFAULT_TIMEOUT
    ) = flow {
        val res = write(command)
        Log.d("REACH", "sendCommand: ${command.contentToString()}, sendResult: $res")
        if (res) {
            receiveFlow.emit(SEND_READY)
            withTimeout(timeout) {
                receiveFlow.collect {
                    if (it == SEND_SUCCESS) {
                        emit(SEND_SUCCESS)
                        this.coroutineContext.cancel()
                    }
                }
            }
        } else {
            emit(SEND_FAIL)
        }
    }.flowOn(write)
        .cancellable()
        .catch { e ->
            if (e is TimeoutCancellationException) {
                emit(SEND_TIMEOUT)
            }
        }.transform {
            Log.d("REACH", "transform: $it")
            emit(it == SEND_SUCCESS)
        }

    private fun write(data: ByteArray) = try {
        if (this::output.isInitialized) {
            output.write(data)
            output.flush()
            true
        } else {
            false
        }
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }

    private fun receiving(input: InputStream) {
        val buffer = ByteArray(1024)
        var receivedLen: Int

        while (isConnected) {
            receivedLen = input.read(buffer)
            if (receivedLen == -1) {
                disconnect()
                break
            }
            val reply = buffer.copyOf(receivedLen)
            if (checkReceivedData(reply)) {
                analyseReceivedData(reply)
            }
        }
    }

    private fun checkReceivedData(data: ByteArray): Boolean {
        if (
            data.size != 5
            || data[0] != 0x11.toByte()
            || data[1] != 0x22.toByte()
            || data[3] != 0x22.toByte()
            || data[4] != 0x11.toByte()
        ) {
            Log.d("REACH", "receive error package: ${data.size}")
            return false
        }
        return true
    }

    private fun analyseReceivedData(data: ByteArray) {
        when {
            data[2] == 0x33.toByte() -> test1.value = SEND_SUCCESS
            data[2] == 0x36.toByte() -> test2.value = SEND_SUCCESS
            data[2] == 0x39.toByte() -> test3.value = SEND_SUCCESS
        }
    }

}