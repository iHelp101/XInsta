package com.ihelp101.instagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;

public class Settings extends Preferences {

    static Context mContext;
    static Context nContext;

    static String automaticUpdate;
    static String changeApp;
    static String changeOrder;
    static String changeVersion;
    static String comment;
    static String contact;
    static String customization;
    static String date;
    static String defaultSource;
    static String discovery;
    static String errorLog;
    static String file;
    static String fileFormat;
    static String fileUsername;
    static String fileURL;
    static String folder;
    static String follow;
    static String like;
    static String liked;
    static String lockFeed;
    static String logging;
    static String experimental;
    static String hide;
    static String hidePost;
    static String misc;
    static String notifHide;
    static String pass;
    static String pin;
    static String push;
    static String resolution;
    static String searchHistory;
    static String saveHooks;
    static String slide;
    static String story;
    static String storyHide;
    static String storyPrivacy;
    static String translate;
    static String videoLikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        nContext = Settings.this;

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
                PreferenceCategory lol = (PreferenceCategory) getPreferenceScreen().getPreference(x);
                for(int y = 0; y < lol.getPreferenceCount(); y++){
                    Preference pref = lol.getPreference(y);
                    Preference menuItem = pref;
                    com.ihelp101.instagram.Pref item = null;
                    
                    if (pref instanceof com.ihelp101.instagram.Pref) {
                        item = (com.ihelp101.instagram.Pref) pref;
                    }

                    String menuTitle = pref.getTitle().toString();
                    
                    if (menuTitle.equals(getResources().getString(R.string.Automatically))) {
                        try {
                            automaticUpdate = Helper.getResourceString(mContext, R.string.Automatically);
                        } catch (Throwable t) {
                            automaticUpdate = getResources().getString(R.string.Automatically);
                        }

                        menuItem.setTitle(automaticUpdate);
                        if (Helper.getSettings("Update")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.ChangeApp))) {
                        try {
                            changeApp = Helper.getResourceString(mContext, R.string.ChangeApp);
                        } catch (Throwable t) {
                            changeApp = getResources().getString(R.string.ChangeApp);
                        }

                        menuItem.setTitle(changeApp);

                        if (Helper.getSettings("AppIcon")) {
                            item.setChecked(true);
                            Helper.setIcon(mContext, "IntroActivity-Alias", false);
                            Helper.setIcon(mContext, "IntroActivity-Alias2", true);
                        } else {
                            item.setChecked(false);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.ChangeOrder))) {
                        try {
                            changeOrder = Helper.getResourceString(mContext, R.string.ChangeOrder);
                        } catch (Throwable t) {
                            changeOrder = getResources().getString(R.string.ChangeOrder);
                        }

                        menuItem.setTitle(changeOrder);
                        if (Helper.getSettings("Order")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Check))) {
                        try {
                            changeVersion = Helper.getResourceString(mContext, R.string.Check);
                        } catch (Throwable t) {
                            changeVersion = getResources().getString(R.string.Check);
                        }
                        menuItem.setTitle(changeVersion);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Comment))) {
                        try {
                            comment = Helper.getResourceString(mContext, R.string.Comment);
                        } catch (Throwable t) {
                            comment = getResources().getString(R.string.Comment);
                        }

                        menuItem.setTitle(comment);
                        if (Helper.getSettings("Comment")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Contact))) {
                        try {
                            contact = Helper.getResourceString(mContext, R.string.Contact);
                        } catch (Throwable t) {
                            contact = getResources().getString(R.string.Contact);
                        }
                        menuItem.setTitle(contact);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Customization))) {
                        try {
                            customization = Helper.getResourceString(mContext, R.string.Customization);
                        } catch (Throwable t) {
                            customization = getResources().getString(R.string.Customization);
                        }
                        menuItem.setTitle(customization);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Date))) {
                        try {
                            date = Helper.getResourceString(mContext, R.string.Date);
                        } catch (Throwable t) {
                            date = getResources().getString(R.string.Date);
                        }
                        menuItem.setTitle(date);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Default))) {
                        try {
                            defaultSource = Helper.getResourceString(mContext, R.string.Default);
                        } catch (Throwable t) {
                            defaultSource = getResources().getString(R.string.Default);
                        }
                        menuItem.setTitle(defaultSource);
                    }

                    if (menuTitle.equals("Direct Message - Download")) {
                        if (Helper.getSettings("Disappearing")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Discovery))) {
                        try {
                            discovery = Helper.getResourceString(mContext, R.string.Discovery);
                        } catch (Throwable t) {
                            discovery = getResources().getString(R.string.Discovery);
                        }
                        menuItem.setTitle(discovery);
                        if (Helper.getSettings("DiscoveryHide")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals("Direct Message - Download")) {
                        if (Helper.getSettings("Disappearing")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Error_log))) {
                        try {
                            errorLog = Helper.getResourceString(mContext, R.string.Error_log);
                        } catch (Throwable t) {
                            errorLog = getResources().getString(R.string.Error_log);
                        }
                        menuItem.setTitle(errorLog);
                        if (Helper.getSettings("Log")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.File))) {
                        try {
                            file = Helper.getResourceString(mContext, R.string.File);
                        } catch (Throwable t) {
                            file = getResources().getString(R.string.File);
                        }
                        menuItem.setTitle(file);

                        if (!Helper.getSetting("File").equals("Instagram")) {
                            menuItem.setSummary(menuItem.getSummary().toString().replace("Month/Day/Year", Helper.getSetting("File")));
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.FileFormat))) {
                        try {
                            fileFormat = Helper.getResourceString(mContext, R.string.FileFormat);
                        } catch (Throwable t) {
                            fileFormat = getResources().getString(R.string.FileFormat);
                        }
                        menuItem.setTitle(fileFormat);

                        if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                            menuItem.setSummary(menuItem.getSummary().toString().replace("Username_Date_MediaID", Helper.getSetting("FileFormat")));
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.FileUsername))) {
                        try {
                            fileUsername = Helper.getResourceString(mContext, R.string.FileUsername);
                        } catch (Throwable t) {
                            fileUsername = getResources().getString(R.string.FileUsername);
                        }
                        menuItem.setTitle(fileUsername);
                        if (Helper.getSettings("Username")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.FileURL))) {
                        try {
                            fileURL = Helper.getResourceString(mContext, R.string.FileURL);
                        } catch (Throwable t) {
                            fileURL = getResources().getString(R.string.FileURL);
                        }
                        menuItem.setTitle(fileURL);
                        if (Helper.getSettings("URLFileName")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Folder))) {
                        try {
                            folder = Helper.getResourceString(mContext, R.string.Folder);
                        } catch (Throwable t) {
                            folder = getResources().getString(R.string.Folder);
                        }
                        menuItem.setTitle(folder);
                        if (Helper.getSettings("Folder")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Follow))) {
                        try {
                            follow = Helper.getResourceString(mContext, R.string.Follow);
                        } catch (Throwable t) {
                            follow = getResources().getString(R.string.Follow);
                        }
                        menuItem.setTitle(follow);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.HidePost))) {
                        try {
                            hidePost = Helper.getResourceString(mContext, R.string.HidePost);
                        } catch (Throwable t) {
                            hidePost = getResources().getString(R.string.HidePost);
                        }
                        menuItem.setTitle(hidePost);
                        if (Helper.getSettings("SponsorPost")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Like))) {
                        try {
                            like = Helper.getResourceString(mContext, R.string.Like);
                        } catch (Throwable t) {
                            like = getResources().getString(R.string.Like);
                        }
                        menuItem.setTitle(like);
                        if (Helper.getSettings("Like")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Liked))) {
                        try {
                            liked = Helper.getResourceString(mContext, R.string.Liked);
                        } catch (Throwable t) {
                            liked = getResources().getString(R.string.Liked);
                        }
                        menuItem.setTitle(liked);
                        if (Helper.getSettings("LikePrivacy")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Logging))) {
                        try {
                            logging = Helper.getResourceString(mContext, R.string.Logging);
                        } catch (Throwable t) {
                            logging = getResources().getString(R.string.Logging);
                        }
                        menuItem.setTitle(logging);
                        if (Helper.getSettings("Log")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Experimental))) {
                        try {
                            experimental = Helper.getResourceString(mContext, R.string.Experimental);
                        } catch (Throwable t) {
                            experimental = getResources().getString(R.string.Experimental);
                        }
                        menuItem.setTitle(experimental);
                        if (Helper.getSettings("ExperimentalHooks")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Hide))) {
                        try {
                            hide = Helper.getResourceString(mContext, R.string.Hide);
                        } catch (Throwable t) {
                            hide = getResources().getString(R.string.Hide);
                        }
                        menuItem.setTitle(hide);

                        if (Helper.getSettings("HideApp")) {
                            item.setChecked(true);
                            Helper.setIcon(mContext, "IntroActivity-Alias", false);
                            Helper.setIcon(mContext, "IntroActivity-Alias2", false);
                        } else {
                            item.setChecked(false);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Misc))) {
                        try {
                            misc = Helper.getResourceString(mContext, R.string.Misc);
                        } catch (Throwable t) {
                            misc = getResources().getString(R.string.Misc);
                        }
                        menuItem.setTitle(misc);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.NotificationHide))) {
                        try {
                            notifHide = Helper.getResourceString(mContext, R.string.NotificationHide);
                        } catch (Throwable t) {
                            notifHide = getResources().getString(R.string.NotificationHide);
                        }
                        menuItem.setTitle(notifHide);
                        if (Helper.getSettings("Notification")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.the_not_so_big_but_big_button2))) {
                        try {
                            lockFeed = Helper.getResourceString(mContext, R.string.the_not_so_big_but_big_button2);
                        } catch (Throwable t) {
                            lockFeed = getResources().getString(R.string.the_not_so_big_but_big_button2);
                        }
                        menuItem.setTitle(lockFeed);
                        if (Helper.getSettings("Lock")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Pass))) {
                        try {
                            pass = Helper.getResourceString(mContext, R.string.Pass);
                        } catch (Throwable t) {
                            pass = getResources().getString(R.string.Pass);
                        }
                        menuItem.setTitle(pass);
                        if (Helper.getSettings("Pass")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Pin))) {
                        try {
                            pin = Helper.getResourceString(mContext, R.string.Pin);
                        } catch (Throwable t) {
                            pin = getResources().getString(R.string.Pin);
                        }
                        menuItem.setTitle(pin);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Push))) {
                        try {
                            push = Helper.getResourceString(mContext, R.string.Push);
                        } catch (Throwable t) {
                            push = getResources().getString(R.string.Push);
                        }
                        menuItem.setTitle(push);
                        if (Helper.getSettings("Push")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Resolution))) {
                        try {
                            resolution = Helper.getResourceString(mContext, R.string.Resolution);
                        } catch (Throwable t) {
                            resolution = getResources().getString(R.string.Resolution);
                        }
                        menuItem.setTitle(resolution);
                        if (Helper.getSettings("Resolution")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Search))) {
                        try {
                            searchHistory = Helper.getResourceString(mContext, R.string.Search);
                        } catch (Throwable t) {
                            searchHistory = getResources().getString(R.string.Search);
                        }
                        menuItem.setTitle(searchHistory);
                        if (Helper.getSettings("History")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.SaveHooks))) {
                        try {
                            saveHooks = Helper.getResourceString(mContext, R.string.SaveHooks);
                        } catch (Throwable t) {
                            saveHooks = getResources().getString(R.string.SaveHooks);
                        }
                        menuItem.setTitle(saveHooks);
                        if (Helper.getSettings("HookSave")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Slide))) {
                        try {
                            slide = Helper.getResourceString(mContext, R.string.Slide);
                        } catch (Throwable t) {
                            slide = getResources().getString(R.string.Sound);
                        }
                        menuItem.setTitle(slide);
                        if (Helper.getSettings("Slide")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Story))) {
                        try {
                            story = Helper.getResourceString(mContext, R.string.Story);
                        } catch (Throwable t) {
                            story = getResources().getString(R.string.Story);
                        }
                        menuItem.setTitle(story);
                        if (Helper.getSettings("Story")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.StoryHide))) {
                        try {
                            storyHide = Helper.getResourceString(mContext, R.string.StoryHide);
                        } catch (Throwable t) {
                            storyHide = getResources().getString(R.string.StoryHide);
                        }
                        menuItem.setTitle(storyHide);
                        if (Helper.getSettings("StoryHide")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.StoryPrivacy))) {
                        try {
                            storyPrivacy = Helper.getResourceString(mContext, R.string.StoryPrivacy);
                        } catch (Throwable t) {
                            storyPrivacy = getResources().getString(R.string.StoryPrivacy);
                        }
                        menuItem.setTitle(storyPrivacy);
                        if (Helper.getSettings("StoryPrivacy")) {
                            item.setChecked(true);
                        }
                    }

                    if (menuTitle.equals(getResources().getString(R.string.Translations))) {
                        try {
                            translate = Helper.getResourceString(mContext, R.string.Translations);
                        } catch (Throwable t) {
                            translate = getResources().getString(R.string.Translations);
                        }
                        menuItem.setTitle(translate);
                    }

                    if (menuTitle.equals(getResources().getString(R.string.VideoLikes))) {
                        try {
                            videoLikes = Helper.getResourceString(mContext, R.string.VideoLikes);
                        } catch (Throwable t) {
                            videoLikes = getResources().getString(R.string.VideoLikes);
                        }
                        menuItem.setTitle(videoLikes);

                        if (Helper.getSettings("VideoLikes")) {
                            item.setChecked(true);
                        } else {
                        }
                    }
                    
                    if (pref instanceof ListPreference) {
                        if (((ListPreference) pref).getDialogTitle().equals(Helper.getResourceString(mContext, R.string.Pin))) {
                            if (Helper.getSetting("Alternate").equals("Hold")) {
                                ((ListPreference) pref).setValueIndex(3);
                            }
                            if (Helper.getSetting("Alternate").equals("Double")) {
                                ((ListPreference) pref).setValueIndex(2);
                            }
                            if (Helper.getSetting("Alternate").equals("One")) {
                                ((ListPreference) pref).setValueIndex(1);
                            }
                        }

                        if (((ListPreference) pref).getDialogTitle().equals(Helper.getResourceString(mContext, R.string.File))) {
                            if (Helper.getSetting("File").equals("Year/Day/Month")) {
                                ((ListPreference) pref).setValueIndex(3);
                            }
                            if (Helper.getSetting("File").equals("Year/Month/Day")) {
                                ((ListPreference) pref).setValueIndex(2);
                            }
                            if (Helper.getSetting("File").equals("Month/Day/Year")) {
                                ((ListPreference) pref).setValueIndex(1);
                            }
                        }

                        if (((ListPreference) pref).getDialogTitle().equals(Helper.getResourceString(mContext, R.string.Default))) {
                            if (Helper.getSetting("Source").equals("Pastebin")) {
                                ((ListPreference) pref).setValueIndex(1);
                            }

                            if (Helper.getSetting("Source").equals("Alternate Source")) {
                                ((ListPreference) pref).setValueIndex(2);
                            }
                        }

                        if (((ListPreference) pref).getDialogTitle().equals("Download Options")) {
                            if (Helper.getSettings("OneTap")) {
                                ((ListPreference) pref).setValueIndex(1);
                            }

                            if (Helper.getSettings("Pass")) {
                                ((ListPreference) pref).setValueIndex(2);
                            }
                        }

                        pref.setOnPreferenceChangeListener(onPreferenceChangeListener);
                    } else if (pref instanceof com.ihelp101.instagram.Pref) {
                        pref.setOnPreferenceChangeListener(onPreferenceChangeListener);
                    } else {
                        pref.setOnPreferenceClickListener(onPreferenceClickListener);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(final Preference preference, Object o) {
            String clicked =  preference.getTitle().toString();

            if (preference instanceof  ListPreference) {

                if (clicked.equals("Download Options")) {
                    if (Integer.parseInt(o.toString()) == 0) {
                        Helper.setSetting("OneTap", "false");
                        Helper.setSetting("Pass", "false");
                    } else if (Integer.parseInt(o.toString()) == 1) {
                        showOneTap();
                    } else if (Integer.parseInt(o.toString()) == 2) {
                        Helper.setSetting("OneTap", "false");
                        Helper.setSetting("Pass", "true");
                    }
                }

                if (clicked.equals(Helper.getResourceString(mContext, R.string.File))) {
                    String value = ((ListPreference) preference).getEntries()[Integer.parseInt(o.toString())].toString();
                    Helper.setSetting("File", value);

                    if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                        String fileFormat = Helper.getSetting("FileFormat");
                        fileFormat = fileFormat.replace("UserID", "Date");
                        Helper.setSetting("FileFormat", fileFormat);
                    }
                }

                if (clicked.equals(Helper.getResourceString(mContext, R.string.Manual))) {
                    if (Integer.parseInt(o.toString()) == 0) {
                        showHooksPasteOption();
                    } else {
                        Intent myIntent = new Intent(mContext, Hooks.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(myIntent);
                    }
                }

                if (clicked.equals(Helper.getResourceString(mContext, R.string.Pin))) {
                    if (Integer.parseInt(o.toString()) == 0) {
                        Helper.setSetting("Alternate", "Instagram");
                    } else if (Integer.parseInt(o.toString()) == 1) {
                        Helper.setSetting("Alternate", "One");
                    } else if (Integer.parseInt(o.toString()) == 2) {
                        Helper.setSetting("Alternate", "Double");
                    } else if (Integer.parseInt(o.toString()) == 3) {
                        Helper.setSetting("Alternate", "Hold");
                    }
                }
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Automatically))) {
                Helper.setSetting("Update", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.ChangeApp))) {
                if (o.toString().equals("true")) {
                    Helper.setIcon(mContext, "IntroActivity-Alias", false);
                    Helper.setIcon(mContext, "IntroActivity-Alias2", true);
                } else {
                    Helper.setIcon(mContext, "IntroActivity-Alias", true);
                    Helper.setIcon(mContext, "IntroActivity-Alias2", false);
                }
                Helper.setSetting("AppIcon", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.ChangeOrder))) {
                Helper.setSetting("Order", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Comment))) {
                Helper.setSetting("Comment", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Custom))) {
                if (Integer.parseInt(o.toString()) == 0) {
                    Helper.setSetting("Language", "Instagram");
                } else {
                    Helper.setSetting("Language", Helper.getRawString(mContext));
                }
            }

            if (clicked.equals("Direct Message - Download")) {
                Helper.setSetting("Disappearing", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Discovery))) {
                Helper.setSetting("DiscoveryHide", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Error_log))) {
                Helper.setSetting("Log", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Folder))) {
                Helper.setSetting("Folder", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Like))) {
                Helper.setSetting("Like", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Liked))) {
                Helper.setSetting("LikePrivacy", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Experimental))) {
                Helper.setSetting("ExperimentalHooks", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.FileUsername))) {
                Helper.setSetting("Username", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.FileURL))) {
                Helper.setSetting("URLFileName", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Hide))) {
                if (o.toString().equals("true")) {
                    if (Helper.getSettings("AppIcon")) {
                        Helper.setIcon(mContext, "IntroActivity-Alias", false);
                        Helper.setIcon(mContext, "IntroActivity-Alias2", true);
                    } else {
                        Helper.setIcon(mContext, "IntroActivity-Alias", true);
                        Helper.setIcon(mContext, "IntroActivity-Alias2", false);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(nContext);

                    String okay;
                    String warning;

                    try {
                        okay = Helper.getResourceString(mContext, R.string.Okay);
                        warning = Helper.getResourceString(mContext, R.string.Warning);
                    } catch (Throwable t) {
                        okay = mContext.getString(R.string.Okay);
                        warning = mContext.getString(R.string.Warning);
                    }



                    builder.setMessage(warning);
                    builder.setPositiveButton(okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Helper.setIcon(mContext, "IntroActivity-Alias", false);
                            Helper.setIcon(mContext, "IntroActivity-Alias2", false);
                            Helper.setSetting("HideApp", "true");
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            com.ihelp101.instagram.Pref item = null;

                            if (preference instanceof com.ihelp101.instagram.Pref) {
                                item = (com.ihelp101.instagram.Pref) preference;
                            }
                            item.setChecked(false);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    if (Helper.getSettings("AppIcon")) {
                        Helper.setIcon(mContext, "IntroActivity-Alias", false);
                        Helper.setIcon(mContext, "IntroActivity-Alias2", true);
                    } else {
                        Helper.setIcon(mContext, "IntroActivity-Alias", true);
                        Helper.setIcon(mContext, "IntroActivity-Alias2", false);
                    }
                    Helper.setSetting("HideApp", "false");
                }
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.NotificationHide))) {
                Helper.setSetting("Notification", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.HidePost))) {
                Helper.setSetting("SponsorPost", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Logging))) {
                Helper.setSetting("Log", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Pass))) {
                Helper.setSetting("Pass", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.the_not_so_big_but_big_button2))) {
                Helper.setSetting("Lock", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Push))) {
                Helper.setSetting("Push", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Resolution))) {
                Helper.setSetting("Resolution", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.SaveHooks))) {
                Helper.setSetting("HookSave", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Search))) {
                Helper.setSetting("History", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Slide))) {
                Helper.setSetting("Slide", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Story))) {
                Helper.setSetting("Story", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.StoryHide))) {
                Helper.setSetting("StoryHide", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.StoryPrivacy))) {
                Helper.setSetting("StoryPrivacy", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Suggestion))) {
                Helper.setSetting("Suggestion", o.toString());
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.VideoLikes))) {
                Helper.setSetting("VideoLikes", o.toString());
            }

            return true;
        }
    };

    private static Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String clicked =  preference.getTitle().toString();

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Date))) {
                if (Helper.getSetting("Date").equals("Instagram")) {
                    if (Helper.getSettings("Hour")) {
                        Helper.setSetting("Date", "MM;/dd;/yyyy; ;HH:;mm:;ss:;a;");
                    } else {
                        Helper.setSetting("Date", "MM;/dd;/yyyy; ;hh:;mm:;ss:;a;");
                    }
                }
                Intent myIntent = new Intent(mContext, com.ihelp101.instagram.Date.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(myIntent);
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.FileFormat))) {
                Intent myIntent = new Intent(mContext, com.ihelp101.instagram.FileFormat.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(myIntent);
            }

            if (clicked.equals(Helper.getResourceString(mContext, R.string.Follow))) {
                String color = Helper.getSetting("Color");
                if (color.equals("Instagram")) {
                    color = "#2E978C";
                }
                ColorPicker cpd = new ColorPicker(nContext, Color.parseColor(color), new ColorPicker.OnColorSelectedListener() {
                    @Override
                    public void colorSelected(Integer color) {
                        String hexColor = String.format("#%06X", (0xFFFFFF & color));
                        Helper.setSetting("Color", hexColor);
                    }
                });
                cpd.setTitle("Pick a color.");
                cpd.show();
            }

            return  true;
        }
    };

    static void showHooksPasteOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
        builder.setTitle("Paste The New Hooks");

        final EditText input = new EditText(nContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Helper.setSetting("Hooks", input.getText().toString());
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

    static void showOneTap() {
        int itemSelected = 0;
        final CharSequence items[] = {"Default", "XInsta"};

        if (!Helper.getSettings("Pass") && !Helper.getSettings("OneTap")) {
            itemSelected = 0;
        } else if (Helper.getSettings("Pass")) {
            itemSelected = 1;
        } else {
            itemSelected = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(nContext);
        builder.setTitle("Which option would you like to handle images?");
        builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Helper.setSetting("OneTap", "true");
                    Helper.setSetting("Pass", "false");
                } else if (which == 1) {
                    Helper.setSetting("OneTap", "true");
                    Helper.setSetting("Pass", "true");
                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}