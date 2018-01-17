package com.ihelp101.instagram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Thanks extends AppCompatActivity {

    Adapter mAdapter;
    ListView listView;
    String currentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanks);

        listView = (ListView) findViewById(R.id.list);

        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                currentAction = mAdapter.getItem(position);
                listAction();
            }
        });
    }

    void listAction() {
        if (currentAction.contains("Jonas Kalderstam")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/spacecowboy/NoNonsense-FilePicker"));
            startActivity(browserIntent);
        }
        if (currentAction.contains("Terlici")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/terlici/DragNDropList"));
            startActivity(browserIntent);
        }
    }

    void updateListView() {
        mAdapter = new Adapter(Thanks.this);
        mAdapter.addSectionHeaderItem("Applications/Projects");
        mAdapter.addItem("Jonas Kalderstam - File Manager");
        mAdapter.addItem("Terlici - Drag and Swipe");
        mAdapter.addSectionHeaderItem("Designers/Coders");
        mAdapter.addItem("Krks.sdt - XInsta App Icon");
        mAdapter.addSectionHeaderItem("Testers");
        mAdapter.addItem("Exodius48 - MIUI Tester");
        mAdapter.addItem("J1gga84 - Bug Finder/Tester");
        mAdapter.addItem("Nostang3 - Tester");
        mAdapter.addSectionHeaderItem("Translators");
        mAdapter.addItem("Arabic - Mansour/Tariq-tq2011");
        mAdapter.addItem("Bosnian - Nedim");
        mAdapter.addItem("Brazilian (Portuguese) - Francisco");
        mAdapter.addItem("Chinese - 古月酋寸");
        mAdapter.addItem("Dutch - fnotsje");
        mAdapter.addItem("Finnish - Eelis");
        mAdapter.addItem("French - Nitorac");
        mAdapter.addItem("German - Dennis");
        mAdapter.addItem("Greek - UnRated");
        mAdapter.addItem("Hindi - Preshak");
        mAdapter.addItem("Indonesian - Ammar");
        mAdapter.addItem("Italian - AlphaUMi/RealGalaxyLink");
        mAdapter.addItem("Korean - 김민우");
        mAdapter.addItem("Kurdish - AhmAd202");
        mAdapter.addItem("Lithuanian - Laurynas");
        mAdapter.addItem("Persian - Farshid");
        mAdapter.addItem("Portuguese - Bruno/João/Pedro");
        mAdapter.addItem("Norwegian/Norsk - jipai17 ");
        mAdapter.addItem("Russian - Александр Спутай");
        mAdapter.addItem("Slovak - Árva");
        mAdapter.addItem("Spanish - Daniel/Lucas/Marcos");
        mAdapter.addItem("Thai - Vattikorn Donsakul");
        mAdapter.addItem("Turkish - Picknick/Sedat");
        listView.setAdapter(mAdapter);
    }
}
