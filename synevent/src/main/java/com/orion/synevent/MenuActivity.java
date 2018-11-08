package com.orion.synevent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.content.Intent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.PopupMenu;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import com.mikepenz.materialdrawer.Drawer;
import com.orion.synevent.models.Activities;
import com.orion.synevent.models.Schedule;
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
    private Toolbar mToolbar;
    private TextView mTvDate;
    private AgendaCalendarView mAgendaCalendarView;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;
    private Drawer drawer;
    private CompositeSubscription mSubscriptions;
    List<CalendarEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);

        mToolbar = findViewById(R.id.activity_toolbar);
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);
        mTvDate = findViewById(R.id.main_date_tv);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mSharedPreferences.getString(Constants.NAME_SCHEDULE,"Synevent"));
        mToolbar.setTitle(mSharedPreferences.getString(Constants.NAME_SCHEDULE,"Synevent"));
        mToolbar.setTitleTextColor(Color.WHITE);

        drawer = DrawerUtil.getDrawer(this,mToolbar);
        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        Date date = java.util.Calendar.getInstance().getTime();

        //Log.i(TAG, String.valueOf(date.getDate()));

        //minDate.add(Calendar.DAY_OF_MONTH, date.getDate());
        minDate.set(Calendar.DAY_OF_MONTH, date.getDate());
        maxDate.add(Calendar.YEAR, 0);

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

    @Override
    protected void onStop() {
        super.onStop();
        drawer.deselect();
        drawer.closeDrawer();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Refresh main activity upon close of dialog box
                Intent refresh = new Intent(this, MenuActivity.class);
                startActivity(refresh);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopUpMenu(View view, final CalendarEvent event) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_calendar);
        popupMenu.setGravity(Gravity.END);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    int position = mAgendaCalendarView.deleteEvent(event);
                    Toast.makeText(MenuActivity.this, "position = " + position, Toast.LENGTH_SHORT).show();
                } else if(item.getItemId() == R.id.action_update) {
                    // change title of event
                    event.title("New title now");
                    mAgendaCalendarView.updateEvent(event);

                }
                return true;
            }
        });
        popupMenu.show();
    }

    @Override public void onDaySelected(IDayItem dayItem) {
        Log.d(TAG, String.format("Selected day: %s", dayItem));
        Toast.makeText(this, "dayItem = " + dayItem, Toast.LENGTH_SHORT).show();
    }

    @Override public void onEventClicked(View view, CalendarEvent event) {
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

    @Override public void onEventLongClicked(View view, CalendarEvent event) {
        showPopUpMenu(view, event);
    }

    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        startTime1.add(Calendar.MONTH, -2);
        endTime1.add(Calendar.MONTH, 2);
        BaseCalendarEvent event = BaseCalendarEvent.prepareWith()
                .title("")
                .description("")
                .location("")
                .id(0)
                .color(ContextCompat.getColor(this, R.color.transparent))
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
        if(!body.isEmpty()) {
            for (int i = 0; i <= body.size(); i++) {
                if (body.get(i).getSelected()) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.ID_SCHEDULE, body.get(i).getId().toString());
                    editor.putString(Constants.NAME_SCHEDULE, body.get(i).getName());
                    editor.commit();
                    break;
                }
            }
        }
    }

    private void LoadCalendar(){
        if(mSharedPreferences.contains(Constants.ID_SCHEDULE)) {
            mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                    .getCalendar(mSharedPreferences.getString(Constants.ID_SCHEDULE, "1"))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleCalendar, this::handleError));
        }else{
            showToastMessage("No events found, please create one!");
        }
    }

    public void handleCalendar(List<Activities> body){
        for(int i = 0; i < body.size(); i++){
            Calendar startTime1 = Calendar.getInstance();
            Calendar endTime1 = Calendar.getInstance();
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            final Calendar c = Calendar.getInstance();
            try {
                c.setTime(format.parse(body.get(i).getBeginsAt()));
                //startTime1.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                //startTime1.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                startTime1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                //startTime1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                //startTime1.set(Calendar.YEAR, c.get(Calendar.YEAR));

                c.setTime(format.parse(body.get(i).getEndsAt()));
                //endTime1.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                //endTime1.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                //Log.i(TAG, String.valueOf(c.get(Calendar.MONTH)));
                endTime1.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
                //endTime1.set(Calendar.MONTH, c.get(Calendar.MONTH));
                //endTime1.set(Calendar.YEAR, c.get(Calendar.YEAR));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BaseCalendarEvent event4 = BaseCalendarEvent.prepareWith()
                    .title(body.get(i).getName())
                    .description("")
                    .location(body.get(i).getPlace())
                    .id(body.get(i).getId())
                    .color(ContextCompat.getColor(this, R.color.orange))
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
