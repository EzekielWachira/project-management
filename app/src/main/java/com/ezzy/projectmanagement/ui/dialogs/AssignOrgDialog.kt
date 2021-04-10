package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.adapters.viewpager.SearchOrgViewHolder
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AssignOrgDialog : DialogFragment() {

    private lateinit var doneButton : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var orgSearchEditText : TextInputEditText
    private lateinit var orgChipGroup : ChipGroup
    private val organizationViewModel : OrganizationViewModel by viewModels()
    private lateinit var orgAdapter : CommonRecyclerViewAdapter<Organization>

    private var organizationList = arrayListOf<Organization>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(R.layout.layout_add_org_dialog, null)

        view?.let {
            doneButton = view.findViewById(R.id.buttonOrgDone)
            recyclerView = view.findViewById(R.id.peopleRecyclerview)
            orgSearchEditText = view.findViewById(R.id.searchOrgEditText)
            orgChipGroup = view.findViewById(R.id.orgChipGroup)
        }

        setUpRecyclerView()

        orgAdapter.setOnClickListener { org ->
            org?.let { organization ->
                if (organizationList.contains(organization)){
                    return@setOnClickListener
                } else {
                    organizationViewModel.addOrgs(organization)
                }
//                organizationViewModel.orgs.observe(this, {
//                    if (it.contains(organization)){
//                        return@observe
//                    } else {
//                        organizationViewModel.addOrgs(organization)
//                    }
//                })
            }
        }

        orgSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                organizationViewModel.searchOrganizations(
                    orgSearchEditText.text.toString().toLowerCase(
                        Locale.getDefault()
                    )
                )
            }

            override fun afterTextChanged(s: Editable?) {
                organizationViewModel.searchOrganizations(
                    s.toString().toLowerCase(Locale.getDefault())
                )
            }
        })

        organizationViewModel.organizations.observe(this, {
            orgAdapter.differ.submitList(it)
        })

        organizationViewModel.orgsSearched.observe(this, {
            orgAdapter.differ.submitList(it)
        })

        organizationViewModel.orgs.observe(this, { orgs ->
            if (orgs.isNotEmpty()){
                orgChipGroup.visibility = View.VISIBLE
                organizationList = orgs
                orgs.forEach {
                    val orgChip = LayoutInflater.from(context).inflate(
                        R.layout.members_chip_item, null, false
                    ) as Chip
                    orgChip.apply {
                        text = it.name
                        setOnCloseIconClickListener {
                            orgChipGroup.removeView(it)
                        }
                    }
                    orgChipGroup.addView(orgChip)
                }
                Timber.d("ORGANUZATIONS: $orgs")
            }
        })

        doneButton.setOnClickListener {
            dialog?.dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun setUpRecyclerView() {
        context?.let { appContext ->
            orgAdapter = CommonRecyclerViewAdapter {
                SearchOrgViewHolder(appContext, it)
            }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orgAdapter
        }
    }

}