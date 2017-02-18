package com.ihelp101.instagram;

import android.Manifest;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Download extends IntentService {

    String getDirectory;
    String linkToDownload;
    String fileName;
    String fileType;
    String notificationTitle;
    String userName;
    String SAVE;
    Uri uriLocation;

    int count = 0;
    int id = 1;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;

    boolean isPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            ContentResolver contentResolver = getContentResolver();
            List list = contentResolver.getPersistedUriPermissions();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toString().contains("com.android.externalstorage.documents")) {
                    return true;
                }
            }
        }
        return false;
    }

    class RequestTask extends AsyncTask<String, String, String> {

        int downloadFailed = 1;

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                Random r = new Random();
                id = r.nextInt(9999999 - 65) + 65;

                if (!Helper.getSettings("Notification")) {
                    String downloading;

                    try {
                        downloading = Helper.getResourceString(getApplicationContext(), R.string.DownloadDots);
                    } catch (Throwable t) {
                        downloading = "Downloading...";
                    }

                    mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentText(downloading);
                    mNotifyManager.notify(id, mBuilder.build());
                }

                URL url = new URL (linkToDownload);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent","Instagram");
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output;

                output = getContentResolver().openOutputStream(getDocumentFile(new File(SAVE), false).getUri());

                byte data[] = new byte[1024];

                while ((count = input.read(data)) >= 0) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                if (!Helper.getSettings("Notification")) {
                    String downloadComplete;

                    try {
                        downloadComplete = Helper.getResourceString(getApplicationContext(), R.string.Download_Completed);
                    } catch (Throwable t) {
                        downloadComplete = "Download Complete";
                    }

                    mBuilder.setContentText(downloadComplete).setTicker(downloadComplete);
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
            } catch (Exception e) {
                downloadFailed = 2;
                Helper.setError("Download Error: " + e);
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (downloadFailed == 1) {

                String downloadComplete;

                try {
                    downloadComplete = Helper.getResourceString(getApplicationContext(), R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                Toast(downloadComplete);
            } else {
                String downloadFailed;

                try {
                    downloadFailed = Helper.getResourceString(getApplicationContext(), R.string.Download_Failed);
                } catch (Throwable t2) {
                    downloadFailed = "Download Failed";
                }

                Toast(downloadFailed);

                if (!Helper.getSettings("Notification")) {
                    mBuilder.setContentText(downloadFailed).setTicker(downloadFailed);
                    mBuilder.setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setAutoCancel(true);
                    mNotifyManager.notify(id, mBuilder.build());
                }
            }
        }
    }

    DocumentFile getDocumentFile(File file, boolean isDirectory) {
        String baseFolder = getExtSdCardFolder(file);

        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        }
        catch (IOException e) {
            return null;
        }

        String fileExtension;
        if (SAVE.contains("jpg")) {
            fileExtension = "image/*";
        } else {
            fileExtension = "video/*";
        }

        DocumentFile document = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(Helper.getSaveLocation(fileType).split(";")[0]));

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                }
                else {
                    nextDocument = document.createFile(fileExtension, parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    String checkSave() {
        String saveLocation = SAVE;

        try {
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
        } catch (Exception e) {
            Helper.setError("Save Location Check Failed: " + e);
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
        }

        return (saveLocation + fileName).replace("%20", " ");
    }

    String checkSaveProfile() {
        String saveLocation = SAVE;

        try {
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
        }  catch (Exception e) {
            Helper.setError("Profile Save Location Check Failed: " +e);
            saveLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram/";
        }
        return (saveLocation + fileName).replace("%20", " ");
    }

    String getExtSdCardFolder(final File file) {
        String[] extSdPaths = getExtSdCardPaths();
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().contains(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    String[] getExtSdCardPaths() {
        List<String> paths = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            for (File file : getApplicationContext().getExternalFilesDirs("external")) {
                if (file != null && !file.equals(getApplicationContext().getExternalFilesDir("external"))) {
                    int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                    if (index > 0) {
                        String path = file.getAbsolutePath().substring(0, index);
                        try {
                            path = new File(path).getCanonicalPath();
                        } catch (IOException e) {
                        }
                        paths.add(path);
                    }
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

    void checkSDCard() {
        try {
            uriLocation = Uri.parse(Helper.getSaveLocation(fileType).split(";")[0]);
            SAVE = Helper.getSaveLocation(fileType).split(";")[1];

            if (fileType.equals("Profile")) {
                SAVE = checkSaveProfile();
            } else {
                SAVE = checkSave();
            }
            checkSDPermission();
        } catch (Exception e) {
            Helper.setError("SD Setup Failed: " +e);
            Helper.setError("Save:  " + Helper.getSaveLocation(fileType));
        }
    }

    void checkSDPermission() {
        if (isPermission()) {
            new RequestTask().execute();
        } else {
            Intent myIntent = new Intent(Download.this, SD.class);
            myIntent.putExtra("URL", linkToDownload);
            myIntent.putExtra("Filename", fileName);
            myIntent.putExtra("Filetype", fileType);
            myIntent.putExtra("Notification", notificationTitle);
            myIntent.putExtra("User", userName);
            myIntent.putExtra("SAVE", SAVE);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        }
    }

    public Download() {
        super("Download");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Helper.setError("SD Request Received");

        linkToDownload = intent.getStringExtra("URL");
        fileName = intent.getStringExtra("Filename");
        fileType = intent.getStringExtra("Filetype");
        notificationTitle = intent.getStringExtra("Notification");
        userName = intent.getStringExtra("User");
        SAVE = Helper.getSaveLocation(fileType);
        getDirectory = Environment.getExternalStorageDirectory().toString();

        checkPermission();
    }

    void checkPermission() {
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
                downloadOrPass();
            }
        } else {
            downloadOrPass();
        }
    }

    void downloadOrPass() {
        SAVE = Helper.getSaveLocation(fileType);

        if (!SAVE.toLowerCase().contains("com.android.externalstorage.documents")) {
            Intent downloadIntent = new Intent();
            downloadIntent.setPackage("com.ihelp101.instagram");
            downloadIntent.setAction("com.ihelp101.instagram.PASS_DOWNLOAD");
            downloadIntent.putExtra("URL", linkToDownload);
            downloadIntent.putExtra("SAVE", SAVE);
            downloadIntent.putExtra("Notification", notificationTitle);
            downloadIntent.putExtra("Filename", fileName);
            downloadIntent.putExtra("Filetype", fileType);
            downloadIntent.putExtra("User", userName);
            getApplicationContext().startService(downloadIntent);
        } else {
            checkSDCard();
        }
    }

    void Toast (final String message) {
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

}