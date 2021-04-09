package com.ezzy.projectmanagement.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class CommonViewHolder<in T>(
    parent: ViewGroup,
    layoutResource: Int,
    val rootView: View = LayoutInflater.from(parent.context)
        .inflate(layoutResource, parent, false)
) : RecyclerView.ViewHolder(rootView){
    abstract fun bindItem(item : T?)
}

class CommonRecyclerViewAdapter<T>(
    private val viewHolderFactory : ((parent : ViewGroup) -> CommonViewHolder<T>),
) : RecyclerView.Adapter<CommonViewHolder<T>>() {

    private var onItemClickListener : ((T?) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun setOnClickListener(listener : (T?) -> Unit){
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<T> {
        return viewHolderFactory(parent)
    }

    override fun onBindViewHolder(holder: CommonViewHolder<T>, position: Int) {
        val item = differ.currentList[position]
//        holder.bindItem(item)
        holder.apply {
            bindItem(item)
            rootView.setOnClickListener {
                onItemClickListener?.let {
                    it(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}