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


        updateListView();
    }

    static void updateListView() {
        String[] lists;
        String list;

        try {
            list = Helper.getSetting("FileFormat");

            if (Helper.getSetting("FileFormat").equals("Instagram")) {
                list = "Username_MediaID_ItemID";

                if (!Helper.getSetting("File").equals("Instagram")) {
                    list = "Username_MediaID_Date";
                    Helper.setSetting("FileFormat", list);
                }
            }

            if (!Helper.getSetting("File").equals("Instagram")) {
                list = list.replace("ItemID", "Date");
            } else {
                list = list.replace("Date", "ItemID");
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
}
