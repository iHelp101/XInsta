package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    private CharSequence[] mMenuOptions = null;
    private CharSequence[] mDirectShareMenuOptions = null;
    private CharSequence[] mProfileOptions = null;
    private Class<?> imageHook;
    private Class<?> User;
    private Class<?> dialogClass;
    public static Context mContext;
    public static Context nContext;
	private Object mCurrentMediaOptionButton;
	private Object mCurrentDirectShareMediaOptionButton;
    private Object mUserName;

    private String directShareCheck = "Nope";
    private String firstHook;
    private String getDirectory;
    private String Hooks = null;
    private String[] HooksArray;
    private String HookCheck = "No";
    private String HooksSave;
    private String imageLocation;
    private String linkToDownload;
    private String notificationTitle;
    private String profileLocation;
    private String oldCheck = "No";
    private String version = "123";
    private String videoLocation;

    private String COMMENT_HOOK_CLASS = "Nope";
    private String COMMENT_HOOK_CLASS2 = "Nope";
    private String DS_DIALOG_CLASS_NAME;
    private String DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = "Nope";
    private String DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = "Nope";
    private String FEED_CLASS_NAME = "Nope";
    private String FULLNAME__HOOK = "Nope";
    private String IMAGE_HOOK = "Nope";
    private String IMAGE_HOOK_CLASS = "Nope";
    private String ITEMID_HOOK = "Nope";
    private String ITEMID_DS_HOOK = "Nope";
    private String ITEMID_DS_HOOK2 = "Nope";
    private String MEDIA_CLASS_NAME = "Nope";
    private String MEDIA_OPTIONS_BUTTON_CLASS_NAME = "Nope";
    private String MEDIA_OPTIONS_BUTTON_HOOK = "Nope";
    private String MEDIA_OPTIONS_BUTTON_HOOK2 = "Nope";
    private String mMEDIA_HOOK = "Nope";
    private String mMEDIA_PHOTO_HOOK = "Nope";
    private String mMEDIA_VIDEO_HOOK = "Nope";
    private String MODEL_HOOK = "Nope";
    private String PERM__HOOK = "Nope";
    private String PERM__HOOK2 = "Nope";
    private String PROFILE_HOOK;
    private String PROFILE_HOOK_3;
    private String PROFILE_HOOK_4;
    private String PROFILE_HOOK_CLASS;
    private String PROFILE_HOOK_CLASS2;
    private String SAVE = "Instagram";
    private String USER_CLASS_NAME = "Nope";
    private String USERNAME_HOOK = "Nope";

    private String Failed = "No";
    private int versionCheck;
    private int count;
    private int id = 1;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private LoadPackageParam loadPackageParam;

    Class<?> MediaOptionsButton;
    Class<?> DirectSharePermalinkMoreOptionsDialog;
    Class < ?> DirectShareMenuClickListener;

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                Failed = "No";

                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (!getNotification().equals("Hide")) {
                    mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(mContext);
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(BitmapFactory.decodeResource(ResourceHelper.getOwnResources(mContext), R.drawable.ic_launcher))
                            .setContentText(ResourceHelper.getString(mContext, R.string.DownloadDots));
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(SAVE);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Throwable t) {
                log("Error: " + t);
                setError("Error: " + t);
                Failed = "Yes";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!getNotification().equals("Hide")) {
                if (Failed.equals("Yes")) {
                    mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Failed));
                    mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Failed));
                } else {
                    mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Completed));
                    mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Completed));
                }

                mBuilder.setContentTitle(notificationTitle);
                mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(ResourceHelper.getOwnResources(mContext), R.drawable.ic_launcher));
                mBuilder.setAutoCancel(true);

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);


                File file = new File(SAVE);
                if (SAVE.contains("jpg")) {
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

            MediaScannerConnection.scanFile(nContext,
                    new String[]{SAVE}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                int scan = 1;
                            }
                        }
                    });

            if (Failed.equals("Yes")) {
                Toast(ResourceHelper.getString(mContext, R.string.Download_Failed));
            } else {
                Toast(ResourceHelper.getString(mContext, R.string.Download_Completed));
            }
        }
    }

    private static void log(String log) {
		XposedBridge.log("XInsta: " + log);
	}

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.instagram.android"))
            return;

            loadPackageParam = lpparam;

            getDirectory = Environment.getExternalStorageDirectory().toString();

            // Thank you to KeepChat For the Following Code Snippet
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            nContext = (Context) callMethod(activityThread, "getSystemContext");

            versionCheck = nContext.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
            //End Snippet

            log("Instagram Version Code: " + versionCheck);

            startErrorLog("XInsta Initialized");
            setError("Instagram Version Code: " + versionCheck);
            setError("Device Codename: " + android.os.Build.HARDWARE);
            setError("Android Version: "+Build.VERSION.RELEASE);

            startHooks();
	}

	@SuppressLint("NewApi")
	private void downloadMedia(Object mMedia, String where) throws IllegalAccessException, IllegalArgumentException {
        String filenameExtension;
        String descriptionType;
        int descriptionTypeId = R.string.photo;

        try {
            linkToDownload = (String) getObjectField(mMedia, mMEDIA_VIDEO_HOOK);
            filenameExtension = "mp4";
            descriptionType = "video";
            descriptionTypeId = R.string.video;

            if (linkToDownload.equals("None"))
                filenameExtension = "";
        } catch (Throwable throwable) {
            setError("Switch Link - Different Media Type");
            if (oldCheck.equals("No")) {
                try {
                    Object photo = getFieldByType(mMedia, imageHook);
                    linkToDownload = (String) getObjectField(photo, IMAGE_HOOK);
                } catch (Throwable t) {
                    Toast("Please contact iHelp101 on XDA.");
                    setError("Photo Hook Invalid - " + imageHook);
                    setError("Link To Download Hook Invalid (Photo) - " + IMAGE_HOOK);
                    return;
                }
            } else {
                try {
                    linkToDownload = (String) getObjectField(mMedia, mMEDIA_PHOTO_HOOK);
                } catch (Throwable t) {
                    Toast("Please contact iHelp101 on XDA.");
                    setError("Link To Download Hook Invalid (Photo) - " + mMEDIA_PHOTO_HOOK);
                    return;
                }
            }
            filenameExtension = "jpg";
            descriptionType = "photo";
            descriptionTypeId = R.string.photo;
        }

        getSave();

        if (descriptionType.equals("photo")) {
            SAVE = imageLocation;
        } else {
            SAVE = videoLocation;
        }

		// Construct filename
		// username_imageId.jpg
		descriptionType = ResourceHelper.getString(mContext, descriptionTypeId);
		String toastMessage = ResourceHelper.getString(mContext, R.string.Downloading, descriptionType);
		Toast(toastMessage);

		Object mUser;
        try {
            mUser = getFieldByType(mMedia, User);
        } catch (Throwable t) {
            Toast("Please contact iHelp101 on XDA.");
            setError("mUser Hook Invalid - " + User);
            return;
        }

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            mUser = mUserName;
        }

		String userName, userFullName;
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
                Object model = getObjectField(mCurrentDirectShareMediaOptionButton, MODEL_HOOK);
                itemId = getObjectField(model, ITEMID_DS_HOOK) + "_" + getObjectField(model, ITEMID_DS_HOOK2);
            } catch (Throwable t) {
                setError("Model Hook Invalid - " + MODEL_HOOK);
                setError("ItemID Directshare Hook Invalid - " + ITEMID_DS_HOOK + " - " + ITEMID_DS_HOOK2);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        } else {
            try {
                itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
            } catch (Throwable t) {
                setError("ItemID Hook Invalid - " + ITEMID_HOOK);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        }

        String fileName = userName + "_" + itemId + "." + filenameExtension;

		if (TextUtils.isEmpty(userFullName)) {
			userFullName = userName;
		}

        notificationTitle = ResourceHelper.getString(mContext, R.string.username_thing, userFullName, descriptionType);;
        notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);;

        if (SAVE.equals("Instagram")) {
            SAVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
            if (getFolder().equals("Yes")) {
                SAVE = SAVE + "/" + userName;
            }
            File directory = new File(URI.create(SAVE).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } else {
            if (getFolder().equals("Yes")) {
                SAVE = SAVE + "/" + userName;
            }
            File directory = new File(URI.create(SAVE).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        SAVE = SAVE + "/" + fileName;
        SAVE = SAVE.replace("%20", " ");
        SAVE = SAVE.replace("file://", "");

        linkToDownload = linkToDownload.replace("750x750", "");
        linkToDownload = linkToDownload.replace("640x640", "");
        linkToDownload = linkToDownload.replace("480x480", "");
        linkToDownload = linkToDownload.replace("320x320", "");

        checkPermission();
	}

    private void Toast (String message) {
        Toast toast = Toast.makeText(nContext, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    private void startHooks() {
        final List<PackageInfo> packs = nContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.packageName.equals("com.instagram.android")) {
                version = Integer.toString(p.versionCode);
                version = version.substring(0, version.length() - 2);
            }
        }

        //Hook Fetch
        File file = new File(getDirectory + "/.Instagram/Hooks.txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            HookCheck = "Yes";
        }


        final String[] split = text.toString().split(";");

        try {
            FEED_CLASS_NAME = split[1];
            firstHook = split[1];
            MEDIA_CLASS_NAME = split[2];
            USER_CLASS_NAME = split[4];
            MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[5];
            DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[6];
            DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = split[7];
            MEDIA_OPTIONS_BUTTON_HOOK = split[8];
            MEDIA_OPTIONS_BUTTON_HOOK2 = split[9];
            PERM__HOOK = split[10];
            PERM__HOOK2 = split[11];
            mMEDIA_HOOK = split[12];
            mMEDIA_VIDEO_HOOK = split[14];
            mMEDIA_PHOTO_HOOK = split[15];
            USERNAME_HOOK = split[16];
            FULLNAME__HOOK = split[17];
        } catch (ArrayIndexOutOfBoundsException e) {
            HookCheck = "Yes";
        }

        try {
            IMAGE_HOOK_CLASS = split[18];
            IMAGE_HOOK = split[19];
        } catch (ArrayIndexOutOfBoundsException e) {
            oldCheck = "Yes";
        }

        try {
            ITEMID_HOOK = split[20];
        } catch (ArrayIndexOutOfBoundsException e) {
            ITEMID_HOOK = "Nope";
        }

        try {
            COMMENT_HOOK_CLASS = split[21];
            COMMENT_HOOK_CLASS2 = split[23];
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        try {
            DS_DIALOG_CLASS_NAME = split[24];
            MODEL_HOOK = split[26];
            ITEMID_DS_HOOK = split[27];
            ITEMID_DS_HOOK2 = split[28];
            directShareCheck = "Yes";
        } catch (ArrayIndexOutOfBoundsException e) {
            directShareCheck = "Nope";
        }

        try {
            PROFILE_HOOK_CLASS = split[29];
            PROFILE_HOOK_CLASS2 = split[30];
            PROFILE_HOOK = split[31];
            PROFILE_HOOK_3 = split[33];
            PROFILE_HOOK_4 = split[34];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        log("Instagram First Hook: " + firstHook);
        setError("Instagram First Hook: " + firstHook);

        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            UpdateHooks();
        }
        if (HookCheck.equals("Yes")) {
            setError("Please update your hooks via the module.");
        } else {
            try {
                hookInstagram(loadPackageParam);
            } catch (Throwable t) {
                setError("Hooks Check Failed - " +t.toString());
            }
        }
    }

    private void hookInstagram(final LoadPackageParam lpparam) {
        try {
            MediaOptionsButton = findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, lpparam.classLoader);
            DirectSharePermalinkMoreOptionsDialog = findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME, lpparam.classLoader);
            DirectShareMenuClickListener = findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, lpparam.classLoader);
        } catch (Throwable t) {
            setError("Start Classes Failed");
        }

        Class<?> Comments = null;
        Class<?> Comments_Support = null;

        try {
            Comments = findClass(COMMENT_HOOK_CLASS, lpparam.classLoader);
            Comments_Support = findClass(COMMENT_HOOK_CLASS2, lpparam.classLoader);
        } catch (Throwable t) {
            setError("Comment Check Class Failed - " +t.toString());
        }


        try {
            dialogClass = findClass(DS_DIALOG_CLASS_NAME, lpparam.classLoader);
        } catch (Throwable t) {
            setError("DirectShare Check Class Failed - " +t.toString());
        }


        User = findClass(USER_CLASS_NAME, lpparam.classLoader);
        if (oldCheck.equals("No")) {
            imageHook = findClass(IMAGE_HOOK_CLASS, lpparam.classLoader);
        }

        XC_MethodHook injectDownloadIntoCharSequenceHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                CharSequence[] result = (CharSequence[]) param.getResult();

                ArrayList<String> array = new ArrayList<String>();
                for (CharSequence sq : result)
                    array.add(sq.toString());

                if (mContext == null) {
                    try {
                        Field f = XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), Context.class);
                        f.setAccessible(true);
                        mContext = (Context) f.get(param.thisObject);
                    } catch (Throwable t) {
                        setError("Unable to get Context, button not translated");
                    }
                }

                if (!array.contains(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button)))
                    array.add(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button));
                CharSequence[] newResult = new CharSequence[array.size()];
                array.toArray(newResult);
                Field menuOptionsField;
                if (param.thisObject.getClass().getName().contains("directshare")) {
                    menuOptionsField = XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), CharSequence[].class);
                } else {
                    menuOptionsField = XposedHelpers.findFirstFieldByExactType(MediaOptionsButton, CharSequence[].class);
                }
                menuOptionsField.set(param.thisObject, newResult);
                if (param.thisObject.getClass().getName().contains("directshare")) {
                    mDirectShareMenuOptions = (CharSequence[]) menuOptionsField.get(param.thisObject);
                } else {
                    mMenuOptions = (CharSequence[]) menuOptionsField.get(param.thisObject);
                }
                param.setResult(newResult);
                } catch (Throwable t) {
                    setError("Download Button Inject Failed - " +t.toString());
                    setError("Download Button Class - " +param.thisObject.getClass().getName());
                }
            }
        };

        try {
        findAndHookMethod(MediaOptionsButton, MEDIA_OPTIONS_BUTTON_HOOK, injectDownloadIntoCharSequenceHook);
        findAndHookMethod(MediaOptionsButton, MEDIA_OPTIONS_BUTTON_HOOK2, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mCurrentMediaOptionButton = param.thisObject;
            }
        });
        } catch (Throwable t) {
            setError("Media Options Button Hook Failed - " +t.toString());
        }

        try {
            if (directShareCheck.equals("Nope")) {
                findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK, injectDownloadIntoCharSequenceHook);
                findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK2, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = param.thisObject;
                    }
                });

                Method[] methods = XposedHelpers.findMethodsByExactParameters(DirectShareMenuClickListener, void.class, DialogInterface.class, int.class);
                String directShareMenuMethod = methods[0].getName();

                findAndHookMethod(DirectShareMenuClickListener, directShareMenuMethod, DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CharSequence localCharSequence = mDirectShareMenuOptions[(Integer) param.args[1]];
                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }
                        if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                            Object mMedia = null;

                            Field[] mCurrentMediaOptionButtonFields =
                                    mCurrentDirectShareMediaOptionButton.getClass().getDeclaredFields();

                            for (Field iField : mCurrentMediaOptionButtonFields) {
                                if (iField.getType().getName().equals(MEDIA_CLASS_NAME)) {
                                    iField.setAccessible(true);
                                    mMedia = iField.get(mCurrentDirectShareMediaOptionButton);
                                    break;
                                }
                            }

                            if (mMedia == null) {
                                Toast.makeText(mContext, com.ihelp101.instagram.ResourceHelper.getString(mContext, R.string.direct_share_download_failed),
                                        Toast.LENGTH_SHORT).show();
                                setError("Unable to determine media");
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
                try {
                    findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK, DirectSharePermalinkMoreOptionsDialog, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            mCurrentDirectShareMediaOptionButton = getObjectField(param.args[0], PERM__HOOK2);
                        }
                    });
                } catch (Throwable t) {
                    setError("Direct Share Option Button Failed - " +t.toString());
                }

                Method[] methods = XposedHelpers.findMethodsByExactParameters(DirectShareMenuClickListener, void.class, DialogInterface.class, int.class);
                String directShareMenuMethod = methods[0].getName();

                findAndHookMethod(DirectShareMenuClickListener, directShareMenuMethod, DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Field menuOptionsField = XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), ArrayList.class);
                        ArrayList arrayList = (ArrayList) menuOptionsField.get(param.thisObject);
                        arrayList.add("Download");
                        menuOptionsField.set(param.thisObject, arrayList);
                        ArrayList mProfileList = (ArrayList) menuOptionsField.get(param.thisObject);

                        String localCharSequence = mProfileList.get((Integer) param.args[1]).toString();

                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }

                        if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                            Object mMedia = null;

                            Object model;

                            try {
                                model = getObjectField(mCurrentDirectShareMediaOptionButton, MODEL_HOOK);
                            } catch (Throwable t) {
                                setError("Directshare Model Hook Invalid - " +t.toString());
                                return;
                            }

                            try {
                                mUserName = getFieldByType(model, User);

                                Field[] mCurrentMediaOptionButtonFields =
                                        model.getClass().getDeclaredFields();

                                for (Field iField : mCurrentMediaOptionButtonFields) {
                                    if (iField.getType().getName().equals(MEDIA_CLASS_NAME)) {
                                        iField.setAccessible(true);
                                        mMedia = iField.get(model);
                                        break;
                                    }
                                }

                                if (mMedia == null) {
                                    Toast(ResourceHelper.getString(mContext, R.string.direct_share_download_failed));
                                    setError("Unable to determine media");
                                    return;
                                }

                                try {
                                    downloadMedia(mMedia, "Direct");
                                } catch (Throwable t) {
                                    Toast("Please contact iHelp101 on XDA.");
                                }
                            } catch (Throwable t) {
                                setError(t.toString());
                                Toast(ResourceHelper.getString(nContext, R.string.Picture_Not));
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
            Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, lpparam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(MenuClickListener, void.class, DialogInterface.class, int.class);
            String feedMethod = methods[0].getName();

            findAndHookMethod(MenuClickListener, feedMethod, DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];
                    if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                        Object mMedia = null;

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, mMEDIA_HOOK);
                        } catch (NoSuchFieldError e) {
                            setError("Menu Click Hook Invalid");
                            e.printStackTrace();
                        }

                        if (mMedia == null) {
                            Toast(ResourceHelper.getString(mContext, R.string.direct_share_download_failed));
                            setError("Unable to determine media");
                            return;
                        }

                        try {
                            downloadMedia(mMedia, "Other");
                        } catch (Throwable t) {
                            setError(t.toString());
                            Toast("Please contact iHelp101 on XDA.");
                        }

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Menu Click Listener Failed - " +t.toString());
        }

        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, boolean.class, Comments_Support);
            String commentMethod = methods[0].getName();

            findAndHookMethod(Comments, commentMethod, Comments_Support, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object pj = param.args[0];
                    if (mContext == null) {
                        mContext = AndroidAppHelper.currentApplication();
                    }

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", (String) getObjectField(pj, "d"));
                    clipboard.setPrimaryClip(clip);
                    Toast(ResourceHelper.getString(nContext, R.string.Copied));
                }
            });
        } catch (Throwable t) {
            setError("Comment Check Failed - " +t.toString());
        }

        try {
            Class<?> Profile = findClass(PROFILE_HOOK_CLASS, lpparam.classLoader);

            findAndHookMethod(Profile, PROFILE_HOOK, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                CharSequence[] result = (CharSequence[]) param.getResult();

                ArrayList<String> array = new ArrayList<String>();
                for (CharSequence sq : result)
                    array.add(sq.toString());

                if (!array.contains(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button)))
                    array.add(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button));

                CharSequence[] newResult = new CharSequence[array.size()];

                mProfileOptions = newResult;

                array.toArray(newResult);
                param.setResult(newResult);
            }
        });
        } catch (Throwable t) {
            setError("Profile Icon Failed - " +t.toString());
        }


        try {
            Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS2, lpparam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile2, void.class, DialogInterface.class, int.class);
            String profile2Method = methods[0].getName();

            findAndHookMethod(Profile2, profile2Method, DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mProfileOptions[(int) param.args[1]].toString();

                    if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {

                        Class<?> Profile = findClass(PROFILE_HOOK_CLASS2, lpparam.classLoader);
                        Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS, lpparam.classLoader);
                        Class<?> ProfileUser = findClass(USER_CLASS_NAME, lpparam.classLoader);

                        String firstField;

                        try {
                            firstField = XposedHelpers.findFirstFieldByExactType(Profile, Profile2).toString();
                            String[] firstFieldArray = firstField.split(PROFILE_HOOK_CLASS2 + ".");
                            firstField = firstFieldArray[1].trim();
                        } catch (Throwable e) {
                            setError("Profile First Field Failed: " + e);
                            return;
                        }

                        String secondField;

                        try {
                            secondField = XposedHelpers.findFirstFieldByExactType(Profile2, ProfileUser).toString();
                            String[] secondFieldArray = secondField.split(PROFILE_HOOK_CLASS + ".");
                            secondField = secondFieldArray[1].trim();
                        } catch (Throwable e) {
                            setError("Profile Second Field Failed: " + e);
                            return;
                        }

                        Object objectStart = XposedHelpers.getObjectField(param.thisObject, firstField);
                        Object object = XposedHelpers.getObjectField(objectStart, secondField);

                        linkToDownload = (String) XposedHelpers.getObjectField(object, PROFILE_HOOK_3);
                        linkToDownload = linkToDownload.replace("s150x150/", "");

                        String userFullName;

                        try {
                            userFullName = (String) XposedHelpers.getObjectField(object, PROFILE_HOOK_4);
                        } catch (Throwable t) {
                            setError("Profile Icon Username Hooks Failed:  " + t);
                            return;
                        }

                        notificationTitle = ResourceHelper.getString(mContext, R.string.username_thing, userFullName, "Icon");
                        notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);


                        String fileName = userFullName + "-Profile.jpg";

                        getSave();

                        SAVE = profileLocation;

                        if (profileLocation.equals("Instagram")) {
                            SAVE = imageLocation;
                        }

                        if (SAVE.equals("Instagram")) {
                            SAVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
                            if (getFolder().equals("Yes")) {
                                SAVE = SAVE + "/" + userFullName;
                            }
                            File directory = new File(URI.create(SAVE).getPath());
                            if (!directory.exists())
                                directory.mkdirs();
                        } else {
                            if (getFolder().equals("Yes")) {
                                SAVE = SAVE + "/" + userFullName;
                            }
                            File directory = new File(URI.create(SAVE).getPath());
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }
                        }

                        SAVE = SAVE + "/" + fileName;
                        SAVE = SAVE.replace("%20", " ");
                        SAVE = SAVE.replace("file://", "");

                        new RequestTask().execute();

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Click Listener Failed - " +t.toString());
        }

        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(dialogClass, dialogClass, CharSequence[].class, android.content.DialogInterface.OnClickListener.class);
            String dialogMethod = methods[0].getName();

            findAndHookMethod(dialogClass, dialogMethod, CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence[] string = (CharSequence[]) param.args[0];
                    android.content.DialogInterface.OnClickListener onClickListener = (android.content.DialogInterface.OnClickListener) param.args[1];
                    ArrayList<String> array = new ArrayList<String>();
                    for (CharSequence sq : string)
                        array.add(sq.toString());

                    if (onClickListener.getClass().getName().equals(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME) && !array.contains(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button))) {
                        array.add(ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button));
                        CharSequence[] newResult = new CharSequence[array.size()];
                        array.toArray(newResult);
                        param.args[0] = newResult;
                    }
                }
            });
        } catch (Throwable t) {
            setError("DirectShare Check Failed - " +t.toString());
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            setError("Instagram Storage Permission Missing");
            Toast("Instagram Storage Permission Missing");
        } else {
            new RequestTask().execute();
        }
    }

    private void UpdateHooks() {
        Thread getHooks= new Thread() {
                public void run() {
                    try {
                        URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = c.getInputStream();

                        Hooks = convertStreamToString(inputStream);
                    } catch (Exception e) {
                        setError("Failed to fetch hooks.");
                        Hooks = "Nope";
                    }
                }
            };
            getHooks.start();
            try {
                getHooks.join();
            } catch (InterruptedException e) {
                System.out.println("Ec: " + e);
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

                if (version.equalsIgnoreCase(finalCheck)) {
                    HooksSave = data.replace("<p>", "");
                    HooksSave = HooksSave.replace("</p>", "");
                    matched = "Yes";
                } else {
                    if (count == max && matched == "No") {
                        String fallback = html[1];
                        HooksSave = fallback.replace("<p>", "");
                        HooksSave = HooksSave.replace("</p>", "");
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
                MEDIA_OPTIONS_BUTTON_CLASS_NAME = HooksArray[5];
                DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = HooksArray[6];
                DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = HooksArray[7];
                MEDIA_OPTIONS_BUTTON_HOOK = HooksArray[8];
                MEDIA_OPTIONS_BUTTON_HOOK2 = HooksArray[9];
                PERM__HOOK = HooksArray[10];
                PERM__HOOK2 = HooksArray[11];
                mMEDIA_HOOK = HooksArray[12];
                mMEDIA_VIDEO_HOOK = HooksArray[14];
                mMEDIA_PHOTO_HOOK = HooksArray[15];
                USERNAME_HOOK = HooksArray[16];
                FULLNAME__HOOK = HooksArray[17];
                log("Hooks Fetched!");
            } catch (ArrayIndexOutOfBoundsException e) {
                HookCheck = "Yes";
            }

            try {
                IMAGE_HOOK_CLASS = HooksArray[18];
                IMAGE_HOOK = HooksArray[19];
            } catch (ArrayIndexOutOfBoundsException e) {
                oldCheck = "Yes";
            }

            try {
                ITEMID_HOOK = HooksArray[20];
            } catch (ArrayIndexOutOfBoundsException e) {
                ITEMID_HOOK = "Nope";
            }

            try {
                COMMENT_HOOK_CLASS = HooksArray[21];
                COMMENT_HOOK_CLASS2 = HooksArray[23];
            } catch (ArrayIndexOutOfBoundsException e) {

            }

            try {
                DS_DIALOG_CLASS_NAME = HooksArray[24];
                MODEL_HOOK = HooksArray[26];
                ITEMID_DS_HOOK = HooksArray[27];
                ITEMID_DS_HOOK2 = HooksArray[28];
            } catch (ArrayIndexOutOfBoundsException e) {
                directShareCheck = "Nope";
            }

            try {
                PROFILE_HOOK_CLASS = HooksArray[29];
                PROFILE_HOOK_CLASS2 = HooksArray[30];
                PROFILE_HOOK = HooksArray[31];
                PROFILE_HOOK_3 = HooksArray[33];
                PROFILE_HOOK_4 = HooksArray[34];
            } catch (ArrayIndexOutOfBoundsException e) {

            }

            try {
                File root = new File(getDirectory, ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "Hooks.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(HooksSave);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                setError("EEE: "+e);
            }
    }

	private static Object getFieldByType(Object object, Class<?> type) {
		Field f = XposedHelpers.findFirstFieldByExactType(object.getClass(), type);
		try {
			return f.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

    public void getSave() {
        //Image Fetch
        File imagelocation = new File(getDirectory + "/.Instagram/Image.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(imagelocation));
            String line;

            line = br.readLine();
            imageLocation = line;
            br.close();
        }
        catch (IOException e) {
            imageLocation = "Instagram";
        }

        //Profile Fetch
        File profilelocation = new File(getDirectory + "/.Instagram/Profile.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(profilelocation));
            String line;

            line = br.readLine();
            profileLocation = line;
            br.close();
        }
        catch (IOException e) {
            profileLocation = "Instagram";
        }

        //Video Fetch
        File videolocation = new File(getDirectory + "/.Instagram/Video.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(videolocation));
            String line;

            line = br.readLine();
            videoLocation = line;
            br.close();
        }
        catch (IOException e) {
            videoLocation = "Instagram";
        }
    }

    public String getFolder() {
        //Notification Option Fetch
        File notification = new File(getDirectory + "/.Instagram/Folder.txt");
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

    public void startErrorLog(String status) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());

            status = time + " - " + status;

            File root = new File(getDirectory, ".Instagram");
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

    public void setError(String status) {
        log(status);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());

            status = time + " - " + status;

            File root = new File(getDirectory, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, "Error.txt");
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.newLine();
            buf.append(status);
            buf.close();
        } catch (IOException e) {

        }
    }

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
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

}


