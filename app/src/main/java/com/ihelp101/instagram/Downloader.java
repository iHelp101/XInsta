package com.ihelp101.instagram;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;


public class Downloader extends BroadcastReceiver {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = intent.getStringExtra("URL");
        String filename = intent.getStringExtra("Name");
        String User = intent.getStringExtra("User");
        String Description = intent.getStringExtra("Description");
        String Location = intent.getStringExtra("Location");
        Location = Location.replace("file://", "");

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(""+User+"'s " +Description)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("Downloading.....");

        new DownloadFileAsync().execute(url, Location, filename, User, Description, context);

    }

    private class DownloadFileAsync extends AsyncTask<Object, String, String> {

        Context mContext;

        String User = null;
        String Desc = null;
        String Location = null;
        String fileName = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Random r = new Random();
            int i1 = r.nextInt(80000000 - 65) + 65;
            id = i1;

            // Displays the progress bar for the first time.
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected String doInBackground(Object... aurl) {
            int count;
            Location = aurl[1] + "/" + aurl[2];
            Location = Location.replace("%20", " ");
            fileName = (String) aurl [2];
            User = (String) aurl[3];
            Desc = (String) aurl[4];
            mContext = (Context) aurl[5];
            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Location);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                System.out.println("Eror: " +e);
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
            mNotifyManager.notify(id, mBuilder.build());
        }

        @Override
        protected void onPostExecute(String unused) {
            mBuilder.setContentTitle(""+User+"'s "+Desc);
            mBuilder.setContentText("Download complete.");
            mBuilder.setProgress(0, 0, false);

            Intent notificationIntent = new Intent();
            notificationIntent.setAction("com.ihelp101.instagram.IMAGE");
            notificationIntent.putExtra("Name", Location);
            PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, 95, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mNotifyManager.notify(id, mBuilder.build());

            Intent serviceIntent = new Intent(mContext, SDCard.class);
            serviceIntent.putExtra("Location", Location);
            serviceIntent.putExtra("Type", Desc);
            mContext.startService(serviceIntent);
        }
    }
}
