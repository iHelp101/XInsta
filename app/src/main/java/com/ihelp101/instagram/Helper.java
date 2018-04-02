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
import android.support.v4.app.NotificationCompat;
import android.support.v4.provider.DocumentFile;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.mayulive.xposed.classhunter.ProfileHelpers;
import com.mayulive.xposed.classhunter.packagetree.PackageTree;
import com.mayulive.xposed.classhunter.profiles.ClassItem;
import com.mayulive.xposed.classhunter.profiles.ClassProfile;
import com.mayulive.xposed.classhunter.profiles.ConstructorProfile;
import com.mayulive.xposed.classhunter.profiles.FieldItem;
import com.mayulive.xposed.classhunter.profiles.MethodProfile;

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

import static com.mayulive.xposed.classhunter.Modifiers.ABSTRACT;
import static com.mayulive.xposed.classhunter.Modifiers.BRIDGE;
import static com.mayulive.xposed.classhunter.Modifiers.ENUM;
import static com.mayulive.xposed.classhunter.Modifiers.EXACT;
import static com.mayulive.xposed.classhunter.Modifiers.FINAL;
import static com.mayulive.xposed.classhunter.Modifiers.INTERFACE;
import static com.mayulive.xposed.classhunter.Modifiers.PROTECTED;
import static com.mayulive.xposed.classhunter.Modifiers.PUBLIC;
import static com.mayulive.xposed.classhunter.Modifiers.STATIC;
import static com.mayulive.xposed.classhunter.Modifiers.SYNTHETIC;
import static com.mayulive.xposed.classhunter.Modifiers.THIS;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static java.lang.reflect.Modifier.PRIVATE;

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
                    PendingIntent contentIntent = PendingIntent.getActivity(context.get(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

                if (save.contains("_LiveVideo.mp4")) {
                    Helper.passLiveStory(save, userName, context.get());
                    if (!Helper.getSettings("Notification")) {
                        mNotifyManager.cancel(12345);
                        mNotifyManager.cancel(id);
                    }
                }
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(context.get(), R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                Helper.Toast(downloadFailed, context.get());
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
                    u = new URL("https://www.instagram.com/" + userName + "/?__a=1");
                } else {
                    linkToDownload = linkToDownload.replaceAll("notification", "");
                    u = new URL(linkToDownload);
                }

                URLConnection c = u.openConnection();
                c.connect();

                String JSONInfo = Helper.convertStreamToString(u.openStream());

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
                        String videoUrl = "https://www.instagram.com/p/" + Helper.getNotificationURL(jsonObject);

                        u = new URL(videoUrl);

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

                    String date = Helper.getNotificationDate(jsonObject);

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, Long.parseLong(date), context.get(), false);
                } else if (descriptionType.equals("false")) {
                    String date = Helper.getNotificationDate(jsonObject);

                    setError("Image: " +fileName);

                    Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, Long.parseLong(date), context.get(), false);
                }
            } catch (Exception e) {
                if (linkToDownload.contains("media123")){
                    Helper.setPush("Private Account");
                    setError("Private Account");
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

    static String getDate (Long epochTime, Context nContext) {
        try {
            String dateFormat = Helper.getSetting("File");

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

    static String getDateEpoch (Long epochTime, Context nContext) {
        try {
            String dateFormat = Helper.getSetting("File");

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

    static String getDateEpochEXIF (Long epochTime, Context nContext) {
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

    static String getDateEpochWithTime (Long epochTime, Context nContext) {
        try {
            String dateFormat = Helper.getSetting("File");

            Date date = new Date(epochTime * 1000L);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace("Month", "MM");
            dateFormat = dateFormat.replace("Day", "dd");
            dateFormat = dateFormat.replace("Year", "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");
            dateFormat = dateFormat + "HHmm";

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
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
                        URL u = new URL("https://www.instagram.com/" + userName + "/?__a=1");
                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());

                        JSONObject jsonObject = new JSONObject(JSONInfo);

                        String postUrl = "https://www.instagram.com/p/" + jsonObject.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("shortcode");
                        Helper.getPostMulti(context, fileName, fileType, fallBackURL, postUrl, notificationTitle, userName);
                    } catch (Exception e) {
                        Helper.setError("Getting Post URL Failed - " +e);
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

                        if (elements.toString().contains("edge_sidecar_to_children")) {
                            Helper.getPostMultiDownload(context, elements, linkToDownload, fileName, fileType, notificationTitle, userName);
                        } else {
                            System.out.println("Single! "  +fileName);
                            new Helper.DownloadNotification(context).execute(linkToDownload, userName, fileName, fileType, notificationTitle);
                        }
                    } catch (Exception e) {
                        Helper.setError("Multi Check Failed: " + e);
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
                String date = Helper.getDate(System.currentTimeMillis() / 1000, context);
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
                    fileFormat = fileFormat.replace("Date", date);
                    fileFormat = fileFormat + "." + filenameExtension;
                } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                    fileFormat = Helper.getSetting("FileFormat");
                    fileFormat = fileFormat.replace("Username", userName);
                    fileFormat = fileFormat.replace("MediaID", mediaId);
                    fileFormat = fileFormat.replace("UserID", userId);
                    fileFormat = fileFormat + "." + filenameExtension;
                } else {
                    fileFormat = userName + "_ " + mediaId + "_ " + userId + "." + filenameExtension;
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
            return json.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("taken_at_timestamp");
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
            date = Helper.getDate(Long.parseLong(date), context);

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
                fileName = userName + "_ " + mediaId + "_ " + date + fileExtension;
            }
        } catch (Throwable t) {
            fileName = fallbackFileName;
            setError("Badddd Get Username: " +t);
        }

        return fileName;
    }

    static String getNotificationURL(JSONObject json) {
        try {
            return json.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("shortcode");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationItemID(JSONObject json) {
        try {
            return json.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationMediaID(JSONObject json) {
        try {
            return json.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationUserID(JSONObject json) {
        try {
            return json.getJSONObject("graphql").getJSONObject("user").getString("id");
        } catch (Throwable t) {
            return null;
        }
    }

    static String getNotificationType(JSONObject json) {
        try {
            return json.getJSONObject("graphql").getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("is_video");
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
                        String url = "https://i.instagram.com/api/v1/users/" + jsonObject.getJSONObject("graphql").getJSONObject("user").getString("id") + "/info/";
                        getProfileIconHD(url);
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
                        Helper.setError("Failed Getting Profile Icon - " +e);
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

    static String getTagged(Object mMedia, String TAGGED_HOOK_CLASS, String userName, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
            String userNames = "";
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                Field[] fields = object.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.toString().contains("UserInfo")) {
                        Object object1 = XposedHelpers.getObjectField(object, field.getName());
                        userNames = userNames + XposedHelpers.getObjectField(object1, XposedHelpers.findFirstFieldByExactType(object1.getClass(), String.class).getName()) + ";";
                    }
                }
            }

            return userNames;
        } catch (Throwable t) {
            return "NOPE";
        }
    }

    static String getUsername(Object mMedia, String FULLNAME__HOOK, String USER_CLASS_NAME, String USERNAME_HOOK, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Object mUser = getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        String userName = (String) getObjectField(mUser, USERNAME_HOOK);

        if (Helper.getSettings("Username")) {
            try {
                String userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
                if (!userFullName.isEmpty()) {
                    userName = userFullName;
                }
            } catch (Throwable t) {
            }
        }

        return userName;
    }

    static String loadProfiledClass (ClassProfile classProfile, PackageTree packageTree, String fallBack) {
        try {
             String className = ProfileHelpers.loadProfiledClass(classProfile, packageTree).getName();

             if (className.equals(null)) {
                 className = fallBack;
             }

             return className;
        } catch (Throwable t) {
            setError(" Experimental Hook Failed - " +classProfile.getKnownPath());
            return fallBack;
        }
    }

    static String checkSave(String SAVE, String userName, String fileName) {
        String saveLocation = SAVE;

        try {
            if (SAVE.equals("Instagram")) {
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

    static String checkSaveProfile(String SAVE, String userName, String fileName) {
        String saveLocation = SAVE;

        try {
            if (SAVE.equals("Instagram")) {
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
        }  catch (Exception e) {
            setError("Profile Save Location Check Failed: " +e);
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
                mBuilder.setContentTitle("Storage Permission Denied").setContentText("Click to open App Settings").setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true);

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
        String SAVE = Helper.getSaveLocation(fileType);

        if (SAVE.contains(";")) {
            SAVE = Helper.getSaveLocation(fileType).split(";")[1];
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

        checkMarshmallowPermission(linkToDownload, fileName, SAVE, fileType, userName, notificationTitle, epoch, context);

        if (!Helper.getSaveLocation(fileType).contains("com.android.externalstorage.documents") && !Helper.getSettings("Pass") || passed) {
            if (fileType.equals("Profile")) {
                SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
            } else {
                SAVE = Helper.checkSave(SAVE, userName, fileName);
            }

            new Download(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, SAVE, notificationTitle, userName, fileType, String.valueOf(epoch));
        } else {
            Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, epoch, context);
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

    static void passLiveStory(String SAVE, String title, Context mContext) {
        Intent downloadIntent = new Intent();
        downloadIntent.setPackage("com.ihelp101.instagram");
        downloadIntent.setAction("com.ihelp101.instagram.LIVE");
        downloadIntent.putExtra("SAVE", SAVE);
        downloadIntent.putExtra("Title", title);
        mContext.startService(downloadIntent);
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

    static ClassProfile getComments() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.comments.d.o");
        newProfile.setKnownPath("com.instagram.feed.comments");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(android.view.GestureDetector.SimpleOnGestureListener.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: onDown
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(android.view.MotionEvent.class)

                                ),

                        //Method #1: onLongPress
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.view.MotionEvent.class)

                                ),

                        //Method #2: onShowPress
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.view.MotionEvent.class)

                                ),

                        //Method #3: onSingleTapUp
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(android.view.MotionEvent.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getDate() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.util.c.d");
        newProfile.setKnownPath("com.instagram.util");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | STATIC | FINAL | EXACT , 	new ClassItem(boolean.class)),	//a

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.Long.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(double.class),
                                        new ClassItem(double.class)

                                ),

                        //Method #2: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(double.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(double.class),
                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PRIVATE | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #5: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(long.class)

                                ),

                        //Method #6: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(long.class)

                                ),

                        //Method #7: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(double.class)

                                ),

                        //Method #8: c
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(double.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {

                });

        return newProfile;
    }

    static ClassProfile getDialog() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.ui.dialog.k");
        newProfile.setKnownPath("com.instagram.ui.dialog");

        newProfile.setMinDepth(0);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.content.Context.class)),	//a
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.view.View.class)),	//c
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//d
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//e
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.widget.CheckBox.class)),	//f
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//g
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//h
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//i
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//j
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.widget.ListView.class)),	//k
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.View.class)),	//l
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.ViewGroup.class)),	//m
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//n
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.View.class)),	//o
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//p
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.widget.TextView.class)),	//q
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.View.class)),	//r
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.ViewGroup.class)),	//s
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.view.View.class)),	//t

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.app.Dialog.class)

                                ),

                        //Method #12: a
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(android.content.DialogInterface.OnClickListener.class),
                                        new ClassItem(android.widget.TextView.class),
                                        new ClassItem(int.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.Context.class)

                                ),

                        //Constructor #1
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(int.class)

                                ),

                        //Constructor #2
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),


                });
        return newProfile;
    }

    static ClassProfile getDirectPrivate() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.direct.fragment.visual.ad");
        newProfile.setKnownPath("com.instagram.direct.fragment");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem("" , PUBLIC | ABSTRACT | EXACT ));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem(android.view.View.OnKeyListener.class),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//A
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//B
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//C
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//D
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(android.graphics.RectF.class)),	//E
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//F
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//G
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//H
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//I
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//J
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//K
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//L
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//M
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//N
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//O
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//P
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//Q
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//R
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//S
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//T
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//U
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//V
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//W
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//X
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//Y
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//Z
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//a
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aa
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//ab
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//ac
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//ad
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//ae
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//af
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(boolean.class)),	//ag
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | ABSTRACT | EXACT )),	//ah
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//ai
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//aj
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(android.view.View.class)),	//b
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//c
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//d
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//e
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//f
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//g
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//h
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//i
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//j
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(android.widget.EditText.class)),	//k
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//l
                        new FieldItem( EXACT , 	new ClassItem(android.text.TextWatcher.class)),	//m
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//n
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//o
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//p
                        new FieldItem( EXACT , 	new ClassItem(android.view.View.class)),	//q
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(java.util.HashSet.class)),	//r
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//s
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//t
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//u
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//v
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//w
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//x
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//y
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//z

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: B
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #1: C
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #2: D
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #5: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #6: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #7: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(float.class)

                                ),

                        //Method #8: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #9: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #10: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #11: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #12: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #13: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(long.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #14: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(float.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #15: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #16: a_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #17: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #18: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #19: b_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #20: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #21: c_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #22: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #23: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #24: getModuleName
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #25: n
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | EXACT )

                                ),

                        //Method #26: o
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )

                                ),

                        //Method #27: onBackPressed
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #28: onCreate
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #29: onCreateView
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(android.view.LayoutInflater.class),
                                        new ClassItem(android.view.ViewGroup.class),
                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #30: onDestroyView
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #31: onKey
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(android.view.View.class),
                                        new ClassItem(int.class),
                                        new ClassItem(android.view.KeyEvent.class)

                                ),

                        //Method #32: onPause
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #33: onResume
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #34: onViewCreated
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.view.View.class),
                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #35: p
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #36: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #37: a
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #38: a
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #39: a
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(android.graphics.RectF.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #40: a
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #41: a
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #42: a
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(float.class)

                                ),

                        //Method #43: b
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #44: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(int.class)

                                ),

                        //Method #45: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #46: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #47: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(int.class),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #48: c
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #49: c
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #50: d
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #51: e
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #52: f
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #53: f
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #54: g
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #55: i
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #56: k
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #57: l
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #58: q
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #59: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #60: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #61: r$1
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #62: u
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),

                        //Method #63: v
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem[0]

                                ),


                });

        return newProfile;
    }

    static ClassProfile getDirectPrivateHelper() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.direct.b.bj");
        newProfile.setKnownPath("com.instagram.direct");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//a
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//b
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//c
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(java.util.List.class)),	//d
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(java.util.HashMap.class)),	//e
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//f

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem(int.class)

                                ),

                        //Method #2: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.util.List.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(java.util.List.class),
                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getDirectPrivateHelper2() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.direct.b.s");
        newProfile.setKnownPath("com.instagram.direct");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String.class)),	//a

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(boolean.class),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(android.content.res.Resources.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {

                });

        return newProfile;
    }

    static ClassProfile getFollowList() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.user.d.d.h");
        newProfile.setKnownPath("com.instagram.user");

        newProfile.setMinDepth(2);
        newProfile.setMaxDepth(2);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem("" , PUBLIC | ABSTRACT | EXACT ));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.content.Context.class)),	//a
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//b
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//c
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(boolean.class)),	//d

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(int.class),
                                        new ClassItem(android.view.View.class),
                                        new ClassItem(android.view.ViewGroup.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #2: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem(boolean.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getFeedOnClick(String feedClass) {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath(feedClass);

        if (feedClass.lastIndexOf(".") != -1) {
            feedClass = feedClass.substring(0, feedClass.lastIndexOf(".")); // not forgot to put check if(endIndex != -1)
        }

        if (feedClass.lastIndexOf(".") != -1) {
            feedClass = feedClass.substring(0, feedClass.lastIndexOf(".")); // not forgot to put check if(endIndex != -1)
        }

        newProfile.setKnownPath(feedClass);

        newProfile.setMinDepth(2);
        newProfile.setMaxDepth(2);
        newProfile.setModifiers(FINAL);

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
        /////////////////////////
        //Interfaces
        /////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem(android.content.DialogInterface.OnClickListener.class)

                });
        /////////////////////////
        //Nested Classes
        /////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
        /////////////////////////
        //Declared fields
        /////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( FINAL | SYNTHETIC | EXACT , 	new ClassItem("com.instagram.feed" , PUBLIC | EXACT )),	//a

                });
        /////////////////////////
        //Declared Methods
        /////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: onClick
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.content.DialogInterface.class),
                                        new ClassItem(int.class)

                                ),


                });
        return newProfile;
    }

    static ClassProfile getFeedHelper() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.o.b.ac");
        newProfile.setKnownPath("com.instagram.feed");

        newProfile.setMinDepth(2);
        newProfile.setMaxDepth(3);
        newProfile.setModifiers(PUBLIC );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(android.app.Dialog.class)),	//b
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//c
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(android.content.DialogInterface.OnDismissListener.class)),	//d
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//e
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//g
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.app.Activity.class)),	//h
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | ABSTRACT | EXACT )),	//i
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | ABSTRACT | EXACT )),	//j
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//k
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//l
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//m
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//n
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(int.class)),	//o
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(int.class)),	//p
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//q
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.os.Handler.class)),	//r
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//s
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//t
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.CharSequence[].class)),	//u
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.CharSequence.class)),	//v
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.CharSequence.class)),	//w
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.CharSequence.class)),	//x
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//y
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//z
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.util.List.class)),	//a
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.Class.class)),	//f

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | THIS | EXACT ),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(android.net.Uri.class),
                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #2: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("" , PUBLIC | EXACT )

                                ),

                        //Method #3: c
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(java.lang.CharSequence[].class),

                                        new ClassItem("" , PUBLIC | THIS | EXACT )

                                ),

                        //Method #4: d
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #5: e
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | THIS | EXACT )

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem(android.app.Activity.class),
                                        new ClassItem("" , PUBLIC | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT ),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT )

                                ),


                });

        return newProfile;
    }

    static ClassProfile getLikeOnClick() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.ui.c.cf");
        newProfile.setKnownPath("com.instagram.feed.ui");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(FINAL );

        newProfile.setTypeParamCount(0);
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.MotionEvent" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #3: b
                        new MethodProfile
                                (
                                        PROTECTED | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.MotionEvent" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #5: onDoubleTap
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("android.view.MotionEvent" , PUBLIC | FINAL | EXACT )

                                ),


                });
        return newProfile;
    }

    static ClassProfile getLikeMulti() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.o.a.q");
        newProfile.setKnownPath("com.instagram.feed");

        newProfile.setMinDepth(2);
        newProfile.setMaxDepth(2);
        newProfile.setModifiers(FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem("com.instagram.ui.c.k" , PUBLIC | ABSTRACT | EXACT ));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem("com.instagram.ui.c.g" , PUBLIC | INTERFACE | ABSTRACT | EXACT )

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( FINAL | SYNTHETIC | EXACT , 	new ClassItem("com.instagram.feed.o.a.r" , PUBLIC | FINAL | EXACT )),	//a

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("com.instagram.ui.c.h" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #2: b
                        new MethodProfile
                                (
                                        PROTECTED | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.MotionEvent" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #3: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("com.instagram.ui.c.h" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #4: onDoubleTap
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("android.view.MotionEvent" , PUBLIC | FINAL | EXACT )

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		EXACT ,

                                        new ClassItem("com.instagram.feed.o.a.r" , PUBLIC | FINAL | EXACT )

                                ),


                });

        return newProfile;
    }

    static ClassProfile getMini() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.s.k");
        newProfile.setKnownPath("com.instagram.feed");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem(android.content.DialogInterface.OnClickListener.class)

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( FINAL | SYNTHETIC | EXACT , 	new ClassItem("" , FINAL | EXACT )),	//a

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: onClick
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.content.DialogInterface.class),
                                        new ClassItem(int.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		EXACT ,

                                        new ClassItem("" , FINAL | EXACT )

                                ),


                });

        return newProfile;
    }

    static ClassProfile getModel() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.model.a.a");
        newProfile.setKnownPath("com.instagram.model");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.util.List.class)),	//a

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(float.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem(int.class)

                                ),

                        //Method #2: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(android.content.Context.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem[0]

                                ),


                });

        return newProfile;
    }

    static ClassProfile getNotification() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.notifications.push.j");
        newProfile.setKnownPath("com.instagram.notifications.push");

        newProfile.setMinDepth(0);
        newProfile.setMaxDepth(0);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.content.Context.class)),	//e	//a
                        new FieldItem( PRIVATE | STATIC | FINAL | EXACT , 	new ClassItem(java.util.HashMap.class)),	//d

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {

                        //Method #4: a
                        new MethodProfile
                                (
                                        STATIC | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #6: b
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PRIVATE | EXACT ,

                                        new ClassItem(android.content.Context.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getProfile() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.profile.h.ez");
        newProfile.setKnownPath("com.instagram.profile");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem("android.content.DialogInterface.OnClickListener" , PUBLIC | STATIC | INTERFACE | ABSTRACT | EXACT )

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: onClick
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.content.DialogInterface" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem(int.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getProfileLiked() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.profile.h.bq");
        newProfile.setKnownPath("com.instagram.profile");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//g

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #1: getModuleName
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #2: hasItems
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #3: hasMoreItems
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #4: i
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #5: isFailed
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #6: isLoadMoreVisible
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #7: isLoading
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #8: loadMore
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #9: m_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #10: onCreate
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #11: onCreateView
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(android.view.LayoutInflater.class),
                                        new ClassItem(android.view.ViewGroup.class),
                                        new ClassItem(android.os.Bundle.class)

                                ),

                        //Method #12: onDestroy
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #13: onScroll
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.widget.AbsListView.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #14: onScrollStateChanged
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.widget.AbsListView.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #15: onViewCreated
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.view.View.class),
                                        new ClassItem(android.os.Bundle.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getProfileLongPress() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.profile.e.g");
        newProfile.setKnownPath("com.instagram.profile");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(android.content.Context.class)),
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(boolean.class))
                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(int.class),
                                        new ClassItem(android.view.View.class),
                                        new ClassItem(android.view.ViewGroup.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #2: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PRIVATE | STATIC | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(int.class),
                                        new ClassItem(android.view.ViewGroup.class)
                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class)
                                ),

                        //Method #5: a
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class)
                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,
                                        new ClassItem(android.content.Context.class),
                                        new ClassItem(android.app.Activity.class),
                                        new ClassItem(boolean.class)
                                ),


                });

        return newProfile;
    }

    static ClassProfile getSponsored(String feedClass) {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath(feedClass);

        if (feedClass.lastIndexOf(".") != -1) {
            feedClass = feedClass.substring(0, feedClass.lastIndexOf(".")); // not forgot to put check if(endIndex != -1)
        }

        newProfile.setKnownPath(feedClass);

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem("" , PUBLIC | ABSTRACT | EXACT ));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//A
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//B
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//C
                        new FieldItem( FINAL | EXACT , 	new ClassItem(android.content.Context.class)),	//a
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | ABSTRACT | EXACT )),	//b
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//c
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//d
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//e
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//f
                        new FieldItem( FINAL | EXACT , 	new ClassItem(boolean.class)),	//g
                        new FieldItem( FINAL | EXACT , 	new ClassItem(boolean.class)),	//h
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//i
                        new FieldItem( FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//j
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//k
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//l
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//m
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//n
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//o
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//p
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//q
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//r
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//s
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//t
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//u
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//v
                        new FieldItem( EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//w
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),	//x
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//y
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("" , PUBLIC | EXACT )),	//z

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(android.view.View.class),

                                        new ClassItem(int.class),
                                        new ClassItem(android.view.View.class),
                                        new ClassItem(android.view.ViewGroup.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #2: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem("" , PUBLIC | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(boolean.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),

                        //Constructor #1
                        new ConstructorProfile
                                (		PRIVATE | EXACT ,

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem("" , PUBLIC | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT ),
                                        new ClassItem(boolean.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT ),
                                        new ClassItem("" , PUBLIC | FINAL | EXACT )

                                ),


                });

        return newProfile;
    }

    static ClassProfile getStory() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.reels.p.t");
        newProfile.setKnownPath("com.instagram.reels");

        newProfile.setMinDepth(1);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem(android.content.DialogInterface.OnClickListener.class)

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( FINAL | SYNTHETIC | EXACT , 	new ClassItem(boolean.class)),	//b
                        new FieldItem( FINAL | SYNTHETIC | EXACT , 	new ClassItem(android.content.DialogInterface.OnDismissListener.class)),	//c

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: onClick
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(android.content.DialogInterface.class),
                                        new ClassItem(int.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		EXACT ,
                                        new ClassItem(boolean.class),
                                        new ClassItem(android.content.DialogInterface.OnDismissListener.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getStoryGallery() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.common.gallery.j");
        newProfile.setKnownPath("com.instagram.common.gallery");

        newProfile.setMinDepth(0);
        newProfile.setMaxDepth(0);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {
                        new ClassItem(java.util.concurrent.Callable.class)

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(android.content.ContentResolver.class)),	//g
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(int.class)),	//h
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(int.class)),	//i
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(int.class)),	//j
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(boolean.class)),	//k
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.util.concurrent.Executor.class)),	//a
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String[].class)),	//b
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String[].class)),	//c
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String[].class)),	//d
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String.class)),	//e
                        new FieldItem( PUBLIC | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.String.class)),	//f

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: call
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(java.lang.Object.class)

                                ),


                });
