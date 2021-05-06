package com.ezzy.core.data

import com.ezzy.core.domain.User

class UserRepository(
    private val dataSource: UserDataSource
) {

    suspend fun getAllUsers() = dataSource.getAllUsers()
    suspend fun searchMembers(name : String) = dataSource.searchMembers(name)
    suspend fun addMember(memberSet : Set<User>) = dataSource.addMember(memberSet)
    suspend fun saveUserOrganizations(organizationId : String, email : String)
        = dataSource.saveUserOrganizations(organizationId, email)
    suspend fun saveUserProjects(projectId : String, email: String) =
        dataSource.saveUserProjects(projectId, email)
    suspend fun updateUserDetails(user: User) = dataSource.updateUserDetails(user)
}