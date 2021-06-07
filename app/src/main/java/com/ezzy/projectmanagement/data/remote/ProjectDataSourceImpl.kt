package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ProjectDataSource
import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.ActivityUseCase
import com.ezzy.core.interactors.SaveUserProjects
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.ezzy.projectmanagement.util.saveActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ProjectDataSourceImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val saveUserProjects: SaveUserProjects,
    val firebaseAuth: FirebaseAuth,
    val addActivity: ActivityUseCase
) : ProjectDataSource {

    private val userCollection = firestore.collection(USERS)
    private val organizationCollection = firestore.collection(ORGANIZATIONS)
    private val projectCollection = firestore.collection(PROJECT_COLLECTION)
    private var authenticatedUser: User? = null

    init {
        firebaseAuth.currentUser?.let {
            authenticatedUser = User(
                it.displayName, it.email
            )
        }
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
                        if (querySnapshot.isSuccessful) {
                            querySnapshot.result!!.forEach { docSnapshot ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    addProject(project, docSnapshot.id, membersSet)
                                }
                            }
                        }
                    }.addOnFailureListener {
                        Timber.e("An error occurred while searching")
                    }
                    .await()

            }
            CoroutineScope(Dispatchers.IO).launch {
                saveActivity<ProjectDataSourceImpl>(
                    firebaseAuth, firestore, ""
                ).apply {
                    addActivity(
                        this, Action.CREATED_PROJECT,
                        null, null, null,
                        project.projectTitle
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e("CATCHED EXCEPTION: ${e.message.toString()}")
        }
    }

    suspend fun addProject(project: Project, snapShotId: String, membersSet: Set<User>) {
        organizationCollection.document(snapShotId)
            .collection(PROJECT_COLLECTION)
            .add(project)
            .addOnSuccessListener { docReference ->
                membersSet.let { users ->
                    users.forEach { member ->
                        CoroutineScope(Dispatchers.IO).launch {
                            addUsersToProject(member, snapShotId, docReference.id)
                        }
//                                                organizationCollection.document(docSnapshot.id)
//                                                    .collection(PROJECT_COLLECTION)
//                                                    .document(docReference.id)
//                                                    .collection(Constants.MEMBERS)
//                                                    .add(member)
//                                                    .addOnSuccessListener {
//                                                        userCollection
//                                                            .whereEqualTo("email", member.email)
//                                                            .get()
//                                                            .addOnSuccessListener { querySnapshot ->
//                                                                querySnapshot.documents.forEach { _ ->
//                                                                    CoroutineScope(Dispatchers.IO)
//                                                                        .launch {
//                                                                            saveUserProjects(
//                                                                                docReference.id, member.email!!
//                                                                            )
//                                                                        }
//                                                                }
//                                                            }
//                                                    }
//                                                    .addOnFailureListener { e ->
//                                                        Timber.e(e.message.toString())
//                                                    }
                    }
                }
            }
            .addOnFailureListener {
                Timber.e("Error add project to organization")
            }.await()
    }

    suspend fun addUsersToProject(user: User, snapShotId: String, documentId: String) {
        organizationCollection.document(snapShotId)
            .collection(PROJECT_COLLECTION)
            .document(documentId)
            .collection(Constants.MEMBERS)
            .add(user)
            .addOnSuccessListener {
                userCollection
                    .whereEqualTo("email", user.email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        querySnapshot.documents.forEach { _ ->
                            CoroutineScope(Dispatchers.IO)
                                .launch {
                                    saveUserProjects(
                                        documentId, user.email!!
                                    )
                                }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Timber.e(e.message.toString())
            }.apply { await() }
    }

    override suspend fun getAll(): List<Project> {
        val projects = mutableListOf<Project>()
        try {
            firestore.collection(PROJECT_COLLECTION)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.forEach { querySnapshot ->
                            val project = querySnapshot.toObject(Project::class.java)
//                                Project(
//                                querySnapshot.getString("projectTitle"),
//                                querySnapshot.getString("projectDescription"),
//                                querySnapshot.getString("projectStage"),
//                                querySnapshot.getString("startDate"),
//                                querySnapshot.getString("endDate"),
//                            )
                            projects.add(project)
                        }
                    }
                }.addOnFailureListener {
                    Timber.e(it.message.toString())
                }.await()
        } catch (e: Exception) {
            Timber.e(e.message.toString())
        }
        return projects
    }

    override suspend fun addMembers(membersSet: Set<User>): Set<User> {
        return membersSet
    }

    override suspend fun attachOrganizations(
        organizationSet: Set<Organization>
    ): Set<Organization> {
        return organizationSet
    }

    override suspend fun getUserProjects(organizations: List<Organization>): List<Project> {
        var projects = listOf<Project>()
        try { projects = getUserDetails(organizations) }
        catch (e : Exception){ Timber.e("ERROR!!!!") }
        return projects
    }

    suspend fun getUserDetails(organizations: List<Organization>) : List<Project> {
        var projects = listOf<Project>()
        userCollection.whereEqualTo("email", authenticatedUser?.email)
            .get()
            .addOnSuccessListener {
                it.documents.forEach { documentSnapshot ->
                    CoroutineScope(Dispatchers.IO).launch {
                        projects = getProjectsIds(documentSnapshot.id, organizations)
                    }
                }
            }.addOnFailureListener { Timber.e("error getting projects ids") }
            .await()
        return projects
    }

    suspend fun getProjectsIds(
        snapShotId: String,
        organizations: List<Organization>
    ): List<Project> {
        var projects = listOf<Project>()
        val projectsIds = mutableSetOf<String?>()
        userCollection.document(snapShotId)
            .collection(PROJECT_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { docSnapshot ->
                    projectsIds.add(docSnapshot.getString("project_id")!!)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    projects = getOrganizationDetails(organizations, projectsIds)
                }
            }.addOnFailureListener { Timber.e("error getting projects ids") }
            .apply { await() }

        return projects
    }

    private suspend fun getOrganizationDetails(
        organizationsList: List<Organization>,
        projectsIds : MutableSet<String?>
    ) : List<Project> {
        var projects = listOf<Project>()
        organizationsList.forEach {
            organizationCollection.whereEqualTo("name", it.name)
                .get().addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach { documentSnapshot ->
                        CoroutineScope(Dispatchers.IO).launch {
                            projects = getProjects(documentSnapshot.id, projectsIds)
                        }
                    }
                }.addOnFailureListener { err -> Timber.e("ERROR ${err.message}") }
                .await()
        }
        return projects
    }

    private suspend fun getProjects(
        documentId: String, projectsIds: MutableSet<String?>
    ) : List<Project> {
        val projects = mutableListOf<Project>()
        organizationCollection.document(documentId)
            .collection(PROJECT_COLLECTION)
            .get().addOnSuccessListener { projectQuerySnapshot ->
                projectQuerySnapshot.documents.forEach { projectDocSnapshot ->
                    projectsIds.forEach { projectId ->
                        if (projectDocSnapshot.id == projectId){
                            projects.add(
                                projectDocSnapshot.toObject(Project::class.java)!!
                            )
                        }
                    }
//                    CoroutineScope(Dispatchers.IO).launch {
//                        projects = filterProjects(documentId, projectDocSnapshot.id)
//                    }
                }
            }.addOnFailureListener {
                Timber.e("Error getting org projects")
            }.await()
        return projects
    }

