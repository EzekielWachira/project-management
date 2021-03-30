package com.ezzy.projectmanagement.ui.activities.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityProjectBinding
import com.ezzy.projectmanagement.ui.activities.auth.LoginActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProjectActivity : AppCompatActivity() {

//    @Inject
//    lateinit var authUser: FirebaseUser
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var authUI: AuthUI

    private lateinit var binding : ActivityProjectBinding
    private lateinit var projectNavHostContainer : FragmentContainerView

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

        val navController = this.findNavController(R.id.projectNavHostContainer)

        binding.bottomNavigation.setupWithNavController(
            navController
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionChatroom -> {
                makeToast("chatrooms")
            }
            R.id.actionOrganization -> {
                makeToast("Organizations")
            }
            R.id.actionLogout -> {
                authUI.signOut(this).addOnCompleteListener {
                    if (it.isSuccessful) {
                        makeToast("Logged out successfully")
                        startActivity(
                            Intent(this, LoginActivity::class.java)
                        )
                        finish()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}