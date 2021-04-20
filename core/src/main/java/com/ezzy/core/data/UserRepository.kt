package com.ezzy.core.data

import com.ezzy.core.domain.User

class UserRepository(
    private val dataSource: UserDataSource
) {

    suspend fun getAll() = dataSource.getAll()
    suspend fun searchMembers(name : String) = dataSource.searchMembers(name)
    suspend fun addMember(member : User) = dataSource.addMember(member)

}