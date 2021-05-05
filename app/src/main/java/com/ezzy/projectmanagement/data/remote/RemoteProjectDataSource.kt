package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ProjectDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.SaveUserProjects
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.google.firebase.auth.FirebaseAuth
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
    private val organizationCollection = firestore.collection(ORGANIZATIONS)
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
                organizationCollection.whereEqualTo("name", org.name)
                    .get()
                    .addOnCompleteListener { querySnapshot ->
                        if (querySnapshot.isSuccessful){
                            querySnapshot.result!!.forEach { docSnapshot ->
                                organizationCollection.document(docSnapshot.id)
                                    .collection(PROJECT_COLLECTION)
                                    .add(project)
                                    .addOnSuccessListener { docReference ->
                                        membersSet.let { users ->
                                            users.forEach { member ->
                                                organizationCollection.document(docSnapshot.id)
                                                    .collection(PROJECT_COLLECTION)
                                                    .document(docReference.id)
                                                    .collection(Constants.MEMBERS)
                                                    .add(member)
                                                    .addOnSuccessListener {
                                                        userCollection
                                                            .whereEqualTo("email", member.email)
                                                            .get()
                                                            .addOnSuccessListener { querySnapshot ->
                                                                querySnapshot.documents.forEach { _ ->
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

    val projects = mutableListOf<Project>()
    val projectsId = mutableListOf<String>()
    override suspend fun getUserProjects(organizations: List<Organization>): List<Project> {
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
                                Timber.d("PROJECT IDS $projectsId")
                                if (projectsId.isNotEmpty()){
                                    organizations.let {
                                        it.forEach { organization ->
                                            organizationCollection.whereEqualTo("name", organization.name)
                                                .get().addOnSuccessListener { querySnapshot ->
                                                    querySnapshot.documents.forEach { documentSnapshot ->
                                                        organizationCollection.document(documentSnapshot.id)
                                                            .collection(PROJECT_COLLECTION)
                                                            .get().addOnSuccessListener { projectQuerySnapshot ->
                                                                projectQuerySnapshot.documents.forEach { projectDocSnapshot ->
                                                                    projectsId.forEach { pId ->
                                                                        if (projectDocSnapshot.id == pId) {
                                                                            organizationCollection.document(documentSnapshot.id)
                                                                                .collection(PROJECT_COLLECTION)
                                                                                .document(projectDocSnapshot.id)
                                                                                .collection(MEMBERS)
                                                                                .get().addOnSuccessListener { membersQSnapshot ->
                                                                                    membersQSnapshot.documents.forEach { membersDSnapshot ->
                                                                                        val project = Project(
                                                                                            projectDocSnapshot.getString("projectTitle"),
                                                                                            projectDocSnapshot.getString("projectDescription"),
                                                                                            projectDocSnapshot.getString("projectStage"),
                                                                                            projectDocSnapshot.getString("startDate"),
                                                                                            projectDocSnapshot.getString("endDate"),
                                                                                        )
                                                                                        projects.add(project)
                                                                                    }
                                                                                    Timber.d("THE PRJS $projects")
                                                                                }.addOnFailureListener {
                                                                                    Timber.e("error getting project members")
                                                                                }
                                                                        }
                                                                    }
                                                                }
                                                            }.addOnFailureListener {
                                                                Timber.e("Error getting org projects")
                                                            }
                                                    }
                                                }.addOnFailureListener {
                                                    Timber.e("Error getting organizations")
                                                }
                                        }
                                    }
                                }
                            }.addOnFailureListener { _ ->
                                Timber.e("error getting projects id")

                            }
                    }
                }.await()

            Timber.d("Projects Ids: $projectsId")

//            if (projectsId.isNotEmpty()){
//                organizations.let {
//                    it.forEach { organization ->
//                        organizationCollection.whereEqualTo("name", organization.name)
//                            .get().addOnSuccessListener { querySnapshot ->
//                                querySnapshot.documents.forEach { documentSnapshot ->
//                                    organizationCollection.document(documentSnapshot.id)
//                                        .collection(PROJECT_COLLECTION)
//                                        .get().addOnSuccessListener { projectQuerySnapshot ->
//                                            projectQuerySnapshot.documents.forEach { projectDocSnapshot ->
//                                                projectsId.forEach { pId ->
//                                                    if (projectDocSnapshot.id == pId) {
//                                                        organizationCollection.document(documentSnapshot.id)
//                                                            .collection(PROJECT_COLLECTION)
//                                                            .document(projectDocSnapshot.id)
//                                                            .collection(MEMBERS)
//                                                            .get().addOnSuccessListener { membersQSnapshot ->
//                                                                membersQSnapshot.documents.forEach { membersDSnapshot ->
//                                                                    val project = Project(
//                                                                        projectDocSnapshot.getString("projectTitle"),
//                                                                        projectDocSnapshot.getString("projectDescription"),
//                                                                        projectDocSnapshot.getString("projectStage"),
//                                                                        projectDocSnapshot.getString("startDate"),
//                                                                        projectDocSnapshot.getString("endDate"),
//                                                                    )
//                                                                    projects.add(project)
//                                                                }
//                                                            }.addOnFailureListener {
//                                                                Timber.e("error getting project members")
//                                                            }
//                                                    }
//                                                }
//                                            }
//                                        }.addOnFailureListener {
//                                            Timber.e("Error getting org projects")
//                                        }
//                                }
//                            }.addOnFailureListener {
//                                Timber.e("Error getting organizations")
//                            }.apply { await() }
//                    }
//                }
//            }
        } catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return projects
    }
}