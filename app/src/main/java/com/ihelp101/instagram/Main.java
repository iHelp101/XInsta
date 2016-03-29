package com.ihelp101.instagram;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main extends ListActivity {

    private ListView mAdapter;
    private String version = "123";
    private static int FILE_CODE = 0;
    String saveLocation = "None";
    String saveSD;
    String currentAction;
    String getDirectory = Environment.getExternalStorageDirectory().toString();

    boolean isSDCard(String saveLocation) {
        boolean result = false;
        try {
            File root = new File(saveLocation, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "SD.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("SD Card Check");
            writer.flush();
            writer.close();

            result = false;
        } catch (FileNotFoundException e) {
            result = true;
        } catch (Exception e) {
        }

        return result;
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                URL u = new URL(uri[0]);
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();

                responseString = Helper.convertStreamToString(inputStream);
            } catch (Exception e) {
                responseString = "Nope";
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

            String text = Helper.getSetting("Hooks");

            String toast = getResources().getString(R.string.Hooks_Updated);

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
                    if (data.trim().equals(text.trim())) {
                        toast = getResources().getString(R.string.Hooks_Latest);
                    } else {
                        Helper.setSetting("Hooks", data);
                    }
                    matched = "Yes";
                } else {
                    if (count == max && matched.equals("No")) {
                        System.out.println("Trying default hook!");
                        String fallback = html[1];
                        fallback = fallback.replace("<p>", "");
                        fallback = fallback.replace("</p>", "");
                        fallback = fallback.replaceAll("[0-9]", "");
                        String SavedHooks = text.replaceAll("[0-9]", "");
                        if (fallback.trim().equals(SavedHooks.trim())) {
                            toast = getResources().getString(R.string.Hooks_Latest);
                        } else {
                            Helper.setSetting("Hooks", fallback);
                            toast = getResources().getString(R.string.Hooks_Updated);
                        }
                    }
                }
            }
            setToast(toast);
        }
    }

    boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                listAction();
            }
        } else {
            listAction();
        }
    }

    void listAction() {
        if (currentAction.equals(getResources().getString(R.string.Image)) || currentAction.equals(getResources().getString(R.string.Profile)) || currentAction.equals(getResources().getString(R.string.Video))) {
            saveLocation = "Image";
            if (currentAction.equals(getResources().getString(R.string.Profile))) {
                saveLocation = "Profile";
            } else if (currentAction.equals(getResources().getString(R.string.Video))) {
                saveLocation = "Video";
            }

            String save = Helper.getSaveLocation(saveLocation);
            save = save.replace("file://", "").replaceAll("%20", " ");

            if (save.contains("com.android.externalstorage.documents") && android.os.Build.VERSION.SDK_INT >= 21) {
                save = save.split(";")[1];
            }

            Intent i = new Intent(Main.this, FilePickerActivity.class);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            if (!save.equals("Instagram")) {
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, save);
            } else {
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram");
            }
            startActivityForResult(i, FILE_CODE);
        }
        if (currentAction.equals("GitHub")) {
            new RequestTask().execute("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
        }
        if (currentAction.equals("Pastebin")) {
            new RequestTask().execute("http://pastebin.com/raw.php?i=sTXbUFcx");
        }
        if (currentAction.equals("Alternate Source")) {
            new RequestTask().execute("http://www.snapprefs.com/xinsta/Hooks.txt");
        }
        if (currentAction.equals("Zoom For Instagram")) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ListView(Main.this);
        updateListView();

        if (!appInstalledOrNot("com.instagram.android")) {
            setToast("Instagram Is Not Installed");
            setError("Instagram Is Not Installed");
        }

        final android.widget.ListView lv = (android.widget.ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if (mAdapter.getItemViewType(position) != ListView.TYPE_SEPARATOR) {
                    currentAction = mAdapter.getItem(position);
                    checkPermission();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        if (Helper.getSettings("Folder")) {
            menu.findItem(R.id.folder).setChecked(true);
        } else {
            menu.findItem(R.id.folder).setChecked(false);
        }

        if (Helper.getSettings("Like")) {
            menu.findItem(R.id.like).setChecked(true);
        } else {
            menu.findItem(R.id.like).setChecked(false);
        }

        if (Helper.getSettings("Notification")) {
            menu.findItem(R.id.notification_hide).setChecked(true);
        } else {
            menu.findItem(R.id.notification_hide).setChecked(false);
        }

        if (Helper.getSettings("Push")) {
            menu.findItem(R.id.push).setChecked(true);
        } else {
            menu.findItem(R.id.push).setChecked(false);
        }

        if (UiUtils.getActivityVisibleInDrawer(Main.this)) {
            menu.findItem(R.id.hide_app).setChecked(false);
        } else {
            menu.findItem(R.id.hide_app).setChecked(true);
        }

        if (Helper.getSettings("Suggestion")) {
            menu.findItem(R.id.suggestion).setChecked(true);
        } else {
            menu.findItem(R.id.suggestion).setChecked(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        String clicked = (String) menuItem.getTitle();

        if (clicked.equals(getResources().getString(R.string.Date))) {
            Intent myIntent = new Intent(getApplicationContext(), com.ihelp101.instagram.Date.class);
            startActivity(myIntent);
        }

        if (clicked.equals(getResources().getString(R.string.Error_log))) {
            try {
                sendErrorLog();
            } catch (Exception e) {
                setToast("Failed To Obtain Logs");
            }
        }

        if (clicked.equals(getResources().getString(R.string.Like))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                Helper.setSetting("Like", Boolean.toString(menuItem.isChecked()));
            } else {
                menuItem.setChecked(true);
                Helper.setSetting("Like", Boolean.toString(menuItem.isChecked()));
            }
        }

        if (clicked.equals(getResources().getString(R.string.Folder))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                Helper.setSetting("Folder", Boolean.toString(menuItem.isChecked()));
            } else {
                menuItem.setChecked(true);
                Helper.setSetting("Folder", Boolean.toString(menuItem.isChecked()));
            }
        }

        if (clicked.equals(getResources().getString(R.string.Hide))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                UiUtils.setActivityVisibleInDrawer(Main.this, true);
            } else {
                menuItem.setChecked(true);
                menuItem.setChecked(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setMessage(getResources().getString(R.string.Warning));
                builder.setPositiveButton(getResources().getString(R.string.Okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UiUtils.setActivityVisibleInDrawer(Main.this, false);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        if (clicked.equals(getResources().getString(R.string.NotificationHide))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                Helper.setSetting("Notification", Boolean.toString(menuItem.isChecked()));
            } else {
                menuItem.setChecked(true);
                Helper.setSetting("Notification", Boolean.toString(menuItem.isChecked()));
            }
        }

        if (clicked.equals(getResources().getString(R.string.Push))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                Helper.setSetting("Push", Boolean.toString(menuItem.isChecked()));
            } else {
                menuItem.setChecked(true);
                Helper.setSetting("Push", Boolean.toString(menuItem.isChecked()));
            }
        }

        if (clicked.equals(getResources().getString(R.string.Suggestion))) {
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
                Helper.setSetting("Suggestion", Boolean.toString(menuItem.isChecked()));
            } else {
                menuItem.setChecked(true);
                Helper.setSetting("Suggestion", Boolean.toString(menuItem.isChecked()));
            }
        }

        if (clicked.equals(getResources().getString(R.string.Translations))) {
            try {
                sendTranslation();
            } catch (Exception e) {
            }
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            saveLocation = "None";
        }
        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            Helper.setSetting(saveLocation, data.getDataString() + ";" + saveSD);
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            saveLocation = "None";
        }

        if (requestCode == FILE_CODE && resultCode == Main.RESULT_OK) {
            Uri Location = null;
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
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
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Location = Uri.parse(path);
                        }
                    }
                }

            } else {
                Location = data.getData();
            }

            String toast;

            try {
                if (Location.toString().contains("/storage/") || Location.toString().contains("/mnt/") || Location.toString().contains("/sdcard/")) {
                    if (!isSDCard(Location.getPath())) {
                        Helper.setSetting(saveLocation, Location.getPath() + "/");
                        saveLocation = "None";
                    } else if (!Helper.getSaveLocation(saveLocation).contains("com.android.externalstorage.documents")){
                        saveSD = Location.getPath() + "/";
                        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 12);
                    } else {
                        Helper.setSetting(saveLocation, Helper.getSaveLocation(saveLocation).split(";")[0] + ";" + Location.getPath() + "/");
                        saveLocation = "None";
                    }
                } else {
                    toast = getResources().getString(R.string.Incorrect_Location);

                    setToast(toast);

                    Intent i = new Intent(Main.this, FilePickerActivity.class);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                    startActivityForResult(i, FILE_CODE);
                }
            } catch (Exception e) {
            }
        }
    }

    void sendErrorLog() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Error Log");
        intent.putExtra(Intent.EXTRA_TEXT, "Please check out my XInsta Error Log.");
        File root = new File(getDirectory, ".Instagram");
        File file = new File(root, "Error.txt");
        Uri uri = Uri.fromFile(file);
        if (!file.exists() || !file.canRead()) {
            setToast("Error Log Missing!");
            return;
        }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Email"));
    }

    void sendTranslation() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Translate.txt");
                    URLConnection c = u.openConnection();
                    c.connect();

                    InputStream inputStream = c.getInputStream();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                    intent.putExtra(Intent.EXTRA_TEXT, Helper.convertStreamToString(inputStream));

                    startActivity(Intent.createChooser(intent, "Email"));
                } catch (Exception e) {
                    setError("Translation - " +e);
                }
            }
        });

        thread.start();
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


    public void setToast(String message) {
        Toast toast = Toast.makeText(Main.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    void updateListView() {
        mAdapter = new ListView(Main.this);
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
        mAdapter.addItem(getResources().getString(R.string.Image));
        mAdapter.addItem(getResources().getString(R.string.Profile));
        mAdapter.addItem(getResources().getString(R.string.Video));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        mAdapter.addItem("Alternate Source");
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Recommended));
        mAdapter.addItem("Zoom For Instagram");
        setListAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listAction();
                } else {
                    setToast("Permission denied. Unable to save hook file, image locations, and preferences.");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}