package com.ezzy.projectmanagement.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityProjectBinding
import com.ezzy.projectmanagement.ui.activities.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProjectActivity : AppCompatActivity() {

    @Inject
    lateinit var authUser: FirebaseUser
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding : ActivityProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (firebaseAuth.currentUser == null){
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        } else {
            makeToast("You are already logged in")
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}