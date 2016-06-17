package com.ihelp101.instagram;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Downloader extends Service {
    Context mContext;

    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                URL u = new URL("https://www.instagram.com/" + uri[0] + "/media/");
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();
                String JSONInfo = Helper.convertStreamToString(inputStream);

                JSONObject jsonObject = new JSONObject(JSONInfo);

                String descriptionType = jsonObject.getJSONArray("items").getJSONObject(0).getString("type");
                String userName = uri[0];

                if (descriptionType.equals("video")) {
                    descriptionType = mContext.getResources().getString(R.string.video);
                    String fileExtension = ".mp4";
                    String fileName = userName + "_" + jsonObject.getJSONArray("items").getJSONObject(0).getString("id") + fileExtension;
                    String fileType = "Video";

                    String linkToDownload = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
                    linkToDownload = linkToDownload.replace("750x750", "");
                    linkToDownload = linkToDownload.replace("640x640", "");
                    linkToDownload = linkToDownload.replace("480x480", "");
                    linkToDownload = linkToDownload.replace("320x320", "");

                    String notificationTitle = mContext.getResources().getString(R.string.username_thing, userName, descriptionType);
                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                    Intent downloadIntent = new Intent();
                    downloadIntent.setPackage("com.ihelp101.instagram");
                    downloadIntent.setAction("com.ihelp101.instagram.PASS");
                    downloadIntent.putExtra("URL", linkToDownload);
                    downloadIntent.putExtra("SAVE", mContext.getResources().getString(R.string.Video));
                    downloadIntent.putExtra("Notification", notificationTitle);
                    downloadIntent.putExtra("Filename", fileName);
                    downloadIntent.putExtra("Filetype", fileType);
                    downloadIntent.putExtra("User", userName);
                    mContext.startService(downloadIntent);
                    stopSelf();
                }
            } catch (Exception e) {
                Error.setError("Video Fetch Failed: " + e);
                stopSelf();
            }

            return responseString;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();

        if (intent.getStringExtra("URL").equals("Fetch")) {
            new RequestTask().execute(intent.getStringExtra("User"));
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
        return super.onStartCommand(intent, flags, startId);
    }
}
