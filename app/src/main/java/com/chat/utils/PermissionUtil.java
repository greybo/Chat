package com.chat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final int PERMISSION_REQUEST_CODE_AUDIO_ACTIVITY = 1101;

    public static boolean checkPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionUtil.isPermissionGranted(activity, permission)) {
                // Permission Granted Already
                return true;
            }
            // Request Permission
            PermissionUtil.requestPermissionActivity(activity, permission);
        } else {
            return true;
        }
        return false;
    }

    public static void requestPermissionActivity(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE_AUDIO_ACTIVITY);
    }

    public static boolean isPermissionGranted(Activity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkShouldShowRequestPermission(Activity activity, String permission) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
//        }
        return false;
    }
}
