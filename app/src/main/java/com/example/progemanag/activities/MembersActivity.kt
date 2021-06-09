package com.example.progemanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.MemberListItemsAdapter
import com.example.progemanag.databinding.ActivityMembersBinding
import com.example.progemanag.databinding.DialogSearchMemberBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var _binding: ActivityMembersBinding

    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>

    private var anyChangesMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            Log.e("YESYES","${mBoardDetails.assignedBy}")
        } else {
            Log.e("NONO","NO")
        }
        setupActionbar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedBy)
    }

    fun setupMemberList(list: ArrayList<User>) {
        mAssignedMembersList = list
        hideProgressDialog()

        _binding.rvMembersList.apply {
            layoutManager = LinearLayoutManager(this@MembersActivity)
            setHasFixedSize(true)
            adapter = MemberListItemsAdapter(this@MembersActivity, list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        val dialogBinding = DialogSearchMemberBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialogBinding.apply {
            tvAdd.setOnClickListener {
                val email = etEmailSearchMember.text.toString()
                if(email.isNotEmpty()) {
                    dialog.dismiss()
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getMemberDetails(this@MembersActivity, email)
                } else {
                    showErrorSnackBar("Please enter members email address.")
                }
            }
            tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    fun memberDetail(user: User) {
        mBoardDetails.assignedBy.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setupMemberList(mAssignedMembersList)
    }

    private fun thisOnBackPressed() {
        if (anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        super.superOnBackPressed()
    }

    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarMembersActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = resources.getString(R.string.members)
            _binding.toolbarMembersActivity.setNavigationOnClickListener {
                thisOnBackPressed()
            }
        }
    }
}