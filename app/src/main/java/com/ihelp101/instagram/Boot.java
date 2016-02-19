package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Boot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            startErrorLog("XInsta Version: " +pInfo.versionName);
        } catch (Exception e) {

        }
    }

    public void startErrorLog(String status) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());

            status = time + " - " + status;

            File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Error.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(status);
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }
}
