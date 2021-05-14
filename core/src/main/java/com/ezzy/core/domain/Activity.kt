package com.ezzy.core.domain


import java.io.Serializable

enum class Action{
    CREATED_PROJECT,
    COMMENTED,
    REPORTED_BUG,
    ADDED_ISSUE,
    UPDATED,
    ADDED_TASK,
    SET_STATUS,
    CREATED_ORGANIZATION
}

data class Activity(
    var activityTitle : String? = null,
    val content : String? = null,
    val creation_date : Long? = null,
    val creatorName : String? = null,
    var creatorImage: String? = null,
) : Serializable