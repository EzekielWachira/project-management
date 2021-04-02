package com.ezzy.projectmanagement.ui.activities.organization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ezzy.projectmanagement.databinding.ActivityOrganizationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrganizationsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrganizationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}