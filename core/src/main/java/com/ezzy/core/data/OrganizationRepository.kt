package com.ezzy.core.data

import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import java.net.URI

class OrganizationRepository(
    private val dataSource: OrganizationDataSource
) {

    suspend fun addOrganization(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri : URI
    ) = dataSource.addOrganization(
        organization, membersSet, fileName, imageUri
    )
    suspend fun retrieveOrganizations() = dataSource.retrieveOrganizations()
    suspend fun searchOrganizations(name : String) = dataSource.searchOrganization(name)
    suspend fun addOrgs(organizationSet: Set<Organization>) = dataSource.addOrgs(organizationSet)
    suspend fun addMembers(membersSet: Set<User>) = dataSource.addMembers(membersSet)
    suspend fun getOrganizationMembers(orgId : String) = dataSource.getOrganizationMembers(orgId)
    suspend fun getOrganizationProjects(orgId: String) = dataSource.getOrganizationProjects(orgId)
    suspend fun getOrganizationId(orgName : String) = dataSource.getOrganizationId(orgName)
    suspend fun getUserOrganizations(userEmail : String) = dataSource.getUserOrganizations(userEmail)
}