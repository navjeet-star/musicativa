package com.developer.musicatiiva.recording.util;

import android.util.Log;

public class Logger {
    public static boolean DEBUG = true;

    public static void log(String tag, String message) {
        if (DEBUG)
            Log.i(tag, message);
    }
}
