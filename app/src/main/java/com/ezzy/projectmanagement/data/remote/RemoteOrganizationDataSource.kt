package com.ezzy.projectmanagement.data.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.ezzy.core.data.OrganizationDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.SaveUserOrganizations
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.URI
import java.util.*
import javax.inject.Inject

class RemoteOrganizationDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage,
    val saveUserOrganizations: SaveUserOrganizations,
    val firebaseAuth: FirebaseAuth
) : OrganizationDataSource{

    val organizations = MutableLiveData<List<Organization>>()
    private val userCollection = firestore.collection(USERS)
    private val organizationCollection = firestore.collection(ORGANIZATIONS)
    private var authenticatedUser : User? = null
    private val userOrganizations = mutableSetOf<Organization>()
    val organizationsId  = mutableListOf<String>()

    init {
        authenticatedUser = User(
            firebaseAuth.currentUser!!.displayName,
            firebaseAuth.currentUser!!.email
        )

        Timber.d("AUTHENTICATED USER: $authenticatedUser")
    }

    override suspend fun addOrganization(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri: URI
    ) {
        try {
            var imagePath: String?
            val imgUri = Uri.parse(imageUri.toString())
            val storageReference = firebaseStorage.reference.child("images/$ORGANIZATIONS/$fileName")
            val organizationReference = firestore.collection(ORGANIZATIONS)

            storageReference.putFile(imgUri)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        imagePath = uri.toString()
                        organization.imageSrc = imagePath
                        organizationReference.add(organization)
                            .addOnSuccessListener { docReference ->
                                membersSet.forEach { member ->
                                    organizationReference.document(docReference.id)
                                        .collection(MEMBERS)
                                        .add(member)
                                        .addOnSuccessListener {
                                            userCollection.whereEqualTo("email", member.email)
                                                .get()
                                                .addOnSuccessListener {  querySnapshot ->
                                                    querySnapshot.documents.forEach { _ ->
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            saveUserOrganizations(
                                                                docReference.id,
                                                                member.email!!,
                                                            )
                                                        }
                                                    }
                                                    Timber.d("SUCCESS")
                                                }
                                        }
                                        .addOnFailureListener {
                                            Timber.d("FAILURE")
                                        }
                                }
                                Timber.d("SUCCESS")
                            }.addOnFailureListener {
                                Timber.d("FAILURE")
                            }
                    }
                }.addOnFailureListener {
                    Timber.d("An error occured")
                }.await()
        } catch (e: Exception) {
            Timber.d("An error occured")
        }
    }

    override suspend fun retrieveOrganizations(): List<Organization> {
        var orgs = mutableListOf<Organization>()
        try {
            firestore.collection(ORGANIZATIONS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val results = mutableListOf<Organization>()
                        it.result!!.forEach { documentSnapshot ->
                            val organization = Organization(
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("imageSrc"),
                                documentSnapshot.getString("about")
                            )
                            results.add(organization)
                        }
                        orgs = results
                        Timber.d("ONE ORG>> $orgs")
                    } else {
                        Timber.e("An error occurred!!!!")
                    }
                }.addOnFailureListener {
                    Timber.e("An error occurred!!!!")
                }.await()

        } catch (e : Exception) {
            Timber.e("An error occurred!!!!")
        }
        Timber.d("BLUUM: >> $orgs")
        return orgs
    }

    override suspend fun searchOrganization(name: String): List<Organization> {
        val orgs = mutableListOf<Organization>()
        try {
            firestore.collection(ORGANIZATIONS)
                .whereEqualTo("name", name.toLowerCase(Locale.getDefault()))
                .get()
                .addOnCompleteListener {
                    it.result!!.forEach { documentSnapshot ->
                        val organization = Organization(
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("imageUrl"),
                            documentSnapshot.getString("about")
                        )

                        orgs.add(organization)
                    }
                }.addOnFailureListener {
                    Timber.d(it.message.toString())
                }.await()
        } catch ( e : Exception ){
            Timber.d(e.message.toString())
        }
        return orgs
    }

    override suspend fun addOrgs(organizationSet: Set<Organization>): Set<Organization> {
        return organizationSet
    }

    override suspend fun addMembers(membersSet: Set<User>): Set<User> {
        return membersSet
    }

    override suspend fun getOrganizationMembers(orgId: String): List<User> {
        val members = mutableSetOf<User>()
        try {
            firestore.collection(ORGANIZATIONS)
                .document(orgId)
                .collection(MEMBERS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        it.result!!.forEach { docSnapshot ->
                            val member = User(
                                docSnapshot.getString("name"),
                                docSnapshot.getString("email")
                            )
                            members.add(member)
                        }
                    }
                }.addOnFailureListener {
                    Timber.e(it.message.toString())
                }.await()
        } catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return members.toList()
    }

    override suspend fun getOrganizationProjects(orgId: String): List<Project> {
        val projects = mutableListOf<Project>()
        try {
            firestore.collection(ORGANIZATIONS)
                .document(orgId)
                .collection(PROJECT_COLLECTION)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        it.result!!.forEach { docSnapShot ->
                            val project = Project(
                                docSnapShot.getString("projectTitle"),
                                docSnapShot.getString("projectDescription"),
                                docSnapShot.getString("projectStage"),
                                docSnapShot.getString("startDate"),
                                docSnapShot.getString("endDate"),
                            )
                            projects.add(project)
                        }
                    }
                }.addOnFailureListener {
                    Timber.e("ERROr querying organization projects")
                }.await()
        } catch (e : Exception){
            Timber.e("Error querying organization projects")
        }
        return projects
    }

    override suspend fun getOrganizationId(orgName: String): String {
        var organizationId = ""
        try {
            firestore.collection(ORGANIZATIONS)
                .whereEqualTo("name", orgName.toLowerCase(Locale.getDefault()))
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { snapshot ->
                        organizationId = snapshot.id
                    }
                }
                .addOnFailureListener {
                    Timber.e(it.message.toString())
                }
                .await()
        }catch (e : Exception){
            Timber.e(e.message.toString())
        }
        return organizationId
    }


    override suspend fun getUserOrganizations():
            Set<Organization> {
        try {
            userCollection.whereEqualTo("email", authenticatedUser?.email)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .collection(ORGANIZATIONS)
                            .get()
                            .addOnSuccessListener { querySnapShot ->
                                querySnapShot.documents.forEach { queryDocumentSnapshot ->
                                    organizationsId.add(
                                        queryDocumentSnapshot.getString("organization_id")!!
                                    )
                                }
                                if (organizationsId.isNotEmpty()) {
                                    organizationsId.forEach { orgId ->
                                        organizationCollection.get()
                                            .addOnSuccessListener { querySnapShot ->
                                                querySnapShot.documents.forEach { documentSnapshot ->
                                                    if (documentSnapshot.id == orgId){
                                                        val organization = Organization(
                                                            documentSnapshot.getString("name"),
                                                            documentSnapshot.getString("imageSrc"),
                                                            documentSnapshot.getString("about")
                                                        )
                                                        userOrganizations.add(organization)
                                                    }
                                                }
                                            }.addOnFailureListener {
                                                Timber.e("error retrieving user organizations")
                                            }
                                    }
                                }

                            }.addOnFailureListener { Timber.e("Error obtaining org ids") }
                            .apply { CoroutineScope(Dispatchers.IO).launch { await() } }
                    }
                }.addOnFailureListener { Timber.e("Error obtaining user") }.await()

