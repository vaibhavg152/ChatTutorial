package com.example.vaibhav.chattutorial.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.vaibhav.chattutorial.R;

public class MyNotificationManager {

    private static final int REQUEST_CODE_NOTIFICATION = 1234;
    private Context context;

    public MyNotificationManager(Context context) {
        this.context = context;
    }

    public void showNotification(String from, String content, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(context,REQUEST_CODE_NOTIFICATION,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setAutoCancel(true).setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_notific)
                .setContentTitle(from).setContentText(content).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(REQUEST_CODE_NOTIFICATION,notification);

    }
}
