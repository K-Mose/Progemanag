package com.example.progemanag.activities

import android.os.Bundle
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {
    private lateinit var _binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar(_binding.toolbarSignInActivity)
    }
}