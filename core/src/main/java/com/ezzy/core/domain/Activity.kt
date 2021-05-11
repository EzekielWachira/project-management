package com.ezzy.core.domain

import java.sql.Timestamp
import java.util.*

enum class Action{
    CREATED_PROJECT,
    COMMENTED,
    REPORTED_BUG,
    ADDED_ISSUE,
    UPDATED,
    ADDED_TASK,
    SET_STATUS
}

data class Activity(
    var activityTitle : String? = null,
    val content : String? = null,
    val creation_date : Timestamp? = null,
    val creatorName : String? = null,
    val creatorImage: String? = null,
    val projectTitle : String? = null,
)