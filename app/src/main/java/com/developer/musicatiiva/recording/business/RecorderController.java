package com.developer.musicatiiva.recording.business;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;


import androidx.annotation.RequiresApi;

import com.developer.musicatiiva.recording.util.Constantsss;
import com.developer.musicatiiva.recording.util.SharedPreferencesHandler;
import com.developer.musicatiiva.recording.util.Utilities;
import com.developer.musicatiiva.utils.Metronome;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecorderController {
    private Handler mHandler;
    public int audioQuality;
    public String audioExtension;
    private MediaRecorder recorder;
    private File path;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    public RecorderController(){
        initPreferences();
        initRecorder();
        mHandler = new Handler();
    }

    private void initPreferences(){
        audioExtension = SharedPreferencesHandler.getStringValue(Constantsss.Preferences.RECORDER_AUDIO_EXTENSION);
        if (audioExtension == null)
            audioExtension = Constantsss.EXT_MP3;
        String audioQualityString = SharedPreferencesHandler.getStringValue(Constantsss.Preferences.RECORDER_AUDIO_QUALITY);
        if (audioExtension == null)
            audioQualityString = Constantsss.QUALITY_8;

        if (audioQualityString != null && !audioQualityString.isEmpty())
            audioQuality = Integer.parseInt(audioQualityString);
    }

    public void initRecorder(){
        try {
            mDPM = (DevicePolicyManager) Metronome.getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(Metronome.getContext(), MyDeviceAdminReceiver.class);
            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");

                Metronome.getContext().startActivity(intent);
            } else {

            }
            recorder = new MediaRecorder();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void startRecording(){
        recorder = new MediaRecorder();
        String out=new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
        String file_name = "Record";
        try {
            path= File.createTempFile(file_name + out, "." + audioExtension, Utilities.getDataFolder(Constantsss.APP_FOLDER_NAME));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        resetRecorder();
        try {
            recorder.start();
            Utilities.log("Starting Record");
            mHandler.post(new Utilities.DisplayToastInRunnable("Starting Record"));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording(){
        try {
            recorder.pause();
            Utilities.log("Pause Audio");
            mHandler.post(new Utilities.DisplayToastInRunnable("Pause Audio"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void stopRecording(){
        try {
            recorder.stop();
            onDestroy();
            Utilities.log("Save Audio");
            mHandler.post(new Utilities.DisplayToastInRunnable("Save Audio"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void onDestroy(){
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    private void resetRecorder(){
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (audioExtension.equals(Constantsss.EXT_MP3)) {
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        }
        else
        {
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
       // recorder.setAudioEncodingBitRate(audioQuality);
        recorder.setOutputFile(path.getAbsolutePath());
        try{
            recorder.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
