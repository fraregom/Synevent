package com.orion.synevent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StatusEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_events);
        Intent myIntent = getIntent(); // gets the previously created intent
        String name_event = myIntent.getStringExtra("name_event"); // will return "FirstKeyValue"
        String number_participants = myIntent.getStringExtra("number_participants"); // will return "FirstKeyValue"
        String finish= myIntent.getStringExtra("finish");
        
    }
}
