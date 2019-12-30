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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityIntro
import com.truevalue.dreamappeal.http.*
import com.truevalue.dreamappeal.utils.Comm_Param
import okhttp3.Call

class ServiceFirebaseMsg : FirebaseMessagingService(){

    private val TAG = "FirebaseService"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "new Token: $p0")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        if(remoteMessage.data != null) {
            Log.d(TAG, "Notification Message Data: ${remoteMessage.data}")
            sendNotification(remoteMessage.data.toString())
        }
    }

    private fun sendNotification(body: String?) {
        val intent = Intent(this, ActivityIntro::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }

        val CHANNEL_ID = "CollocNotification"
        val CHANNEL_NAME = "CollocChannel"
        val description = "This is Colloc channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        var notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }

        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Colloc Notification")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(notificationSound)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, notificationBuilder.build())
    }


}