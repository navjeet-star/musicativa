package com.developer.musicatiiva.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.fragments.SuccesfullPasswordResetFragment;

public class RecoveryCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_code);

        CommonMethods.getInstance().replaceFragment(RecoveryCodeActivity.this,new SuccesfullPasswordResetFragment());



    }
}
