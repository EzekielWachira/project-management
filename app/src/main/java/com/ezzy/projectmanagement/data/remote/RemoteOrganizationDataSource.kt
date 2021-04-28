package com.ezzy.projectmanagement.data.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.ezzy.core.data.OrganizationDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.net.URI
import java.util.*
import javax.inject.Inject

class RemoteOrganizationDataSource @Inject constructor(
    val firestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : OrganizationDataSource{

    val organizations = MutableLiveData<List<Organization>>()

    override suspend fun addOrganization(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri: URI
    ) {
        try {
            var imagePath : String? = null
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
                                        .collection(Constants.MEMBERS)
                                        .add(member)
                                        .addOnSuccessListener {
                                            Timber.d("SUCCESS")
                                        }
                                        .addOnFailureListener {
                                            Timber.d("SUCCESS")
                                        }
                                }
                                Timber.d("SUCCESS")
                            }.addOnFailureListener {
                                Timber.d("SUCCESS")
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

    override suspend fun getUserOrganizations(userEmail : String): List<Organization> {
        val userOrganizations = mutableListOf<Organization>()
        try {
            val organizationCollection = firestore.collection(ORGANIZATIONS)
            organizationCollection.get()
                .addOnSuccessListener {
                    it.documents.forEach { docSnapShot ->
                        organizationCollection.document(docSnapShot.id)
                            .collection(MEMBERS)
                            .whereEqualTo("email", userEmail.toLowerCase(Locale.getDefault()))
                            .get()
                            .addOnSuccessListener { querySnapShot ->
                                querySnapShot.documents.forEach { _ ->
                                    val organization = Organization(
                                        docSnapShot.getString("name"),
                                        docSnapShot.getString("imageUrl"),
                                        docSnapShot.getString("about")
                                    )
                                    userOrganizations.add(organization)
                                }
                            }.addOnFailureListener {
                                Timber.e("Error  getting organization members with the logged in user")
                            }
                    }
                }.addOnFailureListener {
                    Timber.e("Errpr getting organizations")
                }.await()
        } catch (e : Exception) {
            Timber.e("Error getting logged in user organizations")
        }
        return userOrganizations
    }

}

//.addOnCompleteListener {
//    if (it.isSuccessful){
//        it.result!!.forEach { docSnapshot ->
//            organizationId = docSnapshot.id
//        }
//    }
//}