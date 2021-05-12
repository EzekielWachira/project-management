package com.ezzy.core.data

import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Activity

class ActivityRepository(
    private val dataSource: ActivityDataSource
) {
    suspend fun addActivity(
        activity: Activity,
        action: Action,
        type: String?,
        status: String?,
        organizationName: String?,
        projectName: String?
    ) = dataSource.addActivity(
        activity, action, type, status, organizationName, projectName
    )

    suspend fun getActivities() = dataSource.getActivities()
}