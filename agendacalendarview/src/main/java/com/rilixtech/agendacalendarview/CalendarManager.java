package com.rilixtech.agendacalendarview;

import androidx.annotation.NonNull;
import android.view.View;
import com.rilixtech.agendacalendarview.agenda.AgendaEventView;
import com.rilixtech.agendacalendarview.models.BaseCalendarEvent;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.models.DayItem;
import com.rilixtech.agendacalendarview.models.IDayItem;
import com.rilixtech.agendacalendarview.models.IWeekItem;
import com.rilixtech.agendacalendarview.models.WeekItem;
import com.rilixtech.agendacalendarview.utils.DateHelper;
import android.content.Context;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * This class manages information about the calendar. (Events, weather info...)
 * Holds reference to the days list of the calendar.
 * As the app is using several views, we want to keep everything in one place.
 */
public class CalendarManager {
  private static final String TAG = CalendarManager.class.getSimpleName();

  private static CalendarManager mInstance;
  private Context mContext;
  private Locale mLocale;
  private Calendar mCalendarToday = null;
  private SimpleDateFormat mWeekdayFormatter;
  private SimpleDateFormat mMonthHalfNameFormat;
  private List<Integer> weekends = null;
  private int mWeekendsColor;
  private Calendar mTempCalendar;

  private int mFirstDayOfWeek;

  private int mEmptyEventVisibility;

  /**
   * List of days used by the calendar
   */
  private List<IDayItem> mDays;
  /**
   * List of weeks used by the calendar
   */
  private List<IWeekItem> mWeeks;
  /**
   * List of events instances
   */
  private List<CalendarEvent> mEvents;

  private CalendarManager(Context context) {
    this.mContext = context;
    // use default locale when locale is not set
    this.mLocale = Locale.getDefault();
    mDays = new ArrayList<>();
    mWeeks = new ArrayList<>();
    mEvents = new ArrayList<>();
  }

  static CalendarManager initInstance(Context context) {
    if (mInstance == null) mInstance = new CalendarManager(context);
    return mInstance;
  }

  public static CalendarManager getInstance() {
    if (mInstance == null) {
      throw new RuntimeException("Please create CalendarManager with initInstance first!");
    }
    return mInstance;
  }

  public Locale getLocale() {
    return mLocale;
  }

  public Context getContext() {
    return mContext;
  }

  public Calendar getToday() {
    if (mCalendarToday == null) {
      if (mLocale == null) {
        mCalendarToday = Calendar.getInstance();
      } else {
        mCalendarToday = Calendar.getInstance(mLocale);
      }
    }
    return mCalendarToday;
  }

  public void setTodayCalendar(Calendar today) {
    this.mCalendarToday = today;
  }

  public List<IWeekItem> getWeeks() {
    return mWeeks;
  }

  public List<CalendarEvent> getEvents() {
    return mEvents;
  }

  public List<IDayItem> getDays() {
    return mDays;
  }

  public SimpleDateFormat getWeekdayFormatter() {
    return mWeekdayFormatter;
  }

  public SimpleDateFormat getMonthHalfNameFormat() {
    return mMonthHalfNameFormat;
  }

  public List<Integer> getWeekends() {
    return weekends;
  }

  public void setWeekends(List<Integer> weekends) {
    this.weekends = weekends;
  }

  public int getWeekendsColor() {
    return mWeekendsColor;
  }

  public void setWeekendsColor(int weekendsColor) {
    this.mWeekendsColor = weekendsColor;
  }

  public void setEmptyEventVisibility(int visibility) {
    mEmptyEventVisibility = visibility;
  }

  public int getEmptyEventVisibility() {
    return mEmptyEventVisibility;
  }

  public void buildCalendar(@NonNull Calendar minDate, @NonNull Calendar maxDate,
      @NonNull Locale locale) {
    setLocale(locale);
    mDays.clear();
    mWeeks.clear();
    mEvents.clear();

    // maxDate is exclusive, here we bump back to the previous day, as maxDate if December 1st, 2020,
    // we don't include that month in our list
    maxDate.add(Calendar.MINUTE, -1);

    // Now we iterate between minDate and maxDate so we init our list of weeks
    int maxMonth = maxDate.get(Calendar.MONTH);
    int maxYear = maxDate.get(Calendar.YEAR);
    int currentMonth = minDate.get(Calendar.MONTH);
    int currentYear = minDate.get(Calendar.YEAR);

    while ((currentMonth <= maxMonth // Up to, including the month.
        || currentYear < maxYear) // Up to the year.
        && currentYear < maxYear + 1) { // But not > next yr.

      IWeekItem weekItem = generateWeek(minDate, currentYear, currentMonth);
      mWeeks.add(weekItem);

      Log.d(TAG, String.format("Adding week: %s", weekItem));
      minDate.add(Calendar.WEEK_OF_YEAR, 1);
      currentMonth = minDate.get(Calendar.MONTH);
      currentYear = minDate.get(Calendar.YEAR);
    }
  }

