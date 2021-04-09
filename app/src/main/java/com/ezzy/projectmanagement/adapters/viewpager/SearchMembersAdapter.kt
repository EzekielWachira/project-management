package com.ezzy.projectmanagement.adapters.viewpager

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonViewHolder
import com.ezzy.projectmanagement.model.User

class SearchMemberViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<User>(
    parent, R.layout.search_member_item
) {

    private val userNameTextView : TextView = rootView.findViewById(R.id.memberName)
    private val userEmailTextView : TextView = rootView.findViewById(R.id.memberEmail)

    override fun bindItem(item: User?) {
        item?.let { user ->
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
        }
    }
}