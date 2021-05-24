package com.example.progemanag.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMainBinding
import com.example.progemanag.databinding.NavHeaderMainBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var _binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionBar()

        _binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().signInUser(this)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                Toast.makeText(this@MainActivity,
                "My Profile", Toast.LENGTH_SHORT).show()
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