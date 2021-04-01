package com.ezzy.projectmanagement.model

import java.util.*

data class Project(
    private val projectTitle: String,
    private val projectDescription: String,
    private val projectStage : String,
    private val startDate: String,
    private val endDate: String?
)
