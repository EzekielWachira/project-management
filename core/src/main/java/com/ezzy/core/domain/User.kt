package com.ezzy.core.domain

import java.io.Serializable

data class User(
    val name: String? = null,
    val email: String? = null
) : Serializable
