package com.kodakalaris.advisor.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.activities.HomeActivity

/**
 * helper class to create notification
 * fetch vibration, volumen & donotdisturb value to create notification according to that
 */
class NotificationHelper(private val mContext: Context) {
    /**
     * Create and push the notification
     */
    fun createNotification(title: String?, message: String?) {
        val vibration: Boolean = SharedPreferenceHelper.getSharedPreferenceBoolean(
            mContext,
            SharedPreferenceHelper.KEY_VIBRATION,
            true
        )
        val tone: Boolean = SharedPreferenceHelper.getSharedPreferenceBoolean(
            mContext,
            SharedPreferenceHelper.KEY_TONE,
            true
        )
        val donotdisturb: Boolean = SharedPreferenceHelper.getSharedPreferenceBoolean(
            mContext,
            SharedPreferenceHelper.KEY_DONOTDISTURB,
            false
        )
        val msgcount: Int = SharedPreferenceHelper.getSharedPreferenceInt(
            mContext,
            SharedPreferenceHelper.KEY_MESSAGECOUNT,
            0
        ) + 1
        SharedPreferenceHelper.setSharedPreferenceInt(
            mContext,
            SharedPreferenceHelper.KEY_MESSAGECOUNT,
            msgcount
        )
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyIntent = Intent(mContext, HomeActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val notifyPendingIntent =
            PendingIntent.getActivity(mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationChannelId =
            chooseNotificationChannel(mNotificationManager, donotdisturb, vibration, tone)

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(mContext, notificationChannelId)
        mBuilder.setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setAutoCancel(true)
            .setNumber(msgcount)
            .setGroup(Constants.APP.GROUP_KEY_WORK_EMAIL)
            .setGroupSummary(true)
            .setContentIntent(notifyPendingIntent)
        if (msgcount == 1) {
            mBuilder.setStyle(NotificationCompat.InboxStyle().addLine(message))
        } else if (msgcount > 1) {
            mBuilder.setStyle(
                NotificationCompat.InboxStyle().setBigContentTitle(
                    String.format(
                        mContext.resources.getString(R.string.notification_summary),
                        msgcount
                    )
                )
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(notificationChannelId)
        } else {
            val uri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )
            when (chooseNotificationChannel(donotdisturb, vibration, tone)) {
                Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID -> {
                    mBuilder.setPriority(NotificationCompat.PRIORITY_LOW)
                }
                Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID -> {
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                    mBuilder.setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                    mBuilder.setSound(null)
                }
                Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID -> {
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                    mBuilder.setVibrate(longArrayOf(0))
                    mBuilder.setSound(uri)
                }
                Constants.NOTIFICATION.NORMAL_CHANNEL_ID -> {
                    mBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                    mBuilder.setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                    mBuilder.setSound(uri)
                }
            }
        }
        mNotificationManager.notify(
            Constants.APP.SUMMARY_ID /* Request Code */,
            mBuilder.build()
        )
    }

    private fun chooseNotificationChannel(
        donotdisturb: Boolean,
        vibration: Boolean,
        tone: Boolean
    ): String {
        if (donotdisturb) {
            return Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID
        } else if (!vibration && !tone) {
            return Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID
        } else if (vibration && !tone) {
            return Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID
        } else if (!vibration && tone) {
            return Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID
        } else if (vibration && tone) {
            return Constants.NOTIFICATION.NORMAL_CHANNEL_ID
        }
        return Constants.NOTIFICATION.NORMAL_CHANNEL_ID
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun chooseNotificationChannel(
        mNotificationManager: NotificationManager?,
        donotdisturb: Boolean,
        vibration: Boolean,
        tone: Boolean
    ): String {
        if (donotdisturb) {
            val notificationChannel =
                mNotificationManager!!.getNotificationChannel(Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID)
            if (notificationChannel == null) {
                createDonotdisturbChannel()
            }
            return Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID
        } else if (!vibration && !tone) {
            val notificationChannel =
                mNotificationManager!!.getNotificationChannel(Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID)
            if (notificationChannel == null) {
                createDonotdisturbChannel()
            }
            return Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID
        } else if (vibration && !tone) {
            val notificationChannel =
                mNotificationManager!!.getNotificationChannel(Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID)
            if (notificationChannel == null) {
                createOnlyvibrationChannel()
            }
            return Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID
        } else if (!vibration && tone) {
            val notificationChannel =
                mNotificationManager!!.getNotificationChannel(Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID)
            if (notificationChannel == null) {
                createOnlyvolumeChannel()
            }
            return Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID
        } else if (vibration && tone) {
            val notificationChannel =
                mNotificationManager!!.getNotificationChannel(Constants.NOTIFICATION.NORMAL_CHANNEL_ID)
            if (notificationChannel == null) {
                createNormalChannel()
            }
            return Constants.NOTIFICATION.NORMAL_CHANNEL_ID
        }
        return Constants.NOTIFICATION.NORMAL_CHANNEL_ID
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createDonotdisturbChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_ID,
            Constants.NOTIFICATION.DONOTDISTURB_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.setBypassDnd(false)
        notificationChannel.lockscreenVisibility = NotificationManager.IMPORTANCE_LOW
        notificationChannel.setShowBadge(true)
        notificationChannel.enableVibration(false)
        val mNotificationManager =
            (mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.createNotificationChannel(notificationChannel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createOnlyvibrationChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_ID,
            Constants.NOTIFICATION.ONLYVIBRATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setBypassDnd(true)
        notificationChannel.lockscreenVisibility = NotificationManager.IMPORTANCE_HIGH
        notificationChannel.setShowBadge(true)
        notificationChannel.setSound(null, notificationChannel.audioAttributes)
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val mNotificationManager =
            (mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.createNotificationChannel(notificationChannel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createOnlyvolumeChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_ID,
            Constants.NOTIFICATION.ONLYVOLUME_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setBypassDnd(true)
        notificationChannel.lockscreenVisibility = NotificationManager.IMPORTANCE_HIGH
        notificationChannel.setShowBadge(true)
        notificationChannel.enableVibration(false)
        val mNotificationManager =
            (mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.createNotificationChannel(notificationChannel)
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNormalChannel() {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION.NORMAL_CHANNEL_ID,
            Constants.NOTIFICATION.NORMAL_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setBypassDnd(true)
        notificationChannel.lockscreenVisibility = NotificationManager.IMPORTANCE_HIGH
        notificationChannel.setShowBadge(true)
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val mNotificationManager =
            (mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        mNotificationManager.createNotificationChannel(notificationChannel)
    }
}