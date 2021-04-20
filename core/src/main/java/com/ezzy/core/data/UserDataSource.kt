package com.ezzy.core.data

import com.ezzy.core.domain.User

interface UserDataSource {
    suspend fun getAll() : List<User>
    suspend fun searchMembers(name : String) : List<User>
    suspend fun addMember(member : User)
}