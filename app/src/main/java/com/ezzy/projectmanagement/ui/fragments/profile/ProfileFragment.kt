package com.ezzy.projectmanagement.ui.fragments.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentProfileBinding
import com.ezzy.projectmanagement.ui.activities.SettingsActivity
import com.ezzy.projectmanagement.ui.activities.organization.NewOrganizationActivity
import com.ezzy.projectmanagement.ui.dialogs.UpdateProfileDialog
import com.ezzy.projectmanagement.util.imageResult
import com.ezzy.projectmanagement.util.requestPermission
import com.ezzy.projectmanagement.util.selectPicture
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var picImageUri : Uri? = null

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

        binding.editImageView.setOnClickListener{
            requestPermissions()
        }


        return binding.root
    }

    private fun selectImage() {
        activity?.let { selectPicture<ProfileFragment>(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        picImageUri = activity?.let {
            imageResult<ProfileFragment>(
                requestCode, resultCode, data, it,
                binding.userImageView
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissions()
    }

    private fun requestPermissions() {
        activity?.let {
            if (requestPermission<ProfileFragment>(it)){
                selectImage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}