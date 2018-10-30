package com.orion.synevent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;



public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    public Map<Integer, String> mapp_months = new HashMap<Integer, String>();

    private TextView txt_date_init;
    private TextView txt_date_fin;
    private Boolean pressed_btn_first = false;
    private Button btn_save;
    private Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        btn_save = findViewById(R.id.btn_create_event);
        btn_cancel = findViewById(R.id.btn_cancel_event_create);

        mapp_months = new HashMap<Integer, String>();
        mapp_months.put(1, "Enero");
        mapp_months.put(2, "Feb.");
        mapp_months.put(3, "Mar.");
        mapp_months.put(4, "Abr.");
        mapp_months.put(5, "May.");
        mapp_months.put(6, "Jun.");
        mapp_months.put(7, "Jul.");
        mapp_months.put(8, "Ago.");
        mapp_months.put(9, "Sep.");
        mapp_months.put(10, "Oct.");
        mapp_months.put(11, "Nov.");
        mapp_months.put(12, "Dic.");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_event);
        txt_date_init = findViewById(R.id.txt_date);
        txt_date_init.setOnClickListener(v -> {
            setPressed_btn_first(true);
            showDatePicker();
        });

        txt_date_fin = (TextView) findViewById(R.id.txt_time);
        txt_date_fin.setOnClickListener(
            v -> {
                setPressed_btn_first(false);
                showDatePicker();

        });
    }

    private void showDatePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog datepickerdialog = DatePickerDialog.newInstance(
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
    }

    public void setPressed_btn_first(Boolean flag)
    {
        this.pressed_btn_first = flag;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time =  hourString + ":" + minuteString;
        if (pressed_btn_first)
        {
            String old = txt_date_init.getText().toString();
            String mAlertDateTime = old + " " + time;
            txt_date_init.setText(mAlertDateTime);
            //pressed_btn_first = false;
        }else{
            String old = txt_date_fin.getText().toString();
            String mAlertDateTime = old + " " + time;
            txt_date_fin.setText(mAlertDateTime);
        }

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar now2 = Calendar.getInstance();
        // obtain name day
        String dayOfTheWeek = whatNameDay(dayOfMonth,monthOfYear,year);
        // obtain name month
        String month= mapp_months.get(++monthOfYear);
        String dateExact =  dayOfTheWeek+" "+(dayOfMonth) + " " + (month);
        if (pressed_btn_first)
        {
            txt_date_init.setText(dateExact);
        }else {
            txt_date_fin.setText(dateExact);
        }

        TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(CreateEventActivity.this,
                    now2.get(Calendar.HOUR_OF_DAY), now2.get(Calendar.MINUTE), true);
        timepickerdialog.setThemeDark(false); //Dark Theme?
        timepickerdialog.vibrate(false); //vibrate on choosing time?
        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
        timepickerdialog.enableSeconds(false); //show seconds?

        //Handling cancel event
        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            Toast.makeText(CreateEventActivity.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
            }
        });
        timepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog
    }
    private String whatNameDay(int dayOfMonth , int monthOfYear, int year){

        SimpleDateFormat sdf = new SimpleDateFormat("EE",Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dayOfMonth + "/"+(++monthOfYear)+"/"+year;
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        long startDate;
        Date d;
        try {
            date = sdf2.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startDate = date.getTime();
        d = new Date(startDate);
        String dayOfTheWeek = sdf.format(d);

        return dayOfTheWeek;
    }

    public void cancelEvent(View view) throws Throwable {

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        this.finalize();
    }

    public void saveEvent(View view) throws Throwable {
        Toast.makeText(this,"Bien", Toast.LENGTH_LONG).show();

        // get values of form
        
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        this.finalize();
    }
}
