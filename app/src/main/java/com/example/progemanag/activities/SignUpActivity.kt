 package com.example.progemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity() {
    private lateinit var _binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar(_binding.toolbarSignUpActivity)

        _binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name: String = _binding.etName.text.toString().trim { it <= ' '}
        val email: String = _binding.etEmail.text.toString().trim { it <= ' '}
        val password: String = _binding.etPassword.text.toString().trim { it <= ' '}

        if( validateForm(name, email, password)){
            Toast.makeText(
                this@SignUpActivity,
                "Now we can register a new user.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when{
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter a Name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter a Email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter a PassWord")
                false
            }
            else -> true
        }
    }
}