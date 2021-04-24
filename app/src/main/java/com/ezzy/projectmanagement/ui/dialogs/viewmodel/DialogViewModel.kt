package com.ezzy.projectmanagement.ui.dialogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.AddMember
import com.ezzy.core.interactors.GetAllUser
import com.ezzy.core.interactors.SearchMembers
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    val app: Application,
    val getAllUsers: GetAllUser,
    val searchMembers: SearchMembers,
    val addMember: AddMember
) : AndroidViewModel(app) {

    private var _isSearching = MutableLiveData<Boolean>()
    val isSearching : LiveData<Boolean> get()  = _isSearching
    private var _members = MutableLiveData<List<User>>()
    val members : LiveData<List<User>> get() = _members
    private var _allMembers = MutableLiveData<List<User>>()
    val allMembers : LiveData<List<User>> get() = _allMembers
    private var _selectedMembers = MutableLiveData<Set<User>>()
    val selectedMembers  get() = _selectedMembers

    init {
        getAllMembers()
    }

    fun getAllMembers() {
        viewModelScope.launch {
            _isSearching.postValue(true)
            val results = getAllUsers()
            if (results.isNotEmpty()){
                _isSearching.postValue(false)
                _allMembers.postValue(results)
            }
        }
    }

    fun searchMember (name: String) {
        viewModelScope.launch {
            _isSearching.postValue(true)
            val results = searchMembers(name)
            if (results.isNotEmpty()){
                _isSearching.postValue(false)
                _members.postValue(results)
            }
        }
    }

    fun addMembers(memberSet : Set<User>) {
        viewModelScope.launch {
            val results =  addMember(memberSet)
            _selectedMembers.postValue(results)
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(app.applicationContext, message, Toast.LENGTH_LONG).show()
    }

}

operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.value = value
}
