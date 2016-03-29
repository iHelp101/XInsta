package com.ihelp101.instagram;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearLayoutListView extends LinearLayout {

    android.widget.ListView listView;

    public LinearLayoutListView(Context context) {
        super(context);
    }

    public LinearLayoutListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListView(android.widget.ListView lv){
        listView = lv;
    }

}
