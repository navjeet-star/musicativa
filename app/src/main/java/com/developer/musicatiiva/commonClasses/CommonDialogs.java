package com.developer.musicatiiva.commonClasses;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.developer.musicatiiva.R;

import java.util.Objects;

/**
 * Created by brst-pc80 on 3/14/19.
 */

public class CommonDialogs {

    String TAG = CommonDialogs.class.getSimpleName();

    OnClick onClik;
    static CommonDialogs instance = new CommonDialogs();

    public static CommonDialogs getInstance() {
        return instance;
    }

    private Dialog dialog;
    private ProgressDialog progressDialog;
    private TextView textViewTitle;

    public void showMessageDialog(Context context, String message) {
        dismissDialog();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(dialog != null)
        {
            dialog.show();
            textViewTitle = dialog.findViewById(R.id.textViewTitle);
        }

        textViewTitle.setText(Html.fromHtml(message));

       // dialog.findViewById(R.id.textViewOk).setOnClickListener(view -> dialog.dismiss());

        dialog.findViewById(R.id.textViewOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              dialog.dismiss();

            }
        });
    }

    public void dismissDialog() {

        if (dialog != null && dialog.isShowing()) {

            dialog.dismiss();

        }
    }

    public void showMessage2ButtonsDialog(Context context, String message, View.OnClickListener onFirstClick, View.OnClickListener onSecondClick) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_message_two_buttons);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView textViewMessage = dialog.findViewById(R.id.textViewMessage);

        textViewMessage.setText(Html.fromHtml(message));
        TextView textViewFirst = dialog.findViewById(R.id.textViewFirst);
        TextView textViewSecond = dialog.findViewById(R.id.textViewSecond);

        //textViewMessage.setText("do you really want to exit");
        textViewFirst.setText("No");
        textViewSecond.setText("Yes");

        textViewFirst.setOnClickListener(onFirstClick);
        textViewSecond.setOnClickListener(onSecondClick);

    }

    public void showProgressDialog(Context context) {

        dismissProgressDialog();

        progressDialog = ProgressDialog.show(context, "", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

          Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        }

        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);
        CircleProgressBar circleProgressBar = progressDialog.findViewById(R.id.progressWheel);
        circleProgressBar.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

    }

    public void dismissProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();

        }
    }

    public void showMessageYesNo(Context context, String message,OnClick onClik) {
        dismissDialog();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_yes_no);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Objects.requireNonNull(dialog.getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(dialog != null)
        {
            dialog.show();
            textViewTitle = dialog.findViewById(R.id.textViewTitle);
        }

        textViewTitle.setText(Html.fromHtml(message));

        // dialog.findViewById(R.id.textViewOk).setOnClickListener(view -> dialog.dismiss());

        dialog.findViewById(R.id.textViewYES).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClik.onClick(0,"yes");
                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.textViewNO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClik.onClick(0,"no");
                dialog.dismiss();

            }
        });
    }

    public interface OnClick{
        void onClick(int pos,String data);
    }
}

