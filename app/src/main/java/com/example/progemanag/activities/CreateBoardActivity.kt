package com.example.progemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityCreateBoardBinding
import com.example.progemanag.utils.Constants
import java.lang.Exception

class CreateBoardActivity : BaseActivity() {
    private lateinit var _binding: ActivityCreateBoardBinding

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        //
        _binding.apply {
            ivBoardImage.setOnClickListener {
                if(ContextCompat.checkSelfPermission(this@CreateBoardActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(this@CreateBoardActivity)
                } else {
                    ActivityCompat.requestPermissions(
                            this@CreateBoardActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        finish()
    }

    // 순서좀 잘 기억해라;;;
    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarCreateBoard)
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
//            title = resources.getString(R.string.create_board_title)
            title = intent.getStringExtra(Constants.NAME)
        }
        _binding.toolbarCreateBoard.setNavigationOnClickListener {
            superOnBackPressed()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
                && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                        .with(this@CreateBoardActivity)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(_binding.ivBoardImage)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

}