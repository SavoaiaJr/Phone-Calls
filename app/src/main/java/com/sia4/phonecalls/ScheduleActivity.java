package com.sia4.phonecalls;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    final int PHONE_CALL_PERMISSION_REQUEST_CODE = 1;
    final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 2;
    final int PHONE_CALL_AND_READ_CONTACTS_PERMISSIONS_REQUEST_CODE = 3;
    ArrayList<ContactActivity.Contact> contacts = new ArrayList<>();
    Schedule currentSchedule = new Schedule();
    ArrayList<Schedule> schedules = new ArrayList<>();
    int str = 0;

    DataBaseManager dbManager = new DataBaseManager(ScheduleActivity.this);
    CallScheduleManager callScheduleManager = new CallScheduleManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setupSchedulesListView();

        if (!PermissionsManager.getInstance().hasCallPhonePermissions(ScheduleActivity.this) && !PermissionsManager.getInstance().hasReadContactsPermissions(ScheduleActivity.this)) {
            Toast.makeText(this, "Permissions for Call Phone and Read Contacts not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS}, PHONE_CALL_AND_READ_CONTACTS_PERMISSIONS_REQUEST_CODE);
        } else if(!PermissionsManager.getInstance().hasCallPhonePermissions(ScheduleActivity.this)) {
            Toast.makeText(this, "Permissions for Call Phone not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION_REQUEST_CODE);
        } else if (!PermissionsManager.getInstance().hasReadContactsPermissions(ScheduleActivity.this)) {
            Toast.makeText(this, "Permissions for Read Contacts not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        }

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

                if (isCurrentScheduleComplete() == false) {
                    Toast.makeText(ScheduleActivity.this, "Fill all details.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (callScheduleManager.addAlarm(currentSchedule, ScheduleActivity.this) == false) {
                    Toast.makeText(ScheduleActivity.this, "Date and Time should be in future.", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbManager.addSchedule(currentSchedule);
                ((BaseAdapter)((ListView)findViewById(R.id.listView_schedules)).getAdapter()).notifyDataSetChanged();

                cleanCurrentSchedule();
            }
        });
    }

    private void cleanCurrentSchedule() {
        currentSchedule = new Schedule();
        ((TextView) findViewById(R.id.textView_day)).setText("Not selected");
        ((TextView) findViewById(R.id.textView_time)).setText("Not selected");
        ((TextView) findViewById(R.id.textView_contact)).setText("Not selected");
    }

    private boolean isCurrentScheduleComplete() {
        if (currentSchedule.day != -1 && currentSchedule.month != -1 && currentSchedule.year != -1 &&
                currentSchedule.hour != -1 && currentSchedule.minute != -1 && currentSchedule.phone != null && currentSchedule.name != null) {
            return true;
        }
        return false;
    }


    private void setupSchedulesListView() {
        ListView listView_contacts = (ListView) findViewById(R.id.listView_schedules);

        SchedulesAdapter schedulesAdapter = new SchedulesAdapter();
        listView_contacts.setAdapter(schedulesAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PHONE_CALL_AND_READ_CONTACTS_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Call Phone and Read Contacts granted.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        } else if (requestCode == PHONE_CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Call Phone granted.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.callButton).setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        } else if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Read Contacts granted.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
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
        currentSchedule.year = year;
        currentSchedule.month = month;
        currentSchedule.day = day;

        ((TextView) findViewById(R.id.textView_day)).setText(currentSchedule.getDate());
    }

    @Override
    public void setTime(int hour, int minute) {
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

    public static final class Schedule {
        long id = -1;
        int day = -1;
        int month = -1;
        int year = -1;

        int hour = -1;
        int minute = -1;

        String name;
        String phone;

        public Schedule() {}

        public Schedule(long id, int day, int month, int year, int hour, int minute, String name, String phone) {
            this.id = id;
            this.day = day;
            this.month = month;
            this.year = year;

            this.hour = hour;
            this.minute = minute;

            this.name = name;
            this.phone = phone;
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

            final Schedule schedule = dbManager.getSchedules().get(position);

            textView_name.setText(schedule.name);
            textView_phone.setText(schedule.phone);

            convertView.findViewById(R.id.button_remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbManager.removeSchedule(schedule);
                    ((BaseAdapter)((ListView)findViewById(R.id.listView_schedules)).getAdapter()).notifyDataSetChanged();
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return dbManager.getSchedules().size();
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