/////////////////////////
//Declared Constructors
/////////////////////////
        newProfile.setDeclaredConstructors(new ConstructorProfile[]
                {
                        //Constructor #0
                        new ConstructorProfile
                                (		PUBLIC | EXACT ,

                                        new ClassItem(android.content.ContentResolver.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getStoryTime() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.reels.fragment.df");
        newProfile.setKnownPath("com.instagram.reels.fragment");

        newProfile.setMinDepth(0);
        newProfile.setMaxDepth(1);
        newProfile.setModifiers(PUBLIC );
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem("android.content.DialogInterface.OnDismissListener" , PUBLIC | STATIC | INTERFACE | ABSTRACT | EXACT )),	//A
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(java.lang.Runnable.class)),	//G
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(java.lang.Runnable.class)),	//H
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(java.util.Set.class)),	//I
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//O
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//P
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//R
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//S
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//T
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//Z
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.util.ArrayList.class)),	//aA
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//aB
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//aC
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.util.HashMap.class)),	//aD
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//aL
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.util.HashMap.class)),	//aM
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//aN
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aP
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aQ
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//aR
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(long.class)),	//aS
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//aT
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//aV
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aW
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.util.HashMap.class)),	//aX
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//aY
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//aZ
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//aa
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(long.class)),	//ab
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem(java.util.Set.class)),	//ac
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ae
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.lang.String.class)),	//af
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//ag
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//ah
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//ai
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//aj
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//ak
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//al
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//am
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//an
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ao
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//ap
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aq
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//ar
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//as
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//at
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//au
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//av
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(java.util.ArrayList.class)),	//az
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),	//b
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//ba
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Runnable.class)),	//bb
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//bd
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//be
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//bf
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//bi
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),	//bj
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(float.class)),
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),	//g
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),	//h
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),	//i
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),	//j
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem("android.widget.TextView" , PUBLIC | EXACT )),
                        new FieldItem( EXACT , 	new ClassItem("android.text.TextWatcher" , PUBLIC | INTERFACE | ABSTRACT | EXACT )),
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),
                        new FieldItem( EXACT , 	new ClassItem("android.view.View" , PUBLIC | EXACT )),
                        new FieldItem( PRIVATE | STATIC | FINAL | EXACT , 	new ClassItem(java.lang.Class.class))
                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: A
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #1: B
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #2: C
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #3: D
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #5: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #6: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #26: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #27: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(float.class)

                                ),

                        //Method #28: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #29: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #30: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #31: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #32: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #33: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(long.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #34: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(float.class),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #35: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #37: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(boolean.class)

                                ),

                        //Method #38: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class)

                                ),

                        //Method #40: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class),
                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #42: a_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #43: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #44: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #45: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #53: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #54: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #55: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class)

                                ),

                        //Method #56: b_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #57: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #58: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #59: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #60: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #65: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #66: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(float.class),
                                        new ClassItem(float.class)

                                ),

                        //Method #67: c_
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | BRIDGE | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #68: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #69: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class),
                                        new ClassItem(float.class)

                                ),

                        //Method #70: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #71: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),


                        //Method #76: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #77: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #80: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | SYNTHETIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.Object.class)

                                ),

                        //Method #81: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #82: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem(float.class),
                                        new ClassItem(float.class)

                                ),

                        //Method #84: getModuleName
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #85: h
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(long.class)

                                ),

                        //Method #86: i
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(long.class)

                                ),

                        //Method #87: isOrganicEligible
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #88: isSponsoredEligible
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #91: l
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #92: m
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #93: n
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #94: o
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #95: onActivityResult
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class),
                                        new ClassItem("android.content.Intent" , PUBLIC | EXACT )

                                ),

                        //Method #96: onBackPressed
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #97: onCreate
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.os.Bundle" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #99: onDestroy
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #100: onDestroyView
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #102: onKey
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(boolean.class),

                                        new ClassItem("android.view.View" , PUBLIC | EXACT ),
                                        new ClassItem(int.class),
                                        new ClassItem("android.view.KeyEvent" , PUBLIC | EXACT )

                                ),

                        //Method #103: onPause
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #104: onResume
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #105: onStart
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #106: onStop
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #107: onViewCreated
                        new MethodProfile
                                (
                                        PUBLIC | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.View" , PUBLIC | EXACT ),
                                        new ClassItem("android.os.Bundle" , PUBLIC | FINAL | EXACT )

                                ),

                        //Method #108: q
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #109: s
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #110: t
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #111: u
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #112: v
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #113: w
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #114: x
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #115: y
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #116: z
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #117: G
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #119: J
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #122: M
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #125: P
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #126: Q
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #127: R
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #128: S
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(boolean.class)

                                ),



                        //Method #131: V
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #134: Y
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),


                        //Method #136: a
                        new MethodProfile
                                (
                                        PRIVATE | STATIC | EXACT ,
                                        new ClassItem(java.lang.String.class),

                                        new ClassItem(java.util.List.class)

                                ),

                        //Method #140: aa
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #141: b
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #146: d
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class)

                                ),

                        //Method #147: e
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class)

                                ),

                        //Method #148: f
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),


                        //Method #151: g
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #155: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #156: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #157: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(void.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #158: r$0
                        new MethodProfile
                                (
                                        PUBLIC | STATIC | EXACT ,
                                        new ClassItem(boolean.class)
                                )
                });
