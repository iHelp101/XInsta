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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();

        Helper.setError("Got SD Card Pass");

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
        return super.onStartCommand(intent, flags, startId);
    }
}
