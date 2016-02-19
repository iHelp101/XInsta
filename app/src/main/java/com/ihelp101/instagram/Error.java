package com.ihelp101.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Error extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String errorMessage = intent.getStringExtra("Error");
        setError(errorMessage);
    }

    public static void setError(String status) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());

            status = time + " - " + status;

            File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, "Error.txt");
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.newLine();
            buf.append(status);
            buf.close();
        } catch (IOException e) {

        }
    }
}
