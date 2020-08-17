package com.kodakalaris.advisor.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.application.AppController
import com.kodakalaris.advisor.eventbus.AppEvents.OnNotificationEvent
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.generateLogsOnStorage
import com.kodakalaris.advisor.utils.NotificationHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.getSharedPreferenceBoolean
import com.kodakalaris.advisor.utils.SharedPreferenceHelper.setSharedPreferenceString
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Firebase Messaging service class to handle fcm messages
 */
class PushMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val msg = remoteMessage.data["message"]
        Log.e(
            Constants.EventLog.TAG_Notification_Create_Event + "[" + Date().toString() + "]",
            remoteMessage.data.toString() + "\n"
        )
        val notificationFlag = getSharedPreferenceBoolean(
            applicationContext,
            SharedPreferenceHelper.KEY_NOTIFICATION,
            true
        )
        if (!AppController.isAppIsInForeground && msg != null && !msg.isEmpty()) { //Convert notification data into json
            try {
                val `object` = JSONObject()
                for ((key, value) in remoteMessage.data) {
                    `object`.put(key, value)
                }
                generateLogsOnStorage("Push Notification Data")
                generateLogsOnStorage(`object`.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (notificationFlag) {
                val notificationHelper = NotificationHelper(applicationContext)
                notificationHelper.createNotification(resources.getString(R.string.app_name), msg)
            }
        } else if (AppController.isAppIsInForeground) {
            //Convert notification data into json
            try {
                val `object` = JSONObject()
                for ((key, value) in remoteMessage.data) {
                    `object`.put(key, value)
                }
                generateLogsOnStorage("Push Notification Data")
                generateLogsOnStorage(`object`.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            EventBus.getDefault().post(OnNotificationEvent())
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        setSharedPreferenceString(applicationContext, SharedPreferenceHelper.KEY_FCMTOKEN, s)
    }

    companion object {
        private val TAG = PushMessagingService::class.java.simpleName
    }
}