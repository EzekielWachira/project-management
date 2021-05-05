package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.ezzy.projectmanagement.R
import com.firebase.ui.auth.data.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfileDialog : DialogFragment() {

    private lateinit var nameEditText : TextInputEditText
    private lateinit var emailEditText : TextInputEditText
    private lateinit var aboutEditText : TextInputEditText
    private lateinit var btnDone : Button

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

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

        nameEditText.setText(firebaseAuth.currentUser?.displayName)
        emailEditText.setText(firebaseAuth.currentUser?.email)

        builder.setView(view)
        return builder.create()
    }

}