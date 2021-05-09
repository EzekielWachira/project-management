package com.ezzy.projectmanagement.ui.dialogs

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.ui.activities.organization.NewOrganizationActivity
import com.ezzy.projectmanagement.ui.fragments.profile.ProfileViewModel
import com.ezzy.projectmanagement.util.*
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfileDialog : DialogFragment() {

    private lateinit var nameEditText : TextInputEditText
    private lateinit var emailEditText : TextInputEditText
    private lateinit var aboutEditText : TextInputEditText
    private lateinit var btnDone : Button
    private lateinit var userImageView : ImageView

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val profileViewModel : ProfileViewModel by viewModels()

    private var picImageUri : Uri? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(R.layout.update_user_profile, null)

        view?.let {
            nameEditText = view.findViewById(R.id.usernameEditText)
            emailEditText = view.findViewById(R.id.userEmailEditText)
            aboutEditText = view.findViewById(R.id.userAboutEditText)
            btnDone = view.findViewById(R.id.btnSave)
            userImageView = view.findViewById(R.id.userImgView)
        }

//        profileViewModel.getUserInfo()

        userImageView.setOnClickListener { requestPermissions() }

        nameEditText.setText(firebaseAuth.currentUser?.displayName)
        emailEditText.setText(firebaseAuth.currentUser?.email)

        btnDone.setOnClickListener { _->
            picImageUri?.let { imageUri ->
                activity?.let {
                    val user = User(
                        nameEditText.text.toString(),
                        emailEditText.text.toString(),
                        aboutEditText.text.toString()
                    )
                    profileViewModel.saveUserImg(imageUri, imageUri.getNameFromUri(
                        it.applicationContext, imageUri
                    ), user)
                }
            }
            dialog?.dismiss()
            showDialog()
        }

        profileViewModel.user.observe(this) {
            aboutEditText.setText(it.about)
            userImageView.applyImage(it.imageSrc!!)
        }

        profileViewModel.isUserUpdateSuccess.observe(this) {
            isSuccess ->
                if (isSuccess) {
                    dialog?.dismiss()
                    makeToast("User updated")
                }
                else { showDialog() }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun requestPermissions() {
        activity?.let {
            if (requestPermission<UpdateProfileDialog>(it)){
                selectImage()
            }
        }
    }

    private fun selectImage() {
        activity?.let { selectPicture<UpdateProfileDialog>(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activity?.let {
            picImageUri = imageResult<UpdateProfileDialog>(
                requestCode, resultCode, data, it,
                userImageView
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

    private fun showDialog() {
        val dialog = ProgressDialog(activity)
        dialog.apply {
            setTitle("Saving")
            setMessage("Saving your data...")
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        profileViewModel.getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getUserInfo()
    }


}