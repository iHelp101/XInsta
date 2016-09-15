package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage, IXposedHookInitPackageResources, Listen {

    Boolean hookCheck;
    CharSequence[] mMenuOptions = null;
    public static Context mContext;
    public static Context nContext;
    public static Context oContext;
    Object mCurrentMediaOptionButton;
    Object mCurrentDirectShareMediaOptionButton;
    Object mUserName;
    Object miniFeedModel;
    Object model;
    Object storiesModel;

    String directShareCheck = "Nope";
    String fileType;
    String firstHook;
    String Hooks = null;
    String[] HooksArray;
    String HookCheck = "No";
    String HooksSave;
    String linkToDownload;
    String fileName;
    String notificationTitle;
    String oldCheck = "No";
    String userName = "";
    String version = "123";

    String ACCOUNT_HOOK_CLASS;
    String COMMENT_HOOK;
    String COMMENT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS2;
    String COMMENT_HOOK_CLASS3;
    String DS_DIALOG_CLASS;
    String DS_MEDIA_OPTIONS_BUTTON_CLASS;
    String DS_PERM_MORE_OPTIONS_DIALOG_CLASS;
    String FEED_CLASS_NAME;
    String FOLLOW_HOOK;
    String FOLLOW_HOOK_2;
    String FOLLOW_HOOK_CLASS;
    String FOLLOW_LIST_CLASS;
    String FULLNAME__HOOK;
    String IMAGE_HOOK_CLASS;
    String ITEMID_HOOK;
    String LIKE_HOOK_CLASS;
    String LOCK_HOOK;
    String LOCK_HOOK2;
    String LOCK_HOOK3;
    String LOCK_HOOK4;
    String LOCK_HOOK5;
    String LOCK_HOOK6;
    String LOCK_HOOK7;
    String LOCK_HOOK8;
    String LOCK_HOOK9;
    String LOCK_HOOK_CLASS;
    String MEDIA_CLASS_NAME;
    String MEDIA_OPTIONS_BUTTON_CLASS;
    String MEDIA_PHOTO_HOOK;
    String MEDIA_VIDEO_HOOK;
    String MINI_FEED_HOOK_CLASS;
    String MINI_FEED_HOOK_CLASS2;
    String NOTIFICATION_CLASS;
    String PERM__HOOK;
    String PROFILE_HOOK_3;
    String PROFILE_HOOK_4;
    String PROFILE_HOOK_CLASS;
    String PROFILE_HOOK_CLASS2;
    String SAVE = "Instagram";
    String SHARE_HOOK_CLASS;
    String SLIDE_HOOK;
    String SLIDE_HOOK_CLASS;
    String STORY_HOOK;
    String STORY_HOOK_CLASS;
    String STORY_HOOK_CLASS2;
    String STORY_TIME_HOOK;
    String STORY_TIME_HOOK2;
    String STORY_TIME_HOOK_CLASS;
    String STORY_TIME_HOOK_CLASS2;
    String SUGGESTION_HOOK_CLASS;
    String TAGGED_HOOK_CLASS;
    String TIME_HOOK_CLASS;
    String USER_CLASS_NAME;
    String USERNAME_HOOK;
    String VIDEO_HOOK;
    String VIDEO_HOOK_CLASS;

    Bitmap icon;
    int count = 0;
    int Freeze = 0;
    int versionCheck;
    LoadPackageParam loadPackageParam;
    String userNameTagged = "";
    TextView followerCount;

    boolean appInstalledOrNot(String uri) {
        PackageManager pm = nContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public boolean canAutoUpdate(String s, int i) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!hookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }

    boolean hookCheck(final String version) {
        hookCheck = true;
        Thread getHooks= new Thread() {
            public void run() {
                String versions;
                try {
                    String url;

                    if (Helper.getSetting("Source").equals("GitHub")) {
                        url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                    } else if (Helper.getSetting("Source").equals("Pastebin")) {
                        url = "http://pastebin.com/raw.php?i=sTXbUFcx";
                    } else if (Helper.getSetting("Source").equals("Alternate Source")) {
                        url = "http://www.snapprefs.com/xinsta/Hooks.txt";
                    } else {
                        url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                    }

                    URL u = new URL(url);
                    URLConnection c = u.openConnection();
                    c.connect();

                    InputStream inputStream = c.getInputStream();

                    versions = Helper.convertStreamToString(inputStream);
                } catch (Exception e) {
                    setError("Failed to fetch hooks - " +e);
                    versions = "Nope";
                }

                if (versions.contains(version)) {
                    hookCheck = true;
                } else {
                    hookCheck = false;
                }
            }
        };
        getHooks.start();

        try {
            getHooks.join();
        }   catch (Exception e) {

        }

        return hookCheck;
    }

    @Override
    public boolean shouldUserUpdate(String s, int i, String s1) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!hookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }

    class Download extends AsyncTask<String, String, String> {

        String link;
        String save;
        String title;

        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int downloadFailed = 1;
        int id = 1;

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
                downloadFailed = 2;
                setError("Download Error - " + t);
                if (!Helper.getSettings("Notification")) {

                    String downloadFailed;

                    try {
                        downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
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
                    downloadComplete = Helper.getResourceString(mContext, R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
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
                } catch (Throwable t) {
                    setError("Scan Failed - " +t);
                }
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

    Object getFieldByType(Object object, Class<?> type) {
        Field f = XposedHelpers.findFirstFieldByExactType(object.getClass(), type);
        try {
            return f.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    String getDate (Long epochTime) {
        try {
            String dateFormat = Helper.getSetting("File");

            Date date = new Date(epochTime * 1000L);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Month), "MM");
            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Day), "dd");
            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Year), "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    String getDateProfileIcon (Long epochTime) {
        try {
            String dateFormat = Helper.getSetting("File");

            Date date = new Date(epochTime);
            TimeZone timeZone = TimeZone.getDefault();

            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Month), "MM");
            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Day), "dd");
            dateFormat = dateFormat.replace(Helper.getResourceString(nContext, R.string.Year), "yyyy");
            dateFormat = dateFormat.replaceAll("/", "");

            DateFormat format = new SimpleDateFormat(dateFormat);
            format.setTimeZone(timeZone);

            return format.format(date);
        } catch (Throwable t) {
            return "Instagram";
        }
    }

    void checkMarshmallowPermission() {
        try {
            File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Hooks.txt");

            BufferedReader br = new BufferedReader(new FileReader(notification));

            br.readLine();
            br.close();
        } catch (Throwable t) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                NotificationCompat.Builder mBuilder;
                NotificationManager mNotifyManager;

                mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setContentTitle("Storage Permission Denied").setContentText("Click to open App Settings").setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true);

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:com.instagram.android"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(3273, mBuilder.build());
            }
        }
    }

    void copyComment(String string) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", string);
        clipboard.setPrimaryClip(clip);
        String copied;

        try {
            copied = Helper.getResourceString(AndroidAppHelper.currentApplication().getApplicationContext(), R.string.Copied);
        } catch (Throwable t) {
            copied = "Comment Copied";
        }

        Toast(copied);
    }

    void downloadOrPass() {
        SAVE = Helper.getSaveLocation(fileType);

        checkMarshmallowPermission();

        if (!SAVE.toLowerCase().contains("com.android.externalstorage.documents")) {
            SAVE = Helper.getSaveLocation(fileType);
            if (fileType.equals("Profile")) {
                SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
            } else {
                SAVE = Helper.checkSave(SAVE, userName, fileName);
            }

            new Download().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, SAVE, notificationTitle);
        } else {
            Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
        }
    }

    @SuppressLint("NewApi")
	void downloadMedia(Object mMedia, String where) throws IllegalAccessException, IllegalArgumentException {
        String filenameExtension;
        String descriptionType;
        int descriptionTypeId;

        try {
            linkToDownload = (String) getObjectField(mMedia, MEDIA_VIDEO_HOOK);
            filenameExtension = "mp4";
            fileType = "Video";
            descriptionTypeId = R.string.video;

            if (linkToDownload.equals("None")) {
                filenameExtension = "";
            }
        } catch (Throwable throwable) {
            try {
                setError("Falling Back To New Video Method");

                List videoList = (List) getObjectField(mMedia, MEDIA_VIDEO_HOOK);

                for (int i=0;i < videoList.size();i++) {
                    String videoUrl = (String) XposedHelpers.getObjectField(videoList.get(i), XposedHelpers.findFirstFieldByExactType(videoList.get(i).getClass(), String.class).getName());
                    if (videoList.contains("_n.mp4")) {
                        i = 999;
                        linkToDownload = videoUrl;
                    } else {
                        linkToDownload = (String) XposedHelpers.getObjectField(videoList.get(0), XposedHelpers.findFirstFieldByExactType(videoList.get(0).getClass(), String.class).getName());
                    }
                }

                filenameExtension = "mp4";
                fileType = "Video";
                descriptionTypeId = R.string.video;

                if (linkToDownload.equals("None")) {
                    filenameExtension = "";
                }
            } catch (Throwable t) {
                setError("Switch Link - Different Media Type");
                if (oldCheck.equals("No")) {
                    try {
                        Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                        Object photo = getFieldByType(mMedia, Image);

                        linkToDownload = (String) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, String.class).getName());
                    } catch (Throwable t2) {
                        setError("Photo Hook Invalid - " + t2);
                        sendError();
                        return;
                    }
                } else {
                    try {
                        linkToDownload = (String) getObjectField(mMedia, MEDIA_PHOTO_HOOK);
                    } catch (Throwable t2) {
                        setError("Link To Download Hook Invalid (Photo) - " + t2);
                        sendError();
                        return;
                    }
                }
                filenameExtension = "jpg";
                fileType = "Image";
                descriptionTypeId = R.string.photo;
            }
        }

        try {
            userNameTagged = "";
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                Field[] fields = object.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.toString().contains("UserInfo")) {
                        Object object1 = XposedHelpers.getObjectField(object, field.getName());
                        userNameTagged = userNameTagged + XposedHelpers.getObjectField(object1, XposedHelpers.findFirstFieldByExactType(object1.getClass(), String.class).getName()) + ";";
                    }
                }
            }
        } catch (NullPointerException e) {
        } catch (Throwable t) {
            setError("Tagged Failed - " +t);
        }

        linkToDownload = linkToDownload.replace("750x750", "");
        linkToDownload = linkToDownload.replace("640x640", "");
        linkToDownload = linkToDownload.replace("480x480", "");
        linkToDownload = linkToDownload.replace("320x320", "");

		// Construct filename
		// username_imageId.jpg
        try {
            descriptionType = Helper.getResourceString(mContext, descriptionTypeId);
        } catch (Throwable t) {
            descriptionType = fileType;
        }

        String downloading;

        try {
            downloading = Helper.getResourceString(mContext, R.string.Downloading, descriptionType);
        } catch (Throwable t) {
            downloading = "Downloading " + descriptionType;
        }

        if (userNameTagged.isEmpty()) {
            Toast(downloading);
        }

		Object mUser;
        try {
            mUser = getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        } catch (Throwable t) {
            setError("mUser Hook Invalid - " + USER_CLASS_NAME);
            sendError();
            return;
        }

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            mUser = mUserName;
        }

		String userFullName;

        try {
            userName = (String) getObjectField(mUser, USERNAME_HOOK);
            userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
        } catch (Throwable t) {
            setError("Failed to get User from Media, using placeholders");
            userName = "username_placeholder";
            userFullName = "Unknown name";
        }

        String itemId;

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            try {
                itemId = getObjectField(model, XposedHelpers.findFirstFieldByExactType(model.getClass(), Long.class).getName()).toString();
            } catch (Throwable t) {
                setError("ItemID Directshare Hook Failed");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
       } else {
            try {
                itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
            } catch (Throwable t) {
                setError("ItemID Hook Invalid - " + t);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        }

        if (!Helper.getSetting("File").equals("Instagram") && !where.equals("Direct")) {
            try {
                Long itemID = (Long) getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), long.class).getName());

                String itemToString = getDate(itemID);

                itemId =  itemId.replace(itemId.split("_")[1], "") + itemToString;
            } catch (Throwable t) {
                try {
                    itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
                } catch (Throwable t2) {
                    setError("ItemID Hook Invalid - " + t2);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                    itemId = sdf.format(new Date());
                }
            }
        }

        fileName = userName + "_" + itemId + "." + filenameExtension;

		if (TextUtils.isEmpty(userFullName)) {
			userFullName = userName;
		}

        if (!userNameTagged.isEmpty()) {
            userFullName = userName;
        }

        try {
            notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userFullName, descriptionType);
        } catch (Throwable t) {
            notificationTitle = userName + "'s " + descriptionType;
        }
        notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

        SAVE = Helper.getSaveLocation(fileType);

        if (!userNameTagged.isEmpty()) {
            userNameTagged = userNameTagged + userName + ";";
            taggedUserAlert(downloading);
            return;
        }

        try {
            if (fileType.equals("Video") && Helper.getSettings("OneTap") && appInstalledOrNot("com.phantom.onetapvideodownload")) {
                setError("One Tap - " +Helper.getSettings("OneTap"));
                Intent intent = new Intent("com.phantom.onetapvideodownload.action.saveurl");
                intent.setClassName("com.phantom.onetapvideodownload", "com.phantom.onetapvideodownload.IpcService");
                intent.putExtra("com.phantom.onetapvideodownload.extra.url", linkToDownload);
                intent.putExtra("com.phantom.onetapvideodownload.extra.title", fileName);
                intent.putExtra("com.phantom.onetapvideodownload.extra.package_name", loadPackageParam.packageName);
                mContext.startService(intent);
            } else {
                if (fileType.equals("Profile")) {
                    SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
                } else {
                    SAVE = Helper.checkSave(SAVE, userName, fileName);
                }

                downloadOrPass();
            }
        } catch (Exception e) {
            setError("Failed To Send DownlPassoad Broadcast - " +e);
        }
	}

    @Override
    public void handleInitPackageResources(final XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.instagram.android"))
            return;

        resparam.res.hookLayout("com.instagram.android", "layout", "row_profile_scoreboard_header", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liparam) throws Throwable {
                followerCount = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("row_profile_header_textview_following_count", "id", "com.instagram.android"));
            }
        });
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.instagram.android")) {
            loadPackageParam = lpparam;

            // Thank you to KeepChat For the Following Code Snippet
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            nContext = (Context) callMethod(activityThread, "getSystemContext");

            versionCheck = nContext.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
            //End Snippet

            setError("XInsta Initialized");
            setError("Instagram Version Code: " + versionCheck);
            setError("Device Codename: " + Build.MODEL);
            setError("Android Version: " + Build.VERSION.RELEASE);

            try {
                PackageInfo pinfo = nContext.getPackageManager().getPackageInfo("com.ihelp101.instagram", 0);
                setError("XInsta Version " + pinfo.versionName);
            } catch (Exception e) {
            }

            startHooks();
        }
    }

    void hookComments() {
        try {
            Class<?> support = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);
            Class<?> support2 = XposedHelpers.findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader);

            XposedHelpers.findAndHookConstructor(COMMENT_HOOK_CLASS3, loadPackageParam.classLoader, support, int.class, support2, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    Object pj = param.args[2];
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    if (Helper.getSettings("Comment")) {
                        copyComment((String) getObjectField(pj, "d"));
                    }
                }
            });

            try {
                final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);

                Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, void.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

                XposedHelpers.findAndHookMethod(Comments, methods[0].getName(), COMMENT_HOOK_CLASS2, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object pj = param.args[0];
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        if (Helper.getSettings("Comment")) {
                            copyComment((String) getObjectField(pj, "d"));
                        }
                    }
                });
            } catch (Throwable t) {
            }
        } catch (Throwable t) {
            try {
                final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);

                Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, boolean.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

                findAndHookMethod(Comments, methods[0].getName(), findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object pj = param.args[0];
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        if (Helper.getSettings("Comment")) {
                            copyComment((String) getObjectField(pj, "d"));
                        }
                    }
                });
            } catch (Throwable t2) {
                try {
                    final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);

                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, void.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

                    XposedHelpers.findAndHookMethod(Comments, methods[0].getName(), COMMENT_HOOK_CLASS2, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object pj = param.args[0];
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                            if (Helper.getSettings("Comment")) {
                                copyComment((String) getObjectField(pj, "d"));
                            }
                        }
                    });
                } catch (Throwable t3) {
                    setError("Comment Failed - " +t3);
                }
            }
        }
    }

    void hookDate() {
        try {
            if (TIME_HOOK_CLASS.equals("Nope") && !Helper.getSetting("Date").equals("Instagram")) {
                final Class<?> Time = XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, CharSequence.class, Context.class);

                XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            String dateFormat = Helper.getSetting("Date");
                            Long epochTime = (Long) XposedHelpers.getObjectField(param.thisObject, (XposedHelpers.findFirstFieldByExactType(Time, long.class)).getName());

                            Date date = new Date(epochTime * 1000L);
                            TimeZone timeZone = TimeZone.getDefault();

                            dateFormat = dateFormat.replaceAll(";", "");

                            if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                dateFormat = dateFormat.replaceAll("a", " a");
                            } else if (dateFormat.contains(":a")) {
                                dateFormat = dateFormat.replaceAll(":a", " a");
                            } else {
                                dateFormat = dateFormat.replaceAll("a", " a");
                            }

                            if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                dateFormat = dateFormat.substring(1, dateFormat.length());
                            }

                            DateFormat format = new SimpleDateFormat(dateFormat);
                            format.setTimeZone(timeZone);

                            param.setResult(format.format(date));
                        } catch (Throwable t) {
                            setError("Date Failed - " + t);
                        }
                    }
                });
            } else if (!Helper.getSetting("Date").equals("Instagram")) {
                try {
                    final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                    XposedHelpers.findAndHookMethod(Time, methods[1].getName(), Context.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            String dateFormat = Helper.getSetting("Date");
                            Long epochTime = (Long) param.args[1];

                            Date date = new Date(epochTime * 1000L);
                            TimeZone timeZone = TimeZone.getDefault();

                            dateFormat = dateFormat.replaceAll(";", "");

                            if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                dateFormat = dateFormat.replaceAll("a", " a");
                            } else if (dateFormat.contains(":a")) {
                                dateFormat = dateFormat.replaceAll(":a", " a");
                            } else {
                                dateFormat = dateFormat.replaceAll("a", " a");
                            }

                            if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                dateFormat = dateFormat.substring(1, dateFormat.length());
                            }

                            DateFormat format = new SimpleDateFormat(dateFormat);
                            format.setTimeZone(timeZone);

                            param.setResult(format.format(date));
                        }
                    });
                } catch (Throwable t2) {
                    try {
                        final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                        Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                        XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, long.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                String dateFormat = Helper.getSetting("Date");
                                Long epochTime = (Long) param.args[1];

                                Date date = new Date(epochTime * 1000L);
                                TimeZone timeZone = TimeZone.getDefault();

                                dateFormat = dateFormat.replaceAll(";", "");

                                if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                    dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                } else if (dateFormat.contains(":a")) {
                                    dateFormat = dateFormat.replaceAll(":a", " a");
                                } else {
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                }

                                if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                    dateFormat = dateFormat.substring(1, dateFormat.length());
                                }

                                DateFormat format = new SimpleDateFormat(dateFormat);
                                format.setTimeZone(timeZone);

                                param.setResult(format.format(date));
                            }
                        });
                    } catch (Throwable t3) {
                        setError("Date Failed2 - " + t3);
                    }
                }
            }
        } catch (Throwable t) {
            setError("Date Failed3 - " +t);
        }
    }

    void hookDirectShare() {
        try {
            if (directShareCheck.equals("Nope")) {
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), PERM__HOOK, injectDownloadIntoCharSequenceHook);
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), PERM__HOOK, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = param.thisObject;
                    }
                });

                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), void.class, DialogInterface.class, int.class);

                findAndHookMethod(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];
                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }

                        String downloadCheck;

                        try {
                            downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                        } catch (Throwable t) {
                            downloadCheck = "Download";
                        }

                        if (downloadCheck.equals(localCharSequence)) {
                            Object mMedia = null;

                            Field[] mCurrentMediaOptionButtonFields =
                                    mCurrentDirectShareMediaOptionButton.getClass().getDeclaredFields();

                            for (Field iField : mCurrentMediaOptionButtonFields) {
                                if (iField.getType().getName().equals(MEDIA_CLASS_NAME)) {
                                    iField.setAccessible(true);
                                    mMedia = iField.get(mCurrentDirectShareMediaOptionButton);
                                    if (mMedia != null) {
                                        break;
                                    }
                                }
                            }

                            if (mMedia == null) {
                                setError("Unable To Determine Media - Directshare");
                                return;
                            }

                            try {
                                downloadMedia(mMedia, "Other");
                            } catch (Throwable t) {
                                setError("Download Media Failed - " +t.toString());
                            }

                            param.setResult(null);
                        }
                    }
                });
            } else {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), void.class, DialogInterface.class, int.class);

                findAndHookMethod(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String localCharSequence = mMenuOptions[(Integer) param.args[1]].toString();

                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }

                        String downloadCheck;

                        try {
                            downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                        } catch (Throwable t) {
                            downloadCheck = "Download";
                        }

                        if (downloadCheck.equals(localCharSequence)) {
                            Object mMedia = null;

                            try {
                                model = getObjectField(param.thisObject, "b");
                            } catch (Throwable t) {
                                setError("Directshare Model Hook Invalid - " +t.toString());
                                return;
                            }

                            try {
                                mUserName = getFieldByType(model, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));

                                Field[] mCurrentMediaOptionButtonFields = model.getClass().getDeclaredFields();

                                for (Field iField : mCurrentMediaOptionButtonFields) {
                                    if (iField.getType().getName().equals(MEDIA_CLASS_NAME)) {
                                        iField.setAccessible(true);
                                        mMedia = iField.get(model);
                                        if (mMedia != null) {
                                            break;
                                        }
                                    }
                                }

                                if (mMedia == null) {
                                    setError("Unable To Determine Media - DS");
                                    return;
                                }

                                try {
                                    downloadMedia(mMedia, "Direct");
                                } catch (Throwable t) {
                                    setError("Direct Download Failed - " +t);
                                    sendError();
                                }
                            } catch (Throwable t) {
                                setError("Directshare Image/Video Minimized - " +t);
                            }

                            param.setResult(null);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Directshare Hooks Invalid - " +t.toString());
        }

        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), CharSequence[].class, android.content.DialogInterface.OnClickListener.class);

            findAndHookMethod(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence[] string = (CharSequence[]) param.args[0];
                    android.content.DialogInterface.OnClickListener onClickListener = (android.content.DialogInterface.OnClickListener) param.args[1];

                    if (onClickListener.getClass().getName().equals(DS_PERM_MORE_OPTIONS_DIALOG_CLASS)) {
                        param.args[0] = injectDownload(string, "Other");
                    }
                }
            });
        } catch (Throwable t) {
            setError("DirectShare Check Failed - " +t.toString());
        }
    }

    void hookFeed() {
        try {
            Class<?> Media = findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Media, CharSequence[].class);

            findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                    mCurrentMediaOptionButton = param.thisObject;

                    CharSequence[] result;
                    try {
                        result = (CharSequence[]) param.getResult();
                    } catch (Throwable t) {
                        setError("Profile Icon Failed - " +t.toString());
                        setError("Profile Icon Class - " +PROFILE_HOOK_CLASS);
                        sendError();
                        return;
                    }

                    param.setResult(injectDownload(result, "Feed"));
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> Media = findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Media, CharSequence[].class, Media);

                findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), methods[0].getName(), Media, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                        mCurrentMediaOptionButton = param.args[0];

                        CharSequence[] result;
                        try {
                            result = (CharSequence[]) param.getResult();
                        } catch (Throwable t) {
                            setError("Profile Icon Failed - " +t.toString());
                            setError("Profile Icon Class - " +PROFILE_HOOK_CLASS);
                            sendError();
                            return;
                        }

                        param.setResult(injectDownload(result, "Feed"));
                    }
                });
            } catch (Throwable t2) {
                setError("Media Options Button Hook Failed - " +t.toString());
            }
        }

        try {
            final Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, loadPackageParam.classLoader);
            final Method[] methods = XposedHelpers.findMethodsByExactParameters(MenuClickListener, void.class, DialogInterface.class, int.class);

            findAndHookMethod(MenuClickListener, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    String lockFeed;

                    try {
                        lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                    } catch (Throwable t) {
                        lockFeed = "Lock Feed";
                    }


                    String unlockFeed;

                    try {
                        unlockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button3);
                    } catch (Throwable t) {
                        unlockFeed = "Unlock Feed";
                    }

                    if (lockFeed.equals(localCharSequence)) {
                        Freeze = 1;
                        param.setResult(null);
                    }

                    if (unlockFeed.equals(localCharSequence)) {
                        Freeze = 0;
                        param.setResult(null);
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        Object mMedia = null;
                        oContext = ((Dialog) param.args[0]).getContext();

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                        } catch (Throwable t) {
                            setError("Menu Click Hook Failed - " + t);
                        }

                        if (mMedia == null) {
                            setError("Unable To Determine Media - Feed");
                            return;
                        }

                        try {
                            downloadMedia(mMedia, "Other");
                        } catch (Throwable t) {
                            setError(t.toString());
                            sendError();
                        }

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Menu Click Listener Failed - " + t.toString());
        }
    }

    void hookFollow() {
        try {
            XposedHelpers.findAndHookMethod(FOLLOW_HOOK_CLASS, loadPackageParam.classLoader, FOLLOW_HOOK, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        byte[] bytes = (byte[]) getFieldByType(param.thisObject, byte[].class);

                        JSONObject jObject = new JSONObject(new String(bytes, "UTF-8"));

                        if (jObject.getString("followed_by").equals("true")) {
                            String color = Helper.getSetting("Color");
                            if (color.equals("Instagram")) {
                                color = "#2E978C";
                            }
                            followerCount.setTextColor(Color.parseColor(color));
                        }
                    } catch (Throwable t) {
                        try {
                            byte[] bytes = (byte[]) XposedHelpers.getObjectField(param.thisObject, FOLLOW_HOOK_2);

                            JSONObject jObject = new JSONObject(new String(bytes, "UTF-8"));

                            if (jObject.getString("followed_by").equals("true")) {
                                String color = Helper.getSetting("Color");
                                if (color.equals("Instagram")) {
                                    color = "#2E978C";
                                }
                                followerCount.setTextColor(Color.parseColor(color));
                            }
                        } catch (Throwable t2) {
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Follow Feature Failed - " + t.toString());
        }
    }

    void hookFollowList() {
        try {
            Class<?> followList = XposedHelpers.findClass(FOLLOW_LIST_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(followList, View.class, int.class, View.class, ViewGroup.class, Object.class, Object.class);

            XposedHelpers.findAndHookMethod(followList, methods[0].getName(), int.class, View.class, ViewGroup.class, Object.class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Object[] objects = param.args;

                    for (Object object : objects) {
                        try {
                            if (object.getClass().getName().equals(USER_CLASS_NAME)) {
                                String followerName = (String) getObjectField(object, USERNAME_HOOK);
                                if (!Helper.getSetting("Following").contains(followerName)) {
                                    Helper.writeToFollower(followerName);
                                }
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            });

        }  catch (Throwable t) {
            setError("Follow List User Failed - " +t);
        }

        try {
            Class<?> followList = XposedHelpers.findClass(FOLLOW_LIST_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(followList, int.class);

            XposedHelpers.findAndHookMethod(followList, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Helper.resetFollower();
                }
            });
        } catch (Throwable t) {
            setError("Follow List Failed - " +t);
        }
    }

    void hookInstagram() {
        try {
            hookComments();
        } catch (Throwable t) {
            setError("Comments Failed: " +t);
        }

        try {
            hookDate();
        } catch (Throwable t) {
            setError("Date Failed: " +t);
        }

        try {
            hookDirectShare();
        } catch (Throwable t) {
            setError("DirectShare Failed: " +t);
        }

        try {
            hookFeed();
        } catch (Throwable t) {
            setError("Feed Failed: " +t);
        }

        try {
            hookFollow();
        } catch (Throwable t) {
            setError("Follow Failed: " +t);
        }

        try {
            hookFollowList();
        } catch (Throwable t) {
            setError("Follow List Failed: " +t);
        }

        try {
            hookLike();
        } catch (Throwable t) {
            setError("Like Failed: " +t);
        }

        try {
            hookLockFeed();
        } catch (Throwable t) {
            setError("Lock Failed: " +t);
        }

        try {
            hookMiniFeed();
        } catch (Throwable t) {
            setError("Mini Feed Failed: " +t);
        }

        try {
            hookNotification();
        } catch (Throwable t) {
            setError("Notification Failed: " +t);
        }

        try {
            hookProfileIcon();
        } catch (Throwable t) {
            setError("Profile Icon Failed: " +t);
        }

        try {
            hookShare();
        } catch (Throwable t) {
            setError("Share Failed: " +t);
        }

        try {
            hookSlide();
        } catch (Throwable t) {
            setError("Slide Failed: " +t);
        }

        try {
            hookStories();
        } catch (Throwable t) {
            setError("Stories Failed: " +t);
        }

        try {
            hookStoriesTimer();
        } catch (Throwable t) {
            setError("Stories Timer Failed: " +t);
        }

        try {
            hookSuggestion();
        } catch (Throwable t) {
            setError("Suggestion Failed: " +t);
        }

        try {
            hookVideos();
        } catch (Throwable t) {
            setError("Video Failed: " +t);
        }
    }

    void hookLike() {
        try {
            Class<?> Like = findClass(LIKE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Like, boolean.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(Like, methods[0].getName(), MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Like") || Freeze == 1) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Like Hooks Failed - " + t.toString());
            setError("Like Hook Class - " +LIKE_HOOK_CLASS);
        }
    }

    void hookLockFeed() {
        try {
            XposedHelpers.findAndHookMethod(LOCK_HOOK_CLASS, loadPackageParam.classLoader, LOCK_HOOK, MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    if (Freeze == 1) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Failed - " +t);
        }

        try {
            Class<?> feedLocation = XposedHelpers.findClass(LOCK_HOOK2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedLocation, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedLocation, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Location Failed - " +t);
        }

        try {
            Class<?> feedProfileIcon = XposedHelpers.findClass(LOCK_HOOK3, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedProfileIcon, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedProfileIcon, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Profile Icon Failed - " +t);
        }

        try {
            Class<?> feedUsername = XposedHelpers.findClass(LOCK_HOOK4, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedUsername, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedUsername, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Username Failed - " +t);
        }

        try {
            Class<?> feedLikeButton = XposedHelpers.findClass(LOCK_HOOK5, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedLikeButton, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedLikeButton, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Like Button Failed - " +t);
        }

        try {
            Class<?> feedLikeButton = XposedHelpers.findClass(LOCK_HOOK6, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedLikeButton, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedLikeButton, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Comment Button Failed - " +t);
        }

        try {
            Class<?> feedShareButton = XposedHelpers.findClass(LOCK_HOOK7, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedShareButton, void.class, View.class);

            XposedHelpers.findAndHookMethod(feedShareButton, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed Share Button Failed - " +t);
        }

        try {
            Class<?> feedLikeView = XposedHelpers.findClass(LOCK_HOOK8, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedLikeView, boolean.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(feedLikeView, methods[0].getName(), MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed LikeView Failed - " +t);
        }

        try {
            Class<?> feedTextView = XposedHelpers.findClass(LOCK_HOOK9, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(feedTextView, boolean.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(feedTextView, methods[0].getName(), MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Freeze == 1) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Locked Feed TextView Failed - " +t);
        }
    }

    void hookMiniFeed() {
        try {
            Class<?> miniFeedOnClick = XposedHelpers.findClass(MINI_FEED_HOOK_CLASS, loadPackageParam.classLoader);
            final Method[] methods = XposedHelpers.findMethodsByExactParameters(miniFeedOnClick, void.class, DialogInterface.class, int.class);

            XposedHelpers.findAndHookMethod(miniFeedOnClick, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String localCharSequence = mMenuOptions[(int) param.args[1]].toString();

                    oContext = ((Dialog) param.args[0]).getContext();

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        downloadMedia(miniFeedModel, "Other");
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Mini Feed OnClick Failed - " +t);
        }

        try {
            Class<?> miniFeedInject = XposedHelpers.findClass(MINI_FEED_HOOK_CLASS2, loadPackageParam.classLoader);
            final Method[] methods = XposedHelpers.findMethodsByExactParameters(miniFeedInject, CharSequence[].class, miniFeedInject);

            XposedHelpers.findAndHookMethod(miniFeedInject, methods[0].getName(), miniFeedInject, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    CharSequence[] result;

                    try {
                        result = (CharSequence[]) param.getResult();
                    } catch (Throwable t) {
                        setError("Mini Feed OnClick Failed -" +t);
                        sendError();
                        return;
                    }

                    miniFeedModel = getFieldByType(param.args[0], XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader));

                    param.setResult(injectDownload(result, "Other"));
                }
            });
        } catch (Throwable t) {
            setError("Mini Feed Inject Failed - " +t);
        }
    }

    void hookNotification() {
        try {
        Class<?> Notification = findClass(NOTIFICATION_CLASS, loadPackageParam.classLoader);
            if (Helper.getSettings("Push")) {
                Method[] methods;
                try {
                    methods = XposedHelpers.findMethodsByExactParameters(Notification, void.class, Intent.class, String.class);

                    if (methods[0].equals("Check")) {
                    }
                } catch (Throwable t) {
                    try {
                        methods = Notification.getDeclaredMethods();

                        for (Method method : methods) {
                            if (method.toString().contains("Intent") && method.toString().contains("String")) {
                                XposedHelpers.findAndHookMethod(Notification, method.getName(), method.getParameterTypes()[0], method.getParameterTypes()[1], method.getParameterTypes()[2], new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                        super.afterHookedMethod(param);
                                        if (Helper.getSettings("Push")) {
                                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                                            Intent intent = (Intent) param.args[0];
                                            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

                                            String userHolder;

                                            if (jsonObject.getString("m").contains("]: ")) {
                                                userHolder = jsonObject.getString("m").split("]: ")[1];
                                            } else {
                                                userHolder = jsonObject.getString("m");
                                            }

                                            userName = userHolder.split(" ")[0];
                                            String photoName = Helper.getString(mContext, "photo", loadPackageParam.packageName).toLowerCase();
                                            String photoCheck = userHolder.replace(userName, "").toLowerCase();
                                            String videoName = Helper.getString(mContext, "video", loadPackageParam.packageName).toLowerCase();
                                            if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(photoName)) {
                                                String fileExtension = ".jpg";
                                                String fileDescription;
                                                try {
                                                    fileDescription = Helper.getResourceString(mContext, R.string.photo);
                                                } catch (Throwable t) {
                                                    fileDescription = "Photo";
                                                }
                                                fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;

                                                fileType = "Image";
                                                linkToDownload = jsonObject.getString("i");
                                                try {
                                                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                                                } catch (Throwable t) {
                                                    notificationTitle = userName + "'s " + fileDescription;
                                                }
                                                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                                                SAVE = Helper.getSaveLocation(fileType);

                                                downloadOrPass();
                                            } else if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(videoName)) {
                                                linkToDownload = "https://www.instagram.com/" + userName + "/media/";
                                                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                        return;
                    } catch (Throwable t2) {
                        setError("New Push Notification Method Failed - " + t2);
                        return;
                    }
                }

                XposedHelpers.findAndHookMethod(Notification, methods[0].getName(), Intent.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (Helper.getSettings("Push")) {
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                            Intent intent = (Intent) param.args[0];
                            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

                            String userHolder;

                            if (jsonObject.getString("m").contains("]: ")) {
                                userHolder = jsonObject.getString("m").split("]: ")[1];
                            } else {
                                userHolder = jsonObject.getString("m");
                            }

                            userName = userHolder.split(" ")[0];
                            String photoName = Helper.getString(mContext, "photo", loadPackageParam.packageName).toLowerCase();
                            String photoCheck = userHolder.replace(userName, "").toLowerCase();
                            String videoName = Helper.getString(mContext, "video", loadPackageParam.packageName).toLowerCase();
                            if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(photoName)) {
                                String fileExtension = ".jpg";
                                String fileDescription;
                                try {
                                    fileDescription = Helper.getResourceString(mContext, R.string.photo);
                                } catch (Throwable t) {
                                    fileDescription = "Photo";
                                }
                                fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;

                                fileType = "Image";
                                linkToDownload = jsonObject.getString("i");
                                try {
                                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                                } catch (Throwable t) {
                                    notificationTitle = userName + "'s " + fileDescription;
                                }
                                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                                SAVE = Helper.getSaveLocation(fileType);

                                downloadOrPass();
                            } else if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(videoName)) {
                                linkToDownload = "https://www.instagram.com/" + userName + "/media/";
                                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
                            }
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Notification Error: " + t.toString());
        }

    }

    void hookProfileIcon() {
        try {
            Class<?> Profile = findClass(PROFILE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile, CharSequence[].class);

            findAndHookMethod(Profile, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    CharSequence[] result;
                    try {
                        result = (CharSequence[]) param.getResult();
                    } catch (Throwable t) {
                        setError("Profile Icon Failed - " +t.toString());
                        setError("Profile Icon Class - " +PROFILE_HOOK_CLASS);
                        sendError();
                        return;
                    }

                    param.setResult(injectDownload(result, "Other"));
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Failed - " +t.toString());
            setError("Profile Icon Class - " +PROFILE_HOOK_CLASS);
        }


        try {
            Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile2, void.class, DialogInterface.class, int.class);

            findAndHookMethod(Profile2, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mMenuOptions[(int) param.args[1]].toString();

                    if (Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {

                        Class<?> Profile;
                        Class<?> Profile2;
                        Class<?> ProfileUser;

                        try {
                            Profile = findClass(PROFILE_HOOK_CLASS2, loadPackageParam.classLoader);
                            Profile2 = findClass(PROFILE_HOOK_CLASS, loadPackageParam.classLoader);
                            ProfileUser = findClass(USER_CLASS_NAME, loadPackageParam.classLoader);
                        } catch (Throwable t) {
                            setError("Profile OnClick Class Failed -" +t);
                            sendError();
                            return;
                        }

                        String firstField;

                        try {
                            firstField = XposedHelpers.findFirstFieldByExactType(Profile, Profile2).getName();
                        } catch (Throwable e) {
                            setError("Profile First Field Failed - " + e);
                            sendError();
                            return;
                        }

                        String secondField;

                        Field[] fields = Profile2.getDeclaredFields();

                        try {
                            secondField = XposedHelpers.findFirstFieldByExactType(Profile2, ProfileUser).getName();
                        } catch (Throwable e) {
                            setError("Profile Second Field Failed - " + e);
                            sendError();
                            return;
                        }

                        count = 0;

                        for (Field field : fields) {
                            if (field.getType().equals(ProfileUser)) {
                                count++;
                                if (count == 2) {
                                    secondField = field.getName();
                                }
                            }
                        }

                        Object object;

                        try {
                            Object objectStart = XposedHelpers.getObjectField(param.thisObject, firstField);
                            object = XposedHelpers.getObjectField(objectStart, secondField);

                            linkToDownload = (String) XposedHelpers.getObjectField(object, PROFILE_HOOK_3);
                            linkToDownload = linkToDownload.replace("s150x150/", "");

                        } catch (Throwable t) {
                            setError("Profile Link To Download Failed -" +t);
                            sendError();
                            return;
                        }

                        try {
                            userName = (String) XposedHelpers.getObjectField(object, USERNAME_HOOK);
                        } catch (Throwable t) {
                            setError("Profile Icon Username Hooks Failed:  " + t);
                            sendError();
                            return;
                        }

                        try {
                            notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Icon");
                        } catch (Throwable t) {
                            notificationTitle = userName + "'s Icon";
                        }

                        notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                        String dateFormat = String.valueOf(System.currentTimeMillis());

                        if (!Helper.getSetting("File").equals("Instagram")) {
                            dateFormat = getDateProfileIcon(System.currentTimeMillis());
                        }

                        fileName = userName + "_"  + dateFormat +"_Profile.jpg";

                        SAVE = "";

                        fileType = "Profile";

                        downloadOrPass();

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Click Listener Failed - " + t.toString());
            setError("Profile Icon Click Listener Class - " +PROFILE_HOOK_CLASS2);
        }
    }

    void hookShare() {
        try {
            Class<?> shareClass = XposedHelpers.findClass(SHARE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(shareClass, void.class, Object.class);

            XposedHelpers.findAndHookMethod(shareClass, methods[0].getName(), Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    String fieldName = "";

                    Field[] fields = param.args[0].getClass().getDeclaredFields();

                    for (Field field : fields) {
                        if (field.getType().equals(String.class)) {
                            fieldName = field.getName();
                        }
                    }

                    String linkToShare = (String) XposedHelpers.getObjectField(param.args[0], fieldName);

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                    i.putExtra(Intent.EXTRA_TEXT, linkToShare);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (i.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(i);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Share Class Failed - " +t);
        }
    }

    void hookSlide() {
        try {
            XposedHelpers.findAndHookMethod(SLIDE_HOOK_CLASS, loadPackageParam.classLoader, SLIDE_HOOK, MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Slide") || Freeze == 1) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Slide Hook Failed - " +t);
        }
    }

    void hookStories() {
        try {
            Class<?> storiesOnClick = XposedHelpers.findClass(STORY_HOOK_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(storiesOnClick, void.class, DialogInterface.class, int.class);

            XposedHelpers.findAndHookMethod(storiesOnClick, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mMenuOptions[(Integer) param.args[1]].toString();

                    oContext = ((Dialog) param.args[0]).getContext();

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        downloadMedia(storiesModel, "Other");
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Stories OnClick Failed - " +t);
        }

        try {
            Class<?> storiesInject = XposedHelpers.findClass(STORY_HOOK_CLASS2, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(storiesInject, Dialog.class, CharSequence[].class, DialogInterface.OnClickListener.class, DialogInterface.OnDismissListener.class);

            XposedHelpers.findAndHookMethod(storiesInject, methods[0].getName(), CharSequence[].class, DialogInterface.OnClickListener.class, DialogInterface.OnDismissListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    CharSequence[] result;
                    try {
                        result= (CharSequence[]) param.args[0];
                    } catch (Throwable t) {
                        setError("Stories Inject Failed - " +t.toString());
                        sendError();
                        return;
                    }

                    param.args[0] = injectDownload(result, "Other");

                    Class<?> downloadSupport = XposedHelpers.findClass(STORY_HOOK, loadPackageParam.classLoader);
                    Class<?> feedClass = XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader);

                    Object download = getFieldByType(param.thisObject, downloadSupport);

                    storiesModel = getFieldByType(download, feedClass);
                }
            });
        } catch (Throwable t) {
            setError("Stories Inject Failed - " +t);
        }
    }

    void hookStoriesTimer() {
        try {
            Class<?> mediaPlayer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS, loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(mediaPlayer, STORY_TIME_HOOK, MediaPlayer.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Story")) {
                        MediaPlayer mediaPlayer = (MediaPlayer) param.args[0];
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Stories Video Timer Failed - " +t);
        }


        try {
            Class<?> storyTimer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS2, loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(storyTimer, STORY_TIME_HOOK2, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Story")) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Stories Timer Failed - " +t);
        }
    }

    void hookSuggestion() {
        try {
            Class<?> Suggest = XposedHelpers.findClass(SUGGESTION_HOOK_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(Suggest, View.class, Context.class);

            XposedHelpers.findAndHookMethod(Suggest, methods[0].getName(), Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    if (Helper.getSettings("Suggestion")) {
                        View view = (View) param.getResult();
                        Class<?> listClass = findClass(view.getTag().getClass().getName(), loadPackageParam.classLoader);
                        List list = (List) XposedHelpers.getObjectField(view.getTag(), XposedHelpers.findFirstFieldByExactType(listClass, List.class).getName());
                        list.clear();
                    }
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> Suggest = XposedHelpers.findClass(SUGGESTION_HOOK_CLASS, loadPackageParam.classLoader);

                Method[] methods = Suggest.getDeclaredMethods();

                String SuggestMethod = "";
                Class SuggestClass = null;
                Class SuggestClass2 = null;

                for (Method method:methods) {
                    if (method.getParameterTypes()[0].equals(Context.class)) {
                        SuggestMethod = method.getName();
                        SuggestClass = method.getParameterTypes()[0];
                        SuggestClass2 = method.getParameterTypes()[1];
                    }
                }

                XposedHelpers.findAndHookMethod(Suggest, SuggestMethod, SuggestClass, SuggestClass2, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        if (Helper.getSettings("Suggestion")) {
                            View view = (View) param.getResult();
                            Class<?> listClass = findClass(view.getTag().getClass().getName(), loadPackageParam.classLoader);
                            List list = (List) XposedHelpers.getObjectField(view.getTag(), XposedHelpers.findFirstFieldByExactType(listClass, List.class).getName());
                            list.clear();
                        }
                    }
                });
            } catch (Throwable t2) {
                setError("Suggestion Hooks Failed - " + t2.toString());
                setError("Suggestion Hook Class - " + SUGGESTION_HOOK_CLASS);
            }
        }
    }

    void hookVideos() {
        try {
            Class<?> Video = XposedHelpers.findClass(VIDEO_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Video, void.class, Object.class);
            final Method[] methods2 = XposedHelpers.findMethodsByExactParameters(Video, void.class, int.class);

            XposedHelpers.findAndHookMethod(Video, methods[0].getName(), Object.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Sound")) {
                        try {
                            if (!VIDEO_HOOK.isEmpty()) {
                                callMethod(param.thisObject, VIDEO_HOOK, 0);
                            } else {
                                callMethod(param.thisObject, methods2[0].getName(), 0);
                            }
                        } catch (Throwable t) {
                            callMethod(param.thisObject, methods2[0].getName(), 0);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Auto-Play Issue - " +t);
        }
    }

    @Override
    public void init() {
    }

    void sendError() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.ihelp101.instagram", "com.ihelp101.instagram.ErrorActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    void setError(String status) {
        if (!Helper.getSettings("Log")) {
            XposedBridge.log("XInsta - " + status);
        }

        Helper.setError(status);
    }

    void startHooks() {
        final List<PackageInfo> packs = nContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.packageName.equals("com.instagram.android")) {
                version = Integer.toString(p.versionCode);
                version = version.substring(0, version.length() - 2);
            }
        }

        final String[] split = Helper.getSetting("Hooks").split(";");

        try {
            FEED_CLASS_NAME = split[1];
            firstHook = split[1];
            MEDIA_CLASS_NAME = split[2];
            USER_CLASS_NAME = split[4];
            MEDIA_OPTIONS_BUTTON_CLASS = split[5];
            DS_MEDIA_OPTIONS_BUTTON_CLASS = split[6];
            DS_PERM_MORE_OPTIONS_DIALOG_CLASS = split[7];
            PERM__HOOK = split[10];
            MEDIA_VIDEO_HOOK = split[14];
            MEDIA_PHOTO_HOOK = split[15];
            USERNAME_HOOK = split[16];
            FULLNAME__HOOK = split[17];
        } catch (ArrayIndexOutOfBoundsException e) {
            HookCheck = "Yes";
        }

        try {
            IMAGE_HOOK_CLASS = split[18];
        } catch (ArrayIndexOutOfBoundsException e) {
            oldCheck = "Yes";
        }

        try {
            ITEMID_HOOK = split[20];
            COMMENT_HOOK_CLASS = split[21];
            COMMENT_HOOK = split[22];
            COMMENT_HOOK_CLASS2 = split[23];
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        try {
            DS_DIALOG_CLASS = split[24];
            directShareCheck = "Yes";
        } catch (ArrayIndexOutOfBoundsException e) {
            directShareCheck = "Nope";
        }

        try {
            PROFILE_HOOK_CLASS = split[29];
            PROFILE_HOOK_CLASS2 = split[30];
            FOLLOW_HOOK_2 = split[31];
            PROFILE_HOOK_3 = split[33];
            PROFILE_HOOK_4 = split[34];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LIKE_HOOK_CLASS = split[35];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            ACCOUNT_HOOK_CLASS = split[37];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SUGGESTION_HOOK_CLASS = split[39];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            FOLLOW_HOOK_CLASS = split[41];
            FOLLOW_HOOK = split[42];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            TIME_HOOK_CLASS = split[43];
        } catch (ArrayIndexOutOfBoundsException e) {
            TIME_HOOK_CLASS = "Nope";
        }

        try {
            NOTIFICATION_CLASS = split[44];
        } catch (ArrayIndexOutOfBoundsException e) {
            NOTIFICATION_CLASS = "Nope";
        }

        try {
            VIDEO_HOOK_CLASS = split[45];
        } catch (ArrayIndexOutOfBoundsException e) {
            VIDEO_HOOK_CLASS = "Nope";
        }

        try {
            STORY_HOOK_CLASS = split[46];
            STORY_HOOK_CLASS2= split[47];
            STORY_HOOK= split[48];
            STORY_TIME_HOOK_CLASS = split[49];
            STORY_TIME_HOOK = split[50];
            STORY_TIME_HOOK_CLASS2 = split[51];
            STORY_TIME_HOOK2 = split[52];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            MINI_FEED_HOOK_CLASS = split[53];
            MINI_FEED_HOOK_CLASS2 = split[54];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            FOLLOW_LIST_CLASS = split[55];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            COMMENT_HOOK_CLASS3 = split[56];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SHARE_HOOK_CLASS = split[57];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            TAGGED_HOOK_CLASS = split[58];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            VIDEO_HOOK = split[59];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SLIDE_HOOK_CLASS = split[60];
            SLIDE_HOOK = split[61];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LOCK_HOOK_CLASS = split[62];
            LOCK_HOOK = split[63];
            LOCK_HOOK2 = split[64];
            LOCK_HOOK3 = split[65];
            LOCK_HOOK4 = split[66];
            LOCK_HOOK5 = split[67];
            LOCK_HOOK6 = split[68];
            LOCK_HOOK7 = split[69];
            LOCK_HOOK8 = split[70];
            LOCK_HOOK9 = split[71];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            try {
                updateHooks();
            } catch (Throwable t) {

            }
        }

        if (HookCheck.equals("Yes")) {
            setError("Please update your hooks via the module.");
        } else {
            try {
                hookInstagram();
            } catch (Throwable t) {
                setError("Hooks Check Failed - " + t.toString());

            }
        }
    }

    void taggedUserAlert(final String toastMessage) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);

            final CharSequence items[] = userNameTagged.split(";");

            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userNameTagged = "";

                    fileName = fileName.replace(userName, items[which].toString());
                    String userNameFixed = userName.substring(0, 1).toUpperCase() + userName.substring(1);
                    notificationTitle = notificationTitle.replace(userNameFixed, items[which].toString());
                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);
                    userName = items[which].toString();

                    if (userNameTagged.isEmpty()) {
                        Toast(toastMessage);
                    }

                    try {
                        if (fileType.equals("Video") && Helper.getSettings("OneTap") && appInstalledOrNot("com.phantom.onetapvideodownload")) {
                            setError("One Tap - " + Helper.getSettings("OneTap"));
                            Intent intent = new Intent("com.phantom.onetapvideodownload.action.saveurl");
                            intent.setClassName("com.phantom.onetapvideodownload", "com.phantom.onetapvideodownload.IpcService");
                            intent.putExtra("com.phantom.onetapvideodownload.extra.url", linkToDownload);
                            intent.putExtra("com.phantom.onetapvideodownload.extra.title", fileName);
                            intent.putExtra("com.phantom.onetapvideodownload.extra.package_name", loadPackageParam.packageName);
                            mContext.startService(intent);
                        } else {
                            if (fileType.equals("Profile")) {
                                SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
                            } else {
                                SAVE = Helper.checkSave(SAVE, userName, fileName);
                            }

                            downloadOrPass();
                        }
                    } catch (Exception e) {
                        setError("Failed To Send Download Broadcast - " + e);
                    }

                    dialog.dismiss();
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    userNameTagged = "";
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Tagged User Failed - " +t);
        }
    }

    void Toast(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    void updateHooks() {
        Thread getHooks= new Thread() {
                public void run() {
                    try {
                        String url;

                        if (Helper.getSetting("Source").equals("GitHub")) {
                            url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                        } else if (Helper.getSetting("Source").equals("Pastebin")) {
                            url = "http://pastebin.com/raw.php?i=sTXbUFcx";
                        } else if (Helper.getSetting("Source").equals("Alternate Source")) {
                            url = "http://www.snapprefs.com/xinsta/Hooks.txt";
                        } else {
                            url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                        }

                        URL u = new URL(url);
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = c.getInputStream();

                        Hooks = Helper.convertStreamToString(inputStream);
                    } catch (Exception e) {
                        if (!Helper.getSetting("Hooks").equals("Instagram")) {
                            setError("Falling Back On Older Hooks");
                            hookInstagram();
                        } else {
                            setError("Failed to fetch hooks.");
                        }
                        Hooks = "Nope";
                    }
                }
            };
            getHooks.start();
            try {
                getHooks.join();
            } catch (InterruptedException e) {
            }

            int max = 0;
            int count = 0;

            String[] html = Hooks.split("<p>");

            String matched = "No";

            for (String data : html) {
                max++;
            }

            for (String data : html) {
                count++;

                String finalCheck = "123";

                if (!data.isEmpty()) {
                    String[] PasteVersion = data.split(";");
                    finalCheck = PasteVersion[0];
                }

                if (version.equalsIgnoreCase(finalCheck) && !data.isEmpty()) {
                    HooksSave = data.replace("<p>", "");
                    HooksSave = HooksSave.replace("</p>", "");
                    matched = "Yes";
                } else if (count == max && matched == "No") {
                    String fallback = html[1];
                    HooksSave = fallback.replace("<p>", "");
                    HooksSave = HooksSave.replace("</p>", "");
                } else {
                    int typeCheck = Integer.parseInt(version) - Integer.parseInt(finalCheck);
                    if (typeCheck <= 2 && typeCheck >= -2) {
                        HooksSave = data.replace("<p>", "");
                        HooksSave = HooksSave.replace("</p>", "");
                        matched = "Yes";
                    }
                }
            }

            HookCheck = "No";
            oldCheck = "No";
            directShareCheck = "Yes";
            HooksArray = HooksSave.split(";");

            try {
                FEED_CLASS_NAME = HooksArray[1];
                firstHook = HooksArray[1];
                MEDIA_CLASS_NAME = HooksArray[2];
                USER_CLASS_NAME = HooksArray[4];
                MEDIA_OPTIONS_BUTTON_CLASS = HooksArray[5];
                DS_MEDIA_OPTIONS_BUTTON_CLASS = HooksArray[6];
                DS_PERM_MORE_OPTIONS_DIALOG_CLASS = HooksArray[7];
                PERM__HOOK = HooksArray[10];
                MEDIA_VIDEO_HOOK = HooksArray[14];
                MEDIA_PHOTO_HOOK = HooksArray[15];
                USERNAME_HOOK = HooksArray[16];
                FULLNAME__HOOK = HooksArray[17];
                setError("Hooks Fetched!");
            } catch (ArrayIndexOutOfBoundsException e) {
                HookCheck = "Yes";
            }

            try {
                IMAGE_HOOK_CLASS = HooksArray[18];
            } catch (ArrayIndexOutOfBoundsException e) {
                oldCheck = "Yes";
            }

            try {
                ITEMID_HOOK = HooksArray[20];
                COMMENT_HOOK_CLASS = HooksArray[21];
                COMMENT_HOOK = HooksArray[22];
                COMMENT_HOOK_CLASS2 = HooksArray[23];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_DIALOG_CLASS = HooksArray[24];
            } catch (ArrayIndexOutOfBoundsException e) {
                directShareCheck = "Nope";
            }

            try {
                PROFILE_HOOK_CLASS = HooksArray[29];
                PROFILE_HOOK_CLASS2 = HooksArray[30];
                FOLLOW_HOOK_2 = HooksArray[31];
                PROFILE_HOOK_3 = HooksArray[33];
                PROFILE_HOOK_4 = HooksArray[34];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LIKE_HOOK_CLASS = HooksArray[35];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                ACCOUNT_HOOK_CLASS = HooksArray[37];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SUGGESTION_HOOK_CLASS = HooksArray[39];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                FOLLOW_HOOK_CLASS = HooksArray[41];
                FOLLOW_HOOK = HooksArray[42];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                TIME_HOOK_CLASS = HooksArray[43];
            } catch (ArrayIndexOutOfBoundsException e) {
                TIME_HOOK_CLASS = "Nope";
            }

            try {
                NOTIFICATION_CLASS = HooksArray[44];
            } catch (ArrayIndexOutOfBoundsException e) {
                NOTIFICATION_CLASS = "Nope";
            }

            try {
                VIDEO_HOOK_CLASS = HooksArray[45];
            } catch (ArrayIndexOutOfBoundsException e) {
                VIDEO_HOOK_CLASS = "Nope";
            }

            try {
                STORY_HOOK_CLASS = HooksArray[46];
                STORY_HOOK_CLASS2= HooksArray[47];
                STORY_HOOK= HooksArray[48];
                STORY_TIME_HOOK_CLASS = HooksArray[49];
                STORY_TIME_HOOK = HooksArray[50];
                STORY_TIME_HOOK_CLASS2 = HooksArray[51];
                STORY_TIME_HOOK2 = HooksArray[52];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                MINI_FEED_HOOK_CLASS = HooksArray[53];
                MINI_FEED_HOOK_CLASS2 = HooksArray[54];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                FOLLOW_LIST_CLASS = HooksArray[55];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                COMMENT_HOOK_CLASS3 = HooksArray[56];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SHARE_HOOK_CLASS = HooksArray[57];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                TAGGED_HOOK_CLASS = HooksArray[58];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                VIDEO_HOOK = HooksArray[59];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SLIDE_HOOK_CLASS = HooksArray[60];
                SLIDE_HOOK = HooksArray[61];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LOCK_HOOK_CLASS = HooksArray[62];
                LOCK_HOOK = HooksArray[63];
                LOCK_HOOK2 = HooksArray[64];
                LOCK_HOOK3 = HooksArray[65];
                LOCK_HOOK4 = HooksArray[66];
                LOCK_HOOK5 = HooksArray[67];
                LOCK_HOOK6 = HooksArray[68];
                LOCK_HOOK7 = HooksArray[69];
                LOCK_HOOK8 = HooksArray[70];
                LOCK_HOOK9 = HooksArray[71];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            Helper.setSetting("Hooks", HooksSave);
    }

    CharSequence[] injectDownload(CharSequence[] charSequence, String injectLocation) {
        String downloadCheck;

        try {
            downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
        } catch (Throwable t) {
            downloadCheck = "Download";
        }

        ArrayList<String> array = new ArrayList<String>();

        if (!Helper.getSettings("Order")) {
            for (CharSequence sq : charSequence) {
                array.add(sq.toString());
            }
        }

        if (!array.contains(downloadCheck)) {
            array.add(downloadCheck);
        }

        if (injectLocation.equals("Feed")) {
            if (Freeze == 0 && Helper.getSettings("Lock")) {
                String lockFeed;

                try {
                    lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                } catch (Throwable t) {
                    lockFeed = "Lock Feed";
                }

                if (!array.contains(lockFeed)) {
                    array.add(lockFeed);
                }
            }

            if (Freeze == 1 && Helper.getSettings("Lock")) {
                String unlockFeed;

                try {
                    unlockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button3);
                } catch (Throwable t) {
                    unlockFeed = "Unlock Feed";
                }

                if (!array.contains(unlockFeed)) {
                    array.add(unlockFeed);
                }
            }
        }

        if (Helper.getSettings("Order")) {
            for (CharSequence sq : charSequence) {
                array.add(sq.toString());
            }
        }

        CharSequence[] newResult = new CharSequence[array.size()];
        array.toArray(newResult);
        mMenuOptions = newResult;

        return mMenuOptions;
    }

    XC_MethodHook injectDownloadIntoCharSequenceHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            try {
                CharSequence[] result = (CharSequence[]) param.getResult();

                String downloadCheck;

                try {
                    downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                } catch (Throwable t) {
                    downloadCheck = "Download";
                }

                ArrayList<String> array = new ArrayList<String>();

                if (!Helper.getSettings("Order")) {
                    for (CharSequence sq : result) {
                        array.add(sq.toString());
                    }
                }

                if (!array.contains(downloadCheck)) {
                    array.add(downloadCheck);
                }

                if (Freeze == 0 && Helper.getSettings("Lock")) {
                    String lockFeed;

                    try {
                        lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                    } catch (Throwable t) {
                        lockFeed = "Lock Feed";
                    }

                    if (!array.contains(lockFeed)) {
                        array.add(lockFeed);
                    }
                }

                if (Freeze == 1 && Helper.getSettings("Lock")) {
                    String unlockFeed;

                    try {
                        unlockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button3);
                    } catch (Throwable t) {
                        unlockFeed = "Unlock Feed";
                    }

                    if (!array.contains(unlockFeed)) {
                        array.add(unlockFeed);
                    }
                }

                if (Helper.getSettings("Order")) {
                    for (CharSequence sq : result) {
                        array.add(sq.toString());
                    }
                }

                CharSequence[] newResult = new CharSequence[array.size()];
                array.toArray(newResult);
                mMenuOptions = newResult;
                param.setResult(newResult);
            } catch (Throwable t) {
                setError("Download Button Inject Failed - " +t.toString());
            }
        }
    };
}


