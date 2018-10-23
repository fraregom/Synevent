package com.orion.synevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.getbase.floatingactionbutton.FloatingActionButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.CalendarBody;
import com.orion.synevent.models.DayBody;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.User;
import com.orion.synevent.utils.Constants;
import com.orion.synevent.utils.DrawableCalendarEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MenuActivity extends AppCompatActivity implements CalendarPickerController {

    public static final String TAG = MenuActivity.class.getSimpleName();

    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;
    private AgendaCalendarView mAgendaCalendarView;
    private Toolbar mToolbar;

    private CompositeSubscription mSubscriptions;
    List<CalendarEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_menu);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        //setSchedule();
        loadCalendar();


        mToolbar  = findViewById(R.id.activity_toolbar);
        setSupportActionBar(mToolbar);



        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        //minDate.add(Calendar.MONTH, 0);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.DAY_OF_MONTH, 15);


        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);


        FloatingActionsMenu fab = findViewById(R.id.menu_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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


        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Response response) {

        User user = response.getUser();
        Log.i(TAG, user.getId().toString());


        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.ID,user.getId().toString());
        editor.putString(Constants.ID_SCHEDULE,"3");
        editor.apply();
    }


    /*private void setSchedule() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getSchedule()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSchedule,this::handleError));
    }


    private void handleSchedule(Schedule schedule) {


        //Log.i(TAG + "ID CAL", schedule.getId().toString());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.ID_SCHEDULE,"3");
        editor.apply();
    }*/


    private void loadCalendar() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getCalendar(mSharedPreferences.getString(Constants.ID_SCHEDULE,""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleCalendar,this::handleError));
    }

    private void handleCalendar(CalendarBody user) {

        for (Iterator<DayBody> iter = user.getMonday().iterator(); iter.hasNext(); ) {
            DayBody events = iter.next();

            String[] begin = events.getBeginsAt().split("([A-Z])");
            String[] beginDate =  begin[0].split("-");
            String[] beginHour =  begin[1].split(":");

            String[] end = events.getEndsAt().split("([A-Z])");
            String[] endDate =  end[0].split("-");
            String[] endHour =  end[1].split(":");

            Log.i(TAG,events.getEndsAt());

            mockList(eventList, Integer.parseInt(beginDate[1]),Integer.parseInt(beginDate[2]),
                    Integer.parseInt(beginHour[0]),Integer.parseInt(beginHour[1]),Integer.parseInt(endDate[1]),
                    Integer.parseInt(endDate[2]),Integer.parseInt(endHour[0]),Integer.parseInt(endHour[1]),
                    false,events.getName(), "",events.getPlace());


            // 1 - can call methods of element
            // 2 - can use iter.remove() to remove the current element from the list

            // ...
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

            showSnackBarMessage("Error en la red!");
        }
    }



    @Override
    public void onDaySelected(DayItem dayItem) {
        Log.d(TAG, String.format("Selected day: %s", dayItem));
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        Log.d(TAG, String.format("Selected event: %s", event));

    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }



    private void mockList(List<CalendarEvent> eventList,
                          int startMonth, int startDay,  int startHour, int startMin,
                          int endMonth, int endDay, int endHour, int endMin,
                          boolean allDay, String title, String description, String location) {

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        //Start an event!
        startTime.set(Calendar.DATE, startDay);
        startTime.set(Calendar.MONTH, startMonth);
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMin);
        startTime.set(Calendar.YEAR, 2018);

        //Finish an event!
        endTime.set(Calendar.DATE, endDay);
        endTime.set(Calendar.MONTH, endMonth);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMin);
        endTime.set(Calendar.YEAR, 2018);

        //Add description and color
        DrawableCalendarEvent event = new DrawableCalendarEvent(title, description, location,
                ContextCompat.getColor(this, R.color.purple),
                startTime, endTime, false, R.drawable.ic_location);
        eventList.add(event);

    }




    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.fragmentFrame),message,Snackbar.LENGTH_SHORT).show();

    }

    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(this, R.color.orange), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        BaseCalendarEvent event2 = new BaseCalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(this, R.color.yellow), startTime2, endTime2, true);
        eventList.add(event2);

        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        DrawableCalendarEvent event3 = new DrawableCalendarEvent("Visit of Harpa", "", "Dalvík",
                ContextCompat.getColor(this, R.color.blue), startTime3, endTime3, false, R.drawable.ic_location);
        eventList.add(event3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
