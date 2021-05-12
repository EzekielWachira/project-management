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
        status: String?,
        organizationName: String?,
        projectName: String?
    ): Boolean {
        var isActivityAddedSuccess = false
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
        } catch (e: Exception) {
            Timber.e("Exception retrieving activities: ${e.message.toString()}")
        }
        return activities
    }

}