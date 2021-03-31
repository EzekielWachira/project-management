package com.ezzy.projectmanagement.ui.activities.newproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityNewProjectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewProjectActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        val projectStages = resources.getStringArray(R.array.project_stages)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, projectStages)
        binding.projectStageTextView.setAdapter(arrayAdapter)
    }
}