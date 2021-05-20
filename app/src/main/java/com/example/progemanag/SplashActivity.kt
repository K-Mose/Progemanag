package com.example.progemanag

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import com.example.progemanag.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    lateinit var _binding: ActivitySplashBinding
    val binding
        get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFace: Typeface = Typeface.createFromAsset(assets, "uni-sans.heavy-caps.otf")
        binding.tvAppName.typeface = typeFace
    }
}