package com.example.progemanag.activities

import android.app.Dialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityBaseBinding
import com.example.progemanag.databinding.DialogProgressBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * 함수 및 값들을 재사용 할 수 있는 BaseActivity를 생성
 *
 */
open class BaseActivity : AppCompatActivity() {

    private lateinit var _typeFace: Typeface
    val typeFace get() = _typeFace
    lateinit var binding: ActivityBaseBinding
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _typeFace = Typeface.createFromAsset(assets, "uni-sans.heavy-caps.otf")
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun setupActionbar(toolbar: androidx.appcompat.widget.Toolbar){
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun showProgressDialog(text: String){
        val progressBinding: DialogProgressBinding = DialogProgressBinding.inflate(LayoutInflater.from(this))
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(progressBinding.root)
        progressBinding.tvProgressText.text = text

        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    /**
     *
     */
    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    /**
     * 두 번 연속 백 버튼을 눌렀을 때 뒤로가기 허용
     */
    private fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()
        // 2초 이내에 연속 두번 누르지 않으면 값 초기화
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        },2000)
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG).apply {
            view.apply {
                setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.snackbar_error_color))
            }
        }
        snackBar.show()
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
    fun superOnBackPressed() {
        super.onBackPressed()
    }
}