package com.ezzy.projectmanagement.model

import java.io.Serializable
import java.util.*

data class Project(
    val projectTitle: String? = null,
    val projectDescription: String? = null,
    val projectStage : String? = null,
    val startDate: String? = null,
    val endDate: String? = null
) : Serializable
