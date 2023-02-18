package com.developer.musicatiiva.utils;

import android.content.Intent;
import android.media.SoundPool;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.developer.musicatiiva.activities.MetronomeTestActivity;

public class MyTask implements Runnable{

    private SoundPool mSoundPool;
    private MetronomeTestActivity.MetronomeTask sender;
    private int beatSound1Id = -1;
    private int beatSound2Id = -1;
    private int repeatEvery = 4;
    private int internalCount = 0;

    @Override
    public void run() {
        playSound();
    }

    public void setParameters(SoundPool pool, int id1, int id2, int repeatEveryX, MetronomeTestActivity.MetronomeTask sender) {
        mSoundPool = pool;
        beatSound1Id = id1;
        beatSound2Id = id2;
        repeatEvery = repeatEveryX;
        this.sender = sender;
    }

    public void playSound() {
        Intent intent=new Intent(Constants.ACTION_METRONOME);
        intent.putExtra("type","internalCount");
        intent.putExtra("internalCount",internalCount);
        LocalBroadcastManager.getInstance(sender)
                .sendBroadcast(intent);
       // flashButton(internalCount);

        if (mSoundPool != null && beatSound1Id != -1) {
            if (internalCount == 0) {
                mSoundPool.play(beatSound2Id, 0.5f, 0.5f, 0, 0, 0f);
                internalCount += 1;
            } else {
                if (internalCount < repeatEvery) {
                    mSoundPool.play(beatSound1Id, 0.5f, 0.5f, 0, 0, 0f);
                    internalCount += 1;

                    if (internalCount == repeatEvery) {
                        internalCount = 0;
                    }
                }
            }
        }
    }

//    public void flashButton(final int count) {
//
//        final boolean[] shouldMakeVisible = {false};
//
//        ScheduledExecutorService animationExecutor = Executors.newSingleThreadScheduledExecutor();
//        final Runnable flasher = new Runnable() {
//            @Override
//            public void run() {
//                final LinearLayout verticalLayout = sender.findViewById(R.id.buttonLayout);
//
//
//                final Button currentButton = (Button) verticalLayout.getChildAt(count);
//
//                if (shouldMakeVisible[0]) {
//                    ViewCompat.setBackgroundTintList(currentButton, ContextCompat.getColorStateList(sender, android.R.color.holo_blue_dark));
//                } else {
//                    ViewCompat.setBackgroundTintList(currentButton, ContextCompat.getColorStateList(sender, android.R.color.holo_blue_bright));
//                    shouldMakeVisible[0] = true;
//                }
//            }
//        };
//
//        animationExecutor.execute(flasher);
//        animationExecutor.schedule(flasher, 32, TimeUnit.MILLISECONDS);
//    }
}
