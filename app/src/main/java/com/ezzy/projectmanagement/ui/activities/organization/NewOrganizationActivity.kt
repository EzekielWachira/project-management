package com.ezzy.projectmanagement.ui.activities.organization

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewOrganizationBinding
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.ezzy.projectmanagement.ui.dialogs.AddMembersToOrgDialog
import com.ezzy.projectmanagement.util.*
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val TAG = "NewOrganizationActivity"
@AndroidEntryPoint
class NewOrganizationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewOrganizationBinding
    private var picImageUri : Uri? = null
    val orgViewModel : OrganizationViewModel by viewModels()
    private var members = mutableSetOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "New organization"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.orgImage.setOnClickListener {
            requestPermissions()
        }

        binding.btnAddMembers.setOnClickListener {
            AddMembersToOrgDialog().show(
                supportFragmentManager, "ADD_MEMBERS"
            )
        }

        orgViewModel.isImageUploaded.observe(this, Observer {
            if (it) {
                makeToast("Image uploaded successfully")
            } else {
                makeToast("Couldn't upload image")
            }
        })

        orgViewModel.members.observe(this, {
            it.forEach { user ->
                members.add(user)
                val chip = LayoutInflater.from(this).inflate(
                    R.layout.members_chip_item, null, false
                ) as Chip
                chip.apply {
                    text = user.name
                    setOnCloseIconClickListener { memberChip ->
                        members.remove(user)
                        binding.membersChipGrp.removeView(memberChip)
                    }
                }
                binding.membersChipGrp.addView(chip)
            }
        })

        orgViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                makeToast("organization added successfully")
            } else {
                makeToast("Error saving organization")
            }
        }

    }

    private fun increaseImageSize() {
        val layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        binding.orgImage.layoutParams = layoutParams
    }

    private fun selectImage() {
        selectPicture<NewOrganizationActivity>(this)
//        val options = arrayOf(
//            TAKE_PHOTO, PICK_FROM_GALLERY, CANCEL
//        )
//        val builder = AlertDialog.Builder(this)
//        builder.apply {
//            title = CHOOSE_IMAGE
//            setItems(options) { dialog, which ->
//                when (options[which]) {
//                    TAKE_PHOTO -> {
//                        Intent(
//                            MediaStore.ACTION_IMAGE_CAPTURE
//                        ).apply {
//                            startActivityIfNeeded(this, TAKE_IMAGE_REQUEST_CODE)
//                        }
//                    }
//                    PICK_FROM_GALLERY -> {
//                        Intent(
//                            Intent.ACTION_GET_CONTENT
//                        ).apply {
//                            type = "image/*"
//                            startActivityIfNeeded(this, PICK_PHOTO_REQUEST_CODE)
//                        }
//                    }
//                    CANCEL -> {
//                        dialog?.dismiss()
//                    }
//                }
//            }
//            show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        picImageUri = imageResult<NewOrganizationActivity>(
            requestCode, resultCode, data, this,
            binding.orgImage
        )
//        if (resultCode != RESULT_CANCELED){
//            when(requestCode){
//                TAKE_IMAGE_REQUEST_CODE -> {
//                    data?.let {
//                        if (resultCode == RESULT_OK) {
//                            val bitMap = data.extras?.get("data") as Bitmap
//                            binding.orgImage.setImageBitmap(bitMap)
//                            picImageUri = bitMap.convertToUri(this, bitMap)
//                            Timber.i("IMAGE URI: $picImageUri")
//                            Timber.d("PHOTO: $picImageUri")
//                        }
//                    }
//                }
//                PICK_PHOTO_REQUEST_CODE -> {
//                    data?.data?.let { it ->
//                        if (resultCode == RESULT_OK) {
//                            val imageUri : Uri = it
//                            imageUri.let { uri ->
//                                binding.orgImage.setImageURI(uri)
//                                picImageUri = uri
//                                Timber.i("IMAGE URI: $uri")
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun requestPermissions() {
        if (requestPermission<NewOrganizationActivity>(this)) {
            selectImage()
        }
//        val permissions = arrayOf(
//            permission.READ_EXTERNAL_STORAGE,
//            permission.WRITE_EXTERNAL_STORAGE,
//            permission.CAMERA
//        )
//        if (ContextCompat.checkSelfPermission(
//                applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, permissions[2]) == PackageManager.PERMISSION_GRANTED
//        ) {
//            selectImage()
//        } else {
//            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.new_project_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSave -> {
                picImageUri?.let { imageUri ->
                    val organization = Organization(
                        binding.orgName.text.toString(),
                        null,
                        binding.orgAbout.text.toString()
                    )
                    orgViewModel.addOrg(
                        organization,
                        members,
                        imageUri.getNameFromUri(this, imageUri),
                        imageUri
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}