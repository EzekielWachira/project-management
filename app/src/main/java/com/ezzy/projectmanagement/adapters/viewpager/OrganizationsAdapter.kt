package com.ezzy.projectmanagement.adapters.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.adapters.CommonViewHolder

class OrganizationsAdapter : RecyclerView.Adapter<OrganizationsAdapter.ViewHolder>(){

    private var onItemClickListener : ((Organization) -> Unit)? = null

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Organization>(){
        override fun areItemsTheSame(oldItem: Organization, newItem: Organization): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Organization, newItem: Organization): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun setOnClickListener(listener : (Organization) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.org_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val organization = differ.currentList[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.orgName).text = organization.name
            val imageView = findViewById<ImageView>(R.id.orgImageView)
            Glide.with(context)
                .load(organization.imageSrc)
                .placeholder(
                    ContextCompat.getDrawable(
                        context.applicationContext,
                        R.drawable.placeholder
                    )
                )
                .into(imageView)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}


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
