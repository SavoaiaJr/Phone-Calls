package com.sia4.phonecalls;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

public class CallScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(CallScheduleManager.ID_KEY, 0);

        DataBaseManager dbManager = new DataBaseManager(context);
        ScheduleActivity.Schedule schedule = dbManager.getSchedules().get(id);
        dbManager.removeSchedule(schedule);

        String name = intent.getStringExtra(CallScheduleManager.NAME_KEY);
        String phone = intent.getStringExtra(CallScheduleManager.PHONE_KEY);

        Toast.makeText(context, "Calling " + name, Toast.LENGTH_SHORT).show();
        PhoneManager.getInstance().call(phone, context);
    }
}