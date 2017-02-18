package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.app.NotificationCompat;

import de.robv.android.xposed.XposedHelpers;

public class Update extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;

        int skip = 0;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCheck = Integer.parseInt(intent.getStringExtra("Version").replaceAll("\\.", ""));
            int currentVersion = Integer.parseInt(pInfo.versionName.replaceAll("\\.", ""));

            if (currentVersion > versionCheck) {
                skip = 1;
            }
        } catch (Throwable t) {
        }

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("XInsta " +intent.getStringExtra("Version")+ " Update").setContentText("Click To Update XInsta").setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true);

        Intent notificationIntent = new Intent(context, Main.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("Update", "Yes");

        Intent dismissIntent = new Intent(context, Dismiss.class);
        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        mBuilder.setDeleteIntent(PendingIntent.getBroadcast(context.getApplicationContext(), 0, dismissIntent, 0));

        try {
            long dismissCount = System.currentTimeMillis() - Long.parseLong(Helper.getSetting("Dismiss"));
            dismissCount = dismissCount / 1000;

            if (skip == 0 && dismissCount > 86400) {
                mNotifyManager.notify(4838, mBuilder.build());
            }
        } catch (Throwable t) {
            mNotifyManager.notify(4838, mBuilder.build());
        }
    }
}
