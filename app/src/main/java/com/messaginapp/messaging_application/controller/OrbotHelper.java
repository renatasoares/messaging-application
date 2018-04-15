package com.messaginapp.messaging_application.controller;

import android.content.Context;
import android.content.pm.PackageManager;

public class OrbotHelper {

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}



