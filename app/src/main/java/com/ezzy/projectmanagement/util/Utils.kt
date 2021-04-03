package com.ezzy.projectmanagement.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

fun Bitmap.convertToUri(context: Context, bitmap: Bitmap) : Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path : String = MediaStore.Images.Media.insertImage(
        context.contentResolver, bitmap, "photo", null
    )
    return Uri.parse(path)
}