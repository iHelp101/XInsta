package com.ihelp101.instagram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class Permission extends Activity {

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(Permission.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Intent intent = getIntent();

        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        fileType = intent.getStringExtra("Filetype");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = intent.getStringExtra("SAVE");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent downloadIntent = new Intent();
                    downloadIntent.setPackage("com.ihelp101.instagram");
                    downloadIntent.setAction("com.ihelp101.instagram.DOWNLOAD");
                    downloadIntent.putExtra("URL", linkToDownload);
                    downloadIntent.putExtra("Filename", fileName);
                    downloadIntent.putExtra("Filetype", fileType);
                    downloadIntent.putExtra("Notification", notificationTitle);
                    downloadIntent.putExtra("User", userName);
                    downloadIntent.putExtra("SAVE", SAVE);
                    startService(downloadIntent);
                    finish();
                } else {
                    setToast("Permission denied. Unable to save image/video.");
                }
            }
        }
    }

    public void setToast(String message) {
        Toast toast = Toast.makeText(Permission.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
