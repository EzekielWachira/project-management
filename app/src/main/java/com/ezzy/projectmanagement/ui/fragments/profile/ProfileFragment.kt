package com.ezzy.projectmanagement.ui.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.databinding.FragmentProfileBinding
import com.ezzy.projectmanagement.ui.dialogs.UpdateProfileDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(
            inflater, container, false
        )

        binding.editFAB.setOnClickListener {
            UpdateProfileDialog().show(
                activity?.supportFragmentManager!!,
                "Edit User"
            )
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}