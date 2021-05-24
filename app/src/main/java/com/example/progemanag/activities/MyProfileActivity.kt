package com.example.progemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMyProfileBinding

class MyProfileActivity : BaseActivity() {
    private lateinit var _binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar()
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
}