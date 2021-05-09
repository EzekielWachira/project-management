package com.ezzy.projectmanagement.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.projectmanagement.R
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.applyImage
import de.hdodenhof.circleimageview.CircleImageView

class NewProjectMembersViewHolder(
    context: Context,
    parent: ViewGroup
) : CommonViewHolder<User>(
    parent, R.layout.members_item
){

    private val memberNameTextView : TextView = rootView.findViewById(R.id.memberItemName)
    private val memberImage : CircleImageView = rootView.findViewById(R.id.memberImageView)

    override fun bindItem(item: User?) {
        item?.let {
            memberNameTextView.text = it.name
            it.imageSrc?.let { imageSrc ->
                memberImage.applyImage(imageSrc)
            }
        }
    }
}