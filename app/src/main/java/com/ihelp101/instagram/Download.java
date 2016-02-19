package com.ihelp101.instagram;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Download extends IntentService {

    String getDirectory;
    String linkToDownload;
    String fileName;
    String notificationTitle;
    String userName;
    String Failed = "No";
    String SAVE;

    int count;
    int id = 1;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;

    public Download() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = intent.getStringExtra("SAVE");

        getDirectory = Environment.getExternalStorageDirectory().toString();

        checkPermission();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Intent myIntent = new Intent(Download.this, Permission.class);
            myIntent.putExtra("URL", linkToDownload);
            myIntent.putExtra("SAVE", SAVE);
            myIntent.putExtra("Notification", notificationTitle);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        } else {
            new RequestTask().execute();
        }
    }

    public String checkSave(String saveLocation) {
        if (saveLocation.equals("Instagram")) {
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
            if (Helper.getFolder().equals("Yes")) {
                saveLocation = saveLocation + "/" + userName;
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } else {
            if (Helper.getFolder().equals("Yes")) {
                saveLocation = saveLocation + "/" + userName;
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        return saveLocation;
    }

    public String checkSaveProfile(String saveLocation) {
        if (saveLocation.equals("Instagram")) {
            if (!Helper.getImage().equals("Instagram")) {
                if (Helper.getFolder().equals("Yes")) {
                    saveLocation = Helper.getImage();
                    saveLocation = saveLocation + "/" + userName;
                }
                File directory = new File(URI.create(saveLocation).getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            } else {
                saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram";
                if (Helper.getFolder().equals("Yes")) {
                    saveLocation = saveLocation + "/" + userName;
                }
                File directory = new File(URI.create(saveLocation).getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
            }
        } else {
            if (Helper.getFolder().equals("Yes")) {
                saveLocation = saveLocation + "/" + userName;
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        return saveLocation;
    }

    public void setError(String status) {
        try {
            System.out.println("Error: " +status);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String time = sdf.format(new Date());

            status = time + " - " + status;

            File root = new File(getDirectory, ".Instagram");
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

    private void Toast (String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                Failed = "No";

                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (SAVE.equals("Image")) {
                    SAVE = checkSave(Helper.getImage());
                } else if (SAVE.equals("Video")) {
                    SAVE = checkSave(Helper.getVideo());
                } else if (SAVE.equals("Profile")) {
                    SAVE = checkSaveProfile(Helper.getProfile());
                }

                SAVE = SAVE + "/" + fileName;
                SAVE = SAVE.replace("%20", " ");
                SAVE = SAVE.replace("file://", "");

                System.out.println("Info: " +SAVE);

                if (!Helper.getNotification().equals("Hide")) {
                    mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setLargeIcon(BitmapFactory.decodeResource(ResourceHelper.getOwnResources(getApplicationContext()), R.drawable.ic_launcher))
                            .setContentText(ResourceHelper.getString(getApplicationContext(), R.string.DownloadDots));
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(SAVE);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Throwable t) {
                setError("Error: " + t);
                Failed = "Yes";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!Helper.getNotification().equals("Hide")) {
                if (Failed.equals("Yes")) {
                    mBuilder.setContentText(ResourceHelper.getString(getApplicationContext(), R.string.Download_Failed));
                    mBuilder.setTicker(ResourceHelper.getString(getApplicationContext(), R.string.Download_Failed));
                } else {
                    mBuilder.setContentText(ResourceHelper.getString(getApplicationContext(), R.string.Download_Completed));
                    mBuilder.setTicker(ResourceHelper.getString(getApplicationContext(), R.string.Download_Completed));
                }

                mBuilder.setContentTitle(notificationTitle);
                mBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(ResourceHelper.getOwnResources(getApplicationContext()), R.drawable.ic_launcher));
                mBuilder.setAutoCancel(true);

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);


                File file = new File(SAVE);
                if (SAVE.contains("jpg")) {
                    notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                } else {
                    notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                }
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(id, mBuilder.build());
            }

            MediaScannerConnection.scanFile(getApplicationContext(),
                    new String[]{SAVE}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null) {
                                int scan = 1;
                            }
                        }
                    });

            if (Failed.equals("Yes")) {
                Toast(ResourceHelper.getString(getApplicationContext(), R.string.Download_Failed));
            } else {
                Toast(ResourceHelper.getString(getApplicationContext(), R.string.Download_Completed));
            }
        }
    }
}
