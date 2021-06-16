package com.example.progemanag.java

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.progemanag.R
import com.example.progemanag.activities.BaseActivity
import com.example.progemanag.activities.MainActivity
import com.example.progemanag.activities.SignInActivity
import com.example.progemanag.activities.TaskListActivity
import com.example.progemanag.firebase.FirestoreClass
import com.example.progemanag.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.reflect.KClass

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FROM: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: $it")

            var title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!
            val method = remoteMessage.data[Constants.FCM_METHOD]!!

            if (method == Constants.BOARDS)
                sendNotification(title, message) // 유저에게 보내는 알림
            else {
                cardNotification(title, message, remoteMessage.data[Constants.DOCUMENT_ID]!!)
            }
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed Token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // ..
    }

    private fun sendNotification(title:String, message: String) {
        // send에서 intent를 introActivity로 설정하는 것이 말도 안됨. 삭제, Main에서 instance확인 후 인텐트 활성화로 변경
        val intent = Intent(this, MainActivity::class.java)
        /*
        알림이 탭에 반응하여 액티비티를 열기 위해 pendingIntent를 설정한다.
        pedingIntent는 알림의 setContent에 전달되어진다.
         */
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this, channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        /*
        채널 생성하기
        채널은 사용되는 알림의 그룹이라 볼 수 있다.
        각각 설정된 채널을 통해서 특정 알림을 송수신 하고, 사용자가 특정 채널을 on/off 가능하다.
        API 26 이상에서 호환성 유지를 위해 Channel ID 제공해야 함.
        ChannelId - https://www.howtogeek.com/715614/what-are-android-notification-channels/
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*
            채널을 시스템에 등록
            NotificationChannel 생성자에는 Importance가 필요함.
            채널에 속하는 모든 알림을 전달하는 방법이 결정됨.
            Android 7.1 이하에서는 setPriority() 사용해서 우선순위 설정
             */
            val channel = NotificationChannel(channelId,
                "Channel Projemanag title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        // 알림 표시, notificationBuilder.build()의 결과를 전달함
        // notificationId는 유일한 Int형으로 개별의 알림에 구성되어야 함
        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun cardNotification(title: String, message: String, documentId: String) {
        val intent = Intent(this, TaskListActivity::class.java)
            .putExtra(Constants.DOCUMENT_ID, documentId)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent) // Add the intent
            getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }
        val channelId = Constants.CHANNEL_CARD
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_stat_ic_notification)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setContentTitle(title)
            setContentText(message)
            setSound(defaultSound)
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Card Notification",
            NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, builder.build())
    }


    companion object {
        const val TAG = "MyFirebaseMsgService"
    }
}
/*
Start an Activity from a Notification
탐색 활동(Navigation Experience)을 유지하기 위해서 활동을 새로운 작업에서 실행시켜야 한다.
Regular Activity
  이러한 활동은 앱의 일반 UX 흐름에 존재한다. 그러므로 유저가 알림을 통해 활동으로 들어왔을 때,
  새로운 작업은 완전한 백스택을 포함해야 하고, 백 버튼을 눌렀을 때 앱의 계층위로 가는 것을 허락한다.

Special Activity
  활동이 알림에서 시작되었다면, 유저는 오직 이 활동밖에 보지 못한다. 어떤 의미에서, 이러한 활동은
  알림에 표시하기 어려운 정보를 제공함으로서 알림 UI를 확장한다. 그래서 이러한 활동은 백 스택이 필요 없다.

Set up a regular activity PendingIntent
Regular activity를 실행하기 위해, 아래와 같은 백 스택을 생성하기 위해
TaskStackBuilder를 사용한 PedingIntent를 설정해야 한다.

Define your app's Activity hierarchy
Manifest파일에서 <activity>에 parentActivityName 속성을 추가해준다.

 */