//    private suspend fun filterProjects(documentId: String, snapShotId: String) : List<Project>{
//        val projects = mutableListOf<Project>()
//        projectsId.forEach { pId ->
//            if (snapShotId == pId) {
//                organizationCollection.document(documentId)
//                    .collection(PROJECT_COLLECTION)
//                    .document(snapShotId)
//                    .get().addOnSuccessListener {
//                        projects.add(it.toObject(Project::class.java)!!)
//                    }
//                    .addOnFailureListener {
//                        Timber.e("error getting user projects")
//                    }
//                    .await()
//            }
//        }
//        return projects
//    }

    suspend fun getUserProjects(
        baseSnapShotId: String,
        snapShotId: String,
        memberSnapshotId: String
    ) {
        organizationCollection.document(baseSnapShotId)
            .collection(PROJECT_COLLECTION)
            .document(snapShotId)
            .collection(MEMBERS)
            .document(memberSnapshotId)

    }

//    val projects = mutableListOf<Project>()
//    val projectsId = mutableListOf<String>()
//    override suspend fun getUserProjects(organizations: List<Organization>): List<Project> {
//        try {
//            userCollection.whereEqualTo("email", authenticatedUser?.email)
//                .get()
//                .addOnSuccessListener {
//                    it.documents.forEach { documentSnapshot ->
//                        userCollection.document(documentSnapshot.id)
//                            .collection(PROJECT_COLLECTION)
//                            .get()
//                            .addOnSuccessListener { querySnapshot ->
//                                querySnapshot.documents.forEach { docSnapshot ->
//                                    projectsId.add(docSnapshot.getString("project_id")!!)
//                                }
//                                Timber.d("PROJECT IDS $projectsId")
//                                if (projectsId.isNotEmpty()) {
//                                    organizations.let {
//                                        it.forEach { organization ->
//                                            organizationCollection.whereEqualTo(
//                                                "name",
//                                                organization.name
//                                            )
//                                                .get().addOnSuccessListener { querySnapshot ->
//                                                    querySnapshot.documents.forEach { documentSnapshot ->
//                                                        organizationCollection.document(
//                                                            documentSnapshot.id
//                                                        )
//                                                            .collection(PROJECT_COLLECTION)
//                                                            .get()
//                                                            .addOnSuccessListener { projectQuerySnapshot ->
//                                                                projectQuerySnapshot.documents.forEach { projectDocSnapshot ->
//                                                                    projectsId.forEach { pId ->
//                                                                        if (projectDocSnapshot.id == pId) {
//                                                                            organizationCollection.document(
//                                                                                documentSnapshot.id
//                                                                            )
//                                                                                .collection(
//                                                                                    PROJECT_COLLECTION
//                                                                                )
//                                                                                .document(
//                                                                                    projectDocSnapshot.id
//                                                                                )
//                                                                                .collection(MEMBERS)
//                                                                                .get()
//                                                                                .addOnSuccessListener { membersQSnapshot ->
//                                                                                    membersQSnapshot.documents.forEach { membersDSnapshot ->
//                                                                                        val project =
//                                                                                            membersDSnapshot.toObject(
//                                                                                                Project::class.java
//                                                                                            )
////                                                                                            Project(
////                                                                                            projectDocSnapshot.getString("projectTitle"),
////                                                                                            projectDocSnapshot.getString("projectDescription"),
////                                                                                            projectDocSnapshot.getString("projectStage"),
////                                                                                            projectDocSnapshot.getString("startDate"),
////                                                                                            projectDocSnapshot.getString("endDate"),
////                                                                                        )
//                                                                                        projects.add(
//                                                                                            project!!
//                                                                                        )
//                                                                                    }
//                                                                                    Timber.d("THE PRJS $projects")
//                                                                                }
//                                                                                .addOnFailureListener {
//                                                                                    Timber.e("error getting project members")
//                                                                                }
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }.addOnFailureListener {
//                                                                Timber.e("Error getting org projects")
//                                                            }
//                                                    }
//                                                }.addOnFailureListener {
//                                                    Timber.e("Error getting organizations")
//                                                }
//                                        }
//                                    }
//                                }
//                            }.addOnFailureListener { _ ->
//                                Timber.e("error getting projects id")
//
//                            }
//                    }
//                }.await()
//
//            Timber.d("Projects Ids: $projectsId")
//
////            if (projectsId.isNotEmpty()){
////                organizations.let {
////                    it.forEach { organization ->
////                        organizationCollection.whereEqualTo("name", organization.name)
////                            .get().addOnSuccessListener { querySnapshot ->
////                                querySnapshot.documents.forEach { documentSnapshot ->
////                                    organizationCollection.document(documentSnapshot.id)
////                                        .collection(PROJECT_COLLECTION)
////                                        .get().addOnSuccessListener { projectQuerySnapshot ->
////                                            projectQuerySnapshot.documents.forEach { projectDocSnapshot ->
////                                                projectsId.forEach { pId ->
////                                                    if (projectDocSnapshot.id == pId) {
////                                                        organizationCollection.document(documentSnapshot.id)
////                                                            .collection(PROJECT_COLLECTION)
////                                                            .document(projectDocSnapshot.id)
////                                                            .collection(MEMBERS)
////                                                            .get().addOnSuccessListener { membersQSnapshot ->
////                                                                membersQSnapshot.documents.forEach { membersDSnapshot ->
////                                                                    val project = Project(
////                                                                        projectDocSnapshot.getString("projectTitle"),
////                                                                        projectDocSnapshot.getString("projectDescription"),
////                                                                        projectDocSnapshot.getString("projectStage"),
////                                                                        projectDocSnapshot.getString("startDate"),
////                                                                        projectDocSnapshot.getString("endDate"),
////                                                                    )
////                                                                    projects.add(project)
////                                                                }
////                                                            }.addOnFailureListener {
////                                                                Timber.e("error getting project members")
////                                                            }
////                                                    }
////                                                }
////                                            }
////                                        }.addOnFailureListener {
////                                            Timber.e("Error getting org projects")
////                                        }
////                                }
////                            }.addOnFailureListener {
////                                Timber.e("Error getting organizations")
////                            }.apply { await() }
////                    }
////                }
////            }
//        } catch (e: Exception) {
//            Timber.e(e.message.toString())
//        }
//        return projects
//    }
}