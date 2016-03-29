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
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
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
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;
    String skipDownload = "No";
    Uri uriLocation;

    int count = 0;
    int id = 1;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;

    void checkSDCard() {
        if (SAVE.contains("com.android.externalstorage.documents") && android.os.Build.VERSION.SDK_INT >= 19) {
            uriLocation = Uri.parse(Helper.getSaveLocation(fileType).split(";")[0]);
            SAVE = Helper.getSaveLocation(fileType).split(";")[1];

            if (fileType.equals("Profile")) {
                SAVE = checkSaveProfile();
            } else {
                SAVE = checkSave();
            }
            new DownloadFileSD().execute();
        } else  {
            if (fileType.equals("Profile")) {
                SAVE = checkSaveProfile();
            } else {
                SAVE = checkSave();
            }
            new RequestTask().execute();
        }
    }

    public Download() {
        super("Download");
    }

    class DownloadFileSD extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... aurl) {
            int count;
            try {
                String fileExtension;
                if (SAVE.contains("jpg")) {
                    fileExtension = "image/*";
                } else {
                    fileExtension = "video/*";
                }

                if (!Helper.getSettings("Notification")) {
                    mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentText(getResources().getString(R.string.DownloadDots));
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL(linkToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                DocumentFile pickedDir = DocumentFile.fromTreeUri(getApplicationContext(), uriLocation);
                String[] dirNameSplit = SAVE.split(pickedDir.getName());

                String saveLocation = dirNameSplit[0] + pickedDir.getName();

                DocumentFile newFile = pickedDir;
                if (dirNameSplit.length > 1) {
                    String[] folder = dirNameSplit[1].substring(1).split("/");
                    if (folder.length > 1) {
                        for(int i = 0; i < (folder.length - 1); i++) {
                            saveLocation = saveLocation + "/" + folder[i];
                            File file = new File(saveLocation);
                            if (file.exists()) {
                                newFile = newFile.findFile(folder[i].replace("/", ""));
                            } else {
                                newFile = newFile.createDirectory(userName);
                            }
                        }

                        File file = new File(SAVE);
                        if (file.exists()) {
                            skipDownload = "Yes";
                        } else {
                            newFile = newFile.createFile(fileExtension, fileName);
                        }
                    } else {
                        if (newFile.exists()) {
                            File file = new File(SAVE);
                            if (file.exists()) {
                                skipDownload = "Yes";
                            } else {
                                newFile = newFile.findFile(dirNameSplit[1].replace("/", "")).createFile(fileExtension, fileName);
                            }
                        } else {
                            newFile = newFile.createDirectory(dirNameSplit[1].replace("/", ""));
                            File file = new File(SAVE);
                            if (file.exists()) {
                                skipDownload = "Yes";
                            } else {
                                newFile.createFile(fileExtension, fileName);
                            }
                        }
                    }
                } else {
                    File file = new File(SAVE);
                    if (file.exists()) {
                        skipDownload = "Yes";
                    } else  {
                        newFile = newFile.createFile(fileExtension, fileName);
                    }
                }

                if (skipDownload.equals("No")) {
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = getContentResolver().openOutputStream(newFile.getUri());

                    byte data[] = new byte[1024];

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                }

                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentTitle(fileName);
                    mBuilder.setContentText(ResourceHelper.getString(getApplicationContext(), R.string.Download_Completed));
                    mBuilder.setTicker(ResourceHelper.getString(getApplicationContext(), R.string.Download_Completed));
                    mBuilder.setSmallIcon(R.drawable.ic_launcher);
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                    mBuilder.setAutoCancel(true);

                    Intent notificationIntent = new Intent();
                    notificationIntent.setAction(Intent.ACTION_VIEW);

                    File file = new File(SAVE);
                    if (fileName.contains("jpg")) {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "image/*");
                    } else {
                        notificationIntent.setDataAndType(Uri.fromFile(file), "video/*");
                    }
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

                Toast(getResources().getString(R.string.Download_Completed));
            } catch (Throwable t) {
                setError("SD Card Failed: " +t);
                if (!Helper.getSettings("Notification")) {
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle(fileName);
                    mBuilder.setContentText(ResourceHelper.getString(getApplicationContext(), R.string.Download_Failed));
                    mBuilder.setTicker(ResourceHelper.getString(getApplicationContext(), R.string.Download_Failed));
                    mBuilder.setSmallIcon(R.drawable.ic_launcher);
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                    mNotifyManager.notify(id, mBuilder.build());
                }
            }
            return null;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        fileType = intent.getStringExtra("Filetype");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = Helper.getSaveLocation(fileType);

        getDirectory = Environment.getExternalStorageDirectory().toString();

        checkPermission();
    }

    public void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent myIntent = new Intent(Download.this, Permission.class);
                myIntent.putExtra("URL", linkToDownload);
                myIntent.putExtra("Filename", fileName);
                myIntent.putExtra("Filetype", fileType);
                myIntent.putExtra("Notification", notificationTitle);
                myIntent.putExtra("User", userName);
                myIntent.putExtra("SAVE", SAVE);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
            } else {
                checkSDCard();
            }
        } else {
            checkSDCard();
        }
    }

    public String checkSave() {
        String saveLocation = SAVE;
        if (SAVE.equals("Instagram")) {
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
            if (Helper.getSettings("Folder")) {
                saveLocation = saveLocation + userName + "/";
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } else {
            if (Helper.getSettings("Folder")) {
                saveLocation = saveLocation + userName + "/";
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        saveLocation = saveLocation.replace("%20", " ");

        return saveLocation + fileName;
    }

    public String checkSaveProfile() {
        String saveLocation = "";
        if (!SAVE.equals("Instagram")) {
            if (Helper.getSettings("Folder")) {
                saveLocation = Helper.getSetting("Profile");
                saveLocation = saveLocation + userName + "/";
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } else {
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
            if (Helper.getSettings("Folder")) {
                saveLocation = saveLocation + userName + "/";
            }
            File directory = new File(URI.create(saveLocation).getPath());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        return saveLocation + fileName;
    }

    public void setError(String status) {
        try {
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

    private void Toast (final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        });
    }

    class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {

                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (!Helper.getSettings("Notification")) {
                    mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentText(getResources().getString(R.string.DownloadDots));
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent","Instagram");
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(SAVE);

                byte data[] = new byte[1024];

                while ((count = input.read(data)) >= 0) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentText(getResources().getString(R.string.Download_Completed)).setTicker(getResources().getString(R.string.Download_Completed));

                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setAutoCancel(true);

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

                Toast(getResources().getString(R.string.Download_Completed));
            } catch (Throwable t) {
                setError("Download Error: " + t);
                setError("Instagram URL: " +linkToDownload);
                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentText(getResources().getString(R.string.Download_Failed)).setTicker(getResources().getString(R.string.Download_Failed));
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setAutoCancel(true);
                    mNotifyManager.notify(id, mBuilder.build());
                }
                Toast(getResources().getString(R.string.Download_Failed));
            }

            return responseString;
        }
    }
}