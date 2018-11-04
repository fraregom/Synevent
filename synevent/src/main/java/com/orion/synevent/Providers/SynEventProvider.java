package com.orion.synevent.Providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.orion.synevent.Persistance.DatabaseContract;
import com.orion.synevent.Persistance.SyneventDbHelper;

public class SynEventProvider extends ContentProvider {
    public static final int SCHEDULE = 1;
    public static final int SCHEDULE_ITEM = 2;
    public static final int ACTIVITY = 3;
    public static final int ACTIVITY_ITEM = 4;

    private static final UriMatcher sUriMatcher;

    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SynEventContract.AUTHORITY, "schedule", SCHEDULE);
        sUriMatcher.addURI(SynEventContract.AUTHORITY, "schedule/#", SCHEDULE_ITEM);
        sUriMatcher.addURI(SynEventContract.AUTHORITY, "schedule/activity/#", ACTIVITY);
        sUriMatcher.addURI(SynEventContract.AUTHORITY, "activity/#", ACTIVITY_ITEM);
    }

    private SyneventDbHelper mDbHelper;

    public SynEventProvider() {}

    @Override
    public boolean onCreate() {
        mDbHelper = SyneventDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case SCHEDULE:
                return SynEventContract.URI_TYPE_SCHEDULE_DIR;
            case SCHEDULE_ITEM:
                return SynEventContract.URI_TYPE_SCHEDULE_TYPE;
            case ACTIVITY:
                return SynEventContract.URI_TYPE_ACTIVITY_DIR;
            case ACTIVITY_ITEM:
                return SynEventContract.URI_TYPE_ACTIVITY_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String table;
        switch (sUriMatcher.match(uri)){
            case SCHEDULE:
                if(sortOrder == null || TextUtils.isEmpty(sortOrder))
                    sortOrder = SynEventContract.ScheduleColumns.DEFAULT_SORT_ORDER;
                table = DatabaseContract.Schedule.TABLE_NAME;
                break;
            case SCHEDULE_ITEM:
                selection = (selection==null) ?
                        "_ID = " + uri.getLastPathSegment()
                        : selection + "AND _ID = " + uri.getLastPathSegment();
                table = DatabaseContract.Schedule.TABLE_NAME;
                break;
            case ACTIVITY:
                if(sortOrder == null || TextUtils.isEmpty(sortOrder))
                    sortOrder = SynEventContract.ActivityColumns.DEFAULT_SORT_ORDER;
                selection = (selection==null) ?
                        "scheduleId = " + uri.getLastPathSegment()
                        : selection + " AND scheduleId = " + uri.getLastPathSegment();
                table = DatabaseContract.Activity.TABLE_NAME;
                break;
            case ACTIVITY_ITEM:
                selection = (selection == null) ?
                        "_ID = " + uri.getLastPathSegment()
                        :selection + "AND _ID = " + uri.getLastPathSegment();
                table = DatabaseContract.Activity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table;
        switch (sUriMatcher.match(uri)){
            case SCHEDULE:
                table = DatabaseContract.Schedule.TABLE_NAME;
                break;
            case ACTIVITY:
                table = DatabaseContract.Activity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        long id = db.insert(table, null, values);
        if(id > -1){
            Uri insertedId = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(insertedId, null);
            return insertedId;
        }
        return null;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selctionArgs){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table;
        selection = (selection == null) ?
                "_ID = " + uri.getLastPathSegment()
                : selection + "AND _ID = " + uri.getLastPathSegment();
        switch (sUriMatcher.match(uri)){
            case SCHEDULE_ITEM:
                table = DatabaseContract.Schedule.TABLE_NAME;
                break;
            case ACTIVITY_ITEM:
                table = DatabaseContract.Activity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int updateCount = db.update(table, values, selection, selctionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        selection = (selection == null) ?
                "_ID = " + uri.getLastPathSegment()
                : selection + "AND _ID = " + uri.getLastPathSegment();
        String table;
        switch (sUriMatcher.match(uri)){
            case SCHEDULE_ITEM:
                table = DatabaseContract.Schedule.TABLE_NAME;
                break;
            case ACTIVITY_ITEM:
                table = DatabaseContract.Activity.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int deleteCount = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }
}
