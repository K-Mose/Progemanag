package com.example.progemanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivitySignInBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private lateinit var _binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar(_binding.toolbarSignInActivity)
        auth = FirebaseAuth.getInstance()

        _binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    private fun signInRegisteredUser(){
        val email: String = _binding.etEmail.text.toString().trim{ it <= ' '}
        val password: String = _binding.etPassword.text.toString().trim{ it <= ' '}
        if (validateForm(email, password)) {
            showProgressDialog(getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {  task ->
                        if(task.isSuccessful) {
                            Log.d("Sign in :", "signInWithEmail:success")
                            FirestoreClass().loadUserData(this)
                        } else {
                            Log.w("Sign in :", "signInWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            hideProgressDialog()
                        }
                    }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when{
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