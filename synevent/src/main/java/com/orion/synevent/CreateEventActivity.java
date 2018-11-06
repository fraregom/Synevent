package com.orion.synevent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.InvitationBody;
import com.orion.synevent.models.Response;
import com.orion.synevent.utils.Constants;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    public static final String TAG = CreateEventActivity.class.getSimpleName();
    public Map<Integer, String> mapp_months = new HashMap<Integer, String>();

    private TextView txt_date_init;
    private TextView txt_date_fin;
    private Boolean pressed_btn_first = false;
    private Button btn_save;
    private Button btn_cancel;
    private EditText et_title_event;
    private EditText et_location_event;
    private CheckBox finished_by_author;
    private CheckBox finished_by_time;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initSharedPreferences();
        mSubscriptions = new CompositeSubscription();

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

        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_event);
        btn_cancel = findViewById(R.id.btn_cancel_event_create);
        btn_save = findViewById(R.id.btn_create_event);
        et_title_event = findViewById(R.id.et_name_task);
        et_location_event = findViewById(R.id.event_location);
        finished_by_author = findViewById(R.id.cb_by_author);
        finished_by_time = findViewById(R.id.cb_by_time);

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
        btn_cancel.setOnClickListener(v -> cancelEvent(v));


        btn_save.setOnClickListener(v -> saveEvent(v));
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN, "");
    }


    private void showDatePicker(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog datepickerdialog = DatePickerDialog.newInstance(
                CreateEventActivity.this,
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

    public void cancelEvent(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveEvent(View view){
        Toast.makeText(this,"Saved!", Toast.LENGTH_LONG).show();
        // get values of form
        HashMap<String,String> form = obtainDataEvent();
        InvitationBody invitation = new InvitationBody(form.get("title"), form.get("finished_by"), form.get(""));

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).newInvitation(invitation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleInvitation,this::handleError));
    }

    public void handleInvitation(Response response){
        //InvitationBody body = response.getNewInvitation();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

        finish();
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


    public HashMap<String,String> obtainDataEvent() {
        HashMap<String,String> info = new HashMap<>();

        String title = et_title_event.getText().toString();
        String location = et_location_event.getText().toString();
        String date_init = txt_date_init.getText().toString();
        String date_fin = txt_date_fin.getText().toString();

        if(validate(title,location)){
            info.put("title",title);
            info.put("location", location);
            info.put("init",date_init);
            info.put("fin",date_fin);

            if(finished_by_time.isChecked()){
                info.put("finished_by","time");
            }else if(finished_by_author.isChecked()){
                info.put("finished_by","author");
            }else{
                return null;
            }
            return info;
        }else{
            return null;
        }
    }

    private Boolean validate(String title, String location){
        if(title.equals("")){
            return false;
        }else{
            if(finished_by_author.isChecked() && finished_by_time.isChecked()){
                return false;
            }
            return true;
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
