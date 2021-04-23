package com.ezzy.projectmanagement.data.remote

import android.net.Uri
import com.ezzy.core.data.OrganizationDataSource
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants
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

    override suspend fun addOrganization(
        organization: Organization,
        membersSet: Set<User>,
        fileName: String,
        imageUri: URI
    ) {
        try {
            var imagePath : String? = null
            val imgUri = Uri.parse(imageUri.toString())

//            Uri.Builder().scheme(imageUri.scheme)
//                .encodedAuthority(imageUri.rawAuthority)
//                .encodedPath(imageUri.rawQuery)
//                .fragment(imageUri.rawFragment)
//                .build()

            val storageReference = firebaseStorage.reference.child("images/${Constants.ORGANIZATIONS}/$fileName")
            val organizationReference = firestore.collection(Constants.ORGANIZATIONS)

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
        val orgs = mutableListOf<Organization>()
        try {
            firestore.collection(Constants.ORGANIZATIONS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.forEach { documentSnapshot ->
                            val organization = Organization(
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("imageSrc"),
                                documentSnapshot.getString("about")
                            )
                            orgs.add(organization)
                        }
                    } else {
                        Timber.e("An error occurred!!!!")
                    }
                }.addOnFailureListener {
                    Timber.e("An error occurred!!!!")
                }

        } catch (e : Exception) {
            Timber.e("An error occurred!!!!")
        }
        return orgs
    }

    override suspend fun searchOrganization(name: String): List<Organization> {
        val orgs = mutableListOf<Organization>()
        try {
            firestore.collection(Constants.ORGANIZATIONS)
                .whereEqualTo("name", name.toLowerCase(Locale.getDefault()))
                .get()
                .addOnCompleteListener {
                    it.result!!.forEach { documentSnapshot ->
                        var organization = Organization(
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("imageUrl"),
                            documentSnapshot.getString("about")
                        )
                        orgs.add(organization)
                    }
                }.addOnFailureListener {
                    Timber.d(it.message.toString())
                }
        } catch ( e : Exception ){
            Timber.d(e.message.toString())
        }
        return orgs
    }

    override suspend fun addOrgs(organizationSet: Set<Organization>): Set<Organization> {
//        val orgsSet = mutableSetOf<Organization>()
//        if (orgsSet.contains(organization)){
//            Timber.d("Org is already added")
//        } else {
//            orgsSet.add(organization)
//        }
        return organizationSet
    }

    override suspend fun addMembers(membersSet: Set<User>): Set<User> {
        return membersSet
    }

}