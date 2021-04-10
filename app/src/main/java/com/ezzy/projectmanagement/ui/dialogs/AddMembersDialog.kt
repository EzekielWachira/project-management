package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.adapters.viewpager.SearchMemberViewHolder
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.ui.activities.newproject.NewProjectActivity
import com.ezzy.projectmanagement.ui.dialogs.viewmodel.DialogViewModel
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddMembersDialog : DialogFragment() {

    private lateinit var doneButton : Button
    private lateinit var searchEditText : TextInputEditText
    private lateinit var peopleRecyclerview: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var membersChipGroup : ChipGroup
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var membersAdapter : CommonRecyclerViewAdapter<User>
    private val dialogViewModel : DialogViewModel by viewModels()
    private var members = listOf<User?>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(
            R.layout.layout_add_members_dialog, null
        )

        view?.let {
            doneButton = view.findViewById(R.id.buttonDone)!!
            searchEditText = view.findViewById(R.id.searchPeopleEditText)
            peopleRecyclerview = view.findViewById(R.id.peopleRecyclerview)
            progressBar = view.findViewById(R.id.searchPpleprogressBar)
            membersChipGroup = view.findViewById(R.id.membersChipGroup)
        }

        setUpRecyclerView()

        dialogViewModel.getAllMembers()

        membersAdapter.setOnClickListener { user ->
            Timber.d("THE SUSER: $user")
            user?.let {
                if ((activity as NewProjectActivity).members.contains(it) && members.contains(it)){
                    return@setOnClickListener
                } else {
                    (activity as NewProjectActivity).addMembers(it)
                    dialogViewModel.addMembers(it)
                }
            }
        }


        doneButton.setOnClickListener {
            dialog?.dismiss()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String = searchEditText.text.toString()
                Timber.d("SEARCH QUERY: =>> $searchText")
                dialogViewModel.searchMembers(searchText.toLowerCase(Locale.getDefault()))
            }

            override fun afterTextChanged(s: Editable?) {
                Timber.d("SEARCH QUERY ss: =>> ${s.toString().toLowerCase(Locale.getDefault())}")
                dialogViewModel.searchMembers(s.toString().toLowerCase(Locale.getDefault()))
            }

        })

        dialogViewModel.allMembers.observe(this, { users ->
            membersAdapter.differ.submitList(users)
        })

        dialogViewModel.members.observe(this, { users ->
            Timber.d("USER DATA: >> $users")
            membersAdapter.differ.submitList(users!!)
        })

        dialogViewModel.isSearching.observe(this, { isSearching ->
            if (isSearching){
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.INVISIBLE
            }
        })

        dialogViewModel.selectedMembers.observe(this, { membersList ->
            if (membersList.isNotEmpty()) {
                membersChipGroup.visibility = View.VISIBLE
                members = membersList
            }
            membersList.forEach {
                val chip = LayoutInflater.from(context).inflate(
                    R.layout.members_chip_item, null, false
                ) as Chip
                chip.apply {
                    text = it.name
                    setOnCloseIconClickListener { memberChip ->
                        membersChipGroup.removeView(memberChip)
                    }
                }
                membersChipGroup.addView(chip)
            }
        })

        builder.setView(view)

        return builder.create()
    }

    private fun setUpRecyclerView() {
        context?.let { appContext ->
            membersAdapter = CommonRecyclerViewAdapter {
                SearchMemberViewHolder(appContext, it)
            }
        }

        peopleRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = membersAdapter
        }
    }

}