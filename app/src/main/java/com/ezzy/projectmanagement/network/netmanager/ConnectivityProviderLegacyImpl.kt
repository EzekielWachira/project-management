@file:Suppress("DEPRECATION")

package com.ezzy.projectmanagement.network.netmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkInfo
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider.*
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider.NetworkState.ConnectedState

class ConnectivityProviderLegacyImpl(
    private val context : Context,
    private val cm : ConnectivityManager
) : ConnectivityProviderBaseImpl() {

    private val receiver = ConnectivityReceiver()

    override fun subscribeListener() {
        context.registerReceiver(receiver, IntentFilter(CONNECTIVITY_ACTION))
    }

    override fun getNetworkState(): NetworkState {
        val activeNetworkInfo = cm.activeNetworkInfo
        return if (activeNetworkInfo != null)
            ConnectedState.ConnectedLegacy(activeNetworkInfo)
        else NetworkState.NotConnectedState
    }

    private inner class ConnectivityReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val networkInfo = cm.activeNetworkInfo
            val fallbackNetworkInfo : NetworkInfo? = intent?.getParcelableExtra(EXTRA_NETWORK_INFO)
            val state: NetworkState =
                if (networkInfo?.isConnectedOrConnecting == true) {
                    ConnectedState.ConnectedLegacy(networkInfo)
                } else if (networkInfo != null && fallbackNetworkInfo != null &&
                    networkInfo.isConnectedOrConnecting != fallbackNetworkInfo.isConnectedOrConnecting
                ) {
                    ConnectedState.ConnectedLegacy(fallbackNetworkInfo)
                } else {
                    val state = networkInfo ?: fallbackNetworkInfo
                    if (state != null) ConnectedState.ConnectedLegacy(state)
                    else NetworkState.NotConnectedState
                }
            dispatchChange(state)
        }

    }
}