/////////////////////////
//Declared Constructors
/////////////////////////

        return newProfile;
    }

    static ClassProfile getUser() {
            ClassProfile newProfile = new ClassProfile();

            newProfile.setFullPath("com.instagram.user.a.ag");
            newProfile.setKnownPath("com.instagram.user");

            newProfile.setMinDepth(1);
            newProfile.setMaxDepth(1);
            newProfile.setModifiers(PUBLIC | FINAL );

            newProfile.setTypeParamCount(0);
            newProfile.setSuperClass(	new ClassItem(java.lang.Object.class));
/////////////////////////
//Interfaces
/////////////////////////
            newProfile.setInterfaces(new ClassItem[]
                    {
                            new ClassItem("" , PUBLIC | INTERFACE | ABSTRACT | EXACT )

                    });
/////////////////////////
//Nested Classes
/////////////////////////
            newProfile.setNestedClasses(new ClassItem[]
                    {

                    });
/////////////////////////
//Declared fields
/////////////////////////
            newProfile.setDeclaredFields(new FieldItem[]
                    {
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//A
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//B
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//C
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.util.List.class)),	//D
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//E
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//F
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Float.class)),	//G
                            new FieldItem( EXACT , 	new ClassItem("" , FINAL | EXACT )),	//H
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//I
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//J
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//K
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//L
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//M
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//N
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//O
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//P
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.util.List.class)),	//Q
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//R
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//S
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//T
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//U
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//V
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//W
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//X
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//Y
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//Z
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aA
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//aB
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aC
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aD
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//aE
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aF
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aG
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aH
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aI
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aJ
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Long.class)),	//aK
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//aL
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//aM
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aN
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//aO
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//aP
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aQ
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//aR
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aS
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aT
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aU
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//aV
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aW
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//aX
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//aY
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aa
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ab
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ac
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ad
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ae
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//af
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ag
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ah
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ai
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//aj
                            new FieldItem( EXACT , 	new ClassItem(java.lang.String.class)),	//ak
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//al
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//am
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//an
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//ao
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//ap
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//aq
                            new FieldItem( EXACT , 	new ClassItem(java.lang.Boolean.class)),	//ar
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//as
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//at
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//au
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//av
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//aw
                            new FieldItem( EXACT , 	new ClassItem(java.lang.String.class)),	//ax
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//ay
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//az
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//b
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//c
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//d
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//e
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//f
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//g
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//h
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//i
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//j
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//k
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Boolean.class)),	//l
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//m
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//n
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//o
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//p
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.String.class)),	//q
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//r
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//s
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//t
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(boolean.class)),	//u
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//v
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//w
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )),	//x
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//y
                            new FieldItem( PUBLIC | EXACT , 	new ClassItem(java.lang.Integer.class)),	//z
                            new FieldItem( PUBLIC | STATIC | EXACT , 	new ClassItem("" , PUBLIC | FINAL | EXACT )),	//a

                    });
