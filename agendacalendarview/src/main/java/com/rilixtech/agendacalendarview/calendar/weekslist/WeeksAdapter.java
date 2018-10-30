package com.rilixtech.agendacalendarview.calendar.weekslist;

import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.RelativeLayout;
import com.rilixtech.agendacalendarview.CalendarManager;
import com.rilixtech.agendacalendarview.R;
import com.rilixtech.agendacalendarview.models.IDayItem;
import com.rilixtech.agendacalendarview.models.IWeekItem;
import com.rilixtech.agendacalendarview.utils.DateHelper;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rilixtech.agendacalendarview.widgets.EventIndicatorView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class WeeksAdapter extends RecyclerView.Adapter<WeeksAdapter.WeekViewHolder> {

  private static final String TAG = WeeksAdapter.class.getSimpleName();

  private static final long FADE_DURATION = 250;
  private Calendar mCalendarToday;
  private List<IWeekItem> mWeeksList;
  private boolean mDragging;
  private boolean mAlphaSet;
  private int mDayTextColor, mPastDayTextColor, mCurrentDayColor;

  // this always reuse, so we need to make it a class scope.
  private CalendarManager mCalendarManager;
  private Locale mLocale;
  private SimpleDateFormat mMonthDateFormat;
  private float densityMetric;
  private List<WeeksDayClickListener> mListeners = new ArrayList<>();

  private WeeksAdapter() {
  }

  public WeeksAdapter(Calendar today, int dayTextColor, int currentDayTextColor,
      int pastDayTextColor, WeeksDayClickListener listener) {
    this.mCalendarToday = today;
    this.mDayTextColor = dayTextColor;
    this.mCurrentDayColor = currentDayTextColor;
    this.mPastDayTextColor = pastDayTextColor;
    mWeeksList = new ArrayList<>();
    densityMetric = Resources.getSystem().getDisplayMetrics().density;
    configureByCalendarManager();
    mListeners.add(listener);
  }

  public void addWeeksAdapterListener(WeeksDayClickListener listener) {
    mListeners.add(listener);
  }

  private void configureByCalendarManager() {
    mCalendarManager = CalendarManager.getInstance();
    mLocale = mCalendarManager.getLocale();
    Context ctx = mCalendarManager.getContext();
    mMonthDateFormat = new SimpleDateFormat(ctx.getString(R.string.month_name_format), mLocale);
  }

  public void updateWeeksItems(List<IWeekItem> weekItems) {
    this.mWeeksList.clear();
    this.mWeeksList.addAll(weekItems);
    notifyDataSetChanged();
  }

  public List<IWeekItem> getWeeksList() {
    return mWeeksList;
  }

  public boolean isDragging() {
    return mDragging;
  }

  public void setDragging(boolean dragging) {
    if (dragging != mDragging) {
      mDragging = dragging;
      notifyItemRangeChanged(0, mWeeksList.size());
    }
  }

  public boolean isAlphaSet() {
    return mAlphaSet;
  }

  public void setAlphaSet(boolean alphaSet) {
    mAlphaSet = alphaSet;
  }

  @NonNull @Override
  public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.list_item_week, parent, false);
    return new WeekViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull WeekViewHolder weekViewHolder, int position) {
    IWeekItem weekItem = mWeeksList.get(position);
    weekViewHolder.bindWeek(weekItem, mCalendarToday);
  }

  @Override public int getItemCount() {
    return mWeeksList.size();
  }

  class WeekViewHolder extends RecyclerView.ViewHolder {
    private List<RelativeLayout> mCells;
    private TextView mTvMonth;
    //private FrameLayout mMonthBackground;

    private TextView tvWeekMonth;
    private View vWeekCircleView;
    private TextView tvWeekDay;
    private EventIndicatorView eivWeekIndicatorView;

    WeekViewHolder(View itemView) {
      super(itemView);
      mTvMonth = itemView.findViewById(R.id.month_label);
      //mMonthBackground = itemView.findViewById(R.id.month_background);
      LinearLayout daysContainer = itemView.findViewById(R.id.week_days_container);
      mCells = new ArrayList<>();
      for (int i = 0; i < daysContainer.getChildCount(); i++) {
        mCells.add((RelativeLayout) daysContainer.getChildAt(i));
      }
    }

    private void bindCellItemChild(RelativeLayout cellItem) {
      tvWeekMonth = (TextView) cellItem.getChildAt(0);
      vWeekCircleView = cellItem.getChildAt(1);
      tvWeekDay = (TextView) cellItem.getChildAt(2);
      eivWeekIndicatorView = (EventIndicatorView) cellItem.getChildAt(3);
    }

    private RelativeLayout setupCellItem(RelativeLayout cellItem, final IDayItem dayItem) {
      if (dayItem.isWeekend()) {
        cellItem.setBackgroundColor(mCalendarManager.getWeekendsColor());
      } else {
        cellItem.setBackgroundColor(dayItem.getColor());
      }

      cellItem.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Log.d(TAG, "Clicked item");
          for (int j = 0; j < mListeners.size(); j++) {
            mListeners.get(j).onDayItemClick(dayItem);
          }
        }
      });
      return cellItem;
    }

    private void bindWeek(IWeekItem weekItem, Calendar today) {
      Log.d(TAG, "bindWeek called");
      setUpMonthOverlay(mTvMonth);
      List<IDayItem> dayItems = weekItem.getDayItems();
      Log.d(TAG, "dayItems = " + dayItems.size());

      for (int i = 0; i < dayItems.size(); i++) {
        final IDayItem dayItem = dayItems.get(i);
        RelativeLayout cellItem = setupCellItem(mCells.get(i), dayItem);
        bindCellItemChild(cellItem);

        tvWeekDay.setTextColor(mDayTextColor);
        // Display the day
        tvWeekDay.setText(String.format(mLocale, "%d", dayItem.getValue()));
        tvWeekMonth.setTextColor(mDayTextColor);
        // show event indicator
        eivWeekIndicatorView.setIndicatorAmount(dayItem.getEventTotal());

        setupIfDayIsInThePast(dayItem, today);

        // Highlight the cell if this day is today
        if (dayItem.isToday() && !dayItem.isSelected()) {
          tvWeekDay.setTextColor(mCurrentDayColor);
        }

        setSelectedDayCircle(dayItem);
        setHighlightFirstDayOfMonth(dayItem);
        setDisplayMonthLabel(mTvMonth, weekItem, dayItem);
      }
    }

    private void setupIfDayIsInThePast(IDayItem dayItem, Calendar today) {
      // Check if this day is in the past
      if (today.getTime().after(dayItem.getDate()) && !DateHelper.sameDate(today,
          dayItem.getDate())) {
        tvWeekDay.setTextColor(mPastDayTextColor);
        tvWeekMonth.setTextColor(mPastDayTextColor);
      }
    }

    private void setHighlightFirstDayOfMonth(IDayItem dayItem) {
      // Highlight first day of the month
      if (dayItem.isFirstDayOfTheMonth() && !dayItem.isSelected()) {
        tvWeekMonth.setVisibility(View.VISIBLE);
        tvWeekMonth.setText(dayItem.getMonth());
        tvWeekDay.setTypeface(null, Typeface.BOLD);
        tvWeekMonth.setTypeface(null, Typeface.BOLD);
      } else {
        tvWeekDay.setTypeface(null, Typeface.NORMAL);
        tvWeekMonth.setTypeface(null, Typeface.NORMAL);
      }
    }

    private void setSelectedDayCircle(IDayItem dayItem) {
      // Show a circle if the day is selected
      if (dayItem.isSelected()) {
        tvWeekDay.setTextColor(mDayTextColor);
        vWeekCircleView.setVisibility(View.VISIBLE);
        GradientDrawable drawable = (GradientDrawable) vWeekCircleView.getBackground();
        drawable.setStroke((int) (1 * densityMetric), mDayTextColor);
      } else {
        vWeekCircleView.setVisibility(View.GONE);
      }
    }

    private void setDisplayMonthLabel(TextView tvMonth, IWeekItem weekItem, IDayItem dayItem) {
      // Check if the month label has to be displayed
      if (dayItem.getValue() == 15) {
        tvMonth.setVisibility(View.VISIBLE);
        String month = mMonthDateFormat.format(weekItem.getDate()).toUpperCase();
        if (mCalendarToday.get(Calendar.YEAR) != weekItem.getYear()) {
          month = month + String.format(mLocale, " %d", weekItem.getYear());
        }
        tvMonth.setText(month);
      } else {
        tvWeekMonth.setVisibility(View.GONE);
      }
    }

    private void setUpMonthOverlay(TextView tvMonth) {
      tvMonth.setVisibility(View.GONE);
      if (isDragging()) {
        AnimatorSet animatorSetFadeIn = new AnimatorSet();
        animatorSetFadeIn.setDuration(FADE_DURATION);
        ObjectAnimator animatorTxtAlphaIn =
            ObjectAnimator.ofFloat(tvMonth, "alpha", tvMonth.getAlpha(), 1f);
        //ObjectAnimator animatorBackgroundAlphaIn =
        //    ObjectAnimator.ofFloat(mMonthBackground, "alpha", mMonthBackground.getAlpha(), 1f);
        animatorSetFadeIn.playTogether(animatorTxtAlphaIn
            //animatorBackgroundAlphaIn
        );
        animatorSetFadeIn.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animation) {

          }

          @Override public void onAnimationEnd(Animator animation) {
            setAlphaSet(true);
          }

          @Override public void onAnimationCancel(Animator animation) {

          }

          @Override public void onAnimationRepeat(Animator animation) {

          }
        });
        animatorSetFadeIn.start();
      } else {
        AnimatorSet animatorSetFadeOut = new AnimatorSet();
        animatorSetFadeOut.setDuration(FADE_DURATION);
        ObjectAnimator animatorTxtAlphaOut =
            ObjectAnimator.ofFloat(tvMonth, "alpha", tvMonth.getAlpha(), 0f);
        //ObjectAnimator animatorBackgroundAlphaOut =
        //    ObjectAnimator.ofFloat(mMonthBackground, "alpha", mMonthBackground.getAlpha(), 0f);
        animatorSetFadeOut.playTogether(animatorTxtAlphaOut
            //animatorBackgroundAlphaOut
        );
        animatorSetFadeOut.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animation) {

          }

          @Override public void onAnimationEnd(Animator animation) {
            setAlphaSet(false);
          }

          @Override public void onAnimationCancel(Animator animation) {

          }

          @Override public void onAnimationRepeat(Animator animation) {

          }
        });
        animatorSetFadeOut.start();
      }

      if (isAlphaSet()) {
        tvMonth.setAlpha(1f);
      } else {
        tvMonth.setAlpha(0f);
      }
    }
  }
}
