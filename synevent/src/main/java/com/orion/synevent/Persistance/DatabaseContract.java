package com.orion.synevent.Persistance;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    public DatabaseContract () {}

    public static abstract class Schedule implements BaseColumns {
        public static final String TABLE_NAME = "SCHEDULE";

        public static final String COL_NAME_SCHEDULE_NAME = "name";
        public static final String COL_NAME_SCHEDULE_SELECTED = "selected";
        public static final String COL_NAME_SCHEDULE_CREATED_AT = "createdAt";
        public static final String COL_NAME_SCHEDULE_UPDATED_AT = "updatedAt";


        public static final String SQL_CREATE_SCHEDULE_TABLE =
                "CREATE TABLE " + Schedule.TABLE_NAME + " (" +
                        Schedule._ID + " INTEGER PRIMARY KEY," +
                        Schedule.COL_NAME_SCHEDULE_NAME + TEXT_TYPE + COMMA_SEP +
                        Schedule.COL_NAME_SCHEDULE_SELECTED + INTEGER_TYPE + COMMA_SEP +
                        Schedule.COL_NAME_SCHEDULE_CREATED_AT + TEXT_TYPE + COMMA_SEP+
                        Schedule.COL_NAME_SCHEDULE_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                        " )";

        public static final String SQL_DELETE_SCHEDULE =
                "DROP TABLE IF EXISTS " + Schedule.TABLE_NAME;
    }

    public static abstract class Activity implements BaseColumns {
        public static final String TABLE_NAME = "ACTIVITY";

        public static final String COL_NAME_ACTIVITY_NAME = "name";
        public static final String COL_NAME_ACTIVITY_PLACE = "place";
        public static final String COL_NAME_ACTIVITY_DAY = "day";
        public static final String COL_NAME_ACTIVITY_BLOCK_TAG = "blockTag";
        public static final String COL_NAME_ACTIVITY_BEGINS_AT = "beginsAt";
        public static final String COL_NAME_ACTIVITY_ENDS_AT = "endsAt";
        public static final String COL_NAME_ACTIVITY_PERIOD = "period";
        public static final String COL_NAME_ACTIVITY_CREATED_AT = "createdAt";
        public static final String COL_NAME_ACTIVITY_UPDATED_AT = "updatedAt";
        public static final String COL_NAME_ACTIVITY_SCHEDULE_ID = "scheduleId";


        public static final String SQL_CREATE_ACTIVITY_TABLE =
                "CREATE TABLE " + Activity.TABLE_NAME + " (" +
                        Activity._ID + " INTEGER PRIMARY KEY," +
                        Activity.COL_NAME_ACTIVITY_NAME + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_PLACE + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_DAY + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_BLOCK_TAG + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_BEGINS_AT + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_ENDS_AT + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_CREATED_AT + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_UPDATED_AT + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_PERIOD + TEXT_TYPE + COMMA_SEP +
                        Activity.COL_NAME_ACTIVITY_SCHEDULE_ID + INTEGER_TYPE + COMMA_SEP +
                        "FOREIGN KEY ("+COL_NAME_ACTIVITY_SCHEDULE_ID+") REFERENCES "+
                        Schedule.TABLE_NAME+"("+Schedule._ID+")"+
                        " )";

        public static final String SQL_DELETE_ACTIVITY =
                "DROP TABLE IF EXISTS " + Activity.TABLE_NAME;
    }
}
