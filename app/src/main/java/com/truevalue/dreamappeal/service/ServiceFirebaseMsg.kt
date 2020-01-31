package com.truevalue.dreamappeal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.truevalue.dreamappeal.R
import com.truevalue.dreamappeal.activity.ActivityMain
import com.truevalue.dreamappeal.bean.BeanPushMsg
import com.truevalue.dreamappeal.utils.Comm_Prefs
import org.json.JSONException
import org.json.JSONObject
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




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

            for (key in remoteMessage.data.keys) {
                val value = remoteMessage.data[key]
                json.put(key, value)
            }
            if (Comm_Prefs.isNotification()) setNotification(json, remoteMessage.messageId!!)
        }
    }


    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN
        return if (useWhiteIcon) R.drawable.icon_notification else R.mipmap.ic_launcher
    }

    private fun setNotification(body: JSONObject?, id: String) {
        Log.e(TAG, "body : $body")
        var json: JSONObject?
        var bean: BeanPushMsg? = null
        try {
            json = body
            bean = Gson().fromJson<BeanPushMsg>(json.toString(), BeanPushMsg::class.java)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        bean?.let {
            sendNewNotification(it,id)
        }
    }

    private fun sendNewNotification(bean: BeanPushMsg,id : String){

        val CHANNEL_ID = id
        val CHANNEL_NAME = getString(R.string.app_name)
        val requestId = System.currentTimeMillis().toInt()
        val description = "${bean.contents_bold ?: ""}${bean.contents_regular ?: ""}"

        val intent = Intent(this, ActivityMain::class.java)
        intent.putExtra(FIREBASE_NORIFICATION_CALLED,"FIREBASE_NORIFICATION_CALLED")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(ActivityMain::class.java)
        stackBuilder.addNextIntent(intent)
        val pendingIntent = stackBuilder.getPendingIntent(requestId, PendingIntent.FLAG_UPDATE_CURRENT)

//        val pendingIntent = PendingIntent.getActivity(this,requestId,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon_main))
            .setContentTitle(CHANNEL_NAME)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        var notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        builder.setSmallIcon(getNotificationIcon())
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val color = ContextCompat.getColor(this, R.color.main_blue)
            builder.color = color
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,importance)
            channel.description = description

            assert(notificationManager != null)
            notificationManager.createNotificationChannel(channel)
        }

        assert(notificationManager != null)
        notificationManager.notify(1234, builder.build())
    }
}