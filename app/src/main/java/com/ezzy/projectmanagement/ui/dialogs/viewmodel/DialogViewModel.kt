package com.ezzy.projectmanagement.ui.dialogs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {

    private var _isSearching = MutableLiveData<Boolean>()
    val isSearching : LiveData<Boolean> get()  = _isSearching
    private var _members = MutableLiveData<List<User>>()
    val members : LiveData<List<User>> get() = _members
    private var _allMembers = MutableLiveData<List<User>>()
    val allMembers : LiveData<List<User>> get() = _allMembers
    private var _selectedMembers = MutableLiveData<List<User>>()
    val selectedMembers  get() = _selectedMembers

    init {
        getAllMembers()
    }

    fun getAllMembers() {
        try {
            _isSearching.postValue(true)
            firestore.collection(USERS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        _isSearching.postValue(false)
                        val results = mutableListOf<User>()
                        for (querySnapshot in it.result!!){
                            val member = User(querySnapshot.getString("name"), querySnapshot.getString("email"))
                            results.add(member)
                        }
                        _allMembers.postValue(results)
                    }
                }.addOnFailureListener {
                    _isSearching.postValue(false)
                    Timber.d("Error getting users")
                }
        } catch (e: Exception){
            Timber.e(e.message.toString())
        }
    }

    fun searchMembers (name: String) {
        try {
            _isSearching.postValue(true)
            firestore.collection(USERS).whereEqualTo("name", name)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        _isSearching.postValue(false)
                        val results = mutableListOf<User>()
                        for (snapshot in it.result!!){
                            val member = User(snapshot.getString("name"), snapshot.getString("email"))
                            results.add(member)
                        }
                        _members.postValue(results)
                        Timber.d("USERS ==>> $members")
                    }
                }.addOnFailureListener {
                    _isSearching.postValue(true)
                    Timber.e("Error searching members")
                }
        } catch (e : Exception) {
            Timber.e("Error searching members")
        }
    }

    fun addMembers(member : User) {
        val members = mutableListOf<User>()
        if (members.contains(member)){
            return
        } else {
            members.add(member)
        }
        _selectedMembers.postValue(members)
    }

}

operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.value = value
}
