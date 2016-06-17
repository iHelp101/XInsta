package com.ihelp101.instagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


public class Date extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle drawerToggle;
    static DragNDropListView listView;
    static AdapterDrag mAdapter;
    static Context mContext;

    String hour24;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);

        listView = (DragNDropListView) findViewById(R.id.listdrag);
        mContext = getApplicationContext();

        hour24 = "24 " + getResources().getString(R.string.Hour);

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

        drawerLayout.setDrawerListener(drawerToggle);

        setupNav();
        updateListView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("+").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        String clicked = (String) menuItem.getTitle();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawers();

        if (clicked.equals("+")) {
            String originalString = Helper.getData(getApplicationContext());

            String[] lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = originalString.split(";");
            }

            for (String name : originalString.split(";")) {
                for (String name2 : lists) {
                    if (name.equals(name2)) {
                        originalString = originalString.replace(name + ";", "");
                    }
                }
            }

            final String[] finalString = originalString.split(";");

            if (!finalString[0].equals("")) {
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(finalString, 0, null)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                ArrayList<String> arrayList = null;
                                arrayList.add(finalString[((AlertDialog) dialog).getListView().getCheckedItemPosition()]);
                                String saveString = "";

                                for (int i = 0; i < arrayList.size(); i++) {
                                    saveString = saveString + arrayList.get(i) + ";";
                                }

                                Helper.setSetting("Date", saveString);

                                setupNav();
                                updateListView();
                            }
                        }).show();
            }
        }

        if (clicked.equals(getResources().getString(R.string.Change))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Date.this);

            java.util.Date date = new java.util.Date(System.currentTimeMillis());
            TimeZone timeZone = TimeZone.getDefault();
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            format.setTimeZone(timeZone);
            String dateFormat = format.format(date);

            final CharSequence items[] = {dateFormat, dateFormat.replace("/", "-"), dateFormat.replace("/", ".")};

            int itemSelected;
            if (Helper.getSetting("Seperator").equals("Instagram") || Helper.getSetting("Seperator").equals("/")) {
                itemSelected = 0;
            } else if (Helper.getSetting("Seperator").equals("-")) {
                itemSelected = 1;
            } else if (Helper.getSetting("Seperator").equals(".")) {
                itemSelected = 2;
            } else {
                itemSelected = 0;
            }

            builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        Helper.setSetting("Seperator", String.valueOf(items[which].toString().replaceAll("[0-9]", "").charAt(0)));
                        dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        if (clicked.equals(getResources().getString(R.string.Reset))) {
            String originalString = Helper.getData(getApplicationContext());

            Helper.setSetting("Date", originalString);

            setupNav();
            updateListView();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().equals("+")) {
            String originalString = Helper.getData(getApplicationContext());

            String[] lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = originalString.split(";");
            }

            for (String name : originalString.split(";")) {
                for (String name2 : lists) {
                    if (name.equals(name2)) {
                        originalString = originalString.replace(name + ";", "");
                    }
                }
            }

            final String[] finalString = originalString.split(";");

            if (!finalString[0].equals("")) {
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(finalString, 0, null)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                List list = AdapterDrag.mData;
                                list.add(finalString[((AlertDialog) dialog).getListView().getCheckedItemPosition()]);

                                String saveString = "";

                                for (int i = 0; i < list.size(); i++) {
                                    saveString = saveString + list.get(i) + ";";
                                }

                                Helper.setSetting("Date", saveString);

                                setupNav();
                                updateListView();
                            }
                        }).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    void setupNav() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.date);
        navigationView.setNavigationItemSelectedListener(this);

        for (int menuSize = 0; menuSize < navigationView.getMenu().size();menuSize++) {
            for (int subMenuSize = 0; subMenuSize < navigationView.getMenu().getItem(menuSize).getSubMenu().size(); subMenuSize++) {

                final MenuItem menuItem = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize);
                final String menuTitle = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize).getTitle().toString();

                final SwitchCompat item = (SwitchCompat) menuItem.getActionView();

                if (hour24.contains(menuItem.getTitle().toString())) {
                    menuItem.setTitle(hour24);
                    if (Helper.getSettings("Hour")) {
                        item.setChecked(true);
                    }
                }

                try {
                    item.setOnClickListener(null);
                    item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String clicked = menuTitle;
                            if (hour24.contains(clicked)) {
                                Helper.setSetting("Hour", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(getResources().getString(R.string.Change))) {
                                Helper.setSetting("Seperator", Boolean.toString(item.isChecked()));
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    static void updateListView() {
        String[] lists;

        try {
            lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = Helper.getData(mContext).split(";");
            }

            mAdapter = new AdapterDrag(mContext, R.id.text);

            for (String name : lists) {
                mAdapter.addItem(name);
            }

            listView.setDragNDropAdapter(mAdapter);
        } catch (Exception e) {
        }
    }
}
