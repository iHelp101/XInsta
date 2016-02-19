package com.ihelp101.instagram;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class Permission extends Activity {

    String linkToDownload;
    String notificationTitle;
    String SAVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(Permission.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Intent intent = getIntent();

        linkToDownload = intent.getStringExtra("URL");
        SAVE = intent.getStringExtra("SAVE");
        notificationTitle = intent.getStringExtra("Notification");
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
                    downloadIntent.putExtra("SAVE", SAVE);
                    downloadIntent.putExtra("Notification", notificationTitle);
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
