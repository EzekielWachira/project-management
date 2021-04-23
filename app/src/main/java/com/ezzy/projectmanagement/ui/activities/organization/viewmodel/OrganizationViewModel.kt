package com.ezzy.projectmanagement.ui.activities.organization.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class OrganizationViewModel @Inject constructor(
    app: Application,
    val addOrganization: AddOrganization,
    val retrieveOrganizations: RetrieveOrganizations,
    val searchOrganizations: SearchOrganizations,
    val addOrgs: AddOrgs,
    val addMembers: AddMembers
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

    init { getAllOrganizations() }

    fun addOrg(
        organization: Organization, membersSet: Set<User>? ,fileName : String, imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                val imageSrcUri = URI.create(imageUri.toString())
                addOrganization( organization, membersSet!!, fileName, imageSrcUri )
                _isSuccess.postValue(true)
            }catch (e : Exception){
                Timber.e("Error adding organization: ${e.message.toString()}")
                _isSuccess.postValue(false)
            }
        }
    }

    fun getAllOrganizations() =  viewModelScope.launch {
        try {
            _isSuccess.postValue(true)
            val results = retrieveOrganizations()
            if(results.isNotEmpty()){
                _organizations.postValue(results)
                _isSuccess.postValue(false)
            } else {
                _isSuccess.postValue(false)
                _isOrgLoadingSuccess.postValue(false)
            }
        } catch (e : Exception) {
            _isOrgLoadingSuccess.postValue(false)
        }
    }

    fun searchOrgs(keyword : String) {
        viewModelScope.launch {
            try {
                val results = searchOrganizations(keyword)
                if (results.isNotEmpty()){
                    _orgsSearched.postValue(results)
                } else {
                    Timber.d("results are empty")
                }
            } catch (e : Exception){
                Timber.e("Error searching organization: ${e.message.toString()} ")
            }
        }
    }

    fun attachOrgs(organizationSet: Set<Organization>) {
        viewModelScope.launch {
            val results = addOrgs(organizationSet)
            _orgsSelected.postValue(results)
        }
    }

    fun attachMembers(membersSet : Set<User>) {
        viewModelScope.launch {
            val results = addMembers(membersSet)
            _members.postValue(results)
        }
    }
}