package com.ihelp101.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class Date extends AppCompatActivity {

    public class Item {
        String ItemString;
        Item(String t){
            ItemString = t;
        }
    }

    class PassObject{
        View view;
        Item item;
        List<Item> srcList;

        PassObject(View v, Item i, List<Item> s){
            view = v;
            item = i;
            srcList = s;
        }
    }

    static class ViewHolder {
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l){
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.row, null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(list.get(position).ItemString);

            rowView.setOnDragListener(new ItemOnDragListener(list.get(position)));

            return rowView;
        }

        public List<Item> getList(){
            return list;
        }
    }

    List<Item> items;
    android.widget.ListView listView;
    ItemsListAdapter myItemsListAdapter;
    LinearLayoutListView area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);
        listView = (android.widget.ListView) findViewById(R.id.listview1);

        area = (LinearLayoutListView)findViewById(R.id.pane1);
        area.setOnDragListener(myOnDragListener);
        area.setListView(listView);

        items = new ArrayList<Item>();

        String[] lists;
        try {
            lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = (getResources().getString(R.string.month) + ";" + getResources().getString(R.string.day) + ";" + getResources().getString(R.string.year) + ";" + getResources().getString(R.string.space) + ";" + getResources().getString(R.string.hour) + ";" + getResources().getString(R.string.minute) + ";" + getResources().getString(R.string.second) + ";" + getResources().getString(R.string.space2) + ";" + getResources().getString(R.string.am) + ";").split(";");
            }

            for (String name : lists) {
                items.add(new Item(name));
            }
        } catch (Exception e) {
        }


        myItemsListAdapter = new ItemsListAdapter(Date.this, items);
        listView.setAdapter(myItemsListAdapter);

        listView.setOnItemClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v, int i) {
                String date = "";

                String[] lists = Helper.getSetting("Date").split(";");

                if (Helper.getSetting("Date").equals("Instagram")) {
                    lists = (getString(R.string.month) + ";" + getString(R.string.day) + ";" + getString(R.string.year) + ";" + getString(R.string.space) + ";" + getString(R.string.hour) + ";" + getString(R.string.minute) + ";" + getString(R.string.second) + ";" + getString(R.string.space2) + ";" + getString(R.string.am) + ";").split(";");
                }

                Item item = (Item) listView.getItemAtPosition(i);
                String clicked = item.ItemString;
                items.clear();

                for (String name : lists) {
                    if (!name.equals(clicked)) {
                        items.add(new Item(name));
                        date = date + name + ";";
                    }
                }

                Helper.setSetting("Date", date);
                myItemsListAdapter = new ItemsListAdapter(Date.this, items);
                listView.setAdapter(myItemsListAdapter);
            }
        });

        listView.setOnItemLongClickListener(myOnItemLongClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.date, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("+")) {

            String originalString = getResources().getString(R.string.month) + ";" + getResources().getString(R.string.day) + ";" + getResources().getString(R.string.year) + ";" + getResources().getString(R.string.space) + ";" + getResources().getString(R.string.hour) + ";" + getResources().getString(R.string.minute) + ";" + getResources().getString(R.string.second) + ";" + getResources().getString(R.string.space2) + ";" + getResources().getString(R.string.am) + ";";

            String[] lists = Helper.getSetting("Date").split(";");

            if (Helper.getSetting("Date").equals("Instagram")) {
                lists = (getResources().getString(R.string.month) + ";" + getResources().getString(R.string.day) + ";" + getResources().getString(R.string.year) + ";" + getResources().getString(R.string.space) + ";" + getResources().getString(R.string.hour) + ";" + getResources().getString(R.string.minute) + ";" + getResources().getString(R.string.second) + ";" + getResources().getString(R.string.space2) + ";" + getResources().getString(R.string.am) + ";").split(";");
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

                                items.add(new Item(finalString[((AlertDialog) dialog).getListView().getCheckedItemPosition()]));
                                String saveString = "";

                                for (int i = 0; i < items.size(); i++) {
                                    saveString = saveString + items.get(i).ItemString + ";";
                                }

                                Helper.setSetting("Date", saveString);

                                myItemsListAdapter = new ItemsListAdapter(Date.this, items);
                                listView.setAdapter(myItemsListAdapter);
                            }
                        })
                        .show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemLongClickListener myOnItemLongClickListener = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            Item selectedItem = (Item)(parent.getItemAtPosition(position));

            ItemsListAdapter associatedAdapter = (ItemsListAdapter)(parent.getAdapter());
            List<Item> associatedList = associatedAdapter.getList();

            PassObject passObj = new PassObject(view, selectedItem, associatedList);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, passObj, 0);

            return true;
        }

    };

    View.OnDragListener myOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.view;
                    Item passedItem = passObj.item;
                    List<Item> srcList = passObj.srcList;
                    android.widget.ListView oldParent = (android.widget.ListView) view.getParent();
                    ItemsListAdapter srcAdapter = (ItemsListAdapter)(oldParent.getAdapter());

                    LinearLayoutListView newParent = (LinearLayoutListView)v;
                    ItemsListAdapter destAdapter = (ItemsListAdapter)(newParent.listView.getAdapter());
                    List<Item> destList = destAdapter.getList();

                    if (removeItemToList(srcList, passedItem)) {
                        addItemToList(destList, passedItem);
                    }

                    srcAdapter.notifyDataSetChanged();
                    destAdapter.notifyDataSetChanged();

                    newParent.listView.smoothScrollToPosition(destAdapter.getCount()-1);
                    break;
                default:
                    break;
            }

            return true;
        }

    };

    class ItemOnDragListener implements View.OnDragListener {

        Item  me;

        ItemOnDragListener(Item i){
            me = i;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    PassObject passObj = (PassObject)event.getLocalState();
                    View view = passObj.view;
                    Item passedItem = passObj.item;
                    List<Item> srcList = passObj.srcList;
                    android.widget.ListView oldParent = (android.widget.ListView)view.getParent();
                    ItemsListAdapter srcAdapter = (ItemsListAdapter)(oldParent.getAdapter());

                    android.widget.ListView newParent = (android.widget.ListView)v.getParent();
                    ItemsListAdapter destAdapter = (ItemsListAdapter)(newParent.getAdapter());
                    List<Item> destList = destAdapter.getList();

                    int removeLocation = srcList.indexOf(passedItem);
                    int insertLocation = destList.indexOf(me);

                    if(srcList != destList || removeLocation != insertLocation){
                        if(removeItemToList(srcList, passedItem)){
                            destList.add(insertLocation, passedItem);
                        }

                        String date = "";

                        for(int i = 0 ; i < destList.size() ; i++) {
                            if (Helper.getSetting("Date").contains(items.get(i).ItemString)) {
                                date = date + items.get(i).ItemString + ";";
                            }
                        }

                        Helper.setSetting("Date", date);

                        srcAdapter.notifyDataSetChanged();
                        destAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }

            return true;
        }

    }
    private boolean removeItemToList(List<Item> l, Item it){
        boolean result = l.remove(it);
        return result;
    }

    private boolean addItemToList(List<Item> l, Item it){
        boolean result = l.add(it);
        return result;
    }

}

abstract class DoubleClickListener implements AdapterView.OnItemClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;

    long lastClickTime = 0;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
            onDoubleClick(view, i);
        }
        lastClickTime = clickTime;
    }

    public abstract void onDoubleClick(View v, int i);
}