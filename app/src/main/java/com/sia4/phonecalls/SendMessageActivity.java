package com.sia4.phonecalls;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SendMessageActivity extends AppCompatActivity {
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if (!PermissionsManager.getInstance().hasSendSMSPermissions(SendMessageActivity.this)) {
            Toast.makeText(this, "Permissions for Send SMS not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        final String name = getIntent().getStringExtra(ContactActivity.NAME_KEY);
        final String phone = getIntent().getStringExtra(ContactActivity.PHONE_KEY);

        ((TextView) findViewById(R.id.textView_name_value)).setText(name);
        ((TextView) findViewById(R.id.textView_phone_value)).setText(phone);

        findViewById(R.id.button_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText) findViewById(R.id.editText_message)).getText().toString();
                sendSMS(phone, message);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Send SMS granted.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    public void sendSMS(String phone, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show();
            ((EditText) findViewById(R.id.editText_message)).setText("");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
