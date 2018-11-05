package com.orion.synevent;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.orion.synevent.utils.DrawerUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ListEventActivity extends AppCompatActivity implements TabHost.TabContentFactory{

    private Toolbar mToolbar;
    private ListView lv;
    private TabHost tabHost;
    private ArrayList<HashMap<String, String>> list_events;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        setTabhost();
        initializeVars();
        setToolbar();
        setListEvents();
    }

    private void setListEvents() {
        list_events = new ArrayList<>();
        HashMap<String, String> ayu = new HashMap<>();
        ayu.put("text1", "Ayudantía Sistemas Operativos");
        ayu.put("number_of_users_in_event", "78 participantes");
        ayu.put("end_event", "finaliza el 98");
        list_events.add(ayu);


        HashMap<String, String> ayu2 = new HashMap<>();
        ayu2.put("text1", "Ayudantía Sistemas Distribuidos");
        ayu2.put("number_of_users_in_event", "18 participantes");
        ayu2.put("end_event", "finaliza el 18");
        list_events.add(ayu2);

        ListAdapter adapter;

        adapter = new SimpleAdapter(this, list_events, R.layout.item_event_frame,
                new String[]{"text1", "number_of_users_in_event", "end_event"},
                new int[]{R.id.text1, R.id.number_of_users_in_event, R.id.end_event});

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ListView lv = (ListView) arg0;
                LinearLayout tv = (LinearLayout) lv.getChildAt(arg2);
                //tv.getChildCount();
                TextView tv_name_event = (TextView)tv.getChildAt(1);
                String name_eve = tv_name_event.getText().toString();
                LinearLayout ll_status= (LinearLayout) tv.getChildAt(2);
                TextView tv_number_participants = (TextView) ll_status.getChildAt(0);
                String number_participants = tv_number_participants.getText().toString();
                TextView tv_finish = (TextView) ll_status.getChildAt(1);
                String finish = tv_finish.getText().toString();

                Intent myIntent = new Intent(ListEventActivity.this, StatusEventActivity.class);
                myIntent.putExtra("name_event",name_eve);
                myIntent.putExtra("number_participants",number_participants);
                myIntent.putExtra("finish",finish);
                startActivity(myIntent);
                finish();
            } });
    }

    private void setTabhost() {
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(getTabSpec1(tabHost));
        tabHost.addTab(getTabSpec2(tabHost));
        tabHost.addTab(getTabSpec3(tabHost));
    }

    public void initializeVars() {
        mToolbar = findViewById(R.id.activity_toolbar_list);
        lv = findViewById(R.id.mlistactive);
    }

    private TabHost.TabSpec getTabSpec1(TabHost tabHost) {
        return tabHost.newTabSpec("First Tab")
                .setIndicator("Active")
                .setContent(this);
    }

    private TabHost.TabSpec getTabSpec2(TabHost tabHost) {
        return tabHost.newTabSpec("Second Tab")
                .setIndicator("Finished")
                .setContent(this);
    }

    private TabHost.TabSpec getTabSpec3(TabHost tabHost) {
        TabHost.TabSpec aa =  tabHost.newTabSpec("Third Tab")
                .setIndicator("Join It")
                .setContent(this);
        //getJoinedEvents();
        return aa;
    }

    @Override
    public View createTabContent(String tag) {
        // Full ArrayAdapters to list the events
        //getJoinedEvents();
        if(tag == "Second Tab")
        {
            return  LayoutInflater.from(this).inflate(R.layout.finished_events,null);
        }else if(tag == "Third Tab"){
            //LinearLayout ll = (LinearLayout)getLayoutInflater().inflate(R.layout.join_event, null, true);
            View aa = LayoutInflater.from(this).inflate(R.layout.join_event,null);
            //setContentView(aa);

            //getJoinedEvents();
            return  aa;
        }else{
            return LayoutInflater.from(this).inflate(R.layout.actives_events, null);
        }
    }

    private void getJoinedEvents() {
        ArrayList<HashMap<String,String>> list_events_joined = new ArrayList<>();
        HashMap<String, String> ayu = new HashMap<>();
        ayu.put("text1", "Ayudantía Sistemas Operativos");
        ayu.put("number_of_users_in_event", "78 participantes");
        ayu.put("end_event", "finaliza el 98");
        list_events_joined.add(ayu);


        HashMap<String, String> ayu2 = new HashMap<>();
        ayu2.put("text1", "Ayudantía Sistemas Distribuidos");
        ayu2.put("number_of_users_in_event", "18 participantes");
        ayu2.put("end_event", "finaliza el 18");
        list_events.add(ayu2);

        ListAdapter adapter;

        adapter = new SimpleAdapter(this, list_events, R.layout.item_event_frame,
                new String[]{"text1", "number_of_users_in_event", "end_event"},
                new int[]{R.id.text1, R.id.number_of_users_in_event, R.id.end_event});


        ListView lv2 = findViewById(R.id.lv_joined_events);
        lv2.setAdapter(adapter);
    }

    public void setToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Events");
        mToolbar.setTitle("My Events");
        mToolbar.setTitleMargin(200, 2, 15, 2);
        mToolbar.setTitleTextColor(Color.WHITE);
        DrawerUtil.getDrawer(this, mToolbar);
    }

    public void joinToEvent(View view) {
        // connect to server to suscribe to event
        EditText et_code = findViewById(R.id.code_join_event);
        Toast.makeText(this,et_code.getText().toString(), Toast.LENGTH_LONG).show();
    }
}
