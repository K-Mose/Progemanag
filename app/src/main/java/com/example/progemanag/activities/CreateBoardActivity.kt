package com.example.progemanag.activities

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.progemanag.R
import com.example.progemanag.databinding.ActivityCreateBoardBinding
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.models.Board
import com.example.progemanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.Exception

class CreateBoardActivity : BaseActivity() {
    private lateinit var _binding: ActivityCreateBoardBinding

    private var mSelectedImageFileUri: Uri? = null
    private var isCreate = true

    private lateinit var mUserName: String
    private var mBoardDetail: Board? = null
    private var mBoardImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupActionbar()

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetail = intent.getParcelableExtra(Constants.BOARD_DETAIL)
            setupBoard(mBoardDetail!!)
            _binding.btnModify.setOnClickListener {
                if (mSelectedImageFileUri != null) {
                    uploadBoardImage()
                } else {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    modifyBoard()
                }
            }
        } else {
            if (intent.hasExtra(Constants.NAME)) {
                mUserName = intent.getStringExtra(Constants.NAME)!!
            }
            _binding.apply {
                btnCreate.setOnClickListener {
                    if(mSelectedImageFileUri != null) {
                        uploadBoardImage()
                    } else {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        createBoard()
                    }
                }
            }
        }
        _binding.ivBoardImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@CreateBoardActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(imageResult)
            } else {
                ActivityCompat.requestPermissions(
                    this@CreateBoardActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    private fun createBoard() {
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        var board = Board(
                _binding.etBoardName.text.toString(),
                mBoardImageUrl,
                mUserName,
                assignedUserArrayList)
        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null) {
            // FireStore에 저장
            val sRef: StorageReference = FirebaseStorage
                    .getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                Log.e("Board Image URL:", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    mBoardImageUrl = uri.toString()
                    if (isCreate)
                        createBoard()
                    else
                        modifyBoard()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@CreateBoardActivity, exception.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
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

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data!!.data != null) {
            mSelectedImageFileUri = result.data!!.data
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
        } else {
            mSelectedImageFileUri = null
        }
    }

    private fun modifyBoard() {
        _binding.apply {
            val board = Board(
                etBoardName.text.toString(),
                mBoardImageUrl,
                mBoardDetail!!.createdBy,
                mBoardDetail!!.assignedBy,
                intent.getStringExtra(Constants.DOCUMENT_ID)!!,
                mBoardDetail!!.taskList
            )
            FirestoreClass().modifyBoard(this@CreateBoardActivity, board)
        }
    }
    private fun setupBoard(board: Board) {
        isCreate = false
        _binding.apply {
            Glide
                .with(this@CreateBoardActivity)
                .load(board.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(ivBoardImage)
            etBoardName.setText(board.name)
            btnCreate.visibility = View.GONE
            btnModify.visibility = View.VISIBLE
        }
        mUserName = board.createdBy
        mBoardImageUrl = board.image
    }
}
