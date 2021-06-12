package com.example.progemanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.adapters.BoardItemsAdapter
import com.example.progemanag.databinding.ActivityMainBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mUserName: String
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var _binding: ActivityMainBinding
    // https://stackoverflow.com/a/63654043
    // 1 requst당 1 액티비티만 실행, ActivityResultCallback 객체를 리턴. 같은 값 실행하면 재사용 가능함
    // ActivityResultContract / Contract종류 https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContract#expandable-1
    private val dataReload = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // RequestCode가 필요 없어짐
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().loadUserData(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(_binding.root)
        setupActionBar()
        mSharedPreferences =
            this.getSharedPreferences(Constants.PROJEMANAG_PREFERENCE, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        if (tokenUpdated) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this, true)
        } else {
//            FirebaseInstanceId.getInstance() // Deprecated
            FirebaseInstallations.getInstance()
                .getToken(false).addOnSuccessListener{ instanceIdResult ->
                    updateFCMToken(instanceIdResult.token)
                }
        }

        _binding.apply {
            navView.setNavigationItemSelectedListener(this@MainActivity)
            lyAppBarMain.fabCreatingBoard.setOnClickListener {
                dataReload.launch(Intent(this@MainActivity, CreateBoardActivity::class.java))
            }
        }

        FirestoreClass().loadUserData(this, true)
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {
        hideProgressDialog()
        mUserName = user.name
        // nav_header_main.xml을 어떻게 뷰 바인딩 시켜줄 지 모르겠어서 객체 잡아서 넣음
        val ll = (_binding.navView.getHeaderView(0) as LinearLayout).apply {
            (getChildAt(0) as CircleImageView).also {
                Glide
                    .with(this@MainActivity)
                    .load(user.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(it);
            }
            (getChildAt(1) as TextView).also {
                it.text = user.name
            }
        }
        if (readBoardsList) {
           showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()

        _binding.lyAppBarMain.lyMainContent.apply {
            if (boardsList.size > 0) {
                tvNoBoardsAvailable.visibility = View.GONE
//                val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
                rvBoardsList.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    setHasFixedSize(true)
                    adapter = BoardItemsAdapter(this@MainActivity, boardsList).apply {
                        setOnclickListener(object: BoardItemsAdapter.OnClickListener{
                            override fun onClick(position: Int, model: Board) {
                                startActivity(
                                    Intent(this@MainActivity, TaskListActivity::class.java)
                                        .putExtra(Constants.DOCUMENT_ID, model.documentId)
                                )
                            }
                        })
                    }
                }
            } else {
                rvBoardsList.visibility = View.GONE
                tvNoBoardsAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun setupActionBar() {
        _binding.lyAppBarMain.toolbarMainActivity.apply {
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_action_navigation_menu)
            setNavigationOnClickListener {
                // Toggle drawer
                toggleDrawer()
            }
        }
    }

    private fun toggleDrawer() {
        _binding.drawerLayout.apply {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onBackPressed() {
        _binding.drawerLayout.apply {
            if (isDrawerOpen(GravityCompat.START)) {
                closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                dataReload.launch(Intent(this@MainActivity, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                Intent(this@MainActivity, IntroActivity::class.java).apply {
                    // 현재 액티비티를 top으로 하는 모든 엑티비티 종료, 다음 액티비티 실행 시 새로운 TASK에서 실행시킴
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(this)
                }
                finish()
            }
        }
        _binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }

    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }
}