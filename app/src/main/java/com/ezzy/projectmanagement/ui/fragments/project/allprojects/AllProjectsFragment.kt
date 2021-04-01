package com.ezzy.projectmanagement.ui.fragments.project.allprojects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentAllProjectsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllProjectsFragment : Fragment() {

    private var _binding : FragmentAllProjectsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAllProjectsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}