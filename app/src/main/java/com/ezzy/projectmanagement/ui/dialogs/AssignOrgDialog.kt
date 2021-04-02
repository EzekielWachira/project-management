package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ezzy.projectmanagement.R

class AssignOrgDialog : DialogFragment() {

    private lateinit var doneButton : Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(R.layout.layout_add_org_dialog, null)

        view?.let {
            doneButton = view.findViewById(R.id.buttonOrgDone)
        }

        doneButton.setOnClickListener {
            dialog?.dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

}