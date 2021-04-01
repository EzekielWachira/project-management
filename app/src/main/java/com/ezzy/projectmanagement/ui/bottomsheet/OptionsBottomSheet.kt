package com.ezzy.projectmanagement.ui.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.ezzy.projectmanagement.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var layoutAddMember : LinearLayout
    private lateinit var layoutAttachFile : LinearLayout
    private lateinit var layoutAddOrg : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.new_project_bottom_sheet, container, false)
        layoutAddMember = view.findViewById(R.id.layoutAddMember)
        layoutAttachFile = view.findViewById(R.id.layoutAttachFile)
        layoutAddOrg = view.findViewById(R.id.layoutAddOrg)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        layoutAttachFile.setOnClickListener {
            mListener?.onItemClick("attachFile")
        }
    }

    private var mListener: ItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener){
            mListener = context as ItemClickListener
        } else {
            throw RuntimeException(
                context.toString() + "Must implemenent click listener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun onItemClick(item : String)
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) : OptionsBottomSheet {
            val fragment = OptionsBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }
}

