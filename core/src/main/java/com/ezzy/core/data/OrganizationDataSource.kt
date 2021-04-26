package com.ezzy.core.data

import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import java.net.URI

interface OrganizationDataSource {
    suspend fun addOrganization(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri : URI
    )

    suspend fun retrieveOrganizations() : List<Organization>

    suspend fun searchOrganization(name : String) : List<Organization>

    suspend fun addOrgs(organizationSet: Set<Organization>) : Set<Organization>

    suspend fun addMembers(membersSet: Set<User>) : Set<User>

    suspend fun getOrganizationMembers(orgId : String) : List<User>

    suspend fun getOrganizationProjects(orgId: String) : List<Project>
}