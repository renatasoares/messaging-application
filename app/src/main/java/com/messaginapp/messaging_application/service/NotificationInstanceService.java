package com.messaginapp.messaging_application.service;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.messaginapp.messaging_application.model.NotificationTokenDO;

public class NotificationInstanceService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private FirebaseAuth firebaseAuth;
    DynamoDBMapper dynamoDBMapper;

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        AWSMobileClient.getInstance().initialize(this).execute();

        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.getCurrentUser().reload();

        final NotificationTokenDO tokenNotification = new NotificationTokenDO(firebaseAuth.getCurrentUser().getUid(), token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(tokenNotification);
            }
        }).start();

    }
}
