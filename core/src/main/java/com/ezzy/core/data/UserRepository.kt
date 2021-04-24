package com.ezzy.core.data

import com.ezzy.core.domain.User

class UserRepository(
    private val dataSource: UserDataSource
) {

    suspend fun getAllUsers() = dataSource.getAllUsers()
    suspend fun searchMembers(name : String) = dataSource.searchMembers(name)
    suspend fun addMember(memberSet : Set<User>) = dataSource.addMember(memberSet)

}