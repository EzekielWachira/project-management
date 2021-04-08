package com.ezzy.projectmanagement.ui.activities.organization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
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
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OrganizationsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrganizationsBinding
    private lateinit var orgAdapter : OrganizationsAdapter
    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var storage: FirebaseStorage
    private val organizationViewModel: OrganizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = ORGANIZATIONS
            setDisplayHomeAsUpEnabled(true)
        }

        setUpRecyclerView()

        organizationViewModel.retrieveOrganizations()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.text.isNotEmpty()){
                    organizationViewModel.searchOrganizations(
                        binding.searchEditText.text.toString().toLowerCase(
                            Locale.getDefault()
                        )
                    )
                } else {
                    organizationViewModel.retrieveOrganizations()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.searchEditText.text.isNotEmpty()){
                    organizationViewModel.searchOrganizations(
                        s.toString().toLowerCase(Locale.getDefault())
                    )
                } else {
                    organizationViewModel.retrieveOrganizations()
                }
            }

        })

        organizationViewModel.organizations.observe(this, { organizationList ->
            orgAdapter.differ.submitList(organizationList)
        })

        organizationViewModel.isOrgLoadingSuccess.observe(this, { isSuccess ->
            if (isSuccess) {
                binding.orgProgressBar.visibility = View.INVISIBLE
            } else {
                binding.orgProgressBar.visibility = View.VISIBLE
            }
        })

        organizationViewModel.orgsSearched.observe(this, { orgsList ->
            orgAdapter.differ.submitList(orgsList)
        })
    }

    private fun setUpRecyclerView() {
        orgAdapter = OrganizationsAdapter()
        binding.organizationsRecyclerView.apply {
            adapter = orgAdapter
            layoutManager = LinearLayoutManager(this@OrganizationsActivity)
            addItemDecoration(VerticalItemDecorator(10))
        }
    }
}