package com.ezzy.projectmanagement.network.netmanager

import android.content.Context
import android.content.Context.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi

interface ConnectivityProvider {

    fun subscribe()

    fun getNetworkState() : NetworkState

    sealed class NetworkState {
        object NotConnectedState : NetworkState()

        sealed class ConnectedState(val hasInternet : Boolean) : NetworkState() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            data class Connected(val capabilities: NetworkCapabilities) : ConnectedState(
                capabilities.hasCapability(NET_CAPABILITY_INTERNET)
            )

            @Suppress("DEPRECATION")
            data class ConnectedLegacy(val networkInfo : NetworkInfo) : ConnectedState(
                networkInfo.isConnectedOrConnecting
            )
        }

    }

    companion object {
        @JvmStatic
        fun createProvider(context: Context) : ConnectivityProvider {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as
                    ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ConnectivityProviderImpl(connectivityManager)
            } else {
                ConnectivityProviderLegacyImpl(context, connectivityManager)
            }
        }
    }

}