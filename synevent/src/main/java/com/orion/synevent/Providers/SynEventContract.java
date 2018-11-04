package com.orion.synevent.Providers;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public final class SynEventContract {
    public static final String AUTHORITY = "com.orion.synevent.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri SCHEDULE_URI = Uri.withAppendedPath(SynEventContract.BASE_URI, "/schedule");
    public static final Uri SCHEDULE_ACTIVITY_URI = Uri.withAppendedPath(SynEventContract.BASE_URI, "/schedule/activity");
    public static final Uri ACTIVITY_URI = Uri.withAppendedPath(SynEventContract.BASE_URI, "/activity");

    public static final String URI_TYPE_SCHEDULE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd."+ AUTHORITY + ".schedule";
    public static final String URI_TYPE_SCHEDULE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd."+ AUTHORITY + ".schedule";
    public static final String URI_TYPE_ACTIVITY_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd."+ AUTHORITY + ".schedule.activity";
    public static final String URI_TYPE_ACTIVITY_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd."+ AUTHORITY + ".activity";

    public static final class ScheduleColumns implements BaseColumns {

        private ScheduleColumns() {}

        public static final String NAME = "name";
        public static final String SELECTED = "selected";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";

        public static final String DEFAULT_SORT_ORDER = CREATED_AT + " ASC";
    }

    public static final class ActivityColumns implements BaseColumns {

        public static final String NAME = "name";
        public static final String PLACE = "place";
        public static final String DAY = "day";
        public static final String BLOCK_TAG = "blockTag";
        public static final String BEGINS_AT = "beginsAt";
        public static final String ENDS_AT = "endsAt";
        public static final String PERIOD = "period";
        public static final String CREATED_AT = "createdAt";
        public static final String UPDATED_AT = "updatedAt";
        public static final String SCHEDULE_ID = "scheduleId";

        public static final String DEFAULT_SORT_ORDER = BEGINS_AT + " ASC";
    }
}
