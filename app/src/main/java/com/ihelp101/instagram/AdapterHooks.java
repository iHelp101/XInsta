package com.ihelp101.instagram;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeSet;


public class AdapterHooks extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public AdapterHooks(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getPosition(int position) {
        return position - sectionHeader.headSet(position).size();
    }

    public void updateItem(final String item, final int position) {
        mData.set(position, item);
        notifyDataSetChanged();
    }

    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.listview_hooks, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    final TextView textView = (TextView) convertView.findViewById(R.id.text);
                    final TextView textView2 = (TextView) convertView.findViewById(R.id.info);
                    textView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hookDescription = "";

                            String hookName = textView.getText().toString().split("\n")[0];

                            if (hookName.equals("Feed OnClick Class")) {
                                hookDescription = "This class contains the onClick that is classed when the three dot icon is clicked on a feed post.";
                            }

                            if (hookName.equals("Feed Class")) {
                                hookDescription = "This class contains most of the information about a post (Image/Video URL, Sponsored, etc).";
                            }

                            if (hookName.equals("User Class")) {
                                hookDescription = "This class contains information about user (Username, Full Username, Profile Icon, Etc).";
                            }

                            if (hookName.equals("Feed Inject Class (Old)")) {
                                hookDescription = "This class contains a CharSequence that populates the dialog that is injected with Download. This hook is no longer used and replaced with the Dialog Inject hook.";
                            }

                            if (hookName.equals("Username Field")) {
                                hookDescription = "This field is in the User Class and contains the username of a poster.";
                            }

                            if (hookName.equals("Full Username Field")) {
                                hookDescription = "This field is in the User Class and contains the Full Username of a poster.";
                            }

                            if (hookName.equals("Image URL Class")) {
                                hookDescription = "This class contains the image URL of a post. This class is referenced in the Feed Class.";
                            }

                            if (hookName.equals("Post Media ID Field")) {
                                hookDescription = "This field contains a post's Media ID, which is an identifier used by Instagram. This field is in the Feed Class.";
                            }

                            if (hookName.equals("Comment Class")) {
                                hookDescription = "This class contains...";
                            }

                            if (hookName.equals("Comment Method")) {
                                hookDescription = "This is the method used to detect a comment was been clicked. This method is in the Comment Class.";
                            }

                            if (hookName.equals("Comment Class 2")) {
                                hookDescription = "This class contains the comment (String). This class is referenced in the Comment Class.";
                            }

                            if (hookName.equals("Dialog Inject Class")) {
                                hookDescription = "This class contains a CharSequence that is called by all dialogs. This is the new method XInsta uses to inject the Download option.";
                            }

                            if (hookName.equals("Profile Inject Class (Old)")) {
                                hookDescription = "This class contains a CharSequence that is called when the three dot icon in a profile is clicked. This hook is no longer used and replaced with the Dialog Inject hook.";
                            }

                            if (hookName.equals("Profile Inject Class (Old)")) {
                                hookDescription = "This class contains a CharSequence that is called when the three dot icon in a profile is clicked. This hook is no longer used and replaced with the Dialog Inject hook.";
                            }

                            if (hookName.equals("Profile OnClick Class")) {
                                hookDescription = "This class contains the onClick that is called when the three dot icon in a profile is clicked.";
                            }

                            if (hookName.equals("Follow Indicator Byte Field")) {
                                hookDescription = "This field is a byte[] that is parse to see if a user follows you. This is in the Follow Indicator class.";
                            }

                            if (hookName.equals("Like Class")) {
                                hookDescription = "This class contains an MotionEvent that is called when a post is double tapped.";
                            }

                            if (hookName.equals("Suggestion Class")) {
                                hookDescription = "This class contains a View that is loaded to show suggestions.";
                            }

                            if (hookName.equals("Follow Indicator Class")) {
                                hookDescription = "This class contains the method called for Instagram's API request regarding user relationships (Follows, Follows Back, Blocked, etc).";
                            }

                            if (hookName.equals("Follow Indicator Method")) {
                                hookDescription = "This method is called when Instagram makes an API request to check a user relationship. This is in the Follow Indicator class.";
                            }

                            if (hookName.equals("Date Format Class")) {
                                hookDescription = "This class contains a method that is called to format a posts epoch into a date.";
                            }

                            if (hookName.equals("Push Notifications Class")) {
                                hookDescription = "This class contains a method that is called when Instagram recieved a push notification.";
                            }

                            if (hookName.equals("Video URL Class")) {
                                hookDescription = "This class contains a post's video URL. This class is referenced in the Feed Class.";
                            }

                            if (hookName.equals("Stories OnClick Class")) {
                                hookDescription = "This class contain an OnClick that is called when the three dot icon is clicked on in a story.";
                            }

                            if (hookName.equals("Stories Inject Class (Old)")) {
                                hookDescription = "This class contains a CharSequence that populates the dialog that is injected with Download. This hook is no longer used and replaced with the Dialog Inject hook.";
                            }

                            if (hookName.equals("Stories Help Class (Links To Feed)")) {
                                hookDescription = "This class contains a reference to the Feed class. XInsta uses this class to link stories to the feed class in order to get post information. This class is reference in the Stories OnClick Class.";
                            }

                            if (hookName.equals("Stories Video Timer Class (Old)")) {
                                hookDescription = "This class contains a method that is called when MediaPlayer is complete. This hook is no longer used because of the move to ExoPlayer, which means Stories Video Timer Class (ExoPlayer) replaces this.";
                            }

                            if (hookName.equals("Stories Video Timer Method (Old)")) {
                                hookDescription = "This method is called when MediaPlayer is complete. This method is no longer used and this method is in the Stories Timer Class (Old).";
                            }

                            if (hookName.equals("Stories Image Timer Class")) {
                                hookDescription = "This class contains a method that calls onBackPressed() when a stories timer to complete.";
                            }

                            if (hookName.equals("Stories Image Timer Method")) {
                                hookDescription = "This method calls onBackPressed() when a stories timer to complete.";
                            }

                            if (hookName.equals("Force Touch/Peek Feed OnClick Class")) {
                                hookDescription = "This class contains an OnClick that is called when the three dot icon is clicked on the force touch/peek post.";
                            }

                            if (hookName.equals("Force Touch/Peek Feed Inject Class (Old)")) {
                                hookDescription = "This class contains an CharSequence that populated the dialog that is injected with Download. This hook is no longer used and replaced with the Dialog Inject hook.";
                            }

                            if (hookName.equals("Follow List Class")) {
                                hookDescription = "This class has a method that is called when Instagram makes an API request for list of followers/following.";
                            }

                            if (hookName.equals("Copy Link Class")) {
                                hookDescription = "This class has a method that is called when the Copy Share URL is clicked.";
                            }

                            if (hookName.equals("Tagged Users Class")) {
                                hookDescription = "This class contains information about tagged users. This class is reference in the Feed class.";
                            }

                            if (hookName.equals("Slide/Swipe To Navigate Class")) {
                                hookDescription = "This class contains a MotionEvent that is called when the slide/swipe navigiate option is used.";
                            }

                            if (hookName.equals("Slide/Swipe To Navigate Method")) {
                                hookDescription = "This method is called when the slide/swipe navigiate option is used.";
                            }

                            if (hookName.equals("Lock Feed Scroll Class")) {
                                hookDescription = "This class contains an MotionEvent that is called when the feed ListView is scrolled up or down.";
                            }

                            if (hookName.equals("Lock Feed Scroll Method")) {
                                hookDescription = "This method is called when the feed ListView is scrolled up or down.";
                            }

                            if (hookName.equals("Lock Feed Location Class")) {
                                hookDescription = "This class contains an OnClick that is called when the location in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed Profile Icon Class")) {
                                hookDescription = "This class contains an OnClick that is called when the profile icon in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed Username Class")) {
                                hookDescription = "This class contains an OnClick that is called when the username in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed Like Button Class")) {
                                hookDescription = "This class contains an OnClick that is called when the like button in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed Comment Button Class")) {
                                hookDescription = "This class contains an OnClick that is called when the comments button in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed Share Button Class")) {
                                hookDescription = "This class contains an OnClick that is called when the share button in a post is clicked.";
                            }

                            if (hookName.equals("Lock Feed LikeView Class")) {
                                hookDescription = "This class contains an OnClick that is called when the image/video in a post is double clicked";
                            }

                            if (hookName.equals("Lock Feed TextView Class")) {
                                hookDescription = "This class contains an OnClick that is called when the view all comments TextView in a post is clicked.";
                            }

                            if (hookName.equals("Stories Gallery Class")) {
                                hookDescription = "This class contains a method that is called when the stories gallery loads images/video from internal storage.";
                            }

                            if (hookName.equals("Search Tagged Class")) {
                                hookDescription = "This class contains a method that is called that Instagram makes a list of tagged related searches.";
                            }

                            if (hookName.equals("Search Location Class")) {
                                hookDescription = "This class contains a method that is called that makes a list of location related searches.";
                            }

                            if (hookName.equals("Search Username Class")) {
                                hookDescription = "This class contains a method that is called that makes a list of username related searches.";
                            }

                            if (hookName.equals("Sponsored Class")) {
                                hookDescription = "This class contains a method that is called when feed post are created (onCreateView).";
                            }

                            if (hookName.equals("Sponsored Injected Field")) {
                                hookDescription = "This field is set when a post has injected in it's API request, which means it is sponsored.";
                            }

                            if (hookName.equals("Video Like Count Class")) {
                                hookDescription = "This class contains a method that is called when the video like/view count is set.";
                            }

                            if (hookName.equals("Video Like Count Int Field")) {
                                hookDescription = "This int field contain the number of views/likes a post has.";
                            }

                            if (hookName.equals("Stories Video Timer Class (ExoPlayer)")) {
                                hookDescription = "This class has a method that is called to that set ExoPlayer's looping.";
                            }

                            if (hookName.equals("Pin/Saved Class")) {
                                hookDescription = "This class contains a method that is called when the pin/save button is created. This is used to add a onLongPress listener.";
                            }

                            if (hookName.equals("Pin/Saved OnClick Class")) {
                                hookDescription = "This class contains a OnClick that is called when the pin/save button is clicked.";
                            }

                            Toast.makeText(textView2.getContext(), hookDescription, Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.listview_2, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (mData.get(position).contains("-")) {
            String text = "<b>" + mData.get(position).split("-")[0].trim() + "</b><br>" + mData.get(position).split("-")[1].trim() + "</br>";
            holder.textView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        } else {
            holder.textView.setText(mData.get(position));
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }


}
