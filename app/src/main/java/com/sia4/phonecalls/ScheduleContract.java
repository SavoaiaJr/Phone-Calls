package com.sia4.phonecalls;

import android.provider.BaseColumns;

public final class ScheduleContract {
    private ScheduleContract() {}

    public static class ScheduleEntity implements BaseColumns {
        public static final String TABLE_NAME = "schedule";

        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_YEAR = "year";

        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";

        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_NAME = "name";
    }
}