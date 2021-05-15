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
import com.ezzy.projectmanagement.util.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var picImageUri: Uri? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(
            inflater, container, false
        )

        profileViewModel.getUserInfo()

        binding.editFAB.setOnClickListener {
            UpdateProfileDialog().show(
                activity?.supportFragmentManager!!,
                "Edit User"
            )
        }

        binding.settingsLayout.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }

        binding.editImageView.setOnClickListener {
            requestPermissions()
        }

        profileViewModel.user.observe(viewLifecycleOwner) {

            if (firebaseAuth.currentUser!!.email!!.isNotEmpty() &&
                firebaseAuth.currentUser!!.displayName!!.isNotEmpty()
            ) {
                binding.usernameTxt.text = firebaseAuth.currentUser!!.displayName
                binding.emailTxt.text = firebaseAuth.currentUser!!.email
            } else {
                binding.userImageView.applyImage(it.imageSrc!!)
                binding.usernameTxt.text = it.name
                binding.emailTxt.text = it.email
                if (it.about!!.isNotEmpty()) {
                    binding.userAboutTextView.text = it.about
                } else {
                    binding.userAboutTextView.noText()
                }
            }
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
            if (requestPermission<ProfileFragment>(it)) {
                selectImage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}