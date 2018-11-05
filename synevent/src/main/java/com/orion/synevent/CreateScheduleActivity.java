package com.orion.synevent;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.SimpleTimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class CreateScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    private Spinner spinner;
    private static final String[] paths = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" , "Sunday"};
    private Button mCreateSchedule;
    private Button mCancelSchedule;
    private TextView tv_time_picker;
    private Boolean first_time;

    private TextView tv_title_schedule;
    private String Day;
    private EditText et_location;

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
                HashMap<String,String> info = obtainDataSchedule(); //data perteneciente a crear schedule
                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        first_time = false;
        tv_time_picker = findViewById(R.id.tv_time_schedule);
        tv_time_picker.setOnClickListener(v -> showTimePicker());

    }

    public HashMap<String,String> obtainDataSchedule() {
        HashMap<String,String> info = new HashMap<>();
        tv_title_schedule = findViewById(R.id.et_title_new_schedule);
        et_location = findViewById(R.id.schedule_location);


        String title = tv_title_schedule.getText().toString();
        String time = tv_time_picker.getText().toString();
        String location = et_location.getText().toString();

        if(validate(title,time,location)){
            info.put("title",title);
            info.put("day",Day);
            info.put("time",time);
            info.put("location",location);

        }else{
            Toast.makeText(this,"Ponga titulo y fecha, campos requeridos", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    private Boolean validate(String title, String time, String location){
        if(title == "" || time == ""){
            return false;
        }else{
            //comprobar time
            return true;
        }
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
                Day = "Mon";
                break;
            case 1:
                Day = "Tue";
                break;
            case 2:
                Day = "Wed";
                break;
            case 3:
                Day = "Thu";
                break;
            case 4:
                Day = "Fri";
                break;
            case 5:
                Day = "Sat";
                break;
            case 6:
                Day = "Sun";
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

    public void cancelSche(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveSche(View view){
        Toast.makeText(this,"Saved!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        finish();
    }

}