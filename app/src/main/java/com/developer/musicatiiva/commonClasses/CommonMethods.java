package com.developer.musicatiiva.commonClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.utils.MySharedPreferences;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by brst-pc80 on 3/14/19.
 */

public class CommonMethods {

    String TAG = CommonMethods.class.getSimpleName();

    static CommonMethods instance = new CommonMethods();

    public static CommonMethods getInstance()
    {
        return instance;
    }


    public interface Consumer
    {
        void accept(Boolean internet);
    }

   static public Boolean isInternet(Context context){
       ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo nInfo = cm.getActiveNetworkInfo();
       boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
       return connected;
   }
    @SuppressLint("StaticFieldLeak")
    public static class InternetCheck extends AsyncTask<Void, Void, Boolean>
    {

        private Consumer mConsumer;
        Context context;




        public InternetCheck(Context context, Consumer consumer)
        {
            this.context = context;
            mConsumer = consumer;
            execute();
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            try
            {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean internet)
        {
            mConsumer.accept(internet);

            if (!internet)
                CommonDialogs.getInstance().showMessageDialog(context, "The internet connection appears to be offline.");
        }
    }
    public String getCurrentDate() {

        long timestamp = System.currentTimeMillis() / 1000;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.HOUR, 1);
        TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(timestamp * 1000));
        Log.d(TAG, "getCurrentDateAndTime: "+localTime);



        return localTime;
    }
    public String getCurrentTime()
    {
        long timestamp = System.currentTimeMillis() / 1000;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.HOUR, 1);
        TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(timestamp * 1000));
        Log.d(TAG, "setDate: " + localTime);
        String time = localTime.split(" ")[1];
        String amPm=localTime.split(" ")[2];
        Log.d(TAG, "getCurrentTime: "+time+ amPm);
        String currentTime = time.split(":")[0] + ":" + time.split(":")[1]+" "+amPm;
        return  currentTime;
    }
    public String getCurrentTimeIn24HoursFormat()
    {
        long timestamp = System.currentTimeMillis() / 1000;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.HOUR, 1);
        TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(timestamp * 1000));
        Log.d(TAG, "setDate: " + localTime);
        String time = localTime.split(" ")[1];


        String currentTime = time.split(":")[0] + ":" + time.split(":")[1];
        return  currentTime;
    }
    public void saveDialogSeenState(Context context,int user_id)
    {
        String key=getCurrentDate()+"-"+user_id;
        MySharedPreferences.getInstance().saveDialogSeenState(context,key,true);

    }
    public boolean getDialogSeenState(Context context,int user_id)
    {
        String key=getCurrentDate()+"-"+user_id;

        return MySharedPreferences.getInstance().getDialogSeenState(context,key);

    }



    public void replaceFragment(AppCompatActivity activity, Fragment fragment)
    {

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void replaceFragment(FragmentActivity activity, Fragment fragment)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    /*public void replaceFragment(AppCompatActivity activity, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        replaceFragment(activity,fragment);
    }
    public void replaceFragment(FragmentActivity activity, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        replaceFragment(activity,fragment);
    }*/
}
