package com.ezzy.projectmanagement.ui.activities.newproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.projectmanagement.model.Project
import com.ezzy.projectmanagement.util.Constants.PROJECT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewProjectViewModel @Inject constructor(
    app: Application,
    val firebaseFirestore: FirebaseFirestore
) : AndroidViewModel (app){

    private val _isError = MutableLiveData<Boolean>()
    val isError get() = _isError
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage get() = _errorMessage

    fun addProject(project: Project) = viewModelScope.launch {
        try {
            firebaseFirestore.collection(PROJECT_COLLECTION).add(project).await()
            isError.postValue(false)
        } catch (e : Exception) {
            isError.postValue(true)
            errorMessage.postValue(e.message)
        }
    }

}