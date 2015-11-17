package com.ihelp101.instagram;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Main extends ListActivity {

    private ListView mAdapter;
    private String version = "123";
    private static int FILE_CODE = 0;
    String SaveLocation = "None";
    String getDirectory = Environment.getExternalStorageDirectory().toString().replace("1", "0");

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                URL u = new URL(uri[0]);
                URLConnection c = u.openConnection();
                c.connect();
                InputStream in = c.getInputStream();
                final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                in.read(buffer);
                bo.write(buffer);

                responseString = bo.toString();

                try {
                    bo.close();
                } catch (Exception e) {
                    System.out.println("First: " +e);
                }
            } catch (Exception e) {
                System.out.println("Second: " +e);
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);
                if (p.packageName.equals("com.instagram.android")) {
                    version = Integer.toString(p.versionCode);
                    version = version.substring(0, version.length() - 2);
                }
            }

            File file = new File(getDirectory + "/.Instagram/Hooks.txt");

            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                br.close();
            } catch (IOException e) {

            }

            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Updated), Toast.LENGTH_LONG);

            String[] html = result.split("<p>");

            String matched = "No";

            int count = 0;
            int max = 0;
            for (String data : html) {
                max++;
            }

            for (String data : html) {
                count++;

                String finalCheck = "123";

                if (!data.isEmpty()) {
                    String[] PasteVersion = data.split(";");
                    finalCheck = PasteVersion[0];
                }

                if (version.equals(finalCheck) && !data.isEmpty()) {
                    data = data.replace("<p>", "");
                    data = data.replace("</p>", "");
                    if (data.trim().equals(text.toString().trim())) {
                        toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Latest), Toast.LENGTH_LONG);
                    } else {
                        Hooks(data);
                    }
                    matched = "Yes";
                } else {
                    if (count == max && matched.equals("No")) {
                        System.out.println("Trying default hook!");
                        String fallback = html[1];
                        fallback = fallback.replace("<p>", "");
                        fallback = fallback.replace("</p>", "");
                        fallback = fallback.replaceAll("[0-9]", "");
                        String SavedHooks = text.toString().replaceAll("[0-9]", "");
                        if (fallback.trim().equals(SavedHooks.trim())) {
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Latest), Toast.LENGTH_LONG);
                        } else {
                            Hooks(fallback);
                            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Updated), Toast.LENGTH_LONG);
                        }
                    }
                }
            }
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ListView(Main.this);
        updateListView();

        final android.widget.ListView lv = (android.widget.ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if (mAdapter.getItemViewType(position) != ListView.TYPE_SEPARATOR) {

                    String Position = mAdapter.getItem(position);
                    if (Position.equals(getResources().getString(R.string.Hide))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                        builder.setMessage(getResources().getString(R.string.Warning));
                        builder.setPositiveButton(getResources().getString(R.string.Okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UiUtils.setActivityVisibleInDrawer(Main.this, false);

                                mAdapter = new ListView(Main.this);
                                mAdapter.addSectionHeaderItem(getResources().getString(R.string.Visibility));
                                mAdapter.addItem(UiUtils.getActivityVisibleInDrawer(Main.this) ? getResources().getString(R.string.Hide) : getResources().getString(R.string.Show));
                                mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
                                mAdapter.addItem(getResources().getString(R.string.Image));
                                mAdapter.addItem(getResources().getString(R.string.Video));
                                mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
                                mAdapter.addItem("GitHub");
                                mAdapter.addItem("Pastebin");
                                mAdapter.addSectionHeaderItem(getResources().getString(R.string.Recommended));
                                mAdapter.addItem("Zoom For Instagram");
                                setListAdapter(mAdapter);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    if (Position.equals(getResources().getString(R.string.Show))) {
                        UiUtils.setActivityVisibleInDrawer(Main.this, true);
                        updateListView();
                    }
                    if (Position.equals(getResources().getString(R.string.NotificationShow))) {
                        setNotification("Show");
                        updateListView();
                    }
                    if (Position.equals(getResources().getString(R.string.NotificationHide))) {
                        setNotification("Hide");
                        updateListView();
                    }
                    if (Position.equals(getResources().getString(R.string.Image))) {
                        SaveLocation = "Image";

                        //Image Fetch
                        final File imagelocation = new File(getDirectory + "/.Instagram/Image.txt");

                        StringBuilder image = new StringBuilder();

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(imagelocation));
                            String line;

                            while ((line = br.readLine()) != null) {
                                image.append(line);
                            }
                            br.close();
                        } catch (Exception e) {
                            image.append("Instagram");
                        }

                        String imageLocation = image.toString();
                        imageLocation = imageLocation.replace("file://", "");

                        Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                        if (!imageLocation.equals("Instagram")) {
                            i.putExtra(FilePickerActivity.EXTRA_START_PATH, imageLocation);
                        }
                        startActivityForResult(i, FILE_CODE);
                    }
                    if (Position.equals(getResources().getString(R.string.Video))) {
                        SaveLocation = "Video";

                        //Video Fetch
                        File videolocation = new File(getDirectory + "/.Instagram/Video.txt");

                        StringBuilder video = new StringBuilder();

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(videolocation));
                            String line;

                            while ((line = br.readLine()) != null) {
                                video.append(line);
                            }
                            br.close();
                        } catch (Exception e) {
                            video.append("Instagram");
                        }

                        String videoLocation = video.toString();
                        videoLocation = videoLocation.replace("file://", "");

                        Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                        if (!videoLocation.equals("Instagram")) {
                            i.putExtra(FilePickerActivity.EXTRA_START_PATH, videoLocation);
                        }
                        startActivityForResult(i, FILE_CODE);
                    }
                    if (Position.equals("GitHub")) {
                        new RequestTask().execute("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                    }
                    if (Position.equals("Pastebin")) {
                        new RequestTask().execute("http://pastebin.com/raw.php?i=sTXbUFcx");
                    }
                    if (Position.equals("Zoom For Instagram")) {
                        boolean appInstalled = appInstalledOrNot("com.Taptigo.XposedModules.IgZoom");
                        if (appInstalled) {
                            Intent intent = new Intent("de.robv.android.xposed.installer.OPEN_SECTION");
                            intent.setPackage("de.robv.android.xposed.installer");
                            intent.putExtra("section", "modules");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://repo.xposed.info/module/com.Taptigo.XposedModules.IgZoom"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    public void RevertSaveLocation(String fileName) {
        try {
            File root = new File(getDirectory, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("Instagram");
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }

        Toast toast;

        if (fileName.equals("Image.txt")) {
            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.I_LocationChanged), Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.V_LocationChanged), Toast.LENGTH_LONG);
        }

        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            SaveLocation = "None";
        }
        if (resultCode == 1337) {
            if (SaveLocation.equals("Image")) {
                RevertSaveLocation("Image.txt");
            } else {
                RevertSaveLocation("Video.txt");
            }
        }
        if (requestCode == FILE_CODE && resultCode == com.ihelp101.instagram.Main.RESULT_OK) {
            Uri Location = null;
            if (data.getBooleanExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Location = clip.getItemAt(i).getUri();
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (com.ihelp101.instagram.FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Location = Uri.parse(path);
                        }
                    }
                }

            } else {
                Location = data.getData();
            }

            Toast toast;

            if (Location.toString().contains("/storage/")) {
                try {
                    File root = new File(getDirectory, ".Instagram");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File gpxfile;
                    if (SaveLocation.equals("Image")) {
                        gpxfile = new File(root, "Image.txt");
                    } else {
                        gpxfile = new File(root, "Video.txt");
                    }

                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(Location.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {

                }
                SaveLocation = "None";
            } else {
                toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Incorrect_Location), Toast.LENGTH_LONG);

                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if (v != null) v.setGravity(Gravity.CENTER);
                toast.show();

                Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(i, FILE_CODE);
            }
        }
    }

    public void Hooks(String data) {
        try {
            File root = new File(getDirectory, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Hooks.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }


        File file = new File(getDirectory, ".Instagram/Image.txt");
        if(!file.exists()) {
            try {
                File root = new File(getDirectory, ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "Image.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("Instagram");
                writer.flush();
                writer.close();
            } catch (IOException e) {

            }
        }

        file = new File(getDirectory, ".Instagram/Video.txt");
        if(!file.exists()) {
            try {
                File root = new File(getDirectory, ".Instagram");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "Video.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append("Instagram");
                writer.flush();
                writer.close();
            } catch (IOException e) {

            }
        }
    }

    public String getNotification() {
        //Image Fetch
        final File location = new File(getDirectory + "/.Instagram/Notification.txt");

        StringBuilder status = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(location));
            String line;

            while ((line = br.readLine()) != null) {
                status.append(line);
            }
            br.close();
        }
        catch (Exception e) {
            status.append("Show");
        }
        return status.toString();
    }

    public void setNotification(String status) {
        try {
            File root = new File(getDirectory, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Notification.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(status);
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }

        Toast.makeText(Main.this, getResources().getString(R.string.Notification_Changed, status), Toast.LENGTH_SHORT).show();
    }

    public void updateListView() {
        mAdapter = new ListView(Main.this);
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Visibility));
        mAdapter.addItem(UiUtils.getActivityVisibleInDrawer(Main.this) ? getResources().getString(R.string.Hide) : getResources().getString(R.string.Show));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Notification));
        if (getNotification().equals("Hide")) {
            mAdapter.addItem(getResources().getString(R.string.NotificationShow));
        } else {
            mAdapter.addItem(getResources().getString(R.string.NotificationHide));
        }
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
        mAdapter.addItem(getResources().getString(R.string.Image));
        mAdapter.addItem(getResources().getString(R.string.Video));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Recommended));
        mAdapter.addItem("Zoom For Instagram");
        setListAdapter(mAdapter);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}