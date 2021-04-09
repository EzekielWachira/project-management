package com.ezzy.projectmanagement.ui.activities.organization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.ActivityOrgDetailsBinding
import com.ezzy.projectmanagement.model.Organization

class OrgDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrgDetailsBinding
    private lateinit var organization : Organization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("organization")) {
            organization = intent.extras?.get("organization") as Organization
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = organization.name
            }
            setUpViews(organization)
        }
    }

    private fun setUpViews(organization: Organization) {
        binding.orgDetailName.text = organization.name
        binding.orgDetailAbout.text = organization.about
        Glide.with(this)
            .load(organization.imageSrc)
            .placeholder(ContextCompat.getDrawable(
                this, R.drawable.placeholder
            ))
            .into(binding.orgDetailImage)
    }
}