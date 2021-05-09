package com.ezzy.projectmanagement.adapters.viewpager

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonViewHolder
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.util.applyImage
import de.hdodenhof.circleimageview.CircleImageView

class SearchMemberViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<User>(
    parent, R.layout.search_member_item
) {

    private val userNameTextView : TextView = rootView.findViewById(R.id.memberName)
    private val userEmailTextView : TextView = rootView.findViewById(R.id.memberEmail)
    private val userImage : CircleImageView = rootView.findViewById(R.id.memberImg)

    override fun bindItem(item: User?) {
        item?.let { user ->
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
            user.imageSrc?.let {
                userImage.applyImage(it)
            }
        }
    }
}

class SearchOrgViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<Organization>(
    parent, R.layout.organization_item
) {

    val orgImage : CircleImageView = rootView.findViewById(R.id.organizationImage)
    val orgName : TextView = rootView.findViewById(R.id.organizationName)

    override fun bindItem(item: Organization?) {
        item?.let {
            orgName.text = it.name
            Glide.with(context)
                .load(it.imageSrc)
                .placeholder(ContextCompat.getDrawable(
                    context, R.drawable.placeholder
                )).into(orgImage)
        }
    }
}