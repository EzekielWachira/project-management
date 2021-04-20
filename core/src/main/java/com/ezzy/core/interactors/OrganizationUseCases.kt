package com.ezzy.core.interactors

import com.ezzy.core.data.OrganizationRepository
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import java.net.URI

class AddOrganization(private val organizationRepository: OrganizationRepository){
    suspend operator fun invoke(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri : URI
    ) = organizationRepository.addOrganization(organization, membersSet, fileName, imageUri)
}

class RetrieveOrganizations(private val organizationRepository: OrganizationRepository){
    suspend operator fun invoke() = organizationRepository.retrieveOrganizations()
}

class SearchOrganizations(private val organizationRepository: OrganizationRepository) {
    suspend operator fun invoke(
        name : String
    ) = organizationRepository.searchOrganizations(name)
}

class AddOrgs(private val repository: OrganizationRepository) {
    suspend operator fun invoke(
        organization: Organization
    ) = repository.addOrgs(organization)
}

class AddMembers(private val repository: OrganizationRepository) {
    suspend operator fun invoke(
        membersSet: Set<User>
    ) = repository.addMembers(membersSet)
}


