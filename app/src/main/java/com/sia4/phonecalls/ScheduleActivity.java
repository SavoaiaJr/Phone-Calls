package com.sia4.phonecalls;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

interface DateAndTimeSetter {
    void setDate(int year, int month, int day);
    void setTime(int hour, int minute);
}

public class ScheduleActivity extends AppCompatActivity implements DateAndTimeSetter{

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ArrayList<ContactActivity.Contact> contacts = new ArrayList<>();
    Schedule currentSchedule;
    ArrayList<Schedule> schedules = new ArrayList<>();
    int str = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setupSchedulesListView();
        checkPermissions();

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

        findViewById(R.id.button_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contacts.size() == 0) {
                    addDummyContacts();
                }
                setupContactDialog();
            }
        });

        findViewById(R.id.button_schedule_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO DO - Implement schedule call
                schedules.add(currentSchedule);
                ((BaseAdapter)((ListView)findViewById(R.id.listView_schedules)).getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void setupSchedulesListView() {
        ListView listView_contacts = (ListView) findViewById(R.id.listView_schedules);

        SchedulesAdapter schedulesAdapter = new SchedulesAdapter();
        listView_contacts.setAdapter(schedulesAdapter);
    }

    private Boolean checkPermissions() {
        int permissionReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (permissionReadContacts != PackageManager.PERMISSION_GRANTED) {
            // if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this, "Contacts permission is required for this feature to work.", Toast.LENGTH_SHORT).show();
            String[] permissions = {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            // }
        } else {
            return true;
        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contacts = getContacts();
                } else {
                    checkPermissions();
                }
            }
        }
    }

    public ArrayList<ContactActivity.Contact> getContacts() {
        ArrayList<ContactActivity.Contact> contacts = new ArrayList<ContactActivity.Contact>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new ContactActivity.Contact(contactName, phoneNumber));
        }
        return contacts;
    }

    public void setupContactDialog() {
        final Dialog contact_dialog = new Dialog(ScheduleActivity.this);
        contact_dialog.setContentView(R.layout.dialog_contact_picker);
        final NumberPicker numberPicker = contact_dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(contacts.size() - 1);
        String[] contactsList = new String[contacts.size()];
        for (ContactActivity.Contact contact: contacts) {
            contactsList[contacts.indexOf(contact)] = contact.name;

        }
        numberPicker.setDisplayedValues(contactsList);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //value changed
                str = newVal;

            }
        });
        numberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScheduleActivity.this, "" + str, Toast.LENGTH_SHORT).show();
            }
        });

        contact_dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact_dialog.dismiss();
            }
        });

        contact_dialog.findViewById(R.id.button_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.textView_contact);
                String name = contacts.get(numberPicker.getValue()).name;
                String phone = contacts.get(numberPicker.getValue()).phoneNumber;
                textView.setText(name + " - " + phone);
                currentSchedule.name = name;
                currentSchedule.phone = phone;
                contact_dialog.dismiss();
            }
        });

        contact_dialog.show();
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

        String name;
        String phone;

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

    public void addDummyContacts() {
        contacts.add(new ContactActivity.Contact("Andrei Smocot", "0734482441"));
        contacts.add(new ContactActivity.Contact("Constantin Vrabie", "0734482442"));
        contacts.add(new ContactActivity.Contact("Madalin Savoaia", "0734482443"));
    }

    private class SchedulesAdapter extends BaseAdapter {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.schedules_layout, parent, false);
            }

            TextView textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            TextView textView_phone = (TextView) convertView.findViewById(R.id.textView_phone);

            final Schedule schedule = schedules.get(position);

            textView_name.setText(schedule.name);
            textView_phone.setText(schedule.phone);

            convertView.findViewById(R.id.button_remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    schedules.remove(position);
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return schedules.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