/////////////////////////
//Declared Methods
/////////////////////////
            newProfile.setDeclaredMethods(new MethodProfile[]
                    {
                            //Method #0: A
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #1: B
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #2: C
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #3: D
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #4: E
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #5: F
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.Integer.class)

                                    ),

                            //Method #6: G
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #7: H
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #8: I
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #9: J
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #10: K
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #11: L
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #12: M
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #13: N
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #14: O
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #15: P
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #16: Q
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #17: R
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #18: S
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #19: T
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.Boolean.class)

                                    ),

                            //Method #20: U
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #21: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #22: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #23: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem("" , PUBLIC | FINAL | THIS | EXACT )

                                    ),

                            //Method #24: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem("" , PUBLIC | FINAL | EXACT )

                                    ),

                            //Method #25: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #26: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem(java.lang.Boolean.class)

                                    ),

                            //Method #27: a
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem(boolean.class)

                                    ),

                            //Method #28: b
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #29: b
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #30: b
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem(java.lang.Boolean.class)

                                    ),

                            //Method #31: b
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem(boolean.class)

                                    ),

                            //Method #32: c
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #33: c
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class),

                                            new ClassItem(boolean.class)

                                    ),

                            //Method #34: d
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #35: e
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #36: equals
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class),

                                            new ClassItem(java.lang.Object.class)

                                    ),

                            //Method #37: f
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),

                            //Method #38: g
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #39: h
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #40: hashCode
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(int.class)

                                    ),

                            //Method #41: i
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #42: j
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #43: k
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #44: l
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #45: m
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #46: n
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #47: o
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #48: p
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #49: q
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #50: r
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.util.List.class)

                                    ),

                            //Method #51: s
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #52: t
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #53: toString
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(java.lang.String.class)

                                    ),

                            //Method #54: u
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #55: v
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #56: w
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(void.class)

                                    ),

                            //Method #57: x
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #58: y
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #59: z
                            new MethodProfile
                                    (
                                            PUBLIC | FINAL | EXACT ,
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #60: a
                            new MethodProfile
                                    (
                                            PUBLIC | STATIC | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),

                                            new ClassItem("" , PUBLIC | ABSTRACT | EXACT )

                                    ),

                            //Method #61: a
                            new MethodProfile
                                    (
                                            PRIVATE | STATIC | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),

                                            new ClassItem("" , PUBLIC | ABSTRACT | EXACT ),
                                            new ClassItem(boolean.class)

                                    ),

                            //Method #62: b
                            new MethodProfile
                                    (
                                            PUBLIC | STATIC | EXACT ,
                                            new ClassItem("" , PUBLIC | FINAL | THIS | EXACT ),

                                            new ClassItem("" , PUBLIC | ABSTRACT | EXACT )

                                    ),

                            //Method #63: c
                            new MethodProfile
                                    (
                                            PUBLIC | STATIC | EXACT ,
                                            new ClassItem(java.lang.String.class),

                                            new ClassItem("" , PUBLIC | FINAL | ENUM | EXACT )

                                    ),


                    });
