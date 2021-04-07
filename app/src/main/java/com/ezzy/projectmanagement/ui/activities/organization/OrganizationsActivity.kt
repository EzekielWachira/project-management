package com.ezzy.projectmanagement.ui.activities.organization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.adapters.viewpager.OrganizationsAdapter
import com.ezzy.projectmanagement.databinding.ActivityOrganizationsBinding
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.VerticalItemDecorator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrganizationsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrganizationsBinding
    private lateinit var orgAdapter : OrganizationsAdapter
    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var storage: FirebaseStorage
    private lateinit var organizationViewModel: OrganizationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = ORGANIZATIONS
            setDisplayHomeAsUpEnabled(true)
        }

        organizationViewModel = OrganizationViewModel(application, firestore, storage)

        setUpRecyclerView()

        organizationViewModel.retrieveOrganizations()

        organizationViewModel.organizations.observe(this, { organizationList ->
            orgAdapter.differ.submitList(organizationList)
        })
    }

    private fun setUpRecyclerView() {
        orgAdapter = OrganizationsAdapter()
        binding.organizationsRecyclerView.apply {
            adapter = orgAdapter
            layoutManager = LinearLayoutManager(this@OrganizationsActivity)
            addItemDecoration(VerticalItemDecorator(5))
        }
    }
}