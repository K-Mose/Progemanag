package com.example.progemanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityCardDetailsBinding
import com.example.progemanag.dialog.LabelColorListDialog
import com.example.progemanag.dialog.MembersListDialog
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.Card
import com.example.progemanag.models.Task
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var _binding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        getIntentData()
        setupActionBar()
        Log.e("onCreated:: ", "${mBoardDetails.taskList}")

        _binding.apply {
            mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition]
                .also {
                    etNameCardDetails.setText(it.name)
                    etNameCardDetails.setSelection(it.name.length)
                }
            btnUpdateCardDetails.setOnClickListener {
                if (etNameCardDetails.text.toString().isNotEmpty()) {
                    updateCardDetails()
                } else {
                    Toast.makeText(this@CardDetailsActivity,
                    "Enter a Card Name", Toast.LENGTH_SHORT).show()
                }
            }
            tvSelectLabelColor.setOnClickListener{
                labelColorsListDialog()
            }
            mSelectColor = mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].labelColor
            if (mSelectColor.isNotEmpty()) {
                setColor()
            }
            tvSelectMembers.setOnClickListener {
                membersListDialog()
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBERS_LIST)!!
        }


    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails() {
        val card = Card(
            _binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo,
            mSelectColor
        )

        mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition] = card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardList = mBoardDetails.taskList[mTaskListPosition].cardList

        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1) // AddCard 버튼까지 list 안에 들어간다고 한다.. ?

        taskList[mTaskListPosition].cardList = cardList
        Log.e("deleted Card:: ", "${mBoardDetails.taskList}")
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)

    }

    private fun alterDialogForDeleteCard(cardName: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.alert))
            .setMessage(resources.getString(R.string.confirmation_message_to_delete_card, cardName))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                dialog.dismiss()
                deleteCard()
            }.setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alterDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        _binding.apply{
            tvSelectLabelColor.text = ""
            tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectColor))
        }
    }

    private fun labelColorsListDialog() {
        val colorsList = colorsList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun membersListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo
        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList){
                    if (mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                        Log.e("MEMBER_SELECTED:", "${mMembersDetailList[i].name}")
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices)
                mMembersDetailList[i].selected = false
        }

        object : MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo = user.id
            }
        }.show()
    }

    private fun setupActionBar() {
        setSupportActionBar(_binding.toolbarCardDetailsActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition]
                .name
        }
        _binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            superOnBackPressed()
        }
    }
}