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
        member : User
    ) = repository.addMember(member)
}