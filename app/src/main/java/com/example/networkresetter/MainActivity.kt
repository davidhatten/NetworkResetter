package com.example.networkresetter

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.networkresetter.ui.theme.NetworkResetterTheme

class MainActivity : ComponentActivity() {

    val networkCallbacks: MutableList<NetworkCallback> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        Log.d("MainActivity", "Default Network Active Status? ${connManager.isDefaultNetworkActive}")

        attemptNetworkRequest(NetworkRequest.Builder()
            .setIncludeOtherUidNetworks(true)
            .build())
        setContent {
            NetworkResetterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                       modifier = Modifier.padding(innerPadding)
                    ) {
                        Greeting(
                            name = "Network Requested",
                            modifier = Modifier.padding(innerPadding)
                        )
                        RequestNetworkNoFilters(modifier = Modifier.padding(innerPadding))
                        RequestCellNetwork(modifier = Modifier.padding(innerPadding))
                        RequestWifiNetwork(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        attemptNetworkRequest(NetworkRequest.Builder()
            .setIncludeOtherUidNetworks(true)
            .build())
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterNetworkRequests()
    }

    private fun getNetworkCallback(): NetworkCallback {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return object : NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)

                Log.d("AnonymousNetworkCallback", "Network Lost, requesting bandwidth update. Network Still Valid? ${connManager.requestBandwidthUpdate(network)}")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                Log.d("AnonymousNetworkCallback", "Network Successfully Available")

            }
        }
    }

    fun attemptNetworkRequest(request: NetworkRequest) {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = getNetworkCallback()

        networkCallbacks.add(networkCallback)

        connManager.requestNetwork(request, networkCallback)
    }


    private fun unregisterNetworkRequests() {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallbacks.forEach { networkCallback: NetworkCallback ->
            connManager.unregisterNetworkCallback(networkCallback)
        }

    }

    @Composable
    fun RequestNetworkNoFilters(modifier: Modifier = Modifier) {
        Button(onClick = { attemptNetworkRequest(NetworkRequest.Builder()
            .setIncludeOtherUidNetworks(true)
            .build()) },  modifier = modifier, ) {
            Text("Request Network")

        }
    }

    @Composable
    fun RequestCellNetwork(modifier: Modifier = Modifier) {
        Button(onClick = { attemptNetworkRequest(NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .setIncludeOtherUidNetworks(true)
            .build()) }, modifier = modifier, ) {
            Text("Request Cell Network")
        }
    }

    @Composable
    fun RequestWifiNetwork(modifier: Modifier = Modifier) {
        Button(onClick = { attemptNetworkRequest(NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setIncludeOtherUidNetworks(true)
            .build()) }, modifier = modifier, ) {
            Text("Request Wifi Network")
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetworkResetterTheme {
        Greeting("Android Preview")
    }
}

