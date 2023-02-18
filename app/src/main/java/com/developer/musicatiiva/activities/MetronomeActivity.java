package com.developer.musicatiiva.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import java.sql.Timestamp;

import android.media.AudioManager;
import android.os.Bundle;
import android.media.SoundPool;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.developer.musicatiiva.R;

import com.developer.musicatiiva.utils.MyTask;
import com.developer.musicatiiva.utils.PreferenceHandler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.beppi.knoblibrary.Knob;
// https://github.com/BeppiMenozzi/Knob

public class MetronomeActivity extends AppCompatActivity {

    private SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//*
    private int sound1Id = -1;//**
    private int sound2Id = -1;//**
    private int repeatEvery = 4;
    private int division = 4;
    private int tempo = 120;

    private boolean isPlaying = false;

    ScheduledFuture<?> executingTask;
    ScheduledFuture<?> tapDeletionTimer;

    Knob knob;
    ImageButton playPauseBtn;
    ImageButton tapTempoBtn;

    TextView bpmTextView;

    // Time signature controls
    TextView topTextView;
    TextView bottomTextView;
    ImageButton topPlusBtn;
    ImageButton topMinusBtn;
    ImageButton bottomPlusBtn;
    ImageButton bottomMinusBtn;

    int topValueArray[] = {2, 3, 4, 5, 6, 7, 9, 12};
    int bottomValueArray[] = {2, 4, 8};
    int currentTopValueIndex = 2;
    int currentBottomValueIndex = 1;


    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);


    // Tap tempo functionality
    private Queue<Timestamp> taps = new LinkedList<>();
    private ScheduledThreadPoolExecutor tapTempoDeletionExecutor = new ScheduledThreadPoolExecutor(1);


    private long period = 1000000;
    MyTask task = new MyTask();

    LinearLayout verticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronome);

        // Dynamic buttons
        verticalLayout = findViewById(R.id.buttonLayout);
        layoutButtons();

        sound1Id = mSoundPool.load(this, R.raw.beat, 1);
        sound2Id = mSoundPool.load(this, R.raw.beat2, 1);

        // UI elements
        tapTempoBtn = findViewById(R.id.tapTempoBtn);
        tapTempoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                processTap();
            }
        });


        // Time Signature control
        topTextView = findViewById(R.id.topTextView);
        bottomTextView = findViewById(R.id.bottomTextView);

        topTextView.setText(Integer.toString(repeatEvery));
        bottomTextView.setText(Integer.toString(division));

        topPlusBtn = findViewById(R.id.plusBtnTop);
        topMinusBtn = findViewById(R.id.minusBtnTop);
        bottomPlusBtn = findViewById(R.id.plusBtnBottom);
        bottomMinusBtn = findViewById(R.id.minusBtnBottom);
        isPlaying= PreferenceHandler.readBoolean(this,PreferenceHandler.IS_PLAYING,false);

        topPlusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentTopValueIndex < topValueArray.length - 1) {
                    currentTopValueIndex += 1;
                    repeatEvery = topValueArray[currentTopValueIndex];
                    layoutButtons();
                    changeTempo();
                    topTextView.setText(Integer.toString(repeatEvery));
                }
            }
        });

        topMinusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentTopValueIndex > 0) {
                    currentTopValueIndex -= 1;
                    repeatEvery = topValueArray[currentTopValueIndex];
                    layoutButtons();
                    changeTempo();
                    topTextView.setText(Integer.toString(repeatEvery));
                }
            }
        });

        bottomPlusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentBottomValueIndex < bottomValueArray.length - 1) {
                    currentBottomValueIndex += 1;
                    division = bottomValueArray[currentBottomValueIndex];
                    layoutButtons();
                    changeTempo();
                    bottomTextView.setText(Integer.toString(division));
                }
            }
        });

        bottomMinusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentBottomValueIndex > 0) {
                    currentBottomValueIndex -= 1;
                    division = bottomValueArray[currentBottomValueIndex];
                    layoutButtons();
                    changeTempo();
                    bottomTextView.setText(Integer.toString(division));
                }
            }
        });




        playPauseBtn = findViewById(R.id.playPauseBtn);
        if(isPlaying)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pausebtn);

        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.ic_playbtn);

        }
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isPlaying) {

                    isPlaying = false;

                    PreferenceHandler.writeBoolean(getApplicationContext(),PreferenceHandler.IS_PLAYING,false);
                    playPauseBtn.setImageResource(R.drawable.ic_playbtn);
                    stopTimer();
                } else {
                    isPlaying = true;
                    PreferenceHandler.writeBoolean(getApplicationContext(),PreferenceHandler.IS_PLAYING,true);

                    playPauseBtn.setImageResource(R.drawable.ic_pausebtn);
                    startTimer();
                }
            }
        });

        bpmTextView = findViewById(R.id.bpmTextView);

        knob = findViewById(R.id.knob);
        knob.setNumberOfStates(300);
        knob.setState(150);
        knob.setFreeRotation(true);
        knob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                tempo = state;
                changeTempo();
                bpmTextView.setText(Integer.toString(state));
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        //stopTimer();
    }

    private void startTimer() {
        //task.setParameters(mSoundPool, sound1Id, sound2Id, repeatEvery, this);

        period = (((1000000 * 4) / division) * 60) / tempo;
        executingTask = exec.scheduleWithFixedDelay(task, 0, period, TimeUnit.MICROSECONDS);
    }

     private void stopTimer() {
         if(executingTask!=null)
        {
            executingTask.cancel(true);
        }

    }

    private void changeTempo() {
        if (tempo > 0) {
            if (executingTask != null) {
                stopTimer();
                if (isPlaying) {
                    startTimer();
                }
            }
        }
    }


    private void processTap() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());


        if (taps.size() < 3) {
            taps.add(currentTimestamp);
        } else {
            taps.remove();
            taps.add(currentTimestamp);
            computeTapTempo();
        }

        final Runnable tapDeleter = new Runnable() {
            @Override
            public void run() {
                System.out.println("Taps deleted");
                taps = new LinkedList<>();
            }
        };
        if (tapDeletionTimer != null) {
            tapDeletionTimer.cancel(true);
            tapDeletionTimer = tapTempoDeletionExecutor.schedule(tapDeleter, 5000, TimeUnit.MILLISECONDS);
        } else {
            tapDeletionTimer = tapTempoDeletionExecutor.schedule(tapDeleter, 5000, TimeUnit.MILLISECONDS);
        }
    }


    private void computeTapTempo() {
        long sumInterval = 0;

        Iterator iterator = taps.iterator();

        Timestamp previousTimestamp = new Timestamp(System.currentTimeMillis() - System.currentTimeMillis());
        boolean isFirst = true;

        while (iterator.hasNext()) {
            if (isFirst) {
                previousTimestamp = (Timestamp) iterator.next();
                isFirst = false;
            } else {
                Timestamp timestamp = (Timestamp) iterator.next();
                sumInterval += timestamp.getTime() - previousTimestamp.getTime();
                previousTimestamp = timestamp;
            }
        }

        long averageIntervalMillis = sumInterval / (taps.size() - 1);

        int newTempo = (int) (60000 / averageIntervalMillis);
       // System.out.println(averageIntervalMillis);

        if (newTempo > 30 && newTempo < 300) {

            tempo = newTempo;
            knob.setState(tempo);
            bpmTextView.setText(Integer.toString(tempo));

        }

        changeTempo();
    }

    private void layoutButtons() {
        verticalLayout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(1,0,1,0);
        verticalLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < repeatEvery; i++) {
            Button button = new Button(this);
            // You can set button parameters here:
            button.setWidth((verticalLayout.getWidth() / repeatEvery)-2);
            button.setPadding(2,2,2,2);
            button.setId(i);
            button.setLayoutParams(params);
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark));
            button.setEnabled(false);
            verticalLayout.addView(button);
        }
    }
//    public class MetronomeTask extends IntentService {
//
//
//        public MetronomeTask(String name) {
//            super(name);
//        }
//
//        @Override
//        protected void onHandleIntent(@Nullable Intent intent) {
//
//        }
//    }
}
