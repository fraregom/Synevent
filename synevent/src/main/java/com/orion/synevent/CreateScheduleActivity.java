package com.orion.synevent;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class CreateScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private Spinner spinner;
    private static final String[] paths = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado" , "Domingo"};
    private Button mCreateSchedule;
    private Button mCancelSchedule;
    private TextView tv_time_picker;
    private Boolean first_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateScheduleActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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


        mCreateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        first_time = false;
        tv_time_picker = findViewById(R.id.tv_time_schedule);
        tv_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

    }

    private void showTimePicker() {
        Calendar now2 = Calendar.getInstance();

        TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(CreateScheduleActivity.this,
                now2.get(Calendar.HOUR_OF_DAY), now2.get(Calendar.MINUTE), true);
        timepickerdialog.setThemeDark(false); //Dark Theme?
        timepickerdialog.vibrate(false); //vibrate on choosing time?
        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
        timepickerdialog.enableSeconds(false); //show seconds?

        if(first_time == false)
        {
            timepickerdialog.setTitle("Hora Inicio");
        }else {
            timepickerdialog.setTitle("Hora Fin");
        }
        //Handling cancel event
        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(CreateScheduleActivity.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
            }
        });
        timepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String text_old = tv_time_picker.getText().toString();


        if(!first_time){
            text_old = String.format("%02d:%02d", hourOfDay , minute );
            //text_old = hourOfDay + ":" + minute;
            tv_time_picker.setText("");
            tv_time_picker.setText(text_old);
            first_time = true;
            showTimePicker();
            Toast.makeText(CreateScheduleActivity.this,"Hora inicio bien",Toast.LENGTH_LONG).show();
        }else{
            String text = String.format("%02d:%02d", hourOfDay, minute );
            //String text = hourOfDay + ":" + minute;
            text_old += " - "+text;
            tv_time_picker.setText(text_old);
            Toast.makeText(CreateScheduleActivity.this,"Hora Fin bien",Toast.LENGTH_LONG).show();
            first_time = false;
        }
    }
}