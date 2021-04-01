package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ezzy.projectmanagement.R

class AddMembersDialog : DialogFragment() {

    private lateinit var doneButton : Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(
            R.layout.layout_add_members_dialog, null
        )

        view?.let {
            doneButton = view.findViewById(R.id.buttonDone)!!
        }

        doneButton.setOnClickListener {
            dialog?.dismiss()
        }

        builder.setView(view)

        return builder.create()
    }

}