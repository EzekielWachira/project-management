package com.ezzy.projectmanagement.ui.activities.organization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ezzy.projectmanagement.databinding.ActivityOrgDetailsBinding

class OrgDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrgDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}