package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ezzy.projectmanagement.R
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProfileDialog : DialogFragment() {

    private lateinit var nameEditText : TextInputEditText
    private lateinit var emailEditText : TextInputEditText
    private lateinit var aboutEditText : TextInputEditText
    private lateinit var btnDone : Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(R.layout.update_user_profile, null)

        view?.let {
            nameEditText = view.findViewById(R.id.usernameEditText)
            emailEditText = view.findViewById(R.id.userEmailEditText)
            aboutEditText = view.findViewById(R.id.userAboutEditText)
            btnDone = view.findViewById(R.id.btnSave)
        }

        builder.setView(view)
        return builder.create()
    }

}