package com.ezzy.projectmanagement.data.remote

import android.net.Uri
import com.ezzy.core.data.UserDataSource
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.GetAllUser
import com.ezzy.core.interactors.UpdateUser
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.URI
import java.util.*
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage,
) : UserDataSource {

    private val userCollection = firestore.collection(USERS)

    override suspend fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        try {
            firestore.collection(USERS)
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
            firestore.collection(USERS).whereEqualTo("name", name)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        for (snapshot in it.result!!){
                            val member = snapshot.toObject(User::class.java)
//                                User(
//                                snapshot.getString("name"),
//                                snapshot.getString("email")
//                            )
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
    }

    override suspend fun saveUserOrganizations(organizationId: String, email : String) {

        val organizationHashMap = hashMapOf<String, String>()
        organizationHashMap["organization_id"] = organizationId

        try {
            userCollection.whereEqualTo("email", email.toLowerCase(Locale.getDefault()))
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .collection(ORGANIZATIONS)
                            .add(organizationHashMap)
                            .addOnSuccessListener { Timber.d("SUCCESS") }
                            .addOnSuccessListener { Timber.e("ERROR saving user organizations") }
                    }
                }.await()
        } catch (e : Exception){
            Timber.e("Error saving user organizations")
        }
    }

    override suspend fun saveUserProjects(projectId: String, email: String) {
        val projectHashMap = hashMapOf<String, String>()
        projectHashMap["project_id"] = projectId

        try {
            userCollection.whereEqualTo("email", email.toLowerCase(Locale.getDefault()))
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .collection(PROJECT_COLLECTION)
                            .add(projectHashMap)
                            .addOnSuccessListener { Timber.d("SUCCESS") }
                            .addOnSuccessListener { Timber.e("ERROR saving user organizations") }
                    }
                }.await()
        } catch (e : Exception) {
            Timber.e("Error saving user projects")
        }
    }

    override suspend fun updateUserDetails(imageUri: String?, user: User): Boolean {
        var isUserUpdated = false
        val userHashMap = mapOf(
            "name" to user.name,
            "email" to user.email,
            "about" to user.about,
            "imageSrc" to imageUri
        )
        try {
            userCollection.whereEqualTo("email", user.email)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .update(userHashMap)
                            .addOnSuccessListener {
                                isUserUpdated = true
                            }
                            .addOnFailureListener { isUserUpdated = false }
                    }
                }.addOnFailureListener { isUserUpdated = false }.await()
        }catch (e : Exception) {
            Timber.e("An error occurred while updating user")
        }
        return isUserUpdated
    }

    var imagePath : String = ""
    override suspend fun saveUserImage(uri: URI, fileName: String, user: User) : Boolean {
        var isUserUpdateSuccess = false
        try {
            val imageUri = Uri.parse(uri.toString())
            val storageRef = firebaseStorage.reference.child("images/users/$fileName")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { imgUri ->
                        imagePath = imgUri.toString()
                        CoroutineScope(Dispatchers.IO).launch {
                            isUserUpdateSuccess = updateUserDetails(imgUri.toString(), user)
                        }
                    }.addOnFailureListener { Timber.e("error getting dowload url") }
                }.addOnFailureListener{ Timber.e("Error uploading user image") }
                .await()
        } catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return isUserUpdateSuccess
    }

}