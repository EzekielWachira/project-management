package com.ezzy.projectmanagement.ui.activities.newproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.model.Project
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.util.Constants.MEMBERS
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewProjectViewModel @Inject constructor(
    app: Application,
    val firebaseFirestore: FirebaseFirestore
) : AndroidViewModel (app){

    private val _isError = MutableLiveData<Boolean>()
    val isError : LiveData<Boolean> get() = _isError
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> get() = _errorMessage
    private var _isSuccess = MutableLiveData<Boolean>()
    val isSuccess : LiveData<Boolean> get() = _isSuccess
    private var _organizations = MutableLiveData<Set<Organization>>()
    val organizations : LiveData<Set<Organization>> get() = _organizations
    private var _members = MutableLiveData<List<User>>()
    val members : LiveData<List<User>> get() = _members

    init {

    }

    fun addProject(project: Project, members : Set<User>?) = viewModelScope.launch {
        try {
            val projectsRef = firebaseFirestore.collection(PROJECT_COLLECTION)
            projectsRef.add(project)
                .addOnSuccessListener { docReference ->
                    members?.let {  users ->
                        users.forEach{ member ->
                            projectsRef.document(docReference.id)
                                .collection(MEMBERS).add(member)
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                        _isSuccess.postValue(true)
                                    }
                                }.addOnFailureListener { e ->
                                    _isSuccess.postValue(false)
                                    _errorMessage.postValue(e.message.toString())
                                }
                        }
                    }

                    Timber.d("addProject: Success")
                }.addOnFailureListener{
                    Timber.d("addProject: ${it.message}")
                }.await()
            _isError.postValue(false)
        } catch (e : Exception) {
            _isError.postValue(true)
            _errorMessage.postValue(e.message)
        }
    }

    fun addOrganizations(organizationList: Set<Organization>){
        val orgs = mutableListOf<Organization>()
        _organizations.postValue(organizationList)
    }

    fun addMembers(membersList: List<User>){
        _members.postValue(membersList)
    }

}
