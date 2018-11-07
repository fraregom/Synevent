package com.rilixtech.agendacalendarview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.rilixtech.agendacalendarview.agenda.AgendaAdapter;
import com.rilixtech.agendacalendarview.agenda.AgendaView;
import com.rilixtech.agendacalendarview.calendar.CalendarView;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeeksDayClickListener;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.models.IDayItem;
import com.rilixtech.agendacalendarview.render.AbstractEventRenderer;
import com.rilixtech.agendacalendarview.utils.ListViewScrollTracker;
import com.rilixtech.agendacalendarview.widgets.FloatingActionButton;
import com.rilixtech.stickylistheaders.StickyListHeadersListView;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * View holding the agenda and calendar view together.
 */
public class AgendaCalendarView extends FrameLayout
    implements StickyListHeadersListView.OnStickyHeaderChangedListener, WeeksDayClickListener,
    AgendaView.OnAgendaViewDayItemClickListener {

  private static final String LOG_TAG = AgendaCalendarView.class.getSimpleName();

  private CalendarView mCalendarView;
  private AgendaView mAgendaView;
  private FloatingActionButton mFabDirection;

  private int mAgendaCurrentDayTextColor;
  private int mCalendarHeaderColor;
  private int mCalendarBackgroundColor;
  private int mCalendarDayTextColor;
  private int mCalendarPastDayTextColor;
  private int mCalendarCurrentDayColor;
  private int mFabColor;
  private int mWeekendColor;
  private int mEventEmptyVisibility;

  private AgendaCalendarViewListener agendaCalendarViewListener;
  private ListViewScrollTracker mAlvScrollTracker;

  private AgendaCalendarViewFetchListener mAgendaCalendarViewFetchListener;

  public interface AgendaCalendarViewListener {
    void onDaySelected(IDayItem dayItem);
    void onEventClicked(View view, CalendarEvent event);
    void onScrollToDate(Calendar calendar);
    void onEventLongClicked(View view, CalendarEvent event);
  }

  public void setAgendaCalendarViewFetchListener(AgendaCalendarViewFetchListener listener) {
    mAgendaCalendarViewFetchListener = listener;
  }

  private AbsListView.OnScrollListener mAgendaScrollListener = new AbsListView.OnScrollListener() {
    private int mCurrentAngle;
    private int mMaxAngle = 85;

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
        int totalItemCount) {
      int scrollY = mAlvScrollTracker.calculateScrollY(firstVisibleItem, visibleItemCount);
      if (scrollY != 0) mFabDirection.show();

      Log.d(LOG_TAG, String.format("Agenda listView scrollY: %d", scrollY));
      int toAngle = scrollY / 100;
      if (toAngle > mMaxAngle) {
        toAngle = mMaxAngle;
      } else if (toAngle < -mMaxAngle) {
        toAngle = -mMaxAngle;
      }
      RotateAnimation rotate =
          new RotateAnimation(mCurrentAngle, toAngle, mFabDirection.getWidth() / 2,
              mFabDirection.getHeight() / 2);
      rotate.setFillAfter(true);
      mCurrentAngle = toAngle;
      mFabDirection.startAnimation(rotate);
    }
  };

  public AgendaCalendarView(Context context) {
    super(context);
  }

  public AgendaCalendarView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AgendaCalendarView, 0, 0);

    int defAgendaCurrentDayTextColor = getResources().getColor(R.color.theme_primary);
    //int defBackgroundColor = defAgendaCurrentDayTextColor;
    int defFabColor = getResources().getColor(R.color.theme_accent);
    int defHeaderColor = getResources().getColor(R.color.theme_light_primary);
    int defDayTextColor = getResources().getColor(R.color.theme_text_icons);
    int defCalCurrentDayColor = getResources().getColor(R.color.calendar_text_current_day);
    int defPastDayTextColor = getResources().getColor(R.color.theme_light_primary);

    int weekendColor = getResources().getColor(R.color.calendar_day_weekend);

    mAgendaCurrentDayTextColor = a.getColor(R.styleable.AgendaCalendarView_acv_agendaCurrentDayTextColor,
            defAgendaCurrentDayTextColor);
    mCalendarHeaderColor =
        a.getColor(R.styleable.AgendaCalendarView_acv_calendarHeaderColor, defHeaderColor);
    mCalendarBackgroundColor =
        a.getColor(R.styleable.AgendaCalendarView_acv_calendarColor, defAgendaCurrentDayTextColor);
    mCalendarDayTextColor =
        a.getColor(R.styleable.AgendaCalendarView_acv_calendarDayTextColor, defDayTextColor);
    mCalendarCurrentDayColor =
        a.getColor(R.styleable.AgendaCalendarView_acv_calendarCurrentDayTextColor,
            defCalCurrentDayColor);
    mCalendarPastDayTextColor =
        a.getColor(R.styleable.AgendaCalendarView_acv_calendarPastDayTextColor, defPastDayTextColor);
    mFabColor = a.getColor(R.styleable.AgendaCalendarView_acv_fabColor, defFabColor);
    mWeekendColor = a.getColor(R.styleable.AgendaCalendarView_acv_weekendColor, weekendColor);

    // default is visible for empty event.
     mEventEmptyVisibility = a.getInt(R.styleable.AgendaCalendarView_acv_emptyEventVisibility, View.VISIBLE);

    setAlpha(0f);
    a.recycle();

    inflateCalendarView();
  }

  private void inflateCalendarView() {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.view_agendacalendar, this, true);

    mCalendarView = (CalendarView) getChildAt(0);
    mAgendaView = (AgendaView) getChildAt(1);
    mFabDirection = (FloatingActionButton) getChildAt(2);

    ColorStateList csl = new ColorStateList(new int[][] { new int[0] }, new int[] { mFabColor });
    mFabDirection.setBackgroundTintList(csl);

    mCalendarView.findViewById(R.id.cal_day_names).setBackgroundColor(mCalendarHeaderColor);
    mCalendarView.findViewById(R.id.list_week).setBackgroundColor(mCalendarBackgroundColor);

    mAgendaView.setOnAgendaViewDayItemClickListener(this);
    mAgendaView.getAgendaListView()
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            agendaCalendarViewListener.onEventClicked(view,
                CalendarManager.getInstance().getEvents().get(position));
          }
        });

    mAgendaView.getAgendaListView().setOnItemLongClickListener(
        new AdapterView.OnItemLongClickListener() {
          @Override
          public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            agendaCalendarViewListener.onEventLongClicked(view,
                CalendarManager.getInstance().getEvents().get(position));
            return false;
          }
        });
  }

  private void animateView() {
    ObjectAnimator alphaAnimation =
        ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f).setDuration(500);

    alphaAnimation.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        final long fabAnimationDelay = 500;
        // Just after setting the alpha from this view to 1, we hide the fab.
        // It will reappear as soon as the user is scrolling the Agenda view.
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            mFabDirection.hide();
            mAlvScrollTracker = new ListViewScrollTracker(mAgendaView.getAgendaListView());
            mAgendaView.getAgendaListView().setOnScrollListener(mAgendaScrollListener);
            mFabDirection.setOnClickListener(new OnClickListener() {
              @Override public void onClick(View v) {
                mAgendaView.translateList(0);
                mAgendaView.getAgendaListView()
                    .scrollToCurrentDate(CalendarManager.getInstance().getToday());
                new Handler().postDelayed(new Runnable() {
                  @Override public void run() {
                    mFabDirection.hide();
                  }
                }, fabAnimationDelay);
              }
            });
          }
        }, fabAnimationDelay);
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });
    alphaAnimation.start();

    mAgendaCalendarViewFetchListener.onAgendaCalendarViewEventFetched();
  }

  @Override public void onStickyHeaderChanged(StickyListHeadersListView stickyListHeadersListView,
      View header, int position, long headerId) {
    Log.d(LOG_TAG,
        String.format("onStickyHeaderChanged, position = %d, headerId = %d", position, headerId));

    if (CalendarManager.getInstance().getEvents().size() > 0) {
      CalendarEvent event = CalendarManager.getInstance().getEvents().get(position);
      if (event != null) {
        mCalendarView.scrollToDate(event);
        agendaCalendarViewListener.onScrollToDate(event.getInstanceDay());
      }
    }
  }

  private void initCalendarManager(Calendar minDate, Calendar maxDate, List<CalendarEvent> events,
      Locale locale, List<Integer> weekends, int weekendColor, int firstDayOfWeek, int emptyEventVisibility) {
    CalendarManager calendarManager = CalendarManager.initInstance(getContext());
    calendarManager.setWeekends(weekends);
    calendarManager.setWeekendsColor(weekendColor);
    calendarManager.setFirstDayOfWeek(firstDayOfWeek);
    calendarManager.setEmptyEventVisibility(emptyEventVisibility);
    if(locale == null) locale = Locale.getDefault();
    calendarManager.buildCalendar(minDate, maxDate, locale);
    calendarManager.loadEvents(events);
  }

  private Calendar minimumDate = null;
  private Calendar maximumDate = null;
  private Locale locale = null;
  private List<CalendarEvent> events = null;
  private AbstractEventRenderer<?> mAbstractEventRenderer = null;
  private List<Integer> weekends = null;
  private int firstDayOfWeek = -1;

  public AgendaCalendarView setMinimumDate(@NonNull Calendar minimumDate) {
    this.minimumDate = minimumDate;
    return this;
  }

  public AgendaCalendarView setMaximumDate(@NonNull Calendar maximumDate) {
    this.maximumDate = maximumDate;
    return this;
  }

  public AgendaCalendarView setCalendarEvents(@NonNull List<CalendarEvent> events) {
    this.events = events;
    return this;
  }

  public AgendaCalendarView setLocale(@Nullable Locale locale) {
    this.locale = locale;
    return this;
  }

  public AgendaCalendarView setAgendaCalendarViewListener(AgendaCalendarViewListener listener) {
    agendaCalendarViewListener = listener;
    return this;
  }

  public AgendaCalendarView setEventRender(@Nullable AbstractEventRenderer<?> renderer) {
    mAbstractEventRenderer = renderer;
    return this;
  }

  public AgendaCalendarView setWeekends(@Nullable List<Integer> weekends) {
    this.weekends = weekends;
    return this;
  }

  public AgendaCalendarView setWeekendsColor(int weekendsColor) {
    this.mWeekendColor = weekendsColor;
    return this;
  }

  // set Calendar.firstDayOfWeek
  public AgendaCalendarView setFirstDayOfWeek(int firstDayOfWeek) {
    this.firstDayOfWeek = firstDayOfWeek;
    return this;
  }

  public void build() {
    if (minimumDate == null) throw new RuntimeException("Please set minimumDate");
    if (maximumDate == null) throw new RuntimeException("Please set maximumDate");
    if (events == null) throw new RuntimeException("Please set events");
    if (agendaCalendarViewListener == null) {
      throw new RuntimeException("Please set CalendarPickerController");
    }

    init(minimumDate, maximumDate, events, locale, agendaCalendarViewListener,
        mAbstractEventRenderer,
        weekends, mWeekendColor, firstDayOfWeek, mEventEmptyVisibility);
  }

  private void init(Calendar minDate, Calendar maxDate, List<CalendarEvent> eventList,
      Locale locale, AgendaCalendarViewListener pickerController, AbstractEventRenderer<?> eventRenderer,
      List<Integer> weekends, int weekendColor, int firstDayOfWeek, int emptyEventVisibility) {
    agendaCalendarViewListener = pickerController;

    initCalendarManager(minDate, maxDate, eventList, locale, weekends, weekendColor, firstDayOfWeek, emptyEventVisibility);

    // Feed our views with weeks list and events
    mCalendarView.init(mCalendarDayTextColor, mCalendarCurrentDayColor, mCalendarPastDayTextColor, firstDayOfWeek);

    // Load agenda events and scroll to current day
    AgendaAdapter agendaAdapter = new AgendaAdapter(mAgendaCurrentDayTextColor, eventRenderer);

    mAgendaView.getAgendaListView().setAdapter(agendaAdapter);
    mAgendaView.getAgendaListView().setOnStickyHeaderChangedListener(this);
    mCalendarView.addWeeksDayClickListener(mAgendaView);
    mCalendarView.addWeekRecyclerViewListener(mAgendaView);
    mAgendaView.setAgendaViewListener(mCalendarView);

    setAgendaCalendarViewFetchListener(mAgendaView);

    animateView();
    // notify that actually everything is loaded
    mAgendaCalendarViewFetchListener.onAgendaCalendarViewEventFetched();
    Log.d(LOG_TAG, "CalendarEventTask finished");
  }

  public void enableCalenderView(boolean enable) {
    mAgendaView.enablePlaceholderForCalendar(enable);
    mCalendarView.setVisibility(enable ? VISIBLE : GONE);
    mAgendaView.findViewById(R.id.view_shadow).setVisibility(enable ? VISIBLE : GONE);
  }

  public void enableFloatingIndicator(boolean enable) {
    mFabDirection.setVisibility(enable ? VISIBLE : GONE);
  }

  @Override public void onDayItemClick(IDayItem iDayItem) {
    agendaCalendarViewListener.onDaySelected(iDayItem);
  }

  public void addEvent(CalendarEvent event) {
    CalendarManager.getInstance().addEvent(event);
    mAgendaCalendarViewFetchListener.onAgendaCalendarViewEventFetched();
  }

  public int deleteEvent(CalendarEvent event) {
    int total = CalendarManager.getInstance().getEvents().size();
    Log.d(LOG_TAG, "total events before deleting = " + total);
    int position = CalendarManager.getInstance().deleteEvent(event);
    total = CalendarManager.getInstance().getEvents().size();
    Log.d(LOG_TAG, "total events after deleting = " + total);
    ((AgendaAdapter)mAgendaView.getAgendaListView().getAdapter()).notifyDataSetChanged();
    mAgendaCalendarViewFetchListener.onAgendaCalendarViewEventFetched();
    return position;
  }

  public void updateEvent(CalendarEvent fromEvent) {
    mAgendaCalendarViewFetchListener.onAgendaCalendarViewEventFetched();
  }
}
