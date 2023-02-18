package com.developer.musicatiiva.recording.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

import com.developer.musicatiiva.utils.Metronome;

import java.io.File;

public class Utilities {

    public Utilities() {
    }

    public static void toast(String msg) {
        toast(Metronome.getContext(), msg);
    }

    public static void toast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static class DisplayToastInRunnable implements Runnable {
        String mText;

        public DisplayToastInRunnable(String text){
            mText = text;
        }

        public void run(){
            toast(mText);
        }
    }

    public static void log(String msg) {
        Logger.log("debug", msg);
    }

    public static File getDataFolder(String folderName)
    {
        ContextWrapper cw = new ContextWrapper(Metronome.getContext());
     //   Log.d("iiiiiiiiiii",cw.toString());
        File directory = cw.getDir(folderName, Context.MODE_PRIVATE);

        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }
}
