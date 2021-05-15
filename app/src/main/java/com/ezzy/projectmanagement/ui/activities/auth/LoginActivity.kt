package com.ezzy.projectmanagement.ui.activities.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityLoginBinding
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.ui.activities.project.ProjectActivity
import com.ezzy.projectmanagement.util.Constants.SIGN_IN_REQUEST_CODE
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var authUI: AuthUI
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
//    @Inject
//    lateinit var authUser: FirebaseUser

    private lateinit var loginActivityBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)
        createLoginUi()
    }

    fun createLoginUi () {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityIfNeeded(
            authUI.createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(true)
                .setTheme(R.style.Theme_ProjectManagement)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = firebaseAuth.currentUser
                startActivity(
                    Intent(this, ProjectActivity::class.java).apply {
                        putExtra("user", user)
                    }
                )
                finish()
            } else {
                if (response == null){
                    finish()
                }
                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK){
                    return
                }
                if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR){
                    makeToast(response?.error?.errorCode.toString())
                    return
                }
            }
        }
    }

    private fun makeToast(message : String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}