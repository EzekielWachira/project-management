package com.ezzy.projectmanagement.ui.fragments.project.allprojects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.core.interactors.GetAll
import com.ezzy.core.domain.Project
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AllProjectsViewModel @Inject constructor(
    app: Application,
    val getAll: GetAll
) : AndroidViewModel(app) {

    private var _allProjects = MutableLiveData<List<Project>>()
    val allProjects : LiveData<List<Project>> get() = _allProjects
    private var _isProjectLoadSuccess = MutableLiveData<Boolean>()
    val isProjectLoadSuccess : LiveData<Boolean> get() = _isProjectLoadSuccess

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
}