package com.ezzy.projectmanagement.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.ezzy.projectmanagement.BaseApplication

class NetworkMonitor : BroadcastReceiver() {

    var connectionReceiverListener : ConnectionReceiverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetworkInfo

        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        connectionReceiverListener?.onNetworkConnectionChanged(isConnected)
    }

//    fun isConnected() : Boolean {
//        val connectManager = BaseApplication().getInstance()
//            ?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE)
//            as ConnectivityManager
//        val networkInfo = connectManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnectedOrConnecting
//    }

}

interface ConnectionReceiverListener{
    fun onNetworkConnectionChanged(isConnected : Boolean)
}
