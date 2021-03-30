package com.ezzy.projectmanagement.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ezzy.projectmanagement.ui.fragments.project.allprojects.AllProjectsFragment
import com.ezzy.projectmanagement.ui.fragments.project.assigned.AssignedFragment
import com.ezzy.projectmanagement.ui.fragments.project.completed.CompletedProjectsFragment
import com.ezzy.projectmanagement.ui.fragments.project.pending.PendingProjectsFragment
import com.ezzy.projectmanagement.util.Constants.PROJECT_TAB_COUNT

class ProjectViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return PROJECT_TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        var fragment : Fragment? = null
        when(position){
            0 -> fragment = AllProjectsFragment()
            1 -> fragment = AssignedFragment()
            2 -> fragment = CompletedProjectsFragment()
            3 -> fragment = PendingProjectsFragment()
        }
        return fragment!!
    }
}