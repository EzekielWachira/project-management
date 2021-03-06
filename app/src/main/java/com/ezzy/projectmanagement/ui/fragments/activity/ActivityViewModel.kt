package com.ezzy.projectmanagement.ui.fragments.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezzy.core.domain.Action
import com.ezzy.core.domain.Activity
import com.ezzy.core.interactors.ActivityUseCase
import com.ezzy.core.interactors.GetActivities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    val getAllActivities: GetActivities,
    val addActivity: ActivityUseCase
) : ViewModel() {

    private var _activities = MutableLiveData<List<Activity>>()
    val activities: LiveData<List<Activity>> get() = _activities
    private var _isActivityAddedSuccess = MutableLiveData<Boolean>()
    val isActivityAddedSuccess: LiveData<Boolean> get() = _isActivityAddedSuccess
    private var _areActivitiesLoading = MutableLiveData<Boolean>()
    val areActivitiesLoading: LiveData<Boolean> get() = _areActivitiesLoading

    fun getActivities() = viewModelScope.launch {
        _areActivitiesLoading.postValue(true)
        val results = getAllActivities()
        if (results.isNotEmpty()) {
            _activities.postValue(results)
            _areActivitiesLoading.postValue(false)
        }
    }

    fun addNewActivity(
        activity: Activity,
        action: Action,
        type: String?,
        status: String?,
        organizationName: String?,
        projectName: String?
    ) {
        viewModelScope.launch {
            val results = addActivity(
                activity, action, type, status, organizationName, projectName
            )
            if (results) {
                _isActivityAddedSuccess.postValue(true)
            } else _isActivityAddedSuccess.postValue(false)
        }
    }

}