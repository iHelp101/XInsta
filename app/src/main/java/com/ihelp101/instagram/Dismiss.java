package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Dismiss extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Helper.setSetting("Dismiss", "" +System.currentTimeMillis());
    }
}
