package com.ihelp101.instagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.ListPreference;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainNew extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle drawerToggle;
    Adapter mAdapter;
    ListView listView;
    ProgressDialog mDialog;

    static int FILE_CODE = 0;
    String currentAction;
    String getDirectory = Environment.getExternalStorageDirectory().toString();
    String newVersion = "None";
    String saveLocation = "None";
    String saveSD;
    String version = "123";

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            saveLocation = "None";
        }

        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            Helper.setSetting(saveLocation, data.getDataString() + ";" + saveSD);
            if (Build.VERSION.SDK_INT >= 19) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            saveLocation = "None";
        }

        if (requestCode == 5849 && resultCode == Activity.RESULT_OK) {
            Helper.setSetting(saveLocation, data.getDataString() + ";" + saveSD);
            if (Build.VERSION.SDK_INT >= 19) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            saveLocation = "None";
        }

        if (requestCode == FILE_CODE && resultCode == MainNew.RESULT_OK) {
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
                        if (Build.VERSION.SDK_INT >= 24) {
                            try {
                                Helper.setError("Try New SD Card");
                                StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
                                List<StorageVolume> volumesList = sm.getStorageVolumes();
                                Intent intent = volumesList.get(1).createAccessIntent(null);
                                startActivityForResult(intent, 5849);
                            } catch (Throwable t) {
                                Helper.setError("Storage Manager Error: " +t);
                            }
                        } else {
                            showSDTip();
                        }
                    } else {
                        Helper.setSetting(saveLocation, Helper.getSaveLocation(saveLocation).split(";")[0] + ";" + Location.getPath() + "/");
                        saveLocation = "None";
                    }
                } else {
                    try {
                        toast = Helper.getResourceString(getApplicationContext(), R.string.Incorrect_Location);
                    } catch (Throwable t) {
                        toast = "Unable to save here.\nPlease select another location.";
                    }

                    setToast(toast);

                    Intent i = new Intent(MainNew.this, FilePickerActivity.class);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                    startActivityForResult(i, FILE_CODE);
                }
            } catch (Exception e) {
            }
        }

        if (requestCode == FILE_CODE && resultCode == 123456) {
            Helper.setSetting(saveLocation, "Instagram");
            String reset;

            try {
                reset = Helper.getResourceString(getApplicationContext(), R.string.Reset);
            } catch (Throwable t) {
                reset = "Unable to save here.\nPlease select another location.";
            }

            Toast.makeText(getApplicationContext(), reset + " " + currentAction, Toast.LENGTH_SHORT).show();
        }
    }

    class HookCheck extends AsyncTask<String, String, String> {

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
                Helper.setError("Hook Fetch Failed - " +e);
            }


            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo p = packs.get(i);
                    if (p.packageName.equals("com.instagram.android")) {
                        version = Integer.toString(p.versionCode);
                        version = version.substring(0, version.length() - 2);
                    }
                }

                String text = Helper.getSetting("Hooks");

                String toast;
                try {
                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Updated);
                } catch (Throwable t) {
                    toast = "Hooks have been updated.\nPlease kill the Instagram app.";
                }

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
                            try {
                                toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                            } catch (Throwable t) {
                                toast = "You already have the latest hooks.";
                            }
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
                                try {
                                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                                } catch (Throwable t) {
                                    toast = "You already have the latest hooks.";
                                }
                            } else {
                                Helper.setSetting("Hooks", fallback);
                                try {
                                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Updated);
                                } catch (Throwable t) {
                                    toast = "Hooks have been updated.\nPlease kill the Instagram app.";
                                }
                            }
                        } else if (!data.isEmpty() && matched.equals("No")) {
                            int typeCheck = Integer.parseInt(version) - Integer.parseInt(finalCheck);
                            if (typeCheck <= 2 && typeCheck >= -2) {
                                if (data.trim().equals(text.trim())) {
                                    try {
                                        toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                                    } catch (Throwable t) {
                                        toast = "You already have the latest hooks.";
                                    }
                                } else {
                                    Helper.setSetting("Hooks", data);
                                }
                                matched = "Yes";
                            }
                        }
                    }
                }
                mDialog.cancel();
                setToast(toast);
            } catch (Exception e) {
                mDialog.cancel();
                Helper.setError("Hooks Parse Failed - " +e);
            }
        }
    }

    class Translation extends AsyncTask<String, String, String> {
        String response = "Nope";
        String responseDefaultLanguage = "None";
        String responseLanguage = "None";
        String userLocale;

        @Override
        protected String doInBackground(String... uri) {
            try {
                userLocale = Resources.getSystem().getConfiguration().locale.toString() + ";";
                URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Translations.txt");
                URLConnection c = u.openConnection();
                c.connect();

                response = Helper.convertStreamToString(c.getInputStream());

                u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/app/src/main/res/values/strings.xml");
                c = u.openConnection();
                c.connect();
                responseDefaultLanguage = Helper.convertStreamToString(c.getInputStream());

                try {
                    u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/app/src/main/res/values-" + userLocale.replace(";", "") + "/strings.xml");
                    c = u.openConnection();
                    c.connect();
                    responseLanguage = Helper.convertStreamToString(c.getInputStream());
                } catch (Exception e) {
                    responseLanguage = "None";
                }

                responseLanguage = "Please translate the translate the strings below. An example is below that should help you understand the required formatting. Thank you for taking the time to help out.\n\nExample:\nOriginal - <string name=\"Hide\">Hide App</string>\nSpanish - <string name=\"Hide\">Ocultar App</string>\n\nLanguage - Which Language Are You Translating?\n\n" + responseLanguage;
                responseDefaultLanguage = "Please translate the translate the strings below. An example is below that should help you understand the required formatting. Thank you for taking the time to help out.\n\nExample:\nOriginal - <string name=\"Hide\">Hide App</string>\nSpanish - <string name=\"Hide\">Ocultar App</string>\n\nLanguage - Which Language Are You Translating?\n\n" + responseDefaultLanguage;

            } catch (Exception e) {
                Helper.setError("Translation - " + e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (response.contains(userLocale) && !responseLanguage.equals("None")) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                                    intent.putExtra(Intent.EXTRA_TEXT, responseLanguage);
                                    intent.setType("text/plain");
                                    startActivity(Intent.createChooser(intent, "Email"));
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                                    intent.putExtra(Intent.EXTRA_TEXT, responseDefaultLanguage);
                                    intent.setType("text/plain");
                                    startActivity(Intent.createChooser(intent, "Email"));
                                    break;
                            }
                        }
                    };

                    String userLanguage = Locale.getDefault().getDisplayLanguage();
                    userLanguage = userLanguage.substring(0, 1).toUpperCase() + userLanguage.substring(1);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainNew.this);
                    builder.setMessage("Will you be translating " + userLanguage + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                    intent.putExtra(Intent.EXTRA_TEXT, responseDefaultLanguage);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Email"));
                }
            } catch (Exception e) {
                Helper.setError("Translation2 - " + e);
            }
        }
    }

    class VersionCheck extends AsyncTask<String, String, String> {

        String logCheck = "No";

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                logCheck = uri[0];
            } catch (Throwable t) {
            }

            try {
                Random r = new Random();
                int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Version.txt?" +cacheInt);
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();

                responseString = Helper.convertStreamToString(inputStream);

            } catch (Exception e) {
                Helper.setError("Update GitHub Failed - " +e);
                responseString = "None";
            }

            return responseString.trim();
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            int skip = 0;

            try {
                mDialog.cancel();
                version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                version = version.trim();

                int versionCheck = Integer.parseInt(result.replaceAll("\\.", ""));
                int currentVersion = Integer.parseInt(version.replaceAll("\\.", ""));

                if (currentVersion > versionCheck || currentVersion >= 200 && versionCheck == 1999) {
                    skip = 1;
                }

                if (result.equals(version) & !result.equals("None")) {
                    if (logCheck.equals("Log") ) {
                        sendErrorLog();
                    } else {
                        Toast.makeText(getApplicationContext(), "XInsta Up-To-Date", Toast.LENGTH_SHORT).show();
                    }
                } else if (logCheck.equals("Log")) {
                    if (skip == 0) {
                        showErrorLogUpdate(result);
                    } else {
                        sendErrorLog();
                    }
                } else if (skip == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainNew.this);

                    builder.setTitle("Update XInsta To " +result+ "?");

                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Random r = new Random();
                            int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://raw.githubusercontent.com/iHelp101/XInsta/master/XInsta.apk?" +cacheInt));
                            request.setTitle("XInsta " +result+ " Update");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            }
                            request.setMimeType("application/vnd.android.package-archive");
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Insta-" + result + ".apk");

                            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                } else {
                    Toast.makeText(getApplicationContext(), "XInsta Up-To-Date", Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable t) {
                Helper.setError("Update Check Failed - " +t);
                Toast.makeText(getApplicationContext(), "Failed To Check Update", Toast.LENGTH_SHORT).show();

                if (logCheck.equals("Log")) {
                    sendErrorLog();
                }
            }
        }
    }

    class VersionCheckNoDialog extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";


            try {
                Random r = new Random();
                int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Version.txt?" +cacheInt);
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();

                responseString = Helper.convertStreamToString(inputStream);

            } catch (Exception e) {
                responseString = "None";
            }

            return responseString.trim();
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            int skip = 0;

            try {
                version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                version = version.trim();

                int versionCheck = Integer.parseInt(result.replaceAll("\\.", ""));
                int currentVersion = Integer.parseInt(version.replaceAll("\\.", ""));

                if (currentVersion > versionCheck || currentVersion >= 200 && versionCheck == 1999) {
                    skip = 1;
                }

                if (!result.equals(version) && !result.equals("None") && skip == 0) {
                    newVersion = result;
                    updateListView();
                }
            } catch (Throwable t) {
            }
        }
    }

    void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainNew.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                setupNav();
            }
        } else {
            setupNav();
        }
    }

    void listAction() {
        String image;
        String profile;
        String video;

        try {
            image = Helper.getResourceString(getApplicationContext(), R.string.Image);
        } catch (Throwable t) {
            image = "Image Location";
        }

        try {
            profile = Helper.getResourceString(getApplicationContext(), R.string.Profile);
        } catch (Throwable t) {
            profile = "Profile Location";
        }
        try {
            video = Helper.getResourceString(getApplicationContext(), R.string.Video);
        } catch (Throwable t) {
            video = "Video Location";
        }

        if (currentAction.contains("Update To XInsta")) {
            mDialog = new ProgressDialog(MainNew.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
            new VersionCheck().execute();
        }

        if (currentAction.equals(image) || currentAction.equals(profile) || currentAction.equals(video)) {
            saveLocation = "Image";
            if (currentAction.equals(profile)) {
                saveLocation = "Profile";
            } else if (currentAction.equals(video)) {
                saveLocation = "Video";
            }

            String save = Helper.getSaveLocation(saveLocation);
            save = save.replace("file://", "").replaceAll("%20", " ");

            if (save.contains("com.android.externalstorage.documents") && Build.VERSION.SDK_INT >= 21) {
                save = save.split(";")[1];
            }

            Intent i = new Intent(MainNew.this, FilePickerActivity.class);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            if (!save.equals("Instagram")) {
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, save);
            } else {
                File directory = new File(URI.create(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram").getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram");
            }
            startActivityForResult(i, FILE_CODE);
        }
        if (currentAction.equals("GitHub")) {
            mDialog = new ProgressDialog(MainNew.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
            new HookCheck().execute("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
        }
        if (currentAction.equals("Pastebin")) {
            mDialog = new ProgressDialog(MainNew.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
            new HookCheck().execute("http://pastebin.com/raw.php?i=sTXbUFcx");
        }
        if (currentAction.equals("Alternate Source")) {
            mDialog = new ProgressDialog(MainNew.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
            new HookCheck().execute("http://www.snapprefs.com/xinsta/Hooks.txt");
        }
        if (currentAction.equals("One Tap Video Download")) {
            if (appInstalledOrNot("com.phantom.onetapvideodownload")) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.phantom.onetapvideodownload");
                startActivity(intent);
            } else {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.phantom.onetapvideodownload")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.phantom.onetapvideodownload")));
                }
            }
        }
        if (currentAction.equals("Zoom For Instagram")) {
            if (appInstalledOrNot("com.Taptigo.XposedModules.IgZoom")) {
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.list);
        updateListView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent();
        intent.setAction("com.ihelp101.instagram.TESTING");
        intent.putExtra("Notification", "{\"a\":\"https:\\/\\/scontent.cdninstagram.com\\/vp\\/a2d393e60f22741e58fd123992bd41d2\\/5B4724B2\\/t51.2885-19\\/s150x150\\/20635195_128866607729452_2510115871469338624_n.jpg\",\"bc\":\"{\\\"dt\\\":0}\",\"c\":\"post\",\"sound\":\"default\",\"i\":\"https:\\/\\/scontent.cdninstagram.com\\/vp\\/43513e0243d4dec4f8f78c29680baac1\\/5AAFF53A\\/t51.2885-15\\/e15\\/29087830_2164687610426173_8718612868750114816_n.jpg\",\"SuppressBadge\":\"1\",\"m\":\"liztess just posted a video.\",\"collapse_key\":\"post\",\"s\":\"424110996\",\"u\":35518138,\"PushNotifID\":\"567a4b12cb988H21df6baH567a4fac2bc5aH49\",\"pi\":\"567a4b12cb988H21df6baH567a4fac2bc5aH49\",\"ig\":\"media?id=1737388370425602165_424110996\"}");
        //sendBroadcast(intent);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.AM,  R.string.Date) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };

        drawerToggle.syncState();

        checkPermission();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                currentAction = mAdapter.getItem(position);
                listAction();
            }
        });

        try {
            if (getIntent().getStringExtra("Update").equals("Yes")) {
                mDialog = new ProgressDialog(MainNew.this);
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new VersionCheck().execute();
            }
        } catch (Throwable t) {
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        String clicked = (String) menuItem.getTitle();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawers();

        String errorLog;

        try {
            errorLog = Helper.getResourceString(getApplicationContext(), R.string.Error_log);
        } catch (Throwable t) {
            errorLog = getResources().getString(R.string.Error_log);
        }

        if (clicked.equals(errorLog)) {
            try {
                mDialog = new ProgressDialog(MainNew.this);
                mDialog.setMessage("Please wait...");
                mDialog.setCancelable(false);
                mDialog.show();
                new MainNew.VersionCheck().execute("Log");
            } catch (Exception e) {
                setToast("Failed To Obtain Logs");
            }
        }

        String checkVersion;

        try {
            checkVersion = Helper.getResourceString(getApplicationContext(), R.string.Check);
        } catch (Throwable t) {
            checkVersion = getResources().getString(R.string.Check);
        }

        if (clicked.equals(checkVersion)) {
            mDialog = new ProgressDialog(MainNew.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
            new VersionCheck().execute();
        }

        if (clicked.equals("Settings")) {
            Intent myIntent = new Intent(getApplicationContext(), Settings.class);
            startActivity(myIntent);
        }

        String specialThanks;

        try {
            specialThanks = Helper.getResourceString(getApplicationContext(), R.string.Special);
        } catch (Throwable t) {
            specialThanks = getResources().getString(R.string.Special);
        }

        if (clicked.equals(specialThanks)) {
            Intent myIntent = new Intent(getApplicationContext(), Thanks.class);
            startActivity(myIntent);
        }

        String translations;

        try {
            translations = Helper.getResourceString(getApplicationContext(), R.string.Translations);
        } catch (Throwable t) {
            translations = getResources().getString(R.string.Translations);
        }

        if (clicked.equals(translations)) {
            try {
                new MainNew.Translation().execute();
            } catch (Exception e) {
            }
        }

        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupNav();
                } else {
                    setToast("Permission denied. Unable to save hook file, image locations, and preferences.");
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNav();
    }

    void sendErrorLog() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Error Log");
        intent.putExtra(Intent.EXTRA_TEXT, "Please check out my XInsta Error Log.");
        intent.setType("text/plain");

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

    void setToast(String message) {
        Toast toast = Toast.makeText(MainNew.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    void setupNav() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(null);
        navigationView.setNavigationItemSelectedListener(this);

        for (int menuSize = 0; menuSize < navigationView.getMenu().size();menuSize++) {
            for (int subMenuSize = 0; subMenuSize < navigationView.getMenu().getItem(menuSize).getSubMenu().size(); subMenuSize++) {

                final MenuItem menuItem = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize);
                final String menuTitle = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize).getTitle().toString();

                final SwitchCompat item = (SwitchCompat) menuItem.getActionView();

                if (item != null) {
                    item.setOnCheckedChangeListener(null);
                }
            }
        }
    }

    void showErrorLogUpdate(final String version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainNew.this);

        builder.setTitle("XInsta Update Avaliable");
        builder.setMessage("XInsta " +version+ " is available, which may resolve your issue. Would you like to update before sending this error log?");

        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Random r = new Random();
                int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://raw.githubusercontent.com/iHelp101/XInsta/master/XInsta.apk?" +cacheInt));
                request.setTitle("XInsta " +version+ " Update");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setMimeType("application/vnd.android.package-archive");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Insta-" + version + ".apk");

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendErrorLog();
            }
        });

        builder.show();
    }

    void showSDTip() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.test;
        VideoView videoView = new VideoView(this);
        videoView.setVideoPath(path);
        videoView.setZOrderOnTop(true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.start();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 12);
                                    dialog.dismiss();
                                } catch (Throwable t) {
                                    Helper.setError("SD Card Error: " +t);
                                }
                            }
                        })
                        .setView(videoView);

        builder.create().show();
    }

    void updateListView() {
        String image;
        String profile;
        String recommended;
        String save;
        String updateHooks;
        String video;

        try {
            save = Helper.getResourceString(getApplicationContext(), R.string.Save);
            image = Helper.getResourceString(getApplicationContext(), R.string.Image);
            profile = Helper.getResourceString(getApplicationContext(), R.string.Profile);
            recommended = Helper.getResourceString(getApplicationContext(), R.string.Recommended);
            updateHooks = Helper.getResourceString(getApplicationContext(), R.string.Update);
            video = Helper.getResourceString(getApplicationContext(), R.string.Video);
        } catch (Throwable t) {
            image = "Image Location";
            profile = "Profile Location";
            recommended = "RECOMMENDED";
            save = "SAVE LOCATIONS";
            updateHooks = "UPDATE HOOKS";
            video = "Video Location";
        }

        mAdapter = new Adapter(MainNew.this);
        if (!newVersion.equals("None")) {
            mAdapter.addSectionHeaderItem("UPDATE XINSTA");
            mAdapter.addItem("Update To XInsta " + newVersion);
        } else {
            new VersionCheckNoDialog().execute();
        }
        mAdapter.addSectionHeaderItem(save);
        mAdapter.addItem(image);
        mAdapter.addItem(profile);
        mAdapter.addItem(video);
        mAdapter.addSectionHeaderItem(updateHooks);
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        mAdapter.addItem("Alternate Source");
        mAdapter.addSectionHeaderItem(recommended.toUpperCase());
        mAdapter.addItem("One Tap Video Download");
        mAdapter.addItem("Zoom For Instagram");

        listView.setAdapter(mAdapter);
    }
}
