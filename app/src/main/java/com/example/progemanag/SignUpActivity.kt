package com.example.progemanag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progemanag.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var _binding: ActivitySignUpBinding
    val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionbar()

    }

    private fun setupActionbar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar

        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}