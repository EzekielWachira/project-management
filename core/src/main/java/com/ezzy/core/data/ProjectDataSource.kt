package com.ezzy.core.data

import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User

interface ProjectDataSource {
    suspend fun add(
        organizationSet : Set<Organization>,
        project : Project,
        membersSet : Set<User>
    )
    suspend fun getAll() : List<Project>
    suspend fun addMembers(membersSet : Set<User>) : Set<User>
    suspend fun attachOrganizations(organizationSet: Set<Organization>) : Set<Organization>
    suspend fun getUserProjects(projectId : String) : List<Project>
}