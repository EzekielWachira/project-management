package com.ezzy.projectmanagement.ui.fragments.project.allprojects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.AllProjectsViewHolder
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.databinding.FragmentAllProjectsBinding
import com.ezzy.projectmanagement.model.Project
import com.ezzy.projectmanagement.util.VerticalItemDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllProjectsFragment : Fragment() {

    private var _binding : FragmentAllProjectsBinding? = null
    private val binding get() = _binding!!
    private val allProjectsViewModel : AllProjectsViewModel by viewModels()
    private lateinit var allProjectsAdapter : CommonRecyclerViewAdapter<Project>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAllProjectsBinding.inflate(
            inflater, container, false
        )

        setUpRecyclerView()

        allProjectsViewModel.getAllProjects()

        allProjectsViewModel.allProjects.observe(viewLifecycleOwner, { projectsList ->
            allProjectsAdapter.differ.submitList(projectsList)
        })

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
            addItemDecoration(VerticalItemDecorator(5))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}