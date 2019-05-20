package com.sia4.phonecalls;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class CallScheduleManager {

    final static int requestCode = 2;
    final static String ID_KEY = "id";
    final static String NAME_KEY = "name";
    final static String PHONE_KEY = "phone";


    public CallScheduleManager() {}

    public boolean addAlarm(ScheduleActivity.Schedule schedule, Context context) {
        Intent intent  = new Intent(context, CallScheduleReceiver.class);
        intent.putExtra(ID_KEY, schedule.id);
        intent.putExtra(NAME_KEY, schedule.name);
        intent.putExtra(PHONE_KEY, schedule.phone);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarSchedule = (Calendar) calendarNow.clone();


        calendarSchedule.set(Calendar.DAY_OF_MONTH, schedule.day);
        calendarSchedule.set(Calendar.MONTH, schedule.month);
        calendarSchedule.set(Calendar.YEAR, schedule.year);
        calendarSchedule.set(Calendar.HOUR_OF_DAY, schedule.hour);
        calendarSchedule.set(Calendar.MINUTE, schedule.minute);

        if (calendarSchedule.compareTo(calendarNow) <= 0) {
            return false;
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSchedule.getTimeInMillis(), pendingIntent);
        return true;
    }
}
