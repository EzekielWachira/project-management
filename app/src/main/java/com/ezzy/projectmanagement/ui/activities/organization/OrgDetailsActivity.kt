package com.ezzy.projectmanagement.ui.activities.organization

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ezzy.core.domain.Organization
import com.ezzy.core.domain.Project
import com.ezzy.core.domain.User
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.adapters.OrgDetailsMembersViewHolder
import com.ezzy.projectmanagement.adapters.OrgDetailsProjectViewHolder
import com.ezzy.projectmanagement.databinding.ActivityOrgDetailsBinding
import com.ezzy.projectmanagement.ui.activities.newproject.NewProjectActivity
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.ezzy.projectmanagement.util.HorizontalItemDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrgDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrgDetailsBinding
    private var organization : Organization? = null
    private val orgViewModel : OrganizationViewModel by viewModels()
    private lateinit var membersAdapter : CommonRecyclerViewAdapter<User>
    private lateinit var projectAdapter : CommonRecyclerViewAdapter<Project>

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
            organization!!.name?.let { orgViewModel.getOrganizationId(it) }
            setUpViews(organization!!)
        }

        orgViewModel.orgId.observe(this){
            orgViewModel.getOrgMembers(it)
            orgViewModel.getOrgProjects(it)
        }

        orgViewModel.organizationMembers.observe(this) {
            membersAdapter.differ.submitList(it)
        }

        orgViewModel.organizationProjects.observe(this) {
            projectAdapter.differ.submitList(it)
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

        setUpRecyclerViews()
    }

    private fun setUpRecyclerViews() {
        membersAdapter = CommonRecyclerViewAdapter {
            OrgDetailsMembersViewHolder(it)
        }
        projectAdapter = CommonRecyclerViewAdapter {
            OrgDetailsProjectViewHolder(it)
        }

        binding.managementRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@OrgDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = membersAdapter
            addItemDecoration(HorizontalItemDecorator(10))
        }

        binding.projectsRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@OrgDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = projectAdapter
            addItemDecoration(HorizontalItemDecorator(10))
        }
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