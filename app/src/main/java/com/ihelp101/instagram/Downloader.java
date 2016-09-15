package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Downloader extends Service {

    Context mContext;
    Bitmap icon;
    int count = 0;
    int id = 1;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE = "Instagram";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Download extends AsyncTask<String, String, String> {

        int downloadFailed = 1;

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                downloadFailed = 1;
                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

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

                if (!Helper.getSettings("Notification")) {
                    String downloading;

                    try {
                        downloading = Helper.getResourceString(mContext, R.string.DownloadDots);
                    } catch (Throwable t) {
                        downloading = "Downloading...";
                    }

                    mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(mContext);
                    mBuilder.setContentTitle(notificationTitle)
                            .setContentText(downloading)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(icon);
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output;

                output = new FileOutputStream(SAVE);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) >= 0) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();


                if (!Helper.getSettings("Notification")) {
                    String downloadComplete;

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

                    File file = new File(SAVE);
                    if (SAVE.contains("jpg")) {
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
                downloadFailed = 2;
                Helper.setError("Download Error: " + t);

                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentText(downloadFailed)
                            .setTicker(downloadFailed)
                            .setContentTitle(notificationTitle)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setLargeIcon(icon)
                            .setAutoCancel(true);
                    mNotifyManager.notify(id, mBuilder.build());
                }
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
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                Toast(downloadComplete);

                MediaScannerConnection.scanFile(mContext,
                        new String[]{SAVE}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                if (uri != null) {
                                    int scan = 1;
                                }
                            }
                        });
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                Toast(downloadFailed);
            }
        }
    }

    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
                    try {
                        URL u = new URL(linkToDownload);
                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());


                        JSONObject jsonObject = new JSONObject(JSONInfo);

                        String descriptionType;

                        try {
                            descriptionType = jsonObject.getJSONArray("items").getJSONObject(0).getString("type");
                        } catch (Throwable t) {
                            setError("Video Fetch Type Failed: " +t);
                            descriptionType = "None";
                        }

                        if (descriptionType.equals("video")) {
                            try {
                                descriptionType = Helper.getResourceString(mContext, R.string.video);
                            } catch (Throwable t) {
                                descriptionType = "Video";
                            }
                            String fileExtension = ".mp4";
                            try {
                                fileName = userName + "_" + jsonObject.getJSONArray("items").getJSONObject(0).getString("id") + fileExtension;
                            } catch (Throwable t) {
                                setError("Video Fetch File Name Failed: " +t);
                            }
                            fileType = "Video";

                            try {
                                linkToDownload = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
                            } catch (Throwable t) {
                                setError("Video Fetch Link To Download Failed: " +t);
                            }

                            linkToDownload = linkToDownload.replace("750x750", "");
                            linkToDownload = linkToDownload.replace("640x640", "");
                            linkToDownload = linkToDownload.replace("480x480", "");
                            linkToDownload = linkToDownload.replace("320x320", "");

                            try {
                                notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, descriptionType);
                            } catch (Throwable t) {
                                notificationTitle = userName + "'s " +descriptionType;
                            }
                            notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

                            SAVE = Helper.getSaveLocation(fileType);

                            downloadOrPass();
                        }
                    } catch (Exception e) {
                        setError("Video Fetch Failed: " + e);
                    }
            return responseString;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        try {
            if (intent.getStringExtra("URL").contains("/media/")) {
                linkToDownload = intent.getStringExtra("URL");
                userName = intent.getStringExtra("User");
                new RequestTask().execute();
            } else {
                Intent downloadIntent = new Intent();
                downloadIntent.setPackage("com.ihelp101.instagram");
                downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                downloadIntent.putExtra("URL", intent.getStringExtra("URL"));
                downloadIntent.putExtra("SAVE", intent.getStringExtra("SAVE"));
                downloadIntent.putExtra("Notification", intent.getStringExtra("Notification"));
                downloadIntent.putExtra("Filename", intent.getStringExtra("Filename"));
                downloadIntent.putExtra("Filetype", intent.getStringExtra("Filetype"));
                downloadIntent.putExtra("User", intent.getStringExtra("User"));
                mContext.startService(downloadIntent);
                stopSelf();
            }
        } catch (Throwable t) {
            setError("Download Pass Failed: " +t);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void downloadOrPass() {
        SAVE = Helper.getSaveLocation(fileType);

        if (!SAVE.toLowerCase().contains("com.android.externalstorage.documents")) {
            SAVE = Helper.getSaveLocation(fileType);
            SAVE = Helper.checkSave(SAVE, userName, fileName);

            new Download().execute();
        } else {
            Intent downloadIntent = new Intent();
            downloadIntent.setPackage("com.ihelp101.instagram");
            downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
            downloadIntent.putExtra("URL", linkToDownload);
            downloadIntent.putExtra("SAVE", SAVE);
            downloadIntent.putExtra("Notification", notificationTitle);
            downloadIntent.putExtra("Filename", fileName);
            downloadIntent.putExtra("Filetype", fileType);
            downloadIntent.putExtra("User", userName);
            mContext.startService(downloadIntent);
        }
    }

    void setError(String status) {
        Helper.setError(status);
    }

    void Toast(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
