package com.ezzy.projectmanagement.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.Flow

private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

fun String.smartTruncate(length : Int) : String {
    val words = split("")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
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

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.showSnackBar(message : String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
}

fun ImageView.applyImage(imageUrl : String) {
    Glide.with(context)
        .load(imageUrl)
        .placeholder(
            ContextCompat.getDrawable(
                context, R.drawable.placeholder
            )
        )
        .into(this)
}

fun CircleImageView.applyImage(imageUrl: String) {
    Glide.with(context)
        .load(imageUrl)
        .placeholder(
            ContextCompat.getDrawable(
                context, R.drawable.placeholder
            )
        )
        .into(this)
}

fun TextView.noText() {
    this.apply {
        text = context.getString(R.string.no_about)
    }
}

fun String.createdProject(userName : String, projectName : String) : String{
    return "$userName created $projectName"
}

fun String.commented(userName: String, type : String) : String {
    return "$userName commented on $type"
}

fun String.reportedBug(userName: String, projectName: String) : String {
    return "$userName reported bug on $projectName"
}

fun String.addedIssue(userName: String, projectName: String) : String {
    return "$userName reported issue on $projectName"
}

fun String.updated(userName: String, projectName: String) : String {
    return "$userName updated $projectName"
}

fun String.addedTask(userName: String, projectName: String) : String {
    return "$userName added task to $projectName"
}

fun String.setProjectStatus(userName: String, projectName: String, status : String): String {
    return "$userName set project $projectName as $status"
}