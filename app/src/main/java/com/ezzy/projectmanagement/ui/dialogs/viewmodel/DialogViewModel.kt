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
            val results = getAllUsers()
            if (results.isNotEmpty()){
                _allMembers.postValue(results)
            }
        }
//        try {
//            _isSearching.postValue(true)
//            firestore.collection(USERS)
//                .get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful){
//                        _isSearching.postValue(false)
//                        val results = mutableListOf<User>()
//                        for (querySnapshot in it.result!!){
//                            val member = User(querySnapshot.getString("name"), querySnapshot.getString("email"))
//                            results.add(member)
//                        }
//                        _allMembers.postValue(results)
//                    }
//                }.addOnFailureListener {
//                    _isSearching.postValue(false)
//                    Timber.d("Error getting users")
//                }
//        } catch (e: Exception){
//            Timber.e(e.message.toString())
//        }
    }

    fun searchMember (name: String) {
        viewModelScope.launch {
            val results = searchMembers(name)
            if (results.isNotEmpty()){
                _members.postValue(results)
            }
        }
//        try {
//            _isSearching.postValue(true)
//            firestore.collection(USERS).whereEqualTo("name", name)
//                .get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful){
//                        _isSearching.postValue(false)
//                        val results = mutableListOf<User>()
//                        for (snapshot in it.result!!){
//                            val member = User(snapshot.getString("name"), snapshot.getString("email"))
//                            results.add(member)
//                        }
//                        _members.postValue(results)
//                        Timber.d("USERS ==>> $members")
//                    }
//                }.addOnFailureListener {
//                    _isSearching.postValue(true)
//                    Timber.e("Error searching members")
//                }
//        } catch (e : Exception) {
//            Timber.e("Error searching members")
//        }
    }

    fun addMembers(memberSet : Set<User>) {
        viewModelScope.launch {
            val results =  addMember(memberSet)
            _selectedMembers.postValue(results)
        }
//        val members = mutableSetOf<User>()
//        if (members.contains(member)){
//            makeToast("Member already exist in list")
//        } else {
//            members.add(member)
//        }
//        _selectedMembers.postValue(members)
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
