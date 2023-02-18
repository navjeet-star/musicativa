package com.developer.musicatiiva.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {


        String TAG = MySharedPreferences.class.getSimpleName();

        private final String PreferenceName = "MyPreference";

        public MySharedPreferences()
        {
        }

        private static MySharedPreferences instance = null;

        public static MySharedPreferences getInstance()
        {
            if (instance == null)
            {
                instance = new MySharedPreferences();
            }
            return instance;
        }

        public SharedPreferences getPreference(Context context)
        {
            return context.getSharedPreferences(PreferenceName, Activity.MODE_PRIVATE);
        }


    public String saveUserId(Context context, String key, int data) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putInt(key, data);

        editor.apply();
        return key;
    }
    public String saveDialogSeenState(Context context, String key, boolean seen) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putBoolean(key, seen);

        editor.apply();
        return key;
    }
    public boolean getDialogSeenState(Context context, String key) {
        SharedPreferences prefs = getPreference(context);
        return prefs.getBoolean(key, false);
    }
    public int getUserID(Context context, String key) {
        SharedPreferences prefs = getPreference(context);
        return prefs.getInt(key, -1);
    }
    public int getNumberOfPracticeTime(Context context, String key) {
        SharedPreferences prefs = getPreference(context);
        return prefs.getInt(key, -1);
    }
    public String setNumberOfPracticeTime(Context context, String key, int data) {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putInt(key, data);

        editor.apply();
        return key;
    }
    public void clearUserId(Context context)
    {

        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.remove(Constants.USERID);
        editor.apply();
        editor.commit();

    }


    public void setLoggedIn(Context context, boolean isloggedIn)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putBoolean("IS_LOGGED_IN", isloggedIn);
        editor.apply();
    }


    public boolean getLoggedIn(Context context)
    {
        return getPreference(context).getBoolean("IS_LOGGED_IN", false);
    }


      public void setTodaysDate(Context context, String date)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString("TODAYS_DATE", date);
        editor.apply();
    }


    public String getTodaysDate(Context context)
    {
        return getPreference(context).getString("TODAYS_DATE", "");
    }



}
