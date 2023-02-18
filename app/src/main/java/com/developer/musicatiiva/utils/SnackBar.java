package com.developer.musicatiiva.utils;

import android.content.Context;

import android.view.View;

import com.developer.musicatiiva.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBar {
    public static void show(View view,String text,Context context)
    {
        Snackbar.make(view,text, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).setActionTextColor(context.getResources().getColor(R.color.colorPrimary)).show();
    }

}
