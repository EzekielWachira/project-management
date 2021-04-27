package com.ezzy.projectmanagement.adapters

import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.core.domain.Project
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.util.smartTruncate
import com.google.android.material.chip.Chip

class OrgDetailsProjectViewHolder(
    parent: ViewGroup
) : CommonViewHolder<Project>(
    parent, R.layout.project_item
){

    private val projectNameTextView : TextView = rootView.findViewById(R.id.projectItemName)
    private val projectStatusChip : Chip = rootView.findViewById(R.id.projectStatusChip)

    override fun bindItem(item: Project?) {
        item?.let {
            projectNameTextView.text = it.projectTitle?.smartTruncate(10)
            projectStatusChip.text = it.projectStage?.smartTruncate(10)
        }
    }


}