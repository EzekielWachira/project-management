package com.ezzy.core.data

import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User

class ProjectRepository(
    private val dataSource: ProjectDataSource
) {

    suspend fun add(
        organizationSet : Set<Organization>,
        project : Project,
        membersSet : Set<User>
    ) = dataSource.add(organizationSet, project, membersSet)

    suspend fun getAll() = dataSource.getAll()
    suspend fun addMembers(membersSet: Set<User>) = dataSource.addMembers(membersSet)
    suspend fun addOrganization(organizationSet: Set<Organization>) =
        dataSource.addOrganizations(organizationSet)

}