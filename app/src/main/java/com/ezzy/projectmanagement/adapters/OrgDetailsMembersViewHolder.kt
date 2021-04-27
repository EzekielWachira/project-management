package com.ezzy.projectmanagement.adapters

import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.util.smartTruncate
import com.google.android.material.chip.Chip

class OrgDetailsMembersViewHolder(
    parent: ViewGroup
) : CommonViewHolder<User>(
    parent, R.layout.members_item
){

    private val memberNameTextView : TextView = rootView.findViewById(R.id.memberItemName)
    private val membersEmailTextView : TextView = rootView.findViewById(R.id.memberLabel)

    override fun bindItem(item: User?) {
        item?.let {
            memberNameTextView.text = it.name?.smartTruncate(10)
            membersEmailTextView.text = it.email?.smartTruncate(10)
        }
    }


}