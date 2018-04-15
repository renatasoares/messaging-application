package com.messaginapp.messaging_application.service;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Notification", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d("Notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
