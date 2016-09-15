package com.ihelp101.instagram;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class Helper {

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

    static String getResourceString (Context context, int id) throws Throwable {
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
            return getString(context, id);
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

    static String checkSave(String SAVE, String userName, String fileName) {
        String saveLocation = SAVE;

        try {
            if (SAVE.equals("Instagram")) {
                saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
                if (Helper.getSettings("Folder")) {
                    saveLocation = saveLocation + userName + "/";
                }
                File directory = new File(URI.create(saveLocation).getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            } else {
                if (Helper.getSettings("Folder")) {
                    saveLocation = saveLocation + userName + "/";
                }
                File directory = new File(URI.create(saveLocation).getPath());
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
                File directory = new File(URI.create(saveLocation).getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            } else {
                if (Helper.getSettings("Folder")) {
                    saveLocation = saveLocation + userName + "/";
                }
                File directory = new File(URI.create(saveLocation).getPath());
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

    static void setError(String data) {
        if (data.equals("XInsta Initialized")) {
            try {
                if (Helper.getFolderSize(new File(Environment.getExternalStorageDirectory(), ".Instagram/Error.txt")) > 10000) {
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

    static void passDownload(String linkToDownload, String SAVE, String notificationTitle, String fileName, String fileType, String userName, Context mContext) {
        Intent downloadIntent = new Intent();
        downloadIntent.setPackage("com.ihelp101.instagram");
        downloadIntent.setAction("com.ihelp101.instagram.PASS");
        downloadIntent.putExtra("URL", linkToDownload);
        downloadIntent.putExtra("SAVE", SAVE);
        downloadIntent.putExtra("Notification", notificationTitle);
        downloadIntent.putExtra("Filename", fileName);
        downloadIntent.putExtra("Filetype", fileType);
        downloadIntent.putExtra("User", userName);
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
}
