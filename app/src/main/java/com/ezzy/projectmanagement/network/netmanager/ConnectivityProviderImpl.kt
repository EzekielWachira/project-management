package com.ezzy.projectmanagement.network.netmanager

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider.NetworkState
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider.NetworkState.ConnectedState

@RequiresApi(Build.VERSION_CODES.N)
class ConnectivityProviderImpl(val cm: ConnectivityManager) :
ConnectivityProviderBaseImpl(){

    private val networkCallback = ConnectivityCallback()

    override fun subscribeListener() {
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun getNetworkState(): NetworkState {
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null)
            ConnectedState.Connected(capabilities)
        else NetworkState.NotConnectedState
    }

    private inner class ConnectivityCallback : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            dispatchChange(ConnectedState.Connected(networkCapabilities))
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            dispatchChange(NetworkState.NotConnectedState)
        }
    }
}