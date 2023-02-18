package com.developer.musicatiiva.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.developer.musicatiiva.R;

import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class Metronome extends Application {

    private static final int VERSION_BILLING_API = 3;
    public static final int REQUEST_PURCHASE = 614;

     private ServiceConnection serviceConnection;

    private boolean isPremium;
    private boolean isNetworkError = true;
    private String price;
    public static Metronome instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static synchronized Metronome getInstance() {
        return instance;
    }

    public static Context getContext(){
        return Metronome.getInstance().getApplicationContext();
    }


    public String getPrice() {
        return price != null ? price : getString(R.string.title_no_connection);
    }

    public boolean isPremium() {

        return isPremium || isNetworkError;
    }

    public void onPremium(final Activity activity) {
        if (!isPremium()) {
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_premium, null);
            Glide.with(this).load("https://theandroidmaster.github.io/images/headers/metronomePremium.png").into((ImageView) view.findViewById(R.id.image));

            new MaterialDialog.Builder(activity)
                    .customView(view, false)
                    .backgroundColor(Color.WHITE)
                    .cancelable(false)
                    .positiveText(getString(R.string.title_get_premium, getPrice()))
                    .positiveColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            buyPremium(activity);
                            dialog.dismiss();
                        }
                    })
                    .negativeText(R.string.title_use_anyway)
                    .negativeColor(ContextCompat.getColor(this, R.color.textColorSecondaryInverse))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public void buyPremium(Activity activity) {

    }

    public void onPremiumBought(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data.hasExtra("INAPP_PURCHASE_DATA")) {
            try {
                JSONObject object = new JSONObject(data.getStringExtra("INAPP_PURCHASE_DATA"));
                if ("sku".equals(object.getString("productId")))
                    isPremium = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
