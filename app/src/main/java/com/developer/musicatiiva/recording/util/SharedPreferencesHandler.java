package com.developer.musicatiiva.recording.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.developer.musicatiiva.utils.Metronome;


public class SharedPreferencesHandler {
    public static void saveStringValue(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Metronome.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringValue(String key){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Metronome.getContext());
        String value = sharedPreferences.getString(key, null);
        return value;
    }
}
