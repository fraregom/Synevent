package com.rilixtech.sample;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.rilixtech.agendacalendarview.AgendaCalendarView;
import com.rilixtech.agendacalendarview.models.BaseCalendarEvent;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.models.IDayItem;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
    AgendaCalendarView.AgendaCalendarViewListener {

  private static final String LOG_TAG = MainActivity.class.getSimpleName();

  private Toolbar mToolbar;
  private AgendaCalendarView mAgendaCalendarView;
  private TextView mTvDate;

  // region Lifecycle methods

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mToolbar = findViewById(R.id.activity_toolbar);
    mAgendaCalendarView = findViewById(R.id.agenda_calendar_view);
    mTvDate = findViewById(R.id.main_date_tv);

    setSupportActionBar(mToolbar);
    getSupportActionBar().setTitle("Agenda");
    mToolbar.setTitle("Agenda");

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
  }

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
          Toast.makeText(MainActivity.this, "position = " + position, Toast.LENGTH_SHORT).show();
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
    Log.d(LOG_TAG, String.format("Selected day: %s", dayItem));
    Toast.makeText(this, "dayItem = " + dayItem, Toast.LENGTH_SHORT).show();
  }

  @Override public void onEventClicked(CalendarEvent event) {
    Log.d(LOG_TAG, String.format("Selected event: %s", event));
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
    showPopUpMenu(mAgendaCalendarView, event);
  }

  private void mockList(List<CalendarEvent> eventList) {
    Calendar startTime1 = Calendar.getInstance();
    Calendar endTime1 = Calendar.getInstance();
    endTime1.add(Calendar.MONTH, 1);
    BaseCalendarEvent event1 = BaseCalendarEvent.prepareWith().title("Revisión requerimientos")
            .description("Diseño App")
            .location("Despacho 1")
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

    //DrawableCalendarEvent event2 = DrawableCalendarEvent.prepareWith()
    //    .drawableId(R.drawable.ic_launcher);
    //
    //    event2.title("Arquitectura, reunión seguimiento")
    //    .description("Reunión periodica importante")
    //    .location("Sala 2B")
    //    .color(ContextCompat.getColor(this, R.color.theme_event_pending))
    //    .startTime(startTime2)
    //    .endTime(endTime2)
    //    //.drawableId(R.drawable.ic_launcher)
    //    .allDay(true);
    //eventList.add(event2);
    //
    //Calendar startTime3 = Calendar.getInstance();
    //Calendar endTime3 = Calendar.getInstance();
    //startTime3.set(Calendar.HOUR_OF_DAY, 14);
    //startTime3.set(Calendar.MINUTE, 0);
    //endTime3.set(Calendar.HOUR_OF_DAY, 15);
    //endTime3.set(Calendar.MINUTE, 0);

    //DrawableCalendarEvent event3 = DrawableCalendarEvent.prepareWith()
    //    .drawableId(R.drawable.ic_launcher);
    //
    //    event3.title("14:00 - 15:00 Reunión de coordinación AGN")
    //    .description("i")
    //    .location("Despacho 2")
    //    .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
    //    .startTime(startTime3)
    //    .endTime(endTime3)
    //    .allDay(false);
    //eventList.add(event3);

    //Calendar startTime4 = Calendar.getInstance();
    //Calendar endTime4 = Calendar.getInstance();
    //startTime4.set(Calendar.HOUR_OF_DAY, 16);
    //startTime4.set(Calendar.MINUTE, 0);
    //endTime4.set(Calendar.HOUR_OF_DAY, 17);
    //endTime4.set(Calendar.MINUTE, 0);
    //
    //DrawableCalendarEvent event4 = DrawableCalendarEvent.prepareWith()
    //    .drawableId(R.drawable.ic_launcher);
    //
    //    event4.title("16:00 - 17:00 Reunión de coordinación AGN 2")
    //    .description("i")
    //    .location("Despacho 3")
    //    .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
    //    .startTime(startTime4)
    //    .endTime(endTime4)
    //    .allDay(false);
    //
    //eventList.add(event4);

    Calendar startTime5 = Calendar.getInstance();
    Calendar endTime5 = Calendar.getInstance();
    startTime5.set(Calendar.HOUR_OF_DAY, 18);
    startTime5.set(Calendar.MINUTE, 0);
    endTime5.set(Calendar.HOUR_OF_DAY, 19);
    endTime5.set(Calendar.MINUTE, 0);

    DrawableCalendarEvent event5 = DrawableCalendarEvent.prepareWith()
            .drawableId(R.drawable.ic_launcher);
    event5.title("16:00 - 17:00 Reunión de coordinación AGN 3")
            .description("i")
            .location("Despacho 3")
            .id(1)
            .color(ContextCompat.getColor(this, R.color.theme_event_confirmed))
            .startTime(startTime5)
            .endTime(endTime5)
            .calendarDayColor(ContextCompat.getColor(this, R.color.orange_dark))
            .allDay(false);

    eventList.add(event5);

    Calendar startTime6 = Calendar.getInstance();
    startTime6.add(Calendar.DAY_OF_YEAR, 1);
    Calendar endTime6 = Calendar.getInstance();
    endTime6.add(Calendar.DAY_OF_YEAR, 3);

    DrawableCalendarEvent event6 = DrawableCalendarEvent.prepareWith()
            .drawableId(R.drawable.ic_launcher);
    event6.title("Blacky number 1")
            .description("Our beloved song")
            .id(2)
            .location("Sala 2B")
            .color(ContextCompat.getColor(this, R.color.theme_event_pending))
            .startTime(startTime6)
            .endTime(endTime6)
            .allDay(true);
    eventList.add(event6);
  }
}
