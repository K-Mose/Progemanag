package com.example.progemanag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progemanag.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }

    private fun setupActionBar(){
        binding.apply {
            setSupportActionBar(toolbarSignInActivity)
            val actionbar = supportActionBar
            actionbar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            }
            toolbarSignInActivity.setNavigationOnClickListener {
                onBackPressed()
            }
        }

    }
}