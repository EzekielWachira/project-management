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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.ui.activities.organization.NewOrganizationActivity
import com.ezzy.projectmanagement.ui.fragments.profile.ProfileViewModel
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.ezzy.projectmanagement.util.convertToUri
import com.ezzy.projectmanagement.util.imageResult
import com.ezzy.projectmanagement.util.requestPermission
import com.ezzy.projectmanagement.util.selectPicture
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

        userImageView.setOnClickListener { requestPermissions() }

        nameEditText.setText(firebaseAuth.currentUser?.displayName)
        emailEditText.setText(firebaseAuth.currentUser?.email)

        btnDone.setOnClickListener {
            val user = User(
                nameEditText.text.toString(),
                emailEditText.text.toString(),
                aboutEditText.text.toString(),
                ""
            )
            profileViewModel.updateAuthUser(user)
            dialog?.dismiss()
            showDialog()
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

//        if (resultCode != RESULT_CANCELED){
//            when(requestCode){
//                TAKE_IMAGE_REQUEST_CODE -> {
//                    data?.let {
//                        if (resultCode == RESULT_OK) {
//                            val bitMap = data.extras?.get("data") as Bitmap
//                            userImageView.setImageBitmap(bitMap)
//                            picImageUri = activity?.let { it1 ->
//                                bitMap.convertToUri(it1.applicationContext, bitMap)
//                            }
//                            Timber.i("IMAGE URI: $picImageUri")
//                            Timber.d("PHOTO: $picImageUri")
//                        }
//                    }
//                }
//                PICK_PHOTO_REQUEST_CODE -> {
//                    data?.data?.let {
//                        if (resultCode == RESULT_OK) {
//                            val imageUri : Uri = it
//                            imageUri.let { uri ->
//                                userImageView.setImageURI(uri)
//                                picImageUri = uri
//                                Timber.i("IMAGE URI: $uri")
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissions()
    }

    private fun updateUser(user: User) {
        profileViewModel.updateAuthUser(user)
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



}