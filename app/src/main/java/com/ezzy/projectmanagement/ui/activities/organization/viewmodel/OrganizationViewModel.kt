package com.ezzy.projectmanagement.ui.activities.organization.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class OrganizationViewModel @Inject constructor(
    app: Application,
    val firebaseFirestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : AndroidViewModel(app) {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> get() = _isSuccess
    private val _isImageUploaded = MutableLiveData<Boolean>()
    val isImageUploaded : LiveData<Boolean> get() = _isImageUploaded
    private var _organizations = MutableLiveData<List<Organization>>()
    val organizations : LiveData<List<Organization>> get() = _organizations
    private var _isOrgLoadingSuccess = MutableLiveData<Boolean>()
    val isOrgLoadingSuccess = _isOrgLoadingSuccess
    private var _orgsSearched = MutableLiveData<List<Organization>>()
    val orgsSearched : LiveData<List<Organization>> get() = _orgsSearched
    private var _orgsSelected = MutableLiveData<Set<Organization>>()
    val orgsSelected : LiveData<Set<Organization>> get() = _orgsSelected
    private var _members = MutableLiveData<Set<User>>()
    val members : LiveData<Set<User>> get()  = _members

    init {
        retrieveOrganizations()
    }

    fun addOrganization(
        organization: Organization, memberList: Set<User>? ,fileName : String, imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                var imagePath : String? = null
                val storageReference = firebaseStorage.reference.child("images/${ORGANIZATIONS}/$fileName")
                val organizationReference = firebaseFirestore.collection(ORGANIZATIONS)
                storageReference.putFile(imageUri)
                    .addOnSuccessListener {
                        _isImageUploaded.postValue(true)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imagePath = uri.toString()
                            organization.imageSrc = imagePath
                            organizationReference.add(organization)
                                .addOnSuccessListener { docReference ->
                                    memberList?.forEach { member ->
                                        organizationReference.document(docReference.id)
                                            .collection(MEMBERS)
                                            .add(member)
                                            .addOnSuccessListener {
                                                _isSuccess.postValue(true)
                                            }
                                            .addOnFailureListener {
                                                _isSuccess.postValue(false)
                                            }
                                    }
                                    _isSuccess.postValue(true)
                                }.addOnFailureListener {
                                    _isSuccess.postValue(false)
                                }
                        }
                    }.addOnFailureListener {
                        _isImageUploaded.postValue(false)
                    }.await()
            } catch (e: Exception) {
                _isImageUploaded.postValue(false)
            }
        }
    }

    fun retrieveOrganizations() = viewModelScope.launch {
        try {
            _isOrgLoadingSuccess.postValue(true)
            firebaseFirestore.collection(ORGANIZATIONS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _isOrgLoadingSuccess.postValue(false)
                        val orgs = mutableListOf<Organization>()
                        it.result!!.forEach { documentSnapshot ->
                            val organization = Organization(
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("imageSrc"),
                                documentSnapshot.getString("about")
                            )
                            orgs.add(organization)
                        }
                        _organizations.postValue(orgs)
                    } else {  _isOrgLoadingSuccess.postValue(false) }
                }.addOnFailureListener {
                    _isOrgLoadingSuccess.postValue(false)
                }

        } catch (e : Exception) {
            _isOrgLoadingSuccess.postValue(false)
        }
    }

    fun searchOrganizations(keyword : String) {
        viewModelScope.launch {
            try {
                val orgs = mutableListOf<Organization>()
                firebaseFirestore.collection(ORGANIZATIONS)
                    .whereEqualTo("name", keyword.toLowerCase(Locale.getDefault()))
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
                        _orgsSearched.postValue(orgs)
                    }.addOnFailureListener {
                        Timber.d(it.message.toString())
                    }
            } catch ( e : Exception ){
                Timber.d(e.message.toString())
            }
        }
    }

    fun addOrgs(organization: Organization) {
        val orgsList = mutableSetOf<Organization>()
        if (orgsList.contains(organization)){
            Timber.d("Org is already added")
        } else {
            orgsList.add(organization)
        }
        _orgsSelected.postValue(orgsList)
    }

    fun addMembers(membersSet : Set<User>) {
        _members.postValue(membersSet)
    }
    
    fun saveOrgImage(fileName : String, imageUri: Uri) : String? {
        var imagePath : String? = null
        viewModelScope.launch {
            try {
                val storageReference = firebaseStorage.reference.child("images/${ORGANIZATIONS}/$fileName")
                    storageReference.putFile(imageUri)
                    .addOnSuccessListener {
                        _isImageUploaded.postValue(true)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imagePath = uri.toString()
                        }
                    }.addOnFailureListener {
                        _isImageUploaded.postValue(false)
                    }.await()
            } catch (e: Exception) {
                _isImageUploaded.postValue(false)
            }
        }
        return imagePath
    }

}