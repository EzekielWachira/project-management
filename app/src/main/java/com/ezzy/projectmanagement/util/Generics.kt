package com.ezzy.projectmanagement.util

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import com.ezzy.projectmanagement.util.Constants.CANCEL
import com.ezzy.projectmanagement.util.Constants.PICK_FROM_GALLERY
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_PHOTO

fun<T> selectPicture(activity: Activity) {
    val options = arrayOf(
        TAKE_PHOTO, PICK_FROM_GALLERY, CANCEL
    )
    val builder = AlertDialog.Builder(activity)
    builder.apply {
        setTitle(Constants.CHOOSE_IMAGE)
        setItems(options) { dialog, which ->
            when (options[which]) {
                TAKE_PHOTO -> {
                    Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                    ).apply {
                        activity.startActivityIfNeeded(this, TAKE_IMAGE_REQUEST_CODE)
                    }
                }
                PICK_FROM_GALLERY -> {
                    Intent(
                        Intent.ACTION_GET_CONTENT
                    ).apply {
                        type = "image/*"
                        activity.startActivityIfNeeded(this, PICK_PHOTO_REQUEST_CODE)
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