package com.ezzy.projectmanagement.ui.fragments.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.viewpager.ProjectViewPagerAdapter
import com.ezzy.projectmanagement.databinding.FragmentProfileBinding
import com.ezzy.projectmanagement.databinding.FragmentProjectBinding
import com.ezzy.projectmanagement.util.Constants.TAB_TITLES
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProjectFragment : Fragment() {

    private var _binding : FragmentProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentProjectBinding.inflate(
            inflater, container, false
        )

        binding.projectViewPager.apply {
            adapter = ProjectViewPagerAdapter(this@ProjectFragment)
        }

        TabLayoutMediator(
            binding.projectTabLayout, binding.projectViewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = TAB_TITLES[position]
        }.attach()

        binding.projectTabLayout.addOnTabSelectedListener(tabSelectedListener)

        return binding.root
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                binding.projectViewPager.currentItem = tab.position
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab?.let { return }
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            TODO("Not yet implemented")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}