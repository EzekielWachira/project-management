package com.ezzy.projectmanagement.ui.activities.organization.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
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
    val addMembers: AddMembers,
    val getOrganizationProjects: RetrieveOrganizationProjects,
    val getOrganizationMembers: RetrieveOrganizationMembers,
    val getOrgId: GetOrgId,
    val getUserOrganizations: GetUserOrganizations,
    val getOrganizationsIds: GetOrganizationsIds
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
    private var _organizationMembers  = MutableLiveData<List<User>>()
    val organizationMembers : LiveData<List<User>> get() = _organizationMembers
    private var _organizationProjects = MutableLiveData<List<Project>>()
    val organizationProjects : LiveData<List<Project>> get() = _organizationProjects
    private var _orgId = MutableLiveData<String>()
    val orgId : LiveData<String> get() = _orgId
    private val _userOrganizations = MutableLiveData<Set<Organization>>()
    val userOrganizations : LiveData<Set<Organization>> get() = _userOrganizations
    private var _organizationsIds = MutableLiveData<List<String>>()
    val organizationsIds : LiveData<List<String>> get() = _organizationsIds

    init {
        getUserOrgs()
        getAllOrganizations()
        getOrgsIds()
    }

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
            _isSuccess.postValue(true)
            val results = retrieveOrganizations()
            if(results.isNotEmpty()){
                _organizations.postValue(results)
                _isSuccess.postValue(false)
            } else {
                _isSuccess.postValue(false)
                _isOrgLoadingSuccess.postValue(false)
            }
    }

    fun getOrgsIds() {
        viewModelScope.launch {
            val results = getOrganizationsIds()
            if (results.isNotEmpty()){
                Timber.d("ORG IDSSggg $results")
            }
        }
    }

    fun getUserOrgs() = viewModelScope.launch {
        _isSuccess.postValue(true)
        val results = getUserOrganizations()
        Timber.d("BREE ($results)")
        if (results.isNotEmpty()){
            _isSuccess.postValue(false)
            _userOrganizations.postValue(results)
        } else _isSuccess.postValue(true)
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

    fun getOrgProjects(organizationId : String) {
        viewModelScope.launch {
            val results = getOrganizationProjects(organizationId)
            if (results.isNotEmpty()){
                _organizationProjects.postValue(results)
            }
        }
    }

    fun getOrgMembers(organizationId: String) {
        viewModelScope.launch {
            val results = getOrganizationMembers(organizationId)
            if (results.isNotEmpty()){
                _organizationMembers.postValue(results)
            }
        }
    }

    fun getOrganizationId(orgName : String) {
        viewModelScope.launch {
            val results = getOrgId(orgName)
            if (results.isNotEmpty()){
                _orgId.postValue(results)
            }
        }
    }
}