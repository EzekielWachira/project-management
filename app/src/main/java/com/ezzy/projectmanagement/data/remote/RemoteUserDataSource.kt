package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.UserDataSource
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) : UserDataSource {

    override suspend fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        try {
            firestore.collection(Constants.USERS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        for (querySnapshot in it.result!!){
                            val member = User(
                                querySnapshot.getString("name"),
                                querySnapshot.getString("email")
                            )
                            users.add(member)
                        }
                    }
                }.addOnFailureListener {
                    Timber.d("Error getting users")
                }.await()
        } catch (e: Exception){
            Timber.e(e.message.toString())
        }
        return users
    }

    override suspend fun searchMembers(name: String): List<User> {
        val users = mutableListOf<User>()
        try {
            firestore.collection(Constants.USERS).whereEqualTo("name", name)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        for (snapshot in it.result!!){
                            val member = User(
                                snapshot.getString("name"),
                                snapshot.getString("email")
                            )
                            users.add(member)
                        }
                        Timber.d("USERS ==>> $users")
                    }
                }.addOnFailureListener {
                    Timber.e("Error searching members")
                }.await()
        } catch (e : Exception) {
            Timber.e("Error searching members")
        }
        return users
    }

    override suspend fun addMember(memberSet: Set<User>): Set<User> {
        return memberSet
//        val members = mutableSetOf<User>()
//        if (members.contains(member)){
//            Timber.i("Member already exist in list")
//        } else {
//            members.add(member)
//        }
//        return members
    }

}