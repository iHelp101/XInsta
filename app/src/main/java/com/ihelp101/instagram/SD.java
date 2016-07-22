package com.ihelp101.instagram;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class SD extends Activity {

    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        fileType = intent.getStringExtra("Filetype");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = intent.getStringExtra("SAVE");

        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 12);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            Helper.setSetting(fileType, data.getDataString() + ";" + Helper.getSaveLocation(fileType).split(";")[1]);
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

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
            }
        }
    }
}
