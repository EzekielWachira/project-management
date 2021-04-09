package com.ezzy.projectmanagement.adapters.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonViewHolder
import com.ezzy.projectmanagement.model.User

class SearchMembersAdapter : RecyclerView.Adapter<SearchMembersAdapter.ViewHolder>(){

    private var onItemClickListener : ((User) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun setOnClickListener(listener : (User) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.search_member_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.memberName).text = user.name
            findViewById<TextView>(R.id.memberEmail).text = user.email
            setOnClickListener {
                onItemClickListener?.let {
                    it(user)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

class SearchMemberAdapter(
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