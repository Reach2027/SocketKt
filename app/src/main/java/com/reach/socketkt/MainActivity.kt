package com.reach.socketkt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.com.chioy.socketkt.ui.theme.SocketKtTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        setContent {
            Main(mainViewModel)
        }
    }

}

@Composable
fun Main(mainViewModel: MainViewModel) {
    SocketKtTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Greeting(mainViewModel)
        }
    }
}

@Composable
fun Greeting(mainViewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                mainViewModel.connect()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Client", fontSize = 22.sp)
        }

        Button(
            onClick = {
                mainViewModel.command1()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Command1", fontSize = 22.sp)
        }

        Button(
            onClick = {
                mainViewModel.command2()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Command2", fontSize = 22.sp)
        }

        Button(
            onClick = {
                mainViewModel.command3()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Command3", fontSize = 22.sp)
        }

        Button(
            onClick = {
                mainViewModel.asServer()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Server", fontSize = 22.sp)
        }
    }
}