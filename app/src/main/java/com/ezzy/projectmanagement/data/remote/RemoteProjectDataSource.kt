package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ProjectDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.SaveUserProjects
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class RemoteProjectDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    val saveUserProjects: SaveUserProjects,
    firebaseAuth: FirebaseAuth
) : ProjectDataSource {

    private val userCollection = firestore.collection(USERS)
    private val organizationRef = firestore.collection(ORGANIZATIONS)
    private val projectCollection = firestore.collection(PROJECT_COLLECTION)
    private var authenticatedUser: User? = null

    init {
        authenticatedUser = User(
            firebaseAuth.currentUser!!.displayName,
            firebaseAuth.currentUser!!.email
        )
    }

    override suspend fun add(
        organizationSet: Set<Organization>,
        project: Project,
        membersSet: Set<User>
    ) {

        try {
            firestore.collection(PROJECT_COLLECTION)
            organizationSet.forEach { org ->
                organizationRef.whereEqualTo("name", org.name)
                    .get()
                    .addOnCompleteListener { querySnapshot ->
                        if (querySnapshot.isSuccessful){
                            querySnapshot.result!!.forEach { docSnapshot ->
                                organizationRef.document(docSnapshot.id)
                                    .collection(PROJECT_COLLECTION)
                                    .add(project)
                                    .addOnSuccessListener { docReference ->
                                        membersSet.let { users ->
                                            users.forEach { member ->
                                                organizationRef.document(docSnapshot.id)
                                                    .collection(PROJECT_COLLECTION)
                                                    .document(docReference.id)
                                                    .collection(Constants.MEMBERS)
                                                    .add(member)
                                                    .addOnSuccessListener {
                                                        userCollection
                                                            .whereEqualTo("email", member.email)
                                                            .get()
                                                            .addOnSuccessListener { querySnapshot ->
                                                                querySnapshot.documents.forEach { snapShot ->
                                                                    CoroutineScope(Dispatchers.IO)
                                                                        .launch {
                                                                            saveUserProjects(
                                                                                docReference.id, member.email!!
                                                                            )
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
            firestore.collection(PROJECT_COLLECTION)
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

    override suspend fun getUserProjects(projectId: String): List<Project> {
        val projects = mutableListOf<Project>()
        val projectsId = mutableListOf<String>()
        try {
            userCollection.whereEqualTo("email", authenticatedUser?.email)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .collection(PROJECT_COLLECTION)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                querySnapshot.documents.forEach { docSnapshot ->
                                    projectsId.add(docSnapshot.getString("project_id")!!)
                                }
                            }.addOnFailureListener { e ->
                                Timber.e("error getting projects id")
                            }
                    }
                }

            if (projectsId.isNotEmpty()){
                projectsId.forEach { pId ->

                }
            }
        } catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return projects
    }
}