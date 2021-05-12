package com.ezzy.projectmanagement.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ezzy.core.domain.Activity
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.util.*
import de.hdodenhof.circleimageview.CircleImageView

class ActivityViewHolder(
    val context: Context,
    parent: ViewGroup
) : CommonViewHolder<Activity>(parent, R.layout.activity_item, ){

    private val creatorImageView: CircleImageView = rootView.findViewById(R.id.creatorImage)
    private val activityTitle: TextView = rootView.findViewById(R.id.activityTitle)
    private val activityDate : TextView = rootView.findViewById(R.id.activityDate)
    private val activityContent: TextView = rootView.findViewById(R.id.activityContent)

    override fun bindItem(item: Activity?) {
        item?.let {
            creatorImageView.applyImage(it.creatorImage!!)
            activityTitle.text = it.activityTitle
            activityDate.text = it.creation_date?.formatTimeToDate()
            if (it.content!!.isNotEmpty()){
                activityContent.text = it.content
                activityContent.visible()
            } else activityContent.gone()
        }
    }
}