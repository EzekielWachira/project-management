package com.ezzy.core.data

import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Activity

interface ActivityDataSource {

    suspend fun addActivity(
        activity: Activity,
        action: Action,
        type: String?,
        status: String?
    ): Boolean

    suspend fun getActivities(): List<Activity>
}