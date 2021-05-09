package com.ezzy.projectmanagement.ui.fragments.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.User
import com.ezzy.core.interactors.SaveUserImage
import com.ezzy.core.interactors.UpdateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val updateUser: UpdateUser,
    val saveUserImage: SaveUserImage
) : ViewModel() {

    private var _isUserUpdateSuccess = MutableLiveData<Boolean>()
    val isUserUpdateSuccess : LiveData<Boolean> get() = _isUserUpdateSuccess
    private var _imagePath = MutableLiveData<String>()
    val imagePath : LiveData<String> get() = _imagePath

    fun updateAuthUser(imageUri: String?, user : User) {
        viewModelScope.launch {
            val result = updateUser(imageUri, user)
            if (result) {
                _isUserUpdateSuccess.postValue(true)
            } else {
                _isUserUpdateSuccess.postValue(false)
            }
        }
    }

    fun saveUserImg(imageUri : Uri, fileName : String, user: User){
        viewModelScope.launch {
            val imgURI = URI.create(imageUri.toString())
            val result = saveUserImage(imgURI, fileName, user)
            if (result){
                _isUserUpdateSuccess.postValue(true)
            } else {
                _isUserUpdateSuccess.postValue(false)
            }
        }
    }


}