  /**
   * Generate week item by month and year
   *
   * @param calendar calendar of the week
   * @param year year of the week
   * @param month month of the week
   * @return week item
   */
  private IWeekItem generateWeek(Calendar calendar, int year, int month) {
    Date date = calendar.getTime();
    int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
    IWeekItem weekItem = new WeekItem();
    weekItem.setWeekInYear(weekOfYear);
    weekItem.setYear(year);
    weekItem.setDate(date);
    weekItem.setMonth(month);
    weekItem.setLabel(mMonthHalfNameFormat.format(date));
    List<IDayItem> dayItems = getDayCells(calendar); // gather days for the built week
    weekItem.setDayItems(dayItems);
    return weekItem;
  }

  private void setEventForDay(IWeekItem weekItem, DayItem dayItem, CalendarEvent event) {
    CalendarEvent todayEvent = (CalendarEvent) event.copy();
    Calendar dayInstance = (Calendar) mTempCalendar.clone();
    dayInstance.setTime(dayItem.getDate());
    todayEvent.instanceDay(dayInstance);
    todayEvent.dayReference(dayItem);
    todayEvent.weekReference(weekItem);
    // add instances in chronological order
    mEvents.add(todayEvent);

    dayItem.setEventTotal(dayItem.getEventTotal() + 1);

    // check if day is a weekend
    int dayOfWeek = dayInstance.get(Calendar.DAY_OF_WEEK);
    if (weekends != null) dayItem.setWeekend(weekends.contains(dayOfWeek));

    dayItem.setColor(event.getCalendarDayColor());
  }

  void loadEvents(List<CalendarEvent> events) {
    for (int i = 0; i < getWeeks().size(); i++) {
      IWeekItem weekItem = getWeeks().get(i);

      List<IDayItem> dayItems = weekItem.getDayItems();
      for (int weekItemIdx = 0; weekItemIdx < dayItems.size(); weekItemIdx++) {
        DayItem dayItem = (DayItem) dayItems.get(weekItemIdx);
        boolean isEventForAnotherDay = true;
        for (int eventIdx = 0; eventIdx < events.size(); eventIdx++) {
          CalendarEvent event = events.get(eventIdx);
          if (DateHelper.isBetweenInclusive(dayItem.getDate(), event.getStartTime(),
              event.getEndTime())) {
            setEventForDay(weekItem, dayItem, event);
            isEventForAnotherDay = false;
          }
        }
        if (isEventForAnotherDay) {
          setEmptyEvent(weekItem, dayItem);
        }
      }
    }
  }

  private void removeEmptyEventOf(CalendarEvent event) {
    for(Iterator<CalendarEvent> it = mEvents.iterator(); it.hasNext();) {
      CalendarEvent ev = it.next();
      if(event.getInstanceDay().equals(ev.getInstanceDay())) {
        if(ev.getStartTime() == null) it.remove();
      }
    }
  }

  void updateEvent(CalendarEvent fromEvent, CalendarEvent withEvent) {
    // to update event we need to remove the previous event first.
    deleteEvent(fromEvent);

    // then, add the new event
    // This is because we need to remove the empty event first if it this there,
    // then we need to move the event to another day or same day.
    addEvent(withEvent);
  }

  void addEvent(CalendarEvent newEvent) {
    for (int i = 0; i < getWeeks().size(); i++) {
      IWeekItem weekItem = getWeeks().get(i);

      List<IDayItem> dayItems = weekItem.getDayItems();
      for (int weekItemIdx = 0; weekItemIdx < dayItems.size(); weekItemIdx++) {
        DayItem dayItem = (DayItem) dayItems.get(weekItemIdx);

        if (DateHelper.isBetweenInclusive(dayItem.getDate(), newEvent.getStartTime(),
            newEvent.getEndTime())) {
          CalendarEvent dayEvent = (CalendarEvent) newEvent.copy();
          Calendar dayInstance = (Calendar) mTempCalendar.clone();
          dayInstance.setTime(dayItem.getDate());
          dayEvent.instanceDay(dayInstance);
          dayEvent.dayReference(dayItem);
          dayEvent.weekReference(weekItem);
          // add instances in chronological order
          //mEvents.add(0,todayEvent);

          saveEvent(mEvents, dayEvent);

          dayItem.setEventTotal(dayItem.getEventTotal() + 1);

          // check if day is a weekend
          int dayOfWeek = dayInstance.get(Calendar.DAY_OF_WEEK);
          if (weekends != null) dayItem.setWeekend(weekends.contains(dayOfWeek));

          dayItem.setColor(newEvent.getCalendarDayColor());


          removeEmptyEventOf(dayEvent);
        }
      }
    }
  }

