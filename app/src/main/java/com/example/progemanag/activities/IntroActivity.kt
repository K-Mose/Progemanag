package com.example.progemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.progemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var _binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(_binding.root)

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