package com.orion.synevent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.orion.synevent.utils.DrawerUtil;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class StatusEventActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView tv_name;
    private TextView tv_finish;
    private TextView tv_code;
    private ListView lv;
    private ArrayList<HashMap<String, String>> list_participants;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_events);

        Intent myIntent = getIntent(); // gets the previously created intent
        String name_event = myIntent.getStringExtra("name_event"); // will return "name_event"
        String number_participants = myIntent.getStringExtra("number_participants"); // will return "number_participants"
        String finish= myIntent.getStringExtra("finish");

        initializeVars(name_event,finish);
        setToolbar();
        setParticipantsEvent();
    }

    private void setParticipantsEvent() {
        lv = findViewById(R.id.lv_participants);
        list_participants = new ArrayList<>();

        HashMap<String, String> ayu = new HashMap<>();
        ayu.put("name_participant", "John Doe 2.0");
        list_participants.add(ayu);

        HashMap<String, String> ayu2 = new HashMap<>();
        ayu2.put("name_participant", "Benjamin Gautier 2.0");
        list_participants.add(ayu2);

        ListAdapter adapter;
        adapter = new SimpleAdapter(this, list_participants, R.layout.one_participant,
                new String[]{"name_participant"},
                new int[]{R.id.name_participant});

        lv.setAdapter(adapter);
    }

    private void initializeVars(String name,String finished) {
        mToolbar = findViewById(R.id.toolbar_one_event);
        tv_name = findViewById(R.id.name_event);
        tv_finish = findViewById(R.id.finish_by);
        tv_code = findViewById(R.id.code_event);
        tv_name.append(name);
        tv_finish.append(finished);
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Estado Evento");
        mToolbar.setTitle("Estado Evento");
        mToolbar.setTitleMargin(180, 2, 15, 2);
        mToolbar.setTitleTextColor(Color.WHITE);
        DrawerUtil.getDrawer(this, mToolbar);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.share));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_status, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_event:
                // Send to server event syncronize
                Intent refresh = new Intent(this, MenuActivity.class);
                startActivity(refresh);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
