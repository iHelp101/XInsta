package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

public class Click extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String save = intent.getStringExtra("File");

        File file = new File(save);
        Intent intentGallery = new Intent();
        intentGallery.setAction(Intent.ACTION_VIEW);
        intentGallery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri contentUri = FileProvider.getUriForFile(context, "com.ihelp101.instagram.fileprovider", file);
        if (save.contains("jpg")) {
            intentGallery.setDataAndType(contentUri, "image/*");
        } else {
            intentGallery.setDataAndType(contentUri, "video/*");
        }
        context.startActivity(intentGallery);
    }
}
