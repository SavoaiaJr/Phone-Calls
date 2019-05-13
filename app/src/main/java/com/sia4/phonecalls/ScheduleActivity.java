package com.sia4.phonecalls;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

interface DateAndTimeSetter {
    void setDate(int year, int month, int day);
    void setTime(int hour, int minute);
}

public class ScheduleActivity extends AppCompatActivity implements DateAndTimeSetter{

    Schedule currentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        findViewById(R.id.button_day_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        findViewById(R.id.button_time_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void setDate(int year, int month, int day) {
        if (currentSchedule == null) {
            currentSchedule = new Schedule();
        }

        currentSchedule.year = year;
        currentSchedule.month = month;
        currentSchedule.day = day;

        ((TextView) findViewById(R.id.textView_day)).setText(currentSchedule.getDate());
    }

    @Override
    public void setTime(int hour, int minute) {
        if (currentSchedule == null) {
            currentSchedule = new Schedule();
        }

        currentSchedule.hour = hour;
        currentSchedule.minute = minute;

        ((TextView) findViewById(R.id.textView_time)).setText(currentSchedule.getTime());
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Activity currentActivity = getActivity();
            if (currentActivity != null && currentActivity instanceof DateAndTimeSetter) {
                ((DateAndTimeSetter) currentActivity).setDate(year, month, dayOfMonth);
            }
        }
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Activity currentActivity = getActivity();
            if (currentActivity != null && currentActivity instanceof DateAndTimeSetter) {
                ((DateAndTimeSetter) currentActivity).setTime(hourOfDay, minute);
            }
        }
    }

    public final class Schedule {
        int day;
        int month;
        int year;

        int hour;
        int minute;

        public Schedule() {}

        public Schedule(int day, int month, int year, int hour, int minute) {
            this.day = day;
            this.month = month;
            this.year = year;

            this.hour = hour;
            this.minute = minute;
        }

        public String getDate() {
            return day + "/" + month + "/" + year;
        }

        public String getTime() {
            return  hour + ":" + minute;
        }
    }
}
