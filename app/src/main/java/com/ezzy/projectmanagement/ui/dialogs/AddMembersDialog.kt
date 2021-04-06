package com.ezzy.projectmanagement.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ezzy.projectmanagement.R
import com.ezzy.projectmanagement.adapters.viewpager.SearchMembersAdapter
import com.ezzy.projectmanagement.model.User
import com.ezzy.projectmanagement.util.Constants.USERS
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddMembersDialog : DialogFragment() {

    private lateinit var doneButton : Button
    private lateinit var searchEditText : TextInputEditText
    private lateinit var peopleRecyclerview: RecyclerView
    @Inject
    lateinit var firestore: FirebaseFirestore
    lateinit var membersAdapter: SearchMembersAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val inflator = activity?.layoutInflater
        val view = inflator?.inflate(
            R.layout.layout_add_members_dialog, null
        )

        membersAdapter = SearchMembersAdapter()

        view?.let {
            doneButton = view.findViewById(R.id.buttonDone)!!
            searchEditText = view.findViewById(R.id.searchPeopleEditText)
            peopleRecyclerview = view.findViewById(R.id.peopleRecyclerview)
        }

        doneButton.setOnClickListener {
            dialog?.dismiss()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String = searchEditText.text.toString()
                Timber.d("SEARCH QUERY: =>> $searchText")
                searchMembers(searchText.toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
                Timber.d("SEARCH QUERY ss: =>> ${s.toString().toLowerCase(Locale.getDefault())}")
                searchMembers(s.toString().toLowerCase(Locale.getDefault()))
            }

        })

        peopleRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = membersAdapter
        }

        builder.setView(view)

        return builder.create()
    }

    private fun searchMembers (name: String) {
        try {
            firestore.collection(USERS).whereEqualTo("name", name)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val members = mutableListOf<User>()
                        for (snapshot in it.result!!){
                            val member = User(snapshot.getString("name"), snapshot.getString("email"))
                            Timber.d("MEMBER: >> $member")
                            members.add(member)
                        }
                        Timber.d("USERS ==>> $members")
                        membersAdapter.differ.submitList(members)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error searching members", Toast.LENGTH_SHORT).show()
                }
//            firestore.collection(USERS).orderBy("name")
//                .startAt(name).endAt("${name}\uf8ff")
//                .get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        membersAdapter.differ.submitList(
//                            it.result!!.toObjects(User::class.java)
//                        )
//                        Timber.d("USER:=>>  ${it.result!!.size()}")
//                    } else {
//                        Toast.makeText(context, "Error searching members", Toast.LENGTH_SHORT).show()
//                    }
//                }.addOnFailureListener {
//                    Toast.makeText(context, "Error searching members", Toast.LENGTH_SHORT).show()
//                }
        } catch (e : Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

}