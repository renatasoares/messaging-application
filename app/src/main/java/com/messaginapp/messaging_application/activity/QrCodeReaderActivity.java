package com.messaginapp.messaging_application.activity;

import android.*;
import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.messaginapp.messaging_application.R;

import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
import static me.dm7.barcodescanner.core.CameraUtils.getCameraInstance;

public class QrCodeReaderActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    DynamoDBMapper dynamoDBMapper;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_qr_code_reader);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

    }

    public void startCamera()
    {
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    public void stopCamera()
    {
        mScannerView.stopCamera();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        new GetUser().execute(rawResult.getText());

        //I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(QrCodeReaderActivity.this);
            }
        }, 2000);
    }


    private class GetUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            AWSMobileClient.getInstance().initialize(getApplicationContext()).execute();

            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());

            Map<String, AttributeValue> identifier = new HashMap<String, AttributeValue>();
            AttributeValue value = new AttributeValue(params[0]);
            identifier.put("qrCode", value);

            GetItemResult result = null;

            try {
                result = dynamoDBClient.getItem("appmessaging-mobilehub-742744033-token", identifier);
            } catch (AmazonServiceException e) {
                Log.d("RENATA", e.getErrorMessage());

            }

            Log.d("RENATAS", result.toString());

            return result.toString();
        }

    }
}
