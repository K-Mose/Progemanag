package com.example.progemanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.TaskListItemsAdapter
import com.example.progemanag.databinding.ActivityTaskListBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.Card
import com.example.progemanag.models.Task
import com.example.progemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var _binding: ActivityTaskListBinding

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    private val membersRegister = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this, mBoardDocumentId)
        } else {
           Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDocumentId)
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setupActionbar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        val adapter = TaskListItemsAdapter(this, board.taskList)
        _binding.rvTaskList.apply {
            layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FirestoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun createCard(position: Int, name: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserID())

        val card = Card(name, FirestoreClass().getCurrentUserID(), cardAssignedUserList)
        mBoardDetails.taskList[position].cardList.add(card)
        // replcae old task with new task
//        val cardList = mBoardDetails.taskList[position].cardList
//        cardList.add(card)
//        // 개별적으로 업데이트 할 필요 없이
//        val task = Task(
//                mBoardDetails.taskList[position].title,
//                mBoardDetails.taskList[position].createdBy,
//                cardList
//        )
//        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        startActivity(
            Intent(this@TaskListActivity, CardDetailsActivity::class.java)
            .putExtra(Constants.BOARD_DETAIL, mBoardDetails)
            .putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
            .putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_members -> {
                membersRegister.launch(
                        Intent(this, MembersActivity::class.java)
                        .putExtra(Constants.BOARD_DETAIL, mBoardDetails))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarTaskListActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = mBoardDetails.name
            _binding.toolbarTaskListActivity.setNavigationOnClickListener {
                superOnBackPressed()
            }
        }
    }

}