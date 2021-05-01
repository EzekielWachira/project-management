package com.ezzy.projectmanagement.ui.fragments.project.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.interactors.GetAll
import com.ezzy.core.interactors.GetUserProjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseProjectViewModel @Inject constructor(
    app : Application,
    val getAll: GetAll,
    val getUserProjects: GetUserProjects
) : AndroidViewModel(app){

    private var _allProjects = MutableLiveData<List<Project>>()
    val allProjects : LiveData<List<Project>> get() = _allProjects
    private var _isProjectLoadSuccess = MutableLiveData<Boolean>()
    val isProjectLoadSuccess : LiveData<Boolean> get() = _isProjectLoadSuccess
    private var _authUserProjects = MutableLiveData<List<Project>>()
    val authUserProjects : LiveData<List<Project>> get() = _authUserProjects

    init {
        getAllProjects()
    }

    fun getAllProjects() = viewModelScope.launch {
        _isProjectLoadSuccess.postValue(true)
        val results = getAll()
        if (results.isNotEmpty()){
            _isProjectLoadSuccess.postValue(false)
            _allProjects.postValue(results)
        }
    }

    fun getAuthUserProjects(organizationList: List<Organization>) {
        viewModelScope.launch {
            val results = getUserProjects(organizationList)
            if (results.isNotEmpty()) {
                _authUserProjects.postValue(results)
            }
        }
    }

}