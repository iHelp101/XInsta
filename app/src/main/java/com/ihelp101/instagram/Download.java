package com.ihelp101.instagram;

import android.Manifest;
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

    boolean sdCheck = false;
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
                System.out.println("kms: " +list.get(i));
                if (list.get(i).toString().contains("com.android.externalstorage.documents")) {
                    return true;
                }
            }
        }
        return false;
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
                OutputStream output;

                if (sdCheck) {
                    output = getContentResolver().openOutputStream(getDocumentFile(new File(SAVE), false).getUri());
                } else {
                    output = new FileOutputStream(SAVE);
                }

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
            } catch (Exception e) {
                Helper.setError("Download Error: " + e);
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
            if (SAVE.contains("com.android.externalstorage.documents")) {
                uriLocation = Uri.parse(Helper.getSaveLocation(fileType).split(";")[0]);
                SAVE = Helper.getSaveLocation(fileType).split(";")[1];
                sdCheck = true;

                if (fileType.equals("Profile")) {
                    SAVE = checkSaveProfile();
                } else {
                    SAVE = checkSave();
                }
                checkSDPermission();
            } else {
                if (fileType.equals("Profile")) {
                    SAVE = checkSaveProfile();
                } else {
                    SAVE = checkSave();
                }
                new RequestTask().execute();
            }
        } catch (Exception e) {
            Helper.setError("Check SD Failed: " +e);
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
                checkSDCard();
            }
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