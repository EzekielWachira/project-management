package com.ezzy.projectmanagement.ui.activities.newproject.viewmodel

import android.app.Application
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
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewProjectViewModel @Inject constructor(
    app: Application,
    val addProject: AddProject,
    val attachOrganization: AttachOrganization,
    val attachMembers: AttachMembers
) : AndroidViewModel (app){

    private val _isError = MutableLiveData<Boolean>()
    val isError : LiveData<Boolean> get() = _isError
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> get() = _errorMessage
    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> get() = _isSuccess
    private var _organizations = MutableLiveData<Set<Organization>>()
    val organizations : LiveData<Set<Organization>> get() = _organizations
    private var _members = MutableLiveData<Set<User>>()
    val members : LiveData<Set<User>> get() = _members

    init {
        reloadOrgs()
    }

    fun reloadOrgs() = organizations

    fun saveProject(organizationsList : Set<Organization>?, project: Project, members : Set<User>?) {
        viewModelScope.launch {
            try {
                addProject(organizationsList!!, project, members!!)
                _isSuccess.postValue(true)
            } catch (e : Exception){
                Timber.e(e.message.toString())
                _isSuccess.postValue(false)
            }
//            try {
//                val projectsRef = firebaseFirestore.collection(PROJECT_COLLECTION)
//                val organizationRef = firebaseFirestore.collection(ORGANIZATIONS)
//                organizationsList?.forEach { org ->
//                    organizationRef.whereEqualTo("name", org.name)
//                        .get()
//                        .addOnCompleteListener {
//                            if (it.isSuccessful){
//                                it.result!!.forEach { docSnapshot ->
//                                    organizationRef.document(docSnapshot.id)
//                                        .collection(PROJECT_COLLECTION)
//                                        .add(project)
//                                        .addOnSuccessListener { docReference ->
//                                            members?.let { users ->
//                                                users.forEach { member ->
//                                                    organizationRef.document(docSnapshot.id)
//                                                        .collection(PROJECT_COLLECTION)
//                                                        .document(docReference.id)
//                                                        .collection(MEMBERS)
//                                                        .add(member)
//                                                        .addOnSuccessListener {
//                                                            _isSuccess.postValue(true)
//                                                        }
//                                                        .addOnFailureListener {
//                                                            _isSuccess.postValue(false)
//                                                        }
//                                                }
//                                            }
//                                        }
//                                        .addOnFailureListener {
//                                            Timber.e("Error add project to organization")
//                                        }
//                                }
//                            }
//                        }.addOnFailureListener {
//                            Timber.e("An error occurred while searching")
//                        }
//                        .await()
//                    _isError.postValue(false)
//                }
//            } catch (e : Exception) {
//                _isError.postValue(true)
//                _errorMessage.postValue(e.message)
//            }
        }
    }

    fun addOrganizations(organizationList: Set<Organization>){
        viewModelScope.launch {
            _organizations.postValue(attachOrganization(organizationList)!!)
        }
    }

    fun addMembers(membersSet: Set<User>){
        viewModelScope.launch {
            _members.postValue(attachMembers(membersSet)!!)
        }
    }

}
