package com.example.progemanag

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.progemanag.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityIntroBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeFace: Typeface = Typeface.createFromAsset(assets, "uni-sans.heavy-caps.otf")
        binding.tvAppNameIntro.typeface = typeFace
    }
}