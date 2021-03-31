package com.ezzy.projectmanagement.ui.activities.newproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewProjectBinding
import com.ezzy.projectmanagement.ui.bottomsheet.OptionsBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewProjectActivity : AppCompatActivity(), OptionsBottomSheet.ItemClickListener {

    private lateinit var binding : ActivityNewProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddMember.setOnClickListener {
            supportFragmentManager?.let {
                OptionsBottomSheet.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }
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