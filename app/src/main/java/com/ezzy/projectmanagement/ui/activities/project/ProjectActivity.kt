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
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.ui.activities.auth.LoginActivity
import com.ezzy.projectmanagement.ui.activities.newproject.NewProjectActivity
import com.ezzy.projectmanagement.ui.activities.organization.NewOrganizationActivity
import com.ezzy.projectmanagement.util.Constants.USERS
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProjectActivity : AppCompatActivity() {

//    @Inject
//    lateinit var authUser: FirebaseUser
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var authUI: AuthUI
    @Inject
    lateinit var firestore: FirebaseFirestore
    private var firebaseUser: FirebaseUser? = null

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
//            firebaseUser = firebaseAuth.currentUser
            firebaseAuth.currentUser?.let {
                 saveUserToFirebase(it)
            }
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
            R.id.actionNewProject -> {
                startActivity(
                    Intent(this, NewProjectActivity::class.java)
                )
            }
            R.id.actionSearch -> {
                makeToast("Search")
            }
            R.id.actionNewOrg -> {
                startActivity(Intent(this, NewOrganizationActivity::class.java))
            }
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

    private fun saveUserToFirebase(firebaseUser: FirebaseUser) {
        val user = User(
            firebaseUser.displayName,
            firebaseUser.email
        )
        try {
            firestore.collection(USERS).add(user)
                .addOnSuccessListener {
                    makeToast("User saved")
                }.addOnFailureListener {
                    makeToast("Error saving user")
                }
        } catch (e : Exception) {
            Timber.d("USER ERROR ${e.message.toString()}")
            makeToast(e.message.toString())
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}