package com.example.progemanag.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMyProfileBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception

class MyProfileActivity : BaseActivity() {
    private lateinit var _binding: ActivityMyProfileBinding

    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetail: User
    private var mProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar()
        FirestoreClass().loadUserData(this)

        _binding.apply {
            ivMyProfile.setOnClickListener {
                if(ContextCompat.checkSelfPermission(this@MyProfileActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(imageResult)
                } else {
                    ActivityCompat.requestPermissions(
                            this@MyProfileActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
            btnUpdate.setOnClickListener {
                if (mSelectedImageFileUri != null ) uploadUserImage()
                else {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    updateUserProfileData()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(imageResult)
            } else {
                Toast.makeText(
                        this@MyProfileActivity,
                        "Oops, you just denied the permission for storage.",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data!!.data != null) {
            mSelectedImageFileUri = result.data!!.data
            try {
                Glide
                        .with(this@MyProfileActivity)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(_binding.ivMyProfile)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
                && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                        .with(this@MyProfileActivity)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(_binding.ivMyProfile)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(_binding.toolbarProfileActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            title = resources.getString(R.string.my_profile)
            _binding.toolbarProfileActivity.setNavigationOnClickListener {
                superOnBackPressed()
            }
        }

    }

    fun setUserDataInUI(user: User) {
        // 유져 UI 업데이트 시 mUserDetail 할당
        mUserDetail = user
        _binding.apply {
            Glide
                .with(this@MyProfileActivity)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(ivMyProfile)
            etName.setText(user.name)
            etEmail.setText(user.email)
            if (user.mobile != 0L) {
                etMobile.setText(user.mobile.toString())
            }
        }
    }

    // HashMap 생성 후 변경 데이터 업데이트
    private fun updateUserProfileData() {
        // Preparing HashMap & change Observer
        val userHashMap = HashMap<String, Any>()
        var anyChangesMade = false
        // Set Variables
        // 각각의 값을 확인 할 수 있도록 여러 if문으로 작성
        if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetail.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
            anyChangesMade = true
        }
        if (_binding.etName.text.toString() != mUserDetail.name) {
            userHashMap[Constants.NAME] = _binding.etName.text.toString()
            anyChangesMade = true
        }
        if (_binding.etMobile.text.toString() != mUserDetail.mobile.toString()) {
            userHashMap[Constants.MOBILE] = _binding.etMobile.text.toString().toLong()
            anyChangesMade = true
        }

        if (anyChangesMade) FirestoreClass().updateUserProfileData(this, userHashMap)
        else hideProgressDialog()
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null) {
            // FireStore에 저장
            val sRef: StorageReference = FirebaseStorage
                    .getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL:", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        Log.e("Finish", "${this.localClassName} || ${this.javaClass.simpleName}")
        finish()
    }

}