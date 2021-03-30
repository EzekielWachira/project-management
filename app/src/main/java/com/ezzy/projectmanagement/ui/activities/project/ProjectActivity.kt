package com.ezzy.projectmanagement.ui.activities.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityProjectBinding
import com.ezzy.projectmanagement.ui.activities.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProjectActivity : AppCompatActivity() {

//    @Inject
//    lateinit var authUser: FirebaseUser
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.projectNavHostContainer) as NavHostFragment
        val navController = this.findNavController(R.id.projectNavHostContainer)

//        projectNavHostContainer = binding.projectNavHostContainer

        binding.bottomNavigation.setupWithNavController(
            navController
        )
    }
//
//    private fun setUpBottomNavigation() {
//        binding.bottomNavigation.setOnNavigationItemReselectedListener { menuItem ->
//            when(menuItem.itemId) {
//                R.id.projectFragment -> {
//
//                }
//                R.id.activityFragment -> {
//                    TODO("push navigation fragments")
//                }
//                R.id.calendarFragment -> {
//                    TODO("push navigation fragments")
//                }
//                R.id.profileFragment -> {
//                    TODO("push navigation fragments")
//                }
//            }
//        }
//    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}