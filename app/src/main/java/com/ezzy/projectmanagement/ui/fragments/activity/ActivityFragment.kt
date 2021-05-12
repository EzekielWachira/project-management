package com.ezzy.projectmanagement.ui.fragments.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezzy.core.domain.Activity
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.ActivityViewHolder
import com.ezzy.projectmanagement.adapters.CommonRecyclerViewAdapter
import com.ezzy.projectmanagement.databinding.FragmentActivityBinding
import com.ezzy.projectmanagement.util.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private val activityViewModel: ActivityViewModel by viewModels()
    private lateinit var activityAdapter: CommonRecyclerViewAdapter<Activity>

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityBinding.inflate(
            inflater, container, false
        )

        setUpRecyclerView()

        activityViewModel.activities.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                activityAdapter.differ.submitList(it)
                binding.layoutNoActivities.invisible()
            } else {
                binding.activityRecyclerView.invisible()
                binding.layoutNoActivities.visible()
            }
        }

        return binding.root
    }

    private fun setUpRecyclerView() {
        context?.let { ctx ->
            activityAdapter = CommonRecyclerViewAdapter {
                ActivityViewHolder(ctx, it)
            }
        }

        binding.activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = activityAdapter
            addItemDecoration(ItemDecorator(Directions.VERTICAL, 5))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}