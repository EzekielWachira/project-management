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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.adapters.viewpager.SearchOrgViewHolder
import com.ezzy.core.domain.Organization
import com.ezzy.projectmanagement.ui.activities.newproject.NewProjectActivity
import com.ezzy.projectmanagement.ui.activities.newproject.viewmodel.NewProjectViewModel
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
    private val newProjectViewModel : NewProjectViewModel by activityViewModels()
    private lateinit var orgAdapter : CommonRecyclerViewAdapter<Organization>
    private var organizations = mutableSetOf<Organization>()

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
                if (organizations.contains(organization)){
                    Timber.d("ORG IS ALREADY ADDED")
                } else {
                    Timber.d("ORGS_SET $organizations")
                    organizations.add(organization)
                    organizationViewModel.attachOrgs(organizations)
                }
            }
        }

        orgSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                organizationViewModel.searchOrgs(
                    orgSearchEditText.text.toString().toLowerCase(
                        Locale.getDefault()
                    )
                )
            }

            override fun afterTextChanged(s: Editable?) {
                organizationViewModel.searchOrgs(
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

        organizationViewModel.orgsSelected.observe(this, { orgs ->
            if (orgs.isNotEmpty()){
                orgs.forEach {
                    organizations.add(it)
                    val chip = LayoutInflater.from(context).inflate(
                        R.layout.members_chip_item, null, false
                    ) as Chip
                    chip.apply {
                        text = it.name
                        setOnCloseIconClickListener { orgChip ->
                            orgChipGroup.removeView(orgChip)
                            organizations.remove(it)
                        }
                    }
                    orgChipGroup.addView(chip)
                }
            }
        })

        newProjectViewModel.organizations.observe(this, { organizations ->
            organizations?.let { orgs ->
                if (orgs.isNotEmpty()){
                    orgChipGroup.visibility = View.VISIBLE
                    orgs.forEach { org ->
                        val chip = LayoutInflater.from(context).inflate(
                            R.layout.members_chip_item, null, false
                        ) as Chip
                        chip.apply {
                            text = org.name
                            setOnCloseIconClickListener {
                                orgChipGroup.removeView(it)
                            }
                        }
                        orgChipGroup.addView(chip)
                    }
                }
            }
        })

        doneButton.setOnClickListener {
            Timber.i("ORGS TO FORWARD: >> $organizations")
            newProjectViewModel.addOrganizations(organizations)
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