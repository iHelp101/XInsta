package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.InputStream;
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
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage, IXposedHookInitPackageResources, Listen {

    Boolean hookCheck;
    CharSequence[] mMenuOptions = null;
    CharSequence[] mDirectShareMenuOptions = null;
    CharSequence[] mProfileOptions = null;
    public static Context mContext;
    public static Context nContext;
    Object mCurrentMediaOptionButton;
    Object mCurrentDirectShareMediaOptionButton;
    Object mUserName;
    Object model;

    String directShareCheck = "Nope";
    String fileType;
    String firstHook;
    String Hooks = null;
    String[] HooksArray;
    String HookCheck = "No";
    String HooksSave;
    String linkToDownload;
    String notificationTitle;
    String oldCheck = "No";
    String version = "123";

    String ACCOUNT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS2;
    String DS_DIALOG_CLASS_NAME;
    String DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME;
    String DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME;
    String FEED_CLASS_NAME;
    String FOLLOW_HOOK;
    String FOLLOW_HOOK_CLASS;
    String FULLNAME__HOOK;
    String IMAGE_HOOK_CLASS;
    String ITEMID_HOOK;
    String LIKE_HOOK_CLASS;
    String MEDIA_CLASS_NAME;
    String MEDIA_OPTIONS_BUTTON_CLASS_NAME;
    String mMEDIA_PHOTO_HOOK;
    String mMEDIA_VIDEO_HOOK;
    String PERM__HOOK;
    String PROFILE_HOOK_3;
    String PROFILE_HOOK_4;
    String PROFILE_HOOK_CLASS;
    String PROFILE_HOOK_CLASS2;
    String SAVE = "Instagram";
    String SUGGESTION_HOOK_CLASS;
    String TIME_HOOK;
    String USER_CLASS_NAME;
    String USERNAME_HOOK;

    int versionCheck;
    LoadPackageParam loadPackageParam;
    TextView count;

    void log(String log) {
		XposedBridge.log("XInsta: " + log);
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

    String dateFormat(Long epochTime) {
        String dateFormat = Helper.getSetting("Date");

        Date date = new Date(epochTime * 1000L);
        TimeZone timeZone = TimeZone.getDefault();
        if (dateFormat.equals("Instagram")) {
            dateFormat = ResourceHelper.getString(nContext, R.string.month) + ";" + ResourceHelper.getString(nContext, R.string.day) + ";" + ResourceHelper.getString(nContext, R.string.year) + ";" + ResourceHelper.getString(nContext, R.string.space) + ";" + ResourceHelper.getString(nContext, R.string.hour) + ";" + ResourceHelper.getString(nContext, R.string.minute) + ";" + ResourceHelper.getString(nContext, R.string.second) + ";" + ResourceHelper.getString(nContext, R.string.space2) + ";" + ResourceHelper.getString(nContext, R.string.am) + ";";
        }

        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.month) + ";", "/MM");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.day) + ";", "/dd");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.year) + ";", "/yyyy");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.hour) + ";", "hh:");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.minute) + ";", "mm:");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.second) + ";", "ss:");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.am) + ";", "a");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.space) + ";", " ");
        dateFormat = dateFormat.replace(ResourceHelper.getString(nContext, R.string.space2) + ";", " ");
        dateFormat = dateFormat.substring(1);

        String[] dateSplit = dateFormat.split(":");

        if (dateSplit.length > 1) {
            int dateLength = dateSplit.length - 2;
            dateFormat = dateFormat.replace(dateSplit[dateLength] + ":", dateSplit[dateLength]);
        }

        DateFormat format = new SimpleDateFormat(dateFormat);
        format.setTimeZone(timeZone);

        return format.format(date);
    }

    @Override
    public void handleInitPackageResources(final XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.instagram.android"))
            return;

        resparam.res.hookLayout("com.instagram.android", "layout", "row_profile_scoreboard_header", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liparam) throws Throwable {
                count = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("row_profile_header_textview_following_count", "id", "com.instagram.android"));
            }
        });
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

            setError("XInsta Initialized");
            setError("Instagram Version Code: " + versionCheck);
            setError("Device Codename: " + Build.MODEL);
            setError("Android Version: " + Build.VERSION.RELEASE);
            startHooks();
    }

	@SuppressLint("NewApi")
	void downloadMedia(Object mMedia, String where) throws IllegalAccessException, IllegalArgumentException {
        String filenameExtension;
        String descriptionType;
        int descriptionTypeId;

        try {
            linkToDownload = (String) getObjectField(mMedia, mMEDIA_VIDEO_HOOK);
            filenameExtension = "mp4";
            fileType = "Video";
            descriptionType = "video";
            descriptionTypeId = R.string.video;

            if (linkToDownload.equals("None")) {
                filenameExtension = "";
            }
        } catch (Throwable throwable) {
            setError("Switch Link - Different Media Type");
            if (oldCheck.equals("No")) {
                try {
                    Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                    Object photo = getFieldByType(mMedia, Image);
                    linkToDownload = (String) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, String.class).getName());
                    } catch (Throwable t) {
                    Toast("Please contact iHelp101 on XDA.");
                    setError("Photo Hook Invalid - " + IMAGE_HOOK_CLASS);
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
            fileType = "Image";
            descriptionType = "photo";
            descriptionTypeId = R.string.photo;
        }

        if (descriptionType.equals("photo")) {
            SAVE = ResourceHelper.getString(mContext, R.string.Image);
        } else {
            SAVE = ResourceHelper.getString(mContext, R.string.Video);
        }

		// Construct filename
		// username_imageId.jpg
		descriptionType = ResourceHelper.getString(mContext, descriptionTypeId);
		String toastMessage = ResourceHelper.getString(mContext, R.string.Downloading, descriptionType);
		Toast(toastMessage);

		Object mUser;
        try {
            mUser = getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        } catch (Throwable t) {
            Toast("Please contact iHelp101 on XDA.");
            setError("mUser Hook Invalid - " + USER_CLASS_NAME);
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
            downloadIntent.putExtra("Filetype", fileType);
            downloadIntent.putExtra("User", userName);
            mContext.startService(downloadIntent);
        } catch (Exception e) {
            setError("Failed To Send Download Broadcast - " +e);
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

    void hookAccountOptions() {
        try {
            Class<?> Account = findClass(ACCOUNT_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Account, boolean.class);

            XposedHelpers.findAndHookMethod(Account, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }
            });
        } catch (Throwable t) {
            setError("Account Options Failed - " + t.toString());
        }
    }

    boolean hookCheck(final String version) {
        hookCheck = true;
        Thread getHooks= new Thread() {
            public void run() {
                String versions;
                try {
                    URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                    URLConnection c = u.openConnection();
                    c.connect();

                    InputStream inputStream = c.getInputStream();

                    versions = Helper.convertStreamToString(inputStream);
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

    void hookComments() {
        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader), boolean.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

            findAndHookMethod(findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader), methods[0].getName(), findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object pj = param.args[0];

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) AndroidAppHelper.currentApplication().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", (String) getObjectField(pj, "d"));
                    clipboard.setPrimaryClip(clip);
                    Toast(ResourceHelper.getString(AndroidAppHelper.currentApplication().getApplicationContext(), R.string.Copied));
                }
            });
        } catch (Throwable t) {
            setError("Comment Check Failed - " +t.toString());
        }
    }

    void hookDirectShare() {
        try {
            if (directShareCheck.equals("Nope")) {
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), PERM__HOOK, injectDownloadIntoCharSequenceHook);
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), PERM__HOOK, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = param.thisObject;
                    }
                });

                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), void.class, DialogInterface.class, int.class);

                findAndHookMethod(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
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
                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), void.class, DialogInterface.class, int.class);

                findAndHookMethod(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                                setError("Directshare Image/Video Minimized: " +t);
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
            Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), findClass(DS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), CharSequence[].class, android.content.DialogInterface.OnClickListener.class);

            findAndHookMethod(findClass(DS_DIALOG_CLASS_NAME, loadPackageParam.classLoader), methods[0].getName(), CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
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

    void hookFeed() {
        try {
            Class<?> Media = findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Media, CharSequence[].class);

            findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), methods[0].getName(), injectDownloadIntoCharSequenceHook);
            findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mCurrentMediaOptionButton = param.thisObject;
                }
            });
        } catch (Throwable t) {
            setError("Media Options Button Hook Failed - " +t.toString());
        }

        try {
            Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(MenuClickListener, void.class, DialogInterface.class, int.class);

            findAndHookMethod(MenuClickListener, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];
                    if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                        Object mMedia = null;

                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                        } catch (Throwable t) {
                            setError("Menu Click Hook Failed: " + t);
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
            setError("Menu Click Listener Failed - " + t.toString());
        }
    }

    void hookFollow() {
        try {
            XposedHelpers.findAndHookMethod(FOLLOW_HOOK_CLASS, loadPackageParam.classLoader, FOLLOW_HOOK, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        byte[] bytes = (byte[]) getFieldByType(param.thisObject, byte[].class);

                        JSONObject jObject = new JSONObject(new String(bytes, "UTF-8"));

                        if (jObject.getString("followed_by").equals("true")) {
                            count.setTextColor(Color.parseColor("#2E978C"));
                        }
                    } catch (Throwable t) {
                    }
                }
            });
        } catch (Throwable t) {
            setError("Follow Feature Failed - " + t.toString());
        }
    }

    void hookInstagram() {
        try {
            hookAccountOptions();
        } catch (Throwable t) {
        }

        try {
            hookComments();
        } catch (Throwable t) {
        }

        try {
            hookDirectShare();
        } catch (Throwable t) {
        }

        try {
            hookFeed();
        } catch (Throwable t) {
        }

        try {
            hookFollow();
        } catch (Throwable t) {
        }

        try {
            hookLike();
        } catch (Throwable t) {
        }

        try {
            hookNotification();
        } catch (Throwable t) {
        }

        try {
            hookProfileIcon();
        } catch (Throwable t) {
        }

        try {
            hookSuggestion();
        } catch (Throwable t) {
        }

        try {
            hookTime();
        } catch (Throwable t) {
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
                    if (Helper.getSettings("Like")) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Like Hooks Failed - " + t.toString());
            setError("Like Hook Class: " +LIKE_HOOK_CLASS);
        }
    }

    void hookNotification() {
        Class<?> Notification = findClass("com.instagram.android.c2dm.d", loadPackageParam.classLoader);
        Method[] methods = XposedHelpers.findMethodsByExactParameters(Notification, void.class, Intent.class, String.class);

        XposedHelpers.findAndHookMethod(Notification, methods[0].getName(), Intent.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("Info; " +param.getResult());
                if (Helper.getSettings("Push")) {
                    try {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        Intent intent = (Intent) param.args[0];
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

                        String userName = jsonObject.getString("m").split(" ")[0];
                        String photoName = ResourceGrab.getString(mContext, "photo", loadPackageParam.packageName).toLowerCase();
                        String photoCheck = jsonObject.getString("m").replace(userName, "").toLowerCase();

                        if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(photoName)) {
                            Intent downloadIntent = new Intent();

                            String fileExtension = ".jpg";
                            String fileDescription = ResourceHelper.getString(mContext, R.string.photo);
                            fileType = "Image";

                            String fileName = userName + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;
                            notificationTitle = ResourceHelper.getString(mContext, R.string.username_thing, userName, fileDescription);
                            ;
                            notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);
                            ;

                            downloadIntent.setPackage("com.ihelp101.instagram");
                            downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                            downloadIntent.putExtra("URL", jsonObject.getString("i"));
                            downloadIntent.putExtra("SAVE", ResourceHelper.getString(mContext, R.string.Image));
                            downloadIntent.putExtra("Notification", notificationTitle);
                            downloadIntent.putExtra("Filename", fileName);
                            downloadIntent.putExtra("Filetype", fileType);
                            downloadIntent.putExtra("User", userName);
                            mContext.startService(downloadIntent);
                        }
                    } catch (Throwable t) {
                        setError("Notification Error: " + t.toString());
                    }
                }
            }
        });
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
            setError("Profile Icon Class: " +PROFILE_HOOK_CLASS);
        }


        try {
            Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile2, void.class, DialogInterface.class, int.class);

            findAndHookMethod(Profile2, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mProfileOptions[(int) param.args[1]].toString();

                    if (ResourceHelper.getString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {

                        Class<?> Profile = findClass(PROFILE_HOOK_CLASS2, loadPackageParam.classLoader);
                        Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS, loadPackageParam.classLoader);
                        Class<?> ProfileUser = findClass(USER_CLASS_NAME, loadPackageParam.classLoader);

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

                        SAVE = ResourceHelper.getString(mContext, R.string.Profile);

                        Intent downloadIntent = new Intent();
                        downloadIntent.setPackage("com.ihelp101.instagram");
                        downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                        downloadIntent.putExtra("URL", linkToDownload);
                        downloadIntent.putExtra("SAVE", SAVE);
                        downloadIntent.putExtra("Notification", notificationTitle);
                        downloadIntent.putExtra("Filename", fileName);
                        downloadIntent.putExtra("Filetype", "Profile");
                        downloadIntent.putExtra("User", userName);
                        mContext.startService(downloadIntent);

                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Click Listener Failed - " + t.toString());
            setError("Profile Icon Click Listener Class: " +PROFILE_HOOK_CLASS2);
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
                    if (Helper.getSettings("Suggestion")) {
                        View view = (View) param.getResult();
                        Class<?> listClass = findClass(view.getTag().getClass().getName(), loadPackageParam.classLoader);
                        List list = (List) XposedHelpers.getObjectField(view.getTag(), XposedHelpers.findFirstFieldByExactType(listClass, List.class).getName());
                        list.clear();
                    }
                }
            });
        } catch (Throwable t) {
            setError("Suggestion Hooks Failed - " +t.toString());
            setError("Suggestion Hook Class: " +SUGGESTION_HOOK_CLASS);
        }
    }

    void hookTime() {
        try {
            if (TIME_HOOK.equals("Nope")) {
                final Class<?> Time = XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, CharSequence.class, Context.class);

                XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(dateFormat((Long) XposedHelpers.getObjectField(param.thisObject, (XposedHelpers.findFirstFieldByExactType(Time, long.class)).getName())));
                    }
                });
            } else {
                final Class<?> Time = XposedHelpers.findClass(TIME_HOOK, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                XposedHelpers.findAndHookMethod(Time, methods[1].getName(), Context.class, long.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(dateFormat((Long) param.args[1]));
                    }
                });
            }
        } catch(Throwable t){
            setError("Time Hooks Failed: " + t);
            setError("Time Hooks Class: " + MEDIA_CLASS_NAME);
        }
    }

    @Override
    public void init() {
    }

    void setError(String status) {
        log(status);
        Intent intent = new Intent();
        intent.setAction("com.ihelp101.instagram.Error");
        intent.putExtra("Error", status);
        nContext.sendBroadcast(intent);
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
            MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[5];
            DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = split[6];
            DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = split[7];
            PERM__HOOK = split[10];
            mMEDIA_VIDEO_HOOK = split[14];
            mMEDIA_PHOTO_HOOK = split[15];
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
            COMMENT_HOOK_CLASS2 = split[23];
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        try {
            DS_DIALOG_CLASS_NAME = split[24];
            directShareCheck = "Yes";
        } catch (ArrayIndexOutOfBoundsException e) {
            directShareCheck = "Nope";
        }

        try {
            PROFILE_HOOK_CLASS = split[29];
            PROFILE_HOOK_CLASS2 = split[30];
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
            TIME_HOOK = split[43];
        } catch (ArrayIndexOutOfBoundsException e) {
            TIME_HOOK = "Nope";
        }

        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            updateHooks();
        } else {
            setError("Instagram First Hook: " + firstHook);
        }

        if (HookCheck.equals("Yes")) {
            setError("Please update your hooks via the module.");
        } else {
            try {
                hookInstagram();
            } catch (Throwable t) {
                setError("Hooks Check Failed - " +t.toString());
            }
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
                        URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = c.getInputStream();

                        Hooks = Helper.convertStreamToString(inputStream);
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
                PERM__HOOK = HooksArray[10];
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
            } catch (ArrayIndexOutOfBoundsException e) {
                directShareCheck = "Nope";
            }

            try {
                PROFILE_HOOK_CLASS = HooksArray[29];
                PROFILE_HOOK_CLASS2 = HooksArray[30];
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

        setError("Instagram First Hook: " + firstHook);

            Helper.setSetting("Hooks", HooksSave);
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
                    menuOptionsField = XposedHelpers.findFirstFieldByExactType(findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, loadPackageParam.classLoader), CharSequence[].class);
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
            }
        }
    };
}


