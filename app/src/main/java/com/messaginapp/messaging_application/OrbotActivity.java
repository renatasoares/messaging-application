package com.messaginapp.messaging_application;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import static com.messaginapp.messaging_application.OrbotHelper.isOrbotRunning;

public class OrbotActivity extends AppCompatActivity {

    private Button orbotButton;
    private boolean orbotInstalled;
    private static final String appPackageName = "org.torproject.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbot);

        final ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        orbotButton = (Button) findViewById(R.id.orbotButton);

        orbotInstalled = OrbotHelper.isAppInstalled(OrbotActivity.this, appPackageName);

        if(orbotInstalled) {
            boolean started = isOrbotRunning(activityManager);
            Log.d("Ola", "" + started);
            if (started) {
                Intent intent = new Intent(OrbotActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent i = new Intent();
                PackageManager managerclock = getPackageManager();
                i = managerclock.getLaunchIntentForPackage(appPackageName);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
                finish();
            }
        }

        orbotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!orbotInstalled){
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    finish();
                }
            }
        });
    }
}
