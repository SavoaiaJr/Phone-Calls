package com.sia4.phonecalls;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 1;
    final int PHONE_CALL_PERMISSION_REQUEST_CODE = 2;
    final int PHONE_CALL_AND_READ_CONTACTS_PERMISSION_REQUEST_CODE = 3;
    public final static String NAME_KEY = "name";
    public final static String PHONE_KEY = "phone";
    ArrayList<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (!PermissionsManager.getInstance().hasCallPhonePermissions(ContactActivity.this) && !PermissionsManager.getInstance().hasReadContactsPermissions(ContactActivity.this)) {
            Toast.makeText(this, "Permissions for Read Contacts & Phone Calls not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, PHONE_CALL_AND_READ_CONTACTS_PERMISSION_REQUEST_CODE);
            return;
        } else if (!PermissionsManager.getInstance().hasReadContactsPermissions(ContactActivity.this)) {
            Toast.makeText(this, "Permissions for Read Contacts not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
            return;
        } else if (!PermissionsManager.getInstance().hasCallPhonePermissions(ContactActivity.this)) {
            Toast.makeText(this, "Permissions for Phone Call not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION_REQUEST_CODE);
            return;
        }

        setupContactsListView();
        addDummyContacts();
    }

    public void addDummyContacts() {
        contacts.add(new ContactActivity.Contact("Andrei Smocot", "0734482441"));
        contacts.add(new ContactActivity.Contact("Constantin Vrabie", "0734482442"));
        contacts.add(new ContactActivity.Contact("Madalin Savoaia", "0734482443"));
    }


    private void setupContactsListView() {
        contacts = getContacts();
        ListView listView_contacts = (ListView) findViewById(R.id.listView_contacts);

        ContactAdapter contactAdapter = new ContactAdapter();
        listView_contacts.setAdapter(contactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_AND_READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Read Contacts & Phone Call granted.", Toast.LENGTH_SHORT).show();
                setupContactsListView();
            } else {
                finish();
            }
        } else if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Read Contacts granted.", Toast.LENGTH_SHORT).show();
                setupContactsListView();
            } else {
                finish();
            }
        } else if (requestCode == PHONE_CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for PHONE CALL granted.", Toast.LENGTH_SHORT).show();
                setupContactsListView();
            } else {
                finish();
            }
        }
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(contactName, phoneNumber));
        }
        return contacts;
    }

    public static final class Contact {
        Image avatar;
        String name;
        String phoneNumber;

        Contact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        Contact(Image avatar, String name, String phoneNumber) {
            this.avatar = avatar;
            this.name = name;
            this.phoneNumber = phoneNumber;
        }
    }

    private class ContactAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.contact_layout, parent, false);
            }

            ImageView imageView_avatar = (ImageView) convertView.findViewById(R.id.imageView_avatar);
            TextView textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            TextView textView_phone = (TextView) convertView.findViewById(R.id.textView_phone);

            final Contact currentContact = contacts.get(position);

            textView_name.setText(currentContact.name);
            textView_phone.setText(currentContact.phoneNumber);
//            if (currentContact.avatar != null) {
                imageView_avatar.setImageResource(R.mipmap.ic_launcher);
//            }

            convertView.findViewById(R.id.button_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentContact.phoneNumber != null) {
                        PhoneManager.getInstance().call(currentContact.phoneNumber, ContactActivity.this);
                    }
                }
            });

            convertView.findViewById(R.id.button_sendMessage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentContact.phoneNumber != null) {
                        Intent intent = new Intent(ContactActivity.this, SendMessageActivity.class);
                        intent.putExtra(NAME_KEY, currentContact.name);
                        intent.putExtra(PHONE_KEY, currentContact.phoneNumber);
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return contacts.size();
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
