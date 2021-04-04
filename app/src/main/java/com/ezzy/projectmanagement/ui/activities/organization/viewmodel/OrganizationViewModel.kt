package com.ezzy.projectmanagement.ui.activities.organization.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "OrganizationViewModel"
@HiltViewModel
class OrganizationViewModel @Inject constructor(
    app: Application,
    val firebaseFirestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : AndroidViewModel(app) {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess get() = _isSuccess
    private val _isImageUploaded = MutableLiveData<Boolean>()
    val isImageUploaded get() = _isImageUploaded

    fun addOrganization(organization: Organization, fileName : String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                var imagePath : String? = null
                val storageReference = firebaseStorage.reference.child("images/${ORGANIZATIONS}/$fileName")
                storageReference.putFile(imageUri)
                    .addOnSuccessListener {
                        isImageUploaded.postValue(true)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imagePath = uri.toString()
                            organization.imageSrc = imagePath
                            firebaseFirestore.collection(ORGANIZATIONS).add(organization)
                                .addOnSuccessListener {
                                    isSuccess.postValue(true)
                                }.addOnFailureListener {
                                    isSuccess.postValue(false)
                                }
                        }
                    }.addOnFailureListener {
                        isImageUploaded.postValue(false)
                    }.await()
            } catch (e: Exception) {
                isImageUploaded.postValue(false)
            }
        }
    }
    
    fun saveOrgImage(fileName : String, imageUri: Uri) : String? {
        var imagePath : String? = null
        viewModelScope.launch {
            try {
                val storageReference = firebaseStorage.reference.child("images/${ORGANIZATIONS}/$fileName")
                    storageReference.putFile(imageUri)
                    .addOnSuccessListener {
                        isImageUploaded.postValue(true)
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imagePath = uri.toString()
                        }
                    }.addOnFailureListener {
                        isImageUploaded.postValue(false)
                    }.await()
            } catch (e: Exception) {
                isImageUploaded.postValue(false)
            }
        }
        return imagePath
    }

}