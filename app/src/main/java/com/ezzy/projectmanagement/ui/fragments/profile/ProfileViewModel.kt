package com.ezzy.projectmanagement.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.UpdateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val updateUser: UpdateUser
) : ViewModel() {

    private var _isUserUpdateSuccess = MutableLiveData<Boolean>()
    val isUserUpdateSuccess : LiveData<Boolean> get() = _isUserUpdateSuccess

    fun updateAuthUser(user : User) = viewModelScope.launch {
        val result = updateUser(user)
        if (result) {
            _isUserUpdateSuccess.postValue(true)
        } else {
            _isUserUpdateSuccess.postValue(false)
        }
    }
}