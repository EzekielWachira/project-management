package com.ezzy.projectmanagement.ui.fragments.project_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentProjectDetailsBinding

class ProjectDetailsFragment : Fragment() {

    private var _binding : FragmentProjectDetailsBinding? = null
    private val binding get() = _binding!!
    val args : ProjectDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProjectDetailsBinding.inflate(
            inflater, container, false
        )

        val project = args.project

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}