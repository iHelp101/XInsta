package com.ihelp101.instagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle drawerToggle;
    Adapter mAdapter;
    ListView listView;

    static int FILE_CODE = 0;
    String currentAction;
    String getDirectory = Environment.getExternalStorageDirectory().toString();
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

        if (requestCode == FILE_CODE && resultCode == 123456) {
            Helper.setSetting(saveLocation, "Instagram");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Reset) + " " + currentAction, Toast.LENGTH_SHORT).show();
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

                u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Translate.txt");
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
            } catch (Exception e) {
                Helper.setError("Translation - " + e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                System.out.println("Info: " +responseLanguage);
                if (response.contains(userLocale) && !responseLanguage.equals("None")) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    System.out.println("Yes");
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
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
                    } else if (!data.isEmpty() && matched.equals("No")) {
                        int typeCheck = Integer.parseInt(version) - Integer.parseInt(finalCheck);
                        if (typeCheck <= 2 && typeCheck >= -2) {
                            if (data.trim().equals(text.trim())) {
                                toast = getResources().getString(R.string.Hooks_Latest);
                            } else {
                                Helper.setSetting("Hooks", data);
                            }
                            matched = "Yes";
                        }
                    }
                }
            }
            setToast(toast);
        }
    }

    void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                setupNav();
            }
        } else {
            setupNav();
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
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        String clicked = (String) menuItem.getTitle();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawers();

        if (clicked.equals(getResources().getString(R.string.Date))) {
            if (Helper.getSetting("Date").equals("Instagram")) {
                Helper.setSetting("Date", Helper.getData(getApplicationContext()));
            }
            Intent myIntent = new Intent(getApplicationContext(), com.ihelp101.instagram.Date.class);
            startActivity(myIntent);
        }

        if (clicked.equals(getResources().getString(R.string.Default))) {
            final CharSequence items[] = {"GitHub", "Pastebin", "Alternate Source"};

            int itemSelected;
            if (Helper.getSetting("Source").equals("Instagram") || Helper.getSetting("Source").equals("GitHub")) {
                itemSelected = 0;
            } else if (Helper.getSetting("Source").equals("Pastebin")) {
                itemSelected = 1;
            } else if (Helper.getSetting("Source").equals("Alternate")) {
                itemSelected = 2;
            } else {
                itemSelected = 0;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
            builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Helper.setSetting("Source", String.valueOf(items[which]));
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        if (clicked.equals(getResources().getString(R.string.Follow))) {
            String color = Helper.getSetting("Color");
            if (color.equals("Instagram")) {
                color = "#2E978C";
            }
            ColorPicker cpd = new ColorPicker(Main.this, Color.parseColor(color), new ColorPicker.OnColorSelectedListener() {
                @Override
                public void colorSelected(Integer color) {
                    String hexColor = String.format("#%06X", (0xFFFFFF & color));
                    Helper.setSetting("Color", hexColor);
                }
            });
            cpd.setTitle("Pick a color.");
            cpd.show();
        }

        if (clicked.equals(getResources().getString(R.string.Error_log))) {
            try {
                sendErrorLog();
            } catch (Exception e) {
                setToast("Failed To Obtain Logs");
            }
        }

        if (clicked.equals("Special Thanks")) {
            Intent myIntent = new Intent(getApplicationContext(), Thanks.class);
            startActivity(myIntent);
        }

        if (clicked.equals(getResources().getString(R.string.Translations))) {
            try {
                new Translation().execute();
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
        Toast toast = Toast.makeText(Main.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    void setupNav() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        for (int menuSize = 0; menuSize < navigationView.getMenu().size();menuSize++) {
            for (int subMenuSize = 0; subMenuSize < navigationView.getMenu().getItem(menuSize).getSubMenu().size(); subMenuSize++) {

                final MenuItem menuItem = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize);
                final String menuTitle = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize).getTitle().toString();

                final SwitchCompat item = (SwitchCompat) menuItem.getActionView();

                if (menuTitle.equals(getResources().getString(R.string.Comment))) {
                    if (Helper.getSettings("Comment")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.Date))) {
                    if (!Helper.getSetting("Date").equals("Instagram")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.Folder))) {
                    if (Helper.getSettings("Folder")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.Like))) {
                    if (Helper.getSettings("Like")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.Hide))) {
                    if (!UiUtils.getActivityVisibleInDrawer(getApplicationContext())) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.NotificationHide))) {
                    if (Helper.getSettings("Notification")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals("One Tap Video Download")) {
                    if (Helper.getSettings("OneTap")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals((getResources().getString(R.string.Push)))) {
                    if (Helper.getSettings("Push")) {
                        item.setChecked(true);
                    }
                }
                if (menuTitle.equals(getResources().getString(R.string.Suggestion))) {
                    if (Helper.getSettings("Suggestion")) {
                        item.setChecked(true);
                    }
                }

                try {
                    item.setOnClickListener(null);
                    item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String clicked = menuTitle;

                            if (clicked.equals(getResources().getString(R.string.Comment))) {
                                Helper.setSetting("Comment", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Date))) {
                                if (item.isChecked() && Helper.getSetting("Date").equals("Instagram")) {
                                    Helper.setSetting("Date", Helper.getData(getApplicationContext()));
                                } else if (!item.isChecked()) {
                                    Helper.setSetting("Date", "Instagram");
                                }
                            }

                            if (clicked.equals(getResources().getString(R.string.Like))) {
                                Helper.setSetting("Like", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Folder))) {
                                Helper.setSetting("Folder", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Hide))) {
                                if (item.isChecked()) {
                                    UiUtils.setActivityVisibleInDrawer(Main.this, true);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                                    builder.setMessage(getResources().getString(R.string.Warning));
                                    builder.setPositiveButton(getResources().getString(R.string.Okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            UiUtils.setActivityVisibleInDrawer(Main.this, false);
                                            item.setChecked(true);
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    UiUtils.setActivityVisibleInDrawer(Main.this, true);
                                }
                            }

                            if (clicked.equals("One Tap Video Download")) {
                                if (item.isChecked()) {
                                    Helper.setSetting("OneTap", Boolean.toString(item.isChecked()));
                                } else {
                                    Helper.setSetting("OneTap", Boolean.toString(item.isChecked()));
                                }
                            }

                            if (clicked.equals(getResources().getString(R.string.NotificationHide))) {
                                Helper.setSetting("Notification", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Push))) {
                                Helper.setSetting("Push", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Suggestion))) {
                                Helper.setSetting("Suggestion", Boolean.toString(item.isChecked()));
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    void updateListView() {
        mAdapter = new Adapter(Main.this);
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
        mAdapter.addItem(getResources().getString(R.string.Image));
        mAdapter.addItem(getResources().getString(R.string.Profile));
        mAdapter.addItem(getResources().getString(R.string.Video));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        mAdapter.addItem("Alternate Source");
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Recommended));
        mAdapter.addItem("One Tap Video Download");
        mAdapter.addItem("Zoom For Instagram");

        listView.setAdapter(mAdapter);
    }
}
