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


public class Date extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle drawerToggle;
    static DragNDropListView listView;
    static AdapterDrag mAdapter;
    static Context mContext;

    String hour24;
    static String day;
    static String hour;
    static String minute;
    static String month;
    static String second;
    static String space;
    static String year;
    static String AM;

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

        try {
            day = Helper.getResourceString(getApplicationContext(), R.string.Day);
            hour = Helper.getResourceString(getApplicationContext(), R.string.Hour);
            minute = Helper.getResourceString(getApplicationContext(), R.string.Minute);
            month = Helper.getResourceString(getApplicationContext(), R.string.Month);
            second = Helper.getResourceString(getApplicationContext(), R.string.Second);
            space = Helper.getResourceString(getApplicationContext(), R.string.Space);
            year = Helper.getResourceString(getApplicationContext(), R.string.Year);
            AM = Helper.getResourceString(getApplicationContext(), R.string.AM);
        } catch (Throwable t) {
            day = getResources().getString(R.string.Day);
            hour = getResources().getString(R.string.Hour);
            minute = getResources().getString(R.string.Minute);
            month = getResources().getString(R.string.Month);
            second = getResources().getString(R.string.Second);
            space = getResources().getString(R.string.Space);
            year = getResources().getString(R.string.Year);
            AM = getResources().getString(R.string.AM);
        }

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

        if (clicked.equals(getResources().getString(R.string.Change))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Date.this);

            final java.util.Date date = new java.util.Date(System.currentTimeMillis());
            TimeZone timeZone = TimeZone.getDefault();
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            format.setTimeZone(timeZone);
            String dateFormat = format.format(date);

            final CharSequence items[] = {dateFormat, dateFormat.replace("/", "-"), dateFormat.replace("/", ".")};

            int itemSelected;
            if (Helper.getSetting("Separator").equals("Instagram") || Helper.getSetting("Separator").equals("/")) {
                itemSelected = 0;
            } else if (Helper.getSetting("Separator").equals("-")) {
                itemSelected = 1;
            } else if (Helper.getSetting("Separator").equals(".")) {
                itemSelected = 2;
            } else {
                itemSelected = 0;
            }

            builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String dateFormat = Helper.getSetting("Date");

                    if (!Helper.getSetting("Date").contains("/")){
                        dateFormat = dateFormat.replace(Helper.getSetting("Separator"), String.valueOf(items[which].toString().replaceAll("[0-9]", "").charAt(0)));
                    } else {
                        dateFormat = dateFormat.replace("/", String.valueOf(items[which].toString().replaceAll("[0-9]", "").charAt(0)));
                    }

                    if (dateFormat.substring(0,1).equals("/")) {
                        dateFormat = dateFormat.substring(1);
                    }

                    Helper.setSetting("Date", dateFormat);
                    Helper.setSetting("Separator", String.valueOf(items[which].toString().replaceAll("[0-9]", "").charAt(0)));
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        if (clicked.equals(getResources().getString(R.string.Reset))) {
            String originalString = Helper.getData(getApplicationContext());
            String saveString = "";

            for (int i = 0; i < originalString.split(";").length; i++) {
                String dateOption = originalString.split(";")[i];
                if (dateOption.equals(day)) {
                    dateOption = "/dd";
                }
                if (dateOption.equals(hour)) {
                    if (Helper.getSettings("Hour")) {
                        dateOption = "HH:";
                    } else {
                        dateOption = "hh:";
                    }
                }
                if (dateOption.equals(minute)) {
                    dateOption = "mm:";
                }
                if (dateOption.equals(month)) {
                    dateOption = "/MM";
                }
                if (dateOption.equals(second)) {
                    dateOption = "ss:";
                }
                if (dateOption.equals(space)) {
                    dateOption = " ";
                }
                if (dateOption.equals(year)) {
                    dateOption = "/yyyy";
                }
                if (dateOption.equals(AM)) {
                    dateOption = "a";
                }
                saveString = saveString + dateOption + ";";
            }

            if (!Helper.getSetting("Separator").equals("Instagram")) {
                saveString = saveString.replace("/", Helper.getSetting("Separator"));
            }

            if (saveString.substring(0,1).equals("/")) {
                saveString = saveString.substring(1);
            }

            Helper.setSetting("Date", saveString);

            setupNav();
            updateListView();
        }

        if (clicked.equals("Disable")) {
            Helper.setSetting("Date", "Instagram");
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().equals("+")) {
            String originalString;

            if (Helper.getSettings("Hour")) {
                if (!Helper.getSetting("Separator").equals("Instagram")) {
                    originalString = "/MM;/dd;/yyyy; ;HH:;mm:;ss:;a;";
                    originalString = originalString.replace("/", Helper.getSetting("Separator"));
                } else {
                    originalString = "/MM;/dd;/yyyy; ;HH:;mm:;ss:;a;";
                }
            } else {
                if (!Helper.getSetting("Separator").equals("Instagram")) {
                    originalString = "/MM;/dd;/yyyy; ;hh:;mm:;ss:;a;";
                    originalString = originalString.replace("/", Helper.getSetting("Separator"));
                } else {
                    originalString = "/MM;/dd;/yyyy; ;hh:;mm:;ss:;a;";
                }
            }

            String actualString = Helper.getData(getApplicationContext());
            String separator = "/";

            String[] lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = originalString.split(";");
            }

            for (String name : originalString.split(";")) {
                for (String name2 : lists) {
                    if (name.equals(name2)) {
                        originalString = originalString.replace(name + ";", "");

                        if (!Helper.getSetting("Separator").equals("Instagram")) {
                            separator = Helper.getSetting("Separator");
                        }

                        if (name.equals(separator + "dd")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Day);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Day);
                            }
                        }
                        if (name.equals("hh:") || name.equals("HH:")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Hour);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Hour);
                            }
                        }
                        if (name.equals("mm:")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Minute);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Minute);
                            }
                        }
                        if (name.equals(separator + "MM")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Month);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Month);
                            }
                        }
                        if (name.equals("ss:")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Second);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Second);
                            }
                        }
                        if (name.equals(" ")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Space);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Space);
                            }
                        }
                        if (name.equals(separator + "yyyy")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.Year);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.Year);
                            }
                        }
                        if (name.equals("a")) {
                            try {
                                name = Helper.getResourceString(mContext, R.string.AM);
                            } catch (Throwable t) {
                                name = mContext.getResources().getString(R.string.AM);
                            }
                        }
                        actualString = actualString.replace(name + ";", "");
                    }
                }
            }

            final String[] finalString = originalString.split(";");
            final String[] actualStringString = actualString.split(";");

            if (!finalString[0].equals("")) {
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(actualStringString, 0, null)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                                List list = AdapterDrag.mData;
                                list.add(actualStringString[((AlertDialog) dialog).getListView().getCheckedItemPosition()]);

                                String saveString = "";

                                for (int i = 0; i < list.size(); i++) {
                                    String dateOption = list.get(i).toString();
                                    if (dateOption.equals(day)) {
                                        dateOption = "/dd";
                                    }
                                    if (dateOption.equals(hour)) {
                                        if (Helper.getSettings("Hour")) {
                                            dateOption = "HH:";
                                        } else {
                                            dateOption = "hh:";
                                        }
                                    }
                                    if (dateOption.equals(minute)) {
                                        dateOption = "mm:";
                                    }
                                    if (dateOption.equals(month)) {
                                        dateOption = "/MM";
                                    }
                                    if (dateOption.equals(second)) {
                                        dateOption = "ss:";
                                    }
                                    if (dateOption.equals(space)) {
                                        dateOption = " ";
                                    }
                                    if (dateOption.equals(year)) {
                                        dateOption = "/yyyy";
                                    }
                                    if (dateOption.equals(AM)) {
                                        dateOption = "a";
                                    }
                                    saveString = saveString + dateOption + ";";
                                }

                                if (!Helper.getSetting("Separator").equals("Instagram")) {
                                    saveString = saveString.replace("/", Helper.getSetting("Separator"));
                                }

                                if (saveString.substring(0,1).equals("/")) {
                                    saveString = saveString.substring(1);
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
                            if (hour24.contains(menuTitle)) {
                                Helper.setSetting("Hour", Boolean.toString(item.isChecked()));
                                String dateFormat = Helper.getSetting("Date");
                                if (item.isChecked()) {
                                    dateFormat = dateFormat.replace("hh:", "HH:");
                                } else {
                                    dateFormat = dateFormat.replace("HH:", "hh:");
                                }

                                if (dateFormat.substring(0,1).equals("/")) {
                                    dateFormat = dateFormat.substring(1);
                                }
                                Helper.setSetting("Date", dateFormat);
                            }

                            if (menuTitle.equals(getResources().getString(R.string.Change))) {
                                Helper.setSetting("Separator", Boolean.toString(item.isChecked()));
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
                if (name.contains("dd")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Day);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Day);
                    }
                }
                if (name.equals("hh:") || name.equals("HH:")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Hour);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Hour);
                    }
                }
                if (name.equals("mm:")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Minute);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Minute);
                    }
                }
                if (name.contains("MM")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Month);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Month);
                    }
                }
                if (name.equals("ss:")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Second);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Second);
                    }
                }
                if (name.equals(" ")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Space);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Space);
                    }
                }
                if (name.contains("yyyy")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.Year);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.Year);
                    }
                }
                if (name.equals("a")) {
                    try {
                        name = Helper.getResourceString(mContext, R.string.AM);
                    } catch (Throwable t) {
                        name = mContext.getResources().getString(R.string.AM);
                    }
                }
                mAdapter.addItem(name);
            }

            listView.setDragNDropAdapter(mAdapter);
        } catch (Exception e) {
        }
    }
}
