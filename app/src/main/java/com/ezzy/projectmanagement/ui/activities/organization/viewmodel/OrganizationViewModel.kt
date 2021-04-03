package com.ezzy.projectmanagement.ui.activities.organization.viewmodel

import android.app.Application
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

@HiltViewModel
class OrganizationViewModel @Inject constructor(
    app: Application,
    val firebaseFirestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : AndroidViewModel(app) {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess get() = _isSuccess

    private fun addOrganization(organization: Organization) = viewModelScope.launch {
        firebaseFirestore.collection(ORGANIZATIONS).add(organization)
            .addOnSuccessListener {
                isSuccess.postValue(true)
            }.addOnFailureListener {
                isSuccess.postValue(false)
            }.await()
    }
    
    private fun saveOrgImage(fileName : String) =  viewModelScope.launch {
            firebaseStorage.reference.child()
    }

}