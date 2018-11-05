package com.orion.synevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class CreateScheduleActivity extends AppCompatActivity {

    private Button mCreateSchedule;
    private Button mCancelSchedule;
    private Boolean first_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        mCancelSchedule = findViewById(R.id.btn_cancel_schedule_create);
        mCreateSchedule = findViewById(R.id.btn_create_schedule);

        mCancelSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        first_time = false;
    }
}