package com.ezzy.core.data

import com.ezzy.core.domain.User
import java.net.URI

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
    suspend fun updateUserDetails(imageUri : String?, user: User) =
        dataSource.updateUserDetails(imageUri, user)
    suspend fun saveUserImage(uri: URI, fileName : String, user: User) =
        dataSource.saveUserImage(uri, fileName, user)
    suspend fun getUserDetails() = dataSource.getUserDetails()
}