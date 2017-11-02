package com.tealcode.boxingspeed.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by YuBo on 2017/9/20.
 */

public class LocalStorage {
    private static final String TAG = "LocalStorage";

    private static LocalStorage instance = null;

    public static boolean init(Context context){
        instance = new LocalStorage(context);
        return true;
    }

    public static LocalStorage getInstance()
    {
        if(instance == null) {
            Log.e(TAG, "LocalStorage should be inited before use");
        }
        return instance;
    }

    private SharedPreferences sharedPref = null;
    private SharedPreferences.Editor sharedPrefWriter = null;

    private LocalStorage(Context context) {
        sharedPref = context.getSharedPreferences("local_storage", context.MODE_PRIVATE);
        sharedPrefWriter = sharedPref.edit();
    }


    // public methods
    public void setStringValue(String key, String value)
    {
        if(sharedPrefWriter == null) {
            Log.e(TAG, "SharedPreference Editor is NULL");
            return;
        }

        sharedPrefWriter.putString(key, value).commit();
    }

    public String getStringValue(String key, String defaultValue)
    {
        if(sharedPref == null) {
            Log.e(TAG, "SharedPreference is NULL");
            return null;
        }

        if(sharedPref.contains(key)) {
            return sharedPref.getString(key, defaultValue);
        }

        Log.d(TAG, "No Key Found from sharedPreference: " + key);
        return defaultValue;
    }
}
