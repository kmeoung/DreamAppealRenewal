package com.truevalue.dreamappeal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityIntro
import com.truevalue.dreamappeal.bean.BeanPushMsg
import org.json.JSONException
import org.json.JSONObject

class ServiceFirebaseMsg : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebaseService"
        const val FIREBASE_NORIFICATION_CALLED = "FIREBASE NORIFICATION CALLED"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "new Token: $p0")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if (remoteMessage.data != null) {
            Log.d(TAG, "Notification Message Data: ${remoteMessage.data}")
            Log.d(TAG, "Notification Message Noti: ${remoteMessage.notification.toString()}")
            Log.d(TAG, "Notification Message: ${remoteMessage}")

            val json = JSONObject()

            for(key in remoteMessage.data.keys){
                val value = remoteMessage.data[key]
                json.put(key,value)
            }
            sendNotification(json,remoteMessage.messageId!!)
        }
    }

    private fun sendNotification(body: JSONObject?,id : String) {
        Log.e(TAG,"body : $body")
        var json: JSONObject?
        var bean : BeanPushMsg? = null
        try {
            json = body
            bean = Gson().fromJson<BeanPushMsg>(json.toString(), BeanPushMsg::class.java)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        bean?.let {
            val intent = Intent(this, ActivityIntro::class.java).apply {
                putExtra(FIREBASE_NORIFICATION_CALLED, it.contents_bold)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            val CHANNEL_ID = id
            val CHANNEL_NAME = getString(R.string.app_name)
            val description = "${it.contents_bold}${it.contents_regular}"
            val importance = NotificationManager.IMPORTANCE_HIGH

            var notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
                channel.description = description
                channel.enableLights(true)
                channel.lightColor = Color.RED
                channel.enableVibration(true)
                channel.setShowBadge(true)
                notificationManager.createNotificationChannel(channel)
            }

            var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) R.mipmap.ic_launcher else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) R.drawable.ic_noti else R.mipmap.ic_launcher
            var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(applicationContext,R.color.azure))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle(CHANNEL_NAME)
                .setContentText(description)
                .setAutoCancel(false)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)

            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}