  /**
   * Remove an Event from Calendar
   *
   * @param eventToRemove CalendarEvent to be removed
   * @return position of removed event.
   * -2 if event is an empty event.
   * -1 if event is not found.
   */
  int deleteEvent(CalendarEvent eventToRemove) {
    // ignore empty event
    if (eventToRemove.getStartTime() == null) return -2;

    int position = mEvents.indexOf(eventToRemove);
    if (position == -1) return position;

    IDayItem iDayItem = eventToRemove.getDayReference();
    IWeekItem weekItem = eventToRemove.getWeekReference();
    boolean isTheOnlyEventForTheDate = true;
    for (CalendarEvent event : mEvents) {
      // ignore empty event
      if (event.getStartTime() == null) continue;

      // Don't count itself
      if (event.equals(eventToRemove)) continue;

      if (event.getWeekReference().equals(weekItem) && event.getDayReference().equals(iDayItem)) {
        isTheOnlyEventForTheDate = false;
        break;
      }
    }

    iDayItem.setEventTotal(iDayItem.getEventTotal() - 1);

    if (isTheOnlyEventForTheDate) {
      iDayItem = iDayItem.copy();
      weekItem = weekItem.copy();
      mEvents.remove(position);
      addEmptyEvent(weekItem, iDayItem, position);
    } else {
      mEvents.remove(position);
    }
    return position;
  }

  private void saveEvent(List<CalendarEvent> events, CalendarEvent newEvent) {
    for (int i = 0; i < events.size(); i++) {
      CalendarEvent event = events.get(i);

      // ignore empty event
      if (event.getStartTime() == null) continue;

      // if event start is between start and end date
      if (DateHelper.isBetweenInclusive(newEvent.getStartTime().getTime(), event.getStartTime(),
          event.getEndTime())) {
        // check position of event
        DateHelper.DatePosition datePosition = DateHelper.getPositionOfDate(newEvent.getStartTime(),
            event.getStartTime());

        int position = -1;
        switch (datePosition) {
          case BEFORE:
            if (i == 0) {
              position = 0;
            } else {
              position = i - 1;
            }
            break;
          case AFTER:
            position = i + 1;
            break;
          case SAME:
            position = i;
            break;
          case UNKNOWN:
            // this shouldn't happens!
        }

        if (position >= 0) {
          // if previous event is empty event, replace it
          if (event.getWeekReference().equals(newEvent.getWeekReference())
              && event.getDayReference().equals(newEvent.getDayReference())) {
            if (event.getStartTime() == null) {
              events.remove(position);
            }
          }
          events.add(position, newEvent);

        }
        // stop searching
        break;
      }
    }
  }

  private void setEmptyEvent(IWeekItem weekItem, IDayItem dayItem) {
    addEmptyEvent(weekItem, dayItem, mEvents.size());
  }

  private void addEmptyEvent(IWeekItem weekItem, IDayItem dayItem, int position) {
    Calendar calendar = (Calendar) mTempCalendar.clone();
    calendar.setTime(dayItem.getDate());

    // check if day is a weekend
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    if (weekends != null) dayItem.setWeekend(weekends.contains(dayOfWeek));

    CalendarEvent event = new BaseCalendarEvent();
    event.instanceDay(calendar);
    event.startTime(null);
    event.dayReference(dayItem);
    event.weekReference(weekItem);
    event.location("");
    event.title(mContext.getString(R.string.agenda_event_no_events));
    event.placeholder(true);
    event.visibility(mEmptyEventVisibility);
    mEvents.add(position, event);
  }

  private List<IDayItem> getDayCells(Calendar startCal) {
    if (mTempCalendar == null) mTempCalendar = Calendar.getInstance(mLocale);
    mTempCalendar.setTime(startCal.getTime());
    List<IDayItem> dayItems = new ArrayList<>();

    int dayOfWeek = mTempCalendar.get(Calendar.DAY_OF_WEEK);
    int firstDayOfWeek =
        mFirstDayOfWeek == -1 ? mTempCalendar.getFirstDayOfWeek() : mFirstDayOfWeek;
    int offset = firstDayOfWeek - dayOfWeek;
    if (offset > 0) {
      offset -= 7;
    }
    mTempCalendar.add(Calendar.DATE, offset);

    Log.d(TAG, String.format("Building row week starting at %s", mTempCalendar.getTime()));
    for (int i = 0; i < 7; i++) {
      IDayItem dayItem = new DayItem();
      dayItem.buildDayItemFromCal(mTempCalendar);
      //dayItem.setEventTotal(eventPerDay(dayItem));
      dayItems.add(dayItem);
      mTempCalendar.add(Calendar.DATE, 1);
    }

    mDays.addAll(dayItems);
    return dayItems;
  }

  private void setLocale(Locale locale) {
    mLocale = locale;
    mCalendarToday = Calendar.getInstance(locale);
    mWeekdayFormatter = new SimpleDateFormat(mContext.getString(R.string.day_name_format), locale);
    String monthHalfNameFormat = mContext.getString(R.string.month_half_name_format);
    mMonthHalfNameFormat = new SimpleDateFormat(monthHalfNameFormat, locale);
  }

  public Calendar getTempCalendar() {
    if (mTempCalendar == null) mTempCalendar = Calendar.getInstance(mLocale);
    return mTempCalendar;
  }

  public void setFirstDayOfWeek(int firstDayOfWeek) {
    this.mFirstDayOfWeek = firstDayOfWeek;
  }
}
