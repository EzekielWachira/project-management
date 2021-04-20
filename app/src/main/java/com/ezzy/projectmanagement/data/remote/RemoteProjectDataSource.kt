package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ProjectDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class RemoteProjectDataSource @Inject constructor(
    val firestore: FirebaseFirestore
) : ProjectDataSource {

    override suspend fun add(
        organizationSet: Set<Organization>,
        project: Project,
        membersSet: Set<User>
    ) {
        try {
            val projectsRef = firestore.collection(Constants.PROJECT_COLLECTION)
            val organizationRef = firestore.collection(Constants.ORGANIZATIONS)
            organizationSet.forEach { org ->
                organizationRef.whereEqualTo("name", org.name)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            it.result!!.forEach { docSnapshot ->
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
                }
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