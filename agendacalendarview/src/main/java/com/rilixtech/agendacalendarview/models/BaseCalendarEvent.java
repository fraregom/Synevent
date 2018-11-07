package com.rilixtech.agendacalendarview.models;

import androidx.annotation.ColorInt;
import java.util.Calendar;

/**
 * Event model class containing the information to be displayed on the agenda view.
 */
public class BaseCalendarEvent implements CalendarEvent {

  /**
   * Id of the event.
   */
  protected long mId;
  /**
   * Color to be displayed in the agenda view.
   */
  protected int mColor;
  /**
   * Title of the event.
   */
  protected String mTitle;
  /**
   * Description of the event.
   */
  protected String mDescription;
  /**
   * Where the event takes place.
   */
  protected String mLocation;
  /**
   * Calendar instance helping sorting the events per section in the agenda view.
   */
  private Calendar mInstanceDay;
  /**
   * Start time of the event.
   */
  protected Calendar mStartTime;
  /**
   * End time of the event.
   */
  protected Calendar mEndTime;
  /**
   * Indicates if the event lasts all day.
   */
  protected boolean mIsAllDay;
  /**
   * Tells if this BaseCalendarEvent instance is used as a placeholder in the agenda view, if there's
   * no event for that day.
   */
  protected boolean mIsPlaceHolder;
  /**
   * Tells if this BaseCalendarEvent instance is used as a forecast information holder in the agenda
   * view.
   */
  protected boolean mIsWeather;
  /**
   * Duration of the event.
   */
  protected String mDuration;
  /**
   * References to a DayItem instance for that event, used to link interaction between the
   * calendar view and the agenda view.
   */
  protected IDayItem mDayReference;
  /**
   * References to a WeekItem instance for that event, used to link interaction between the
   * calendar view and the agenda view.
   */
  protected IWeekItem mWeekReference;
  /**
   * Weather icon string returned by the Dark Sky API.
   */
  protected String mWeatherIcon;
  /**
   * Temperature value returned by the Dark Sky API.
   */
  protected double mTemperature;

  /**
   * Color of day in calendar. Using integer RGB value
   */
  protected int mCalendarDayColor;

  protected int mVisibilityType;

  public BaseCalendarEvent() {
  }

  //protected T obj;
  protected BaseCalendarEvent createObj() {
    //BaseCalendarEvent<BaseCalendarEvent> event = new BaseCalendarEvent<>();
    return new BaseCalendarEvent();
  }

  public static BaseCalendarEvent prepareWith() {
    return new BaseCalendarEvent();
  }

  protected BaseCalendarEvent(BaseCalendarEvent calendarEvent) {
    this.mId = calendarEvent.mId;
    this.mTitle = calendarEvent.mTitle;
    this.mColor = calendarEvent.mColor;
    this.mDescription = calendarEvent.mDescription;
    this.mLocation = calendarEvent.mLocation;
    this.mStartTime = calendarEvent.mStartTime;
    this.mEndTime = calendarEvent.mEndTime;
    this.mIsAllDay = calendarEvent.mIsAllDay;
    this.mIsPlaceHolder = calendarEvent.mIsPlaceHolder;
    this.mIsWeather = calendarEvent.mIsWeather;
    this.mDuration = calendarEvent.mDuration;
    this.mDayReference = calendarEvent.mDayReference;
    this.mWeekReference = calendarEvent.mWeekReference;
    this.mWeatherIcon = calendarEvent.mWeatherIcon;
    this.mTemperature = calendarEvent.mTemperature;
    this.mCalendarDayColor = calendarEvent.mCalendarDayColor;
    this.mVisibilityType = calendarEvent.mVisibilityType;
  }

  public int getColor() {
    return mColor;
  }

  public BaseCalendarEvent color(int color) {
    this.mColor = color;
    return this;
  }

  public String getDescription() {
    return mDescription;
  }

  public boolean isAllDay() {
    return mIsAllDay;
  }

