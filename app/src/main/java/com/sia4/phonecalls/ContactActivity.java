package com.sia4.phonecalls;

import android.Manifest;
import android.app.Activity;
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

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ArrayList<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (checkPermissions()) {
            setupContactsListView();
        }
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

    private void setupContactsListView() {
        contacts = getContacts();
        ListView listView_contacts = (ListView) findViewById(R.id.listView_contacts);

        ContactAdapter contactAdapter = new ContactAdapter();
        listView_contacts.setAdapter(contactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupContactsListView();
                } else {
                    checkPermissions();
                }
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
            if (currentContact.avatar != null) {
//                imageView_avatar.setImageResource(currentContact.avatar);
            }

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
                    Toast.makeText(getApplicationContext(),"This feature is not yet implemented",Toast.LENGTH_SHORT).show();
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
