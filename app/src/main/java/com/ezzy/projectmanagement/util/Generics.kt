package com.ezzy.projectmanagement.util

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ezzy.core.domain.Action
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.Constants.ACTIVITY
import com.ezzy.projectmanagement.util.Constants.CANCEL
import com.ezzy.projectmanagement.util.Constants.PICK_FROM_GALLERY
import com.ezzy.projectmanagement.util.Constants.PICK_PHOTO_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.REQUEST_PERMISSION_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_IMAGE_REQUEST_CODE
import com.ezzy.projectmanagement.util.Constants.TAKE_PHOTO
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import com.ezzy.core.domain.Activity as UserActivity

fun <T> selectPicture(activity: Activity) {
    val options = arrayOf( TAKE_PHOTO, PICK_FROM_GALLERY, CANCEL )
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

fun <T> requestPermission(activity: Activity): Boolean {
    val isPermissionsGranted: Boolean
    val permissions = arrayOf(
        permission.READ_EXTERNAL_STORAGE,
        permission.WRITE_EXTERNAL_STORAGE,
        permission.CAMERA
    )
    if (ContextCompat.checkSelfPermission(
            activity.applicationContext, permissions[0]
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            activity,
            permissions[1]
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            activity,
            permissions[2]
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        isPermissionsGranted = true
    } else {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CODE)
        isPermissionsGranted = false
    }
    return isPermissionsGranted
}


fun <T> imageResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    activity: Activity,
    imageView: ImageView
): Uri? {
    var picImageUri: Uri? = null
    if (resultCode != RESULT_CANCELED) {
        when (requestCode) {
            TAKE_IMAGE_REQUEST_CODE -> {
                data?.let {
                    if (resultCode == RESULT_OK) {
                        val bitMap = data.extras?.get("data") as Bitmap
                        imageView.setImageBitmap(bitMap)
                        picImageUri = bitMap.convertToUri(activity)
                        Timber.d("PHOTO: $picImageUri")
                    }
                }
            }
            PICK_PHOTO_REQUEST_CODE -> {
                data?.data?.let {
                    if (resultCode == RESULT_OK) {
                        val imageUri: Uri = it
                        imageUri.let { uri ->
                            imageView.setImageURI(uri)
                            picImageUri = uri
                        }
                    }
                }
            }
        }
    }
    return picImageUri
}

suspend fun <T> addActivity(
    fireStore: FirebaseFirestore,
    activity: UserActivity,
    action: Action,
    type: String?,
    status: String?,
    organizationName: String?,
    projectName: String?
): Boolean {
    var isActivityAddedSuccess = false
    val activityCollection = fireStore.collection(ACTIVITY)
    try {
        when (action) {
            Action.ADDED_ISSUE -> {
                activity.activityTitle = addedIssue(
                    activity.creatorName!!, projectName!!
                )
            }
            Action.ADDED_TASK -> {
                activity.activityTitle = addedTask(
                    activity.creatorName!!, projectName!!
                )
            }
            Action.COMMENTED -> {
                activity.activityTitle = commented(
                    activity.creatorName!!, type!!
                )
            }
            Action.CREATED_PROJECT -> {
                activity.activityTitle = createdProject(
                    activity.creatorName!!, projectName!!
                )
            }
            Action.REPORTED_BUG -> {
                activity.activityTitle = reportedBug(
                    activity.creatorName!!, projectName!!
                )
            }
            Action.SET_STATUS -> {
                activity.activityTitle = setProjectStatus(
                    activity.creatorName!!, projectName!!, status!!
                )
            }
            Action.UPDATED -> updated(activity.creatorName!!, projectName!!)
            Action.CREATED_ORGANIZATION -> {
                activity.activityTitle = createdOrganization(
                    activity.creatorName!!, organizationName!!
                )
            }
        }
        activityCollection.add(activity).addOnSuccessListener {
            isActivityAddedSuccess = true
            Timber.i("Activity added")
        }.addOnFailureListener {
            isActivityAddedSuccess = false
            Timber.e("cannot add activity: ${it.message.toString()}")
        }.apply { await() }
    } catch (e: Exception) {
        isActivityAddedSuccess = false
        Timber.e("activity exception ${e.message.toString()}")
    }
    return isActivityAddedSuccess
}

suspend fun <T> saveActivity(
    firebaseAuth: FirebaseAuth,
    fireStore: FirebaseFirestore,
    content: String?
): UserActivity {
    var activity: UserActivity? = null
    var creatorImage: String? = null
    var creatorName: String? = null
    val creationDate: Long = System.currentTimeMillis()
    val userCollection = fireStore.collection(USERS)
    userCollection.whereEqualTo("email", firebaseAuth.currentUser!!.email)
        .get().addOnSuccessListener {
            it.documents.forEach { documentSnapshot ->
                creatorImage = documentSnapshot.getString("imageSrc")
                creatorName = documentSnapshot.getString("name")
            }
        }.addOnFailureListener { Timber.e("error getting user image") }
        .apply { await() }
    activity = UserActivity(
        null, content,
        creationDate, creatorName, creatorImage
    )
    return activity
}