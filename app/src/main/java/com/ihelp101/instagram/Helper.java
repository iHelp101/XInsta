package com.ihelp101.instagram;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.provider.DocumentFile;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class Helper {

    static DownloadManager downloadManager;
    static String profileHelper;

    static boolean getSettings(String saveName) {
        String setting;
        File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/" + saveName + ".txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(notification));

            setting = br.readLine();;
            br.close();
        }
        catch (Exception e) {
            setting = "false";
        }

        if (!setting.equals("true") && !setting.equals("false")) {
            setting = "false";
            Helper.setSetting(saveName, "false");
        }

        return Boolean.valueOf(setting);
    }

    static boolean isTagged(Object mMedia, String TAGGED_HOOK_CLASS, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            if (arrayList.size() >= 1) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable t) {
            return false;
        }
    }

    static class Download extends AsyncTask<String, String, String> {

        String link;
        String save;
        String title;
        String userName;
        String fileType;
        String epoch;

        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int downloadFailed = 1;
        int id = 1;
        int logNotification = 0;
        Bitmap icon;
        WeakReference<Context> context;


        public Download (Context passedContext) {
            context = new WeakReference<>(passedContext);
        }

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                link = uri[0];
                save = uri[1];
                title = uri[2];
                userName = uri[3];
                fileType = uri[4];

                try {
                    epoch = uri[5];
                } catch (Throwable t) {
                }

                downloadFailed = 1;
                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (save.contains("_LiveAudio.mp4")) {
                    id = 12345;
                }

                if (link.contains("notification")) {
                    link = link.replaceAll("notification", "");
                    logNotification = 1;
                }

                if (link.contains("/vp/") && link.contains(".jpg")) {
                    //link = link.replace("/vp/", "/");
                }

                if (link.contains("media123;")) {
                    link = link.replaceAll("media123;", "");
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
                    downloading = Helper.getResourceString(context.get(), R.string.DownloadDots);
                } catch (Throwable t) {
                    downloading = "Downloading...";
                }

                if (!Helper.getSettings("Notification")) {
                    mNotifyManager = (NotificationManager) context.get().getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(context.get());
                    mBuilder.setContentTitle(title)
                            .setContentText(downloading)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(icon);
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL(link);

                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream());

                OutputStream output;

                if (Helper.getSaveLocation(fileType).contains("com.android.externalstorage.documents")) {
                    output = context.get().getContentResolver().openOutputStream(getDocumentFile(new File(save), false, save, fileType, context.get()).getUri());
                } else {
                    output = new FileOutputStream(save);
                }

                byte data[] = new byte[4096];

                int count;

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
                        downloadComplete = Helper.getResourceString(context.get(), R.string.Download_Completed);
                    } catch (Throwable t) {
                        downloadComplete = "Download Completed";
                    }

                    mBuilder.setContentText(downloadComplete).setTicker(downloadComplete);

                    mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(icon)
                            .setAutoCancel(true);

                    Intent notificationIntent  = new Intent();
                    PendingIntent contentIntent;

                    if (Build.VERSION.SDK_INT >= 24) {
                        notificationIntent.setAction("com.ihelp101.instagram.CLICK");
                        notificationIntent.putExtra("File", save);

                        contentIntent = PendingIntent.getBroadcast(context.get(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        notificationIntent.setAction(Intent.ACTION_VIEW);

                        File file = new File(save);
                        if (save.contains("jpg")) {
                            notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                        } else {
                            notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                        }
                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        contentIntent = PendingIntent.getActivity(context.get(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    mBuilder.setContentIntent(contentIntent);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            } catch (Throwable t) {
                if (logNotification == 1) {
                    Helper.setPush("Downloaded (Failed): " +title);
                }

                downloadFailed = 2;
                setError("Download Error - " + t);
                if (!Helper.getSettings("Notification")) {

                    String downloadFailed;

                    try {
                        downloadFailed = Helper.getResourceString(context.get(), R.string.Download_Failed);
                    } catch (Throwable t2) {
                        downloadFailed = "Download Failed";
                    }

                    mBuilder.setContentText(downloadFailed)
                            .setTicker(downloadFailed)
                            .setContentTitle(title)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
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
                    downloadComplete = Helper.getResourceString(context.get(), R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                Toast(downloadComplete, context.get());

                try {
                    MediaScannerConnection.scanFile(context.get(),
                            new String[]{save}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    if (uri != null) {
                                        int scan = 1;
                                    }
                                }
                            });
                } catch (Throwable t) {
                    setError("Scan Failed - " +t);
                }

                if (save.contains(".jpg")) {
                    try {
                        if (Helper.getSettings("ChrisEXIF")) {
                            String timeStamp = Helper.getDateEpochEXIF(Long.parseLong(epoch), context.get());
                            ExifInterface exif = null;

                            if (Helper.getSaveLocation(fileType).contains("com.android.externalstorage.documents")) {
                                if (Build.VERSION.SDK_INT >= 24) {
                                    FileDescriptor fileDescriptor = context.get().getContentResolver().openFileDescriptor(getDocumentFile(new File(save), false, save, fileType, context.get()).getUri(), "rw").getFileDescriptor();
                                    exif = new ExifInterface(fileDescriptor);
                                }
                            } else {
                                exif = new ExifInterface(new File(save).getAbsolutePath());
                            }

                            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, timeStamp);
                            exif.setAttribute(ExifInterface.TAG_DATETIME, timeStamp);
                            exif.saveAttributes();
                        }
                    } catch (Throwable t) {
                        setError("EXIF Failed - " +t);
                    }
                }
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(context.get(), R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                Toast(downloadFailed, context.get());
            }
        }
    }

    static class DownloadLive extends AsyncTask<String, String, String> {

        String saveLocation;
        String name;
        Context mContext;

        DownloadLive(final Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
            String[] strings = uri[0].split(";");

            for (String string : strings) {
                String link = string;
                String notificationTitle = "";
                String userName = uri[1];
                String file = uri[2];

                name = uri[1];

                try {
                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Audio");
                } catch (Throwable t) {
                    notificationTitle = userName + "'s Live Story Audio";
                }

                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                if (link.equals(strings[1])) {
                    notificationTitle = notificationTitle.replace("Live Story Audio", "Live Story Video");
                }

                if (link.equals(strings[1])) {
                    file = file.replace("LiveAudio", "LiveVideo");
                }

                saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + file;

                new File(saveLocation).delete();

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
                request.setDescription("Downloading...");
                request.setTitle(notificationTitle);
                request.setDestinationUri(Uri.fromFile(new File(saveLocation)));
                request.allowScanningByMediaScanner();

                Helper.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Helper.downloadManager.enqueue(request);
            }

            return responseString;
        }
    }

    static class DownloadLive2 extends AsyncTask<String, String, String> {

        String fileName;
        String name;
        String saveLocation;
        String userName;
        Context mContext;

        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        Bitmap icon;

        DownloadLive2(final Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
            String linksToDownload = uri[0];
            String[] strings = linksToDownload.split(";");

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

            if (strings.length > 1) {
                try {
                    String link = strings[0];
                    String notificationTitle;
                    userName = uri[1];
                    fileName = uri[2];

                    name = uri[1];

                    Helper.setError("Live - Downloading Audio");

                    try {
                        notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Audio");
                    } catch (Throwable t) {
                        notificationTitle = "Live Story Audio";
                    }

                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                    saveLocation = Helper.getSaveLocation("Video");

                    if (saveLocation.toLowerCase().contains("com.android.externalstorage.documents")) {
                        saveLocation = saveLocation.split(";")[1];
                    }

                    saveLocation = Helper.checkSave(saveLocation, userName, fileName);

                    if (new File(saveLocation.replace("Audio", "")).exists()) {
                        saveLocation = saveLocation.replace("Audio", "");
                        return "Previous";
                    }

                    new File(saveLocation).delete();

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
                    mNotifyManager.notify(100, mBuilder.build());

                    URL url = new URL(link);

                    URLConnection connection = url.openConnection();
                    connection.connect();

                    InputStream input = new BufferedInputStream(url.openStream());

                    OutputStream output;

                    if (saveLocation.contains("com.android.externalstorage.documents")) {
                        output = mContext.getContentResolver().openOutputStream(getDocumentFile(new File(saveLocation), false, saveLocation, "Video", mContext).getUri());
                    } else {
                        output = new FileOutputStream(saveLocation);
                    }

                    byte data[] = new byte[4096];

                    int count;

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    File fileSave = new File(saveLocation);

                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileSave)));

                    linksToDownload = linksToDownload.replace(link + ";", "");

                    return linksToDownload;
                } catch (Throwable t) {
                    Helper.setError("Live Downloads Failed: " + t);
                    return responseString;
                }
            } else {
                try {
                    String notificationTitle;
                    userName = uri[1];
                    fileName = uri[2];

                    name = uri[1];

                    Helper.setError("Live - Downloading Video");

                    try {
                        notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Video");
                    } catch (Throwable t) {
                        notificationTitle = "Live Story Video";
                    }

                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                    saveLocation = Helper.getSaveLocation("Video");

                    if (saveLocation.toLowerCase().contains("com.android.externalstorage.documents")) {
                        saveLocation = saveLocation.split(";")[1];
                    }

                    saveLocation = Helper.checkSave(saveLocation, userName, fileName);

                    new File(saveLocation).delete();

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
                    mNotifyManager.notify(100, mBuilder.build());

                    URL url = new URL(linksToDownload);

                    URLConnection connection = url.openConnection();
                    connection.connect();

                    InputStream input = new BufferedInputStream(url.openStream());

                    OutputStream output;

                    if (saveLocation.contains("com.android.externalstorage.documents")) {
                        output = mContext.getContentResolver().openOutputStream(getDocumentFile(new File(saveLocation), false, saveLocation, "Video", mContext).getUri());
                    } else {
                        output = new FileOutputStream(saveLocation);
                    }

                    byte data[] = new byte[4096];

                    int count;

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                    File fileSave = new File(saveLocation);

                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileSave)));

                    return "Completed";
                } catch (Throwable t) {
                    Helper.setError("Live Downloads Failed2: " + t);
                }
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("Nope")) {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                mBuilder.setContentText(downloadFailed)
                        .setTicker(downloadFailed)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setLargeIcon(icon);
                mNotifyManager.notify(100, mBuilder.build());
            } else if (result.equals("Completed")) {
                try {
                    String videoFile = URLDecoder.decode(saveLocation, "utf-8");
                    String audioFile = URLDecoder.decode(saveLocation.replace("Video", "Audio"), "utf-8");
                    String notificationTitle;

                    try {
                        notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story");
                    } catch (Throwable t) {
                        notificationTitle = userName + "'s Live Story";
                    }

                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                    Live.startMux2(mContext, audioFile, videoFile, notificationTitle);
                } catch (Throwable t) {
                    Helper.setError("Live Story Download Failed - " +t);

                    String downloadFailed;

                    try {
                        downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                    } catch (Throwable t2) {
                        downloadFailed = "Download Failed";
                    }

                    mBuilder.setContentText(downloadFailed)
                            .setTicker(downloadFailed)
                            .setContentTitle(downloadFailed)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setLargeIcon(icon);
                    mNotifyManager.notify(100, mBuilder.build());
                }
            } else if (result.equals("Previous")) {
                Helper.setError("Previous Download");
                String downloadComplete;

                try {
                    downloadComplete = Helper.getResourceString(mContext, R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                String notificationTitle;

                try {
                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Video");
                } catch (Throwable t) {
                    notificationTitle = "Live Story Video";
                }

                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setContentTitle(notificationTitle)
                        .setContentText(downloadComplete)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setLargeIcon(icon);

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);

                File fileSave = new File(saveLocation);

                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileSave)));

                notificationIntent.setDataAndType(Uri.fromFile(fileSave), "video/*");
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(100, mBuilder.build());
                Live.stopService(mContext, mBuilder);
            } else {
                fileName = fileName.replace("Audio", "Video");
                new Helper.DownloadLive2(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result, userName, fileName);
            }
        }
    }

    static class DownloadNotification extends AsyncTask<String, String, String> {

        String linkToDownload = "";
        String userName = "";
        String fallbackURL = "";
        String fileName = "";
        String fileType = "";
        String notificationTitle = "";
        long longId;
        WeakReference<Context> context;


        public DownloadNotification (Context passedContext) {
            context = new WeakReference<>(passedContext);
        }

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
            try {
                linkToDownload = uri[0];
                userName = uri[1];
                fileName = uri[2];
                fileType = uri[3];
                notificationTitle = uri[4];

                URL u;
                fallbackURL = linkToDownload;
                longId = System.currentTimeMillis() / 1000;

                if (linkToDownload.contains("media123")) {
                    u = new URL("https://www.instagram.com/" + userName);
                } else {
                    linkToDownload = linkToDownload.replaceAll("notification", "");
                    u = new URL(linkToDownload);
                }

                URLConnection c = u.openConnection();
                c.connect();

                String JSONInfo = Helper.convertStreamToString(u.openStream());

                return JSONInfo;
            } catch (Exception e) {
                if (linkToDownload.contains("media123")){
                    Helper.setPush("Private Account");
                    setError("Private Account: " +e);
                    linkToDownload = "notification" + fallbackURL;

                    long longId = System.currentTimeMillis() / 1000;

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context.get(), false);
                } else {
                    setError("Notification Fetch Failed: " + e);
                    Helper.setPush("Notification Fetch Failed: " +e);
                    Helper.setPush("Notification Fetch Failed URL - " +fallbackURL);
                }
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                String JSONInfo = s.split("window._sharedData = ")[1].split("</script>")[0];

                JSONObject jsonObject = new JSONObject(JSONInfo);

                String descriptionType;

                try {
                    descriptionType = Helper.getNotificationType(jsonObject);
                    if (descriptionType.equals("Test")) {
                    }
                } catch (Throwable t) {
                    setError("Video Fetch Type Failed: " +t);
                    if (linkToDownload.contains("media123")) {
                        Helper.setPush("Private Account - Trying Image4");

                        long longId = System.currentTimeMillis() / 1000;

                        Helper.downloadOrPass(fallbackURL, fileName, fileType, userName, notificationTitle, longId, context.get(), false);
                    }
                    descriptionType = "None";
                }


                if (descriptionType.equals("false")) {
                    System.out.println("Linl: " +linkToDownload);
                    linkToDownload = fallbackURL;

                    String fileExtension = ".jpg";
                    fileName = Helper.getNotificationFileName(jsonObject, fileExtension, fileName, userName, context.get());

                    if (Helper.getSettings("URLFileName")) {
                        int value = fallbackURL.replace("https://", "").replace("http://", "").split("/").length - 1;
                        fileName = fallbackURL.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
                    }

                    //linkToDownload = linkToDownload.replace("750x750", "");
                    //linkToDownload = linkToDownload.replace("640x640", "");
                    //linkToDownload = linkToDownload.replace("480x480", "");
                    //linkToDownload = linkToDownload.replace("320x320", "");
                    linkToDownload = "notification" + linkToDownload;
                }

                if (descriptionType.equals("true")) {
                    try {
                        descriptionType = Helper.getResourceString(context.get(), R.string.video);
                    } catch (Throwable t) {
                        descriptionType = "Video";
                    }
                    String fileExtension = ".mp4";
                    try {
                        fileName = userName + "_" + Helper.getNotificationItemID(jsonObject) + fileExtension;
                    } catch (Throwable t) {
                        setError("Video Fetch File Name Failed: " +t);
                    }
                    fileType = "Video";

                    fileName = Helper.getNotificationFileName(jsonObject, fileExtension, fileName, userName, context.get());

                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);

                        String videoUrl = "https://www.instagram.com/p/" + Helper.getNotificationURL(jsonObject);

                        URL u = new URL(videoUrl);

                        URLConnection c2 = u.openConnection();
                        c2.connect();

                        String videoHTML = Helper.convertStreamToString(u.openStream());

                        linkToDownload = videoHTML.split("og:video:secure_url\" content=\"")[1].split("\"")[0];
                    } catch (Throwable t) {
                        setError("Video Fetch Link To Download Failed: " +t);
                    }

                    if (Helper.getSettings("URLFileName")) {
                        int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
                        fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
                    }

                    //linkToDownload = linkToDownload.replace("750x750", "");
                    //linkToDownload = linkToDownload.replace("640x640", "");
                    //linkToDownload = linkToDownload.replace("480x480", "");
                    //linkToDownload = linkToDownload.replace("320x320", "");

                    linkToDownload = "notification" + linkToDownload;

                    try {
                        notificationTitle = Helper.getResourceString(context.get(), R.string.username_thing, userName, descriptionType);
                    } catch (Throwable t) {
                        notificationTitle = userName + "'s " +descriptionType;
                    }
                    notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

                    String date = Helper.getNotificationDate(jsonObject);

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, Long.parseLong(date), context.get(), false);
                } else if (linkToDownload.contains("media123")){
                    linkToDownload = linkToDownload.replaceAll("media123;", "");
                    linkToDownload = "notification" + linkToDownload;

                    System.out.println("11111111111111");

                    String date = Helper.getNotificationDate(jsonObject);

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, Long.parseLong(date), context.get(), false);
                } else if (descriptionType.equals("false")) {
                    String date = Helper.getNotificationDate(jsonObject);

                    setError("Image: " +fileName);

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, Long.parseLong(date), context.get(), false);
                }
            } catch (Throwable t) {
                if (linkToDownload.contains("media123")){
                    Helper.setPush("Private Account");
                    setError("Private Account: " +t);
                    linkToDownload = "notification" + fallbackURL;

                    long longId = System.currentTimeMillis() / 1000;

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context.get(), false);
                } else {
                    setError("Notification Fetch Failed: " + t);
                    Helper.setPush("Notification Fetch Failed: " +t);
                    Helper.setPush("Notification Fetch Failed URL - " +fallbackURL);
                }
            }

        }
    }

    static DocumentFile getDocumentFile(File file, boolean isDirectory, String SAVE, String fileType, Context context) {
        String baseFolder = getExtSdCardFolder(file, context);

        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        }
        catch (IOException e) {
            return null;
        }

        String fileExtension;
        if (SAVE.contains("jpg")) {
            fileExtension = "image/*";
        } else {
            fileExtension = "video/*";
        }

        DocumentFile document = DocumentFile.fromTreeUri(context, Uri.parse(Helper.getSaveLocation(fileType).split(";")[0]));

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                }
                else {
                    nextDocument = document.createFile(fileExtension, parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    static int getFileCount(File f) {
        StringBuilder text = new StringBuilder();
        int lineCount = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");

                lineCount++;
            }
            br.close();
        } catch (IOException e) {
            return 0;
        }
        return lineCount;
    }

    static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    static List injectButton(Context context, List list, String type) {
        try {
            String downloadCheck;

            try {
                downloadCheck = Helper.getResourceString(context, R.string.the_not_so_big_but_big_button);
            } catch (Throwable t) {
                downloadCheck = "Download";
            }



            Pair downloadPair = Pair.create(((Pair) list.get(0)).first, downloadCheck);
            List pairsList = new ArrayList();
            pairsList.add(downloadPair);

            if (Helper.getSettings("Lock") && type.equals("Feed")) {
                String lockFeed;

                try {
                    lockFeed = Helper.getResourceString(context, R.string.the_not_so_big_but_big_button2);
                } catch (Throwable t) {
                    lockFeed = "Privacy Lock";
                }

                Pair lockFeedPair = Pair.create("COPY_LINK", lockFeed);

                pairsList.add(lockFeedPair);
            }

            if (Helper.getSettings("Order")) {
                pairsList.addAll(list);
                list.clear();
                list.addAll(pairsList);
            } else {
                list.addAll(pairsList);
            }
        } catch (Throwable t) {
            setError("Failed to Inject New Feed Dialog - " +t);
            return null;
        }

        return list;
    }

    static Object getOtherFieldByType(Object object, Class<?> type) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                try {
                    if (field.getType().getName().equals(type.getName()) && XposedHelpers.getObjectField(object, field.getName()) != null) {
                        return XposedHelpers.getObjectField(object, field.getName());
                    }
                } catch (Throwable t) {
                    return null;
                }
            }

            return null;
        } catch (Throwable t) {
            setError("Failed Other Field By Type - " +t);
            return null;
        }
    }

    static Object getFieldByType(Object object, Class<?> type) {
        Field f = XposedHelpers.findFirstFieldByExactType(object.getClass(), type);
        try {
            return f.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Object getFieldsByType(Object object, Class<?> type) {
        Field[] fields = object.getClass().getDeclaredFields();
        List<Object> results = new ArrayList<>();

        for (Field field : fields) {
            try {
                Object fieldObject = XposedHelpers.getObjectField(object, field.getName());
                if (field.getType().equals(type) && fieldObject != null) {
                    results.add(fieldObject);
                }
            } catch (Throwable t) {
            }
        }

        return results.get(0);
    }

    static Object getMedia(Object object, ClassLoader classLoader, String MEDIA_CLASS_NAME) {
        Field fields[] = object.getClass().getDeclaredFields();
        Object returnObject = null;

        for (Field field : fields) {
            String className = XposedHelpers.getObjectField(object, field.getName()).getClass().toString();
            if (!className.contains("List")) {
                try {
                    returnObject = XposedHelpers.getObjectField(object, field.getName());
                    returnObject = getObjectField(returnObject, XposedHelpers.findFirstFieldByExactType(returnObject.getClass(), XposedHelpers.findClass(MEDIA_CLASS_NAME, classLoader)).getName());
                    if (returnObject != null) {
                        break;
                    }
                } catch (Throwable t) {
                }
            }
        }

        return returnObject;
    }

    static Object getProfile(Object object, ClassLoader classLoader, String PROFILE_HOOK_CLASS2, String USER_CLASS_NAME) {
        Field fields[] = object.getClass().getDeclaredFields();
        Object returnObject = null;

        for (Field field : fields) {
            String className = XposedHelpers.getObjectField(object, field.getName()).getClass().toString();
            if (!className.contains("List")) {
                try {
                    Object returnObjectTest = XposedHelpers.getObjectField(object, field.getName());
                    //Get Profile Class 2
                    returnObjectTest = getObjectField(returnObjectTest, XposedHelpers.findFirstFieldByExactType(returnObject.getClass(), XposedHelpers.findClass(PROFILE_HOOK_CLASS2, classLoader)).getName());
                    //Get User Class
                    returnObjectTest = getObjectField(returnObjectTest, XposedHelpers.findFirstFieldByExactType(returnObject.getClass(), XposedHelpers.findClass(USER_CLASS_NAME, classLoader)).getName());

                    if (returnObjectTest != null && returnObjectTest.getClass().equals(findClass(USER_CLASS_NAME, classLoader))) {
                        returnObject = returnObjectTest;
                        System.out.println("d: " +returnObject);
                        break;
                    }
                } catch (Throwable t) {
                }
            }
        }

        if (returnObject == null) {
            //Get Profile Class 2
            returnObject = getObjectField(object, XposedHelpers.findFirstFieldByExactType(object.getClass(), XposedHelpers.findClass(PROFILE_HOOK_CLASS2, classLoader)).getName());
            //Get User Class
            returnObject = getObjectField(returnObject, XposedHelpers.findFirstFieldByExactType(returnObject.getClass(), XposedHelpers.findClass(USER_CLASS_NAME, classLoader)).getName());
        }

        return returnObject;
    }

    static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    static String getData(Context context) {
        return context.getResources().getString(R.string.Month) + ";" + context.getResources().getString(R.string.Day) + ";" + context.getResources().getString(R.string.Year) + ";" + context.getResources().getString(R.string.Space) + ";" + context.getResources().getString(R.string.Hour) + ";" + context.getResources().getString(R.string.Minute) + ";" + context.getResources().getString(R.string.Second) + ";" + context.getResources().getString(R.string.AM) + ";";
    }

    static String getDate(Long epochTime) {
        try {
            String dateFormat = Helper.getSetting("File");

            if (Helper.getSetting("File").equals("Instagram")) {
                dateFormat = "Month/Day/Year";
            }

            Date date = new Date(epochTime * 1000L);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace("Month", "MM");
            dateFormat = dateFormat.replace("Day", "dd");
            dateFormat = dateFormat.replace("Year", "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    static String getDateEpoch(Long epochTime, Context nContext) {
        try {
            String dateFormat = Helper.getSetting("File");

            if (Helper.getSetting("File").equals("Instagram")) {
                dateFormat = "Month/Day/Year";
            }

            Date date = new Date(epochTime);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace("Month", "MM");
            dateFormat = dateFormat.replace("Day", "dd");
            dateFormat = dateFormat.replace("Year", "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    static String getDateEpochEXIF(Long epochTime, Context nContext) {
        try {
            String dateFormat = "yyyy:MM:dd HH:mm:ss";

            Date date = new Date(epochTime * 1000L);
            TimeZone timeZone = TimeZone.getDefault();

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    static String getDateEpochWithTime(Long epochTime) {
        try {
            String dateFormat = Helper.getSetting("File");

            if (Helper.getSetting("File").equals("Instagram")) {
                dateFormat = "Month/Day/Year";
            }

            Date date = new Date(epochTime * 1000L);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace("Month", "MM");
            dateFormat = dateFormat.replace("Day", "dd");
            dateFormat = dateFormat.replace("Year", "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");
            dateFormat = dateFormat + "HHmmss";

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    static String getStringFromPair(List list, int i) {
        String title;
        try {
            Pair pair = (Pair) list.get(i);
            title = (String) pair.second;
        } catch (Throwable t) {
            title = "Instagram";
        }

        return title;
    }

    static Resources getOwnResources(Context context) {
        return getResourcesForPackage(context, "com.ihelp101.instagram");
    }

    static Resources getResourcesForPackage(Context context, String packageName) {
        try {
            return context.getPackageManager().getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().contains(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            for (File file : context.getExternalFilesDirs("external")) {
                if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                    int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                    if (index > 0) {
                        String path = file.getAbsolutePath().substring(0, index);
                        try {
                            path = new File(path).getCanonicalPath();
                        } catch (IOException e) {
                        }
                        paths.add(path);
                    }
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

    static void getPostUrl(final Context context, final String fallBackURL, final String fileName, final String fileType, final String notificationTitle, final String userName) {
        try {
            final Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL("https://www.instagram.com/" + userName);
                        URLConnection c = u.openConnection();
                        c.connect();

                        setError("URL: " + u.toURI());

                        String shortCode = Helper.convertStreamToString(u.openStream());

                        shortCode = shortCode.split("window._sharedData")[1].split("</script>")[0];
                        shortCode = shortCode.split("timeline_media")[1];

                        shortCode = shortCode.split("shortcode")[1].split("\",")[0];
                        shortCode = shortCode.replace("\":\"", "");

                        String postUrl = "https://www.instagram.com/p/" + shortCode;

                        setError("Post: " +postUrl);
                        Helper.getPostMulti(context, fileName, fileType, fallBackURL, postUrl, notificationTitle, userName);
                    } catch (Exception e) {
                        Helper.setError("Getting Post URL Failed - " +e);
                        Helper.setError("Post URL Tried - " +"https://www.instagram.com/" + userName);
                        Helper.setPush("Private Account - Trying Image2");

                        String linkToDownload = fallBackURL.replaceAll("media123;", "");
                        linkToDownload = "notification" + linkToDownload;

                        long longId = System.currentTimeMillis() / 1000;
                        Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context, false);
                    }
                }
            };
            checkMedia.start();
        } catch (Throwable t) {
            Helper.setError("Get Post URL Failed - " +t);
            Helper.setError("Private Account - Trying Image3");
            Helper.setPush("Private Account - Trying Image3");
            String linkToDownload = fallBackURL.replaceAll("media123;", "");
            linkToDownload = "notification" + linkToDownload;

            long longId = System.currentTimeMillis() / 1000;
            Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context, false);
        }
    }

    static void getPostMulti(final Context context, final String fileName, final String fileType, final String linkToDownload, final String postUrl, final String notificationTitle, final String userName) {
        try {
            Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL(postUrl);
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = u.openStream();

                        Elements elements = Jsoup.parse(Helper.convertStreamToString(inputStream)).select("body").first().children();

                        if (elements.toString().contains("GraphSidecar")) {
                            Helper.getPostMultiDownload(context, elements, linkToDownload, fileName, fileType, notificationTitle, userName);
                        } else {
                            System.out.println("Single! "  +fileName);
                            new Helper.DownloadNotification(context).execute(linkToDownload, userName, fileName, fileType, notificationTitle);
                        }
                    } catch (Exception e) {
                        Helper.setError("Multi Check Failed: " + e);
                        Helper.setError("Post URL Tried - " +postUrl);
                    }
                }
            };
            checkMedia.start();
        } catch (Throwable t) {
            Helper.setError("Check For Multi Failed - " +t);
            Helper.setPush("Private Account - Trying Image5");
            Helper.setError("Private Account - Trying Image");
            String fallBackURL = linkToDownload.replaceAll("media123;", "");
            fallBackURL = "notification" + fallBackURL;

            long longId = System.currentTimeMillis() / 1000;
            Helper.downloadOrPass(fallBackURL, fileName, fileType, userName, notificationTitle, longId, context, false);
        }
    }

    static void getPostMultiDownload(final Context context, Elements elements, String fallBackURL, String fileName, String fileType, String notificationTitle, String userName) {
        try {
            String JSONCheck = Jsoup.parse(elements.toString()).select("script[type=text/javascript]:not([src~=[a-zA-Z0-9./\\s]+)").first().html();
            JSONCheck = JSONCheck.replace("window._sharedData = ", "");

            JSONObject myjson = new JSONObject(JSONCheck).getJSONObject("entry_data");
            myjson = myjson.getJSONArray("PostPage").getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
            JSONArray jsonArray = myjson.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");

            for (int i=0;i < jsonArray.length();i++) {
                String linkToDownload;
                String userFullName;
                userName = myjson.getJSONObject("owner").getString("username");

                try {
                    userFullName = myjson.getJSONObject("owner").getString("full_name");

                    if (userFullName.isEmpty()) {
                        userFullName = myjson.getJSONObject("owner").getString("username");
                    }
                } catch (Throwable t) {
                    userFullName = userName;
                }

                if (Helper.getSettings("Username")) {
                    if (!userFullName.isEmpty()) {
                        userName = userFullName;
                    }
                }

                try {
                    fileType = "Video";
                    linkToDownload = jsonArray.getJSONObject(i).getJSONObject("node").getString("video_url");

                    try {
                        notificationTitle = Helper.getResourceString(context, R.string.username_thing, userName, fileType);
                    } catch (Throwable t) {
                        notificationTitle = userName + "'s " + fileType;
                    }
                } catch (Throwable t) {
                    fileType = "Image";
                    linkToDownload= jsonArray.getJSONObject(i).getJSONObject("node").getString("display_url");

                    try {
                        notificationTitle = Helper.getResourceString(context, R.string.username_thing, userName, fileType);
                    } catch (Throwable t2) {
                        notificationTitle = userName + "'s " + fileType;
                    }
                }

                String fileFormat;
                String mediaId = jsonArray.getJSONObject(i).getJSONObject("node").getString("id");
                String userId = myjson.getJSONObject("owner").getString("id");
                String date = Helper.getDate(System.currentTimeMillis() / 1000);
                String filenameExtension;

                try {
                    if (!jsonArray.getJSONObject(i).getJSONObject("node").getString("video_url").equals("")) {
                        filenameExtension = "mp4";
                    } else {
                        filenameExtension = "jpg";
                    }
                } catch (Throwable t) {
                    filenameExtension = "jpg";
                }

                if (!Helper.getSetting("FileFormat").equals("Instagram") && !Helper.getSetting("File").equals("Instagram")) {
                    fileFormat = Helper.getSetting("FileFormat");
                    fileFormat = fileFormat.replace("Username", userName);
                    fileFormat = fileFormat.replace("MediaID", mediaId);
                    fileFormat = fileFormat.replace("UserID", userId);
                    fileFormat = fileFormat.replace("Date", date);
                    fileFormat = fileFormat + "." + filenameExtension;
                } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                    fileFormat = Helper.getSetting("FileFormat");
                    fileFormat = fileFormat.replace("Username", userName);
                    fileFormat = fileFormat.replace("MediaID", mediaId);
                    fileFormat = fileFormat.replace("UserID", userId);
                    fileFormat = fileFormat.replace("Date", date);
                    fileFormat = fileFormat + "." + filenameExtension;
                } else {
                    fileFormat = userName + "_" + mediaId + "_" + userId + "." + filenameExtension;
                }

                if (Helper.getSettings("URLFileName")) {
                    int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
                    fileFormat = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
                }

                fileName = fileFormat;

                long longId = System.currentTimeMillis() / 1000;
                Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context, false);
            }
        } catch (Throwable t) {
            Helper.setError("Get Multi Post Failed - " +t);
            Helper.setPush("Private Account - Trying Image1");
            String linkToDownload = fallBackURL.replaceAll("media123;", "");
            linkToDownload = "notification" + linkToDownload;

            long longId = System.currentTimeMillis() / 1000;
            Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, context, false);
        }
    }

    static String getNotificationDate(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("taken_at_timestamp");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationFileName(JSONObject jsonObject, String fileExtension, String fileName, String userName, Context context) {
        String fallbackFileName = fileName;
        try {
            String mediaId = Helper.getNotificationMediaID(jsonObject);
            String userId = Helper.getNotificationUserID(jsonObject);
            String date = Helper.getNotificationDate(jsonObject);
            date = Helper.getDate(Long.parseLong(date));

            if (!Helper.getSetting("FileFormat").equals("Instagram") && !Helper.getSetting("File").equals("Instagram")) {
                fileName = Helper.getSetting("FileFormat");
                fileName = fileName.replace("Username", userName);
                fileName = fileName.replace("MediaID", mediaId);
                fileName = fileName.replace("UserID", userId);
                fileName = fileName.replace("Date", date);
                fileName = fileName + fileExtension;
            } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                fileName = Helper.getSetting("FileFormat");
                fileName = fileName.replace("Username", userName);
                fileName = fileName.replace("MediaID", mediaId);
                fileName = fileName.replace("UserID", userId);
                fileName = fileName.replace("Date", date);
                fileName = fileName + fileExtension;
            } else if (!Helper.getSetting("File").equals("Instagram")) {
                try {
                    String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), context);
                    String itemId = Helper.getNotificationItemID(jsonObject);

                    itemId = itemId + itemToString;

                    fileName = userName + "_" + itemId + fileExtension;
                } catch (Throwable t) {
                    setError("Auto Epoch Failed - " + t);
                }
            } else {
                fileName = userName + "_" + mediaId + "_" + date + fileExtension;
            }
        } catch (Throwable t) {
            fileName = fallbackFileName;
            setError("Badddd Get Username: " +t);
        }

        return fileName;
    }

    static String getNotificationURL(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("shortcode");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationItemID(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationMediaID(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationUserID(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationType(JSONObject json) {
        try {
            return json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("is_video");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getProfileIcon(final String userName) {
        try {
            final Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL("https://www.instagram.com/" + userName + "/?__a=1");
                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());
                        JSONObject jsonObject = new JSONObject(JSONInfo);

                        profileHelper = jsonObject.getJSONObject("graphql").getJSONObject("user").getString("profile_pic_url_hd");

                    } catch (Exception e) {
                        setError("Failed Getting Profile Icon - " +e);
                    }
                }
            };
            checkMedia.start();
            checkMedia.join();

            return profileHelper;
        } catch (Throwable t) {
            setError("Failed Profile Icon Fetch - " +t);
            return null;
        }
    }

    static void getProfileIconHD(final String url) {
        try {
            final Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL(url);
                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());

                        JSONObject jsonObject = new JSONObject(JSONInfo);
                        profileHelper = jsonObject.getJSONObject("user").getJSONObject("hd_profile_pic_url_info").getString("url");
                    } catch (Exception e) {
                        Helper.setError("Failed Getting Profile Icon2 - " +e);
                    }
                }
            };
            checkMedia.start();
            checkMedia.join();
        } catch (Throwable t) {
            setError("Failed Profile Icon Fetch - " +t);
        }
    }

    static String getSaveLocation(String saveName) {
        String saveLocation;
        File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/" + saveName + ".txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(notification));

            saveLocation = br.readLine();
            saveLocation = saveLocation.replace("file://", "").replaceAll(" ", "%20");
            br.close();
        }
        catch (Throwable t) {
            saveLocation = "Instagram";
        }

        if (!saveLocation.substring(saveLocation.length() - 1).equals("/") && !saveLocation.equals("Instagram")) {
            saveLocation = saveLocation + "/";
        }

        return saveLocation;
    }

    static String getSetting(String name) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/" + name +".txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            text.append("Instagram");
        }

        return text.toString();
    }

    static String getString(Context context, int resourceID) {
        String stringResult = "Instagram";
        try {
            Context packageContext = context.createPackageContext("com.ihelp101.instagram", Context.CONTEXT_IGNORE_SECURITY);
            stringResult = packageContext.getString(resourceID);
        } catch (Exception e) {

        }
        return stringResult;
    }

    static String getString(Context context, int id, Object...formatArgs) {
        return getOwnResources(context).getString(id, formatArgs);
    }

    static String getString(Context context, String id, String packageName) {
        return context.getResources().getString(context.getResources().getIdentifier(id, "string", packageName));
    }

    static String getRawString(Context context) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.strings_kurdish)));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
            return result.toString();
        } catch (Exception e) {
            return "Instagram" ;
        }
    }

    static String getResourceString (Context context, int id) {
        try {
            if (!Helper.getSetting("Language").equals("Instagram")) {
                String originalString = Helper.getSetting("Language");
                String string;
                String[] split = originalString.split("<string name=\"" + getResourcesForPackage(context, "com.ihelp101.instagram").getResourceEntryName(id) + "\">");

                string = split[1];
                string = string.split("</string>")[0];

                return string;
            } else {
                return getString(context, id);
            }
        } catch (Throwable t) {
            return context.getString(id);
        }
    }

    static String getResourceString (Context context, int id, String id2) throws Throwable {
        try {
            if (!Helper.getSetting("Language").equals("Instagram")) {
                String originalString = Helper.getSetting("Language");
                String idString = getResourcesForPackage(context, "com.ihelp101.instagram").getResourceEntryName(id);
                String string;
                String[] split = originalString.split("<string name=\"" + idString + "\">");

                string = split[1];
                string = string.split("</string>")[0];

                string = string.replace("%1$s", idString);
                string = string.replace("%2$s", id2);

                return string;
            } else {
                return getString(context, id, id2);
            }
        } catch (Throwable t) {
            return getString(context, id, id2);
        }
    }

    static String getResourceString (Context context, int id, String id2, String id3) throws Throwable {
        try {
            if (!Helper.getSetting("Language").equals("Instagram")) {
                String originalString = Helper.getSetting("Language");
                String idString = getResourcesForPackage(context, "com.ihelp101.instagram").getResourceEntryName(id);
                String string;
                String[] split = originalString.split("<string name=\"" + idString + "\">");

                string = split[1];
                string = string.split("</string>")[0];

                string = string.replace("%1$s", id2);
                string = string.replace("%2$s", id3);

                return string;
            } else {
                return getString(context, id, id2, id3);
            }
        } catch (Throwable t) {
            return getString(context, id, id2, id3);
        }
    }

    static String getTagged(Object mMedia, String TAGGED_HOOK_CLASS, String FULLNAME_HOOK, String USER_CLASS_NAME, String USERNAME_HOOK, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            String userNames = "";
            String userName = getUsername(mMedia, FULLNAME_HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam);
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                Field[] fields = object.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.toString().contains("UserInfo")) {
                        Object object1 = XposedHelpers.getObjectField(object, field.getName());
                        for (Field field1 : object1.getClass().getDeclaredFields()) {
                            if (field1.getType().equals(String.class)) {
                                try {
                                    String taggedUserName = (String) XposedHelpers.getObjectField(object1, field1.getName());
                                    if (!taggedUserName.matches("[0-9]+") && taggedUserName.length() > 2 && !taggedUserName.contains("https://") && !userNames.contains(taggedUserName + ";") && !taggedUserName.equals(userName)) {
                                        if (Helper.getSettings("Username") && taggedUserName.contains(" ") || !taggedUserName.contains(" ")) {
                                            userNames = userNames + taggedUserName + ";";
                                        }
                                    }
                                } catch (Throwable t) {
                                }
                            }
                        }
                    }
                }
            }

            userNames = getUsernames(mMedia, userNames, FULLNAME_HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam);
            return userNames;
        } catch (Throwable t) {
            return "";
        }
    }

    static String getUsername(Object mMedia, String FULLNAME_HOOK, String USER_CLASS_NAME, String USERNAME_HOOK, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Object mUser = getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        String userName = (String) getObjectField(mUser, USERNAME_HOOK);

        if (Helper.getSettings("Username")) {
            try {
                String userFullName = (String) getObjectField(mUser, FULLNAME_HOOK);
                if (!userFullName.isEmpty()) {
                    userName = userFullName;
                }
            } catch (Throwable t) {
            }
        }

        return userName;
    }

    static String getUsernames(Object mMedia, String userNames, String FULLNAME_HOOK, String USER_CLASS_NAME, String USERNAME_HOOK, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Object mUser = getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        String userName = (String) getObjectField(mUser, USERNAME_HOOK);

        if (!userNames.contains(userName + ";")) {
            userNames = userNames + userName + ";";
        }

        try {
            String userFullName = (String) getObjectField(mUser, FULLNAME_HOOK);
            if (!userFullName.isEmpty() && !userNames.contains(userFullName + ";")) {
                userNames = userNames + userFullName + ";";
            }
        } catch (Throwable t) {
        }

        return userNames;
    }

    static String setFileFormat(String userName, String itemID, String date, String fileExtension, Boolean multi) {
        String fileFormat = "";
        try {
            String mediaID = itemID.replace(itemID.split("_")[1], "");
            String userID = itemID.split("_")[1];

            fileFormat = Helper.getSetting("FileFormat");

            if (!Helper.getSetting("FileFormat").contains("MediaID") && !Helper.getSetting("FileFormat").contains("Date") && multi) {
                fileFormat = fileFormat + "_MediaID";
            }

            fileFormat = fileFormat.replace("Username", userName);
            fileFormat = fileFormat.replace("MediaID", mediaID);
            fileFormat = fileFormat.replace("UserID", userID);
            fileFormat = fileFormat.replace("Date", date);
            fileFormat = fileFormat + "." + fileExtension;

        } catch (Throwable t) {
            fileFormat = userName + "_" + itemID + "." + fileExtension;
        }

        return fileFormat;
    }

    static String checkSave(String saveLocation, String userName, String fileName) {
        try {
            if (saveLocation.equals("Instagram")) {
                saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
                if (Helper.getSettings("Folder")) {
                    saveLocation = saveLocation + userName + "/";
                }

                Uri uri = Uri.parse(saveLocation);

                File directory = new File(uri.getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            } else {
                if (Helper.getSettings("Folder")) {
                    saveLocation = saveLocation + userName + "/";
                }

                Uri uri = Uri.parse(saveLocation);

                File directory = new File(uri.getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            }
        } catch (Exception e) {
            setError("Save Location Check Failed: " + e);
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
        }

        return (saveLocation + fileName).replace("%20", " ");
    }

    static void checkMarshmallowPermission(String linkToDownload, String fileName, String SAVE, String fileType, String userName, String notificationTitle, long epoch, Context context) {
        try {
            File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Hooks.txt");

            BufferedReader br = new BufferedReader(new FileReader(notification));

            br.readLine();
            br.close();
        } catch (Throwable t) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                NotificationCompat.Builder mBuilder;
                NotificationManager mNotifyManager;

                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setContentTitle("Storage Permission Denied").setContentText("Click to open App Settings").setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:com.instagram.android"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(3273, mBuilder.build());

                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, epoch, context);
            }
        }
    }

    static void downloadOrPass(String linkToDownload, String fileName, String fileType, String userName, String notificationTitle, long epoch, Context context, boolean passed){
        String saveLocation = Helper.getSaveLocation(fileType);

        if (saveLocation.contains(";")) {
            saveLocation = Helper.getSaveLocation(fileType).split(";")[1];
        }

        if (Helper.getSettings("URLFileName")) {
            int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
            fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
        }

        if (linkToDownload.contains("/vp/")) {
            String urlHash = "/" + linkToDownload.split("/")[3] + "/" + linkToDownload.split("/")[4] + "/" +  linkToDownload.split("/")[5];
            //linkToDownload = linkToDownload.replace(urlHash, "");
        }

        if (userName.contains(" / ") | userName.contains(" | ") ) {
            userName = userName.replace(" / ", "");
            userName = userName.replace(" | ", "");
            fileName = fileName.replace(" / ", "");
            fileName = fileName.replace(" | ", "");
        }

        checkMarshmallowPermission(linkToDownload, fileName, saveLocation, fileType, userName, notificationTitle, epoch, context);

        if (!Helper.getSaveLocation(fileType).contains("com.android.externalstorage.documents") && !Helper.getSettings("Pass") || passed) {
            saveLocation = Helper.checkSave(saveLocation, userName, fileName);

            if (new File(saveLocation).exists()) {
                fileExist(context, saveLocation, notificationTitle);
            } else {
                new Download(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, saveLocation, notificationTitle, userName, fileType, String.valueOf(epoch));
            }
        } else {
            Helper.passDownload(linkToDownload, saveLocation, notificationTitle, fileName, fileType, userName, epoch, context);
        }
    }

    static void downloadSelection(Object mMedia, SparseBooleanArray sparseBooleanArray) {

    }

    static void fileExist(Context context, String saveLocation, String notificationTitle) {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int id = 1;
        Bitmap icon;

        Random r = new Random();
        id = r.nextInt(9999999 - 65) + 65;

        String iconString = "iVBORw0KGgoAAAANSUhEUgAAAJAAAACQCAYAAADnRuK4AAAEH0lEQVR42u3dX4hMURzA8V2s/ytJiiJFpM3u3HvHnyW1JSRvIqWUB8ULL14ktXfvvUsbUfsgk7bZPefsbk288ODF3wcktYk8UDwRmz8r8n93x7m7Hqhdlrkzc/98P3XyqvP7NnPmzp27FRUAAAAAAAAAAAAAAAAAAAAAACD+Vnjd801PPjFd2Rv8UrfY4ZhL2dmFetjv9coHvSxHPmKHCYiAQEAgIBAQCIiAQEAgIBAQCIiACIiAQEAgIBAQAREQAYGAQEAgIAIiIAICAYGAQEAgIAICAYGAQEAgIAICAYGAQEAgIAIiIAICAYGAQEAEREAEBALCiCw7MzXliN16SHsLXp48rP/9XIyADFe+DOT/qJfhiV01dm4i0w+CbY8zHHGsGEMP7dKhV+TzlQw/ILXHxTS9sTcSEtAlK5OpYuoBMxvlYr25r2Iez3OrWc5l2sWKyO3cGuN4vppNYhNTLvah2hWtMYxn0HSFx3RLdB4yXHEnXodmcbXBzk5muqV6FXLU8hidh55Zbvsiplrq89DwtaFvEY/ni37r2so0y6DBvjbB8OTZKAek34pP+Ne5mGaZ1Nu5WXoQPREN6ObSlrZqplhmhifSehjvIhbP27qjHUuYXmiuDw19hxShT11qB1MLE32O0INREQnoDAMLoZSdnamH8zjk8fTU2LnpTCus5yGno14P6UNIP3G9STV3pphS2C8yuuqgHthAyALqtxy5h+lEIaBMpsp01fmQHZqz/nUrphOVt7LG7nkhOg/dt+yu2UwlahE1qfV6eB/LHM/7tNe5kmlEUT5faXjiSDkDsjxxgEFEmH9jun+LaFkCcuS57bnceKYQj/PQs5K+8rjyqf89HbsfE+kmuUEP9nuJAvqkX33WsetxeyVy1dHhW0eLGs+A5alD7HYM+beM6gFfL25A4uLi1tZJ7HZcLzK67Yv0oF8U69xj2WIBuxz/t7Jtw7eSBnvuMZrVZnY3CfxbPxxxKuDfxTv8FDlBVtlqhh787YACusK5J4H0gXeZHv7rAuN5wbknyYdqR+0s4KN9v9Ust7CLCeZ/1eDfYvo/P0Ue+kkOsLalrVoHce9ff5JTfzI3hd3DkLTTYego+sb6JLK0K5eya/j9+pAj9vnnmr89gsU/N7FbGO08pP7y6nOanyJj9E9ldtds05EPRwnorn/9iF3CH5meWj3Co4D76pz2GnYHYzsPuWr/L9eHBv3H7rIrGLPhW2FV7ufTw9q4NRX/rLZRzNHnoQuce/D/h2qe1wwAAAAAAAAAJZLy5BrLVRtZyVn+A9wDC8h05YNE/c1Slv93Wy8TEIuAWATEiuSfopLXCIhV0OOJCYhVyHpAQCwCYhEQi4AIiIAKC8h/umkvK1HrOl/iAQAAAAAAAAAAAAAAAAAAAADwBz8AZCjlxOhH1GcAAAAASUVORK5CYII=";

        byte[] decodedByte = Base64.decode(iconString, 0);
        icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

        String downloadComplete;

        try {
            downloadComplete = Helper.getResourceString(context, R.string.Download_Completed);
        } catch (Throwable t) {
            downloadComplete = "Download Completed";
        }

        Toast(downloadComplete, context);

        if (!Helper.getSettings("Notification")) {
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);

            mBuilder.setContentText(downloadComplete).setTicker(downloadComplete);

            mBuilder.setContentTitle(notificationTitle)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setLargeIcon(icon)
                    .setAutoCancel(true);

            Intent notificationIntent  = new Intent();
            PendingIntent contentIntent;

            if (Build.VERSION.SDK_INT >= 24) {
                notificationIntent.setAction("com.ihelp101.instagram.CLICK");
                notificationIntent.putExtra("File", saveLocation);

                contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                notificationIntent.setAction(Intent.ACTION_VIEW);

                File file = new File(saveLocation);
                if (saveLocation.contains("jpg")) {
                    notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                } else {
                    notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                }
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            mBuilder.setContentIntent(contentIntent);
            mNotifyManager.notify(id, mBuilder.build());
        }
    }

    static void setIcon(Context context, String alias, boolean visible) {
        PackageManager packageManager = context.getPackageManager();
        int state = visible ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ComponentName aliasName = new ComponentName(context, context.getPackageName() + "." + alias);
        packageManager.setComponentEnabledSetting(aliasName, state, PackageManager.DONT_KILL_APP);
    }

    static void setError(String data) {
        System.out.println("Set Error: " +data);
        if (data.equals("XInsta Initialized")) {
            try {
                if (Helper.getFolderSize(new File(Environment.getExternalStorageDirectory(), ".Instagram/Error.txt")) > 20000) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String time = sdf.format(new Date());

                        data = time + " - " + data;

                        File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        File gpxfile = new File(root, "Error.txt");
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.append(data);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {

                    }
                } else {
                    setError("---------------------------");
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String time = sdf.format(new Date());

                        data = time + " - " + data;

                        File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        File file = new File(root, "Error.txt");
                        BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                        buf.newLine();
                        buf.append(data);
                        buf.close();
                    } catch (IOException e) {

                    }
                }
            } catch (Exception e) {
            }
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String time = sdf.format(new Date());

                data = time + " - " + data;

                File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File file = new File(root, "Error.txt");
                BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                buf.newLine();
                buf.append(data);
                buf.close();
            } catch (IOException e) {

            }
        }
    }

    static void setPush(String data) {
        try {
            if (Helper.getFileCount(new File(Environment.getExternalStorageDirectory(), ".Instagram/Notification Log.txt")) > Integer.parseInt(Helper.getSetting("Filesize"))) {
                File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File file = new File(root, "Notification Log.txt");
                File to = new File(root, "Notification Log.txtold");

                file.renameTo(to);
                Helper.setPush(data);
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                    String time = sdf.format(new Date());

                    data = time + " - " + data;

                    File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File file = new File(root, "Notification Log.txt");
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                    buf.newLine();
                    buf.append(data);
                    buf.close();
                } catch (IOException e) {

                }
            }
        } catch (Throwable t) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                String time = sdf.format(new Date());

                data = time + " - " + data;

                File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File file = new File(root, "Notification Log.txt");
                BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                buf.newLine();
                buf.append(data);
                buf.close();
            } catch (IOException e) {
            }
        }
    }

    static void setSetting(String name, String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, name + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
    }

    static void passDownload(String linkToDownload, String SAVE, String notificationTitle, String fileName, String fileType, String userName, long epoch, Context mContext) {
        Intent downloadIntent = new Intent();
        downloadIntent.setPackage("com.ihelp101.instagram");
        downloadIntent.setAction("com.ihelp101.instagram.PASS");
        downloadIntent.putExtra("URL", linkToDownload);
        downloadIntent.putExtra("SAVE", SAVE);
        downloadIntent.putExtra("Notification", notificationTitle);
        downloadIntent.putExtra("Filename", fileName);
        downloadIntent.putExtra("Filetype", fileType);
        downloadIntent.putExtra("User", userName);
        downloadIntent.putExtra("Epoch", epoch);
        mContext.startService(downloadIntent);
    }

    static void passLiveStory(Context context, String linkToDownload, String userName, String fileName) {
        Intent downloadIntent = new Intent();
        downloadIntent.setPackage("com.ihelp101.instagram");
        downloadIntent.setAction("com.ihelp101.instagram.LIVE");
        downloadIntent.putExtra("SAVE", linkToDownload);
        downloadIntent.putExtra("Username", userName);
        downloadIntent.putExtra("Filename", fileName);
        context.startService(downloadIntent);
    }

    static void writeToFollower(String name) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();

            }
            File file = new File(root, "Following.txt");
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.newLine();
            buf.append(name);
            buf.close();
        } catch (Throwable t) {
        }
    }

    static void resetFollower() {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();

            }
            File file = new File(root, "Following.txt");
            file.delete();
        } catch (Throwable t) {
            XposedBridge.log("Issue: " +t);
        }
    }

    static void Toast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
