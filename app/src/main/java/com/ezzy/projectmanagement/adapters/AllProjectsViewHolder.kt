package com.ezzy.projectmanagement.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.projectmanagement.R
import com.ezzy.core.domain.Project

class AllProjectsViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<Project>(
    parent, R.layout.project_item_layout
){

    private val projectTitleTextView : TextView = rootView.findViewById(R.id.projectNameTextView)
    private val startDateTextView : TextView = rootView.findViewById(R.id.startDateTextView)
    private val endDateTextView : TextView = rootView.findViewById(R.id.endDateTextView)


    override fun bindItem(item: Project?) {
        item?.let {
            projectTitleTextView.text = it.projectTitle
            startDateTextView.text = it.startDate
            endDateTextView.text = it.endDate
        }
    }
}