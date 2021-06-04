package com.example.progemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

object Constants {

    const val USER: String = "users"

    const val BOARDS: String = "boards"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"

    const val ASSIGNED_TO: String = "assignedBy"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"

    const val BOARD_DETAIL: String = "board_detail"

    const val ID: String = "id"
    fun showImageChooser(register: ActivityResultLauncher<Intent>) {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        register.launch(galleryIntent)
    }

    // 확장자 받아오는 함수
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}