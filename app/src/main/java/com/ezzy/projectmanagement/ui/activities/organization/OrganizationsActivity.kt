package com.ezzy.projectmanagement.ui.activities.organization

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.adapters.viewpager.OrganizationViewHolder
import com.ezzy.projectmanagement.databinding.ActivityOrganizationsBinding
import com.ezzy.core.domain.Organization
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.ezzy.projectmanagement.util.Constants.ORGANIZATIONS
import com.ezzy.projectmanagement.util.Directions
import com.ezzy.projectmanagement.util.ItemDecorator
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class OrganizationsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOrganizationsBinding
    private lateinit var organizationsAdapter: CommonRecyclerViewAdapter<Organization>
    private val organizationViewModel: OrganizationViewModel by viewModels()
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = ORGANIZATIONS
            setDisplayHomeAsUpEnabled(true)
        }

        setUpRecyclerView()

        organizationViewModel.getAllOrganizations()
        organizationViewModel.getUserOrgs()

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.text.isNotEmpty()){
                    organizationViewModel.searchOrgs(
                        binding.searchEditText.text.toString().toLowerCase(
                            Locale.getDefault()
                        )
                    )
                } else {
                    organizationViewModel.getAllOrganizations()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.searchEditText.text.isNotEmpty()){
                    organizationViewModel.searchOrgs(
                        s.toString().toLowerCase(Locale.getDefault())
                    )
                } else {
                    organizationViewModel.getAllOrganizations()
                }
            }

        })

        organizationViewModel.userOrganizations.observe(this) { organizationList ->
            Timber.d("ORGANIZATION LIST = $organizationList")
            organizationsAdapter.differ.submitList(organizationList.toList())
        }

        organizationViewModel.isOrgLoadingSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                binding.orgProgressBar.visibility = View.INVISIBLE
            } else {
                binding.orgProgressBar.visibility = View.VISIBLE
            }
        }

        organizationViewModel.orgsSearched.observe(this) { orgsList ->
            organizationsAdapter.differ.submitList(orgsList)
        }

        organizationsAdapter.setOnClickListener {
            Intent(this, OrgDetailsActivity::class.java).apply {
                putExtra("organization", it)
                startActivity(this)
            }
        }
    }

    private fun setUpRecyclerView() {
        organizationsAdapter = CommonRecyclerViewAdapter {
            OrganizationViewHolder(this, it)
        }
        binding.organizationsRecyclerView.apply {
            adapter = organizationsAdapter
            layoutManager = LinearLayoutManager(this@OrganizationsActivity)
            addItemDecoration(ItemDecorator(Directions.VERTICAL, 10))
        }
    }

    private fun makeToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}