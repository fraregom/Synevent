package com.rilixtech.agendacalendarview.agenda;

import androidx.annotation.ColorInt;
import com.rilixtech.agendacalendarview.CalendarManager;
import com.rilixtech.agendacalendarview.R;
import com.rilixtech.agendacalendarview.utils.DateHelper;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Header view for the StickyHeaderListView of the agenda view
 */
public class AgendaHeaderView extends LinearLayout {
  private TextView mTvDayOfMonth;
  private TextView mTvDayOfWeek;
  private View mViewCircle;
  private Calendar mCalendarToday;
  private SimpleDateFormat mSdfDayWeek;
  private float mDensityDisplay;
  private int mCurrentDayTextColor;
  private int mDefaultDayTextColor;

  public AgendaHeaderView(Context context) {
    super(context);
    init();
  }

  public AgendaHeaderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AgendaHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    Context cx = getContext();
    Resources resources = getResources();
    mDefaultDayTextColor = resources.getColor(R.color.calendar_text_default);
    mCurrentDayTextColor = resources.getColor(R.color.calendar_text_default);

    LayoutInflater inflater = LayoutInflater.from(cx);
    inflater.inflate(R.layout.view_agenda_header, this, true);
    mTvDayOfMonth = findViewById(R.id.view_agenda_day_of_month);
    mTvDayOfWeek = findViewById(R.id.view_agenda_day_of_week);
    mViewCircle = findViewById(R.id.view_day_circle_selected);

    mDensityDisplay = Resources.getSystem().getDisplayMetrics().density;
    CalendarManager manager = CalendarManager.getInstance();
    mCalendarToday = manager.getToday();

    mSdfDayWeek = new SimpleDateFormat(cx.getString(R.string.day_name_format), manager.getLocale());
  }

  public void setDay(Calendar day) {
    if (DateHelper.sameDate(day, mCalendarToday)) {
      mTvDayOfMonth.setTextColor(mCurrentDayTextColor);
      mViewCircle.setVisibility(VISIBLE);
      GradientDrawable drawable = (GradientDrawable) mViewCircle.getBackground();
      drawable.setStroke((int) (2 * mDensityDisplay), mCurrentDayTextColor);
    } else {
      mTvDayOfMonth.setTextColor(mDefaultDayTextColor);
      mViewCircle.setVisibility(INVISIBLE);
    }

    mTvDayOfMonth.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

    mTvDayOfWeek.setTextColor(mDefaultDayTextColor);
    mTvDayOfWeek.setText(mSdfDayWeek.format(day.getTime()));
  }

  public void setCurrentDayTextColor(@ColorInt int color) {
    mCurrentDayTextColor = color;
  }
}