/////////////////////////
//Declared Constructors
/////////////////////////
            newProfile.setDeclaredConstructors(new ConstructorProfile[]
                    {
                            //Constructor #0
                            new ConstructorProfile
                                    (		PUBLIC | EXACT ,

                                            new ClassItem[0]

                                    ),


                    });

        return newProfile;
    }

    static ClassProfile getVideoLikes() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.feed.ui.text.av");
        newProfile.setKnownPath("com.instagram.feed.ui.text");

        newProfile.setMinDepth(0);
        newProfile.setMaxDepth(0);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
        newProfile.setSuperClass(new ClassItem(java.lang.Object.class));
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        STATIC | EXACT ,
                                        new ClassItem(android.text.SpannableStringBuilder.class),
                                        new ClassItem(android.content.res.Resources.class),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem(boolean.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        STATIC | EXACT ,
                                        new ClassItem(java.lang.CharSequence.class),

                                        new ClassItem(android.content.Context.class),
                                        new ClassItem("" , PUBLIC | EXACT ),
                                        new ClassItem(java.util.List.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem(int.class)

                                ),


                });

        return newProfile;
    }

    static ClassProfile getVideoPlayer() {
        ClassProfile newProfile = new ClassProfile();

        newProfile.setFullPath("com.instagram.video.a.b.q");
        newProfile.setKnownPath("com.instagram.video");

        newProfile.setMinDepth(2);
        newProfile.setMaxDepth(2);
        newProfile.setModifiers(PUBLIC | FINAL );

        newProfile.setTypeParamCount(0);
/////////////////////////
//Interfaces
/////////////////////////
        newProfile.setInterfaces(new ClassItem[]
                {

                });
/////////////////////////
//Nested Classes
/////////////////////////
        newProfile.setNestedClasses(new ClassItem[]
                {

                });
/////////////////////////
//Declared fields
/////////////////////////
        newProfile.setDeclaredFields(new FieldItem[]
                {
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(boolean.class)),	//F
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//G
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//H
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//I
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//J
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//K
                        new FieldItem( PRIVATE | EXACT , 	new ClassItem(int.class)),	//L
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//N
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//O
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//Q
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(int.class)),	//R
                        new FieldItem( PUBLIC | EXACT , 	new ClassItem(long.class)),	//S
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(java.lang.Runnable.class)),	//T
                        new FieldItem( PRIVATE | FINAL | EXACT , 	new ClassItem(long.class)),	//U
                        new FieldItem( FINAL | EXACT , 	new ClassItem("android.content.Context" , PUBLIC | ABSTRACT | EXACT )),	//a
                        new FieldItem( PUBLIC | FINAL | EXACT , 	new ClassItem("android.os.Handler" , PUBLIC | EXACT )),	//c
                        new FieldItem( FINAL | EXACT , 	new ClassItem(java.lang.String.class)),	//f
                        new FieldItem( EXACT , 	new ClassItem(java.lang.Runnable.class)),	//i
                        new FieldItem( EXACT , 	new ClassItem("android.view.Surface" , PUBLIC | EXACT )),	//j
                        new FieldItem( EXACT , 	new ClassItem("android.net.Uri" , PUBLIC | ABSTRACT | EXACT )),	//l
                        new FieldItem( EXACT , 	new ClassItem(boolean.class)),	//m
                        new FieldItem( EXACT , 	new ClassItem(float.class)),	//n
                        new FieldItem( EXACT , 	new ClassItem(boolean.class)),	//o
                        new FieldItem( EXACT , 	new ClassItem(boolean.class)),	//p
                        new FieldItem( EXACT , 	new ClassItem(int.class)),	//q
                        new FieldItem( EXACT , 	new ClassItem(long.class)),	//r
                        new FieldItem( EXACT , 	new ClassItem(long.class)),	//t
                        new FieldItem( EXACT , 	new ClassItem(java.lang.String.class)),	//u

                });
