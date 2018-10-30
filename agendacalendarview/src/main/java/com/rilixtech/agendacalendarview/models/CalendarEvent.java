package com.rilixtech.agendacalendarview.models;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import java.util.Calendar;

import androidx.annotation.ColorInt;

public interface CalendarEvent<T> {

  T placeholder(boolean placeholder);

  boolean isPlaceholder();

  public String getLocation();

  public T location(String location);

  long getId();

  T id(long id);

  Calendar getStartTime();

  T startTime(Calendar startCalendarTime);

  Calendar getEndTime();

  T endTime(Calendar endCalendarTime);

  String getTitle();

  T title(String title);

  Calendar getInstanceDay();

  T instanceDay(Calendar instanceDay);

  IDayItem getDayReference();

  T dayReference(IDayItem dayReference);

  IWeekItem getWeekReference();

  T weekReference(IWeekItem weekReference);


  T calendarDayColor(@ColorInt int color);

  @ColorInt
  int getCalendarDayColor();

  T copy();
}
