package com.developer.musicatiiva.activities;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.databinding.ActivityMainBinding;
import com.developer.musicatiiva.databinding.ActivityRecordingBinding;
import com.developer.musicatiiva.recording.business.RecorderController;
import com.developer.musicatiiva.stopwatch.StopWatchUtil;
import com.developer.musicatiiva.stopwatch.TimerValue;
import com.developer.musicatiiva.utils.RipplePulseLayout;

public class RecordingActivity extends AppCompatActivity {

    ActivityRecordingBinding binding;
    StopWatchUtil stopWatchUtil;
    TimerValue timerValue;
    private RecorderController recorderController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_recording);
        timerValue = new TimerValue();
        binding.setData1(timerValue);
        binding.setData(this);
        stopWatchUtil = new StopWatchUtil(timerValue);

        recorderController = new RecorderController();
        RequestPermission();


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onPlayClicked(){
       // binding.playIV.tint

        if(checkPermissions()==true) {
            Log.d("callinnggggg","start");
            stopWatchUtil.start();
            recorderController.startRecording();
            binding.mRipplePulseLayout.startRippleAnimation();


            binding.playIV.setColorFilter(getResources().getColor(R.color.colorPrimary));
            binding.pauseIV.setColorFilter(getResources().getColor(R.color.colorBlack));
            binding.stopIV.setColorFilter(getResources().getColor(R.color.colorBlack));

        }else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onPauseClicked(){
        Log.d("callinnggggg","pause");
        stopWatchUtil.pause();
        recorderController.pauseRecording();
        binding.mRipplePulseLayout.stopRippleAnimation();


        binding.playIV.setColorFilter(getResources().getColor(R.color.colorBlack));
        binding.pauseIV.setColorFilter(getResources().getColor(R.color.colorPrimary));
        binding.stopIV.setColorFilter(getResources().getColor(R.color.colorBlack));

    }

    public void onStopClicked(){

        Log.d("callinnggggg","stop");
        stopWatchUtil.pause();
        stopWatchUtil.reset();
        recorderController.stopRecording();
        binding.mRipplePulseLayout.stopRippleAnimation();

        binding.playIV.setColorFilter(getResources().getColor(R.color.colorBlack));
        binding.pauseIV.setColorFilter(getResources().getColor(R.color.colorBlack));
        binding.stopIV.setColorFilter(getResources().getColor(R.color.colorPrimary));


    }

    private void RequestPermission(){
        ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case 1:
                if(grantResults.length > 0){
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    Log.d("ttttttttttt",permissionToStore+""+"     "+permissionToRecord+"");
                    if(permissionToRecord && permissionToStore){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }else{
                        RequestPermission();
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermissions(){

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}