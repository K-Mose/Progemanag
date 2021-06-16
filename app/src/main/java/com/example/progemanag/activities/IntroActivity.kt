package com.example.progemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.progemanag.databinding.ActivityIntroBinding
import com.example.progemanag.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : BaseActivity() {
    private lateinit var _binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        Log.e("Sign_out1:","${FirebaseAuth.getInstance().currentUser}")
        if (FirestoreClass().getCurrentUserID().isNotEmpty()) {
            Toast.makeText(this, "인스턴스있음", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "인스턴스없음", Toast.LENGTH_SHORT).show()
        }
        _binding.tvAppNameIntro.typeface = typeFace

        _binding.apply {
            btnSignUpIntro.setOnClickListener {
                startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
            }
            btnSignInIntro.setOnClickListener {
                startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
            }
        }
    }
}