package com.ihelp101.instagram;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class Download_Passed extends IntentService {

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;
    Long epoch;

    public Download_Passed() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Helper.setError("Passed To XInsta");

        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        fileType = intent.getStringExtra("Filetype");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = Helper.getSaveLocation(fileType);
        epoch = intent.getLongExtra("Epoch", 123);


        checkPermission();
    }

    void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent myIntent = new Intent(Download_Passed.this, Permission.class);
                myIntent.putExtra("URL", linkToDownload);
                myIntent.putExtra("Filename", fileName);
                myIntent.putExtra("Filetype", fileType);
                myIntent.putExtra("Notification", notificationTitle);
                myIntent.putExtra("User", userName);
                myIntent.putExtra("SAVE", SAVE);
                myIntent.putExtra("Epoch", epoch);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            } else {
                Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, epoch, getApplicationContext(), true);
            }
        } else {
            Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, epoch, getApplicationContext(), true);
        }
    }

    void Toast (final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
    }

}