package com.ezzy.projectmanagement

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.ezzy.core.data.ProjectRepository
import com.ezzy.projectmanagement.network.netmanager.ConnectivityProvider
import com.ezzy.projectmanagement.util.ConnectionReceiverListener
import com.ezzy.projectmanagement.util.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        if (isMainProcess())
            ConnectivityProvider.createProvider(this).subscribe()
    }

    private fun isMainProcess() : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageName == getProcessName()
        } else packageName == getProcessNameLegacy()
    }

    private fun getProcessNameLegacy() : String? {
        val processId = Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        infos.forEach {
            if(it.pid == processId) return it.processName
        }
        return null
    }

}