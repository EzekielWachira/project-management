package com.ezzy.projectmanagement.ui.fragments.project.pending

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentPendingProjectsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingProjectsFragment : Fragment() {

    private var _binding : FragmentPendingProjectsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPendingProjectsBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}