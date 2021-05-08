package com.ezzy.projectmanagement.data.remote

import android.net.Uri
import com.ezzy.core.data.UserDataSource
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.URI
import java.util.*
import javax.inject.Inject

class RemoteUserDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : UserDataSource {

    private val userCollection = firestore.collection(USERS)

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

    override suspend fun updateUserDetails(user: User) : Boolean{
        var isUserUpdated = false
        try {
            userCollection.whereEqualTo("email", user.email)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .set(user)
                            .addOnSuccessListener { _->
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

    override suspend fun saveUserImage(uri: URI, fileName: String): String? {
        var imagePath : String? = null
        try {
            val imageUri = Uri.parse(uri.toString())
            val storageRef = firebaseStorage.reference.child("images/users/$fileName")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { imgUri ->
                        imagePath = imgUri.toString()
                    }.addOnFailureListener { Timber.e("error getting dowload url") }
                }.addOnFailureListener{ Timber.e("Error uploading user image") }
                .await()
        } catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return imagePath
    }

}