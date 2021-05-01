package com.ezzy.projectmanagement.ui.fragments.project.allprojects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezzy.core.domain.Organization
import com.ezzy.projectmanagement.adapters.AllProjectsViewHolder
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.databinding.FragmentAllProjectsBinding
import com.ezzy.core.domain.Project
import com.ezzy.projectmanagement.ui.activities.organization.viewmodel.OrganizationViewModel
import com.ezzy.projectmanagement.ui.fragments.project.viewmodel.BaseProjectViewModel
import com.ezzy.projectmanagement.util.Directions
import com.ezzy.projectmanagement.util.ItemDecorator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllProjectsFragment : Fragment() {

    private var _binding : FragmentAllProjectsBinding? = null
    private val binding get() = _binding!!
    private val baseViewModel : BaseProjectViewModel by viewModels()
    private lateinit var allProjectsAdapter : CommonRecyclerViewAdapter<Project>
    private val organizationViewModel : OrganizationViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var userOrganizations = listOf<Organization>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAllProjectsBinding.inflate(
            inflater, container, false
        )

        setUpRecyclerView()

        baseViewModel.getAllProjects()
        organizationViewModel.getUserOrgs()

        baseViewModel.allProjects.observe(viewLifecycleOwner, { projectsList ->
            allProjectsAdapter.differ.submitList(projectsList)
        })

        baseViewModel.isProjectLoadSuccess.observe(viewLifecycleOwner, { isSuccess ->
            if (isSuccess) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        })

        organizationViewModel.userOrganizations.observe(viewLifecycleOwner) { orgList ->
            if (orgList.isNotEmpty()) {
                userOrganizations = orgList
            }
        }

        return binding.root
    }

    private fun setUpRecyclerView() {
        context?.let { appContext ->
            allProjectsAdapter = CommonRecyclerViewAdapter {
                AllProjectsViewHolder(appContext, it)
            }
        }
        binding.allProjectRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allProjectsAdapter
            addItemDecoration(ItemDecorator(Directions.VERTICAL, 8))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}