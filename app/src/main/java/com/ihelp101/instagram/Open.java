package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class Open extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String fileName = intent.getStringExtra("Name");
        fileName = fileName.replace("%20", "");
        Intent Image = new Intent();
        Image.setAction(Intent.ACTION_VIEW);
        File file = new File(fileName);
        if (fileName.contains("jpg")) {
            Image.setDataAndType(Uri.fromFile(file), "image/*");
        } else {
            Image.setDataAndType(Uri.fromFile(file), "video/*");
        }
        Image.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Image);
    }
}
