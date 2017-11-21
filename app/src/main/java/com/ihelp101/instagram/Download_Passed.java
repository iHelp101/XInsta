package com.ihelp101.instagram;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Download_Passed extends IntentService {

    String getDirectory;
    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;
    Context mContext;

    int count = 0;
    int id = 1;

    String[] links;
    String[] fileNames;
    String[] fileTypes;
    String[] notificationTitles;
    String[] saveLocations;
    String[] userNames;
    int current = 0;

    class Download extends AsyncTask<String, String, String> {

        String link;
        String save;
        String title;

        Bitmap icon;
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int downloadFailed = 1;
        int id = 1;
        int logNotification = 0;

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                link = uri[0];
                save = uri[1];
                title = uri[2];
                downloadFailed = 1;
                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (link.contains("notification")) {
                    link = link.replaceAll("notification", "");
                    logNotification = 1;
                }

                if (link.contains("media123;")) {
                    link = link.replaceAll("media123;", "");
                }

                if (save.contains("_LiveAudio.mp4")) {
                    id = 12345;
                }

                String iconString = "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAABHNCSVQICAgIfAhkiAAAAyVJREFU\n" +
                        "eJzt2U9IVFEUBvDvzGgKbRRCDdpE0LKFbiKCcBMEbQRp4UxjWiQaWAqFlTO81EVtahG0yo1BuqgM\n" +
                        "oXYm0UaocXT6R6gJYSkWZIKT4bx32sngjHNnxnvnjXJ+y+v1vMOnPu89AwghhBBCCCGyR2489Ejv\n" +
                        "wMEi0PtM9xNwJxw822uyp60UufHQEttLttfZm+l+Byg22U86HrcevFNIQAoSkIIEpCABKUhAChKQ\n" +
                        "ggSkIAEpSEAKEpCCBKQgASlIQAoSkIIEpCABKWgfudb0DRxnRkX6h1IlAw8yrUnAEwYPKvcVl46G\n" +
                        "u878ybRuJrSPXJmxB6AhpBmTcrY1gXqA6hXbRsJrn55nWVrJq7vgwtjwXFVtXRERndBdO41v7F0/\n" +
                        "vRjsWNVd2Mg7yFP5tw/AaxO1U4gzcyByo/mnieJGAgq3tKzHbfIDMNJ0IgJ6IqGAsR+Gsf9iUcs/\n" +
                        "z8AFALapZ4AwiorYbWP1YeAdlGhxbPjL/tq6chAdNVB+ySbnVKTz/LKB2huMn4NWypa7ALzVXNb2\n" +
                        "gJumuhu/a66bxHhAM+3t/xx4fAD0nU8Y994FAy+11UsjLyfpyaBvmpjakP0RKJXxlbLf3RrqZMTo\n" +
                        "OyjRwqunH6reRA8QUL2NMstx8MnP1y7+0taYQv7uYkTsiccuE+hjjhUcZm6NBgNzWvtSyOtlNWy1\n" +
                        "xNhjNwDI/sRLeBgJBYb0d5Ve3m/zEzcbowB1ZPlt0XgJdxppSMGVcceEPdMPINPfhlWb7Ybo1YD2\n" +
                        "e1Ym3JkHWZbj2PFWALOKnUyMK1Ohc7m+t7bNtYHZpNW0DCI/gLWt9jB4MBz09+exrSSuThQnuv3j\n" +
                        "RAhu8eVpT3FpG4h0nJ1y5vrINRyfvQvGi03Law6TT/d0MBeuBwTLcuI2NxMwv7FGdH0y5Nd9f8uJ\n" +
                        "+wEBiN4KLDG4EcA6gJFDh/fcd7unglTdO3Cpxnq8z+0+hBBC7BBJHz1X9zx6xoRyN5pxH/+IBAO+\n" +
                        "xJWkj56ZcIyAyvw1VUCIvm5eKoiDYsFglGxekoAScIq/KAlIIVViMc5lZrwLeICY2z0IIYQQQgix\n" +
                        "O/wH4P/cZvq+E/gAAAAASUVORK5CYII=";

                byte[] decodedByte = Base64.decode(iconString, 0);
                icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

                String downloading;

                try {
                    downloading = Helper.getResourceString(mContext, R.string.DownloadDots);
                } catch (Throwable t) {
                    downloading = "Downloading...";
                }

                if (!Helper.getSettings("Notification")) {
                    mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(mContext);
                    mBuilder.setContentTitle(title)
                            .setContentText(downloading)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(icon);
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL(link);

                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Instagram");
                connection.setConnectTimeout(10000);
                connection.connect();

                InputStream input = connection.getInputStream();

                OutputStream output = new FileOutputStream(save);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

                if (!Helper.getSettings("Notification")) {
                    String downloadComplete;

                    if (logNotification == 1) {
                        Helper.setPush("Downloaded: " +title);
                    }

                    try {
                        downloadComplete = Helper.getResourceString(mContext, R.string.Download_Completed);
                    } catch (Throwable t) {
                        downloadComplete = "Download Complete";
                    }

                    mBuilder.setContentText(downloadComplete).setTicker(downloadComplete);

                    mBuilder.setContentTitle(mBuilder.mContentTitle)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(icon)
                            .setAutoCancel(true);

                    Intent notificationIntent = new Intent();
                    notificationIntent.setAction(Intent.ACTION_VIEW);

                    File file = new File(save);
                    if (save.contains("jpg")) {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    } else {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                    }
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(contentIntent);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            } catch (Throwable t) {
                if (logNotification == 1) {
                    Helper.setPush("Downloaded (Failed): " +title);
                    Helper.setPush("Downloaded (Failed): " +t);
                }

                downloadFailed = 2;
                Helper.setError("Download Error - " + t);
            }


            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (downloadFailed == 1) {

                String downloadComplete;

                try {
                    downloadComplete = Helper.getResourceString(mContext, R.string.Download_Completed);
                } catch (Throwable t2) {
                    downloadComplete = "Download Completed";
                }

                Toast(downloadComplete);

                try {
                    MediaScannerConnection.scanFile(mContext,
                            new String[]{save}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    if (uri != null) {
                                        int scan = 1;
                                    }
                                }
                            });
                } catch (Throwable t2) {
                    Helper.setError("Scan Failed - " + t2);
                }

                if (save.contains("_LiveVideo.mp4")) {
                    Helper.passLiveStory(save, userName, getApplicationContext());
                    mNotifyManager.cancel(12345);
                    mNotifyManager.cancel(id);
                }
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentText(downloadFailed)
                            .setTicker(downloadFailed)
                            .setContentTitle(title)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setLargeIcon(icon)
                            .setAutoCancel(true);
                    mNotifyManager.notify(id, mBuilder.build());
                }

                Toast(downloadFailed);
            }

            try {
                if (current + 1 < links.length && downloadFailed == 1) {
                    current = current + 1;
                    linkToDownload = links[current];
                    userName = userNames[current];
                    notificationTitle = notificationTitles[current];
                    fileName = fileNames[current];
                    fileType = fileTypes[current];
                    SAVE = saveLocations[current];

                    passDownload();
                }
            } catch (Throwable t) {
            }
        }
    }

    public Download_Passed() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Helper.setError("Passed To XInsta");

        mContext = getApplicationContext();

        if (intent.getStringExtra("URL").contains(";")) {
            links = intent.getStringExtra("URL").split(";");
            userNames = intent.getStringExtra("User").split(";");
            notificationTitles = intent.getStringExtra("Notification").split(";");
            fileNames = intent.getStringExtra("Filename").split(";");
            fileTypes = intent.getStringExtra("Filetype").split(";");
            saveLocations = intent.getStringExtra("SAVE").split(";");

            linkToDownload = links[0];
            userName = userNames[0];
            notificationTitle = notificationTitles[0];
            fileName = fileNames[0];
            fileType = fileTypes[0];
            SAVE = saveLocations[0];

            checkPermission();
        } else {
            linkToDownload = intent.getStringExtra("URL");
            fileName = intent.getStringExtra("Filename");
            fileType = intent.getStringExtra("Filetype");
            notificationTitle = intent.getStringExtra("Notification");
            userName = intent.getStringExtra("User");
            SAVE = Helper.getSaveLocation(fileType);
            getDirectory = Environment.getExternalStorageDirectory().toString();

            checkPermission();
        }
    }

    void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent myIntent = new Intent(Download_Passed.this, Permission.class);
                myIntent.putExtra("URL", linkToDownload);
                myIntent.putExtra("Filename", fileName);
                myIntent.putExtra("Filetype", fileType);
                myIntent.putExtra("Notification", notificationTitle);
                myIntent.putExtra("User", userName);
                myIntent.putExtra("SAVE", SAVE);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            } else {
                passDownload();
            }
        } else {
            passDownload();
        }
    }

    void passDownload() {
        SAVE = Helper.getSaveLocation(fileType);
        if (fileType.equals("Profile")) {
            SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
        } else {
            SAVE = Helper.checkSave(SAVE, userName, fileName);
        }

        new Download().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, SAVE, notificationTitle);
    }

    void Toast (final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
    }

}