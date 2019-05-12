package com.sia4.phonecalls;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    private void setup() {
        findViewById(R.id.button_phoneCalls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneCallsIntent = new Intent(MainActivity.this, PhoneActivity.class);
                startActivity(phoneCallsIntent);
            }
        });

        findViewById(R.id.button_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactsIntent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactsIntent);
            }
        });
        
        findViewById(R.id.button_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This feature is not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
