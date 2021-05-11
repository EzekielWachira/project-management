package com.ezzy.projectmanagement.data.remote

import com.ezzy.core.data.ActivityDataSource
import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Activity
import com.ezzy.projectmanagement.util.*
import com.ezzy.projectmanagement.util.Constants.ACTIVITY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ActivityDataSourceImpl @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val firestore: FirebaseFirestore
) : ActivityDataSource {

    private val activityCollection = firestore.collection(ACTIVITY)

    override suspend fun addActivity(
        activity: Activity,
        action: Action,
        type: String?,
        status: String?
    ) {
        try {
            when (action) {
                Action.ADDED_ISSUE -> {
                    activity.activityTitle = "".addedIssue(
                        activity.creatorName!!, activity.projectTitle!!
                    )
                }
                Action.ADDED_TASK -> {
                    activity.activityTitle = "".addedTask(
                        activity.creatorName!!, activity.projectTitle!!
                    )
                }
                Action.COMMENTED -> {
                    activity.activityTitle = "".commented(
                        activity.creatorName!!, type!!
                    )
                }
                Action.CREATED_PROJECT -> {
                    activity.activityTitle = "".createdProject(
                        activity.creatorName!!, activity.projectTitle!!
                    )
                }
                Action.REPORTED_BUG -> {
                    activity.activityTitle = "".reportedBug(
                        activity.creatorName!!, activity.projectTitle!!
                    )
                }
                Action.SET_STATUS -> {
                    activity.activityTitle = "".setProjectStatus(
                        activity.creatorName!!, activity.projectTitle!!, status!!
                    )
                }
                Action.UPDATED -> "".updated(activity.projectTitle!!, activity.projectTitle!!)
            }
            activityCollection.add(activity).addOnSuccessListener {
                Timber.i("Activity added")
            }.addOnFailureListener {
                Timber.e("cannot add activity: ${it.message.toString()}")
            }.apply { await() }
        } catch (e: Exception) {
            Timber.e("activity exception ${e.message.toString()}")
        }
    }

    override suspend fun getActivities(): List<Activity> {
        val activities = mutableListOf<Activity>()
        try {
            activityCollection.get().addOnSuccessListener {
                it.documents.forEach { documentSnapshot ->
                    val activity = documentSnapshot.toObject(Activity::class.java)
                    activities.add(activity!!)
                }
            }.addOnFailureListener {
                Timber.e("Error getting activities: ${it.message.toString()}")
            }.apply { await() }
        } catch (e : Exception) {
            Timber.e("Exception retrieving activities: ${e.message.toString()}")
        }
        return activities
    }

}