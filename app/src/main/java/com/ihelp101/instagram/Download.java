package com.ihelp101.instagram;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Download extends IntentService {

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;
    long epoch;
    Intent serviceIntent;

    boolean hasSDPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            ContentResolver contentResolver = getContentResolver();
            List list = contentResolver.getPersistedUriPermissions();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toString().contains("com.android.externalstorage.documents")) {
                    return true;
                }
            }
        }
        return false;
    }

    void checkSDCard() {
        try {
            SAVE = Helper.getSaveLocation(fileType).split(";")[1];
            SAVE = Helper.checkSave(SAVE, userName, fileName);

            checkSDPermission();
        } catch (Exception e) {
            Helper.setError("SD Setup Failed: " +e);
            Helper.setError("Save:  " + Helper.getSaveLocation(fileType));
        }
    }

    void checkSDPermission() {
        if (hasSDPermission()) {
            if (serviceIntent.getStringExtra("URL").contains(";")) {
                String links = serviceIntent.getStringExtra("URL");
                String userNames = serviceIntent.getStringExtra("User");
                String notificationTitles = serviceIntent.getStringExtra("Notification");
                String fileNames = serviceIntent.getStringExtra("Filename");
                String fileTypes = serviceIntent.getStringExtra("Filetype");

                for (String link : links.split(";")) {
                    String fileName = fileNames.split(";")[0];
                    String fileType = fileTypes.split(";")[0];
                    String userName = userNames.split(";")[0];
                    String notificationTitle = notificationTitles.split(";")[0];

                    Helper.downloadOrPass(link, fileName, fileType, userName, notificationTitle, epoch, getApplicationContext(), true);

                    fileNames = fileNames.replace(fileName + ";", "");
                    fileTypes = fileTypes.replace(fileType + ";", "");
                    userNames = userNames.replace(userName + ";", "");
                    notificationTitles = notificationTitles.replace(notificationTitle + ";", "");
                }
            } else {
                Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, epoch, getApplicationContext(), true);
            }
        } else {
            Intent myIntent = new Intent(Download.this, SD.class);
            myIntent.putExtra("URL", linkToDownload);
            myIntent.putExtra("Filename", fileName);
            myIntent.putExtra("Filetype", fileType);
            myIntent.putExtra("Notification", notificationTitle);
            myIntent.putExtra("User", userName);
            myIntent.putExtra("SAVE", SAVE);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        }
    }

    public Download() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Helper.setError("SD Request Received");
        serviceIntent = intent;

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
                Intent myIntent = new Intent(Download.this, Permission.class);
                myIntent.putExtra("URL", linkToDownload);
                myIntent.putExtra("Filename", fileName);
                myIntent.putExtra("Filetype", fileType);
                myIntent.putExtra("Notification", notificationTitle);
                myIntent.putExtra("User", userName);
                myIntent.putExtra("SAVE", SAVE);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            } else {
                downloadOrPass();
            }
        } else {
            downloadOrPass();
        }
    }

    void downloadOrPass() {
        SAVE = Helper.getSaveLocation(fileType);

        if (!SAVE.toLowerCase().contains("com.android.externalstorage.documents")) {
            Intent downloadIntent = new Intent();
            downloadIntent.setPackage("com.ihelp101.instagram");
            downloadIntent.setAction("com.ihelp101.instagram.PASS_DOWNLOAD");
            downloadIntent.putExtra("URL", linkToDownload);
            downloadIntent.putExtra("SAVE", SAVE);
            downloadIntent.putExtra("Notification", notificationTitle);
            downloadIntent.putExtra("Filename", fileName);
            downloadIntent.putExtra("Filetype", fileType);
            downloadIntent.putExtra("User", userName);
            downloadIntent.putExtra("Epoch", epoch);
            getApplicationContext().startService(downloadIntent);
        } else {
            checkSDCard();
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