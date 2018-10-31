package com.orion.synevent;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import com.orion.synevent.utils.DrawableCalendarEvent;
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
import java.util.ArrayList;
import java.util.Calendar;
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
    private AgendaCalendarView mAgendaCalendarView;
    private TextView mTvDate;

    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);
        mSubscriptions = new CompositeSubscription();

        mToolbar = findViewById(R.id.activity_toolbar);
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);
        mTvDate = findViewById(R.id.main_date_tv);

        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Agenda");
        //mToolbar.setTitle("Agenda");

        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, 0);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 0);

        List<CalendarEvent> eventList = new ArrayList<>();
        mockList(eventList);

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

        initSharedPreferences();
        loadProfile();


        FloatingActionButton btn_new_event = (FloatingActionButton) findViewById(R.id.new_event);
        btn_new_event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
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
            case R.id.action_add:
                Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();
                addNewEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopUpMenu(View view, final CalendarEvent event) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_calendar, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    int position = mAgendaCalendarView.deleteEvent(event);
                    Toast.makeText(MenuActivity.this, "position = " + position, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void addNewEvent() {

        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        BaseCalendarEvent event4 = BaseCalendarEvent.prepareWith()
                .title("NEW ITEM")
                .description("NEW ITEM")
                .location("NEW ITEM")
                .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
                .startTime(startTime1)
                .endTime(endTime1)
                .allDay(false);
        mAgendaCalendarView.addEvent(event4);
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
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = BaseCalendarEvent.prepareWith().title("Synevent works!")
                .description("description")
                .location("USM")
                .id(0)
                .color(ContextCompat.getColor(this, R.color.theme_event_pending))
                .startTime(startTime1)
                .endTime(endTime1)
                //.drawableId(R.drawable.ic_launcher)
                .allDay(true);

        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);

        Calendar startTime5 = Calendar.getInstance();
        Calendar endTime5 = Calendar.getInstance();
        startTime5.set(Calendar.HOUR_OF_DAY, 18);
        startTime5.set(Calendar.MINUTE, 0);
        endTime5.set(Calendar.HOUR_OF_DAY, 19);
        endTime5.set(Calendar.MINUTE, 0);

        DrawableCalendarEvent event5 = DrawableCalendarEvent.prepareWith()
                .drawableId(R.drawable.location);
        event5.title("Meeting!")
                .description("i")
                .location("USM")
                .id(1)
                .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
                .startTime(startTime5)
                .endTime(endTime5)
                .calendarDayColor(ContextCompat.getColor(this, R.color.orange))
                .allDay(false);

        eventList.add(event5);
    }


    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

        Log.i(TAG, "loadProfile success");
    }

    private void handleResponse(Response response) {

        User user = response.getUser();
        Log.i(TAG, response.getMsg());

        /*mTvName.setText(user.getUserName());
        mTvEmail.setText(user.getEmail());

        long timestamp = Long.parseLong(user.getIat().toString()) * 1000L;
        mTvDate.setText(getDate(timestamp ));*/
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

            showSnackBarMessage("Error en la red!");
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