/////////////////////////
//Declared Methods
/////////////////////////
        newProfile.setDeclaredMethods(new MethodProfile[]
                {
                        //Method #0: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #1: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(float.class)

                                ),

                        //Method #2: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class)

                                ),

                        //Method #3: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(int.class),
                                        new ClassItem(int.class)

                                ),

                        //Method #4: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.net.Uri" , PUBLIC | ABSTRACT | EXACT )

                                ),

                        //Method #5: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.net.Uri" , PUBLIC | ABSTRACT | EXACT ),
                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(boolean.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #6: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.Surface" , PUBLIC | EXACT )

                                ),


                        //Method #8: a
                        new MethodProfile
                                (
                                        FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(java.lang.String.class),
                                        new ClassItem(java.lang.String.class)

                                ),

                        //Method #9: a
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem(boolean.class)

                                ),

                        //Method #10: b
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #11: b
                        new MethodProfile
                                (
                                        FINAL | EXACT ,
                                        new ClassItem(void.class),

                                        new ClassItem("android.view.Surface" , PUBLIC | EXACT )

                                ),

                        //Method #12: c
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #13: d
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #14: e
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #15: f
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(void.class)

                                ),

                        //Method #16: g
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #17: h
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #18: i
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #19: j
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #20: k
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #21: l
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #22: m
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #23: n
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #24: o
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(int.class)

                                ),

                        //Method #25: p
                        new MethodProfile
                                (
                                        PUBLIC | FINAL | EXACT ,
                                        new ClassItem(boolean.class)

                                ),

                        //Method #28: u
                        new MethodProfile
                                (
                                        PRIVATE | EXACT ,
                                        new ClassItem(void.class)

                                ),


                });

        return newProfile;
    }
}
