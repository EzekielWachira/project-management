package com.ezzy.projectmanagement.ui.activities.organization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.databinding.ActivityOrgDetailsBinding
import com.ezzy.core.domain.Organization
import com.ezzy.projectmanagement.ui.activities.newproject.NewProjectActivity
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel

class OrgDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrgDetailsBinding
    private var organization : Organization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("organization")) {
            organization = intent.extras?.get("organization") as Organization
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = organization!!.name
            }
            setUpViews(organization!!)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_org_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionAddProject -> {
                Intent(this, NewProjectActivity::class.java).apply {
                    organization?.let {
                        putExtra("organization", it)
                    }
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}