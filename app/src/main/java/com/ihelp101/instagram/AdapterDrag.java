package com.ihelp101.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;


public class AdapterDrag extends BaseAdapter implements DragNDropAdapter {

    private static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    static public ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private int mPosition[];
    int mHandler;

    private LayoutInflater mInflater;

    public AdapterDrag(Context context, int handler) {
        mData.clear();
        mHandler = handler;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item) {
        mData.add(item);
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_date, null);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mData.get(position));

        final TextView delete = (TextView) convertView.findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List originalList = new ArrayList();
                for (int i = 0; i < mData.size(); ++i) {
                    originalList.add(mData.get(i));
                }

                originalList.remove(position);

                mData.clear();

                String saveString = "";

                for (int i = 0; i < originalList.size(); ++i) {
                    addItem(originalList.get(i).toString());
                    String dateOption = originalList.get(i).toString();
                    if (dateOption.equals(Date.day)) {
                        dateOption = "/dd";
                    }

                    if (dateOption.equals(Date.hour)) {
                        if (Helper.getSettings("Hour")) {
                            dateOption = "HH:";
                        } else {
                            dateOption = "hh:";
                        }
                    }
                    if (dateOption.equals(Date.minute)) {
                        dateOption = "mm:";
                    }
                    if (dateOption.equals(Date.month)) {
                        dateOption = "/MM";
                    }
                    if (dateOption.equals(Date.second)) {
                        dateOption = "ss:";
                    }
                    if (dateOption.equals(Date.space)) {
                        dateOption = " ";
                    }
                    if (dateOption.equals(Date.year)) {
                        dateOption = "/yyyy";
                    }
                    if (dateOption.equals(Date.AM)) {
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
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
    }

    @Override
    public void onItemDrag(DragNDropListView parent, View view, int position, long id) {

    }

    @Override
    public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
        mPosition = new int[mData.size()];

        List originalList = new ArrayList();
        for (int i = 0; i < mData.size(); ++i) {
            String dateOption = mData.get(i);
            originalList.add(dateOption);
        }

        Collections.swap(originalList, startPosition, endPosition);

        mData.clear();

        String saveString = "";

        for (int i = 0; i < originalList.size(); ++i) {
            String dateOption = originalList.get(i).toString();
            if (dateOption.equals(Date.day)) {
                dateOption = "/dd";
            }
            if (dateOption.equals(Date.hour)) {
                if (Helper.getSettings("Hour")) {
                    dateOption = "HH:";
                } else {
                    dateOption = "hh:";
                }
            }
            if (dateOption.equals(Date.minute)) {
                dateOption = "mm:";
            }
            if (dateOption.equals(Date.month)) {
                dateOption = "/MM";
            }
            if (dateOption.equals(Date.second)) {
                dateOption = "ss:";
            }
            if (dateOption.equals(Date.space)) {
                dateOption = " ";
            }
            if (dateOption.equals(Date.year)) {
                dateOption = "/yyyy";
            }
            if (dateOption.equals(Date.AM)) {
                dateOption = "a";
            }
            addItem(originalList.get(i).toString());
            saveString = saveString + dateOption + ";";
        }

        if (!Helper.getSetting("Separator").equals("Instagram")) {
            saveString = saveString.replace("/", Helper.getSetting("Separator"));
        }

        if (saveString.substring(0,1).equals("/")){
            saveString = saveString.substring(1);
        }

        Helper.setSetting("Date", saveString);
    }

    @Override
    public int getDragHandler() {
        return mHandler;
    }

    private void setup(int size) {
        mPosition = new int[size];

        for (int i = 0; i < size; ++i)
            mPosition[i] = i;
    }
}
