package com.ezzy.projectmanagement.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.model.Project

class AllProjectsViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<Project>(
    parent, R.layout.project_item_layout
){

    private val projectNameTextView : TextView = rootView.findViewById(R.id.projectNameTextView)
    private val projectAboutTextView : TextView = rootView.findViewById(R.id.projectAboutTextView)
    private val startDateTextView : TextView = rootView.findViewById(R.id.startDateTextView)
    private val endDateTextView : TextView = rootView.findViewById(R.id.endDateTextView)


    override fun bindItem(item: Project?) {
        TODO("Not yet implemented")
    }
}