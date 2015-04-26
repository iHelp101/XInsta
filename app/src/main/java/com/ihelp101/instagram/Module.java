package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;

	private CharSequence[] mMenuOptions = null;
	private CharSequence[] mDirectShareMenuOptions = null;
	private Object mCurrentMediaOptionButton;
	private Object mCurrentDirectShareMediaOptionButton;
	private static final String mDownloadString = "Download";
	private static String mDownloadTranslated;
    private static String videoLocation;
    private static String imageLocation;
    private static String linkToDownload;
    private static String oldCheck = "No";

	private static Context mContext;
    private static Context nContext;

	private static Class<?> MediaType;
	private static Class<?> User;
    private static Class<?> imageHook;

    private static String SAVE = "Instagram";
	private static String FEED_CLASS_NAME = "Nope";
	private static String MEDIA_CLASS_NAME = "Nope";
	private static String MEDIA_TYPE_CLASS_NAME = "Nope";
	private static String USER_CLASS_NAME = "Nope";
	private static String MEDIA_OPTIONS_BUTTON_CLASS_NAME = "Nope";
	private static String DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME = "Nope";
	private static String DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME = "Nope";
    private static String MEDIA_OPTIONS_BUTTON_HOOK = "Nope";
    private static String MEDIA_OPTIONS_BUTTON_HOOK2 = "Nope";
    private static String PERM__HOOK = "Nope";
    private static String PERM__HOOK2 = "Nope";
    private static String mMEDIA_HOOK = "Nope";
    private static String VIDEOTYPE_HOOK = "Nope";
    private static String mMEDIA_VIDEO_HOOK = "Nope";
    private static String mMEDIA_PHOTO_HOOK = "Nope";
    private static String USERNAME_HOOK = "Nope";
    private static String FULLNAME__HOOK = "Nope";
    private static String IMAGE_HOOK_CLASS = "Nope";
    private static String IMAGE_HOOK = "Nope";

    private int scan = 0;


    private static void log(String log) {
		XposedBridge.log("Module: " + log);
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.instagram.android"))
			return;

        // Thank you to KeepChat For the Following Code Snippet
        // http://git.io/JJZPaw
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context context = (Context) callMethod(activityThread, "getSystemContext");

        final int versionCheck = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
        //End Snippet

        nContext = context;

        XposedBridge.log("Instagram Version Code: " + versionCheck);

        //Hook Fetch
        File file = new File(Environment.getExternalStorageDirectory() + "/.Instagram/Hooks.txt");

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
            XposedBridge.log("Please update hooks via the module.");
        }

        String[] split = text.toString().split(";");

        //Image Fetch
        File imagelocation = new File(Environment.getExternalStorageDirectory() + "/.Instagram/Image.txt");

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
        File videolocation = new File(Environment.getExternalStorageDirectory() + "/.Instagram/Video.txt");

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

        imageLocation = image.toString();
        videoLocation = video.toString();
        FEED_CLASS_NAME = split[1];
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
        try {
            IMAGE_HOOK_CLASS = split[18];
            IMAGE_HOOK = split[19];
        } catch (ArrayIndexOutOfBoundsException e) {
            oldCheck = "Yes";
        }

        XposedBridge.log("Instagram First Hook: " + split[1]);

        if (FEED_CLASS_NAME.equals("Nope")||MEDIA_CLASS_NAME.equals("Nope")||MEDIA_TYPE_CLASS_NAME.equals("Nope")||USER_CLASS_NAME.equals("Nope")||MEDIA_OPTIONS_BUTTON_CLASS_NAME.equals("Nope")||DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME.equals("Nope")||DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME.equals("Nope")||MEDIA_OPTIONS_BUTTON_HOOK.equals("Nope")||MEDIA_OPTIONS_BUTTON_HOOK2.equals("Nope")||PERM__HOOK.equals("Nope")||PERM__HOOK2.equals("Nope")||mMEDIA_HOOK.equals("Nope")||VIDEOTYPE_HOOK.equals("Nope")||mMEDIA_VIDEO_HOOK.equals("Nope")||mMEDIA_PHOTO_HOOK .equals("Nope")||USERNAME_HOOK.equals("Nope")||FULLNAME__HOOK.equals("Nope")) {

        } else {
		    /* Hi Facebook team! Obfuscating the package isn't enough */
            final Class<?> MediaOptionsButton = findClass(MEDIA_OPTIONS_BUTTON_CLASS_NAME, lpparam.classLoader);
            final Class<?> DirectSharePermalinkMoreOptionsDialog = findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS_NAME,
                    lpparam.classLoader);
            MediaType = findClass(MEDIA_TYPE_CLASS_NAME, lpparam.classLoader);
            User = findClass(USER_CLASS_NAME, lpparam.classLoader);
            if (oldCheck.equals("No")) {
                imageHook = findClass(IMAGE_HOOK_CLASS, lpparam.classLoader);
            }

            if (imageLocation.equals("")) {
                imageLocation = "Instagram";
            }

            if (videoLocation.equals("")) {
                imageLocation = "Instagram";
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

                    if (mContext != null) {
                        mDownloadTranslated = ResourceHelper.getString(mContext, R.string.the_not_so_big_but_big_button);
                    }

                    if (!array.contains(getDownloadString()))
                        array.add(getDownloadString());
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


            findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK, injectDownloadIntoCharSequenceHook);
            findAndHookMethod(DirectSharePermalinkMoreOptionsDialog, PERM__HOOK2, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mCurrentDirectShareMediaOptionButton = param.thisObject;
                }
            });

            Class < ?> DirectShareMenuClickListener = findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS_NAME, lpparam.classLoader);
            findAndHookMethod(DirectShareMenuClickListener, "onClick", DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence localCharSequence = mDirectShareMenuOptions[(Integer) param.args[1]];
                    if (mContext == null)
                        mContext = ((Dialog) param.args[0]).getContext();
                    if (getDownloadString().equals(localCharSequence)) {
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

                        downloadMedia(mCurrentDirectShareMediaOptionButton, mMedia);

                        param.setResult(null);
                    }
                }
            });

            Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, lpparam.classLoader);
            findAndHookMethod(MenuClickListener, "onClick", DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (mContext == null) {
                        mContext = context;
                    }
                    CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];
                    if (mDownloadString.equals(localCharSequence)) {
                        Object mMedia = null;

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, mMEDIA_HOOK);
                        } catch (NoSuchFieldError e) {
                            log("Failed to get media: " + e.getMessage());
                            e.printStackTrace();
                        }

                        if (mMedia == null) {
                            Toast.makeText(mContext, "Unable to determine media, download failed",
                                    Toast.LENGTH_SHORT).show();
                            log("Unable to determine media");
                            return;
                        }

                        try {
                            downloadMedia(mCurrentMediaOptionButton, mMedia);
                        } catch (Throwable t) {
                            log("Unable to download media: " + t.getMessage());
                            t.printStackTrace();
                        }
                        param.setResult(null);
                    }
                }
            });
        }
	}

	@SuppressLint("NewApi")
	private void downloadMedia(Object sourceButton, Object mMedia) throws IllegalAccessException, IllegalArgumentException {
		Field contextField =
				XposedHelpers.findFirstFieldByExactType(sourceButton.getClass(), Context.class);
		if (mContext == null) {
			try {
				mContext = (Context) contextField.get(sourceButton);
			} catch (Exception e) {
				e.printStackTrace();
				log("Failed to get Context");
				return;
			}
		}

		Object mMediaType = getFieldByType(mMedia, MediaType);
		if (mMediaType == null) {
			log("Failed to get MediaType");
			return;
		}

		Object videoType = getStaticObjectField(MediaType, VIDEOTYPE_HOOK);

        if (videoType == null) {
            log("Video Type not found!");
        }

		String filenameExtension;
		String descriptionType;
		int descriptionTypeId = R.string.photo;

		if (mMediaType.equals(videoType)) {
            linkToDownload = (String) getObjectField(mMedia, mMEDIA_VIDEO_HOOK);
			filenameExtension = "mp4";
			descriptionType = "video";
			descriptionTypeId = R.string.video;
		} else {
            if (oldCheck.equals("No")) {
                Object photo = getFieldByType(mMedia, imageHook);
                linkToDownload = (String) getObjectField(photo, IMAGE_HOOK);
            } else {
                linkToDownload = (String) getObjectField(mMedia, mMEDIA_PHOTO_HOOK);
            }
			filenameExtension = "jpg";
			descriptionType = "photo";
			descriptionTypeId = R.string.photo;
		}

        if (descriptionType.equals("photo")) {
            SAVE = imageLocation;
        } else {
            SAVE = videoLocation;
        }

		// Construct filename
		// username_imageId.jpg
		descriptionType = ResourceHelper.getString(mContext, descriptionTypeId);
		String toastMessage = ResourceHelper.getString(mContext, R.string.downloading, descriptionType);
		Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show();

		Object mUser = getFieldByType(mMedia, User);
		String userName, userFullName;
		if (mUser == null) {
			log("Failed to get User from Media, using placeholders");
			userName = "username_placeholder";
			userFullName = "Unknown name";
		} else {
			userName = (String) getObjectField(mUser, USERNAME_HOOK);
			userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
		}

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String itemId = sdf.format(new Date());
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext);
            mBuilder.setContentTitle("" + User + "'s " + Desc)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentText("Downloading.....");
            if (Build.VERSION.SDK_INT > 11) {
                mBuilder.setProgress(100, 0, false);
            }
            mNotifyManager.notify(id, mBuilder.build());


            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Location);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
                Toast.makeText(nContext, "Download failed.", Toast.LENGTH_LONG).show();
                mBuilder.setContentTitle("" + User + "'s " + Desc);
                mBuilder.setContentText("Download failed.");
                mBuilder.setTicker("Download failed.");
                if (Build.VERSION.SDK_INT > 11) {
                    mBuilder.setProgress(0, 0, false);
                }
                mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
                mBuilder.setAutoCancel(true);
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (Build.VERSION.SDK_INT > 11) {
                mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }

        @Override
        protected void onPostExecute(String unused) {
            Toast.makeText(mContext, "Download completed.", Toast.LENGTH_LONG).show();

            mBuilder.setContentTitle("" + User + "'s " + Desc);
            mBuilder.setContentText("Download completed.");
            mBuilder.setTicker("Download completed.");
            mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
            if (Build.VERSION.SDK_INT > 11) {
                mBuilder.setProgress(0, 0, false);
            }
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
                    PendingIntent.FLAG_UPDATE_CURRENT );

            mBuilder.setContentIntent(contentIntent);
            mNotifyManager.notify(id, mBuilder.build());

            MediaScannerConnection.scanFile(nContext,
                    new String[]{Location}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                scan = 1;
                            }
                        }
                    });

            if (scan == 1) {
                scan = 0;
                Toast.makeText(nContext, "Download completed.", Toast.LENGTH_LONG).show();
            }
        }
    }

	private String getDownloadString() {
		if (mDownloadTranslated == null)
			return mDownloadString;

		return mDownloadTranslated;
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
}


