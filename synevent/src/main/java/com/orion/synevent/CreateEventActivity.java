package com.orion.synevent;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;



public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {


    private TextView txt_view_date;
    private TextView txt_view_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        txt_view_date = (TextView) findViewById(R.id.txt_date);
        txt_view_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog datepickerdialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        CreateEventActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datepickerdialog.setThemeDark(true); //set dark them for dialog?
                datepickerdialog.vibrate(true); //vibrate on choosing date?
                datepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
                datepickerdialog.showYearPickerFirst(false); //choose year first?
                datepickerdialog.setAccentColor(Color.parseColor("#9C27A0")); // custom accent color
                datepickerdialog.setTitle("Please select a date"); //dialog title
                datepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog

                datepickerdialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar now2 = Calendar.getInstance();
                        TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(CreateEventActivity.this,
                                now2.get(Calendar.HOUR_OF_DAY), now2.get(Calendar.MINUTE), true);
                        timepickerdialog.setThemeDark(false); //Dark Theme?
                        timepickerdialog.vibrate(false); //vibrate on choosing time?
                        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
                        timepickerdialog.enableSeconds(true); //show seconds?

                        //Handling cancel event
                        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Toast.makeText(CreateEventActivity.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                            }
                        });
                        timepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
                    }
                });
                txt_view_time = (TextView) findViewById(R.id.txt_time);
                txt_view_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar now = Calendar.getInstance();
                        TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(CreateEventActivity.this,
                                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                        timepickerdialog.setThemeDark(false); //Dark Theme?
                        timepickerdialog.vibrate(false); //vibrate on choosing time?
                        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
                        timepickerdialog.enableSeconds(true); //show seconds?

                        //Handling cancel event
                        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Toast.makeText(CreateEventActivity.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
                            }
                        });
                        timepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
                    }
                });
            }
        });
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        txt_view_time.setText(time);
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: " + dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        txt_view_date.setText(date);
    }
}
