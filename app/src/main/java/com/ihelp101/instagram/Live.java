package com.ihelp101.instagram;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class Live extends Service {

    static NotificationCompat.Builder mBuilder;
    static NotificationManager mNotifyManager;
    static int id = 100;
    static boolean ready = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setContentTitle("Muxing")
                .setContentText("Muxing...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher));


        startForeground(100, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotifyManager.notify(100, mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            try {
                if (intent.getAction().equals("StopService")) {
                    stopForeground(false);
                    stopSelf();

                    return super.onStartCommand(intent, flags, startId);
                }
            } catch (Throwable t) {
            }

            System.out.println("Handle");
            String linkToDownload = URLDecoder.decode(intent.getStringExtra("SAVE"), "utf-8");
            String userName = intent.getStringExtra("Username");
            String fileName = intent.getStringExtra("Filename");
            ready = false;

            Helper.setError("Passing Live To Downloader");
            new Helper.DownloadLive2(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, userName, fileName);
        } catch (Throwable t) {
            Helper.setError("Live Story Intent Failed - " +t);

            String downloadFailed = "Intent Failed";

            Toast(getApplicationContext(), downloadFailed);


            mBuilder.setContentText(downloadFailed)
                    .setTicker(downloadFailed)
                    .setContentTitle(downloadFailed)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher));
            mNotifyManager.notify(id, mBuilder.build());
        }
        return START_NOT_STICKY;
    }

    static void startMux(Context context, String audioFile, String videoFile, String notificationTitle) {
        try {
            Helper.setError("Started Mux");
            String out = Helper.getSaveLocation("Video");

            if (out.toLowerCase().contains("com.android.externalstorage.documents")) {
                out = out.split(";")[1];
            }

            String fileName = videoFile.split("/")[videoFile.split("/").length - 1];
            String userName = fileName.split("_")[0];

            Helper.setError("Pre-Save Location: " +out);

            out = Helper.checkSave(out, userName, fileName);

            Helper.setError("Mux Save Location: " +out);

            String outputFile = URLDecoder.decode(out.replace("LiveVideo", "Live"), "utf-8");

            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);

            notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

            mBuilder.setContentTitle(notificationTitle)
                    .setContentText("Muxing...")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            mNotifyManager.notify(id, mBuilder.build());

            if (mux(context, videoFile, audioFile, outputFile)) {
                new File(audioFile).delete();
                new File(videoFile).delete();

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(audioFile))));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoFile))));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(outputFile))));

                String downloadComplete;

                try {
                    downloadComplete = Helper.getResourceString(context, R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                mBuilder.setContentTitle(notificationTitle)
                        .setContentText(downloadComplete)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);

                File fileSave = new File(outputFile);

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileSave)));

                notificationIntent.setDataAndType(Uri.fromFile(fileSave), "video/*");
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(id, mBuilder.build());

                Toast(context, downloadComplete);
            } else {
                Helper.setError("Muxing Failed - Returned False");
                String downloadFailed = "Muxing Failed";

                failed(context, audioFile, videoFile, downloadFailed);
            }
        } catch (Throwable t) {
            Helper.setError("Live Story Muxing Failed - " +t);

            String downloadFailed = "Muxing Failed";

            failed(context, audioFile, videoFile, downloadFailed);
        }
    }

    static void startMux2(Context context, String audioFile, String videoFile, String notificationTitle) {
        try {
            Helper.setError("Started Mux");
            String outputFile = videoFile.replace("Video", "");

            Helper.setError("Mux Save Location: " +outputFile);

            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(context);

            notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

            mBuilder.setContentTitle(notificationTitle)
                    .setContentText("Muxing...")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
            mNotifyManager.notify(id, mBuilder.build());

            int rc = FFmpeg.execute("-i '"  + videoFile +  "' -i '" + audioFile +  "' -y -acodec copy -vcodec copy '" +outputFile + "'");

            if (rc == RETURN_CODE_SUCCESS) {
                Helper.setError("Completed Mux: " +outputFile);
                new File(audioFile).delete();
                new File(videoFile).delete();

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(audioFile))));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoFile))));
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(outputFile))));

                String downloadComplete;

                try {
                    downloadComplete = Helper.getResourceString(context, R.string.Download_Completed);
                } catch (Throwable t) {
                    downloadComplete = "Download Complete";
                }

                mBuilder.setContentTitle(notificationTitle)
                        .setContentText(downloadComplete)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));

                Intent notificationIntent = new Intent();
                notificationIntent.setAction(Intent.ACTION_VIEW);

                File fileSave = new File(outputFile);

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileSave)));

                notificationIntent.setDataAndType(Uri.fromFile(fileSave), "video/*");
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mNotifyManager.notify(id, mBuilder.build());

                Toast(context, downloadComplete);

                stopService(context, mBuilder);
            } else {
                Helper.setError("Muxing Failed - Returned False");
                Helper.setError("Muxing Failed Audio - " +audioFile);
                Helper.setError("Muxing Failed Video - " +videoFile);
                String downloadFailed = "Muxing Failed";

                failed(context, audioFile, videoFile, downloadFailed);
            }
        } catch (Throwable t) {
            Helper.setError("Live Story Muxing Failed - " +t);

            String downloadFailed = "Muxing Failed";

            failed(context, audioFile, videoFile, downloadFailed);
        }
    }

    static public boolean mux(Context context, String videoFile, String audioFile, String outputFile) {
        Movie video;
        try {
            video = new MovieCreator().build(videoFile);
        } catch (RuntimeException e) {
            Helper.setError("Live Story Muxing Failed2 - " +e);
            Helper.setError("Live Story Audio - " +audioFile);
            Helper.setError("Live Story Video - " +videoFile);
            return false;
        } catch (IOException e) {
            Helper.setError("Live Story Muxing Failed3 - " +e);
            return false;
        }

        Movie audio;
        try {
            audio = new MovieCreator().build(audioFile);
        } catch (IOException e) {
            Helper.setError("Live Story Muxing Failed4 - " +e);
            return false;
        } catch (NullPointerException e) {
            Helper.setError("Live Story Muxing Failed5 - " +e);
            return false;
        }

        Track audioTrack = audio.getTracks().get(0);
        video.addTrack(audioTrack);

        BufferedWritableFileByteChannel byteBufferByteChannel;

        Container out = new DefaultMp4Builder().build(video);
        OutputStream outputStream= null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);
        } catch (FileNotFoundException e) {
            try {
                outputStream = context.getContentResolver().openOutputStream(getDocumentFile(context, new File(outputFile), false, outputFile).getUri());

                byteBufferByteChannel = new BufferedWritableFileByteChannel(outputStream);
            } catch (Throwable t) {
                Helper.setError("Live Story Muxing Failed6 - " + e);
                return false;
            }
        }

        try {
            out.writeContainer(byteBufferByteChannel);
            byteBufferByteChannel.close();

            if (fos != null) {
                fos.close();
            } else {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (Throwable t) {
            Helper.setError("Live Story Muxing Failed7 - " +t);
            return false;
        }
        return true;
    }

    private static class BufferedWritableFileByteChannel implements WritableByteChannel {
        //    private static final int BUFFER_CAPACITY = 1000000;
        private static final int BUFFER_CAPACITY = 10000000;

        private boolean isOpen = true;
        private final OutputStream outputStream;
        private final ByteBuffer byteBuffer;
        private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

        private BufferedWritableFileByteChannel(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.byteBuffer = ByteBuffer.wrap(rawBuffer);
            Log.e("Audio Video", "13");
        }

        @Override
        public int write(ByteBuffer inputBuffer) throws IOException {
            int inputBytes = inputBuffer.remaining();

            if (inputBytes > byteBuffer.remaining()) {
                Log.e("Size ok ", "song size is ok");
                dumpToFile();
                byteBuffer.clear();

                if (inputBytes > byteBuffer.remaining()) {
                    Log.e("Size ok ", "song size is not okssss ok");
                    throw new BufferOverflowException();
                }
            }

            byteBuffer.put(inputBuffer);

            return inputBytes;
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public void close() throws IOException {
            dumpToFile();
            isOpen = false;
        }

        private void dumpToFile() {
            try {
                outputStream.write(rawBuffer, 0, byteBuffer.position());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static DocumentFile getDocumentFile(Context context, File file, boolean isDirectory, String SAVE) {
        String baseFolder = getExtSdCardFolder(context, file);

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

        DocumentFile document = DocumentFile.fromTreeUri(context, Uri.parse(Helper.getSaveLocation("Video").split(";")[0]));

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

    static String getExtSdCardFolder(Context context, final File file) {
        String[] extSdPaths = getExtSdCardPaths(context);
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

    static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            for (File file : context.getExternalFilesDirs("external")) {
                if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
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

    static void Toast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    static void failed(Context context, String audioFile, String videoFile, String downloadFailed) {
        File file = new File(audioFile);
        file.delete();
        file = new File(videoFile);
        file.delete();

        Toast(context, downloadFailed);

        mBuilder.setContentText(downloadFailed)
                .setTicker(downloadFailed)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        mNotifyManager.notify(id, mBuilder.build());

        Intent serviceToStop = new Intent(context, Live.class);
        context.stopService(serviceToStop);
    }

    static void stopService(Context context, NotificationCompat.Builder builder) {
        Intent serviceToStop = new Intent(context, Live.class);
        serviceToStop.setAction("StopService");
        context.startService(serviceToStop);
        mBuilder = builder;
    }
}
