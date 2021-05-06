package com.ezzy.core.interactors

import com.ezzy.core.data.UserRepository
import com.ezzy.core.domain.User

class GetAllUser(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.getAllUsers()
}

class SearchMembers(private val repository: UserRepository) {
    suspend operator fun invoke(
        name : String
    ) = repository.searchMembers(name)
}

class AddMember(private val repository: UserRepository){
    suspend operator fun invoke(
        memberSet : Set<User>
    ) = repository.addMember(memberSet)
}

class SaveUserOrganizations(private val repository: UserRepository){
    suspend operator fun invoke(organizationId : String, email : String) =
        repository.saveUserOrganizations(organizationId, email)
}

class SaveUserProjects(private val repository: UserRepository) {
    suspend operator fun invoke (
        projectId : String,
        email: String
    ) = repository.saveUserProjects(projectId, email)
}

class UpdateUser(private val repository: UserRepository) {
    suspend operator fun invoke(
        user: User
    ) = repository.updateUserDetails(user)
}