package com.reach.socketkt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reach.socketkt.socket.TestServerSocket
import com.reach.socketkt.socket.TestSocket
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val client = TestSocket()

    private val server = TestServerSocket()

    fun connect() {
        viewModelScope.launch { client.connect("192.168.31.196", 9077) }
    }

    fun asServer() {
        server.awaitConnect()
    }

    fun command1() {
        viewModelScope.launch {
            client.sendCommand1().collect {
                Log.d("REACH", "command1 res: $it")
            }
        }
    }

    fun command2() {
        viewModelScope.launch {
            client.sendCommand2().collect {
                Log.d("REACH", "command2 res: $it")
            }
        }
    }

    fun command3() {
        viewModelScope.launch {
            client.sendCommand3().collect {
                Log.d("REACH", "command3 res: $it")
            }
        }
    }

}