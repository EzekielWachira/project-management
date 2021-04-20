package com.ezzy.core.interactors

import com.ezzy.core.data.ProjectRepository
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User

class AddProject(private val repository: ProjectRepository){
    suspend operator fun invoke(
        organizationSet : Set<Organization>,
        project : Project,
        membersSet : Set<User>
    ) = repository.addProject(organizationSet, project, membersSet)
}

class GetAll(private val repository: ProjectRepository) {
    suspend operator fun invoke() = repository.getAll()
}

class AttachOrganization(private val repository: ProjectRepository) {
    suspend operator fun invoke(
        organizationSet: Set<Organization>
    ) = repository.attachOrganization(organizationSet)
}