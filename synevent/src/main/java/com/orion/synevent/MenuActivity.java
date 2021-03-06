package com.orion.synevent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.orion.synevent.models.Activities;
import com.orion.synevent.models.Schedule;
import com.orion.synevent.utils.DrawableCalendarEvent;
import com.orion.synevent.utils.DrawableEventRenderer;
import com.orion.synevent.utils.DrawerUtil;
import com.rilixtech.agendacalendarview.AgendaCalendarView;
import com.rilixtech.agendacalendarview.models.BaseCalendarEvent;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.models.IDayItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.User;
import com.orion.synevent.utils.Constants;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MenuActivity extends AppCompatActivity implements
        AgendaCalendarView.AgendaCalendarViewListener {

    public static final String TAG = MenuActivity.class.getSimpleName();

    private MenuItem mitem;
    private Toolbar mToolbar;
    private AgendaCalendarView mAgendaCalendarView;
    private TextView mTvDate;

    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    private CompositeSubscription mSubscriptions;
    List<CalendarEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);
        mSubscriptions = new CompositeSubscription();

        initSharedPreferences();

        mToolbar = findViewById(R.id.activity_toolbar);
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);
        mTvDate = findViewById(R.id.main_date_tv);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mSharedPreferences.getString(Constants.NAME_SCHEDULE,"Synevent APP"));
        mToolbar.setTitle(mSharedPreferences.getString(Constants.NAME_SCHEDULE,"Synevent APP"));
        mToolbar.setTitleTextColor(Color.WHITE);

        DrawerUtil.getDrawer(this,mToolbar);
        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        //minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.MONTH, 2);

        mockList(eventList);
        LoadCalendar();

        List<Integer> weekends = new ArrayList<>();
        weekends.add(Calendar.SUNDAY);
        mAgendaCalendarView.setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .setCalendarEvents(eventList)
                //.setLocale(Locale.ENGLISH)
                //.setEventRender(new DrawableEventRenderer())
                .setAgendaCalendarViewListener(this)
                //.setWeekendsColor(getResources().getColor(android.R.color.background_dark))
                .setWeekends(weekends)
                //.setFirstDayOfWeek(Calendar.MONDAY)
                .build();

        FloatingActionButton btn_new_event = findViewById(R.id.new_event);
        btn_new_event.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CreateEventActivity.class);
            startActivity(intent);
        });

        FloatingActionButton btn_new_schedule = findViewById(R.id.new_schedule);
        btn_new_schedule.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CreateScheduleActivity.class);
            startActivity(intent);
        });

    }

    /*private void logout() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();
        finish();
    }*/


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_add:
                Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();
                addNewEvent();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopUpMenu(View view, final CalendarEvent event) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_calendar, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                int position = mAgendaCalendarView.deleteEvent(event);
                Toast.makeText(MenuActivity.this, "position = " + position, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popupMenu.show();
    }

    @Override public void onDaySelected(IDayItem dayItem) {
        Log.d(TAG, String.format("Selected day: %s", dayItem));
        Toast.makeText(this, "dayItem = " + dayItem, Toast.LENGTH_SHORT).show();
    }

    @Override public void onEventClicked(CalendarEvent event) {
        Log.d(TAG, String.format("Selected event: %s", event));
    }

    @Override public void onScrollToDate(Calendar calendar) {
        //if (getSupportActionBar() != null) {
        mTvDate.setText(
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                        + " "
                        + calendar.get(Calendar.YEAR));
        //}
    }

    @Override public void onEventLongClicked(CalendarEvent event) {
        //showPopUpMenu(mAgendaCalendarView, event);
    }

    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        startTime1.add(Calendar.MONTH, -2);
        endTime1.add(Calendar.MONTH, 2);
        BaseCalendarEvent event = BaseCalendarEvent.prepareWith()
                .title("Synevent works!")
                .description("description")
                .location("USM")
                .id(0)
                .color(ContextCompat.getColor(this, R.color.yellow))
                .startTime(startTime1)
                .endTime(endTime1)
                //.drawableId(R.drawable.ic_launcher)
                .allDay(false);

        eventList.add(event);
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

        if(!mSharedPreferences.contains(Constants.ID_SCHEDULE)) GetSchedule();
        
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {
        User user = response.getUser();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.USERNAME, user.getUserName());
        editor.commit();
    }

    private void GetSchedule(){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getSchedule()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSchedule,this::handleError));
    }

    public void handleSchedule(List<Schedule> body){

        for(int i = 0; i <= body.size(); i++){
            if(body.get(i).getSelected()){
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(Constants.ID_SCHEDULE, body.get(i).getId().toString());
                editor.putString(Constants.NAME_SCHEDULE, body.get(i).getName());
                editor.commit();
                break;
            }
        }
    }

    private void LoadCalendar(){
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                .getCalendar(mSharedPreferences.getString(Constants.ID_SCHEDULE,"1"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCalendar,this::handleError));
    }

    public void handleCalendar(List<Activities> body){
        for(int i = 0; i < body.size(); i++){
            Calendar startTime1 = Calendar.getInstance();
            Calendar endTime1 = Calendar.getInstance();
            /*final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            final Calendar c = Calendar.getInstance();
            try {
                c.setTime(format.parse(body.get(i).getBeginsAt()));
                startTime1.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                startTime1.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                startTime1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                startTime1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                startTime1.set(Calendar.YEAR, c.get(Calendar.YEAR));

                c.setTime(format.parse(body.get(i).getEndsAt()));
                endTime1.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                endTime1.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                endTime1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                endTime1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                endTime1.set(Calendar.YEAR, c.get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
            BaseCalendarEvent event4 = BaseCalendarEvent.prepareWith()
                    .title(body.get(i).getName())
                    .description("")
                    .location(body.get(i).getPlace())
                    //.id(body.get(i).getId())
                    .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
                    .startTime(startTime1)
                    .endTime(endTime1)
                    .allDay(false);
            mAgendaCalendarView.addEvent(event4);
        }
    }


    private void handleError(Throwable error) {

        Log.e(TAG , String.valueOf(error));
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMsg());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("An unknown error has occurred!");
        }
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.fragmentFrame),message,Snackbar.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
