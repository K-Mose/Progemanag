package com.example.progemanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.TaskListItemsAdapter
import com.example.progemanag.databinding.ActivityTaskListBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.Card
import com.example.progemanag.models.Task
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.reflect.KClass

class TaskListActivity : BaseActivity() {
    private lateinit var _binding: ActivityTaskListBinding

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

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


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedBy)
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

        // create card notification -  assigned된 멤버에게 각각 알림을 보냄
        for ( user in mAssignedMemberDetailList) {
            if (FirestoreClass().getCurrentUserID() != user.id){
                Log.i("Send_TO:",user.id)
                CardViewModel(name,user.fcmToken).cardCreated()
            }
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        membersRegister.launch(
            Intent(this@TaskListActivity, CardDetailsActivity::class.java)
                .putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                .putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
                .putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
                .putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)
        )
    }

    fun boardMembersDetailsList(list: ArrayList<User>) {
        mAssignedMemberDetailList = list

        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)
        _binding.rvTaskList.apply {
            layoutManager = LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            this.adapter = adapter
        }
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
            R.id.action_delete_board -> {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().deleteBoard(this, mBoardDetails)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteBoardSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun updateCardsInTaskList(tasListPosition: Int, cards: ArrayList<Card>) {
        mBoardDetails.taskList.also{
            it.removeAt(it.size - 1)
            it[tasListPosition].cardList = cards
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
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

    /**
     * Card create / update / delete Notifications
     */
    private inner class CardViewModel(val cardName: String, val token: String) : ViewModel() {
        fun cardCreated() {
            viewModelScope.launch {
                val result = try {
                    CardCoroutine(cardName, token).cardCrated()
                } catch (e: SocketTimeoutException) {
                    "Connection TimeOut"
                } catch (e: Exception) {
                    "Error " + e.message
                }
                Log.i("Notification_Result:",result)
            }
        }
    }

    private inner class CardCoroutine(val cardName: String, val token: String) {
        suspend fun cardCrated(): String{
            return withContext(Dispatchers.IO) {
                (URL(Constants.FCM_BASE_URL).openConnection() as HttpURLConnection).run {
                    doOutput = true
                    doInput = true
                    instanceFollowRedirects = false
                    requestMethod = "POST"

                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("charset", "utf-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty(
                        Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                    )
                    useCaches = false
                    val jsonRequest = JSONObject()
                    val dataObject = JSONObject()
                    dataObject.put(Constants.FCM_KEY_TITLE, "At the board ${mBoardDetails.name}")
                    dataObject.put(Constants.FCM_KEY_MESSAGE, "new Card added $cardName ")
                    dataObject.put(Constants.FCM_METHOD, Constants.CARDS)
                    dataObject.put(Constants.DOCUMENT_ID, mBoardDocumentId)
                    jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                    jsonRequest.put(Constants.FCM_KEY_TO, token)
                    outputStream.write(jsonRequest.toString().toByteArray())
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream.use {
                            it.reader().use { reader ->
                                reader.readText()
                            }
                        }
                    } else {
                        responseMessage
                    }
                }
            }
        }
    }
}