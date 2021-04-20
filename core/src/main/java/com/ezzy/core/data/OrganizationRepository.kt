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
    suspend fun addOrgs(organization: Organization) = dataSource.addOrgs(organization)
    suspend fun addMembers(membersSet: Set<User>) = dataSource.addMembers(membersSet)

}