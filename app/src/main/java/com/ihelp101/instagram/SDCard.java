package com.ihelp101.instagram;

import android.app.Service;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import java.io.File;

public class SDCard extends Service {
    String Location;
    String Type;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Location = intent.getStringExtra("Location");
        Location = Location.replace("%20", " ");

        Type = intent.getStringExtra("Type");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File imageFile = new File (Location);
            if (Type.equals("photo")) {
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getPath()}, new String[]{"image/jpg"}, null);
            } else {
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{imageFile.getPath()}, new String[]{"image/mp4"}, null);
            }
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Location)));
        }
        stopService(new Intent(com.ihelp101.instagram.SDCard.this, com.ihelp101.instagram.SDCard.class));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
