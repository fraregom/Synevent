package com.rilixtech.agendacalendarview.calendar;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rilixtech.agendacalendarview.CalendarManager;
import com.rilixtech.agendacalendarview.R;
import com.rilixtech.agendacalendarview.agenda.AgendaView;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeekRecyclerView;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeeksAdapter;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeeksDayClickListener;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.models.IDayItem;
import com.rilixtech.agendacalendarview.models.IWeekItem;
import com.rilixtech.agendacalendarview.utils.DateHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * The calendar view is a freely scrolling view that allows the user to browse between days of the
 * year.
 */
public class CalendarView extends LinearLayout implements WeekRecyclerView.CalendarScrolledListener,
    AgendaView.AgendaViewListener {

  private static final String LOG_TAG = CalendarView.class.getSimpleName();

  /**
   * Top of the calendar view layout, the week days list
   */
  private LinearLayout mDayNamesHeader;
  /**
   * Part of the calendar view layout always visible, the weeks list
   */
  private WeekRecyclerView mWeekRecyclerView;
  /**
   * The adapter for the weeks list
   */
  private WeeksAdapter mWeeksAdapter;
  /**
   * The current highlighted day in blue
   */
  private IDayItem mSelectedDay;
  /**
   * The current row displayed at top of the list
   */
  private int mCurrentListPosition;

  private CalendarManager calendarManager;

  public CalendarView(Context context) {
    super(context);
  }

  public CalendarView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.view_calendar, this, true);
    setOrientation(VERTICAL);
    inflateCalendarView();
  }

  public IDayItem getSelectedDay() {
    return mSelectedDay;
  }

  public void setSelectedDay(IDayItem mSelectedDay) {
    this.mSelectedDay = mSelectedDay;
  }

  public WeekRecyclerView getListViewWeeks() {
    return mWeekRecyclerView;
  }

  public void addWeeksDayClickListener(WeeksDayClickListener listener) {
    mWeeksAdapter.addWeeksAdapterListener(listener);
  }

  public void addWeekRecyclerViewListener(WeekRecyclerView.CalendarScrolledListener listener) {
    mWeekRecyclerView.addCalendarScrolledListener(listener);
  }

  private void inflateCalendarView() {
    mDayNamesHeader = findViewById(R.id.cal_day_names);
    mWeekRecyclerView = findViewById(R.id.list_week);
    mWeekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mWeekRecyclerView.setHasFixedSize(true);
    mWeekRecyclerView.setItemAnimator(null);
    mWeekRecyclerView.setSnapEnabled(true);

    // display only two visible rows on the calendar view
    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        if (getWidth() != 0 && getHeight() != 0) {
          collapseCalendarView();
          getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
      }
    });
  }

  public void init(int dayTextColor, int currentDayTextColor, int pastDayTextColor, int firstDayOfWeek) {
    calendarManager = CalendarManager.getInstance();
    Calendar today = calendarManager.getToday();
    Locale locale = calendarManager.getLocale();
    SimpleDateFormat weekDayFormatter = calendarManager.getWeekdayFormatter();
    List<IWeekItem> weeks = calendarManager.getWeeks();

    firstDayOfWeek = firstDayOfWeek == -1? today.getFirstDayOfWeek(): firstDayOfWeek;
    Log.d(LOG_TAG, "firstDayOfWeek = " + firstDayOfWeek);

    setupHeader(today, weekDayFormatter, locale, firstDayOfWeek);
    setupAdapter(today, weeks, dayTextColor, currentDayTextColor, pastDayTextColor);
    scrollToDate(today, weeks);
  }

  /**
   * Fired when the Agenda list view changes section.
   *
   * @param calendarEvent The event for the selected position in the agenda listview.
   */
  public void scrollToDate(final CalendarEvent calendarEvent) {
    mWeekRecyclerView.post(new Runnable() {
      @Override public void run() {
        scrollToPosition(updateSelectedDay(calendarEvent.getInstanceDay(), calendarEvent.getDayReference()));
      }
    });
  }

  public void scrollToDate(Calendar today, List<IWeekItem> weeks) {
    int currentWeekIndex = -1;

    for (int i = 0; i < weeks.size(); i++) {
      if (DateHelper.sameWeek(today, weeks.get(i))) {
        currentWeekIndex = i;
        break;
      }
    }

    if (currentWeekIndex != -1) {
      final int finalCurrentWeekIndex = currentWeekIndex;
      mWeekRecyclerView.post(new Runnable() {
        @Override public void run() {
          scrollToPosition(finalCurrentWeekIndex);
        }
      });
    }
  }

  public void setBackgroundColor(int color) {
    mWeekRecyclerView.setBackgroundColor(color);
  }

  private void scrollToPosition(int targetPosition) {
    mWeekRecyclerView.getLayoutManager().scrollToPosition(targetPosition);
  }

  private void updateItemAtPosition(int position) {
    WeeksAdapter weeksAdapter = (WeeksAdapter) mWeekRecyclerView.getAdapter();
    weeksAdapter.notifyItemChanged(position);
  }

  /**
   * Creates a new adapter if necessary and sets up its parameters.
   */
  private void setupAdapter(Calendar today, List<IWeekItem> weeks, int dayTextColor, int currentDayTextColor,
      int pastDayTextColor) {
    if (mWeeksAdapter == null) {
      Log.d(LOG_TAG, "Setting adapter with today's calendar: " + today.toString());
      WeeksDayClickListener listener = new WeeksDayClickListener() {
        @Override public void onDayItemClick(IDayItem iDayItem) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(iDayItem.getDate());
          updateSelectedDay(cal, iDayItem);
        }
      };
      mWeeksAdapter = new WeeksAdapter(today, dayTextColor, currentDayTextColor, pastDayTextColor, listener);
      mWeekRecyclerView.setAdapter(mWeeksAdapter);
      mWeekRecyclerView.addCalendarScrolledListener(this);
    }
    mWeeksAdapter.updateWeeksItems(weeks);
  }

  private void setupHeader(Calendar today, SimpleDateFormat weekDayFormatter, @NonNull Locale locale,
      int firstDayOfWeek) {
    int daysPerWeek = 7;
    String[] dayLabels = new String[daysPerWeek];
    Calendar cal = calendarManager.getTempCalendar();
    cal.setTime(today.getTime());
    for (int count = 0; count < 7; count++) {
      cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + count);
      dayLabels[count] = weekDayFormatter.format(cal.getTime()).toUpperCase(locale);
    }

    for (int i = 0; i < mDayNamesHeader.getChildCount(); i++) {
      TextView tvDay = (TextView) mDayNamesHeader.getChildAt(i);
      tvDay.setText(dayLabels[i]);
    }
  }

  private void expandCalendarView() {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
    layoutParams.height = (int) (getResources().getDimension(R.dimen.calendar_header_height) + 5 * getResources().getDimension(R.dimen.day_cell_height));
    setLayoutParams(layoutParams);
  }

  private void collapseCalendarView() {
    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
    layoutParams.height =
        (int) (getResources().getDimension(R.dimen.calendar_header_height) + 2 * getResources().getDimension(
            R.dimen.day_cell_height));
    setLayoutParams(layoutParams);
  }

  /**
   * Update a selected cell day item.
   *
   * @param calendar The Calendar instance of the day selected.
   * @param dayItem The DayItem information held by the cell item.
   * @return The selected row of the weeks list, to be updated.
   */
  private int updateSelectedDay(Calendar calendar, IDayItem dayItem) {
    //Integer currentWeekIndex = null;
    int currentWeekIndex = -1;

    // update highlighted/selected day
    if (!dayItem.equals(getSelectedDay())) {
      dayItem.setSelected(true);
      if (getSelectedDay() != null) {
        getSelectedDay().setSelected(false);
      }
      setSelectedDay(dayItem);
    }

    for (int c = 0; c < calendarManager.getWeeks().size(); c++) {
      if (DateHelper.sameWeek(calendar, calendarManager.getWeeks().get(c))) {
        currentWeekIndex = c;
        break;
      }
    }

    if (currentWeekIndex >= 0) {
      // highlighted day has changed, update the rows concerned
      if (currentWeekIndex != mCurrentListPosition) {
        updateItemAtPosition(mCurrentListPosition);
      }
      mCurrentListPosition = currentWeekIndex;
      updateItemAtPosition(currentWeekIndex);
    }

    return mCurrentListPosition;
  }

  @Override public void onWeekRecyclerViewCalendarScrolled() {
    expandCalendarView();
  }

  @Override public void onAgendaViewTouched() {
    collapseCalendarView();
  }
}
