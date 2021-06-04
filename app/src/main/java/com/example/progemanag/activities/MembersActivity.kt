package com.example.progemanag.activities

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.MemberListItemsAdapter
import com.example.progemanag.databinding.ActivityMembersBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var _binding: ActivityMembersBinding

    private lateinit var mBoardDetails: Board

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
        hideProgressDialog()

        _binding.rvMembersList.apply {
            layoutManager = LinearLayoutManager(this@MembersActivity)
            setHasFixedSize(true)
            adapter = MemberListItemsAdapter(this@MembersActivity, list)
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarMembersActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = resources.getString(R.string.members)
            _binding.toolbarMembersActivity.setNavigationOnClickListener {
                superOnBackPressed()
            }
        }
    }
}