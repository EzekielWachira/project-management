package com.ezzy.projectmanagement.ui.activities.newproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val TAG = "NewProjectActivity"
@AndroidEntryPoint
class NewProjectActivity : AppCompatActivity(){

    private lateinit var binding : ActivityNewProjectBinding
    private val projectViewModel: NewProjectViewModel by viewModels()
//    @Inject
//    lateinit var firebaseFirestore: FirebaseFirestore
    var members : MutableSet<User>? = null
    var organizations : MutableList<Organization>? = null
    private  var organization: Organization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("organization")) {
            organization = intent?.extras?.get("organization") as Organization
            supportActionBar?.title = organization!!.name
        } else { supportActionBar?.title = "New Project" }

//        projectViewModel = NewProjectViewModel(application, firebaseFirestore)

        BottomSheetBehavior.from(binding.btmSheet).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

//        setUpChips()

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

        projectViewModel.isError.observe(this, Observer { isError ->
            if (isError) {
                makeToast(projectViewModel.errorMessage.toString())
            } else {
                makeToast("Project added succesfully")
            }
        })

        projectViewModel.organizations.observe(this, {

            it.forEach { org ->
                val orgChip = LayoutInflater.from(this).inflate(
                    R.layout.members_chip_item, null, false
                ) as Chip
                orgChip.apply {
                    text = org.name
                    setOnCloseIconClickListener { chip ->
                        binding.organizationChipGroup.removeView(chip)
                    }
                }
                binding.organizationChipGroup.addView(orgChip)
            }
        })


    }

    private fun setUpChips() {
        if (members?.isNotEmpty() == true || organizations?.isNotEmpty() == true) {
            binding.membersLayout.visibility = View.VISIBLE
            members?.forEach { member ->
                val chip = LayoutInflater.from(this).inflate(
                    R.layout.members_chip_item, null, false
                ) as Chip
                chip.apply {
                    text = member?.name
                    setOnCloseIconClickListener {
                        binding.membersChipGroup.removeView(it)
                    }
                }
                binding.membersChipGroup.addView(chip)
            }
            organizations.let {
                it?.forEach { org ->
                    val chip = LayoutInflater.from(this).inflate(
                        R.layout.members_chip_item, null, false
                    ) as Chip
                    chip.apply {
                        text = org.name
                        setOnCloseIconClickListener {
                            binding.organizationChipGroup.removeView(it)
                        }
                    }
                    binding.organizationChipGroup.addView(chip)
                }
            }
        }
    }

    fun addMembers(users: MutableSet<User>) {
        members?.addAll(users)
        Timber.d("MEMBERS : =>>> $members")
    }

    fun addOrganizations(organization: Organization){
        if (organizations?.contains(organization) == true){
            makeToast("Organization already exist")
        } else {
            organizations?.add(organization)
        }
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
                    projectViewModel.addProject(project, members)
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
    }

    private fun isEmpty(string: String): Boolean {
        return string.isEmpty()
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}