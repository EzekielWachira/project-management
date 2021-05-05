package com.ezzy.projectmanagement.network.netmanager

import androidx.lifecycle.MutableLiveData
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider.*
import com.ezzy.projectmanagement.util.Constants.CONNECTED
import com.ezzy.projectmanagement.util.Constants.DISCONNECTED

abstract class ConnectivityProviderBaseImpl : ConnectivityProvider {

    protected abstract fun subscribeListener()

    override fun subscribe() {
        subscribeListener()
        dispatchChange(getNetworkState())
    }

    protected fun dispatchChange(state : NetworkState){
        val networkState = if (state.hasInternet()) CONNECTED else DISCONNECTED
        if (networkState != NetworkManager.networkStatus.value) {
            NetworkManager.networkStatus.postValue(networkState)
        }
    }

    private fun NetworkState.hasInternet() : Boolean {
        return (this as? NetworkState.ConnectedState)?.hasInternet == true
    }

}

object NetworkManager {
    val networkStatus = MutableLiveData<Int>()
    fun isDisconnected() : Boolean {
        return networkStatus.value == DISCONNECTED
    }
}

class NetworkDisconnectedException : Throwable()