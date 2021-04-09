package com.ezzy.projectmanagement.adapters.viewpager

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonViewHolder
import com.ezzy.projectmanagement.model.Organization

class OrganizationViewHolder(
    val context: Context,
    parent: ViewGroup,
)  : CommonViewHolder<Organization>(
    parent, R.layout.org_item_layout
){

    private val orgNameTextView: TextView = rootView.findViewById(R.id.orgName)
    private val orgImageView : ImageView = rootView.findViewById(R.id.orgImageView)

    override fun bindItem(item: Organization?) {
        item?.let {
            orgNameTextView.text = it.name
            Glide.with(context.applicationContext)
                .load(item.imageSrc)
                .placeholder(ContextCompat.getDrawable(
                    context.applicationContext,
                    R.drawable.placeholder
                ))
                .into(orgImageView)
        }
    }
}
