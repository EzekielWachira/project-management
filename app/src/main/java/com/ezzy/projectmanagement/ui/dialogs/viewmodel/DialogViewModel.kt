package com.ezzy.projectmanagement.ui.dialogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    val app: Application,
    val firestore: FirebaseFirestore
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
        val members = mutableSetOf<User>()
        if (members.contains(member)){
            makeToast("Member already exist in list")
        } else {
            members.add(member)
        }
        _selectedMembers.postValue(members)
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
