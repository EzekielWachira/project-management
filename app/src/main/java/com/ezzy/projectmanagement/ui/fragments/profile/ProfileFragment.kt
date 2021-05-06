package com.ezzy.projectmanagement.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentProfileBinding
import com.ezzy.projectmanagement.ui.activities.SettingsActivity
import com.ezzy.projectmanagement.ui.dialogs.UpdateProfileDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val profileViewModel : ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(
            inflater, container, false
        )

        binding.editFAB.setOnClickListener {
            UpdateProfileDialog().show(
                activity?.supportFragmentManager!!,
                "Edit User"
            )
        }

        binding.usernameTxt.text = firebaseAuth.currentUser?.displayName
        binding.emailTxt.text = firebaseAuth.currentUser?.email

        binding.settingsLayout.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}