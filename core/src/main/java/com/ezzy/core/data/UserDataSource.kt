package com.ezzy.core.data

import com.ezzy.core.domain.User
import java.net.URI

interface UserDataSource {
    suspend fun getAllUsers() : List<User>
    suspend fun searchMembers(name : String) : List<User>
    suspend fun addMember(memberSet : Set<User>) : Set<User>
    suspend fun saveUserOrganizations(organizationId : String, email : String)
    suspend fun saveUserProjects(projectId : String, email: String)
    suspend fun updateUserDetails(imageUri : String?, user: User) : Boolean
    suspend fun saveUserImage(uri: URI, fileName : String, user: User) : Boolean
}