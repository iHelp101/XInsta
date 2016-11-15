package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Update extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("XInsta " +intent.getStringExtra("Version")+ " Update").setContentText("Click To Update XInsta").setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true);

        Intent notificationIntent = new Intent(context, Main.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("Update", "Yes");

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mNotifyManager.notify(4838, mBuilder.build());
    }
}
