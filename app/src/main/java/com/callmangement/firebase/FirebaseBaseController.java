package com.callmangement.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.callmangement.Network.AppConfig;
import com.callmangement.R;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.LocationRequest;

/**
 * Created and updated by Kelly George on 7/12/2018.
 * All reserved by Kelly (c) 2017
 */

public class FirebaseBaseController {

    public static Intent toNewTask(Intent intent){
        intent.addFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |*/ Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void showNotification(Context context, Intent intent , String title, String message) {
        try {
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_MUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = AppConfig.PACKAGE_NAME;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelName = "Channel One";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setShowBadge(true);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH); // or NotificationCompat.PRIORITY_MAX

            if (notificationManager != null) {
                notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
