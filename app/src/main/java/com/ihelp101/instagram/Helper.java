package com.ihelp101.instagram;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    public static String getFolder() {
        //Notification Option Fetch
        File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Folder.txt");
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

    public static String getHooks() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Hooks.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error: " +e);
        }

        return text.toString();
    }

    public static String getImage() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Image.txt");
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

    public static String getLike() {
        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/.Instagram/Like.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            text.append("Show");
        }

        return text.toString();
    }

    public static String getNotification() {
        //Notification Option Fetch
        File notification = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Notification.txt");
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

    public static String getProfile() {
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.Instagram/Profile.txt");
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

    public static String getVideo() {
        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/.Instagram/Video.txt");
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

    public static void setError(String data) {
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

    public static void setFolder(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Folder.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setHooks(String hooks) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Hooks.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(hooks);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setImage(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Image.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setLike(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Like.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setNotification(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Notification.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setProfile(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Profile.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }

    public static void setVideo(String data) {
        try {
            File root = new File(Environment.getExternalStorageDirectory().toString(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Video.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            setError("EEE: "+e);
        }
    }
}
