package com.sia4.phonecalls;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class PhoneManager {
    private static final PhoneManager instance = new PhoneManager();

    private PhoneManager(){}
    public static PhoneManager getInstance() { return instance; }

    public void call(String number, Context context) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            if (/*number.matches("^[0-9]{10,13}$")*/true) {
                callIntent.setData(Uri.parse("tel:" + number));
                context.startActivity(callIntent);
            } else {
                Toast.makeText(context, "Numarul format nu este corect.", Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
            Log.e("PhoneCalls", "Call failed", e);
        }
    }
}
