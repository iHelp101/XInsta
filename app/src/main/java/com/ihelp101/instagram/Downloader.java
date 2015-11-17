package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


public class Downloader extends BroadcastReceiver {
    int count;
    Context mContext;
    String getDirectory = Environment.getExternalStorageDirectory().toString();

    String linkToDownload;
    String fileName;
    String notificationTitle;
    String saveLocation;
    String Failed = "No";
    String Location;

    private int id = 1;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        linkToDownload = intent.getStringExtra("Link");
        fileName = intent.getStringExtra("File");
        notificationTitle = intent.getStringExtra("Notification");
        saveLocation = intent.getStringExtra("Save");
        saveLocation = saveLocation.replace("file://", "");

        if (saveLocation.equals("Instagram")) {
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
            File directory =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram");
            if (!directory.exists())
                directory.mkdirs();
        } else {
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        if (!getNotification().equals("Hide")) {
            mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentText(ResourceHelper.getString(mContext, R.string.DownloadDots));
            mNotifyManager.notify(id, mBuilder.build());
        }

        DownloadFromUrl(linkToDownload, fileName);
    }

    public void DownloadFromUrl(final String URL, final String fileName) {
        Thread download= new Thread() {
            public void run() {
                try {
                    URL url = new URL (URL);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(saveLocation + "/" + fileName);

                    byte data[] = new byte[1024];

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    System.out.println("Error: " +e);
                    Failed = "Yes";
                }

                if (!getNotification().equals("Hide")) {
                    if (Failed.equals("Yes")) {
                        mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Failed));
                        mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Failed));
                    } else {
                        mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Completed));
                        mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Completed));
                    }

                    mBuilder.setContentTitle(notificationTitle);
                    mBuilder.setSmallIcon(R.drawable.ic_launcher);
                    mBuilder.setAutoCancel(true);

                    Intent notificationIntent = new Intent();
                    notificationIntent.setAction(Intent.ACTION_VIEW);

                    Location = saveLocation + "/" + fileName;
                    Location = Location.replace("%20", " ");
                    Location = Location.replace("file://", "");

                    File file = new File(Location);
                    if (fileName.contains("jpg")) {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    } else {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                    }
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(contentIntent);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            }
        };
        download.start();
        Toast.makeText(mContext, ResourceHelper.getString(mContext, R.string.Download_Completed), Toast.LENGTH_SHORT).show();
    }

    public String getNotification() {
        //Notification Option Fetch
        File notification = new File(getDirectory + "/.Instagram/Notification.txt");
        String line;

        try {
            BufferedReader br = new BufferedReader(new FileReader(notification));

            line = br.readLine();
            br.close();
        }
        catch (IOException e) {
            line = "Show";
        }

        return line;
    }
}
