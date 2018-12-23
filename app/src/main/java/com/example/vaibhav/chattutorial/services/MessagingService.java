package com.example.vaibhav.chattutorial.services;

import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

import com.example.vaibhav.chattutorial.ChatActivity;
import com.example.vaibhav.chattutorial.util.MyNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.firebase.ui.auth.AuthUI.TAG;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: ");

        if (remoteMessage.getData().size() > 0){
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "onMessageReceived: "+ remoteMessage.getNotification().getBody());

            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());

            myNotificationManager.showNotification(remoteMessage.getFrom(),remoteMessage.getNotification().getBody()
            ,new Intent(getApplicationContext(),ChatActivity.class));

        }
    }

}
