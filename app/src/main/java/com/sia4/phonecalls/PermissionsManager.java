package com.sia4.phonecalls;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.security.Permission;

public class PermissionsManager {
    private static PermissionsManager permissionsManager = new PermissionsManager();
    private PermissionsManager() {}

    public static PermissionsManager getInstance() {
        return permissionsManager;
    }

    public boolean hasCallPhonePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED ? true : false;
    }

    public boolean hasReadContactsPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ? true : false;
    }

    public boolean hasSendSMSPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED ? true : false;
    }


    public void requestPermissions(Activity activity, String[] permissions, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity, permissions, permissionRequestCode);
    }

}
