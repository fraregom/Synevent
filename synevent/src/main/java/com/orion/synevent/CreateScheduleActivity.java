package com.orion.synevent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.Activities;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.User;
import com.orion.synevent.utils.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CreateScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = CreateScheduleActivity.class.getSimpleName();
    public Map<Integer, String> mapp_months = new HashMap<Integer, String>();
    public Map<String, String> mapp_week = new HashMap<String, String>();
    private Button mCreateSchedule;
    private Button mCancelSchedule;
    private Boolean first_time;
    private TextView txt_date_init;
    private TextView txt_date_fin;
    private Boolean pressed_btn_first = false;
    private Spinner spinner;
    private static final String[] paths = {"Does not repeat", "Every day", "Every Week", "Every Month", "Every Year"};

    private TextView tv_title_schedule;
    private EditText et_location;
    private String dateStart = null;
    private String dateEnd = null;
    private String dayStart = null;
    private String repeat = null;
    private String timeStart = null;
    private String timeEnd = null;

    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private String mToken;
    private String mSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_schedule);

        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateScheduleActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mCancelSchedule = findViewById(R.id.btn_cancel_schedule_create);
        mCreateSchedule = findViewById(R.id.btn_create_schedule);

        mapp_months = new HashMap<Integer, String>();
        mapp_months.put(1, "Jan.");
        mapp_months.put(2, "Feb.");
        mapp_months.put(3, "Mar.");
        mapp_months.put(4, "Apr.");
        mapp_months.put(5, "May.");
        mapp_months.put(6, "Jun.");
        mapp_months.put(7, "Jul.");
        mapp_months.put(8, "Aug.");
        mapp_months.put(9, "Sep.");
        mapp_months.put(10, "Oct.");
        mapp_months.put(11, "Nov.");
        mapp_months.put(12, "Dec.");

        mapp_week = new HashMap<String, String>();
        mapp_week.put("Mon", "MONDAY");
        mapp_week.put("Tue", "TUESDAY");
        mapp_week.put("Wed", "WEDNESDAY");
        mapp_week.put("Thu", "THURSDAY");
        mapp_week.put("Fri", "FRIDAY");
        mapp_week.put("Sat", "SATURDAY");
        mapp_week.put("Sun", "SUNDAY");

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
                newActivity(info);

                Intent intent = new Intent(v.getContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        first_time = false;
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN, "");
        mSchedule = mSharedPreferences.getString(Constants.ID_SCHEDULE,"");
    }

    private void showDatePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog datepickerdialog = DatePickerDialog.newInstance(
                CreateScheduleActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datepickerdialog.setThemeDark(false); //set dark them for dialog?
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
            timeStart = time + ":00";
            //pressed_btn_first = false;
        }else{
            String old = txt_date_fin.getText().toString();
            String mAlertDateTime = old + " " + time;
            txt_date_fin.setText(mAlertDateTime);
            timeEnd = time + ":00";
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

        String monthFix = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String dayFix = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;

        if (pressed_btn_first)
        {
            dateStart = String.valueOf(year) + "-" + monthFix +"-"+ dayFix + " " + timeStart + ":00";
            dayStart = mapp_week.get(dayOfTheWeek);
            txt_date_init.setText(dateExact);
        }else {
            dateEnd = String.valueOf(year) + "-" + monthFix +"-"+ dayFix + " " + timeEnd + ":00";
            txt_date_fin.setText(dateExact);
        }

        TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(CreateScheduleActivity.this,
                now2.get(Calendar.HOUR_OF_DAY), now2.get(Calendar.MINUTE), true);
        timepickerdialog.setThemeDark(false); //Dark Theme?
        timepickerdialog.vibrate(false); //vibrate on choosing time?
        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
        timepickerdialog.enableSeconds(false); //show seconds?

        //Handling cancel event
        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(CreateScheduleActivity.this, "Cancel choosing time", Toast.LENGTH_SHORT).show();
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

    public HashMap<String,String> obtainDataSchedule() {
        HashMap<String,String> info = new HashMap<>();
        tv_title_schedule = findViewById(R.id.et_title_new_schedule);
        et_location = findViewById(R.id.schedule_location);

        String title = tv_title_schedule.getText().toString();
        String location = et_location.getText().toString();
        String date_init = txt_date_init.getText().toString();
        String date_fin = txt_date_fin.getText().toString();

        if(validate(title,location)){
            info.put("title",title);
            info.put("location",location);
            info.put("init",date_init);
            info.put("fin",date_fin);

        }else{
            Toast.makeText(this,"Check your info!", Toast.LENGTH_LONG).show();
        }
        return info;
    }

    private Boolean validate(String title, String location){
        if(title.equals("")){
            return false;
        }else{
            //comprobar time
            return true;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                repeat = "";
                break;
            case 1:
                repeat = "DAILY";
                break;
            case 2:
                repeat = "WEEKLY";
                break;
            case 3:
                repeat = "MONTHLY";
                break;
            case 4:
                repeat = "EVERYYEAR";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void newActivity(HashMap<String,String> ActivityInfo){

        Activities activities = new Activities (ActivityInfo.get("title"), ActivityInfo.get("location"),
                dayStart, "", dateStart, dateEnd, repeat);

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).newActivity(mSchedule, activities)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Activities activitiesBody) {
        showToastMessage("Saved!");
    }

    private void handleError(Throwable error) {

        Log.e(TAG , String.valueOf(error));
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showToastMessage(response.getMsg());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showToastMessage("An unknown error has occurred!");
        }
    }


    private void showToastMessage(String message) {
        Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}