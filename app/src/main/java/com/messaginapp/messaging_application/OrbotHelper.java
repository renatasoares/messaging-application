package com.messaginapp.messaging_application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class OrbotHelper{

    private static final String appPackageName = "org.torproject.android";

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isOrbotRunning(ActivityManager activityManager) {
        List<ActivityManager.RunningServiceInfo> procInfos = activityManager.getRunningServices(100000);

        for (int i = 0; i < procInfos.size(); i++) {
            Log.d("Proc", procInfos.get(i).service.getPackageName());
            if (procInfos.get(i).service.getPackageName().equals(appPackageName)){
                Log.d("Orbot", procInfos.get(i).service.getPackageName());
                return true;
            }
        }

        return false;
    }
}
