package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class Downloader extends Service {

    Context mContext;
    Bitmap icon;
    int count = 0;
    int id = 1;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String postCode = "";
    String userName;
    String SAVE = "Instagram";
    String jsonInformation;


    String[] links;
    String[] fileNames;
    String[] fileTypes;
    String[] notificationTitles;
    String[] saveLocations;
    String[] userNames;
    int current = 0;
    int skip = 0;

    Intent serviceIntent;
    Elements elements;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Download extends AsyncTask<String, String, String> {

        int downloadFailed = 1;
        int logNotification = 0;

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                downloadFailed = 1;
                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (linkToDownload.contains("media123;")) {
                    linkToDownload = linkToDownload.replaceAll("media123;", "");
                }

                if (linkToDownload.contains("notification")) {
                    linkToDownload = linkToDownload.replaceAll("notification", "");
                    logNotification = 1;
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

                if (!Helper.getSettings("Notification")) {
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
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output;

                output = new FileOutputStream(SAVE);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) >= 0) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                if (!Helper.getSettings("Notification")) {
                    String downloadComplete;

                    if (logNotification == 1) {
                        Helper.setPush("Downloaded: " +notificationTitle);
                    }

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

                    File file = new File(SAVE);
                    if (SAVE.contains("jpg")) {
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
                Helper.setError("Download Error: " + t);

                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                if (!Helper.getSettings("Notification") || Helper.getSettings("PushFailed")) {
                    mBuilder.setContentText(downloadFailed)
                            .setTicker(downloadFailed)
                            .setContentTitle(notificationTitle)
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
                } catch (Throwable t2) {
                    downloadComplete = "Download Complete";
                }

                Toast(downloadComplete);

                MediaScannerConnection.scanFile(mContext,
                            new String[]{SAVE}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    if (uri != null) {
                                        int scan = 1;
                                    }
                                }
                            });
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(mContext, R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                Toast(downloadFailed);
            }

            try {
                if (current + 1 < links.length) {
                    current = current + 1;
                    linkToDownload = links[current];
                    userName = userNames[current];
                    notificationTitle = notificationTitles[current];
                    fileName = fileNames[current];
                    fileType = fileTypes[current];
                    SAVE = saveLocations[current];

                    downloadOrPass();
                } else {
                    stopSelf();
                }
            } catch (Throwable t) {
                stopSelf();
            }
        }
    }

    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
                    try {
                        URL u;

                        if (linkToDownload.contains("media123")) {
                            u = new URL("https://www.instagram.com/" + userName + "/?__a=1");
                        } else {
                            linkToDownload = linkToDownload.replaceAll("notification", "");
                            u = new URL(linkToDownload);
                        }

                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());

                        jsonInformation = JSONInfo;

                        JSONObject jsonObject = new JSONObject(JSONInfo);

                        String descriptionType;

                        System.out.println("JSON: " +jsonObject);

                        try {
                            descriptionType = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("is_video");
                        } catch (Throwable t) {
                            setError("Video Fetch Type Failed: " +t);
                            if (linkToDownload.contains("media123")) {
                                Helper.setPush("Private Account - Trying Image4");
                                linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
                                linkToDownload = "notification" + linkToDownload;

                                downloadOrPass();
                            }
                            descriptionType = "None";
                        }

                        System.out.println("Hi!");

                        if (descriptionType.equals("false")) {
                            linkToDownload = "https://www.instagram.com/p/" + jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("code") + "/media";

                            String fileExtension = "jpg";
                            String mediaId = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("id");
                            String userId = jsonObject.getJSONObject("user").getString("id");
                            String date = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("date");
                            date = Helper.getDate(Long.parseLong(date), mContext);

                            if (!Helper.getSetting("FileFormat").equals("Instagram") && !Helper.getSetting("File").equals("Instagram")) {
                                fileName = Helper.getSetting("FileFormat");
                                fileName = fileName.replace("Username", userName);
                                fileName = fileName.replace("MediaID", mediaId);
                                fileName = fileName.replace("UserID", userId);
                                fileName = fileName.replace("Date", date);
                                fileName = fileName + "." + fileExtension;
                            } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                                fileName = Helper.getSetting("FileFormat");
                                fileName = fileName.replace("Username", userName);
                                fileName = fileName.replace("MediaID", mediaId);
                                fileName = fileName.replace("UserID", userId);
                                fileName = fileName.replace("Date", date);
                                fileName = fileName + "." + fileExtension;
                            } else if (!Helper.getSetting("File").equals("Instagram")) {
                                try {
                                    String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), getApplicationContext());
                                    String itemId = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("id");

                                    itemId = itemId + itemToString;

                                    fileName = userName + "_" + itemId + fileExtension;
                                } catch (Throwable t) {
                                    setError("Auto Epoch Failed - " +t);
                                }
                            } else {
                                fileName = userName + "_ " + mediaId + "_ " + date + "." + fileExtension;
                            }

                            if (Helper.getSettings("URLFileName")) {
                                int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
                                fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
                            }

                            linkToDownload = linkToDownload.replace("750x750", "");
                            linkToDownload = linkToDownload.replace("640x640", "");
                            linkToDownload = linkToDownload.replace("480x480", "");
                            linkToDownload = linkToDownload + "/?size=l";
                            linkToDownload = linkToDownload.replace("320x320", "");
                            linkToDownload = "notification" + linkToDownload;

                        }

                        if (descriptionType.equals("true")) {
                            try {
                                descriptionType = Helper.getResourceString(mContext, R.string.video);
                            } catch (Throwable t) {
                                descriptionType = "Video";
                            }
                            String fileExtension = ".mp4";
                            try {
                                fileName = userName + "_" + jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("id") + fileExtension;
                            } catch (Throwable t) {
                                setError("Video Fetch File Name Failed: " +t);
                            }
                            fileType = "Video";

                            String mediaId = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("id");
                            String userId = jsonObject.getJSONObject("user").getString("id");
                            String date = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("date");
                            date = Helper.getDate(Long.parseLong(date), mContext);

                            if (!Helper.getSetting("FileFormat").equals("Instagram") && !Helper.getSetting("File").equals("Instagram")) {
                                fileName = Helper.getSetting("FileFormat");fileName = Helper.getSetting("FileFormat");
                                fileName = fileName.replace("Username", userName);
                                fileName = fileName.replace("MediaID", mediaId);
                                fileName = fileName.replace("UserID", userId);
                                fileName = fileName.replace("Date", date);
                                fileName = fileName + "." + fileExtension;
                            } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                                fileName = Helper.getSetting("fileName");fileName = Helper.getSetting("FileFormat");
                                fileName = fileName.replace("Username", userName);
                                fileName = fileName.replace("MediaID", mediaId);
                                fileName = fileName.replace("UserID", userId);
                                fileName = fileName.replace("Date", date);
                                fileName = fileName + "." + fileExtension;
                            } else if (!Helper.getSetting("File").equals("Instagram")) {
                                try {
                                    String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), getApplicationContext());
                                    String itemId = jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("id");

                                    itemId = itemId + itemToString;

                                    fileName = userName + "_" + itemId + fileExtension;
                                } catch (Throwable t) {
                                    setError("Auto Epoch Failed - " +t);
                                }
                            } else {
                                fileName = userName + "_ " + mediaId + "_ " + date + "." + fileExtension;
                            }

                            try {
                                String videoUrl = "https://www.instagram.com/p/" + jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("code");

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

                            linkToDownload = linkToDownload.replace("750x750", "");
                            linkToDownload = linkToDownload.replace("640x640", "");
                            linkToDownload = linkToDownload.replace("480x480", "");
                            linkToDownload = linkToDownload.replace("320x320", "");

                            linkToDownload = "notification" + linkToDownload;

                            try {
                                notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, descriptionType);
                            } catch (Throwable t) {
                                notificationTitle = userName + "'s " +descriptionType;
                            }
                            notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

                            SAVE = Helper.getSaveLocation(fileType);

                            downloadOrPass();
                        } else if (linkToDownload.contains("media123")){
                            linkToDownload = linkToDownload.replaceAll("media123;", "");
                            linkToDownload = "notification" + linkToDownload;

                            downloadOrPass();
                        } else if (descriptionType.equals("false")) {
                            SAVE = Helper.getSaveLocation(fileType);
                            downloadOrPass();
                        }
                    } catch (Exception e) {
                        if (linkToDownload.contains("media123")){
                            Helper.setPush("Private Account");
                            linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
                            linkToDownload = "notification" + linkToDownload;

                            downloadOrPass();
                        } else {
                            setError("Notification Fetch Failed: " + e);
                            Helper.setPush("Notification Fetch Failed: " +e);
                            Helper.setPush("Notification Fetch Failed URL - " +linkToDownload);
                            Helper.setPush("Notification JSON: " +jsonInformation);
                        }
                    }
            return responseString;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        serviceIntent = intent;
        skip = 0;
        try {
            if (intent.getStringExtra("URL").contains("/?__a=1") || intent.getStringExtra("URL").contains("media123;") ) {
                linkToDownload = intent.getStringExtra("URL");
                userName = intent.getStringExtra("User");

                try {
                    notificationTitle = intent.getStringExtra("Notification");
                    fileName = intent.getStringExtra("Filename");
                    fileType = intent.getStringExtra("Filetype");
                } catch (Throwable t) {
                }

                getPostUrl();
            } else if (intent.getStringExtra("URL").contains(";")) {
                current = 0;
                links = intent.getStringExtra("URL").split(";");
                userNames = intent.getStringExtra("User").split(";");
                notificationTitles = intent.getStringExtra("Notification").split(";");
                fileNames = intent.getStringExtra("Filename").split(";");
                fileTypes = intent.getStringExtra("Filetype").split(";");
                saveLocations =intent.getStringExtra("SAVE").split(";");

                linkToDownload = links[0];
                userName = userNames[0];
                notificationTitle = notificationTitles[0];
                fileName = fileNames[0];
                fileType = fileTypes[0];
                SAVE = saveLocations[0];

                downloadOrPass();
            } else {
                Intent downloadIntent = new Intent();
                downloadIntent.setPackage("com.ihelp101.instagram");
                downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                downloadIntent.putExtra("URL", intent.getStringExtra("URL"));
                downloadIntent.putExtra("SAVE", intent.getStringExtra("SAVE"));
                downloadIntent.putExtra("Notification", intent.getStringExtra("Notification"));
                downloadIntent.putExtra("Filename", intent.getStringExtra("Filename"));
                downloadIntent.putExtra("Filetype", intent.getStringExtra("Filetype"));
                downloadIntent.putExtra("User", intent.getStringExtra("User"));
                mContext.startService(downloadIntent);
                stopSelf();
            }
        } catch (Throwable t) {
            setError("Download Pass Failed: " + t);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void checkForMulti(final String postUrl) {
        elements = null;
        try {
            Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL(postUrl);
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = u.openStream();

                        elements = Jsoup.parse(Helper.convertStreamToString(inputStream)).select("body").first().children();
                    } catch (Exception e) {
                        Helper.setError("Multi Check Failed: " + e);
                    }
                }
            };
            checkMedia.start();
            checkMedia.join();

            if (elements.toString().contains("edge_sidecar_to_children")) {
                downloadMulti();
            } else {
                new RequestTask().execute();
            }

        } catch (Throwable t) {
            Helper.setError("Check For Multi Failed - " +t);
            Helper.setPush("Private Account - Trying Image5");
            Helper.setError("Private Account - Trying Image");
            linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
            linkToDownload = "notification" + linkToDownload;

            downloadOrPass();
        }
    }

    void downloadMulti() {
        try {
            String JSONCheck = Jsoup.parse(elements.toString()).select("script[type=text/javascript]:not([src~=[a-zA-Z0-9./\\s]+)").first().html();
            JSONCheck = JSONCheck.replace("window._sharedData = ", "");

            JSONObject myjson = new JSONObject(JSONCheck).getJSONObject("entry_data");
            myjson = myjson.getJSONArray("PostPage").getJSONObject(0).getJSONObject("graphql").getJSONObject("shortcode_media");
            JSONArray jsonArray = myjson.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");

            String Authors = "";
            String URLs = "";
            String IDs = "";
            String fileNames = "";
            String fileLocations = "";
            String fileTypes = "";
            String notifTitles = "";
            String fullName;

            try {
                fullName = myjson.getJSONObject("owner").getString("full_name");

                if (fullName.isEmpty()) {
                    fullName = myjson.getJSONObject("owner").getString("username");
                }
            } catch (Throwable t) {
                setError("Info: " +t);
                fullName = myjson.getJSONObject("owner").getString("username");
            }

            for (int i=0;i < jsonArray.length();i++) {
                if (!Authors.equals("")) {
                    Authors = Authors + ";" + myjson.getJSONObject("owner").getString("username");
                } else {
                    Authors = myjson.getJSONObject("owner").getString("username");
                }

                if (!URLs.equals("")) {
                    try {
                        URLs = URLs + ";" + jsonArray.getJSONObject(i).getJSONObject("node").getString("video_url");

                        if (!fileLocations.equals("")) {
                            fileLocations = fileLocations + ";" + Helper.getSaveLocation("Video");
                        } else {
                            fileLocations = Helper.getSaveLocation("Video");
                        }

                        if (!notifTitles.equals("")) {
                            notifTitles = notifTitles + ";" + fullName + "'s Video";
                        } else {
                            notifTitles = fullName + "'s Video";
                        }

                        if (!fileTypes.equals("")) {
                            fileTypes = fileTypes + ";" +"Video";
                        } else {
                            fileTypes = "Video";
                        }
                    } catch (Throwable t) {
                        URLs = URLs + ";" + jsonArray.getJSONObject(i).getJSONObject("node").getString("display_url");

                        if (!fileLocations.equals("")) {
                            fileLocations = fileLocations + ";" + Helper.getSaveLocation("Image");
                        } else {
                            fileLocations = Helper.getSaveLocation("Image");
                        }

                        if (!notifTitles.equals("")) {
                            notifTitles = notifTitles + ";" + fullName + "'s Photo";
                        } else {
                            notifTitles = fullName + "'s Photo";
                        }

                        if (!fileTypes.equals("")) {
                            fileTypes = fileTypes + ";" +"Image";
                        } else {
                            fileTypes = "Image";
                        }
                    }
                } else {
                    try {
                        URLs = jsonArray.getJSONObject(i).getJSONObject("node").getString("video_url");

                        if (!fileLocations.equals("")) {
                            fileLocations = fileLocations + ";" + Helper.getSaveLocation("Video");
                        } else {
                            fileLocations = Helper.getSaveLocation("Video");
                        }

                        if (!notifTitles.equals("")) {
                            notifTitles = notifTitles + ";" + fullName + "'s Video";
                        } else {
                            notifTitles = fullName + "'s Video";
                        }

                        if (!fileTypes.equals("")) {
                            fileTypes = fileTypes + ";" +"Video";
                        } else {
                            fileTypes = "Video";
                        }
                    } catch (Throwable t) {
                        URLs = jsonArray.getJSONObject(i).getJSONObject("node").getString("display_url");

                        if (!fileLocations.equals("")) {
                            fileLocations = fileLocations + ";" + Helper.getSaveLocation("Image");
                        } else {
                            fileLocations = Helper.getSaveLocation("Image");
                        }

                        if (!notifTitles.equals("")) {
                            notifTitles = notifTitles + ";" + fullName + "'s Photo";
                        } else {
                            notifTitles = fullName + "'s Photo";
                        }

                        if (!fileTypes.equals("")) {
                            fileTypes = fileTypes + ";" +"Image";
                        } else {
                            fileTypes = "Image";
                        }
                    }
                }

                if (!IDs.equals("")) {
                    IDs = IDs + ";" + jsonArray.getJSONObject(i).getJSONObject("node").getString("id");
                } else {
                    IDs = jsonArray.getJSONObject(i).getJSONObject("node").getString("id");
                }

                if (!fileNames.equals("")) {
                    String fileFormat = "";
                    String mediaId = jsonArray.getJSONObject(i).getJSONObject("node").getString("id");
                    String userId = myjson.getJSONObject("owner").getString("id");
                    String date = Helper.getDate(System.currentTimeMillis() / 1000, getApplicationContext());
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

                    fileNames = fileNames + ";" + fileFormat;
                } else {
                    String fileFormat = "";
                    String mediaId = jsonArray.getJSONObject(i).getJSONObject("node").getString("id");
                    String userId = myjson.getJSONObject("owner").getString("id");
                    String date = Helper.getDate(System.currentTimeMillis() / 1000, getApplicationContext());
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

                    fileNames = fileFormat;
                }
            }

            Intent downloadIntent = new Intent();
            downloadIntent.setPackage("com.ihelp101.instagram");
            downloadIntent.setAction("com.ihelp101.instagram.PASS");
            downloadIntent.putExtra("URL", URLs);
            downloadIntent.putExtra("SAVE", fileLocations);
            downloadIntent.putExtra("Notification", notifTitles);
            downloadIntent.putExtra("Filename", fileNames);
            downloadIntent.putExtra("Filetype", fileTypes);
            downloadIntent.putExtra("User", Authors);
            mContext.startService(downloadIntent);

            stopSelf();
        } catch (Throwable t) {
            Helper.setError("Get Multi Post Failed - " +t);
            Helper.setPush("Private Account - Trying Image1");
            linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
            linkToDownload = "notification" + linkToDownload;

            skip = 1;
            downloadOrPass();
        }
    }

    void downloadOrPass() {
        SAVE = Helper.getSaveLocation(fileType);

        if (!SAVE.toLowerCase().contains("com.android.externalstorage.documents")) {
            if (Helper.getSettings("URLFileName")) {
                int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
                fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
            }

            SAVE = Helper.getSaveLocation(fileType);
            SAVE = Helper.checkSave(SAVE, userName, fileName);

            new Download().execute();
        } else if (serviceIntent.getStringExtra("URL").contains(";") && skip == 0) {
            linkToDownload = serviceIntent.getStringExtra("URL");
            userName = serviceIntent.getStringExtra("User");
            notificationTitle = serviceIntent.getStringExtra("Notification");
            fileName = serviceIntent.getStringExtra("Filename");
            fileType = serviceIntent.getStringExtra("Filetype");
            SAVE = serviceIntent.getStringExtra("SAVE");

            if (Helper.getSettings("URLFileName")) {
                int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
                fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
            }

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
        } else {
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
        }
    }

    void getPostUrl() {
        postCode = "";
        try {
            Thread checkMedia = new Thread() {
                public void run() {
                    try {
                        URL u = new URL("https://www.instagram.com/" + userName + "/?__a=1");
                        URLConnection c = u.openConnection();
                        c.connect();

                        String JSONInfo = Helper.convertStreamToString(u.openStream());

                        JSONObject jsonObject = new JSONObject(JSONInfo);

                        postCode = "https://www.instagram.com/p/" + jsonObject.getJSONObject("user").getJSONObject("media").getJSONArray("nodes").getJSONObject(0).getString("code");
                    } catch (Exception e) {
                        Helper.setError("Getting Post URL Failed - " +e);
                        Helper.setPush("Private Account - Trying Image2");
                        linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
                        linkToDownload = "notification" + linkToDownload;
                    }
                }
            };
            checkMedia.start();
            checkMedia.join();

            checkForMulti(postCode);
        } catch (Throwable t) {
            Helper.setError("Get Post URL Failed - " +t);
            Helper.setPush("Private Account - Trying Image3");
            linkToDownload = serviceIntent.getStringExtra("URL").replaceAll("media123;", "");
            linkToDownload = "notification" + linkToDownload;

            skip = 1;
            downloadOrPass();
        }
    }

    void setError(String status) {
        Helper.setError(status);
    }

    void Toast(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
