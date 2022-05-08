package com.example.tattoosalon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "appointment_not_channel";
    private final int NOTIFICATION_ID = 0;

    private NotificationManager mNotificationManager;
    private Context mContext;

    public NotificationHandler(Context mContext) {
        this.mContext = mContext;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "Shop Notification", NotificationManager.IMPORTANCE_HIGH);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Notifications from Tattoo Salon app");

        mNotificationManager.createNotificationChannel(channel);
    }

    public void send(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Appointment")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_date);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
