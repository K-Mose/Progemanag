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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityMyProfileBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception

class MyProfileActivity : BaseActivity() {
    private lateinit var _binding: ActivityMyProfileBinding

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private var mSelectedImageFileUri: Uri? = null
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
                    showImageChooser()
                } else {
                    ActivityCompat.requestPermissions(
                            this@MyProfileActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
            btnUpdate.setOnClickListener {
                uploadUserImage()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                        this@MyProfileActivity,
                        "Oops, you just denied the permission for storage.",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE
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
                onBackPressed()
            }
        }

    }

    fun setUserDataInUI(user: User) {
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

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null) {
            // FireStore에 저장
            val sRef: StorageReference = FirebaseStorage
                    .getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL:", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()

                    //TODO update user profile data
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

}