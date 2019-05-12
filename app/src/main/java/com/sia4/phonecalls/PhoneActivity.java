package com.sia4.phonecalls;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PhoneActivity extends AppCompatActivity {

    EditText numberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        setup();
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
        PhoneManager.getInstance().call(telNumber, this);
    }
}
