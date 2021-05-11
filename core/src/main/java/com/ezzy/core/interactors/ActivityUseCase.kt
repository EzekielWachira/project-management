package com.ezzy.core.interactors

import com.ezzy.core.data.ActivityDataSource
import com.ezzy.core.data.ActivityRepository
import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Activity

class ActivityUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(
        activity: Activity,
        action: Action,
        type: String?,
        status: String?
    ) = activityRepository.addActivity(activity, action, type, status)
}

class GetActivities(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke() = activityRepository.getActivities()
}