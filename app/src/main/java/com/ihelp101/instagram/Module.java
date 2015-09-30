package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    private ArrayList mMenuOptionss = null;
    private CharSequence[] mMenuOptions = null;
    private CharSequence[] mDirectShareMenuOptions = null;
    private Class<?> imageHook;
    private Class<?> MediaType;
    private Class<?> User;
    private Class<?> dialogClass;
    private Context mContext;
    private Context nContext;
    private int id = 1;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
	private Object mCurrentMediaOptionButton;
	private Object mCurrentDirectShareMediaOptionButton;
    private Object mUserName;

    private String directShareCheck = "Nope";
    private String notificationCheck = "Show";
    private String firstHook;
    private String getDirectory = Environment.getExternalStorageDirectory().toString().replace("1", "0");
    private String Hooks = null;
    private String[] HooksArray;
    private String HookCheck = "No";
    private String HooksSave;
    private String imageLocation;
    private String linkToDownload;
    private String oldCheck = "No";
    private String commentCheck = "No";
    private String version = "123";
    private String videoLocation;

    private String COMMENT_HOOK = "Nope";
    private String COMMENT_HOOK_CLASS = "Nope";
    private String COMMENT_HOOK_CLASS2 = "Nope";
    private String DS_DIALOG_CLASS_NAME;
    private String DS_DIALOG_HOOK = "Nope";
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
    private String MEDIA_TYPE_CLASS_NAME = "Nope";
    private String mMEDIA_VIDEO_HOOK = "Nope";
    private String MODEL_HOOK = "Nope";
    private String PERM__HOOK = "Nope";
    private String PERM__HOOK2 = "Nope";
    private String SAVE = "Instagram";
    private String USER_CLASS_NAME = "Nope";
    private String USERNAME_HOOK = "Nope";
    private String VIDEOTYPE_HOOK = "Nope";

    private static void log(String log) {
		XposedBridge.log("XInsta: " + log);
	}

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.instagram.android"))
            return;

        // Thank you to KeepChat For the Following Code Snippet
        // http://git.io/JJZPaw
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context context = (Context) callMethod(activityThread, "getSystemContext");

        final int versionCheck = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
        //End Snippet

        nContext = context;

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

        //Image Fetch
        File imagelocation = new File(getDirectory + "/.Instagram/Image.txt");

        StringBuilder image = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(imagelocation));
            String line;

            while ((line = br.readLine()) != null) {
                image.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            image.append("Instagram");
        }

        //Video Fetch
        File videolocation = new File(getDirectory + "/.Instagram/Video.txt");

        StringBuilder video = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(videolocation));
            String line;

            while ((line = br.readLine()) != null) {
                video.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            video.append("Instagram");
        }

        try {
            FEED_CLASS_NAME = split[1];
            firstHook = split[1];
            MEDIA_CLASS_NAME = split[2];
            MEDIA_TYPE_CLASS_NAME = split[3];
            USER_CLASS_NAME = split[4];
            MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[5];
            DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[6];
            DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = split[7];
            MEDIA_OPTIONS_BUTTON_HOOK = split[8];
            MEDIA_OPTIONS_BUTTON_HOOK2 = split[9];
            PERM__HOOK = split[10];
            PERM__HOOK2 = split[11];
            mMEDIA_HOOK = split[12];
            VIDEOTYPE_HOOK = split[13];
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
            COMMENT_HOOK = split[22];
            COMMENT_HOOK_CLASS2 = split[23];
        } catch (ArrayIndexOutOfBoundsException e) {
            commentCheck = "Yes";
        }

        try {
            DS_DIALOG_CLASS_NAME = split[24];
            DS_DIALOG_HOOK = split[25];
            MODEL_HOOK = split[26];
            ITEMID_DS_HOOK = split[27];
            ITEMID_DS_HOOK2 = split[28];
            directShareCheck = "Yes";
        } catch (ArrayIndexOutOfBoundsException e) {
            directShareCheck = "Nope";
        }

        final List<PackageInfo> packs = nContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.packageName.equals("com.instagram.android")) {
                version = Integer.toString(p.versionCode);
                version = version.substring(0, version.length() - 2);
            }
        }

        log("Instagram Version Code: " + versionCheck);
        log("Instagram First Hook: " + firstHook);
        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            UpdateHooks();
        }
        if (HookCheck.equals("Yes")) {
            log(ResourceHelper.getString(nContext, R.string.Please));
        } else {
            hookInstagram(lpparam);
        }
	}

	@SuppressLint("NewApi")
	private void downloadMedia(Object mMedia, String where) throws IllegalAccessException, IllegalArgumentException {

        Object mMediaType;
        try {
            mMediaType = getFieldByType(mMedia, MediaType);
        } catch (Throwable t) {
            Toast("Please contact iHelp101 on XDA.");
            log("Media Type Hook Invalid - " +MediaType);
            return;
        }

		Object videoType;
        try {
            videoType = getStaticObjectField(MediaType, VIDEOTYPE_HOOK);
        } catch (Throwable t) {
            Toast("Please contact iHelp101 on XDA.");
            log("Video Type Hook Invalid - " +VIDEOTYPE_HOOK);
            return;
        }

        getSave();

		String filenameExtension;
		String descriptionType;
		int descriptionTypeId = R.string.photo;

		if (mMediaType.equals(videoType)) {
            try {
                linkToDownload = (String) getObjectField(mMedia, mMEDIA_VIDEO_HOOK);
            } catch (Throwable t) {
                Toast("Please contact iHelp101 on XDA.");
                log("Link To Download Hook Invalid (Video) - " +mMEDIA_VIDEO_HOOK);
                return;
            }
			filenameExtension = "mp4";
			descriptionType = "video";
			descriptionTypeId = R.string.video;
		} else {
            if (oldCheck.equals("No")) {
                try {
                    Object photo = getFieldByType(mMedia, imageHook);
                    linkToDownload = (String) getObjectField(photo, IMAGE_HOOK);
                } catch (Throwable t) {
                    Toast("Please contact iHelp101 on XDA.");
                    log("Photo Hook Invalid - " +imageHook);
                    log("Link To Download Hook Invalid (Photo) - " + IMAGE_HOOK);
                    return;
                }
            } else {
                try {
                    linkToDownload = (String) getObjectField(mMedia, mMEDIA_PHOTO_HOOK);
                } catch (Throwable t) {
                    Toast("Please contact iHelp101 on XDA.");
                    log("Link To Download Hook Invalid (Photo) - " +mMEDIA_PHOTO_HOOK);
                    return;
                }
            }
			filenameExtension = "jpg";
			descriptionType = "photo";
		}

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
            log ("mUser Hook Invalid - " +User);
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
            log("Failed to get User from Media, using placeholders");
            userName = "username_placeholder";
            userFullName = "Unknown name";
		}

        String itemId;

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            try {
                Object model = getObjectField(mCurrentDirectShareMediaOptionButton, MODEL_HOOK);
                itemId = getObjectField(model, ITEMID_DS_HOOK) + "_" + getObjectField(model, ITEMID_DS_HOOK2);
            } catch (Throwable t) {
                log("Model Hook Invalid - " +MODEL_HOOK);
                log("ItemID Directshare Hook Invalid - " +ITEMID_DS_HOOK+ " - " +ITEMID_DS_HOOK2);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        } else {
            try {
                itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
            } catch (Throwable t) {
                log("ItemID Hook Invalid - " +ITEMID_HOOK);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        }

        String fileName = userName + "_" + itemId + "." + filenameExtension;

		if (TextUtils.isEmpty(userFullName)) {
			userFullName = userName;
		}

        if (SAVE.equals("Instagram")) {
            SAVE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
            File directory =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram");
            if (!directory.exists())
                directory.mkdirs();
        } else {
            File directory = new File(URI.create(SAVE).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        new DownloadFileAsync().execute(linkToDownload, SAVE, fileName, userFullName, descriptionType);
	}

    private class DownloadFileAsync extends AsyncTask<Object, String, String> {

        String User = null;
        String Desc = null;
        String Location = null;
        String fileName = null;
        String Failed = "No";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            notificationCheck = getNotification();

            Random r = new Random();
            int i1 = r.nextInt(80000000 - 65) + 65;
            id = i1;
        }

        @Override
        protected String doInBackground(Object... aurl) {
            int count;
            Location = aurl[1] + "/" + aurl[2];
            Location = Location.replace("%20", " ");
            Location = Location.replace("file://", "");
            fileName = (String) aurl [2];
            User = (String) aurl[3];
            Desc = (String) aurl[4];

            if (!notificationCheck.equals("Hide")) {
                mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setContentTitle("" + User + "'s " + Desc)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentText(ResourceHelper.getString(mContext, R.string.DownloadDots));
                mNotifyManager.notify(id, mBuilder.build());
            }

            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Location);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                log("Exception: " + e);
                Failed = "Yes";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            Toast(ResourceHelper.getString(mContext, R.string.Download_Completed));

            if (!notificationCheck.equals("Hide")) {
                if (Failed.equals("Yes")) {
                    mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Failed));
                    mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Failed));
                } else {
                    mBuilder.setContentText(ResourceHelper.getString(mContext, R.string.Download_Completed));
                    mBuilder.setTicker(ResourceHelper.getString(mContext, R.string.Download_Completed));
                }

                mBuilder.setContentTitle("" + User + "'s " + Desc);
                mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
                mBuilder.setAutoCancel(true);

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);

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

            MediaScannerConnection.scanFile(nContext,
                    new String[]{Location}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                int scan = 1;
                            }
                        }
                    });
        }
    }

    private void Toast (String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void hookInstagram(final LoadPackageParam lpparam) {
        final Class<?> MediaOptionsButton = findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, lpparam.classLoader);
        final Class<?> DirectSharePermalinkMoreOptionsDialog = findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME, lpparam.classLoader);
        final Class < ?> DirectShareMenuClickListener = findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, lpparam.classLoader);
        Class<?> Comments = null;
        Class<?> Comments_Support = null;

        if (commentCheck.equals("No")) {
            Comments = findClass(COMMENT_HOOK_CLASS, lpparam.classLoader);
            Comments_Support = findClass(COMMENT_HOOK_CLASS2, lpparam.classLoader);
        }

        if (directShareCheck.equals("Yes")) {
            dialogClass = findClass(DS_DIALOG_CLASS_NAME, lpparam.classLoader);
        }

        MediaType = findClass(MEDIA_TYPE_CLASS_NAME, lpparam.classLoader);
        User = findClass(USER_CLASS_NAME, lpparam.classLoader);
        if (oldCheck.equals("No")) {
            imageHook = findClass(IMAGE_HOOK_CLASS, lpparam.classLoader);
        }

        XC_MethodHook injectDownloadIntoCharSequenceHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                        log("Unable to get Context, button not translated");
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
            }
        };

        findAndHookMethod(MediaOptionsButton, MEDIA_OPTIONS_BUTTON_HOOK, injectDownloadIntoCharSequenceHook);
        findAndHookMethod(MediaOptionsButton, MEDIA_OPTIONS_BUTTON_HOOK2, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mCurrentMediaOptionButton = param.thisObject;
            }
        });

        try {

            if (directShareCheck.equals("Nope")) {
                findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK, injectDownloadIntoCharSequenceHook);
                findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK2, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = param.thisObject;
                    }
                });

                findAndHookMethod(DirectShareMenuClickListener, "onClick", DialogInterface.class, int.class, new XC_MethodHook() {
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
                                log("Unable to determine media");
                                return;
                            }

                            downloadMedia(mMedia, "Other");

                            param.setResult(null);
                        }
                    }
                });
            } else {
                findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK, DirectSharePermalinkMoreOptionsDialog, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = getObjectField(param.args[0], PERM__HOOK2);
                    }
                });

                findAndHookMethod(DirectShareMenuClickListener, "onClick", DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Field menuOptionsField = XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), ArrayList.class);
                        ArrayList arrayList = (ArrayList) menuOptionsField.get(param.thisObject);
                        arrayList.add("Download");
                        menuOptionsField.set(param.thisObject, arrayList);
                        mMenuOptionss = (ArrayList) menuOptionsField.get(param.thisObject);

                        String localCharSequence = mMenuOptionss.get((Integer) param.args[1]).toString();

                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }

                        if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                            Object mMedia = null;

                            Object model = getObjectField(mCurrentDirectShareMediaOptionButton, MODEL_HOOK);

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
                                    log("Unable to determine media");
                                    return;
                                }

                                try {
                                    downloadMedia(mMedia, "Direct");
                                } catch (Throwable t) {
                                    Toast("Please contact iHelp101 on XDA.");
                                }
                            } catch (Exception e) {
                                Toast(ResourceHelper.getString(nContext, R.string.Picture_Not));
                            }

                            param.setResult(null);
                        }
                    }
                });
            }

        } catch (Exception e) {
            log ("Directshare Hooks Invalid.");
        }

        Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, lpparam.classLoader);
        findAndHookMethod(MenuClickListener, "onClick", DialogInterface.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];
                if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                    Object mMedia = null;

                    try {
                        mMedia = getObjectField(mCurrentMediaOptionButton, mMEDIA_HOOK);
                    } catch (NoSuchFieldError e) {
                        log("Menu Click Hook Invalid");
                        e.printStackTrace();
                    }

                    if (mMedia == null) {
                        Toast(ResourceHelper.getString(mContext, R.string.direct_share_download_failed));
                        log("Unable to determine media");
                        return;
                    }
                    try {
                        downloadMedia(mMedia, "Other");
                    } catch (Throwable t) {
                        Toast("Please contact iHelp101 on XDA.");
                    }
                    param.setResult(null);
                }
            }
        });

        if (commentCheck.equals("No")) {
            findAndHookMethod(Comments, COMMENT_HOOK, Comments_Support, new XC_MethodHook() {
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
        }

        if (directShareCheck.equals("Yes")) {
            findAndHookMethod(dialogClass, DS_DIALOG_HOOK, CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
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
        }
    }

    private void UpdateHooks() {
        Thread getHooks= new Thread() {
                public void run() {
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response = httpclient.execute(new HttpGet("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt"));
                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            Hooks = out.toString();
                            out.close();
                        } else {
                            //Closes the connection.
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    } catch (IOException e) {
                        System.out.println(e);
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
            commentCheck = "No";
            directShareCheck = "Yes";
            HooksArray = HooksSave.split(";");

            try {
                FEED_CLASS_NAME = HooksArray[1];
                firstHook = HooksArray[1];
                MEDIA_CLASS_NAME = HooksArray[2];
                MEDIA_TYPE_CLASS_NAME = HooksArray[3];
                USER_CLASS_NAME = HooksArray[4];
                MEDIA_OPTIONS_BUTTON_CLASS_NAME = HooksArray[5];
                DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = HooksArray[6];
                DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = HooksArray[7];
                MEDIA_OPTIONS_BUTTON_HOOK = HooksArray[8];
                MEDIA_OPTIONS_BUTTON_HOOK2 = HooksArray[9];
                PERM__HOOK = HooksArray[10];
                PERM__HOOK2 = HooksArray[11];
                mMEDIA_HOOK = HooksArray[12];
                VIDEOTYPE_HOOK = HooksArray[13];
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
                COMMENT_HOOK = HooksArray[22];
                COMMENT_HOOK_CLASS2 = HooksArray[23];
            } catch (ArrayIndexOutOfBoundsException e) {
                commentCheck = "Yes";
            }

            try {
                DS_DIALOG_CLASS_NAME = HooksArray[24];
                DS_DIALOG_HOOK = HooksArray[25];
                MODEL_HOOK = HooksArray[26];
                ITEMID_DS_HOOK = HooksArray[27];
                ITEMID_DS_HOOK2 = HooksArray[28];
            } catch (ArrayIndexOutOfBoundsException e) {
                directShareCheck = "Nope";
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
                log("EEE: "+e);
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

        StringBuilder image = new StringBuilder();

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

        //Video Fetch
        File videolocation = new File(getDirectory + "/.Instagram/Video.txt");

        StringBuilder video = new StringBuilder();

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


