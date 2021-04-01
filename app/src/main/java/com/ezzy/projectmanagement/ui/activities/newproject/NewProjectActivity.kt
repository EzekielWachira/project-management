package com.ezzy.projectmanagement.ui.activities.newproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewProjectBinding
import com.ezzy.projectmanagement.ui.bottomsheet.OptionsBottomSheet
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewProjectActivity : AppCompatActivity(), OptionsBottomSheet.ItemClickListener {

    private lateinit var binding : ActivityNewProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showBottomSheet.setOnClickListener {
            supportFragmentManager?.let {
                OptionsBottomSheet.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }
        }

        binding.startDateEditText.setOnClickListener {
            showDatePicker("Select project start date", binding.startDateEditText)
        }

        binding.endDateEditText.setOnClickListener {
            showDatePicker("Select project end date", binding.endDateEditText)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_project_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSave -> makeToast("Save clicked")
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

    override fun onItemClick(item: String) {
        when(item){
            "add" -> makeToast("Button add clicked")
            else -> makeToast("Nothing was clicked")
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}