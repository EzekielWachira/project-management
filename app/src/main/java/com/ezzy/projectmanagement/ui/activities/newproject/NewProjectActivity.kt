package com.ezzy.projectmanagement.ui.activities.newproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewProjectBinding
import com.ezzy.projectmanagement.model.Organization
import com.ezzy.projectmanagement.model.Project
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.ui.activities.newproject.viewmodel.NewProjectViewModel
import com.ezzy.projectmanagement.ui.dialogs.AddMembersDialog
import com.ezzy.projectmanagement.ui.dialogs.AssignOrgDialog
import com.ezzy.projectmanagement.util.Constants.ADD_MEMBERS
import com.ezzy.projectmanagement.util.Constants.ASSIGN_ORG
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewProjectActivity : AppCompatActivity(){

    private lateinit var binding : ActivityNewProjectBinding
    private val projectViewModel: NewProjectViewModel by viewModels()
    var members = mutableSetOf<User>()
    var organizations = mutableSetOf<Organization>()
    private  var organization: Organization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("organization")) {
            organization = intent?.extras?.get("organization") as Organization
            supportActionBar?.title = organization!!.name
            organizations.add(organization!!)
        } else { supportActionBar?.title = "New Project" }

        BottomSheetBehavior.from(binding.btmSheet).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnAddMembers.setOnClickListener {
            AddMembersDialog().show(supportFragmentManager, ADD_MEMBERS)
        }

        binding.btnAddOrg.setOnClickListener {
            AssignOrgDialog().show(supportFragmentManager, ASSIGN_ORG)
        }

        binding.startDateEditText.setOnClickListener {
            showDatePicker("Select project start date", binding.startDateEditText)
        }

        binding.endDateEditText.setOnClickListener {
            showDatePicker("Select project end date", binding.endDateEditText)
        }

        projectViewModel.isError.observe(this, { isError ->
            if (isError) {
                makeToast(projectViewModel.errorMessage.toString())
            } else {
                SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE).apply {
                    titleText = "Project Saved"
                    contentText = "Project added successfully"
                    show()
                }
            }
        })

        projectViewModel.organizations.observe(this, {
            if (it.isNotEmpty()) {
                binding.organizationLayout.visibility = View.VISIBLE
                it.forEach { org ->
                    organizations.add(org)
                    val orgChip = LayoutInflater.from(this).inflate(
                        R.layout.members_chip_item, null, false
                    ) as Chip
                    orgChip.apply {
                        text = org.name
                        setOnCloseIconClickListener { chip ->
                            organizations.remove(org)
                            binding.chipOrgGroup.removeView(chip)
                        }
                    }
                    binding.chipOrgGroup.addView(orgChip)
                }
            } else {binding.organizationLayout.visibility = View.INVISIBLE}
        })

        projectViewModel.members.observe(this, {
            if (it.isNotEmpty()) {
                binding.membersLayout.visibility = View.VISIBLE
                it.forEach { user ->
                    members.add(user)
                    val membersChip = LayoutInflater.from(this)
                        .inflate(R.layout.members_chip_item, null, false) as Chip
                    membersChip.apply {
                        text = user.name
                        setOnCloseIconClickListener {
                            binding.membersChipGroup.removeView(it)
                            members.remove(user)
                        }
                    }
                    binding.membersChipGroup.addView(membersChip)
                }
            } else { binding.membersLayout.visibility = View.INVISIBLE }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_project_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSave -> {

                if (isEmpty(binding.projectTitleEditText.text.toString()) ||
                    isEmpty(binding.projectDescriptionEditText.text.toString()) ||
                    isEmpty(binding.projectStageTextView.text.toString()) ||
                    isEmpty(binding.startDateEditText.text.toString()) ||
                    isEmpty(binding.endDateEditText.text.toString())
                ) {
                    makeToast("Please fill out all the fields")
                } else {
                    val project = Project(
                        binding.projectTitleEditText.text.toString(),
                        binding.projectDescriptionEditText.text.toString(),
                        binding.projectStageTextView.text.toString(),
                        binding.startDateEditText.text.toString(),
                        binding.endDateEditText.text.toString()
                    )
                    Timber.d("ORGANISATIONS: >> $organizations : PROJECT: >> $project : MEMBERS $members")
                    projectViewModel.addProject( organizations, project, members)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDatePicker(title : String, editText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText(title)
            setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        }.build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            val date = datePicker.headerText
            editText.setText(date)
        }
    }

    override fun onResume() {
        super.onResume()
        val projectStages = resources.getStringArray(R.array.project_stages)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, projectStages)
        binding.projectStageTextView.setAdapter(arrayAdapter)
        projectViewModel.reloadOrgs()
    }

    private fun isEmpty(string: String): Boolean {
        return string.isEmpty()
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}