package com.ihelp101.instagram;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends ListActivity {

    private ListView mAdapter;
    private String version = "123";
    private static int FILE_CODE = 0;
    String SaveLocation = "None";

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
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

            String savedVersion = "2";

            if (!Hooks[0].equals("")) {
                savedVersion = Hooks[0];
            }

            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Updated), Toast.LENGTH_LONG);

            String[] html = result.split("<p>");

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
                    if (version.equals(savedVersion) && data.contains(text.toString())) {
                        toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Latest), Toast.LENGTH_LONG);
                    } else {
                        Hooks(data);
                    }
                    count = 69;
                } else {
                    if (count == max) {
                        System.out.println("Trying default hook!");
                        String fallback = html[1];
                        fallback = fallback.replace("<p>", "");
                        fallback = fallback.replace("</p>", "");
                        Hooks(fallback);
                        toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Hooks_Updated), Toast.LENGTH_LONG);
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
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Visibility));
        mAdapter.addItem(UiUtils.getActivityVisibleInDrawer(Main.this) ? getResources().getString(R.string.Hide) : getResources().getString(R.string.Show));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
        mAdapter.addItem(getResources().getString(R.string.Image));
        mAdapter.addItem(getResources().getString(R.string.Video));
        mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        setListAdapter(mAdapter);

        android.widget.ListView lv = (android.widget.ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                if (mAdapter.getItemViewType(position) != ListView.TYPE_SEPARATOR) {
                    int Position = mAdapter.getPosition(position);
                    if (Position == 0) {

                        String visible = UiUtils.getActivityVisibleInDrawer(Main.this) ? getResources().getString(R.string.Hide) : getResources().getString(R.string.Show);
                        if (visible.equals(getResources().getString(R.string.Hide))) {
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
                                    setListAdapter(mAdapter);
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            UiUtils.setActivityVisibleInDrawer(Main.this, true);
                            mAdapter = new ListView(Main.this);
                            mAdapter.addSectionHeaderItem(getResources().getString(R.string.Visibility));
                            mAdapter.addItem(UiUtils.getActivityVisibleInDrawer(Main.this) ? getResources().getString(R.string.Hide) : getResources().getString(R.string.Show));
                            mAdapter.addSectionHeaderItem(getResources().getString(R.string.Save));
                            mAdapter.addItem(getResources().getString(R.string.Image));
                            mAdapter.addItem(getResources().getString(R.string.Video));
                            mAdapter.addSectionHeaderItem(getResources().getString(R.string.Update));
                            mAdapter.addItem("GitHub");
                            mAdapter.addItem("Pastebin");
                            setListAdapter(mAdapter);
                        }
                    }
                    if (Position == 1) {
                        SaveLocation = "Image";
                        Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                        startActivityForResult(i, FILE_CODE);
                    }
                    if (Position == 2) {
                        SaveLocation = "Video";
                        Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                        i.putExtra(com.ihelp101.instagram.FilePickerActivity.EXTRA_MODE, com.ihelp101.instagram.FilePickerActivity.MODE_DIR);
                        startActivityForResult(i, FILE_CODE);
                    }
                    if (Position == 3) {
                        new RequestTask().execute("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
                    }
                    if (Position == 4) {
                        new RequestTask().execute("http://pastebin.com/raw.php?i=sTXbUFcx");
                    }
                }
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
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.I_LocationChanged), Toast.LENGTH_LONG);
                } else {
                    toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.V_LocationChanged), Toast.LENGTH_LONG);
                }
                SaveLocation = "None";
            } else {
                toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Incorrect_Location), Toast.LENGTH_LONG);

                Intent i = new Intent(com.ihelp101.instagram.Main.this, com.ihelp101.instagram.FilePickerActivity.class);
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
        try {
            File root = new File(Environment.getExternalStorageDirectory(), ".Instagram");
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