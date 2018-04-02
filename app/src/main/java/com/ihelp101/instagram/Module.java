package com.ihelp101.instagram;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.filterfw.core.Frame;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.mayulive.xposed.classhunter.ProfileHelpers;
import com.mayulive.xposed.classhunter.ProfileSearch;
import com.mayulive.xposed.classhunter.ProfileServer;
import com.mayulive.xposed.classhunter.packagetree.PackageTree;
import com.mayulive.xposed.classhunter.profiles.ClassProfile;
import com.mayulive.xposed.classhunter.profiles.MethodProfile;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Module implements IXposedHookLoadPackage, Listen {

    Boolean hookCheck;
    CharSequence[] mMenuOptions = null;
    public static Context mContext;
    public static Context nContext;
    public static Context oContext;
    Object mCurrentMediaOptionButton;
    Object mCurrentDirectShareMediaOptionButton;
    Object mUserName;
    Object model;

    String date = "Nope";
    String directShareCheck = "Nope";
    String fileType;
    String firstHook;
    String Hooks = null;
    String[] HooksArray;
    String HookCheck = "No";
    String HooksSave;
    String linkToDownload = "";
    String fileName;
    String notificationTitle;
    String oldCheck = "No";
    String userName = "";
    String userFullName = "";
    String version = "123";

    String CAROUSEL_HOOK;
    String COMMENT_HOOK;
    String COMMENT_HOOK_CLASS;
    String COMMENT_HOOK_CLASS2;
    String DISCOVERY_CLASS;
    String DS_DIALOG_CLASS;
    String DS_MEDIA_OPTIONS_BUTTON_CLASS;
    String DS_PERM_MORE_OPTIONS_DIALOG_CLASS;
    String DS_PRIVATE_CLASS;
    String DS_PRIVATE_CLASS2;
    String DS_PRIVATE_CLASS3;
    String DS_PRIVATE_HOOK;
    String FEED_CLASS_NAME;
    String FOLLOW_HOOK;
    String FOLLOW_HOOK_2;
    String FOLLOW_HOOK_CLASS;
    String FOLLOW_LIST_CLASS;
    String FULLNAME__HOOK;
    String IMAGE_HOOK_CLASS;
    String ITEMID_HOOK;
    String LIKE_HOOK_CLASS;
    String LIKE_HOOK_CLASS2;
    String LIKED_POST_HOOK_CLASS;
    String LOCK_HOOK8;
    String MEDIA_CLASS_NAME;
    String MEDIA_OPTIONS_BUTTON_CLASS;
    String MEDIA_PHOTO_HOOK;
    String MEDIA_VIDEO_HOOK;
    String MINI_FEED_HOOK_CLASS;
    String MINI_FEED_HOOK_CLASS2;
    String NOTIFICATION_CLASS;
    String PAID_HOOK;
    String PERM__HOOK;
    String PIN_HOOK_CLASS;
    String PIN_HOOK_CLASS2;
    String PIN_HOOK_CLASS3;
    String PIN_HOOK_CLASS4;
    String PIN_HOOK_CLASS5;
    String PROFILE_HOOK_3;
    String PROFILE_HOOK_4;
    String PROFILE_HOOK_CLASS;
    String PROFILE_HOOK_CLASS2;
    String PROFILE_ICON_CLASS;
    String PROFILE_ICON_CLASS2;
    String PROFILE_ICON_HOOK;
    String SAVE = "Instagram";
    String SCREENSHOT_CLASS;
    String SEARCH_HOOK_CLASS;
    String SEARCH_HOOK_CLASS2;
    String SEARCH_HOOK_CLASS3;
    String SEARCH_HOOK_CLASS4;
    String SHARE_HOOK_CLASS;
    String SLIDE_HOOK;
    String SLIDE_HOOK_CLASS;
    String SPONSORED_HOOK_CLASS;
    String SPONSORED_HOOK;
    String STORY_GALLERY_CLASS;
    String STORY_HOOK;
    String STORY_HOOK_CLASS;
    String STORY_HOOK_CLASS2;
    String STORY_TIME_HOOK;
    String STORY_TIME_HOOK2;
    String STORY_TIME_HOOK_CLASS;
    String STORY_TIME_HOOK_CLASS2;
    String STORY_TIME_HOOK_CLASS3;
    String STORY_VIEWS_HOOK;
    String SUGGESTION_HOOK_CLASS;
    String TAGGED_HOOK_CLASS;
    String TIME_HOOK_CLASS;
    String USER_AGENT_CLASS;
    String USER_CLASS_NAME;
    String USERNAME_HOOK;
    String VIDEO_HOOK;
    String VIDEO_HOOK_CLASS;
    String VIDEO_LIKE_HOOK;
    String VIDEO_LIKE_HOOK_CLASS;

    boolean followed = false;
    int count = 0;
    int feedCount = 0;
    int followerCountValue = 0;
    int followingCountValue = 0;
    int versionCheck;
    long lastTap = 0;
    LoadPackageParam loadPackageParam;
    List videoList = null;
    String checked = "";
    String linksToDownload = null;
    String locationsToDownload = null;
    String lowered = "Nope";
    String notificationsToDownload = null;
    String fileNamesToDownload = null;
    String fileTypesToDownload = null;
    String userNamesToDownload = null;
    String userNameTagged = "";
    String userProfileIcon;
    TextView followerCount;
    ArrayList<View> views = null;

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = Helper.downloadManager.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    String fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)).replace("file://", "");
                    if (title.contains("Live Story Video")) {
                        title = title.replace(" Video", "");
                        Helper.passLiveStory(fileUri, title, ctxt);
                    }
                }
            }
        }
    };

    boolean appInstalledOrNot(String uri) {
        PackageManager pm = nContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public boolean canAutoUpdate(String s, int i) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!hookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }

    boolean hookCheck(final String version) {
        hookCheck = true;
        Thread getHooks= new Thread() {
            public void run() {
                String versions;
                try {
                    String url;

                    if (Helper.getSetting("Source").equals("GitHub")) {
                        url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                    } else if (Helper.getSetting("Source").equals("Pastebin")) {
                        url = "http://pastebin.com/raw.php?i=sTXbUFcx";
                    } else if (Helper.getSetting("Source").equals("Alternate Source")) {
                        url = "http://www.snapprefs.com/xinsta/Hooks.txt";
                    } else {
                        url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                    }

                    URL u = new URL(url);
                    URLConnection c = u.openConnection();
                    c.connect();

                    InputStream inputStream = c.getInputStream();

                    versions = Helper.convertStreamToString(inputStream);
                } catch (Exception e) {
                    Helper.setError("Failed to fetch hooks - " +e);
                    versions = "Nope";
                }

                if (versions.contains(version)) {
                    hookCheck = true;
                } else {
                    hookCheck = false;
                }
            }
        };
        getHooks.start();

        try {
            getHooks.join();
        }   catch (Exception e) {

        }

        return hookCheck;
    }

    @Override
    public boolean shouldUserUpdate(String s, int i, String s1) {
        boolean answer = true;
        if (s.equals("com.instagram.android")) {
            String version = Integer.toString(i);
            version = version.substring(0, version.length() - 2);
            if (!hookCheck(version)) {
                answer = false;
            }
        }
        return answer;
    }

    class ShowToast extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            return uri[0];
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Helper.Toast(s, mContext);
        }
    }

    class DownloadLive extends AsyncTask<String, String, String> {

        String save;
        String name;

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";
            String[] strings = uri[0].split(";");

            for (String string : strings) {
                String link = string;
                String notificationTitle = "";
                String userName = uri[1];
                String file = uri[2];

                name = uri[1];

                try {
                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Audio");
                } catch (Throwable t) {
                    notificationTitle = userName + "'s Live Story Audio";
                }

                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                if (link.equals(strings[1])) {
                    notificationTitle = notificationTitle.replace("Live Story Audio", "Live Story Video");
                }

                if (link.equals(strings[1])) {
                    file = file.replace("LiveAudio", "LiveVideo");
                }

                save = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + file;

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
                request.setDescription("Downloading...");
                request.setTitle(notificationTitle);
                request.setDestinationUri(Uri.fromFile(new File(save)));
                request.allowScanningByMediaScanner();

                Helper.downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Helper.downloadManager.enqueue(request);
            }

            return responseString;
        }
    }

    class Privacy extends AsyncTask<String, String, String> {

        Dialog builder = null;
        String url;
        String check = "Nope";
        VideoView videoView = null;
        WebView webView = null;
        int originalHeight = 0;
        int originalWidth = 0;
        RelativeLayout.LayoutParams lp2 = null;

        @Override
        protected String doInBackground(String... uri) {
            try {
                url = uri[0];
                check = uri[1];
            } catch (Throwable t) {
            }

            return "Nope";
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            try {
                String urlHash = "/" + url.split("/")[3] + "/" + url.split("/")[4] + "/" +  url.split("/")[5];
                //url = url.replace(urlHash, "");

                builder = new Dialog(oContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                if (!check.equals("Other")) {
                    builder.setCanceledOnTouchOutside(false);
                    builder.setCancelable(false);
                }
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                if (check.equals("Other")) {
                    builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                if (url.contains("jpg")) {
                    webView = new WebView(oContext);
                    webView.getSettings().setSupportZoom(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setDisplayZoomControls(false);
                    webView.setInitialScale(1);
                    webView.getSettings().setLoadWithOverviewMode(true);
                    webView.getSettings().setUseWideViewPort(true);
                    webView.setBackgroundColor(Color.BLACK);
                    webView.loadUrl(url);

                    if (check.equals("Other")) {
                        webView.setBackgroundColor(Color.TRANSPARENT);
                    }

                    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                    webView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (originalHeight == 0) {
                                originalHeight = webView.getHeight();
                                originalWidth = webView.getWidth();
                            } else {
                                lp2.height = originalHeight;
                                lp2.width = originalWidth;
                                webView.setLayoutParams(lp2);
                            }
                            return false;
                        }
                    });
                } else {
                    videoView = new VideoView(oContext);
                    videoView.setVideoURI(Uri.parse(url));
                    videoView.setZOrderOnTop(true);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                        }
                    });
                    videoView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (videoView.isPlaying()) {
                                videoView.pause();
                            } else {
                                videoView.start();
                            }

                            return false;
                        }
                    });
                    videoView.start();
                }

                TextView button = new TextView (oContext);
                button.setText("...");
                button.setRotation(90);
                button.setTextColor(Color.parseColor("#D3D3D3"));
                button.setTextSize(32);
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(oContext)
                                .setTitle("Close?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                });

                RelativeLayout layout = new RelativeLayout(oContext);
                layout.setGravity(RelativeLayout.CENTER_IN_PARENT);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                if (check.equals("Other")) {
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            builder.dismiss();
                        }
                    });

                    button.setText("");

                    String data = "<html><head><meta name='viewport' content='width=device-width, minimum-scale=0.1'></head></html>";
                    data= data + "<img style='-webkit-user-select: none' width='100%' src='" + url+ "'/></html>";

                    webView.getSettings().setUseWideViewPort(false);
                    webView.loadData(data, "text/html", null);
                }

                if (url.contains(".jpg")) {
                    lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
                    webView.setLayoutParams(lp2);
                    layout.addView(webView);
                } else {
                    lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
                    videoView.setLayoutParams(lp2);
                    layout.addView(videoView);
                }

                button.setLayoutParams(lp);
                layout.addView(button);
                button.bringToFront();

                builder.setContentView(layout);
                builder.show();
            } catch (Throwable t) {
                setError("Privacy View Failed: " +t);
            }
        }
    }

    class PrivacyMulti extends AsyncTask<String, String, String> {

        Dialog builder = null;
        String url;
        WebView webView = null;
        RelativeLayout.LayoutParams lp2 = null;

        @Override
        protected String doInBackground(String... uri) {
            try {
                url = uri[0];
            } catch (Throwable t) {
            }

            return "Nope";
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            try {
                String urlHash = "/" + url.split("/")[3] + "/" + url.split("/")[4] + "/" +  url.split("/")[5];
                //url = url.replace(urlHash, "");

                builder = new Dialog(oContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                builder.setCanceledOnTouchOutside(false);
                builder.setCancelable(false);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                String html = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta name=\"viewport\" content='width=device-width, minimum-scale=0.1'\">\n" +
                        "<style>\n" +
                        "* {box-sizing:border-box}\n" +
                        "body {font-family: Verdana,sans-serif;margin:0}\n" +
                        ".mySlides {display:none}\n" +
                        "\n" +
                        "/* Slideshow container */\n" +
                        ".slideshow-container {\n" +
                        "  max-width: 1000px;\n" +
                        "  position: relative;\n" +
                        "  margin: auto;\n" +
                        "}\n" +
                        "\n" +
                        "/* Next & previous buttons */\n" +
                        ".prev, .next {\n" +
                        "  cursor: pointer;\n" +
                        "  position: absolute;\n" +
                        "  top: 50%;\n" +
                        "  width: auto;\n" +
                        "  padding: 16px;\n" +
                        "  margin-top: -22px;\n" +
                        "  color: white;\n" +
                        "  font-weight: bold;\n" +
                        "  font-size: 18px;\n" +
                        "  transition: 0.6s ease;\n" +
                        "  border-radius: 0 3px 3px 0;\n" +
                        "}\n" +
                        "\n" +
                        "/* Position the \"next button\" to the right */\n" +
                        ".next {\n" +
                        "  right: 0;\n" +
                        "  border-radius: 3px 0 0 3px;\n" +
                        "}\n" +
                        "\n" +
                        "/* On hover, add a black background color with a little bit see-through */\n" +
                        ".prev:hover, .next:hover {\n" +
                        "  background-color: rgba(0,0,0,0.8);\n" +
                        "}\n" +
                        "\n" +
                        "/* Caption text */\n" +
                        ".text {\n" +
                        "  color: #f2f2f2;\n" +
                        "  font-size: 15px;\n" +
                        "  padding: 8px 12px;\n" +
                        "  position: absolute;\n" +
                        "  bottom: 8px;\n" +
                        "  width: 100%;\n" +
                        "  text-align: center;\n" +
                        "}\n" +
                        "\n" +
                        "/* Number text (1/3 etc) */\n" +
                        ".numbertext {\n" +
                        "  color: #f2f2f2;\n" +
                        "  font-size: 12px;\n" +
                        "  padding: 8px 12px;\n" +
                        "  position: absolute;\n" +
                        "  top: 0;\n" +
                        "}\n" +
                        "\n" +
                        "/* The dots/bullets/indicators */\n" +
                        ".dot {\n" +
                        "  cursor:pointer;\n" +
                        "  height: 13px;\n" +
                        "  width: 13px;\n" +
                        "  margin: 0 2px;\n" +
                        "  background-color: #bbb;\n" +
                        "  border-radius: 50%;\n" +
                        "  display: inline-block;\n" +
                        "  transition: background-color 0.6s ease;\n" +
                        "}\n" +
                        "\n" +
                        ".active, .dot:hover {\n" +
                        "  background-color: #717171;\n" +
                        "}\n" +
                        "\n" +
                        "/* Fading animation */\n" +
                        ".fade {\n" +
                        "  -webkit-animation-name: fade;\n" +
                        "  -webkit-animation-duration: 1.5s;\n" +
                        "  animation-name: fade;\n" +
                        "  animation-duration: 1.5s;\n" +
                        "}\n" +
                        "\n" +
                        "@-webkit-keyframes fade {\n" +
                        "  from {opacity: .4} \n" +
                        "  to {opacity: 1}\n" +
                        "}\n" +
                        "\n" +
                        "@keyframes fade {\n" +
                        "  from {opacity: .4} \n" +
                        "  to {opacity: 1}\n" +
                        "}\n" +
                        "\n" +
                        "/* On smaller screens, decrease text size */\n" +
                        "@media only screen and (max-width: 300px) {\n" +
                        "  .prev, .next,.text {font-size: 11px}\n" +
                        "}\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<div class=\"slideshow-container\">\n" +
                        "\n" +
                        "replaceThisSlideShow" +
                        "\n" +
                        "<a class=\"prev\" onclick=\"plusSlides(-1)\">&#10094;</a>\n" +
                        "<a class=\"next\" onclick=\"plusSlides(1)\">&#10095;</a>\n" +
                        "\n" +
                        "</div>\n" +
                        "<br>\n" +
                        "\n" +
                        "<script>\n" +
                        "var slideIndex = 1;\n" +
                        "showSlides(slideIndex);\n" +
                        "\n" +
                        "function plusSlides(n) {\n" +
                        "  showSlides(slideIndex += n);\n" +
                        "}\n" +
                        "\n" +
                        "function currentSlide(n) {\n" +
                        "  showSlides(slideIndex = n);\n" +
                        "}\n" +
                        "\n" +
                        "function showSlides(n) {\n" +
                        "  var i;\n" +
                        "  var slides = document.getElementsByClassName(\"mySlides\");\n" +
                        "  var dots = document.getElementsByClassName(\"dot\");\n" +
                        "  if (n > slides.length) {slideIndex = 1}    \n" +
                        "  if (n < 1) {slideIndex = slides.length}\n" +
                        "  for (i = 0; i < slides.length; i++) {\n" +
                        "      slides[i].style.display = \"none\";  \n" +
                        "  }\n" +
                        "  for (i = 0; i < dots.length; i++) {\n" +
                        "      dots[i].className = dots[i].className.replace(\" active\", \"\");\n" +
                        "  }\n" +
                        "  slides[slideIndex-1].style.display = \"block\";  \n" +
                        "  dots[slideIndex-1].className += \" active\";\n" +
                        "}\n" +
                        "</script>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html> \n";

                String slideshow = "";

                for (String string : url.split(";")) {
                    if (string.contains(".mp4")) {
                        slideshow = slideshow + "<div class='mySlides fade'>\n" +
                                "  <video src='replaceURL' controls onclick=\"this.paused ? this.play() : this.pause();\" style='width:100%'>\n" +
                                "</div>";
                    } else {
                        slideshow = slideshow + "<div class='mySlides fade'>\n" +
                                "  <img src='replaceURL' style='width:100%'>\n" +
                                "</div>";
                    }
                    slideshow = slideshow.replace("replaceURL", string);
                }

                html = html.replace("replaceThisSlideShow", slideshow);

                webView = new WebView(oContext);
                webView.setWebChromeClient(new WebChromeClient());
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.setInitialScale(1);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setBackgroundColor(Color.BLACK);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadData(html, "text/html", "UTF-8");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }

                TextView button = new TextView (oContext);
                button.setText("...");
                button.setRotation(90);
                button.setTextColor(Color.parseColor("#D3D3D3"));
                button.setTextSize(32);
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(oContext)
                                .setTitle("Close?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                });

                RelativeLayout layout = new RelativeLayout(oContext);
                layout.setGravity(RelativeLayout.CENTER_IN_PARENT);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
                webView.setLayoutParams(lp2);
                layout.addView(webView);

                button.setLayoutParams(lp);
                layout.addView(button);
                button.bringToFront();

                builder.setContentView(layout);
                builder.show();
            } catch (Throwable t) {
                System.out.println("Set Error: " +t);
            }
        }
    }

    class VersionCheck extends AsyncTask<String, String, String> {
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
                responseString = "Nope";
            }

            return responseString.trim();
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            try {
                version = nContext.getPackageManager().getPackageInfo("com.ihelp101.instagram", 0).versionName;
                version = version.trim();

                int skip = 0;

                int versionCheck = Integer.parseInt(result.replaceAll("\\.", ""));
                int currentVersion = Integer.parseInt(version.replaceAll("\\.", ""));

                if (currentVersion > versionCheck) {
                    skip = 1;
                }

                if (!result.equals(version) && !result.equals("Nope") && skip == 0) {
                    Intent intent = new Intent();
                    intent.setPackage("com.ihelp101.instagram");
                    intent.setAction("com.ihelp101.instagram.UPDATE");
                    intent.putExtra("Version", result);
                    nContext.sendBroadcast(intent);
                }
            } catch (Throwable t) {
            }
        }
    }

    void copyComment(String string) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", string);
        clipboard.setPrimaryClip(clip);
        String copied;

        try {
            copied = Helper.getResourceString(AndroidAppHelper.currentApplication().getApplicationContext(), R.string.Copied);
        } catch (Throwable t) {
            copied = "Comment Copied";
        }

        Helper.Toast(copied, mContext);
    }

    @SuppressLint("NewApi")
	void downloadMedia(Object mMedia, String where) throws IllegalAccessException, IllegalArgumentException {
        String filenameExtension;
        String descriptionType;
        String itemId;

        int descriptionTypeId;
        long longId = 0;

        if (!where.equals("Multi") && !where.equals("Select")) {
            date = "Nope";
            videoList = null;
        }

        try {
            linkToDownload = (String) getObjectField(mMedia, MEDIA_VIDEO_HOOK);
            filenameExtension = "mp4";
            fileType = "Video";
            descriptionTypeId = R.string.video;

            if (linkToDownload.equals("None")) {
                filenameExtension = "";
            }
        } catch (Throwable throwable) {
            try {
                setError("Falling Back To New Video Method");

                List videoList = (List) getObjectField(mMedia, MEDIA_VIDEO_HOOK);

                for (int i=0;i < videoList.size();i++) {
                    String videoUrl = (String) XposedHelpers.getObjectField(videoList.get(i), XposedHelpers.findFirstFieldByExactType(videoList.get(i).getClass(), String.class).getName());
                    if (videoList.contains("_n.mp4")) {
                        i = 999;
                        linkToDownload = videoUrl;
                    } else {
                        linkToDownload = (String) XposedHelpers.getObjectField(videoList.get(0), XposedHelpers.findFirstFieldByExactType(videoList.get(0).getClass(), String.class).getName());
                    }
                }

                filenameExtension = "mp4";
                fileType = "Video";
                descriptionTypeId = R.string.video;

                if (linkToDownload.equals("None")) {
                    filenameExtension = "";
                }
            } catch (Throwable t) {
                setError("Switch Link - Different Media Type");
                oldCheck = "No";
                if (oldCheck.equals("No")) {
                    try {
                        Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                        Object photo = Helper.getFieldByType(mMedia, Image);

                        linkToDownload = (String) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, String.class).getName());
                    } catch (Throwable t2) {
                        try {
                            Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                            Object photo = Helper.getFieldByType(mMedia, Image);

                            if (photo == null) {
                                photo = Helper.getOtherFieldByType(mMedia, Image);
                            }

                            List videoList = (List) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, List.class).getName());

                            for (int i=0;i < videoList.size();i++) {
                                String videoUrl = (String) XposedHelpers.getObjectField(videoList.get(i), XposedHelpers.findFirstFieldByExactType(videoList.get(i).getClass(), String.class).getName());
                                if (videoList.contains("_n.jpg") && videoList.contains("full_size")) {
                                    i = 999;
                                    linkToDownload = videoUrl;
                                } else {
                                    linkToDownload = (String) XposedHelpers.getObjectField(videoList.get(0), XposedHelpers.findFirstFieldByExactType(videoList.get(0).getClass(), String.class).getName());
                                }
                            }
                        } catch (Throwable t3) {
                            try {
                                videoList = (List) getObjectField(mMedia, CAROUSEL_HOOK);
                            } catch (Throwable t4) {
                                setError("Photo Hook Invalid - " + t4);

                                if (!where.equals("Private")) {
                                    sendError();
                                }
                                return;
                            }
                        }
                    }
                } else {
                    try {
                        linkToDownload = (String) getObjectField(mMedia, MEDIA_PHOTO_HOOK);
                    } catch (Throwable t2) {
                        setError("Link To Download Hook Invalid (Photo) - " + t2);
                        sendError();
                        return;
                    }
                }
                filenameExtension = "jpg";
                fileType = "Image";
                descriptionTypeId = R.string.photo;
            }
        }

        try {
            userNameTagged = "";
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                Field[] fields = object.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.toString().contains("UserInfo")) {
                        Object object1 = XposedHelpers.getObjectField(object, field.getName());
                        userNameTagged = userNameTagged + XposedHelpers.getObjectField(object1, XposedHelpers.findFirstFieldByExactType(object1.getClass(), String.class).getName()) + ";";
                    }
                }
            }
        } catch (NullPointerException e) {
        } catch (Throwable t) {
            setError("Tagged Failed - " +t);
        }

        try {
            //linkToDownload = linkToDownload.replace("750x750", "");
            //linkToDownload = linkToDownload.replace("640x640", "");
            //linkToDownload = linkToDownload.replace("480x480", "");
            //linkToDownload = linkToDownload.replace("320x320", "");
        } catch (Throwable t) {
            if (videoList == null) {
                setError("Failed To Replace Link To Download - " + t);
            }
        }

        if (where.equals("Lock") && videoList == null) {
            new Privacy().execute(linkToDownload);
            return;
        }

		// Construct filename
		// username_imageId.jpg
        try {
            descriptionType = Helper.getResourceString(mContext, descriptionTypeId);
        } catch (Throwable t) {
            descriptionType = fileType;
        }

        String downloading;

        try {
            downloading = Helper.getResourceString(mContext, R.string.Downloading, descriptionType);
        } catch (Throwable t) {
            downloading = "Downloading " + descriptionType;
        }

        if (userNameTagged.isEmpty()) {
            if (videoList == null) {
                Helper.Toast(downloading, mContext);
            }
        }

		Object mUser;
        try {
            mUser = Helper.getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        } catch (Throwable t) {
            setError("mUser Hook Invalid - " + USER_CLASS_NAME);
            sendError();
            return;
        }

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            mUser = mUserName;
        }

        try {
            if (!where.equals("Select")) {
                userName = (String) getObjectField(mUser, USERNAME_HOOK);
                userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
            }
        } catch (Throwable t) {
            if (!where.equals("Multi")) {
                setError("Failed to get User from Media, using placeholders");
                userName = "username_placeholder";
                userFullName = "Unknown name";
            }
        }

        if (Helper.getSettings("Username") && !where.equals("Multi") && !where.equals("Select")) {
            if (!userFullName.isEmpty()) {
                userName = userFullName;
            }
        }

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            try {
                itemId = getObjectField(model, XposedHelpers.findFirstFieldByExactType(model.getClass(), Long.class).getName()).toString();
            } catch (Throwable t) {
                setError("ItemID Directshare Hook Failed");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
       } else {
            try {
                itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
            } catch (Throwable t) {
                setError("ItemID Hook Invalid - " + t);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                itemId = sdf.format(new Date());
            }
        }

        if (!Helper.getSetting("File").equals("Instagram") && !where.equals("Direct") && !where.equals("Private")) {
            try {
                longId = (Long) getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), long.class).getName());

                Field[] fields = mMedia.getClass().getDeclaredFields();

                try {
                    for (Field field : fields) {
                        try {
                            if (field.getType().equals(long.class)) {
                                Long testID = (Long) XposedHelpers.getObjectField(mMedia, field.getName());

                                if (testID != 0 && testID != null) {
                                    longId = testID;
                                    break;
                                }
                            }
                        } catch (Throwable t2) {
                        }
                    }
                } catch (Throwable t) {
                }

                if (!where.equals("Multi") && !where.equals("Select")) {
                    date = Helper.getDate(longId, nContext);
                }

                String mediaId =  itemId.replace(itemId.split("_")[1], "");

                if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                    fileName = Helper.getSetting("FileFormat");
                    fileName = fileName.replace("Username", userName);
                    fileName = fileName.replace("MediaID", mediaId);
                    fileName = fileName.replace("Date", date);
                    fileName = fileName + "." + filenameExtension;
                } else {
                    fileName = userName + "_" + itemId + "." + filenameExtension;
                }
            } catch (Throwable t) {
                setError("Bad - " +t);
                date = "Nope";
                try {
                    itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
                } catch (Throwable t2) {
                    setError("ItemID Hook Invalid - " + t2);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                    itemId = sdf.format(new Date());
                }
            }
        }

        try {
            if (!Helper.getSetting("FileFormat").equals("Instagram") && date.equals("Nope") && !where.equals("Direct") && !where.equals("Private")) {
                String mediaID = itemId.replace(itemId.split("_")[1], "");
                String userID = itemId.split("_")[1];
                date = "Yes";

                fileName = Helper.getSetting("FileFormat");
                fileName = fileName.replace("Username", userName);
                fileName = fileName.replace("MediaID", mediaID);
                fileName = fileName.replace("UserID", userID);
                fileName = fileName + "." + filenameExtension;
            } else if (date.equals("Nope")) {
                fileName = userName + "_" + itemId + "." + filenameExtension;
            }
        } catch (Throwable t) {
            setError("Bad2 - " +t);
        }

        if (fileName.contains("_.")) {
            fileName = fileName.replace("_.", ".");
        }

        fileName = fileName.replaceAll("__", "_");

        if (userFullName.isEmpty()) {
            userFullName = userName;
        }

        try {
            notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userFullName, descriptionType);
        } catch (Throwable t) {
            notificationTitle = userName + "'s " + descriptionType;
        }

        notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

        SAVE = Helper.getSaveLocation(fileType);

        if (videoList != null && !where.equals("Lock") && !where.equals("Multi") && !where.equals("Select")) {
            multiAlert(longId, mMedia);
            return;
        } else if (videoList!= null && where.equals("Lock")) {
            multiAlertLock(longId);
            return;
        }

        if (!userNameTagged.isEmpty() && Helper.getSettings("Folder") && !where.equals("Multi") && !where.equals("Select")) {
            if (!userNameTagged.contains(userName)) {
                userNameTagged = userNameTagged + userName + ";";
            }
            taggedUserAlert(downloading, longId);
            return;
        }

        try {
            if (where.equals("Private")) {
                linkToDownload = linkToDownload.replace("v/", "");
            }
        } catch (Throwable t) {
            setError("Session Bypass Failed - " +t);
        }

        try {
            if (fileType.equals("Video") && Helper.getSettings("OneTap") && appInstalledOrNot("com.phantom.onetapvideodownload")) {
                setError("One Tap - " +Helper.getSettings("OneTap"));
                Intent intent = new Intent("com.phantom.onetapvideodownload.action.saveurl");
                intent.setClassName("com.phantom.onetapvideodownload", "com.phantom.onetapvideodownload.IpcService");
                intent.putExtra("com.phantom.onetapvideodownload.extra.url", linkToDownload);
                intent.putExtra("com.phantom.onetapvideodownload.extra.title", fileName);
                intent.putExtra("com.phantom.onetapvideodownload.extra.package_name", loadPackageParam.packageName);
                mContext.startService(intent);
            } else {
                Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, mContext, false);
            }
        } catch (Exception e) {
            setError("Failed To Send Download Pass Broadcast - " +e);
        }
	}

    @SuppressLint("NewApi")
    void downloadMultiMedia(Object mMedia, String where, long longId) throws IllegalAccessException, IllegalArgumentException {
        String filenameExtension;
        String descriptionType;
        int descriptionTypeId;
        fileName = "Nope";

        try {
            linkToDownload = (String) getObjectField(mMedia, MEDIA_VIDEO_HOOK);
            filenameExtension = "mp4";
            fileType = "Video";
            descriptionTypeId = R.string.video;

            if (linkToDownload.equals("None")) {
                filenameExtension = "";
            }
        } catch (Throwable throwable) {
            try {
                setError("Falling Back To New Video Method");

                List videoList = (List) getObjectField(mMedia, MEDIA_VIDEO_HOOK);

                for (int i=0;i < videoList.size();i++) {
                    String videoUrl = (String) XposedHelpers.getObjectField(videoList.get(i), XposedHelpers.findFirstFieldByExactType(videoList.get(i).getClass(), String.class).getName());
                    if (videoList.contains("_n.mp4")) {
                        i = 999;
                        linkToDownload = videoUrl;
                    } else {
                        linkToDownload = (String) XposedHelpers.getObjectField(videoList.get(0), XposedHelpers.findFirstFieldByExactType(videoList.get(0).getClass(), String.class).getName());
                    }
                }

                filenameExtension = "mp4";
                fileType = "Video";
                descriptionTypeId = R.string.video;

                if (linkToDownload.equals("None")) {
                    filenameExtension = "";
                }
            } catch (Throwable t) {
                setError("Switch Link - Different Media Type");
                if (oldCheck.equals("No")) {
                    try {
                        Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                        Object photo = Helper.getFieldByType(mMedia, Image);

                        linkToDownload = (String) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, String.class).getName());
                    } catch (Throwable t2) {
                        try {
                            Class<?> Image = findClass(IMAGE_HOOK_CLASS, loadPackageParam.classLoader);
                            Object photo = Helper.getFieldByType(mMedia, Image);

                            if (photo == null) {
                                photo = Helper.getOtherFieldByType(mMedia, Image);
                            }

                            List videoList = (List) getObjectField(photo, XposedHelpers.findFirstFieldByExactType(Image, List.class).getName());

                            for (int i=0;i < videoList.size();i++) {
                                String videoUrl = (String) XposedHelpers.getObjectField(videoList.get(i), XposedHelpers.findFirstFieldByExactType(videoList.get(i).getClass(), String.class).getName());
                                if (videoList.contains("_n.jpg") && videoList.contains("full_size")) {
                                    i = 999;
                                    linkToDownload = videoUrl;
                                } else {
                                    linkToDownload = (String) XposedHelpers.getObjectField(videoList.get(0), XposedHelpers.findFirstFieldByExactType(videoList.get(0).getClass(), String.class).getName());
                                }
                            }
                        } catch (Throwable t3) {
                            setError("Photo Hook Invalid2 - " + t3);
                            sendError();
                            return;
                        }
                    }
                } else {
                    try {
                        linkToDownload = (String) getObjectField(mMedia, MEDIA_PHOTO_HOOK);
                    } catch (Throwable t2) {
                        setError("Link To Download Hook Invalid (Photo) - " + t2);
                        sendError();
                        return;
                    }
                }
                filenameExtension = "jpg";
                fileType = "Image";
                descriptionTypeId = R.string.photo;
            }
        }

        try {
            Object feedObject = XposedHelpers.getObjectField(mMedia, XposedHelpers.findFirstFieldByExactType(mMedia.getClass(), XposedHelpers.findClass(TAGGED_HOOK_CLASS, loadPackageParam.classLoader)).getName());
            ArrayList arrayList = (ArrayList) XposedHelpers.getObjectField(feedObject, XposedHelpers.findFirstFieldByExactType(feedObject.getClass(), ArrayList.class).getName());

            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                Field[] fields = object.getClass().getDeclaredFields();

                for (Field field : fields) {
                    if (field.toString().contains("UserInfo")) {
                        Object object1 = XposedHelpers.getObjectField(object, field.getName());
                        if (!userNameTagged.contains(XposedHelpers.getObjectField(object1, XposedHelpers.findFirstFieldByExactType(object1.getClass(), String.class).getName()).toString())) {
                            userNameTagged = userNameTagged + XposedHelpers.getObjectField(object1, XposedHelpers.findFirstFieldByExactType(object1.getClass(), String.class).getName()) + ";";
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
        } catch (Throwable t) {
            setError("Tagged Failed - " +t);
        }

        try {
            //linkToDownload = linkToDownload.replace("750x750", "");
            //linkToDownload = linkToDownload.replace("640x640", "");
            //linkToDownload = linkToDownload.replace("480x480", "");
            //linkToDownload = linkToDownload.replace("320x320", "");
        } catch (Throwable t) {
            setError("Failed To Replace Link To Download - " +t);
        }

        // Construct filename
        // username_imageId.jpg
        try {
            descriptionType = Helper.getResourceString(mContext, descriptionTypeId);
        } catch (Throwable t) {
            descriptionType = fileType;
        }

        Object mUser;
        try {
            mUser = Helper.getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
        } catch (Throwable t) {
            setError("mUser Hook Invalid - " + USER_CLASS_NAME);
            sendError();
            return;
        }

        if (where.equals("Direct") && directShareCheck.equals("Yes")) {
            mUser = mUserName;
        }

        try {
            userName = (String) getObjectField(mUser, USERNAME_HOOK);
            userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
        } catch (Throwable t) {
            try {
                if (userName.equals("")) {
                }

                if (userFullName.equals("")) {
                }
            } catch (Throwable t2) {
                setError("Failed to get User from Single Media, using placeholders - " +t2);
                userName = "username_placeholder";
                userFullName = "Unknown name";
            }
        }

        if (Helper.getSettings("Username")) {
            if (!userFullName.isEmpty()) {
                userName = userFullName;
            }
        }

        String itemId;

        try {
            itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
        } catch (Throwable t) {
            setError("ItemID Hook Invalid - " + t);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
            itemId = sdf.format(new Date());
        }

        if (!Helper.getSetting("File").equals("Instagram") && !where.equals("Direct")) {
            try {
                date = Helper.getDate(longId, nContext);

                String mediaId =  itemId.replace(itemId.split("_")[1], "");

                if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                    fileName = Helper.getSetting("FileFormat");
                    fileName = fileName.replace("Username", userName);
                    fileName = fileName.replace("MediaID", mediaId);
                    fileName = fileName.replace("Date", date);
                    fileName = fileName + "." + filenameExtension;
                } else {
                    fileName = userName + "_" + itemId + date + "." + filenameExtension;
                }
            } catch (Throwable t) {
                date = "Nope";
                try {
                    itemId = (String) getObjectField(mMedia, ITEMID_HOOK);
                } catch (Throwable t2) {
                    setError("ItemID Hook Invalid - " + t2);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
                    itemId = sdf.format(new Date());
                }
            }
        }

        if (!Helper.getSetting("FileFormat").equals("Instagram") && fileName.equals("Nope")) {
            String mediaID = itemId.replace(itemId.split("_")[1], "");
            String userID = itemId.split("_")[1];

            fileName = Helper.getSetting("FileFormat");
            fileName = fileName.replace("Username", userName);
            fileName = fileName.replace("MediaID", mediaID);
            fileName = fileName.replace("UserID", userID);
            fileName = fileName + "." + filenameExtension;
        } else if (fileName.equals("Nope")){
            fileName = userName + "_" + itemId + "." + filenameExtension;
        }

        if (fileName.contains("_.")) {
            fileName = fileName.replace("_.", ".");
        }

        if (TextUtils.isEmpty(userFullName)) {
            userFullName = userName;
        }

        try {
            notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userFullName, descriptionType);
        } catch (Throwable t) {
            notificationTitle = userName + "'s " + descriptionType;
        }

        notificationTitle = notificationTitle.substring(0,1).toUpperCase() + notificationTitle.substring(1);

        if (Helper.getSettings("URLFileName")) {
            int value = linkToDownload.replace("https://", "").replace("http://", "").split("/").length - 1;
            fileName = linkToDownload.replace("https://", "").replace("http://", "").split("/")[value].split("\\?")[0];
        }

        SAVE = Helper.getSaveLocation(fileType);

        if (linkToDownload.contains("/vp/") && linkToDownload.contains(".jpg")) {
            //linkToDownload = linkToDownload.replace("/vp/", "/");
        }

        if (!linksToDownload.equals("")) {
            linksToDownload = linksToDownload + ";" + linkToDownload;
        } else {
            linksToDownload = linkToDownload;
        }

        if (!locationsToDownload.equals("")) {
            locationsToDownload = locationsToDownload + ";" + SAVE;
        } else {
            locationsToDownload = SAVE;
        }

        if (!notificationsToDownload.equals("")) {
            notificationsToDownload = notificationsToDownload + ";" + notificationTitle;
        } else {
            notificationsToDownload = notificationTitle;
        }

        if (!fileNamesToDownload.equals("")) {
            fileNamesToDownload = fileNamesToDownload + ";" + fileName;
        } else {
            fileNamesToDownload = fileName;
        }

        if (!fileTypesToDownload.equals("")) {
            fileTypesToDownload = fileTypesToDownload + ";" + fileType;
        } else {
            fileTypesToDownload = fileType;
        }

        if (!userNamesToDownload.equals("")) {
            userNamesToDownload = userNamesToDownload + ";" + userName;
        } else {
            userNamesToDownload = userName;
        }
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.instagram.android")) {
            loadPackageParam = lpparam;

            // Thank you to KeepChat For the Following Code Snippet
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            nContext = (Context) callMethod(activityThread, "getSystemContext");

            versionCheck = nContext.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
            //End Snippet

            if (Helper.getSettings("Update")) {
                new VersionCheck().execute();
            }

            setError("XInsta Initialized");
            setError("Instagram Version Code: " + versionCheck);
            setError("Device Codename: " + Build.MODEL);
            setError("Android Version: " + Build.VERSION.RELEASE);

            try {
                PackageInfo pinfo = nContext.getPackageManager().getPackageInfo("com.ihelp101.instagram", 0);
                setError("XInsta Version " + pinfo.versionName);
            } catch (Exception e) {
            }

            try {
                views = new ArrayList<View>();
            } catch (Throwable t) {
            }

            try {
                XposedHelpers.findAndHookMethod(LayoutInflater.class, "inflate", int.class, ViewGroup.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            int resInt = (Integer) param.args[0];
                            Context viewContext = AndroidAppHelper.currentApplication().getApplicationContext();
                            String viewName = viewContext.getResources().getResourceName(resInt);

                            if (viewName.contains("explore_video_item_view_with_top_icon") || viewName.contains("explore_top_live_item_view")) {
                                if (Helper.getSettings("DiscoverHide")) {
                                    try {
                                        View view = (View) param.getResult();
                                        ViewGroup.LayoutParams params = view.getLayoutParams();
                                        params.height = 1;
                                        view.setLayoutParams(params);
                                        view.setVisibility(View.GONE);
                                    } catch (Throwable t) {
                                    }
                                }
                            }

                            if (viewName.contains("stories_tray")) {
                                if (Helper.getSettings("StoryHide")) {
                                    try {
                                        View view = (View) param.getResult();
                                        ViewGroup.LayoutParams params = view.getLayoutParams();
                                        params.height = 1;
                                        view.setLayoutParams(params);
                                        view.setVisibility(View.GONE);
                                    } catch (Throwable t) {
                                    }
                                }
                            }
                        } catch (Throwable t) {
                        }
                    }
                });
            } catch (Throwable t) {
                setError("Layout Inflator Hooked Failed - " +t);
            }

            startHooks();
        }
    }

    void hookComments() {
        try {
            final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);;

            findAndHookMethod(Comments, COMMENT_HOOK, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object pj = param.args[0];
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    if (Helper.getSettings("Comment")) {
                        copyComment((String) getObjectField(pj, "d"));
                    }
                }
            });

            try {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, void.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

                XposedHelpers.findAndHookMethod(Comments, methods[0].getName(), COMMENT_HOOK_CLASS2, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object pj = param.args[0];
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        if (Helper.getSettings("Comment")) {
                            copyComment((String) getObjectField(pj, "d"));
                        }
                    }
                });
            } catch (Throwable t) {
            }
        } catch (Throwable t2) {
            try {
                final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, boolean.class, MotionEvent.class);

                XposedHelpers.findAndHookMethod(Comments, methods[1].getName(), MotionEvent.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object pj = Helper.getFieldByType(getObjectField(param.thisObject, Comments.getDeclaredFields()[0].getName()), findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        if (Helper.getSettings("Comment")) {
                            copyComment((String) getObjectField(pj, "d"));
                        }
                    }
                });
            } catch (Throwable t) {
                try {
                    final Class<?> Comments = XposedHelpers.findClass(COMMENT_HOOK_CLASS, loadPackageParam.classLoader);
                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Comments, void.class, findClass(COMMENT_HOOK_CLASS2, loadPackageParam.classLoader));

                    XposedHelpers.findAndHookMethod(Comments, methods[0].getName(), COMMENT_HOOK_CLASS2, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object pj = param.args[0];
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                            if (Helper.getSettings("Comment")) {
                                copyComment((String) getObjectField(pj, "d"));
                            }
                        }
                    });
                } catch (Throwable t3) {
                    setError("Comment Failed - " +t3);
                    setError("Comment Class: " + COMMENT_HOOK_CLASS);
                }
            }
        }
    }

    void hookDate() {
        try {
            if (TIME_HOOK_CLASS.equals("Nope") && !Helper.getSetting("Date").equals("Instagram")) {
                final Class<?> Time = XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, CharSequence.class, Context.class);

                XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            String dateFormat = Helper.getSetting("Date");
                            Long epochTime = (Long) XposedHelpers.getObjectField(param.thisObject, (XposedHelpers.findFirstFieldByExactType(Time, long.class)).getName());

                            Date date = new Date(epochTime * 1000L);
                            TimeZone timeZone = TimeZone.getDefault();

                            dateFormat = dateFormat.replaceAll(";", "");

                            if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                dateFormat = dateFormat.replaceAll("a", " a");
                            } else if (dateFormat.contains(":a")) {
                                dateFormat = dateFormat.replaceAll(":a", " a");
                            } else {
                                dateFormat = dateFormat.replaceAll("a", " a");
                            }

                            if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                dateFormat = dateFormat.substring(1, dateFormat.length());
                            }

                            DateFormat format = new SimpleDateFormat(dateFormat);
                            format.setTimeZone(timeZone);

                            param.setResult(format.format(date));
                        } catch (Throwable t) {
                            setError("Date Failed - " + t);
                        }
                    }
                });
            } else if (!Helper.getSetting("Date").equals("Instagram")) {
                try {
                    final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                    XposedHelpers.findAndHookMethod(Time, methods[2].getName(), Context.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            try {
                                String dateFormat = Helper.getSetting("Date");
                                Long epochTime = (Long) param.args[1];

                                Date date = new Date(epochTime * 1000L);
                                TimeZone timeZone = TimeZone.getDefault();

                                dateFormat = dateFormat.replaceAll(";", "");

                                if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                    dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                } else if (dateFormat.contains(":a")) {
                                    dateFormat = dateFormat.replaceAll(":a", " a");
                                } else {
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                }

                                if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                    dateFormat = dateFormat.substring(1, dateFormat.length());
                                }

                                DateFormat format = new SimpleDateFormat(dateFormat);
                                format.setTimeZone(timeZone);

                                param.setResult(format.format(date));
                            } catch (Throwable t) {
                            }
                        }
                    });
                } catch (Throwable t2) {
                    try {
                        final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                        Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                        XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, long.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                try {
                                    String dateFormat = Helper.getSetting("Date");
                                    Long epochTime = (Long) param.args[1];

                                    Date date = new Date(epochTime * 1000L);
                                    TimeZone timeZone = TimeZone.getDefault();

                                    dateFormat = dateFormat.replaceAll(";", "");

                                    if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                        dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                        dateFormat = dateFormat.replaceAll("a", " a");
                                    } else if (dateFormat.contains(":a")) {
                                        dateFormat = dateFormat.replaceAll(":a", " a");
                                    } else {
                                        dateFormat = dateFormat.replaceAll("a", " a");
                                    }

                                    if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                        dateFormat = dateFormat.substring(1, dateFormat.length());
                                    }

                                    DateFormat format = new SimpleDateFormat(dateFormat);
                                    format.setTimeZone(timeZone);

                                    param.setResult(format.format(date));
                                } catch (Throwable t) {
                                }
                            }
                        });
                    } catch (Throwable t3) {
                        setError("Date Failed2 - " + t3);
                        setError("Date Failed2 Class - " + TIME_HOOK_CLASS);
                    }
                }

                try {
                    final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, long.class);

                    XposedHelpers.findAndHookMethod(Time, methods[1].getName(), Context.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            try {
                                String dateFormat = Helper.getSetting("Date");
                                Long epochTime = (Long) param.args[1];

                                Date date = new Date(epochTime * 1000L);
                                TimeZone timeZone = TimeZone.getDefault();

                                dateFormat = dateFormat.replaceAll(";", "");

                                if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                    dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                } else if (dateFormat.contains(":a")) {
                                    dateFormat = dateFormat.replaceAll(":a", " a");
                                } else {
                                    dateFormat = dateFormat.replaceAll("a", " a");
                                }

                                if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                    dateFormat = dateFormat.substring(1, dateFormat.length());
                                }

                                DateFormat format = new SimpleDateFormat(dateFormat);
                                format.setTimeZone(timeZone);

                                param.setResult(format.format(date));
                            } catch (Throwable t) {
                            }
                        }
                    });
                } catch (Throwable t) {
                }
            }

            try {
                final Class<?> Time = XposedHelpers.findClass(TIME_HOOK_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Time, String.class, Context.class, double.class, int.class, boolean.class, int.class);

                XposedHelpers.findAndHookMethod(Time, methods[0].getName(), Context.class, double.class, int.class, boolean.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            String dateFormat = Helper.getSetting("Date");
                            Long epochTime = Double.valueOf(1000.0D * (Double) param.args[1]).longValue();


                            Date date = new Date(epochTime);
                            TimeZone timeZone = TimeZone.getDefault();

                            dateFormat = dateFormat.replaceAll(";", "");

                            if (dateFormat.substring(dateFormat.length() - 1).equals(":")) {
                                dateFormat = dateFormat.substring(0, dateFormat.length() - 1);
                                dateFormat = dateFormat.replaceAll("a", " a");
                            } else if (dateFormat.contains(":a")) {
                                dateFormat = dateFormat.replaceAll(":a", " a");
                            } else {
                                dateFormat = dateFormat.replaceAll("a", " a");
                            }

                            if (dateFormat.substring(0, 1).equals(Helper.getSetting("Separator"))) {
                                dateFormat = dateFormat.substring(1, dateFormat.length());
                            }

                            DateFormat format = new SimpleDateFormat(dateFormat);
                            format.setTimeZone(timeZone);

                            param.setResult(format.format(date));
                        } catch (Throwable t) {
                        }
                    }
                });
            } catch (Throwable t5) {
                setError("Date Failed4 - " + t5);
                setError("Date Failed4 Class - " + TIME_HOOK_CLASS);
            }
        } catch (Throwable t) {
            setError("Date Failed3 - " +t);
        }
    }

    void hookDialog() {
        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), CharSequence[].class, android.content.DialogInterface.OnClickListener.class);

            findAndHookMethod(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence[] string = (CharSequence[]) param.args[0];
                    android.content.DialogInterface.OnClickListener onClickListener = (android.content.DialogInterface.OnClickListener) param.args[1];

                    String dialogName = onClickListener.getClass().getName();

                    if (dialogName.contains("direct") || dialogName.equals(FEED_CLASS_NAME) || dialogName.equals(MINI_FEED_HOOK_CLASS) || dialogName.equals(PROFILE_HOOK_CLASS2) || dialogName.equals(STORY_HOOK_CLASS)) {
                        if (dialogName.equals(FEED_CLASS_NAME)) {
                            param.args[0] = injectDownload(string, "Feed");
                        } else if (dialogName.contains("direct")) {
                            lowered = "Nope";
                            hookDirectShareDialog(onClickListener.getClass());
                            param.args[0] = injectDownload(string, "Other");
                        } else {
                            param.args[0] = injectDownload(string, "Other");
                        }
                    }
                }
            });
        } catch (Throwable t2) {
            try {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), CharSequence[].class, android.content.DialogInterface.OnClickListener.class);

                findAndHookMethod(findClass(DS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), CharSequence[].class, android.content.DialogInterface.OnClickListener.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CharSequence[] string = (CharSequence[]) param.args[0];
                        android.content.DialogInterface.OnClickListener onClickListener = (android.content.DialogInterface.OnClickListener) param.args[1];

                        String dialogName = onClickListener.getClass().getName();

                        if (dialogName.equals(DS_PERM_MORE_OPTIONS_DIALOG_CLASS) || dialogName.equals(FEED_CLASS_NAME) || dialogName.equals(MINI_FEED_HOOK_CLASS) || dialogName.equals(PROFILE_HOOK_CLASS2) || dialogName.equals(STORY_HOOK_CLASS)) {
                            if (dialogName.equals(FEED_CLASS_NAME)) {
                                param.args[0] = injectDownload(string, "Feed");
                            } else if (dialogName.equals(DS_PERM_MORE_OPTIONS_DIALOG_CLASS)) {
                                lowered = "Nope";
                                hookDirectShareDialog(onClickListener.getClass());
                                param.args[0] = injectDownload(string, "Other");
                            } else {
                                param.args[0] = injectDownload(string, "Other");
                            }
                        }
                    }
                });

            } catch (Throwable t3) {
                setError("Dialog Hook Failed: " + t3);
            }
        }
    }

    void hookDirectPrivate() {
        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PRIVATE_CLASS, loadPackageParam.classLoader), void.class, Bundle.class);

            XposedHelpers.findAndHookMethod(findClass(DS_PRIVATE_CLASS, loadPackageParam.classLoader), methods[0].getName(), Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        try {
                            List list = null;
                            Object object = XposedHelpers.getObjectField(param.thisObject, XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), findClass(DS_PRIVATE_CLASS2, loadPackageParam.classLoader)).getName());

                            try {
                                for (Field field: object.getClass().getDeclaredFields()) {
                                    if (field.getType().toString().contains("List")) {
                                        list = (List) XposedHelpers.getObjectField(object, field.getName());
                                    }
                                }
                            } catch (Throwable t) {
                                setError("Direct Private List Failed -" +t);
                            }

                            try {
                                for (int i = 0; i < list.size(); i++) {
                                    Object o = list.get(i);
                                    o = XposedHelpers.getObjectField(o, XposedHelpers.findFirstFieldByExactType(o.getClass(), findClass(DS_PRIVATE_CLASS3, loadPackageParam.classLoader)).getName());
                                    Object mMedia = XposedHelpers.getObjectField(o, XposedHelpers.findFirstFieldByExactType(o.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());

                                    if (mMedia == null) {
                                        Field[] fields = o.getClass().getDeclaredFields();
                                        for (Field field : fields) {
                                            if (mMedia != null) {
                                                break;
                                            }

                                            if (field.getType().equals(findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader))) {
                                                mMedia = XposedHelpers.getObjectField(o, field.getName());
                                            }
                                        }
                                    }


                                    if (Helper.getSettings("Disappearing")) {
                                        downloadMedia(mMedia, "Private");
                                    }
                                }
                            } catch (Throwable t) {
                            }
                        } catch (Throwable t2) {
                            setError("Direct Private Download Failed - " + t2);
                        }
                    } catch (Throwable t) {
                    }
                }
            });
        } catch (Throwable t) {
            setError("Direct Private Failed - " +t);
        }
    }

    void hookDirectShare() {
        try {
            if (directShareCheck.equals("Nope")) {
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), PERM__HOOK, injectDownloadIntoCharSequenceHook);
                findAndHookMethod(findClass(DS_MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), PERM__HOOK, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mCurrentDirectShareMediaOptionButton = param.thisObject;
                    }
                });

                Method[] methods = XposedHelpers.findMethodsByExactParameters(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), void.class, DialogInterface.class, int.class);

                findAndHookMethod(findClass(DS_PERM_MORE_OPTIONS_DIALOG_CLASS, loadPackageParam.classLoader), methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];

                        if (mContext == null) {
                            mContext = ((Dialog) param.args[0]).getContext();
                        }

                        String downloadCheck;

                        try {
                            downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                        } catch (Throwable t) {
                            downloadCheck = "Download";
                        }

                        if (downloadCheck.equals(localCharSequence)) {
                            param.setResult(null);
                            Object mMedia = null;

                            Field[] mCurrentMediaOptionButtonFields =
                                    mCurrentDirectShareMediaOptionButton.getClass().getDeclaredFields();

                            for (Field iField : mCurrentMediaOptionButtonFields) {
                                if (iField.getType().getName().equals(MEDIA_CLASS_NAME)) {
                                    iField.setAccessible(true);
                                    mMedia = iField.get(mCurrentDirectShareMediaOptionButton);
                                    if (mMedia != null) {
                                        break;
                                    }
                                }
                            }

                            if (mMedia == null) {
                                setError("Unable To Determine Media - Directshare");
                                return;
                            }

                            try {
                                downloadMedia(mMedia, "Other");
                            } catch (Throwable t) {
                                setError("Download Media Failed - " +t.toString());
                            }
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Directshare Hooks Invalid - " +t.toString());
        }
    }

    void hookDirectShareDialog(Class onClickListener) {
        try {
            Method[] methods = XposedHelpers.findMethodsByExactParameters(onClickListener, void.class, DialogInterface.class, int.class);

            findAndHookMethod(onClickListener, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String localCharSequence = mMenuOptions[(Integer) param.args[1]].toString();

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        Object mMedia = null;

                        try {
                            Field[] fields = param.thisObject.getClass().getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    String className = XposedHelpers.getObjectField(param.thisObject, field.getName()).getClass().toString();
                                    if (className.contains("model")) {
                                        model = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                    } else if (className.contains("direct") && !className.contains("fragment")) {
                                        if (!XposedHelpers.getObjectField(param.thisObject, field.getName()).toString().contains("ViewHolder")) {
                                            model = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                        }
                                    }
                                } catch (Throwable t) {
                                }
                            }
                        } catch (Throwable t) {
                            setError("Directshare Model Hook Invalid - " + t.toString());
                            return;
                        }

                        try {
                            mUserName = Helper.getFieldByType(model, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));

                            if (mUserName == null) {
                                mUserName = Helper.getOtherFieldByType(model, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
                            }

                            try {
                                mMedia = Helper.getOtherFieldByType(model, findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader));
                            } catch (Throwable t) {
                            }

                            if (mMedia == null) {
                                setError("Unable To Determine Media - DS");
                                return;
                            }

                            try {
                                downloadMedia(mMedia, "Direct");
                            } catch (Throwable t) {
                                setError("Direct Download Failed - " + t);
                                sendError();
                            }
                        } catch (Throwable t) {
                            try {
                                Field[] fields = model.getClass().getDeclaredFields();

                                for (Field field : fields) {
                                    try {
                                        String className = XposedHelpers.getObjectField(model, field.getName()).getClass().toString();
                                        if (className.contains("direct") && !className.contains("fragment")) {
                                            if (!XposedHelpers.getObjectField(model, field.getName()).toString().contains("ViewHolder") && !XposedHelpers.getObjectField(model, field.getName()).getClass().toString().contains(model.getClass().toString())) {
                                                model = XposedHelpers.getObjectField(model, field.getName());
                                                break;
                                            }
                                        }
                                    } catch (Throwable t2) {
                                    }
                                }

                                mUserName = Helper.getFieldByType(model, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));

                                if (mUserName == null) {
                                    mUserName = Helper.getOtherFieldByType(model, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
                                }

                                try {
                                    mMedia = Helper.getOtherFieldByType(model, findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader));
                                } catch (Throwable t2) {
                                }

                                if (mMedia == null) {
                                    setError("Unable To Determine Media - DS");
                                    return;
                                }

                                try {
                                    downloadMedia(mMedia, "Direct");
                                } catch (Throwable t2) {
                                    setError("Direct Download Failed - " + t2);
                                    sendError();
                                }
                            } catch (Throwable t2) {
                                setError("Directshare Image/Video Minimized - " + t2);
                            }
                        }

                        param.setResult(null);
                    } else if (Helper.getSettings("Order") && lowered.equals("Nope")) {
                        lowered = "Yes";
                        param.args[1] = ((int) param.args[1] - 1);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Directshare Dialog OnClick Hooks Invalid - " +t.toString());
        }
    }

    void hookDiscovery() {
        try {
            final Class<?> Discovery = XposedHelpers.findClass(DISCOVERY_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Discovery, View.class, LayoutInflater.class, ViewGroup.class, Bundle.class);

            XposedHelpers.findAndHookMethod(Discovery, methods[0].getName(), LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (Helper.getSettings("DiscoveryHide")) {
                        FrameLayout frameLayout = (FrameLayout) param.getResult();
                        frameLayout.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Discovery Class Failed - " +t);
            setError("Discovery Class - " +DISCOVERY_CLASS);
        }
    }

    void hookFeed() {
        if (directShareCheck.equals("Nope")) {
            try {
                Class<?> Media = findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Media, CharSequence[].class);

                findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), methods[0].getName(), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                        mCurrentMediaOptionButton = param.thisObject;

                        CharSequence[] result;
                        try {
                            result = (CharSequence[]) param.getResult();
                        } catch (Throwable t) {
                            setError("Profile Icon Failed - " + t.toString());
                            setError("Profile Icon Class - " + PROFILE_HOOK_CLASS);
                            sendError();
                            return;
                        }

                        param.setResult(injectDownload(result, "Feed"));
                    }
                });
            } catch (Throwable t) {
                try {
                    Class<?> Media = findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader);
                    Method[] methods = XposedHelpers.findMethodsByExactParameters(Media, CharSequence[].class, Media);

                    findAndHookMethod(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), methods[0].getName(), Media, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                            mCurrentMediaOptionButton = param.args[0];

                            CharSequence[] result;
                            try {
                                result = (CharSequence[]) param.getResult();
                            } catch (Throwable t) {
                                setError("Profile Icon Failed - " + t.toString());
                                setError("Profile Icon Class - " + PROFILE_HOOK_CLASS);
                                sendError();
                                return;
                            }

                            param.setResult(injectDownload(result, "Feed"));
                        }
                    });
                } catch (Throwable t2) {
                    setError("Media Options Button Hook Failed - " + t.toString());
                }
            }
        }

        try {
            final Class<?> MenuClickListener = findClass(FEED_CLASS_NAME, loadPackageParam.classLoader);
            final Method[] methods = XposedHelpers.findMethodsByExactParameters(MenuClickListener, void.class, DialogInterface.class, int.class);

            findAndHookMethod(MenuClickListener, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CharSequence localCharSequence = mMenuOptions[(Integer) param.args[1]];

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    String lockFeed;

                    try {
                        lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                    } catch (Throwable t) {
                        lockFeed = "Privacy Lock";
                    }


                    if (downloadCheck.equals(localCharSequence) || lockFeed.equals(localCharSequence)) {
                        param.setResult(null);

                        Object mMedia = null;
                        oContext = ((Dialog) param.args[0]).getContext();

                        try {
                            Field fields[] = MenuClickListener.getDeclaredFields();

                            if (fields.length == 1) {
                                mCurrentMediaOptionButton = XposedHelpers.getObjectField(param.thisObject, fields[0].getName());
                            } else {
                                for (Field field : fields) {
                                    String className = XposedHelpers.getObjectField(param.thisObject, field.getName()).getClass().toString();
                                    if (className.contains("android.feed")) {
                                        mCurrentMediaOptionButton = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            setError("Media Option Failed - " +t);
                        }

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(findClass(MEDIA_OPTIONS_BUTTON_CLASS, loadPackageParam.classLoader), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                        } catch (Throwable t) {
                            setError("Menu Click Hook Failed - " + t);
                        }

                        if (mMedia == null) {
                            setError("Unable To Determine Media - Feed");
                            return;
                        }

                        try {
                            if (lockFeed.equals(localCharSequence)) {
                                downloadMedia(mMedia, "Lock");
                            } else {
                                downloadMedia(mMedia, "Other");
                            }
                        } catch (Throwable t) {
                            setError("Download Media Failed: " +t.toString());
                            sendError();
                        }
                    } else if (Helper.getSettings("Order")) {
                        if (Helper.getSettings("Lock")) {
                            param.args[1] = ((int) param.args[1] - 2);
                        } else {
                            param.args[1] = ((int) param.args[1] - 1);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Menu Click Listener Failed - " + t.toString());
        }
    }

    void hookFollow() {
        try {
            XposedHelpers.findAndHookMethod(ViewGroup.class, "addView", View.class, int.class, ViewGroup.LayoutParams.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        View view = (View) param.args[0];
                        String viewName = view.getContext().getResources().getResourceEntryName(view.getId());

                        if (viewName.contains("outer_container") || viewName.contains("tray_divider_stub") || viewName.contains("tray_title") || viewName.contains("tray_sub_title_stub") || viewName.contains("tray_play_all_stub")) {
                            if (Helper.getSettings("StoryHide")) {
                                try {
                                    ViewGroup.LayoutParams params = view.getLayoutParams();
                                    params.height = 1;
                                    view.setLayoutParams(params);
                                    view.setVisibility(View.GONE);
                                } catch (Throwable t) {
                                }
                            }
                        }
                        if (viewName.contains("iglive_comment_list") || viewName.contains("reel_viewer_broadcast_cover") || viewName.contains("reel_viewer_attribution") || viewName.contains("comment_pin_viewstub") || viewName.contains("iglive_pinned_comment") || viewName.contains("video_reaction_button") || viewName.contains("avatar_likes_container") || viewName.contains("comment_prompt_text")) {
                            try {
                                views.add(view);
                                try {
                                    if (views.get(1).getVisibility() == View.GONE) {
                                        view.setVisibility(View.GONE);
                                    }
                                } catch (Throwable t) {
                                }
                            } catch (Throwable t) {
                            }
                        }
                        if (viewName.contains("comment_composer_edit_text")) {
                            try {
                                view.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        for (int i = 0; i < views.size(); i++) {
                                            try {
                                                if (views.get(i) == null) {
                                                    views.remove(i);
                                                }
                                            } catch (Throwable t) {
                                                views.remove(i);
                                            }
                                        }

                                        if (views.get(0).getVisibility() == View.GONE) {
                                            for (int i = 0; i < views.size(); i++) {
                                                try {
                                                    views.get(i).setVisibility(View.VISIBLE);
                                                } catch (Throwable t) {
                                                    views.remove(i);
                                                }
                                            }
                                        } else {
                                            for (int i = 0; i < views.size(); i++) {
                                                try {
                                                    views.get(i).setVisibility(View.GONE);
                                                } catch (Throwable t) {
                                                    views.remove(i);
                                                }
                                            }
                                        }
                                        return false;
                                    }
                                });
                            } catch (Throwable t) {
                            }
                        }
                        if (viewName.equalsIgnoreCase("row_profile_header_textview_following_count")) {
                            followerCount = (TextView) view;
                            String color = Helper.getSetting("Color");
                            if (color.equals("Instagram")) {
                                color = "#2E978C";
                            }
                            if (followed && followerCount.getCurrentTextColor() != Color.parseColor(color)) {
                                followed = false;
                                followerCount.setTextColor(Color.parseColor(color));
                            }

                            if (followingCountValue != 0) {
                                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
                                formatter .applyPattern("#,###");
                                final String followingCountString = formatter.format(followingCountValue);

                                followerCount.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                                        new ShowToast().execute("Following " + followingCountString + " Users");
                                        return false;
                                    }
                                });
                            }
                        }

                        if (viewName.equalsIgnoreCase("row_profile_header_textview_followers_count")) {
                            TextView textView = (TextView) view;

                            if (followerCountValue != 0) {
                                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
                                formatter .applyPattern("#,###");
                                final String followerCountString = formatter.format(followerCountValue);

                                textView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                                        new ShowToast().execute("Followed by " + followerCountString + " Users");
                                        return false;
                                    }
                                });
                            }
                        }
                    } catch (Throwable t) {
                    }
                }
            });
        } catch (Throwable t) {
            setError("Follow TextView Failed - " + t.toString());
        }

        try {
            XposedHelpers.findAndHookMethod(FOLLOW_HOOK_CLASS, loadPackageParam.classLoader, FOLLOW_HOOK, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        byte[] bytes = (byte[]) Helper.getFieldByType(param.thisObject, byte[].class);

                        JSONObject jObject = new JSONObject(new String(bytes, "UTF-8"));

                        try {
                            followerCountValue = jObject.getJSONObject("user").getInt("follower_count");
                            followingCountValue = jObject.getJSONObject("user").getInt("following_count");
                        } catch (Throwable t2) {
                        }

                        if (jObject.getString("followed_by").equals("true")) {
                            String color = Helper.getSetting("Color");
                            if (color.equals("Instagram")) {
                                color = "#2E978C";
                            }
                            followed = true;
                            followerCount.setTextColor(Color.parseColor(color));
                        }
                    } catch (Throwable t) {
                        try {
                            byte[] bytes = (byte[]) XposedHelpers.getObjectField(param.thisObject, FOLLOW_HOOK_2);

                            JSONObject jObject = new JSONObject(new String(bytes, "UTF-8"));

                            if (jObject.getString("followed_by").equals("true")) {
                                String color = Helper.getSetting("Color");
                                if (color.equals("Instagram")) {
                                    color = "#2E978C";
                                }
                                followed = true;
                                followerCount.setTextColor(Color.parseColor(color));
                            }
                        } catch (Throwable t2) {
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Follow Feature Failed - " + t.toString());
        }
    }

    void hookFollowList() {
        try {
            Class<?> followList = XposedHelpers.findClass(FOLLOW_LIST_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(followList, View.class, int.class, View.class, ViewGroup.class, Object.class, Object.class);

            XposedHelpers.findAndHookMethod(followList, methods[0].getName(), int.class, View.class, ViewGroup.class, Object.class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object[] objects = param.args;

                    for (Object object : objects) {
                        try {
                            if (object.getClass().getName().equals(USER_CLASS_NAME)) {
                                String followerName = (String) getObjectField(object, USERNAME_HOOK);

                                if (!Helper.getSetting("Following").contains(followerName)) {
                                    Helper.writeToFollower(followerName);
                                }
                            }
                        } catch (Throwable t) {
                        }
                    }
                }
            });
        }  catch (Throwable t) {
            setError("Follow List User Failed - " +t);
            setError("Follow List User Class - " +FOLLOW_LIST_CLASS);
        }

        try {
            Class<?> followList = XposedHelpers.findClass(FOLLOW_LIST_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(followList, int.class);

            XposedHelpers.findAndHookMethod(followList, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Helper.resetFollower();
                }
            });
        } catch (Throwable t) {
            setError("Follow List Failed - " +t);
            setError("Follow List Class - " +FOLLOW_LIST_CLASS);
        }
    }

    void hookInstagram() {
        try {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    hookComments();
                } catch (Throwable t) {
                    setError("Comments Failed: " + t);
                }

                try {
                    hookDate();
                } catch (Throwable t) {
                    setError("Date Failed: " + t);
                }

                try {
                    hookDialog();
                } catch (Throwable t) {
                    setError("Dialog Failed: " + t);
                }

                try {
                    hookDirectPrivate();
                } catch (Throwable t) {
                    setError("Direct Private Failed: " + t);
                }

                try {
                    hookDirectShare();
                } catch (Throwable t) {
                    setError("DirectShare Failed: " + t);
                }

                try {
                    hookDiscovery();
                } catch (Throwable t) {
                    setError("Discovery Failed: " + t);
                }

                try {
                    hookFeed();
                } catch (Throwable t) {
                    setError("Feed Failed: " + t);
                }

                try {
                    hookFollow();
                } catch (Throwable t) {
                    setError("Follow Failed: " + t);
                }

            try {
                hookFollowList();
            } catch (Throwable t) {
                setError("Follow List Failed: " + t);
            }

            try {
                hookLike();
            } catch (Throwable t) {
                setError("Like Failed: " + t);
            }

            try {
                hookLikedPost();
            } catch (Throwable t) {
                setError("Like Post Failed: " + t);
            }

            try {
                hookMiniFeed();
            } catch (Throwable t) {
                setError("Mini Feed Failed: " + t);
            }

            try {
                hookNotification();
            } catch (Throwable t) {
                setError("Notification Failed: " + t);
            }

            try {
                hookPin();
            } catch (Throwable t) {
                setError("Pin Failed:" + t);
            }

            try {
                hookProfileIcon();
            } catch (Throwable t) {
                setError("Profile Icon Failed: " + t);
            }

            try {
                hookScreenShotPrivacy();
            } catch (Throwable t) {
                setError("Screenshot Privacy Failed: " + t);
            }

            try {
                hookSearch();
            } catch (Throwable t) {
                setError("Search Failed: " + t);
            }

            try {
                hookShare();
            } catch (Throwable t) {
                setError("Share Failed: " + t);
            }

            try {
                hookSlide();
            } catch (Throwable t) {
                setError("Slide Failed: " + t);
            }

            try {
                hookSponsoredPost();
            } catch (Throwable t) {
                setError("Sponsored Post Failed: " + t);
            }

            try {
                hookStories();
            } catch (Throwable t) {
                setError("Stories Failed: " + t);
            }

            try {
                hookStoriesGallery();
            } catch (Throwable t) {
                setError("Stories Gallery Failed: " + t);
            }

            try {
                hookStoriesTimer();
            } catch (Throwable t) {
                setError("Stories Timer Failed: " + t);
            }

            try {
                hookStoriesViews();
            } catch (Throwable t) {
                setError("Stories Views Failed: " + t);
            }

            try {
                hookUserAgent();
            } catch (Throwable t) {
                setError("User Agent Failed: " + t);
            }

            try {
                hookVideosLikes();
            } catch (Throwable t) {
                setError("Video Likes Failed: " + t);
            }
                }
            });
        } catch (Throwable t) {
            setError("Multi-Dex Hook Issue" +t);
        }
    }

    void hookLike() {
        try {
            Class<?> Like = findClass(LIKE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Like, boolean.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(Like, methods[0].getName(), MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Like")) {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Like Hooks Failed - " + t.toString());
            setError("Like Hook Class - " +LIKE_HOOK_CLASS);
        }

        try {
            Class<?> Like = findClass(LIKE_HOOK_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Like, boolean.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(Like, methods[0].getName(), MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Like")) {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Like Multi Hooks Failed - " + t.toString());
            setError("Like Multi Hook Class - " +LIKE_HOOK_CLASS2);
        }
    }

    void hookLikedPost() {
        try {
            Class<?> Like = XposedHelpers.findClass(LIKED_POST_HOOK_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(Like, View.class, LayoutInflater.class, ViewGroup.class, Bundle.class);

            XposedHelpers.findAndHookMethod(Like, methods[0].getName(), LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        if (Helper.getSettings("LikePrivacy")) {
                            View view = (View) param.getResult();
                            view.setVisibility(View.GONE);
                        }
                    } catch (Throwable t) {
                    }
                }
            });
        } catch (Throwable t) {
            setError("Liked Post Failed - " +t);
            setError("Liked Post Class - " +LIKED_POST_HOOK_CLASS);
        }
    }

    void hookMiniFeed() {
        try {
            final Class<?> miniFeedOnClick = XposedHelpers.findClass(MINI_FEED_HOOK_CLASS, loadPackageParam.classLoader);
            final Method[] methods = XposedHelpers.findMethodsByExactParameters(miniFeedOnClick, void.class, DialogInterface.class, int.class);

            XposedHelpers.findAndHookMethod(miniFeedOnClick, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mMenuOptions[(int) param.args[1]].toString();

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                    oContext = ((Dialog) param.args[0]).getContext();


                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        param.setResult(null);

                        Object miniFeedObject = null;
                        Object mMedia = null;

                        try {
                            Field fields[] = miniFeedOnClick.getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    String className = XposedHelpers.getObjectField(param.thisObject, field.getName()).getClass().toString();

                                    if (className.contains("android.") || className.contains("com.instagram.feed")) {
                                        miniFeedObject = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                    }
                                } catch (Throwable t) {
                                }
                            }
                        } catch (Throwable t) {
                            setError("Mini Feed First Field Failed - " + t);
                            sendError();
                            return;
                        }

                        try {
                            Field fields[] = miniFeedObject.getClass().getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    String className = XposedHelpers.getObjectField(miniFeedObject, field.getName()).getClass().toString();
                                    if (className.contains("android.") || className.contains("com.instagram.feed")) {
                                        mMedia = XposedHelpers.getObjectField(miniFeedObject, field.getName());
                                        mMedia = Helper.getFieldByType(mMedia, XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader));
                                    }
                                } catch (Throwable t) {
                                }
                            }
                        } catch (Throwable t) {
                            setError("Mini Feed Second Field Failed - " + t);
                            sendError();
                            return;
                        }

                        downloadMedia(mMedia, "Other");
                    } else if (Helper.getSettings("Order")) {
                        param.args[1] = ((int) param.args[1] - 1);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Mini Feed OnClick Failed - " +t);
            setError("Mini Feed Class Failed - " +MINI_FEED_HOOK_CLASS);
        }
    }

    void hookNotification() {
        try {
        Class<?> Notification = findClass(NOTIFICATION_CLASS, loadPackageParam.classLoader);
            if (Helper.getSettings("Push")) {
                Method[] methods;
                try {
                    methods = XposedHelpers.findMethodsByExactParameters(Notification, void.class, Intent.class, String.class);

                    if (methods[0].equals("Check")) {
                    }
                } catch (Throwable t) {
                    try {
                        methods = Notification.getDeclaredMethods();

                        for (Method method : methods) {
                            if (method.toString().contains("Intent") && method.toString().contains("String")) {
                                XposedHelpers.findAndHookMethod(Notification, method.getName(), method.getParameterTypes()[0], method.getParameterTypes()[1], method.getParameterTypes()[2], new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                        super.afterHookedMethod(param);
                                        if (Helper.getSettings("Push")) {
                                            try {
                                                try {
                                                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                                                } catch (Throwable t) {
                                                    mContext = nContext;
                                                }

                                                Intent intent = (Intent) param.args[0];

                                                JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

                                                setError("GOt it: " +jsonObject);

                                                Helper.setPush("Push: " + jsonObject);

                                                String userHolder;

                                                if (jsonObject.getString("m").contains("]: ")) {
                                                    userHolder = jsonObject.getString("m").split("]: ")[1];
                                                } else {
                                                    userHolder = jsonObject.getString("m");
                                                }

                                                userName = userHolder.split(" ")[0];

                                                if (jsonObject.getString("collapse_key").equals("post") || jsonObject.getString("collapse_key").equals("resurrected_user_post")) {
                                                    String fileExtension = ".jpg";
                                                    String fileDescription;
                                                    try {
                                                        fileDescription = Helper.getResourceString(mContext, R.string.photo);
                                                    } catch (Throwable t) {
                                                        fileDescription = "Photo";
                                                    }

                                                    String itemId = jsonObject.getString("ig").replace("media?id=", "");
                                                    String mediaId = itemId.replace(itemId.split("_")[0], "");
                                                    itemId = itemId.replace(itemId.split("_")[1], "");

                                                    if (!Helper.getSetting("FileFormat").equals("Instagram") && !Helper.getSetting("File").equals("Instagram")) {
                                                        setError("Here");
                                                        fileName = Helper.getSetting("FileFormat");
                                                        fileName = fileName.replace("Username", userName);
                                                        fileName = fileName.replace("MediaID", mediaId);
                                                        fileName = fileName.replace("UserID", itemId);
                                                        fileName = fileName.replace("Date", Helper.getDateEpoch(System.currentTimeMillis(), mContext));
                                                        fileName = fileName + fileExtension;
                                                    } else if (!Helper.getSetting("FileFormat").equals("Instagram")) {
                                                        fileName = Helper.getSetting("FileFormat");
                                                        fileName = fileName.replace("Username", userName);
                                                        fileName = fileName.replace("MediaID", mediaId);
                                                        fileName = fileName.replace("UserID", itemId);
                                                        fileName = fileName.replace("Date", Helper.getDateEpoch(System.currentTimeMillis(), mContext));
                                                        fileName = fileName + fileExtension;
                                                    } else {
                                                        fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;
                                                    }

                                                    fileType = "Image";
                                                    linkToDownload = jsonObject.getString("i");

                                                    try {
                                                        notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                                                    } catch (Throwable t) {
                                                        notificationTitle = userName + "'s " + fileDescription;
                                                    }

                                                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                                                    SAVE = Helper.getSaveLocation(fileType);

                                                    linkToDownload = "media123;" + linkToDownload;

                                                    Helper.setPush("Pushed Post : " + userName);

                                                    setError("Username Started: " +fileName);

                                                    Helper.getPostUrl(mContext, linkToDownload, fileName, fileType, notificationTitle, userName);
                                                } else {
                                                    Helper.setPush("This is not a post.");
                                                }
                                            } catch (Throwable t) {
                                                setError("Push Notification Issue: " +t);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                        return;
                    } catch (Throwable t2) {
                        setError("New Push Notification Method Failed - " + t2);
                        Helper.setPush("Push Failed: " +t2);
                        return;
                    }
                }

                XposedHelpers.findAndHookMethod(Notification, methods[0].getName(), Intent.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (Helper.getSettings("Push")) {
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                            Intent intent = (Intent) param.args[0];
                            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));

                            String userHolder;

                            if (jsonObject.getString("m").contains("]: ")) {
                                userHolder = jsonObject.getString("m").split("]: ")[1];
                            } else {
                                userHolder = jsonObject.getString("m");
                            }

                            userName = userHolder.split(" ")[0];
                            String photoName = Helper.getString(mContext, "photo", loadPackageParam.packageName).toLowerCase();
                            String photoCheck = userHolder.replace(userName, "").toLowerCase();
                            String videoName = Helper.getString(mContext, "video", loadPackageParam.packageName).toLowerCase();
                            if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(photoName)) {
                                String fileExtension = ".jpg";
                                String fileDescription;
                                try {
                                    fileDescription = Helper.getResourceString(mContext, R.string.photo);
                                } catch (Throwable t) {
                                    fileDescription = "Photo";
                                }

                                fileName = userName + "_" + jsonObject.getString("ig").replace("media?id=", "") + fileExtension;

                                if (!Helper.getSetting("File").equals("Instagram")) {
                                    try {
                                        String itemToString = Helper.getDateEpoch(System.currentTimeMillis(), nContext);
                                        String itemId = jsonObject.getString("ig").replace("media?id=", "");

                                        itemId = itemId.replace(itemId.split("_")[1], "") + itemToString;

                                        fileName = userName + "_" + itemId + fileExtension;
                                    } catch (Throwable t) {
                                        setError("Auto Epoch Failed - " +t);
                                    }
                                }

                                fileType = "Image";
                                linkToDownload = jsonObject.getString("i");
                                try {
                                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, fileDescription);
                                } catch (Throwable t) {
                                    notificationTitle = userName + "'s " + fileDescription;
                                }
                                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                                SAVE = Helper.getSaveLocation(fileType);

                                long longId = System.currentTimeMillis() / 1000;

                                Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, mContext, false);
                            } else if (jsonObject.getString("collapse_key").equals("post") && photoCheck.contains(videoName)) {
                                linkToDownload = "https://www.instagram.com/" + userName + "/?__a=1";

                                long longId = System.currentTimeMillis() / 1000;

                                Helper.passDownload(linkToDownload, SAVE, notificationTitle, fileName, fileType, userName, longId, mContext);
                            }
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Notification Error: " + t.toString());
        }

    }

    void hookPin() {
        try {
            Class<?> Pin = findClass(PIN_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Pin, null);

            XposedHelpers.findAndHookMethod(Pin, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    final ImageView imageView = (ImageView) param.getResult();
                    oContext = imageView.getRootView().getContext();
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            try {
                                Object mMedia = null;

                                mCurrentMediaOptionButton = param.thisObject;
                                mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                                try {
                                    mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(mCurrentMediaOptionButton.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                                } catch (Throwable t) {
                                    setError("Menu Click Hook Failed - " + t);
                                }

                                if (mMedia == null) {
                                    setError("Unable To Determine Media - Feed");
                                }

                                try {
                                    if (Helper.getSetting("Alternate").equals("Hold")) {
                                        downloadMedia(mMedia, "Other");
                                    }
                                } catch (Throwable t) {
                                    setError("Download Media Failed: " + t.toString());
                                    sendError();
                                }
                            } catch (Throwable t) {
                                setError("Pin Hold Long Press Failed - " +t);
                            }
                            return true;
                        }
                    });
                }
            });
        } catch (Throwable t) {
        }

        try {
            Class<?> Pin = findClass(PIN_HOOK_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Pin, void.class, View.class);

            XposedHelpers.findAndHookMethod(Pin, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSetting("Alternate").equals("One")) {
                        param.setResult(false);
                    }
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (Helper.getSetting("Alternate").equals("One")) {
                        setError("One2");
                        Object mMedia = null;

                        mCurrentMediaOptionButton = param.thisObject;
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                        oContext = ((View) param.args[0]).getContext();

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(mCurrentMediaOptionButton.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                        } catch (Throwable t) {
                            setError("Menu Click Hook Failed - " + t);
                        }

                        if (mMedia == null) {
                            setError("Unable To Determine Media - Feed");
                        }

                        try {
                            downloadMedia(mMedia, "Other");
                        } catch (Throwable t) {
                            setError("Download Media Failed: " + t.toString());
                            sendError();
                        }
                        param.setResult(false);
                    } else if (Helper.getSetting("Alternate").equals("Double")) {
                        feedCount++;
                        if (feedCount >= 2) {
                            feedCount = 0;
                            Object mMedia = null;

                            mCurrentMediaOptionButton = param.thisObject;
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                            oContext = ((View) param.args[0]).getContext();

                            try {
                                mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(mCurrentMediaOptionButton.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                            } catch (Throwable t) {
                                setError("Menu Click Hook Failed - " + t);
                            }

                            if (mMedia == null) {
                                setError("Unable To Determine Media - Feed");
                            }

                            try {
                                downloadMedia(mMedia, "Other");
                            } catch (Throwable t) {
                                setError("Download Media Failed: " + t.toString());
                                sendError();
                            }
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Pin Press Failed - " +t);
            setError("Pin Press Class - " +PIN_HOOK_CLASS2);
        }

        try {
            Class<?> Pin = findClass(PIN_HOOK_CLASS3, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Pin, boolean.class, View.class, MotionEvent.class);

            XposedHelpers.findAndHookMethod(Pin, methods[0].getName(), View.class, MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    oContext = ((View) param.args[0]).getContext();

                    if (Helper.getSetting("Alternate").equals("Hold")) {
                        param.setResult(false);
                    } else if (Helper.getSetting("Alternate").equals("One") || Helper.getSetting("Alternate").equals("Double")) {
                        try {
                            MotionEvent motionEvent = (MotionEvent) param.args[1];

                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                Object mMedia = null;
                                mCurrentMediaOptionButton = param.thisObject;
                                mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                                try {
                                    mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(mCurrentMediaOptionButton.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                                } catch (Throwable t) {
                                    setError("Menu Click Hook Failed - " + t);
                                }

                                if (mMedia == null) {
                                    setError("Unable To Determine Media - Feed");
                                }

                                if (Helper.getSetting("Alternate").equals("One")) {
                                    //param.setResult(false);
                                    //downloadMedia(mMedia, "Other");
                                } else if (Helper.getSetting("Alternate").equals("Double")) {
                                    feedCount++;
                                    if (feedCount >= 2 ) {
                                        feedCount = 0;
                                        if (System.currentTimeMillis() - lastTap < 2000) {
                                            downloadMedia(mMedia, "Other");
                                        }
                                    } else {
                                        lastTap = System.currentTimeMillis();
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            setError("Pin Press New Touch Media Failed - " + t);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Pin Press New Touch Failed - " +t);
            setError("Pin Press New Touch Class - " +PIN_HOOK_CLASS3);
        }

        try {
            XposedHelpers.findAndHookMethod(PIN_HOOK_CLASS4, loadPackageParam.classLoader, "run", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        Object mMedia = null;

                        mCurrentMediaOptionButton = XposedHelpers.getObjectField(param.thisObject, XposedHelpers.findFirstFieldByExactType(param.thisObject.getClass(), findClass(PIN_HOOK_CLASS3, loadPackageParam.classLoader)).getName());
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        Field[] fields = mCurrentMediaOptionButton.getClass().getDeclaredFields();

                        String booleanCheck = "";

                        for (Field field: fields) {
                            if (field.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
                                booleanCheck = booleanCheck + XposedHelpers.getObjectField(mCurrentMediaOptionButton, field.getName()) + ";";
                            }
                        }

                        try {
                            mMedia = getObjectField(mCurrentMediaOptionButton, XposedHelpers.findFirstFieldByExactType(mCurrentMediaOptionButton.getClass(), findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)).getName());
                        } catch (Throwable t) {
                            setError("Menu Click Hook Failed - " + t);
                        }

                        if (mMedia == null) {
                            setError("Unable To Determine Media - Feed");
                        }

                        if (Helper.getSetting("Alternate").equals("Hold") && booleanCheck.contains("true")) {
                            downloadMedia(mMedia, "Other");
                        }
                    } catch (Throwable t) {
                        setError("Pin Hold Long/Click Press Failed - " +t);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Pin Press New Long Failed - " +t);
            setError("Pin Press New Long Class - " +PIN_HOOK_CLASS4);
        }

        try {
            Class<?> Pin = XposedHelpers.findClass(PIN_HOOK_CLASS5, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Pin, boolean.class, View.class);

            XposedHelpers.findAndHookMethod(Pin, methods[0].getName(), View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Object mMedia = Helper.getFieldByType(param.thisObject, XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader));


                    if (Helper.getSetting("Alternate").equals("Hold")) {
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                        downloadMedia(mMedia, "Other");
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Pin Press New New Long Failed - " +t);
            setError("Pin Press New New Long Class - " +PIN_HOOK_CLASS4);
        }
    }

    void hookProfileIcon() {
        if (directShareCheck.equals("Nope")) {
            try {
                Class<?> Profile = findClass(PROFILE_HOOK_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile, CharSequence[].class);

                findAndHookMethod(Profile, methods[0].getName(), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        CharSequence[] result;
                        try {
                            result = (CharSequence[]) param.getResult();
                        } catch (Throwable t) {
                            setError("Profile Icon Failed - " + t.toString());
                            setError("Profile Icon Class - " + PROFILE_HOOK_CLASS);
                            sendError();
                            return;
                        }

                        param.setResult(injectDownload(result, "Other"));
                    }
                });
            } catch (Throwable t) {
                setError("Profile Icon Failed - " + t.toString());
                setError("Profile Icon Class - " + PROFILE_HOOK_CLASS);
            }
        }

        try {
            final Class<?> Profile2 = findClass(PROFILE_HOOK_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Profile2, void.class, DialogInterface.class, int.class);

            findAndHookMethod(Profile2, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mMenuOptions[(int) param.args[1]].toString();

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    if (Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button).equals(localCharSequence)) {
                        param.setResult(null);

                        Class<?> ProfileUser;

                        try {
                            ProfileUser = findClass(USER_CLASS_NAME, loadPackageParam.classLoader);
                        } catch (Throwable t) {
                            setError("Profile OnClick Class Failed -" +t);
                            sendError();
                            return;
                        }

                        Object firstObject = null;

                        try {
                            Field fields[] = Profile2.getDeclaredFields();

                            if (fields.length == 1) {
                                firstObject = XposedHelpers.getObjectField(param.thisObject, fields[0].getName());
                            } else {
                                for (Field field : fields) {
                                    try {
                                        String className = XposedHelpers.getObjectField(param.thisObject, field.getName()).getClass().toString();
                                        if (className.contains("com.instagram.android")) {
                                            firstObject = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                        }
                                    } catch (Throwable t) {
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            setError("Profile First Field Failed - " + t);
                            sendError();
                            return;
                        }

                        Object secondObject;

                        try {
                            Field[] fields = firstObject.getClass().getDeclaredFields();
                            secondObject = XposedHelpers.findFirstFieldByExactType(firstObject.getClass(), ProfileUser);

                            count = 0;

                            for (Field field : fields) {
                                if (field.getType().equals(ProfileUser)) {
                                    count++;
                                    if (count == 2) {
                                        secondObject = XposedHelpers.getObjectField(firstObject, field.getName());
                                    }
                                }
                            }

                            if (count == 1) {
                                secondObject = Helper.getFieldByType(firstObject, ProfileUser);
                            }

                        } catch (Throwable e) {
                            setError("Profile Second Field Failed - " + e);
                            sendError();
                            return;
                        }

                        try {
                            Field[] fields = secondObject.getClass().getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    if (field.getType().equals(String.class)) {
                                        String link = (String) XposedHelpers.getObjectField(secondObject, field.getName());
                                        if (link.contains("http://") && link.contains(".jpg")) {
                                            linkToDownload = link;
                                        } else if (link.contains("https://") && link.contains(".jpg")) {
                                            linkToDownload = link;
                                        }
                                    }
                                } catch (Throwable t) {
                                }
                            }

                            //linkToDownload = linkToDownload.replace("s150x150/", "");
                        } catch (Throwable t) {
                            setError("Profile Link To Download Failed -" +t);
                            sendError();
                            return;
                        }

                        try {
                            userName = (String) XposedHelpers.getObjectField(secondObject, USERNAME_HOOK);
                            userFullName = (String) getObjectField(secondObject, FULLNAME__HOOK);
                        } catch (Throwable t) {
                            setError("Profile Icon Username Hooks Failed:  " + t);
                            sendError();
                            return;
                        }

                        try {
                            String fixedLink = Helper.getProfileIcon(userName);
                            if (!fixedLink.isEmpty()) {
                                linkToDownload = fixedLink;
                            }
                        } catch (Throwable t) {
                        }

                        if (Helper.getSettings("Username")) {
                            if (!userFullName.isEmpty()) {
                                userName = userFullName;
                            }
                        }

                        try {
                            notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userFullName, "Icon");
                        } catch (Throwable t) {
                            notificationTitle = userFullName + "'s Icon";
                        }

                        notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                        String dateFormat = String.valueOf(System.currentTimeMillis());

                        if (!Helper.getSetting("File").equals("Instagram")) {
                            dateFormat = Helper.getDateEpoch(System.currentTimeMillis(), nContext);
                        }

                        fileName = userName + "_"  + dateFormat +"_Profile.jpg";

                        SAVE = "";

                        fileType = "Profile";

                        long longId = System.currentTimeMillis() / 1000;

                        Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, mContext, false);
                    } else if (Helper.getSettings("Order")) {
                        param.args[1] = ((int) param.args[1] - 1);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Click Listener Failed - " + t.toString());
            setError("Profile Icon Click Listener Class - " +PROFILE_HOOK_CLASS2);
        }

        try {
            final Class<?> profileLongClick = XposedHelpers.findClass(PROFILE_ICON_CLASS2, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(profileLongClick, View.class, int.class, View.class, ViewGroup.class, Object.class, Object.class);

            XposedHelpers.findAndHookMethod(profileLongClick, methods[0].getName(), int.class, View.class, ViewGroup.class, Object.class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    View view = (View) param.args[1];
                    Object profileView;

                    try {
                        if (view.getTag().toString().contains(PROFILE_ICON_CLASS)) {
                            Object[] objects = param.args;

                            profileView = view.getTag();

                            for (Field field : profileView.getClass().getDeclaredFields()) {
                                if (field.getType().toString().contains("ImageView") && XposedHelpers.getObjectField(profileView, field.getName()) != null) {
                                    try {
                                        final ImageView imageView = (ImageView) XposedHelpers.getObjectField(profileView, field.getName());
                                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View view) {
                                                oContext = imageView.getContext();
                                                //userProfileIcon = userProfileIcon.replace("s150x150/", "");

                                                try {
                                                    String fixedLink = Helper.getProfileIcon(userName);
                                                    if (!fixedLink.isEmpty()) {
                                                        userProfileIcon = fixedLink;
                                                    }
                                                } catch (Throwable t) {
                                                }

                                                new Privacy().execute(userProfileIcon, "Other");
                                                return false;
                                            }
                                        });
                                    } catch (Throwable t) {
                                        setError("Profile Long Press ImageView Failed - " + t);
                                        setError("Profile Long Press ImageView Hook - " + PROFILE_ICON_CLASS);
                                    }
                                }
                            }

                            profileView = XposedHelpers.getObjectField(view.getTag(), "a");

                            for (Field field : profileView.getClass().getDeclaredFields()) {
                                if (field.getType().toString().contains("ImageView") && XposedHelpers.getObjectField(profileView, field.getName()) != null) {
                                    try {
                                        final ImageView imageView = (ImageView) XposedHelpers.getObjectField(profileView, field.getName());
                                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View view) {
                                                oContext = imageView.getContext();
                                                //userProfileIcon = userProfileIcon.replace("s150x150/", "");

                                                try {
                                                    String fixedLink = Helper.getProfileIcon(userName);
                                                    if (!fixedLink.isEmpty()) {
                                                        userProfileIcon = fixedLink;
                                                    }
                                                } catch (Throwable t) {
                                                }

                                                new Privacy().execute(userProfileIcon, "Other");
                                                return false;
                                            }
                                        });
                                    } catch (Throwable t) {
                                        setError("Profile Long Press ImageView Failed - " + t);
                                        setError("Profile Long Press ImageView Hook - " + PROFILE_ICON_CLASS);
                                    }
                                }
                            }

                            for (Object object : objects) {
                                Field[] fields = object.getClass().getDeclaredFields();
                                for (Field field : fields) {
                                    try {
                                        if (field.toString().contains(USER_CLASS_NAME)) {
                                            Object user = XposedHelpers.getObjectField(object, field.getName());
                                            Field[] fields2 = user.getClass().getDeclaredFields();
                                            for (Field field2 : fields2) {
                                                try {
                                                    String URL = XposedHelpers.getObjectField(user, field2.getName()).toString();
                                                    if (URL.contains(".jpg")) {
                                                        userProfileIcon = URL;

                                                        try {
                                                            userName = (String) getObjectField(user, USERNAME_HOOK);
                                                        } catch (Throwable t) {
                                                            setError("Profile Icon Failed Username - " +t);
                                                        }
                                                    }
                                                } catch (Throwable t) {
                                                }
                                            }
                                        }
                                    } catch (Throwable t) {
                                    }
                                }
                            }
                        }
                    } catch (Throwable t) {
                    }
                }
            });
        } catch (Throwable t) {
            setError("Profile Icon Long Press Failed - " +t);
            setError("Profile Icon Long Press Hook - " +PROFILE_ICON_CLASS2);
        }
    }

    void hookScreenShotPrivacy () {
        try {
            Class<?> sak = XposedHelpers.findClass(SCREENSHOT_CLASS, loadPackageParam.classLoader);

            for (Method method : sak.getDeclaredMethods()) {
                if (method.toString().contains("List") && method.toString().contains("String") && method.toString().contains(sak.getName())) {
                    try {
                        XposedHelpers.findAndHookMethod(sak, method.getName(), sak, String.class, List.class, boolean.class, int.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                param.args[1] = "Privacy";
                            }
                        });
                    } catch (Throwable t) {
                        try {
                            XposedHelpers.findAndHookMethod(sak, method.getName(), String.class, List.class, boolean.class, int.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    param.args[0] = "Privacy";
                                }
                            });
                        } catch (Throwable t2) {
                            setError("Screenshot Privacy Failed2 - " +t2);
                        }
                    }
                }
            }

        } catch (Throwable t) {
            setError("Screenshot Privacy Failed - " +t);
        }
    }

    void hookSearch() {
        try {
            Class<?> searchClass = XposedHelpers.findClass(SEARCH_HOOK_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(searchClass, List.class);

            for (Method method : methods) {
                XposedHelpers.findAndHookMethod(searchClass, method.getName(), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (Helper.getSettings("History")) {
                            List list = new ArrayList();
                            param.setResult(list);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Search Tag Hook Failed - " +t);
        }

        try {
            Class<?> searchClass = XposedHelpers.findClass(SEARCH_HOOK_CLASS2, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(searchClass, List.class);

            for (Method method : methods) {
                XposedHelpers.findAndHookMethod(searchClass, method.getName(), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (Helper.getSettings("History")) {
                            List list = new ArrayList();
                            param.setResult(list);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Search Location Hook Failed - " +t);
        }

        try {
            Class<?> searchClass = XposedHelpers.findClass(SEARCH_HOOK_CLASS3, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(searchClass, List.class);

            for (Method method : methods) {
                XposedHelpers.findAndHookMethod(searchClass, method.getName(), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (Helper.getSettings("History")) {
                            List list = new ArrayList();
                            param.setResult(list);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Search Username Hook Failed - " +t);
        }

        try {
            Class<?> searchClass = XposedHelpers.findClass(SEARCH_HOOK_CLASS4, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(searchClass, void.class);

            for (Method method : methods) {
                XposedHelpers.findAndHookMethod(SEARCH_HOOK_CLASS4, loadPackageParam.classLoader, method.getName(), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (Helper.getSettings("History")) {
                            param.setResult(null);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            setError("Search Top Hook Failed - " +t);
        }
    }

    void hookShare() {
        try {
            Class<?> shareClass = XposedHelpers.findClass(SHARE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(shareClass, void.class, Object.class);

            XposedHelpers.findAndHookMethod(shareClass, methods[0].getName(), Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                    String fieldName = "";

                    Field[] fields = param.args[0].getClass().getDeclaredFields();

                    for (Field field : fields) {
                        if (field.getType().equals(String.class)) {
                            fieldName = field.getName();
                        }
                    }

                    String linkToShare = (String) XposedHelpers.getObjectField(param.args[0], fieldName);

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                    i.putExtra(Intent.EXTRA_TEXT, linkToShare);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (i.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(i);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Share Class Failed - " +t);
            setError("Share Class - " +SHARE_HOOK_CLASS);
        }
    }

    void hookSlide() {
        try {
            XposedHelpers.findAndHookMethod(SLIDE_HOOK_CLASS, loadPackageParam.classLoader, SLIDE_HOOK, MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Slide") || Helper.getSettings("Freeze")) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Slide Hook Failed - " +t);
        }
    }

    void hookSponsoredPost () {
        try {
            Class<?> sponsoredClass = XposedHelpers.findClass(SPONSORED_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(sponsoredClass, View.class, int.class, View.class, ViewGroup.class, Object.class, Object.class);

            XposedHelpers.findAndHookMethod(sponsoredClass, methods[0].getName(), int.class, View.class, ViewGroup.class, Object.class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object[] objects = param.args;

                    for (Object object : objects) {
                        try {
                            if (object.getClass().getName().equals(MEDIA_CLASS_NAME)) {
                                View view = (View) param.args[1];

                                Object adCheck = XposedHelpers.getObjectField(object, SPONSORED_HOOK);
                                if (adCheck != null) {
                                    if (view.getVisibility() != View.GONE) {
                                        ViewGroup.LayoutParams params = view.getLayoutParams();
                                        params.height = 1;
                                        view.setLayoutParams(params);
                                        view.setVisibility(View.GONE);
                                    }
                                } else {
                                    try {
                                        Object paidCheck = XposedHelpers.getObjectField(object, PAID_HOOK);
                                        if (paidCheck != null) {
                                            if (view.getVisibility() != View.GONE) {
                                                if (Helper.getSettings("SponsorPost")) {
                                                    ViewGroup.LayoutParams params = view.getLayoutParams();
                                                    params.height = 1;
                                                    view.setLayoutParams(params);
                                                    view.setVisibility(View.GONE);
                                                }
                                            }
                                        } else {
                                            ViewGroup.LayoutParams params = view.getLayoutParams();
                                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                            view.setLayoutParams(params);
                                            view.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Throwable t) {
                                        ViewGroup.LayoutParams params = view.getLayoutParams();
                                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        view.setLayoutParams(params);
                                        view.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } catch (NullPointerException e) {
                            try {
                                View view = (View) param.args[1];
                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                view.setLayoutParams(params);
                                view.setVisibility(View.VISIBLE);
                            } catch (Throwable t2) {
                            }
                        }
                    }
                }
            });
        } catch (Throwable t) {
            setError("Sponsored Post Hook Failed - " + t);
            setError("Sponsored Post Class - " + SPONSORED_HOOK_CLASS);
        }
    }

    void hookStories() {
        try {
            final Class<?> storiesOnClick = XposedHelpers.findClass(STORY_HOOK_CLASS, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(storiesOnClick, void.class, DialogInterface.class, int.class);

            XposedHelpers.findAndHookMethod(storiesOnClick, methods[0].getName(), DialogInterface.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String localCharSequence = mMenuOptions[(Integer) param.args[1]].toString();

                    mContext = AndroidAppHelper.currentApplication().getApplicationContext();
                    oContext = ((Dialog) param.args[0]).getContext();

                    String downloadCheck;

                    try {
                        downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                    } catch (Throwable t) {
                        downloadCheck = "Download";
                    }

                    if (downloadCheck.equals(localCharSequence)) {
                        param.setResult(null);

                        Class<?> downloadSupport = XposedHelpers.findClass(STORY_HOOK, loadPackageParam.classLoader);
                        Class<?> feedClass = XposedHelpers.findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader);

                        Object storiesObject = null;
                        Object mMedia = null;

                        try {
                            Field fields[] = storiesOnClick.getDeclaredFields();

                            for (Field field : fields) {
                                try {
                                    String className = XposedHelpers.getObjectField(param.thisObject, field.getName()).getClass().toString();
                                    if (className.contains("feed.reels") || className.contains("instagram.reels")) {
                                        storiesObject = XposedHelpers.getObjectField(param.thisObject, field.getName());
                                        mMedia = Helper.getFieldByType(storiesObject, downloadSupport);
                                        mMedia = Helper.getFieldByType(mMedia, feedClass);

                                        try {
                                            if (mMedia == null) {
                                                setError("Trying Live Story");
                                                mMedia = Helper.getFieldByType(storiesObject, downloadSupport);

                                                Object mUser;
                                                try {
                                                    mUser = Helper.getFieldByType(mMedia, findClass(USER_CLASS_NAME, loadPackageParam.classLoader));
                                                } catch (Throwable t) {
                                                    setError("mUser Hook Invalid - " + USER_CLASS_NAME);
                                                    sendError();
                                                    return;
                                                }

                                                try {
                                                    userName = (String) getObjectField(mUser, USERNAME_HOOK);
                                                    userFullName = (String) getObjectField(mUser, FULLNAME__HOOK);
                                                } catch (Throwable t) {
                                                    setError("Failed to get User from Media, using placeholders");
                                                    userName = "username_placeholder";
                                                    userFullName = "Unknown name";
                                                }

                                                if (Helper.getSettings("Username")) {
                                                    if (!userFullName.isEmpty()) {
                                                        userName = userFullName;
                                                    }
                                                }

                                                Field[] mMediaFields = mMedia.getClass().getDeclaredFields();

                                                for (Field mMediaField: mMediaFields) {
                                                    if (mMediaField.getType().getName().contains(STORY_HOOK.replace(STORY_HOOK.split("\\.")[STORY_HOOK.split("\\.").length - 1], ""))) {
                                                        mMedia = XposedHelpers.getObjectField(mMedia, mMediaField.getName());
                                                    }
                                                }

                                                Field[] fields1 = mMedia.getClass().getDeclaredFields();

                                                long epochMax = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
                                                long epochMin = epochMax - 87000;

                                                String string = "";
                                                String epoch = "";

                                                for (Field field123 : fields1) {
                                                    try {
                                                        if (field123.getType().getName().equals(long.class.getName())) {
                                                            long check = XposedHelpers.getLongField(mMedia, field123.getName());

                                                            if (check > epochMin && check < epochMax) {
                                                                epoch = String.valueOf(check);
                                                            }
                                                        }

                                                        String checkString = (String) XposedHelpers.getObjectField(mMedia, field123.getName());

                                                        if (checkString.contains("<?xml version=") && checkString.contains("MPD file")) {
                                                            string = checkString;
                                                        } else if (checkString.contains("BaseURL") && checkString.contains("audioSamplingRate")) {
                                                            string = checkString;
                                                        }
                                                    } catch (Throwable t) {
                                                    }
                                                }

                                                if (!Helper.getSetting("File").equals("Instagram")) {
                                                    epoch = Helper.getDateEpochWithTime(Long.parseLong(epoch), mContext);
                                                }

                                                String audioString = "";

                                                String videoString = "";

                                                String[] representation = string.split("<Representation");


                                                for (String value : representation) {
                                                    String baseURL = value.split("</BaseURL>")[0];

                                                    if (baseURL.contains("video/mp4")) {
                                                        videoString = baseURL.split("<BaseURL>")[1];
                                                    } else if (baseURL.contains("audio/mp4")) {
                                                        audioString = baseURL.split("<BaseURL>")[1];
                                                    }
                                                }

                                                linkToDownload = audioString;

                                                try {
                                                    notificationTitle = Helper.getResourceString(mContext, R.string.username_thing, userName, "Live Story Audio");
                                                } catch (Throwable t) {
                                                    notificationTitle = userName + "'s Live Story Audio";
                                                }

                                                notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);

                                                fileName = userName + "_LiveAudio_"  + epoch + ".mp4";

                                                fileType = "Video";
                                                SAVE = Helper.getSaveLocation(fileType);

                                                linkToDownload = linkToDownload + ";" + videoString;

                                                mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                                new DownloadLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, linkToDownload, userName, fileName);

                                                return;
                                            }
                                        } catch (Throwable t) {
                                        }

                                    }
                                } catch (Throwable t) {
                                }
                            }
                        } catch (Throwable t) {
                            setError("Stories First Field Failed - " + t);
                            sendError();
                            return;
                        }

                        downloadMedia(mMedia, "Other");
                    } else if (Helper.getSettings("Order")) {
                        param.args[1] = ((int) param.args[1] - 1);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Stories OnClick Failed - " +t);
            setError("Stories OnClick Class - " +STORY_HOOK_CLASS);
        }


        if (directShareCheck.equals("Nope")) {
            try {
                Class<?> storiesInject = XposedHelpers.findClass(STORY_HOOK_CLASS2, loadPackageParam.classLoader);

                Method[] methods = XposedHelpers.findMethodsByExactParameters(storiesInject, Dialog.class, CharSequence[].class, DialogInterface.OnClickListener.class, DialogInterface.OnDismissListener.class);

                XposedHelpers.findAndHookMethod(storiesInject, methods[0].getName(), CharSequence[].class, DialogInterface.OnClickListener.class, DialogInterface.OnDismissListener.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                        CharSequence[] result;
                        try {
                            result = (CharSequence[]) param.args[0];
                        } catch (Throwable t) {
                            setError("Stories Inject Failed - " + t.toString());
                            sendError();
                            return;
                        }

                        param.args[0] = injectDownload(result, "Other");
                    }
                });
            } catch (Throwable t) {
                setError("Stories Inject Failed - " + t);
            }
        }
    }

    void hookStoriesGallery() {
        try {
            Class<?> storiesGallery = XposedHelpers.findClass(STORY_GALLERY_CLASS, loadPackageParam.classLoader);

            XposedHelpers.findAndHookConstructor(storiesGallery, Context.class, int.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.args[3] = 1;
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> storiesGallery = XposedHelpers.findClass(STORY_GALLERY_CLASS, loadPackageParam.classLoader);

                XposedHelpers.findAndHookConstructor(storiesGallery, ContentResolver.class, int.class, int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[3] = 1;
                    }
                });
            } catch (Throwable t2) {
                try {
                    Class<?> storiesGallery = XposedHelpers.findClass(STORY_GALLERY_CLASS, loadPackageParam.classLoader);

                    XposedHelpers.findAndHookConstructor(storiesGallery, ContentResolver.class, int.class, int.class, int.class, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[3] = 1;
                        }
                    });
                } catch (Throwable t3) {
                    setError("Stories Gallery Hook Failed - " + t3);
                    setError("Stories Gallery Hook Class - " + STORY_GALLERY_CLASS);
                }
            }
        }
    }

    void hookStoriesTimer() {
        try {
            //MediaPlayer Stories Video Hook
            Class<?> mediaPlayer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS, loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(mediaPlayer, STORY_TIME_HOOK, MediaPlayer.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Story")) {
                        param.setResult(null);
                        MediaPlayer mediaPlayer = (MediaPlayer) param.args[0];
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                }
            });
        } catch (Throwable t) {
        }

        try {
            //ExoPlayer Stories Video Hook
            Class<?> mediaPlayer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS3, loadPackageParam.classLoader);

            Method[] methods = XposedHelpers.findMethodsByExactParameters(mediaPlayer, void.class, boolean.class);

            XposedHelpers.findAndHookMethod(mediaPlayer, methods[0].getName(),  boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Story")) {
                        param.args[0] = true;
                    }
                }
            });
        } catch (Throwable t) {
            setError("Stories Video Timer Failed - " +t);
            setError("Stories Video Timer Class3 - " +STORY_TIME_HOOK_CLASS3);
        }

        try {
            Class<?> storyTimer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS2, loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(storyTimer, STORY_TIME_HOOK2, Object.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("Story")) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> storyTimer = XposedHelpers.findClass(STORY_TIME_HOOK_CLASS2, loadPackageParam.classLoader);

                XposedHelpers.findAndHookMethod(storyTimer, STORY_TIME_HOOK2, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (Helper.getSettings("Story")) {
                            param.setResult(null);
                        }
                    }
                });
            } catch (Throwable t2) {
                setError("Stories Timer Failed - " +t2);
            }
        }
    }

    void hookStoriesViews() {
        try {
            XposedHelpers.findAndHookMethod(STORY_TIME_HOOK_CLASS2, loadPackageParam.classLoader, STORY_VIEWS_HOOK, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Helper.getSettings("StoryPrivacy")) {
                        param.setResult(null);
                    }
                }
            });
        } catch (Throwable t) {
            setError("Story Privacy Hook Failed - " +t);
            setError("Story Privacy Hook Class - " +STORY_TIME_HOOK_CLASS2);
            setError("Story Privacy Hook Hook - " +STORY_VIEWS_HOOK);
        }
    }

    void hookUserAgent() {
        try {
            Class<?> userAgentClass = XposedHelpers.findClass("com.instagram.api.useragent.a", loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(userAgentClass, String.class);

            XposedHelpers.findAndHookMethod("com.instagram.api.useragent.a", loadPackageParam.classLoader, methods[0].getName(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        String changeResult = (String) param.getResult();

                        if (Helper.getSettings("Resolution")) {
                            int splitCount = 0;

                            for (String splitString : changeResult.split(";")) {
                                if (splitString.contains("dpi")) {
                                    changeResult = changeResult.replace(changeResult.split(";")[splitCount], " 440dpi");
                                    changeResult = changeResult.replace(changeResult.split(";")[splitCount + 1], " 1080x1920");
                                }
                                splitCount++;
                            }
                        }

                        param.setResult(changeResult);
                    } catch (Throwable t) {
                        setError("Useragent Swap Failed - " +t);
                    }
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> userAgentClass = XposedHelpers.findClass(USER_AGENT_CLASS, loadPackageParam.classLoader);
                Method[] methods = XposedHelpers.findMethodsByExactParameters(userAgentClass, String.class);

                XposedHelpers.findAndHookMethod(userAgentClass, methods[0].getName(), new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            String changeResult = (String) param.getResult();

                            if (Helper.getSettings("Resolution")) {
                                int splitCount = 0;

                                for (String splitString : changeResult.split(";")) {
                                    if (splitString.contains("dpi")) {
                                        changeResult = changeResult.replace(splitString, " 420dpi");
                                        changeResult = changeResult.replace(changeResult.split(";")[splitCount + 1], " 1080x1920");
                                    }
                                    splitCount++;
                                }
                            }

                            param.setResult(changeResult);
                        } catch (Throwable t) {
                            setError("Useragent Swap Failed - " +t);
                        }
                    }
                });
            } catch (Throwable t2) {
                setError("Useragent Hook Failed - " +t2);
            }
        }
    }

    void hookVideosLikes() {
        try {
            Class<?> Video = XposedHelpers.findClass(VIDEO_LIKE_HOOK_CLASS, loadPackageParam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Video, boolean.class, int.class);

            XposedHelpers.findAndHookMethod(Video, methods[0].getName(), int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try {
                        TextView textView = (TextView) Helper.getFieldByType(param.thisObject, findClass(LOCK_HOOK8, loadPackageParam.classLoader));
                        int likesCount = (int) XposedHelpers.getObjectField(Helper.getFieldByType(param.thisObject, findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader)), VIDEO_LIKE_HOOK);
                        DecimalFormat formatter = new DecimalFormat("#,###,###");

                        mContext = ((View) Helper.getFieldByType(param.thisObject, View.class)).getContext();
                        String likes = Helper.getString(mContext, "likes", "com.instagram.android").toLowerCase();
                        String views = Helper.getString(mContext, "views", "com.instagram.android").toLowerCase();

                        if (textView.getText().toString().contains(views)) {
                            if (Helper.getSettings("VideoLikes")) {
                                mContext = ((View) Helper.getFieldByType(param.thisObject, View.class)).getContext();
                                textView.setText("♥ " + formatter.format(likesCount) + " " + likes + "   ▶️ " + formatter.format(Integer.parseInt(textView.getText().toString().replaceAll("[^0-9]", ""))) + " " + views);
                                textView.setTypeface(Typeface.DEFAULT_BOLD);
                            }
                        }
                    } catch (Throwable t) {
                        setError("Video Like Hook Issue- " +t);
                    }
                }
            });
        } catch (Throwable t) {
            try {
                Class<?> Video = XposedHelpers.findClass(VIDEO_LIKE_HOOK_CLASS, loadPackageParam.classLoader);

                Method[] videoMethods = Video.getDeclaredMethods();

                for (Method method : videoMethods) {
                    try {
                        if (method.getParameterTypes()[0].equals(Resources.class) && method.getParameterTypes()[2].equals(boolean.class) && method.getParameterTypes()[3].equals(int.class)) {
                            if (!MEDIA_CLASS_NAME.equals(method.getParameterTypes()[1].getCanonicalName())) {
                                setError("Forcing Media Class");
                                MEDIA_CLASS_NAME = method.getParameterTypes()[1].getCanonicalName();
                            }
                        }
                    } catch (Throwable t3) {
                    }
                }

                Method[] methods = XposedHelpers.findMethodsByExactParameters(Video, SpannableStringBuilder.class, Resources.class, findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader), boolean.class, int.class);

                XposedHelpers.findAndHookMethod(VIDEO_LIKE_HOOK_CLASS, loadPackageParam.classLoader, methods[0].getName(), Resources.class, findClass(MEDIA_CLASS_NAME, loadPackageParam.classLoader), boolean.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            String result = param.getResult().toString();
                            int likesCount = (int) XposedHelpers.getObjectField(param.args[1], VIDEO_LIKE_HOOK);
                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            mContext = AndroidAppHelper.currentApplication().getApplicationContext();

                            Resources resources = (Resources) param.args[0];
                            String likes = resources.getString(resources.getIdentifier("likes", "string", "com.instagram.android")).toLowerCase();
                            String views = resources.getString(resources.getIdentifier("views", "string", "com.instagram.android")).toLowerCase();

                            if (result.contains(views)) {
                                if (Helper.getSettings("VideoLikes")) {
                                    SpannableStringBuilder span = (SpannableStringBuilder) param.getResult();
                                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("♥ " + formatter.format(likesCount) + " " + likes + "   ▶️ " + span);
                                    final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                                    spannableStringBuilder.setSpan(bold, 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    param.setResult(spannableStringBuilder);
                                }
                            }
                        } catch (Throwable t) {
                            setError("Video Like Hook2 Issue- " +t);
                        }
                    }
                });
            } catch (Throwable t2) {
                setError("Video Like Failed - " + t2);
                setError("Video Like Class - " + VIDEO_LIKE_HOOK_CLASS);
            }
        }
    }

    @Override
    public void init() {
    }

    void sendError() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.ihelp101.instagram", "com.ihelp101.instagram.ErrorActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    void setError(String status) {
        if (!Helper.getSettings("Log")) {
            XposedBridge.log("XInsta - " + status);
        }

        Helper.setError(status);
    }

    void startHooks() {
        List<PackageInfo> packs = nContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p.packageName.equals("com.instagram.android")) {
                version = Integer.toString(p.versionCode);
                version = version.substring(0, version.length() - 2);
            }
        }

        String[] split = Helper.getSetting("Hooks").split(";");

        try {
            FEED_CLASS_NAME = split[1];
            firstHook = split[1];
            MEDIA_CLASS_NAME = split[2];
            USER_CLASS_NAME = split[4];
            MEDIA_OPTIONS_BUTTON_CLASS = split[5];
            DS_MEDIA_OPTIONS_BUTTON_CLASS = split[6];
            DS_PERM_MORE_OPTIONS_DIALOG_CLASS = split[7];
            PERM__HOOK = split[10];
            MEDIA_VIDEO_HOOK = split[14];
            MEDIA_PHOTO_HOOK = split[15];
            USERNAME_HOOK = split[16];
            FULLNAME__HOOK = split[17];
        } catch (ArrayIndexOutOfBoundsException e) {
            HookCheck = "Yes";
        }

        try {
            IMAGE_HOOK_CLASS = split[18];
        } catch (ArrayIndexOutOfBoundsException e) {
            oldCheck = "Yes";
        }

        try {
            ITEMID_HOOK = split[20];
            COMMENT_HOOK_CLASS = split[21];
            COMMENT_HOOK = split[22];
            COMMENT_HOOK_CLASS2 = split[23];
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        try {
            DS_DIALOG_CLASS = split[24];
            directShareCheck = "Yes";
        } catch (ArrayIndexOutOfBoundsException e) {
            directShareCheck = "Nope";
        }

        try {
            PROFILE_HOOK_CLASS = split[29];
            PROFILE_HOOK_CLASS2 = split[30];
            FOLLOW_HOOK_2 = split[31];
            PROFILE_HOOK_3 = split[33];
            PROFILE_HOOK_4 = split[34];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LIKE_HOOK_CLASS = split[35];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SUGGESTION_HOOK_CLASS = split[39];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            FOLLOW_HOOK_CLASS = split[41];
            FOLLOW_HOOK = split[42];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            TIME_HOOK_CLASS = split[43];
        } catch (ArrayIndexOutOfBoundsException e) {
            TIME_HOOK_CLASS = "Nope";
        }

        try {
            NOTIFICATION_CLASS = split[44];
        } catch (ArrayIndexOutOfBoundsException e) {
            NOTIFICATION_CLASS = "Nope";
        }

        try {
            VIDEO_HOOK_CLASS = split[45];
        } catch (ArrayIndexOutOfBoundsException e) {
            VIDEO_HOOK_CLASS = "Nope";
        }

        try {
            STORY_HOOK_CLASS = split[46];
            STORY_HOOK_CLASS2 = split[47];
            STORY_HOOK = split[48];
            STORY_TIME_HOOK_CLASS = split[49];
            STORY_TIME_HOOK = split[50];
            STORY_TIME_HOOK_CLASS2 = split[51];
            STORY_TIME_HOOK2 = split[52];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            MINI_FEED_HOOK_CLASS = split[53];
            MINI_FEED_HOOK_CLASS2 = split[54];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            FOLLOW_LIST_CLASS = split[55];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SHARE_HOOK_CLASS = split[57];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            TAGGED_HOOK_CLASS = split[58];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            VIDEO_HOOK = split[59];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SLIDE_HOOK_CLASS = split[60];
            SLIDE_HOOK = split[61];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LOCK_HOOK8 = split[70];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            STORY_GALLERY_CLASS = split[72];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SEARCH_HOOK_CLASS = split[73];
            SEARCH_HOOK_CLASS2 = split[74];
            SEARCH_HOOK_CLASS3 = split[75];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SPONSORED_HOOK_CLASS = split[76];
            SPONSORED_HOOK = split[77];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            VIDEO_LIKE_HOOK_CLASS = split[78];
            VIDEO_LIKE_HOOK = split[79];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            STORY_TIME_HOOK_CLASS3 = split[80];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            SEARCH_HOOK_CLASS4 = split[81];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PIN_HOOK_CLASS = split[82];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PIN_HOOK_CLASS2 = split[83];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PROFILE_ICON_CLASS = split[84];
            PROFILE_ICON_HOOK = split[86];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            CAROUSEL_HOOK = split[87];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LIKE_HOOK_CLASS2 = split[88];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            STORY_VIEWS_HOOK = split[89];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PROFILE_ICON_CLASS2 = split[90];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PIN_HOOK_CLASS3 = split[91];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PIN_HOOK_CLASS4 = split[92];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            DS_PRIVATE_CLASS = split[93];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            DS_PRIVATE_CLASS2 = split[94];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            DS_PRIVATE_CLASS3 = split[95];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            DS_PRIVATE_HOOK = split[96];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PIN_HOOK_CLASS5 = split[97];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            LIKED_POST_HOOK_CLASS = split[98];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            PAID_HOOK = split[99];
        } catch (ArrayIndexOutOfBoundsException e) {
        }


        try {
            SCREENSHOT_CLASS = split[100];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            DISCOVERY_CLASS = split[101];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            USER_AGENT_CLASS = split[102];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (HookCheck.equals("Yes") || !version.equalsIgnoreCase(split[0])) {
            try {
                updateHooks();
            } catch (Throwable t) {

            }
        }

        if (HookCheck.equals("Yes")) {
            setError("Please update your hooks via the module.");
        } else {
            try {
                packs = nContext.getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo p = packs.get(i);
                    if (p.packageName.equals("com.instagram.android")) {
                        version = Integer.toString(p.versionCode);
                        version = version.substring(0, version.length() - 2);
                    }
                }

                split = Helper.getSetting("Hooks").split(";");

                if (!version.equalsIgnoreCase(split[0])) {
                    try {
                        PackageTree classTree = new PackageTree(loadPackageParam.appInfo.sourceDir, loadPackageParam.classLoader);

                        //ProfileServer server = new ProfileServer(8282, loadPackageParam.classLoader, classTree); //packageTree is nullable
                        //server.start();

                        if (Helper.getSettings("ExperimentalHooks")) {
                            setError("Experimental Hooks Set");

                            COMMENT_HOOK_CLASS = Helper.loadProfiledClass(Helper.getComments(), classTree, COMMENT_HOOK_CLASS);
                            DS_DIALOG_CLASS = Helper.loadProfiledClass(Helper.getDialog(), classTree, DS_DIALOG_CLASS);
                            DS_PRIVATE_CLASS = Helper.loadProfiledClass(Helper.getDirectPrivate(), classTree, DS_PRIVATE_CLASS);
                            DS_PRIVATE_CLASS2 = Helper.loadProfiledClass(Helper.getDirectPrivateHelper(), classTree, DS_PRIVATE_CLASS2);
                            DS_PRIVATE_CLASS3 = Helper.loadProfiledClass(Helper.getDirectPrivateHelper2(), classTree, DS_PRIVATE_CLASS3);
                            FEED_CLASS_NAME = Helper.loadProfiledClass(Helper.getFeedOnClick(FEED_CLASS_NAME), classTree, FEED_CLASS_NAME);
                            FOLLOW_LIST_CLASS = Helper.loadProfiledClass(Helper.getFollowList(), classTree, FOLLOW_LIST_CLASS);
                            IMAGE_HOOK_CLASS = Helper.loadProfiledClass(Helper.getModel(), classTree, IMAGE_HOOK_CLASS);
                            LIKE_HOOK_CLASS = Helper.loadProfiledClass(Helper.getLikeOnClick(), classTree, LIKE_HOOK_CLASS);
                            LIKE_HOOK_CLASS2 = Helper.loadProfiledClass(Helper.getLikeMulti(), classTree, LIKE_HOOK_CLASS2);
                            LIKED_POST_HOOK_CLASS = Helper.loadProfiledClass(Helper.getProfileLiked(), classTree, LIKED_POST_HOOK_CLASS);
                            MEDIA_OPTIONS_BUTTON_CLASS = Helper.loadProfiledClass(Helper.getFeedHelper(), classTree, MEDIA_OPTIONS_BUTTON_CLASS);
                            MINI_FEED_HOOK_CLASS = Helper.loadProfiledClass(Helper.getMini(), classTree, MINI_FEED_HOOK_CLASS);
                            NOTIFICATION_CLASS = Helper.loadProfiledClass(Helper.getNotification(), classTree, NOTIFICATION_CLASS);
                            PROFILE_HOOK_CLASS2 = Helper.loadProfiledClass(Helper.getProfile(), classTree, PROFILE_HOOK_CLASS2);
                            PROFILE_ICON_CLASS2 = Helper.loadProfiledClass(Helper.getProfileLongPress(), classTree, PROFILE_HOOK_CLASS2);
                            SPONSORED_HOOK_CLASS = Helper.loadProfiledClass(Helper.getSponsored(SPONSORED_HOOK_CLASS), classTree, SPONSORED_HOOK_CLASS);
                            STORY_HOOK_CLASS = Helper.loadProfiledClass(Helper.getStory(), classTree, STORY_HOOK_CLASS);
                            STORY_TIME_HOOK_CLASS = Helper.loadProfiledClass(Helper.getStoryTime(), classTree, STORY_TIME_HOOK_CLASS);
                            STORY_TIME_HOOK_CLASS2 = Helper.loadProfiledClass(Helper.getStoryTime(), classTree, STORY_TIME_HOOK_CLASS2);
                            STORY_TIME_HOOK_CLASS3 = Helper.loadProfiledClass(Helper.getVideoPlayer(), classTree, STORY_TIME_HOOK_CLASS3);
                            STORY_GALLERY_CLASS = Helper.loadProfiledClass(Helper.getStoryGallery(), classTree, STORY_GALLERY_CLASS);
                            TIME_HOOK_CLASS = Helper.loadProfiledClass(Helper.getDate(), classTree, TIME_HOOK_CLASS);
                            USER_CLASS_NAME = Helper.loadProfiledClass(Helper.getUser(), classTree, USER_CLASS_NAME);
                            VIDEO_LIKE_HOOK_CLASS = Helper.loadProfiledClass(Helper.getVideoLikes(), classTree, VIDEO_LIKE_HOOK_CLASS);
                        }

                        hookInstagram();
                    } catch (Throwable t) {
                        setError("Experiment Hook Finder Failed: " +t);
                    }
                } else {
                    hookInstagram();
                }
            } catch (Throwable t) {
                setError("Hooks Check Failed - " + t.toString());

            }
        }
    }

    void taggedUserAlert(final String toastMessage, final long longId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);

            final CharSequence items[] = userNameTagged.split(";");

            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userNameTagged = "";

                    fileName = fileName.replace(userName, items[which].toString());
                    String userNameFixed = userName.substring(0, 1).toUpperCase() + userName.substring(1);
                    notificationTitle = notificationTitle.replace(userNameFixed, items[which].toString());
                    notificationTitle = notificationTitle.substring(0, 1).toUpperCase() + notificationTitle.substring(1);
                    userName = items[which].toString();

                    if (userNameTagged.isEmpty()) {
                        Helper.Toast(toastMessage, mContext);
                    }

                    try {
                        if (fileType.equals("Video") && Helper.getSettings("OneTap") && appInstalledOrNot("com.phantom.onetapvideodownload")) {
                            setError("One Tap - " + Helper.getSettings("OneTap"));
                            Intent intent = new Intent("com.phantom.onetapvideodownload.action.saveurl");
                            intent.setClassName("com.phantom.onetapvideodownload", "com.phantom.onetapvideodownload.IpcService");
                            intent.putExtra("com.phantom.onetapvideodownload.extra.url", linkToDownload);
                            intent.putExtra("com.phantom.onetapvideodownload.extra.title", fileName);
                            intent.putExtra("com.phantom.onetapvideodownload.extra.package_name", loadPackageParam.packageName);
                            mContext.startService(intent);
                        } else {
                            if (fileType.equals("Profile")) {
                                SAVE = Helper.checkSaveProfile(SAVE, userName, fileName);
                            } else {
                                SAVE = Helper.checkSave(SAVE, userName, fileName);
                            }

                            Helper.downloadOrPass(linkToDownload, fileName, fileType, userName, notificationTitle, longId, mContext, false);
                        }
                    } catch (Exception e) {
                        setError("Failed To Send Download Broadcast - " + e);
                    }

                    dialog.dismiss();
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    userNameTagged = "";
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Tagged User Failed - " +t);
        }
    }

    void taggedUserMultiAlert(final String users, final long longId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);

            final CharSequence items[] = users.split(";");

            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userName = items[which].toString();
                    setError("Hi: " +userName);

                    for (int i=0;i < videoList.size();i++) {
                        try {
                            if (!checked.isEmpty() && checked.contains("" +i)) {
                                downloadMedia(videoList.get(i), "Select");
                            } else if (checked.isEmpty()) {
                                downloadMedia(videoList.get(i), "Select");
                            }
                        } catch (Throwable t) {
                            setError("Multi Tagged Download Failed: " +t);
                        }
                    }

                    dialog.dismiss();
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    userNameTagged = "";
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Tagged User Failed - " +t);
        }
    }

    void multiAlert(final long longId, final Object mMedia) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);

            String multiOptions = "All;Selection";

            final CharSequence items[] = multiOptions.split(";");

            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (Helper.isTagged(mMedia, TAGGED_HOOK_CLASS, loadPackageParam)) {
                            String users = Helper.getUsername(mMedia, FULLNAME__HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam) + ";" + Helper.getTagged(mMedia, TAGGED_HOOK_CLASS, Helper.getUsername(mMedia, FULLNAME__HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam), loadPackageParam);
                            taggedUserMultiAlert(users, longId);
                        } else {
                            for (int i=0;i < videoList.size();i++) {
                                try {
                                    downloadMedia(videoList.get(i), "Multi");
                                } catch (Throwable t) {
                                    setError("Multi Download Failed: " +t);
                                }
                            }
                        }
                    } else if (which == 1) {
                        multiAlertSelection(longId, mMedia);
                    }

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Multi Alert Failed - " +t);
        }
    }

    void multiAlertLock(final long longId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);

            String multiOptions = "All;Selection";

            final CharSequence items[] = multiOptions.split(";");

            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        linksToDownload = "";
                        locationsToDownload = "";
                        notificationsToDownload = "";
                        fileNamesToDownload = "";
                        fileTypesToDownload = "";
                        userNamesToDownload = "";
                        userNameTagged = "";

                        for (int i=0;i < videoList.size();i++) {
                            try {
                                downloadMultiMedia(videoList.get(i), "Other", longId);
                            } catch (Throwable t) {
                                setError("Multi Download Failed: " +t);
                            }
                        }

                        new PrivacyMulti().execute(linksToDownload);
                    } else if (which == 1) {
                        multiAlertSelectionLock(longId);
                    }

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Multi Alert Failed - " +t);
        }
    }

    void multiAlertSelection(final long longId, final Object mMedia) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);
            checked = "";

            String multiLength = "";

            for (int i=1;i < videoList.size() + 1;i++) {
                if (!multiLength.equals("")) {
                    multiLength = multiLength + ";" + i;
                } else {
                    multiLength = "" + i;
                }
            }

            final CharSequence items[] = multiLength.split(";");

            builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                    if (isChecked) {
                        if (!checked.equals("")) {
                            checked = checked + ";" +item;
                        } else {
                            checked = "" +item;
                        }
                    } else {
                        if (checked.equals("1")) {
                            checked = "";
                        } else {
                            checked = checked.replace(";" + item, "");
                        }
                    }
                }
            });

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int in) {
                    if (!checked.equals("") && !checked.equals(";")) {

                        if (Helper.isTagged(mMedia, TAGGED_HOOK_CLASS, loadPackageParam)) {
                            String users = Helper.getUsername(mMedia, FULLNAME__HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam) + ";" + Helper.getTagged(mMedia, TAGGED_HOOK_CLASS, Helper.getUsername(mMedia, FULLNAME__HOOK, USER_CLASS_NAME, USERNAME_HOOK, loadPackageParam), loadPackageParam);
                            taggedUserMultiAlert(users, longId);
                        } else {
                            for (int i=0;i < videoList.size();i++) {
                                try {
                                    if (checked.contains("" +i)) {
                                        downloadMedia(videoList.get(i), "Multi");
                                    }
                                } catch (Throwable t) {
                                    setError("Multi Selection Download Failed: " +t);
                                }
                            }
                        }
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Multi Alert Failed - " +t);
        }
    }

    void multiAlertSelectionLock(final long longId) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(oContext, android.R.style.Theme_Holo_Light_Dialog);
            checked = "";

            String multiLength = "";

            for (int i=1;i < videoList.size() + 1;i++) {
                if (!multiLength.equals("")) {
                    multiLength = multiLength + ";" + i;
                } else {
                    multiLength = "" + i;
                }
            }

            final CharSequence items[] = multiLength.split(";");

            builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                    if (isChecked) {
                        if (!checked.equals("")) {
                            checked = checked + ";" +item;
                        } else {
                            checked = "" +item;
                        }
                    } else {
                        if (checked.equals("1")) {
                            checked = "";
                        } else {
                            checked = checked.replace(";" + item, "");
                        }
                    }
                }
            });

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int in) {
                    if (!checked.equals("") && !checked.equals(";")) {
                        linksToDownload = "";
                        locationsToDownload = "";
                        notificationsToDownload = "";
                        fileNamesToDownload = "";
                        fileTypesToDownload = "";
                        userNamesToDownload = "";
                        userNameTagged = "";

                        for (int i=0;i < videoList.size();i++) {
                            try {
                                if (checked.contains("" +i)) {
                                    downloadMultiMedia(videoList.get(i), "Select", longId);
                                }
                            } catch (Throwable t) {
                                setError("Multi Download Failed: " +t);
                            }
                        }

                        new PrivacyMulti().execute(linksToDownload);
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Throwable t) {
            setError("Multi Alert Failed - " +t);
        }
    }

    void updateHooks() {
        Thread getHooks= new Thread() {
                public void run() {
                    try {
                        String url;

                        if (Helper.getSetting("Source").equals("GitHub")) {
                            url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                        } else if (Helper.getSetting("Source").equals("Pastebin")) {
                            url = "http://pastebin.com/raw.php?i=sTXbUFcx";
                        } else if (Helper.getSetting("Source").equals("Alternate Source")) {
                            url = "http://www.snapprefs.com/xinsta/Hooks.txt";
                        } else {
                            url = "https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt";
                        }

                        URL u = new URL(url);
                        URLConnection c = u.openConnection();
                        c.connect();

                        InputStream inputStream = c.getInputStream();

                        Hooks = Helper.convertStreamToString(inputStream);
                    } catch (Exception e) {
                        if (!Helper.getSetting("Hooks").equals("Instagram")) {
                            setError("Falling Back On Older Hooks");
                            hookInstagram();
                        } else {
                            setError("Failed to fetch hooks.");
                        }
                        Hooks = "Nope";
                    }
                }
            };
            getHooks.start();
            try {
                getHooks.join();
            } catch (InterruptedException e) {
            }

            int max = 0;
            int count = 0;

            String[] html = Hooks.split("<p>");

            String matched = "No";

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

                if (version.equalsIgnoreCase(finalCheck) && !data.isEmpty()) {
                    HooksSave = data.replace("<p>", "");
                    HooksSave = HooksSave.replace("</p>", "");
                    matched = "Yes";
                } else if (count == max && matched == "No") {
                    String fallback = html[1];
                    HooksSave = fallback.replace("<p>", "");
                    HooksSave = HooksSave.replace("</p>", "");
                } else {
                    int typeCheck = Integer.parseInt(version) - Integer.parseInt(finalCheck);
                    if (typeCheck <= 2 && typeCheck >= -2) {
                        HooksSave = data.replace("<p>", "");
                        HooksSave = HooksSave.replace("</p>", "");
                        matched = "Yes";
                    }
                }
            }

            HookCheck = "No";
            oldCheck = "No";
            directShareCheck = "Yes";
            HooksArray = HooksSave.split(";");

            try {
                FEED_CLASS_NAME = HooksArray[1];
                firstHook = HooksArray[1];
                MEDIA_CLASS_NAME = HooksArray[2];
                USER_CLASS_NAME = HooksArray[4];
                MEDIA_OPTIONS_BUTTON_CLASS = HooksArray[5];
                DS_MEDIA_OPTIONS_BUTTON_CLASS = HooksArray[6];
                DS_PERM_MORE_OPTIONS_DIALOG_CLASS = HooksArray[7];
                PERM__HOOK = HooksArray[10];
                MEDIA_VIDEO_HOOK = HooksArray[14];
                MEDIA_PHOTO_HOOK = HooksArray[15];
                USERNAME_HOOK = HooksArray[16];
                FULLNAME__HOOK = HooksArray[17];
                setError("Hooks Fetched!");
            } catch (ArrayIndexOutOfBoundsException e) {
                HookCheck = "Yes";
            }

            try {
                IMAGE_HOOK_CLASS = HooksArray[18];
            } catch (ArrayIndexOutOfBoundsException e) {
                oldCheck = "Yes";
            }

            try {
                ITEMID_HOOK = HooksArray[20];
                COMMENT_HOOK_CLASS = HooksArray[21];
                COMMENT_HOOK = HooksArray[22];
                COMMENT_HOOK_CLASS2 = HooksArray[23];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_DIALOG_CLASS = HooksArray[24];
            } catch (ArrayIndexOutOfBoundsException e) {
                directShareCheck = "Nope";
            }

            try {
                PROFILE_HOOK_CLASS = HooksArray[29];
                PROFILE_HOOK_CLASS2 = HooksArray[30];
                FOLLOW_HOOK_2 = HooksArray[31];
                PROFILE_HOOK_3 = HooksArray[33];
                PROFILE_HOOK_4 = HooksArray[34];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LIKE_HOOK_CLASS = HooksArray[35];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SUGGESTION_HOOK_CLASS = HooksArray[39];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                FOLLOW_HOOK_CLASS = HooksArray[41];
                FOLLOW_HOOK = HooksArray[42];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                TIME_HOOK_CLASS = HooksArray[43];
            } catch (ArrayIndexOutOfBoundsException e) {
                TIME_HOOK_CLASS = "Nope";
            }

            try {
                NOTIFICATION_CLASS = HooksArray[44];
            } catch (ArrayIndexOutOfBoundsException e) {
                NOTIFICATION_CLASS = "Nope";
            }

            try {
                VIDEO_HOOK_CLASS = HooksArray[45];
            } catch (ArrayIndexOutOfBoundsException e) {
                VIDEO_HOOK_CLASS = "Nope";
            }

            try {
                STORY_HOOK_CLASS = HooksArray[46];
                STORY_HOOK_CLASS2= HooksArray[47];
                STORY_HOOK= HooksArray[48];
                STORY_TIME_HOOK_CLASS = HooksArray[49];
                STORY_TIME_HOOK = HooksArray[50];
                STORY_TIME_HOOK_CLASS2 = HooksArray[51];
                STORY_TIME_HOOK2 = HooksArray[52];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                MINI_FEED_HOOK_CLASS = HooksArray[53];
                MINI_FEED_HOOK_CLASS2 = HooksArray[54];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                FOLLOW_LIST_CLASS = HooksArray[55];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SHARE_HOOK_CLASS = HooksArray[57];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                TAGGED_HOOK_CLASS = HooksArray[58];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                VIDEO_HOOK = HooksArray[59];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SLIDE_HOOK_CLASS = HooksArray[60];
                SLIDE_HOOK = HooksArray[61];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LOCK_HOOK8 = HooksArray[70];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                STORY_GALLERY_CLASS = HooksArray[72];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SEARCH_HOOK_CLASS = HooksArray[73];
                SEARCH_HOOK_CLASS2 = HooksArray[74];
                SEARCH_HOOK_CLASS3 = HooksArray[75];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SPONSORED_HOOK_CLASS = HooksArray[76];
                SPONSORED_HOOK = HooksArray[77];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                VIDEO_LIKE_HOOK_CLASS = HooksArray[78];
                VIDEO_LIKE_HOOK = HooksArray[79];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                STORY_TIME_HOOK_CLASS3 = HooksArray[80];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SEARCH_HOOK_CLASS4 = HooksArray[81];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PIN_HOOK_CLASS = HooksArray[82];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PIN_HOOK_CLASS2 = HooksArray[83];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PROFILE_ICON_CLASS = HooksArray[84];
                PROFILE_ICON_HOOK = HooksArray[86];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                CAROUSEL_HOOK = HooksArray[87];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LIKE_HOOK_CLASS2 = HooksArray[88];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                STORY_VIEWS_HOOK = HooksArray[89];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PROFILE_ICON_CLASS2 = HooksArray[90];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PIN_HOOK_CLASS3 = HooksArray[91];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PIN_HOOK_CLASS4 = HooksArray[92];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_PRIVATE_CLASS = HooksArray[93];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_PRIVATE_CLASS2 = HooksArray[94];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_PRIVATE_CLASS3 = HooksArray[95];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DS_PRIVATE_HOOK = HooksArray[96];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PIN_HOOK_CLASS5 = HooksArray[97];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                LIKED_POST_HOOK_CLASS = HooksArray[98];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                PAID_HOOK = HooksArray[99];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                SCREENSHOT_CLASS = HooksArray[100];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                DISCOVERY_CLASS = HooksArray[101];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            try {
                USER_AGENT_CLASS = HooksArray[102];
            } catch (ArrayIndexOutOfBoundsException e) {
            }

            Helper.setSetting("Hooks", HooksSave);
    }

    CharSequence[] injectDownload(CharSequence[] charSequence, String injectLocation) {
        String downloadCheck;

        try {
            downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
        } catch (Throwable t) {
            downloadCheck = "Download";
        }

        ArrayList<String> array = new ArrayList<String>();

        if (!Helper.getSettings("Order")) {
            for (CharSequence sq : charSequence) {
                array.add(sq.toString());
            }
        }

        if (!array.contains(downloadCheck)) {
            array.add(downloadCheck);
        }

        if (injectLocation.equals("Feed")) {
            if (Helper.getSettings("Lock")) {
                String lockFeed;

                try {
                    lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                } catch (Throwable t) {
                    lockFeed = "Privacy Lock";
                }

                if (!array.contains(lockFeed)) {
                    array.add(lockFeed);
                }
            }

        }

        if (Helper.getSettings("Order")) {
            for (CharSequence sq : charSequence) {
                array.add(sq.toString());
            }
        }

        CharSequence[] newResult = new CharSequence[array.size()];
        array.toArray(newResult);
        mMenuOptions = newResult;

        return mMenuOptions;
    }

    XC_MethodHook injectDownloadIntoCharSequenceHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            try {
                CharSequence[] result = (CharSequence[]) param.getResult();

                String downloadCheck;

                try {
                    downloadCheck = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button);
                } catch (Throwable t) {
                    downloadCheck = "Download";
                }

                ArrayList<String> array = new ArrayList<String>();

                if (!Helper.getSettings("Order")) {
                    for (CharSequence sq : result) {
                        array.add(sq.toString());
                    }
                }

                if (!array.contains(downloadCheck)) {
                    array.add(downloadCheck);
                }

                if (Helper.getSettings("Lock")) {
                    String lockFeed;

                    try {
                        lockFeed = Helper.getResourceString(nContext, R.string.the_not_so_big_but_big_button2);
                    } catch (Throwable t) {
                        lockFeed = "Privacy Lock";
                    }

                    if (!array.contains(lockFeed)) {
                        array.add(lockFeed);
                    }
                }

                if (Helper.getSettings("Order")) {
                    for (CharSequence sq : result) {
                        array.add(sq.toString());
                    }
                }

                CharSequence[] newResult = new CharSequence[array.size()];
                array.toArray(newResult);
                mMenuOptions = newResult;

                param.setResult(newResult);
            } catch (Throwable t) {
                setError("Download Button Inject Failed - " +t.toString());
            }
        }
    };
}



