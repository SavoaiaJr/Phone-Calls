package com.sia4.phonecalls;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneActivity extends AppCompatActivity {

    private static int PHONE_CALL_PERMISSION_REQUEST_CODE = 1;
    EditText numberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        if(!PermissionsManager.getInstance().hasCallPhonePermissions(PhoneActivity.this)) {
            Toast.makeText(this, "Permissions for Call Phone not granted.", Toast.LENGTH_SHORT).show();
            PermissionsManager.getInstance().requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION_REQUEST_CODE);
            findViewById(R.id.callButton).setVisibility(View.INVISIBLE);
        }

        setup();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions for Call Phone granted.", Toast.LENGTH_SHORT).show();
                findViewById(R.id.callButton).setVisibility(View.VISIBLE);
            }
        }
    }

    private void setup() {
        findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        numberEditText = findViewById(R.id.numberEditText);

        findViewById(R.id.oneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "1");
            }
        });

        findViewById(R.id.twoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "2");
            }
        });

        findViewById(R.id.threeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "3");
            }
        });

        findViewById(R.id.fourButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "4");
            }
        });

        findViewById(R.id.fiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "5");
            }
        });

        findViewById(R.id.sixButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "5");
            }
        });

        findViewById(R.id.sevenButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "7");
            }
        });

        findViewById(R.id.eightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "8");
            }
        });

        findViewById(R.id.nineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "9");
            }
        });

        findViewById(R.id.zeroButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberEditText.setText(numberEditText.getText().toString() + "0");
            }
        });

        findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberEditText.getText().toString().length() > 0) {
                    String newNumber = numberEditText.getText().toString().substring(0, numberEditText.getText().toString().length() - 1);
                    numberEditText.setText(newNumber);
                }
            }
        });
    }


    private void call() {
        String telNumber = numberEditText.getText().toString();
        if (telNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneManager.getInstance().call(telNumber, this);
    }
}
