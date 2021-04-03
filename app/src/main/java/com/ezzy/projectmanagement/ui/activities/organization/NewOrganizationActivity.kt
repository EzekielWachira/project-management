package com.ezzy.projectmanagement.ui.activities.organization

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewOrganizationBinding
import com.ezzy.projectmanagement.util.Constants.CANCEL
import com.ezzy.projectmanagement.util.Constants.CHOOSE_IMAGE
import com.ezzy.projectmanagement.util.Constants.PICK_FROM_GALLERY
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.REQUEST_PERMISSION_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_PHOTO
import com.ezzy.projectmanagement.util.convertToUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrganizationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewOrganizationBinding
    private var picImageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Create new organization"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.orgImage.setOnClickListener {
            requestPermissions()
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
        val options = arrayOf(
            TAKE_PHOTO, PICK_FROM_GALLERY, CANCEL
        )
        val builder = AlertDialog.Builder(this)
        builder.apply {
            title = CHOOSE_IMAGE
            setItems(options) { dialog, which ->
                when (options[which]) {
                    TAKE_PHOTO -> {
                        val takePictureIntent = Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE
                        )
                        startActivityIfNeeded(takePictureIntent, TAKE_IMAGE_REQUEST_CODE)
                    }
                    PICK_FROM_GALLERY -> {
                        Intent(
                            Intent.ACTION_GET_CONTENT
                        ).apply {
                            type = "image/*"
                            startActivityIfNeeded(this, PICK_PHOTO_REQUEST_CODE)
                        }
                    }
                    CANCEL -> {
                        dialog?.dismiss()
                    }
                }
            }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED){
            when(requestCode){
                TAKE_IMAGE_REQUEST_CODE -> {
                    data?.let {
                        if (resultCode == RESULT_OK) {
                            val bitMap = data.extras?.get("data") as Bitmap
                            binding.orgImage.setImageBitmap(bitMap)
                            picImageUri = bitMap.convertToUri(this, bitMap)
                        }
                    }
                }
                PICK_PHOTO_REQUEST_CODE -> {
                    data?.data?.let { it ->
                        if (resultCode == RESULT_OK) {
                            val imageUri : Uri = it
                            imageUri.let { uri ->
                                binding.orgImage.setImageURI(uri)
                                picImageUri = uri
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.CAMERA
        )
        if (ContextCompat.checkSelfPermission(
                applicationContext, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, permissions[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            selectImage()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.new_project_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSave -> {
                makeToast("Action save")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}