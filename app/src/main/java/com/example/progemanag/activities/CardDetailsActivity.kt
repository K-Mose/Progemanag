package com.example.progemanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.CardMemberListItemsAdapter
import com.example.progemanag.databinding.ActivityCardDetailsBinding
import com.example.progemanag.dialog.LabelColorListDialog
import com.example.progemanag.dialog.MembersListDialog
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.*
import com.example.progemanag.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {
    private lateinit var _binding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        getIntentData()
        setupActionBar()
        setupSelectedMembersList()

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
            mSelectColor = mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition].labelColor
            if (mSelectColor.isNotEmpty()) {
                setColor()
            }
            tvSelectMembers.setOnClickListener {
                membersListDialog()
            }
            mSelectedDueDateMilliSeconds = mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition].dueDate
            if (mSelectedDueDateMilliSeconds > 0) {
                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                tvSelectDueDate.text = sdf.format(Date(mSelectedDueDateMilliSeconds))
            }
            tvSelectDueDate.setOnClickListener {
                dataPicker()
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
            Log.d("MemberDetailList", "$mMembersDetailList")
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
            mSelectColor,
            mSelectedDueDateMilliSeconds
        )
        mBoardDetails.taskList.also{
            it.removeAt(it.size-1)
            it[mTaskListPosition].cardList[mCardPosition] = card
        }
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
            resources.getString(R.string.str_select_member)) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }.show()
    }

    /**
     * setupSelectedMembersList
     * Card안에 AssginTo를 받고 선택된 멤버리스트와 비교하여 selectedMemberList에 추가
     *
     */
    private fun setupSelectedMembersList() {
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMemberList){
                if (mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("","")) // (+) 아이콘용
            _binding.apply{
                tvSelectMembers.visibility = View.GONE
                rvSelectedMembersList.apply{
                    visibility = View.VISIBLE
                    layoutManager = GridLayoutManager(
                        this@CardDetailsActivity,
                        6
                    )
                    adapter = CardMemberListItemsAdapter(this@CardDetailsActivity, selectedMembersList, true)
                        .also {
                            it.setOnClickListener(object : CardMemberListItemsAdapter.OnClickListener{
                                override fun onClick() {
                                    membersListDialog()
                                }
                            })
                        }
                }
            }
        } else {
            _binding.apply{
                tvSelectMembers.visibility = View.VISIBLE
                rvSelectedMembersList.visibility = View.GONE
            }
        }
    }

    private fun dataPicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                _binding.tvSelectDueDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
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