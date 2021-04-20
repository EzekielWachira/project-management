package com.ezzy.projectmanagement

import android.app.Application
import com.ezzy.core.data.ProjectRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        val projectRepository = ProjectRepository()
    }
}