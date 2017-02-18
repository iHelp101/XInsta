package com.ihelp101.instagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


public class Hooks extends AppCompatActivity {

    ActionBarDrawerToggle drawerToggle;
    ListView listView;
    AdapterHooks mAdapter;
    Context mContext;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hooks);

        listView = (ListView) findViewById(R.id.list);
        mContext = getApplicationContext();
        updateListView();
    }

    void edit(final String hook, final String hooksArray, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(hook.split("-")[0].trim());

        final EditText input = new EditText(this);
        input.setText(hook.split("-")[1].trim());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hooksSave = hooksArray.replace(hook.split("-")[1].trim() + ";", input.getText().toString() + ";");
                Helper.setSetting("Hooks", hooksSave);
                mAdapter.updateItem(hook.split("-")[0].trim() + " - " + input.getText().toString(), i);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void updateListView() {
        String HooksArray[] = Helper.getSetting("Hooks").split(";");
        mAdapter = new AdapterHooks(Hooks.this);
        mAdapter.addSectionHeaderItem("Hooks");

        try {
            mAdapter.addItem("Feed OnClick Class - " +HooksArray[1]);
            mAdapter.addItem("Feed Class - " +HooksArray[2]);
            mAdapter.addItem("User Class - " +HooksArray[4]);
            mAdapter.addItem("Feed Inject Class (Old) - " +HooksArray[5]);
            mAdapter.addItem("Username Field - " +HooksArray[16]);
            mAdapter.addItem("Full Username Field - " +HooksArray[17]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Image URL Class - " +HooksArray[18]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Post Media ID Field - " +HooksArray[20]);
            mAdapter.addItem("Comment Class - " +HooksArray[21]);
            mAdapter.addItem("Comment Method - " +HooksArray[22]);
            mAdapter.addItem("Comment Class 2 - " +HooksArray[23]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Dialog Inject Class - " +HooksArray[24]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Profile Inject Class (Old) - " +HooksArray[29]);
            mAdapter.addItem("Profile OnClick Class - " +HooksArray[30]);
            mAdapter.addItem("Follow Indicator Byte Field - " +HooksArray[31]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Like Class - " +HooksArray[35]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Suggestion Class - " +HooksArray[37]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Follow Indicator Class - " +HooksArray[41]);
            mAdapter.addItem("Follow Indicator Method - " +HooksArray[42]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Date Format Class - " +HooksArray[43]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Push Notifications Class - " +HooksArray[44]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Video URL Class - " +HooksArray[45]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Stories OnClick Class - " +HooksArray[46]);
            mAdapter.addItem("Stories Inject Class (Old) - " +HooksArray[47]);
            mAdapter.addItem("Stories Help Class (Links To Feed) - " +HooksArray[48]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Stories Video Timer Class (Old) - " +HooksArray[49]);
            mAdapter.addItem("Stories Video Timer Method (Old) - " +HooksArray[50]);
            mAdapter.addItem("Stories Image Timer Class - " +HooksArray[51]);
            mAdapter.addItem("Stories Image Timer Method - " +HooksArray[52]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Force Touch/Peek Feed OnClick Class - " +HooksArray[53]);
            mAdapter.addItem("Force Touch/Peek Feed Inject Class (Old) - " +HooksArray[54]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Follow List Class - " +HooksArray[55]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Copy Link Class - " +HooksArray[57]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Tagged Users Class - " +HooksArray[58]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Slide/Swipe To Navigate Class - " +HooksArray[60]);
            mAdapter.addItem("Slide/Swipe To Navigate Method - " +HooksArray[61]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Stories Gallery Class - " +HooksArray[72]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Search Tagged Class - " +HooksArray[73]);
            mAdapter.addItem("Search Location Class - " +HooksArray[74]);
            mAdapter.addItem("Search Username Class - " +HooksArray[75]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Sponsored Class - " +HooksArray[76]);
            mAdapter.addItem("Sponsored Injected Field - " +HooksArray[77]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Video Like Count Class - " +HooksArray[78]);
            mAdapter.addItem("Video Like Count Int Field - " +HooksArray[79]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Stories Video Timer Class (ExoPlayer) - " +HooksArray[80]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Search Top Class - " +HooksArray[81]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Pin/Saved Class - " +HooksArray[82]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            mAdapter.addItem("Pin/Saved OnClick Class - " +HooksArray[83]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edit(mAdapter.getItem(i), Helper.getSetting("Hooks"), i);
            }
        });
    }
}