//            if (organizationsId.isNotEmpty()) {
//                organizationsId.forEach { orgId ->
//                    organizationCollection.get()
//                        .addOnSuccessListener { querySnapShot ->
//                            querySnapShot.documents.forEach { documentSnapshot ->
//                                if (documentSnapshot.id == orgId){
//                                    val organization = Organization(
//                                        documentSnapshot.getString("name"),
//                                        documentSnapshot.getString("imageSrc"),
//                                        documentSnapshot.getString("about")
//                                    )
//                                    userOrganizations.add(organization)
//                                }
//                            }
//                        }.addOnFailureListener {
//                            Timber.e("error retrieving user organizations")
//                        }.await()
//                }
//            }

        } catch (e : Exception) {
            Timber.e("Error getting logged in user organizations")
        }

//        try {
//
//            organizationCollection.get()
//                .addOnSuccessListener {
//                    it.documents.forEach { docSnapShot ->
//                        organizationCollection.document(docSnapShot.id)
//                            .collection(MEMBERS)
//                            .whereEqualTo("email", organizationId.toLowerCase(Locale.getDefault()))
//                            .get()
//                            .addOnSuccessListener { querySnapShot ->
//                                querySnapShot.documents.forEach { _ ->
//                                    val organization = Organization(
//                                        docSnapShot.getString("name"),
//                                        docSnapShot.getString("imageUrl"),
//                                        docSnapShot.getString("about")
//                                    )
//                                    userOrganizations.add(organization)
//                                }
//                            }.addOnFailureListener {
//                                Timber.e("Error  getting organization members with the logged in user")
//                            }
//                    }
//                }.addOnFailureListener {
//                    Timber.e("Errpr getting organizations")
//                }.await()
//        } catch (e : Exception) {
//            Timber.e("Error getting logged in user organizations")
//        }
        Timber.d("USER ORGANIZATIONS: $userOrganizations")
        return userOrganizations

    }


    override suspend fun getOrganizationsIds() : List<String> {
        try {
            userCollection.whereEqualTo("email", authenticatedUser?.email)
                .get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        userCollection.document(documentSnapshot.id)
                            .collection(ORGANIZATIONS)
                            .get()
                            .addOnSuccessListener { querySnapShot ->
                                querySnapShot.documents.forEach { queryDocumentSnapshot ->
                                    organizationsId.add(
                                        queryDocumentSnapshot.getString("organization_id")!!
                                    )
                                }
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    getUserOrganizations(organizationsId)
//                                }
                            }
                    }
                }.addOnFailureListener { Timber.e("Error retrieving organizations ids") }
                .apply { await() }
        } catch (e : Exception) {
            Timber.e(e.message.toString())
        }
        Timber.d("MY IDS: $organizationsId")
        return organizationsId
    }

}