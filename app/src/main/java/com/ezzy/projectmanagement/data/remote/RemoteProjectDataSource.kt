package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ProjectDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.SaveUserProjects
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class RemoteProjectDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    val saveUserProjects: SaveUserProjects,
    val firebaseAuth: FirebaseAuth
) : ProjectDataSource {

    private val userCollection = firestore.collection(USERS)
    private val organizationRef = firestore.collection(Constants.ORGANIZATIONS)

    override suspend fun add(
        organizationSet: Set<Organization>,
        project: Project,
        membersSet: Set<User>
    ) {

        val authUser = User(
            firebaseAuth.currentUser!!.displayName,
            firebaseAuth.currentUser!!.email
        )

        try {
            firestore.collection(Constants.PROJECT_COLLECTION)
            organizationSet.forEach { org ->
                organizationRef.whereEqualTo("name", org.name)
                    .get()
                    .addOnCompleteListener { querySnapshot ->
                        if (querySnapshot.isSuccessful){
                            querySnapshot.result!!.forEach { docSnapshot ->
                                organizationRef.document(docSnapshot.id)
                                    .collection(Constants.PROJECT_COLLECTION)
                                    .add(project)
                                    .addOnSuccessListener { docReference ->
                                        membersSet.let { users ->
                                            users.forEach { member ->
                                                organizationRef.document(docSnapshot.id)
                                                    .collection(Constants.PROJECT_COLLECTION)
                                                    .document(docReference.id)
                                                    .collection(Constants.MEMBERS)
                                                    .add(member)
                                                    .addOnSuccessListener {
                                                        Timber.i("PROJECT ADDED SUCCESSFULLY")
                                                        val projectHashMap = hashMapOf<String, String>()
                                                        projectHashMap["project_id"] = it.id
                                                        userCollection
                                                            .whereEqualTo("email", member.email)
                                                            .get()
                                                            .addOnSuccessListener { querySnapshot ->
                                                                querySnapshot.documents.forEach { snapShot ->
                                                                    userCollection.document(snapShot.id)
                                                                        .collection(PROJECT_COLLECTION)
                                                                        .add(projectHashMap)
                                                                        .addOnCompleteListener {
                                                                            Timber.d("Project id saved to users")
                                                                        }
                                                                }
                                                            }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Timber.e(e.message.toString())
                                                    }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Timber.e("Error add project to organization")
                                    }
                            }
                        }
                    }.addOnFailureListener {
                        Timber.e("An error occurred while searching")
                    }
                    .await()

            }
        } catch (e : Exception) {
            Timber.e("CATCHED EXCEPTION: ${e.message.toString()}")
        }
    }

    override suspend fun getAll(): List<Project> {
        val projects = mutableListOf<Project>()
        try {
            firestore.collection(Constants.PROJECT_COLLECTION)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.forEach { querySnapshot ->
                            val project = Project(
                                querySnapshot.getString("projectTitle"),
                                querySnapshot.getString("projectDescription"),
                                querySnapshot.getString("projectStage"),
                                querySnapshot.getString("startDate"),
                                querySnapshot.getString("endDate"),
                            )
                            projects.add(project)
                        }
                    }
                }.addOnFailureListener {
                    Timber.e(it.message.toString())
                }.await()
        } catch (e : Exception) {
            Timber.e(e.message.toString())
        }
        return projects
    }

    override suspend fun addMembers(membersSet: Set<User>): Set<User> {
        return membersSet
    }

    override suspend fun attachOrganizations(organizationSet: Set<Organization>): Set<Organization> {
        return organizationSet
    }
}