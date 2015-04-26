package com.ihelp101.instagram;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity extends android.app.Activity {

    private int version = 123;
    private static int FILE_CODE = 0;
    String SaveLocation = "None";
    String Code = "Missing";

    Expandable listAdapter;
    ExpandableListView ListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        ListView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new Expandable(this, listDataHeader, listDataChild);

        ListView.setAdapter(listAdapter);

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("App Visibility");
        listDataHeader.add("Save Locations");
        listDataHeader.add("Reset Save Locations");
        listDataHeader.add("Update Hooks");

        List<String> appVisibility = new ArrayList<String>();
        appVisibility.add(UiUtils.getActivityVisibleInDrawer(Activity.this) ? "Hide App" : "Show App");

        List<String> saveLocation = new ArrayList<String>();
        saveLocation.add("Image Save Location");
        saveLocation.add("Video Save Location");

        List<String> revertSave = new ArrayList<String>();
        revertSave.add("Image Save Location ");
        revertSave.add("Video Save Location ");

        List<String> updateHooks = new ArrayList<String>();
        updateHooks.add("1st Source (Pastebin)");
        updateHooks.add("2nd Source (GitHub)");

        listDataChild.put(listDataHeader.get(0), appVisibility);
        listDataChild.put(listDataHeader.get(1), saveLocation);
        listDataChild.put(listDataHeader.get(2), revertSave);
        listDataChild.put(listDataHeader.get(3),updateHooks);

        ListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                String selectedFromList = "" +listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                if (selectedFromList.equals("1st Source (Pastebin)")) {
                    getHooks("http://pastebin.com/raw.php?i=sTXbUFcx");
                }

                if (selectedFromList.equals("2nd Source (GitHub)")) {
                    getHooks("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                }

                if (selectedFromList.equals("Hide App")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity.this);
                    builder.setMessage("You can access the app via the Xposed Installer once it has been hidden.");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UiUtils.setActivityVisibleInDrawer(Activity.this, false);

                            List<String> appVisibility = new ArrayList<String>();
                            appVisibility.add(UiUtils.getActivityVisibleInDrawer(Activity.this) ? "Hide App" : "Show App");
                            listDataChild.put(listDataHeader.get(0), appVisibility);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                if (selectedFromList.equals("Image Save Location")) {
                    SaveLocation = "Image";
                    Intent i = new Intent(com.ihelp101.instagram.Activity.this, com.ihelp101.instagram.FilePickerActivity.class);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                    startActivityForResult(i, FILE_CODE);
                }

                if (selectedFromList.equals("Image Save Location ")) {
                    RevertSaveLocation("Image.txt");
                }

                if (selectedFromList.equals("Video Save Location ")) {
                    RevertSaveLocation("Video.txt");
                }

                if (selectedFromList.equals("Show App")) {
                    UiUtils.setActivityVisibleInDrawer(Activity.this, true);

                    List<String> appVisibility = new ArrayList<String>();
                    appVisibility.add(UiUtils.getActivityVisibleInDrawer(Activity.this) ? "Hide App" : "Show App");
                    listDataChild.put(listDataHeader.get(0), appVisibility);
                    listAdapter.notifyDataSetChanged();
                }

                if (selectedFromList.equals("Video Save Location")) {
                    SaveLocation = "Video";
                    Intent i = new Intent(com.ihelp101.instagram.Activity.this, com.ihelp101.instagram.FilePickerActivity.class);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                    startActivityForResult(i, FILE_CODE);
                }
                return false;
            }
        });
    }


    public void RevertSaveLocation(String fileName) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
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
            toast = Toast.makeText(getApplicationContext(), "Image save location reset to default.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(getApplicationContext(), "Video save location reset to default.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
        }

        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public void getHooks(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.packageName.equals("com.instagram.android")) {
                version = p.versionCode;
                String fix = Integer.toString(version);
                fix = fix.substring(0, fix.length() - 1);
                version = Integer.parseInt(fix);
            }
        }

        StringBuilder total = null;

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(url);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HttpEntity ht = response.getEntity();

            BufferedHttpEntity buf = null;
            try {
                buf = new BufferedHttpEntity(ht);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            InputStream is = null;
            try {
                is = buf.getContent();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            total = new StringBuilder();
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Broke");
        }

        File file = new File(Environment.getExternalStorageDirectory() + "/.Instagram/Hooks.txt");

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

        String[] Hooks = text.toString().split(";");

        int savedVersion = 2;

        if (!Hooks[0].equals("")) {
            savedVersion = Integer.parseInt(Hooks[0]);
        }

        Toast toast = null;

        String hooks = total.toString();
        String[] html = hooks.split("<p>");

        int count = 0;
        int max = 0;
        for (String data : html) {
            max++;
        }

        for (String data : html) {
            count++;
            Code = Integer.toString(version);

            String finalCheck = "123";

            if (!data.isEmpty()) {
                String[] PasteVersion = data.split(";");
                finalCheck = PasteVersion[0];
            }
            if (Code.equals(finalCheck) && !data.isEmpty()) {
                data = data.replace("<p>", "");
                data = data.replace("</p>", "");
                if (version == savedVersion && data.equals(text.toString())) {
                    toast = Toast.makeText(getApplicationContext(), "You already have the latest hooks", Toast.LENGTH_LONG);
                } else {
                    Hooks(data);
                    toast = Toast.makeText(getApplicationContext(), "Hooks have been updated.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
                }
                count = 69;
            } else {
                if (count == max) {
                    System.out.println("Trying default hook!");
                    String fallback = html[1];
                    fallback = fallback.replace("<p>", "");
                    fallback = fallback.replace("</p>", "");
                    Hooks(fallback);
                    toast = Toast.makeText(getApplicationContext(), "Hooks have been updated.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
                }
            }
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
        if (requestCode == FILE_CODE && resultCode == com.ihelp101.instagram.Activity.RESULT_OK) {
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
                    File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
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
                if (SaveLocation.equals("Image")) {
                    toast = Toast.makeText(getApplicationContext(), "Image save location updated!.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getApplicationContext(), "Video save location updated!.\nPlease kill the Instagram app.", Toast.LENGTH_LONG);
                }
                SaveLocation = "None";
            } else {
                toast = Toast.makeText(getApplicationContext(), "Unable to save here.\nPlease select another location.", Toast.LENGTH_LONG);

                Intent i = new Intent(com.ihelp101.instagram.Activity.this, com.ihelp101.instagram.FilePickerActivity.class);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                startActivityForResult(i, FILE_CODE);
            }

            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    public void Hooks(String data) {
        String[] split = data.split(";");

        String string = "";

        for (String hook : split) {
            if (!hook.equals(null)) {
                string = string + hook + ";";
            }
        }


        try {
            File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "Hooks.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(string);
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }

        File file = new File(Environment.getExternalStorageDirectory(), ".Instagram/Image.txt");
        if(!file.exists()) {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
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

        file = new File(Environment.getExternalStorageDirectory(), ".Instagram/Video.txt");
        if(!file.exists()) {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
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
}