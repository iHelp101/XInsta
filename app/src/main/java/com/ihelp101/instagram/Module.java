package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage, Listen {

    Boolean hookCheck;
    CharSequence[] mMenuOptions = null;
    CharSequence[] mDirectShareMenuOptions = null;
    CharSequence[] mProfileOptions = null;
    Class<?> imageHook;
    Class<?> User;
    Class<?> dialogClass;
    public static Context mContext;
    public static Context nContext;
    Object mCurrentMediaOptionButton;
    Object mCurrentDirectShareMediaOptionButton;
    Object mUserName;

    String directShareCheck = "Nope";
    String firstHook;
    String Hooks = null;
    String[] HooksArray;
    String HookCheck = "No";
    String HooksSave;
    String linkToDownload;
    String notificationTitle;
    String oldCheck = "No";
    String version = "123";

    String ACCOUNT_HOOK;
    String ACCOUNT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS2;
    String DS_DIALOG_CLASS_NAME;
    String DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME;
    String DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME;
    String FEED_CLASS_NAME;
    String FULLNAME__HOOK;
    String IMAGE_HOOK;
    String IMAGE_HOOK_CLASS;
    String ITEMID_HOOK;
    String ITEMID_DS_HOOK;
    String ITEMID_DS_HOOK2;
    String LIKE_HOOK;
    String LIKE_HOOK_CLASS;
    String MEDIA_CLASS_NAME;
    String MEDIA_OPTIONS_BUTTON_CLASS_NAME;
    String MEDIA_OPTIONS_BUTTON_HOOK;
    String MEDIA_OPTIONS_BUTTON_HOOK2;
    String mMEDIA_HOOK;
    String mMEDIA_PHOTO_HOOK;
    String mMEDIA_VIDEO_HOOK;
    String MODEL_HOOK;
    String PERM__HOOK;
    String PERM__HOOK2;
    String PROFILE_HOOK;
    String PROFILE_HOOK_3;
    String PROFILE_HOOK_4;
    String PROFILE_HOOK_CLASS;
    String PROFILE_HOOK_CLASS2;
    String SAVE = "Instagram";
    String USER_CLASS_NAME;
    String USERNAME_HOOK;

    int versionCheck;
    LoadPackageParam loadPackageParam;

    Class<?> MediaOptionsButton;
    Class<?> DirectSharePermalinkMoreOptionsDialog;
    Class < ?> DirectShareMenuClickListener;

    private static void log(String log) {
		XposedBridge.log("XInsta: " + log);
	}

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.instagram.android"))
                return;

            loadPackageParam = lpparam;

            // Thank you to KeepChat For the Following Code Snippet
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            nContext = (Context) callMethod(activityThread, "getSystemContext");

            versionCheck = nContext.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
            //End Snippet

            setError("---------------------------------------------");
            setError("XInsta Initialized");
            setError("Instagram Version Code: " + versionCheck);
            setError("Device Codename: " + android.os.Build.HARDWARE);
            setError("Android Version: " + Build.VERSION.RELEASE);
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

        if (descriptionType.equals("photo")) {
            SAVE = "Image";
        } else {
            SAVE = "Video";
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

        linkToDownload = linkToDownload.replace("750x750", "");
        linkToDownload = linkToDownload.replace("640x640", "");
        linkToDownload = linkToDownload.replace("480x480", "");
        linkToDownload = linkToDownload.replace("320x320", "");

        try {
            Intent downloadIntent = new Intent();
            downloadIntent.setPackage("com.ihelp101.instagram");
            downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
            downloadIntent.putExtra("URL", linkToDownload);
            downloadIntent.putExtra("SAVE", SAVE);
            downloadIntent.putExtra("Notification", notificationTitle);
            downloadIntent.putExtra("Filename", fileName);
            downloadIntent.putExtra("User", userName);
            mContext.startService(downloadIntent);
        } catch (Exception e) {
            setError("Failed To Send Download Broadcast - " +e);
        }
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

        final String[] split = Helper.getHooks().split(";");

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

        try {
            LIKE_HOOK_CLASS = split[35];
            LIKE_HOOK = split[36];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            ACCOUNT_HOOK_CLASS = split[37];
            ACCOUNT_HOOK = split[38];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            UpdateHooks();
        } else {
            setError("Instagram First Hook: " + firstHook);
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
                                    setError("Unable To Determine Media - DS");
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

                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, mMEDIA_HOOK);
                        } catch (NoSuchFieldError e) {
                            setError("Menu Click Hook Invalid");
                            e.printStackTrace();
                        }

                        if (mMedia == null) {
                            setError("Unable To Determine Media - Feed");
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

                        String userName;

                        try {
                            userName = (String) XposedHelpers.getObjectField(object, PROFILE_HOOK_4);
                        } catch (Throwable t) {
                            setError("Profile Icon Username Hooks Failed:  " + t);
                            return;
                        }

                        notificationTitle = ResourceHelper.getString(mContext, R.string.username_thing, userName, "Icon");
                        notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);


                        String fileName = userName + "-Profile.jpg";

                        SAVE = "Profile";

                        Intent downloadIntent = new Intent();
                        downloadIntent.setPackage("com.ihelp101.instagram");
                        downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                        downloadIntent.putExtra("URL", linkToDownload);
                        downloadIntent.putExtra("SAVE", SAVE);
                        downloadIntent.putExtra("Notification", notificationTitle);
                        downloadIntent.putExtra("Filename", fileName);
                        downloadIntent.putExtra("User", userName);
                        mContext.startService(downloadIntent);

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Click Listener Failed - " +t.toString());
        }

        try {
            XposedHelpers.findAndHookMethod(LIKE_HOOK_CLASS, lpparam.classLoader, LIKE_HOOK, MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getLike().equals("Hide")) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Like Hooks Failed - " +t.toString());
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

        try {
            XposedHelpers.findAndHookMethod(ACCOUNT_HOOK_CLASS, lpparam.classLoader, ACCOUNT_HOOK, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }
            });
        } catch (Throwable t) {
            setError("Account Options Failed - " +t.toString());
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
                LIKE_HOOK_CLASS = HooksArray[35];
                LIKE_HOOK = HooksArray[36];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                ACCOUNT_HOOK_CLASS = HooksArray[37];
                ACCOUNT_HOOK = HooksArray[38];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            setError("Instagram First Hook: " + firstHook);

            Helper.setHooks(HooksSave);
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

    public void setError(String status) {
        log(status);
        Intent intent = new Intent();
        intent.setAction("com.ihelp101.instagram.Error");
        intent.putExtra("Error", status);
        nContext.sendBroadcast(intent);
    }

    public boolean HookCheck(final String version) {
        hookCheck = true;
        Thread getHooks= new Thread() {
            public void run() {
                String versions;
                try {
                    URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                    URLConnection c = u.openConnection();
                    c.connect();

                    InputStream inputStream = c.getInputStream();

                    versions = convertStreamToString(inputStream);
                } catch (Exception e) {
                    setError("Failed to fetch hooks.");
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

    @Override
    public void init() {

    }

    @Override
    public boolean shouldUserUpdate(String s, int i, String s1) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!HookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }

    @Override
    public boolean canAutoUpdate(String s, int i) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!HookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }
}


