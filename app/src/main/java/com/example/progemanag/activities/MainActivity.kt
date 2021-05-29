package com.example.progemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMainBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    private lateinit var _binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionBar()

        _binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)
    }

    fun updateNavigationUserDetails(user: User) {
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

    // Change to registerForActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                //TODO Change startActivityForResult to registerForActivityResult
                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java), MainActivity.MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
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
}