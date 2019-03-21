package me.siketyan.silicagel.util;

import android.util.Log;

public class Logger {
    private static final String TAG = "SilicaGel";

    public static void debug(String str) {
        Log.d(TAG, str);
    }

    public static void info(String str) {
        Log.i(TAG, str);
    }

    public static void error(String str) {
        Log.e(TAG, str);
    }
}
