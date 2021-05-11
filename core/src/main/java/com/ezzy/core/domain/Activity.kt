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
    private val creation_date : Timestamp? = null,
    private val creatorName : String? = null,
    private val creatorImage: String? = null,
    private val projectTitle : String? = null,
    private val content : String? = null,
    private val action: Action? = null
)