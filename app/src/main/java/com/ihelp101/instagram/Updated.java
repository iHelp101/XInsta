package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Updated extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Helper.setSetting("Hooks", "123;abc");
        } catch (Throwable t) {
        }
    }
}
