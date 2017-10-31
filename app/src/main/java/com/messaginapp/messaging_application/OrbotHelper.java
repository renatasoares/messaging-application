package com.messaginapp.messaging_application;

import android.content.Context;
import android.content.pm.PackageManager;

public class OrbotHelper {
    private static final String appPackageName = "org.torproject.android";

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}



