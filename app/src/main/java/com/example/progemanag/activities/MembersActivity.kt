package com.example.progemanag.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progemanag.R
import com.example.progemanag.adapters.MemberListItemsAdapter
import com.example.progemanag.databinding.ActivityMembersBinding
import com.example.progemanag.databinding.DialogSearchMemberBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

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

        SendNotificationToUserViewModel(mBoardDetails.name, user.fcmToken).send()
//        SendNotificationToUserAsyncTask(mBoardDetails.name, user.fcmToken ).execute()
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


    private inner class SendNotificationToUserViewModel(val boardName: String, val token: String) : ViewModel() {
        fun send() {
            viewModelScope.launch {
                showProgressDialog("SEND_NOTIFICATION ...")
                val result = try {
                    SendNotificationToUserCoroutine(boardName, token).sendNotification()
                } catch (e: SocketTimeoutException) {
                    "Connection TimeOut"
                } catch (e: Exception) {
                    "Error " + e.message
                } finally {
                    hideProgressDialog()
                }
                Log.i("Notification_Result:",result)
            }
        }
    }

    inner class SendNotificationToUserCoroutine(private val boardName: String, private val token: String) {
        suspend fun sendNotification() : String {
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
                    dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $boardName")
                    dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")

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


    //  Deprecated AsyncTask to Coroutine
    @SuppressLint("StaticFieldLeak")
    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String)
        : AsyncTask<Any, Void, String>() {
        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var con: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                con = url.openConnection() as HttpURLConnection
                con.doOutput = true
                con.doInput = true
                con.instanceFollowRedirects = false
                con.requestMethod = "POST"

                con.setRequestProperty("Content-Type", "application/json")
                con.setRequestProperty("charset", "utf-8")
                con.setRequestProperty("Accept", "application/json")

                con.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                con.useCaches = false

                val wr = DataOutputStream(con.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int = con.responseCode
                Log.e("HttpResult:","$httpResult")
                result = if (httpResult == HttpURLConnection.HTTP_OK) {
                    con.inputStream.use {
                        it.reader().use { reader ->
                            reader.readText()
                        }
                    }
                } else {
                    con.responseMessage
                }
                Log.e("Result:","$result")
            } catch (e : SocketTimeoutException) {
                result = "Connection TimeOut"
            } catch (e : Exception) {
                result = "Error : " + e.message
            } finally {
                con?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))
        }
    }
}