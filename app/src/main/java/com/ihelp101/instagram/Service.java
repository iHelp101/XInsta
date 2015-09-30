package com.ihelp101.instagram;

import android.content.Intent;
import android.os.IBinder;


public class Service extends android.app.Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Info: Started!");
    }
}
