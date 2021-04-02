package com.ezzy.projectmanagement.ui.activities.organization

import android.Manifest.permission
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrganizationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewOrganizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.orgImage.setOnClickListener {
//            selectImage()
            requestPermissions()
        }

    }

    private fun selectImage() {
        val options = arrayOf(
            TAKE_PHOTO, PICK_FROM_GALLERY, CANCEL
        )
        val builder = AlertDialog.Builder(this)
        builder.apply {
            title = CHOOSE_IMAGE
            setItems(options, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(options[which]){
                        TAKE_PHOTO -> {
                            val takePictureIntent = Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE
                            )
                            startActivityIfNeeded(takePictureIntent, TAKE_IMAGE_REQUEST_CODE)
                        }
                        PICK_FROM_GALLERY -> {
                            val photoIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityIfNeeded(photoIntent, PICK_PHOTO_REQUEST_CODE)
                        }
                        CANCEL -> {
                            dialog?.dismiss()
                        }
                    }
//                    if (options[which] == TAKE_PHOTO) {
//                        val takePictureIntent = Intent(
//                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE
//                        )
//                        startActivityIfNeeded(takePictureIntent, TAKE_IMAGE_REQUEST_CODE)
//                    } else if(options[which] == PICK_FROM_GALLERY) {
//                        val photoIntent = Intent(
//                            Intent.ACTION_PICK,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                        )
//                        startActivityIfNeeded(photoIntent, PICK_PHOTO_REQUEST_CODE)
//                    } else if (options[which] == CANCEL) {
//                        dialog?.dismiss()
//                    }
                }
            })
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
                        }
                    }
                }
                PICK_PHOTO_REQUEST_CODE -> {
                    data?.let {
                        if (resultCode == RESULT_OK) {
                            val imageUri : Uri? = data.data
                            val filePathColumn = arrayOf(
                                MediaStore.Images.Media.DATA
                            )
                            imageUri?.let { uri ->
                                val cursor = contentResolver.query(
                                    uri, filePathColumn, null, null, null
                                )
                                cursor?.let {
                                    cursor.moveToFirst()
                                    val columnIndex = it.getColumnIndex(filePathColumn[0])
                                    val imagePath = it.getString(columnIndex)
                                    binding.orgImage.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                                    it.close()
                                }
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

//    val dialogInterface = DialogInterface.OnClickListener { dialog, which ->
//        TODO("Not yet implemented")
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.top_menu, menu)
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