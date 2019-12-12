package com.ihelp101.instagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


public class FileFormat extends AppCompatActivity {

    static DragNDropListView listView;
    static AdapterDragFileDate mAdapter;
    static Context mContext;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);

        listView = (DragNDropListView) findViewById(R.id.listdrag);
        mContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateListView();
    }

    static void updateListView() {
        String[] lists;
        String list;

        try {
            list = Helper.getSetting("FileFormat");

            if (Helper.getSetting("FileFormat").equals("Instagram")) {
                list = "Username_MediaID_UserID";
            }

            lists = list.split("_");

            mAdapter = new AdapterDragFileDate(mContext, R.id.text);

            for (String name : lists) {
                mAdapter.addItem(name);
            }

            listView.setDragNDropAdapter(mAdapter);
        } catch (Exception e) {
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("+").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("+")) {

            String list = "Username_MediaID_Date_UserID";

            if (Helper.getSetting("FileFormat").equals("Instagram")) {
                String fileFormat = "Username_MediaID_UserID";

                for (String string : fileFormat.split("_")) {
                    list = list.replace(string + "_", "");
                    list = list.replace(string, "");
                }
            } else {
                String fileFormat = Helper.getSetting("FileFormat");

                for (String string : fileFormat.split("_")) {
                    list = list.replace(string + "_", "");
                    list = list.replace(string, "");
                }
            }

            final String[] actualStringString = list.split("_");

            if (!list.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(actualStringString, 0, null)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                List list = mAdapter.mData;
                                list.add(actualStringString[((AlertDialog) dialog).getListView().getCheckedItemPosition()]);

                                String saveString = "";

                                for (int i = 0; i < list.size(); i++) {
                                    if (saveString.isEmpty()) {
                                        saveString = list.get(i).toString();
                                    } else {
                                        saveString = saveString + "_" + list.get(i).toString();
                                    }
                                }

                                Helper.setSetting("FileFormat", saveString);
                                updateListView();
                            }
                        }).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
