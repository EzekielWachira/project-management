package com.ezzy.projectmanagement.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.util.*

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

fun String.smartTruncate(length : Int) : String {
    val words = split("")
    var added = 0
    var hasMore = false
    var builder = StringBuilder()
    for (word in words) {
        if (builder.length > length){
            hasMore = true
            break
        }
        builder.append(word)
        builder.append("")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)){
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if(hasMore){
        builder.append("...")
    }

    return builder.toString()
}


fun Bitmap.convertToUri(context: Context, bitmap: Bitmap) : Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path : String = MediaStore.Images.Media.insertImage(
        context.contentResolver, bitmap, "photo", null
    )
    return Uri.parse(path)
}

fun Uri.getNameFromUri(context: Context, uri: Uri) : String {
    var name : String? = null
    if (uri.scheme.equals("content")) {
        val cursor: Cursor? = context.contentResolver.query(
            uri, null, null, null, null
        )
        try {
            cursor?.let {
                if (it.moveToFirst()){
                    name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (name == null) {
        name = uri.path
        val cut = name?.lastIndexOf("/")
        if (cut != -1) {
            name = name?.substring(cut!!.plus(1))
        }
    }
    return name.toString()
}

fun String.makeLowerCase(s : String) : String{
    return s.toLowerCase(Locale.getDefault())
}