package com.orion.synevent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import com.orion.synevent.utils.DrawerUtil;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ListEventActivity extends AppCompatActivity implements TabHost.TabContentFactory{

    private Toolbar mToolbar;
    private ListView mListActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        mToolbar = findViewById(R.id.activity_toolbar_list);

/*
        ArrayAdapter<String> lista = new ArrayAdapter<>();
        lista.add("Chile");
        ListAdapter adapter = new SimpleAdapter(
                ListEventActivity.this,lista ,
                R.layout.create_event, new String[]{"text"}, new int[]{R.id.text});

        lv.setAdapter(adapter);*/

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Mis eventos");
        mToolbar.setTitle("Mis Eventos");
        mToolbar.setTitleMargin(200,2,15,2);
        mToolbar.setTitleTextColor(Color.WHITE);

        DrawerUtil.getDrawer(this,mToolbar);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(getTabSpec1(tabHost));
        tabHost.addTab(getTabSpec2(tabHost));

    }

    private TabHost.TabSpec getTabSpec1(TabHost tabHost) {
        return tabHost.newTabSpec("First Tab")
                .setIndicator("Activos")
                .setContent(this);
    }

    private TabHost.TabSpec getTabSpec2(TabHost tabHost) {
        return tabHost.newTabSpec("Second Tab")
                .setIndicator("Terminados")
                .setContent(this);
    }

    @Override
    public View createTabContent(String tag) {
        // Full ArrayAdapters to list the events

        /*ArrayList<String> lista = new ArrayList<>();
        lista.add("Chile");
        mListActive = findViewById(R.id.mlistactive);
        mListActive.setAdapter(new ArrayAdapter<String>(ListEventActivity.this,R.layout.actives_events, lista));
        */
        if(tag == "Second Tab")
        {
            return  LayoutInflater.from(this).inflate(R.layout.finished_events,null);
        }
        return LayoutInflater.from(this).inflate(R.layout.actives_events, null);
    }
}
