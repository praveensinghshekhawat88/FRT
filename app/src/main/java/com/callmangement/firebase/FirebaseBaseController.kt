package com.callmangement.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.callmangement.R
import com.callmangement.network.AppConfig

/**
 * Created and updated by Kelly George on 7/12/2018.
 * All reserved by Kelly (c) 2017
 */
open class FirebaseBaseController {
    companion object {
        fun toNewTask(intent: Intent): Intent {
            intent.addFlags( /*Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |*/
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
            return intent
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun showNotification(context: Context, intent: Intent?, title: String?, message: String?) {
            try {
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(
                        context,
                        10,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                val defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = AppConfig.PACKAGE_NAME
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelName = "Channel One"
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val notificationChannel =
                        NotificationChannel(channelId, channelName, importance)
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.BLUE
                    notificationChannel.setShowBadge(true)
                    notificationManager?.createNotificationChannel(notificationChannel)
                }
                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH) // or NotificationCompat.PRIORITY_MAX

                notificationManager?.notify(
                    System.currentTimeMillis().toInt(),
                    notificationBuilder.build()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
