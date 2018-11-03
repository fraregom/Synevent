package com.orion.synevent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import com.orion.synevent.utils.DrawerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ListEventActivity extends AppCompatActivity implements TabHost.TabContentFactory{

    private Toolbar mToolbar;
    private ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        mToolbar = findViewById(R.id.activity_toolbar_list);


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



        lv = findViewById(R.id.mlistactive);

        ArrayList<HashMap<String,String>> list = new ArrayList<>();

        HashMap<String,String> ayu = new HashMap<>();
        ayu.put("text1", "Ayudantía Sistemas Operativos");
        ayu.put("number_of_users_in_event", "78 participantes");
        ayu.put("end_event","finaliza el 98");
        list.add(ayu);

        ListAdapter adapter;

        adapter = new SimpleAdapter(this,list,R.layout.item_event_frame,
                new String[]{"text1","number_of_users_in_event","end_event"},
                new int[]{R.id.text1,R.id.number_of_users_in_event,R.id.end_event});

        lv.setAdapter(adapter);

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

        if(tag == "Second Tab")
        {
            return  LayoutInflater.from(this).inflate(R.layout.finished_events,null);
        }

        return LayoutInflater.from(this).inflate(R.layout.actives_events, null);
    }
}
