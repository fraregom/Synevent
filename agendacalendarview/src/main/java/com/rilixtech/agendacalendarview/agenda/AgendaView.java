package com.rilixtech.agendacalendarview.agenda;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import com.rilixtech.agendacalendarview.AgendaCalendarViewFetchListener;
import com.rilixtech.agendacalendarview.CalendarManager;
import com.rilixtech.agendacalendarview.R;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeekRecyclerView;
import com.rilixtech.agendacalendarview.calendar.weekslist.WeeksDayClickListener;
import com.rilixtech.agendacalendarview.models.IDayItem;
import java.util.Calendar;

public class AgendaView extends FrameLayout implements WeeksDayClickListener,
    WeekRecyclerView.CalendarScrolledListener, AgendaCalendarViewFetchListener {

  private AgendaListView mAgendaListView;
  private View mShadowView;
  private boolean enablePlaceholder = true;
  private AgendaViewListener listener;
  private OnAgendaViewDayItemClickListener mOnAgendaViewDayItemClickListener;

  public AgendaView(Context context) {
    super(context);
  }

  public AgendaView(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.view_agenda, this, true);
  }

  public interface AgendaViewListener {
    void onAgendaViewTouched();
  }

  public interface OnAgendaViewDayItemClickListener {
    void onDayItemClick(IDayItem iDayItem);
  }

  public void setAgendaViewListener(AgendaViewListener listener) {
    this.listener = listener;
  }

  public void setOnAgendaViewDayItemClickListener(OnAgendaViewDayItemClickListener listener) {
    mOnAgendaViewDayItemClickListener = listener;
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    mAgendaListView = findViewById(R.id.agenda_listview);
    mShadowView = findViewById(R.id.view_shadow);
  }

  @Override public boolean dispatchTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // if the user touches the listView, we put it back to the top
        translateList(0);
        break;
      default:
    }

    return super.dispatchTouchEvent(event);
  }

  public AgendaListView getAgendaListView() {
    return mAgendaListView;
  }

  public void translateList(final int targetY) {
    if (targetY != getTranslationY()) {
      ObjectAnimator mover = ObjectAnimator.ofFloat(this, "translationY", targetY);
      mover.setDuration(150);
      mover.addListener(new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animation) {
          mShadowView.setVisibility(GONE);
        }

        @Override public void onAnimationEnd(Animator animation) {
          if (targetY == 0) {
            listener.onAgendaViewTouched();
          }
          mShadowView.setVisibility(VISIBLE);
        }

        @Override public void onAnimationCancel(Animator animation) {

        }

        @Override public void onAnimationRepeat(Animator animation) {

        }
      });
      mover.start();
    }
  }

  public void enablePlaceholderForCalendar(boolean enable) {
    this.enablePlaceholder = enable;
  }

  @Override public void onDayItemClick(IDayItem iDayItem) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(iDayItem.getDate());
    getAgendaListView().scrollToCurrentDate(calendar);
    mOnAgendaViewDayItemClickListener.onDayItemClick(iDayItem);
  }

  @Override public void onWeekRecyclerViewCalendarScrolled() {
    int offset = (int) (3 * getResources().getDimension(R.dimen.day_cell_height));
    translateList(offset);
  }

  @Override public void onAgendaCalendarViewEventFetched() {
    ((AgendaAdapter) getAgendaListView().getAdapter()).swapEvents(
        CalendarManager.getInstance().getEvents());

    OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        if (getWidth() != 0 && getHeight() != 0) {
          // display only two visible rows on the calendar view
          if (enablePlaceholder) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
            int height = getHeight();
            Resources res = getContext().getResources();
            int margin = (int) (res.getDimension(R.dimen.calendar_header_height)
                    + 2 * res.getDimension(R.dimen.day_cell_height));
            params.height = height;
            params.setMargins(0, margin, 0, 0);
            setLayoutParams(params);
          }

          getAgendaListView().scrollToCurrentDate(CalendarManager.getInstance().getToday());
          getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
      }
    };
    getViewTreeObserver().addOnGlobalLayoutListener(listener);
  }
}
