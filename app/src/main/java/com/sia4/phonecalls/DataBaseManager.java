package com.sia4.phonecalls;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    ScheduleDbHelper scheduleDbHelper;

    public DataBaseManager(Context context) {
        scheduleDbHelper = new ScheduleDbHelper(context);
    }

    public boolean addSchedule(ScheduleActivity.Schedule schedule) {
        // Gets the data repository in write mode
        SQLiteDatabase db = scheduleDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScheduleContract.ScheduleEntity.COLUMN_DAY, schedule.day);
        values.put(ScheduleContract.ScheduleEntity.COLUMN_MONTH, schedule.month);
        values.put(ScheduleContract.ScheduleEntity.COLUMN_YEAR, schedule.year);

        values.put(ScheduleContract.ScheduleEntity.COLUMN_HOUR, schedule.hour);
        values.put(ScheduleContract.ScheduleEntity.COLUMN_MINUTE, schedule.minute);

        values.put(ScheduleContract.ScheduleEntity.COLUMN_NAME, schedule.name);
        values.put(ScheduleContract.ScheduleEntity.COLUMN_PHONE, schedule.phone);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ScheduleContract.ScheduleEntity.TABLE_NAME, null, values);

        return newRowId == -1 ? false : true;
    }

    public boolean removeSchedule(ScheduleActivity.Schedule schedule) {
        SQLiteDatabase db = scheduleDbHelper.getWritableDatabase();

        String selection = ScheduleContract.ScheduleEntity._ID + " = ?";
        String[] selectionArgs = { "" + schedule.id };

        int numberOfAffectedRows = db.delete(ScheduleContract.ScheduleEntity.TABLE_NAME, selection, selectionArgs);

        return numberOfAffectedRows == 0 ? false : true;
    }

    public List<ScheduleActivity.Schedule> getSchedules() {
        SQLiteDatabase db = scheduleDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                ScheduleContract.ScheduleEntity.COLUMN_DAY,
                ScheduleContract.ScheduleEntity.COLUMN_MONTH,
                ScheduleContract.ScheduleEntity.COLUMN_YEAR,
                ScheduleContract.ScheduleEntity.COLUMN_HOUR,
                ScheduleContract.ScheduleEntity.COLUMN_MINUTE,
                ScheduleContract.ScheduleEntity.COLUMN_NAME,
                ScheduleContract.ScheduleEntity.COLUMN_PHONE
        };


        String sortOrder =
                BaseColumns._ID + " ASC";

        Cursor cursor = db.query(
                ScheduleContract.ScheduleEntity.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List schedules = new ArrayList<ScheduleActivity.Schedule>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity._ID));
            int day = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_DAY));
            int month = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_MONTH));
            int year = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_YEAR));

            int hour = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_HOUR));
            int minute = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_MINUTE));

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_NAME));
            String phone = cursor.getString(
                    cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntity.COLUMN_PHONE));

            schedules.add(new ScheduleActivity.Schedule(itemId, day, month, year, hour, minute, name, phone));
        }
        cursor.close();

        return schedules;
    }
}