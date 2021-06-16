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
    const val CARDS: String = "cards"
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
    const val EMAIL: String = "email"
    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"

    const val PROJEMANAG_PREFERENCE = "ProjemanagPrefs"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAA8Qr4q8U:APA91bForaZ-jDatfiIk5SL5R8rjpXXm-X2dc8_f7PyQhfLLDgS8aS45JL6nee5S7EdJgxJYPVR0CdOPWIB-a-asr-hvMZ-n-aP2P8JAsKxSMbI0FjdKRU98u049zdUk-ZOOkoQSVyC8"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    const val FCM_METHOD = "FCM_METHOD"

    // Channel
    const val CHANNEL_MEMBER = "CHANNEL_MEMBER"
    const val CHANNEL_CARD = "CHANNEL_CARD"

    fun showImageChooser(register: ActivityResultLauncher<Intent>) {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        register.launch(galleryIntent)
    }

    // 확장자 받아오는 함수
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}