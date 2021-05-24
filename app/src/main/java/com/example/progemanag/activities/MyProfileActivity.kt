package com.example.progemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMyProfileBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User

class MyProfileActivity : BaseActivity() {
    private lateinit var _binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar()
        FirestoreClass().loadUserData(this)
    }

    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarProfileActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = resources.getString(R.string.my_profile)
            _binding.toolbarProfileActivity.setNavigationOnClickListener {
                onBackPressed()
            }
        }

    }

    fun setUserDataInUI(user: User) {
        _binding.apply {
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(ivMyProfile)
            etName.setText(user.name)
            etEmail.setText(user.email)
            if (user.mobile != 0L) {
                etMobile.setText(user.mobile.toString())
            }
        }


    }
}