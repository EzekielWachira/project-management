package com.ezzy.core.domain

import java.io.Serializable

data class Organization(
    val name: String? = null,
    var imageSrc: String? = null,
    val about: String? = null
) : Serializable