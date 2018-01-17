package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;


public class Receiver extends BroadcastReceiver {

    String linkToDownload;
    String fileName;
    String notificationTitle;
    String userName;
    String SAVE;
    String fileType;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
            mContext = context;
            String notificationInfo = intent.getStringExtra("Notification");
            getNotification(notificationInfo);
    }

    public void getNotification(String notificationInfo) {
        try {
            JSONObject jsonObject = new JSONObject(notificationInfo);

            Helper.setPush("Push: " + jsonObject);

            String userHolder;

            if (jsonObject.getString("m").contains("]: ")) {
                userHolder = jsonObject.getString("m").split("]: ")[1];
            } else {
                userHolder = jsonObject.getString("m");
            }

            userName = userHolder.split(" ")[0];
            String photoName = "Photo";
            String photoCheck = userHolder.replace(userName, "").toLowerCase();
            String videoName = "Video";

            if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(photoName)) {
                String fileExtension = ".jpg";
                String fileDescription;
                try {
                    fileDescription = Helper.getResourceString(mContext, R.string.photo);
                } catch (Throwable t) {
                    fileDescription = "Photo";
                }
                fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;

                String itemId;

                if (!Helper.getSetting("File").equals("Instagram")) {
                    try {
                        String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), mContext);
                        itemId = jsonObject.getString("ig").replace("media?id=", "");

                        itemId = itemId.replace(itemId.split("_")[1], "") + itemToString;

                        fileName = userName + "_" + itemId + fileExtension;
                    } catch (Throwable t) {
                        Helper.setError("Auto Epoch Failed - " + t);
                    }
                } else {

                }

                fileType = "Image";
                linkToDownload = "notification" + jsonObject.getString("i");

                try {
                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                } catch (Throwable t) {
                    notificationTitle = userName + "'s " + fileDescription;
                }
                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                SAVE = Helper.getSaveLocation(fileType);

                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
            } else if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(videoName)) {
                Helper.setPush("Pushed: " + userName);

                linkToDownload = "https://www.instagram.com/" + userName + "/?__a=1";
                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
            } else if (jsonObject.getString("collapse_key").equals("post") || jsonObject.getString("collapse_key").equals("resurrected_user_post")) {
                String fileExtension = ".jpg";
                String fileDescription;
                try {
                    fileDescription = Helper.getResourceString(mContext, R.string.photo);
                } catch (Throwable t) {
                    fileDescription = "Photo";
                }
                fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;

                if (!Helper.getSetting("File").equals("Instagram")) {
                    try {
                        String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), mContext);
                        String itemId = jsonObject.getString("ig").replace("media?id=", "");

                        itemId = itemId.replace(itemId.split("_")[1], "") + itemToString;

                        fileName = userName + "_" + itemId + fileExtension;
                    } catch (Throwable t) {
                        Helper.setError("Auto Epoch Failed - " + t);
                    }
                }

                fileType = "Image";
                linkToDownload = jsonObject.getString("i");

                try {
                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                } catch (Throwable t) {
                    notificationTitle = userName + "'s " + fileDescription;
                }

                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                SAVE = Helper.getSaveLocation(fileType);

                linkToDownload = "media123;" + linkToDownload;

                Helper.setPush("Pushed Post : " + userName);

                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, mContext);
            } else {
                Helper.setPush("This is not a post.");
            }
        } catch (Throwable t) {
            Helper.setError("Simulated Notification Error - " +t);
        }
    }
}