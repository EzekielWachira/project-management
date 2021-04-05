package com.ezzy.projectmanagement.ui.activities.newproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ezzy.projectmanagement.model.Project
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
    private  val TAG = "NewProjectViewModel"
    private val _isError = MutableLiveData<Boolean>()
    val isError get() = _isError
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage get() = _errorMessage

    fun addProject(project: Project) = viewModelScope.launch {
        try {
            firebaseFirestore.collection(PROJECT_COLLECTION).add(project)
                .addOnSuccessListener {
                    Timber.d("addProject: Success")
                }.addOnFailureListener{
                    Timber.d("addProject: ${it.message}")
                }.await()
            isError.postValue(false)
        } catch (e : Exception) {
            isError.postValue(true)
            errorMessage.postValue(e.message)
        }
    }

}