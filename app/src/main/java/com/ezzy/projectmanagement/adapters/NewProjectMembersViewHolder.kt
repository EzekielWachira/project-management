package com.ezzy.projectmanagement.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.model.User

class NewProjectMembersViewHolder(
    context: Context,
    parent: ViewGroup
) : CommonViewHolder<User>(
    parent, R.layout.members_item
){

    private val memberNameTextView : TextView = rootView.findViewById(R.id.memberItemName)

    override fun bindItem(item: User?) {
        item?.let {
            memberNameTextView.text = it.name
        }
    }
}