  public BaseCalendarEvent allDay(boolean allDay) {
    this.mIsAllDay = allDay;
    return this;
  }

  public BaseCalendarEvent description(String description) {
    this.mDescription = description;
    return this;
  }

  public Calendar getInstanceDay() {
    return mInstanceDay;
  }

  public BaseCalendarEvent instanceDay(Calendar instanceDay) {
    this.mInstanceDay = instanceDay;
    this.mInstanceDay.set(Calendar.HOUR, 0);
    this.mInstanceDay.set(Calendar.MINUTE, 0);
    this.mInstanceDay.set(Calendar.SECOND, 0);
    this.mInstanceDay.set(Calendar.MILLISECOND, 0);
    this.mInstanceDay.set(Calendar.AM_PM, 0);

    return this;
  }

  public Calendar getEndTime() {
    return mEndTime;
  }

  public BaseCalendarEvent endTime(Calendar endCalendarTime) {
    this.mEndTime = endCalendarTime;
    return this;
  }

  public BaseCalendarEvent placeholder(boolean placeholder) {
    mIsPlaceHolder = placeholder;
    return this;
  }

  public boolean isPlaceholder() {
    return mIsPlaceHolder;
  }

  public long getId() {
    return mId;
  }

  public BaseCalendarEvent id(long id) {
    this.mId = id;
    return this;
  }

  public String getLocation() {
    return mLocation;
  }

  public BaseCalendarEvent location(String location) {
    this.mLocation = location;
    return this;
  }

  public Calendar getStartTime() {
    return mStartTime;
  }

  public BaseCalendarEvent startTime(Calendar startCalendarTime) {
    this.mStartTime = startCalendarTime;
    return this;
  }

  public String getTitle() {
    return mTitle;
  }

  public BaseCalendarEvent title(String title) {
    this.mTitle = title;
    return this;
  }

  public String getDuration() {
    return mDuration;
  }

  public BaseCalendarEvent duration(String duration) {
    this.mDuration = duration;
    return this;
  }

  public boolean isPlaceHolder() {
    return mIsPlaceHolder;
  }

  public BaseCalendarEvent placeHolder(boolean placeHolder) {
    this.mIsPlaceHolder = placeHolder;
    return this;
  }

  public boolean isWeather() {
    return mIsWeather;
  }

  public BaseCalendarEvent weather(boolean weather) {
    this.mIsWeather = weather;
    return this;
  }

  public IDayItem getDayReference() {
    return mDayReference;
  }

  public BaseCalendarEvent dayReference(IDayItem dayReference) {
    this.mDayReference = dayReference;
    return this;
  }

  public IWeekItem getWeekReference() {
    return mWeekReference;
  }

  public BaseCalendarEvent weekReference(IWeekItem weekReference) {
    this.mWeekReference = weekReference;
    return this;
  }

  public String getWeatherIcon() {
    return mWeatherIcon;
  }

  public BaseCalendarEvent weatherIcon(String weatherIcon) {
    this.mWeatherIcon = weatherIcon;
    return this;
  }

  public double getTemperature() {
    return mTemperature;
  }

  public BaseCalendarEvent temperature(double temperature) {
    this.mTemperature = temperature;
    return this;
  }

  @ColorInt
  public int getCalendarDayColor() {
    return mCalendarDayColor;
  }

  public BaseCalendarEvent calendarDayColor(@ColorInt int calendarDayColor) {
    this.mCalendarDayColor = calendarDayColor;
    return this;
  }

  @Override public BaseCalendarEvent copy() {
    return new BaseCalendarEvent(this);
    //return createObj();
  }

  @Override public BaseCalendarEvent visibility(int visibilityType) {
    mVisibilityType = visibilityType;
    return this;
  }

  @Override public int getVisibility() {
    return mVisibilityType;
  }

  @Override public String toString() {
    return "BaseCalendarEvent{"
        + "title='"
        + mTitle
        + ", instanceDay= "
        + mInstanceDay.getTime()
        + "}";
  }
}
