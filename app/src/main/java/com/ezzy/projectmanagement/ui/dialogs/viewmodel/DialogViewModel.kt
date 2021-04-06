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

    fun searchMembers (name: String) {
        try {
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
                            Timber.d("MEMBER: >> $results")
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

}

operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
    val value = this.value ?: arrayListOf()
    value.addAll(values)
    this.value = value
}
