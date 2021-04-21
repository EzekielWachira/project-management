package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.interactors.AddProject
import com.ezzy.core.interactors.AttachMembers
import com.ezzy.core.interactors.AttachOrganization
import com.ezzy.core.interactors.GetAll

data class ProjectProvider(
    private val addProject: AddProject,
    private val getAll: GetAll,
    private val attachOrganization: AttachOrganization,
    private val attachMembers: AttachMembers
)