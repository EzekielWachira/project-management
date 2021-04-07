package com.ezzy.projectmanagement.adapters.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.model.Organization
import de.hdodenhof.circleimageview.CircleImageView

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
                .inflate(R.layout.organization_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val organization = differ.currentList[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.organizationName).text = organization.name
            val imageView = findViewById<CircleImageView>(R.id.organizationImage)
            Glide.with(context)
                .load(organization.imageSrc)
                .into(imageView)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}