package com.developer.musicatiiva.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MyTask;
import com.developer.musicatiiva.utils.PreferenceHandler;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.beppi.knoblibrary.Knob;

public class MetronomeTestActivity extends AppCompatActivity {
   // private SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    //private int sound1Id = -1;
    //private int sound2Id = -1;
    private  int repeatEvery = 4;
    private  int division = 4;
    private  int tempo = 120;
     Context context;

    private  boolean isPlaying = false;
    private  SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    private int sound1Id;

    private  int sound2Id;
 //   ScheduledFuture<?> executingTask;


    //ScheduledFuture<?> tapDeletionTimer;

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
    private MetronomeTask metronomeTask;
    private boolean mIsBound=false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
             // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            metronomeTask = ((MetronomeTask.Binder)service).getService();
            metronomeTask.setParameters(mSoundPool,sound1Id,sound2Id);


            // Tell the user about this for our demo.

        }

        public void onServiceDisconnected(ComponentName className) {
             // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            //metronomeTask = null;
//            Toast.makeText(Binding.this,
//                    R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
         // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other
        // applications).
        Intent intent=new Intent(this, MetronomeTask.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
        else {
            context.startService(intent);
        }
//        startService(intent);
        bindService(intent,
                mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       doUnbindService();
    }


//    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);


    // Tap tempo functionality
  //  private Queue<Timestamp> taps = new LinkedList<>();
   // private ScheduledThreadPoolExecutor tapTempoDeletionExecutor = new ScheduledThreadPoolExecutor(1);



//    MyTask task = new MyTask();

    LinearLayout verticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_metronome_test);
        context=this;
        sound1Id = mSoundPool.load(context, R.raw.beat, 1);
        sound2Id = mSoundPool.load(context, R.raw.beat2, 1);
        tempo=PreferenceHandler.readInteger(context,PreferenceHandler.TEMPO,120);
        repeatEvery=PreferenceHandler.readInteger(context,PreferenceHandler.REPEAT_QUERY,4);
        division=PreferenceHandler.readInteger(context,PreferenceHandler.DIVISION,4);


        if(!isMyServiceRunning(MetronomeTask.class))
        {
             doBindService();
        }
        // Dynamic buttons
        verticalLayout = findViewById(R.id.buttonLayout);
        layoutButtons(repeatEvery,"");

        //sound1Id = mSoundPool.load(this, R.raw.beat, 1);
        //sound2Id = mSoundPool.load(this, R.raw.beat2, 1);

        // UI elements
        tapTempoBtn = findViewById(R.id.tapTempoBtn);
        tapTempoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","processTap");
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
                //processTap();
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
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","handleTopPlus");
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
//                if (currentTopValueIndex < topValueArray.length - 1) {
//                    currentTopValueIndex += 1;
//                    repeatEvery = topValueArray[currentTopValueIndex];
//                    layoutButtons();
//                    if(isMyServiceRunning(MetronomeTask.class))
//                        metronomeTask.changeTempo();
//                   // changeTempo();
//                    topTextView.setText(Integer.toString(repeatEvery));
//                }
            }
        });

        topMinusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","handleTopMinus");
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
//                if (currentTopValueIndex > 0) {
//                    currentTopValueIndex -= 1;
//                    repeatEvery = topValueArray[currentTopValueIndex];
//                    layoutButtons();
//                    if(isMyServiceRunning(MetronomeTask.class))
//                        metronomeTask.changeTempo();
//                   // changeTempo();
//                    topTextView.setText(Integer.toString(repeatEvery));
//                }
            }
        });

        bottomPlusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","handleBottomPlus");
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
//                if (currentBottomValueIndex < bottomValueArray.length - 1) {
//                    currentBottomValueIndex += 1;
//                    division = bottomValueArray[currentBottomValueIndex];
//                    layoutButtons();
//                    if(isMyServiceRunning(MetronomeTask.class))
//                        metronomeTask.changeTempo();
//                    //changeTempo();
//                    bottomTextView.setText(Integer.toString(division));
//                }
            }
        });

        bottomMinusBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","handleBottomMinus");
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
//                if (currentBottomValueIndex > 0) {
//                    currentBottomValueIndex -= 1;
//                    division = bottomValueArray[currentBottomValueIndex];
//                    layoutButtons();
//                    if(isMyServiceRunning(MetronomeTask.class))
//                        metronomeTask.changeTempo();
//                    //changeTempo();
//                    bottomTextView.setText(Integer.toString(division));
//                }
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
//                    if(metronomeTask!=null)
//                    {
//                        Log.d(TAG, "onClick: stop timer");
//                       metronomeTask.stopTimer();
//                    }
                    Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                    intent.putExtra("type","stopTimer");
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(intent);

                   // stopTimer();
                } else {
                     isPlaying = true;
                    PreferenceHandler.writeBoolean(getApplicationContext(),PreferenceHandler.IS_PLAYING,true);

                    playPauseBtn.setImageResource(R.drawable.ic_pausebtn);
                    Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                    intent.putExtra("type","startTimer");
                    LocalBroadcastManager.getInstance(context)
                            .sendBroadcast(intent);
                    
                }
            }
        });

        bpmTextView = findViewById(R.id.bpmTextView);
        bpmTextView.setText(String.valueOf(tempo));

        knob = findViewById(R.id.knob);
        knob.setNumberOfStates(300);
        knob.setState(150);
        knob.setFreeRotation(true);
        knob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                Intent intent=new Intent(Constants.ACTION_METRONOME_TASK);
                intent.putExtra("type","handleKnob");
                intent.putExtra("tempo",state);
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(intent);
//                tempo = state;
//                if(isMyServiceRunning(MetronomeTask.class))
//                metronomeTask.changeTempo();

                bpmTextView.setText(Integer.toString(state));
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
       registerReceiver();
    }

    @Override
    public void onStop() {
        unRegisterReciever();

        super.onStop();
    }
    private void registerReceiver(){

        IntentFilter f=new IntentFilter(Constants.ACTION_METRONOME);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(onEvent, f);
    }
    private void unRegisterReciever(){

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(onEvent);
    }

     private BroadcastReceiver onEvent=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
             String type=i.getStringExtra("type");
            if(type.equals("internalCount"))
            {
                flashButton(i.getIntExtra("internalCount",0));
            }
            else if(type.equals("top"))
            {
                layoutButtons(i.getIntExtra("repeatEvery",4),type);
            }
            else if(type.equals("bottom"))
            {
                layoutButtons(i.getIntExtra("division",4),type);
            }
            else if(type.equals("tempo"))
            {
                int tempo=i.getIntExtra("tempo",120);
                knob.setState(tempo);
                bpmTextView.setText(Integer.toString(tempo));
            }


            //Toast.makeText(getActivity(), R.string.download_complete,
              //      Toast.LENGTH_LONG).show();
        }
    };
    public void flashButton(final int count) {

        final boolean[] shouldMakeVisible = {false};

        ScheduledExecutorService animationExecutor = Executors.newSingleThreadScheduledExecutor();
        final Runnable flasher = new Runnable() {
            @Override
            public void run() {
                final LinearLayout verticalLayout = findViewById(R.id.buttonLayout);


                final Button currentButton = (Button) verticalLayout.getChildAt(count);

                if (shouldMakeVisible[0]) {
                    ViewCompat.setBackgroundTintList(currentButton, ContextCompat.getColorStateList(MetronomeTestActivity.this, android.R.color.holo_blue_dark));
                } else {
                    ViewCompat.setBackgroundTintList(currentButton, ContextCompat.getColorStateList(MetronomeTestActivity.this, android.R.color.holo_blue_bright));
                    shouldMakeVisible[0] = true;
                }
            }
        };

        animationExecutor.execute(flasher);
        animationExecutor.schedule(flasher, 32, TimeUnit.MILLISECONDS);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




//    private void processTap() {
//        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
//
//
//        if (taps.size() < 3) {//
    //ont he cvaiable is used for the upwork billling is under the value of hte new sysmbols
    //dori ye keechi dori..palne ki tune mori...mere sapno ko jhukya sari raat .chaye..bagiya teri sgodi.chaye nindiya teri todi
    ///bas itti sibaat toh rhiyo meri yaad......
//            taps.add(currentTimestamp);
//        } else {
//            taps.remove();
//            taps.add(currentTimestamp);
//            computeTapTempo();
//        }
//
//        final Runnable tapDeleter = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Taps deleted");
//                taps = new LinkedList<>();
//            }
    // while giving the advice turn on the varibael
//        };
//        if (tapDeletionTimer != null) {
//            tapDeletionTimer.cancel(true);
//            tapDeletionTimer = tapTempoDeletionExecutor.schedule(tapDeleter, 5000, TimeUnit.MILLISECONDS);
//        } else {
//            tapDeletionTimer = tapTempoDeletionExecutor.schedule(tapDeleter, 5000, TimeUnit.MILLISECONDS);
//        }
//    }


//    private void computeTapTempo() {
//        long sumInterval = 0;
//
//        Iterator iterator = taps.iterator();
//
//        Timestamp previousTimestamp = new Timestamp(System.currentTimeMillis() - System.currentTimeMillis());
//        boolean isFirst = true;
//
//        while (iterator.hasNext()) {
//            if (isFirst) {
//                previousTimestamp = (Timestamp) iterator.next();
//                isFirst = false;
//            } else {
//                Timestamp timestamp = (Timestamp) iterator.next();
//                sumInterval += timestamp.getTime() - previousTimestamp.getTime();
//                previousTimestamp = timestamp;
//            }
//        }
//
//        long averageIntervalMillis = sumInterval / (taps.size() - 1);
//
//        int newTempo = (int) (60000 / averageIntervalMillis);
//        System.out.println(averageIntervalMillis);
//
//        if (newTempo > 30 && newTempo < 300) {
//
//            tempo = newTempo;
//            knob.setState(tempo);
//            bpmTextView.setText(Integer.toString(tempo));
//
//        }
//        if(isMyServiceRunning(MetronomeTask.class))
//            metronomeTask.changeTempo();
//       // changeTempo();
//    }

    private void layoutButtons(int repeatEvery,String type) {
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
        if(type.equals("top"))
            topTextView.setText(Integer.toString(repeatEvery));
        else if(type.equals("bottom"))
            bottomTextView.setText(Integer.toString(repeatEvery));
    }
    public static class MetronomeTask extends Service {
        ScheduledFuture<?> executingTask;
        ScheduledFuture<?> tapDeletionTimer;
        private Queue<Timestamp> taps = new LinkedList<>();
        private ScheduledThreadPoolExecutor tapTempoDeletionExecutor = new ScheduledThreadPoolExecutor(1);

        SoundPool mSoundPool;
        private  long period = 1000000;
        int sound1Id;
        int sound2Id;
        int repeatEvery = 4;
        private int division = 4;
        private int tempo = 120;
        int topValueArray[] = {2, 3, 4, 5, 6, 7, 9, 12};
        int bottomValueArray[] = {2, 4, 8};
        int currentTopValueIndex = 2;
        int currentBottomValueIndex = 1;
        private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
       MyReceiver mReceiver;

        MyTask task = new MyTask();

        public Binder binder;

        public MetronomeTask() {
            super();
        }
        public void setParameters(SoundPool mSoundPool,int sound1Id,int sound2Id)
        {
            this.mSoundPool=mSoundPool;
            this.sound1Id=sound1Id;
            this.sound2Id=sound2Id;
        }
//in order to est it to diffrent device hthis app has bee rejected once the value is h
        public class MyReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                // do something
                String type=intent.getStringExtra("type");
                if(type.equals("stopTimer"))
                {
                    stopTimer();
                }
                else if(type.equals("startTimer"))
                {
                    startTimer();
                }
                else if(type.equals("handleTopPlus"))
                {
                    handleTopPlusButton();
                }
                else if(type.equals("handleTopMinus"))
                {
                    handleTopMinusButton();
                }
                else if(type.equals("handleBottomPlus"))
                {
                    handleBottomPlusButton();
                }
                else if(type.equals("handleBottomMinus"))
                {
                    handleBottomMinusButton();
                }
                else if(type.equals("processTap"))
                {
                    processTap();
                }
                else if(type.equals("handleKnob"))
                {
                    int tempo= intent.getIntExtra("tempo",120);
                    handleKnob(tempo);
                }
            }

            // constructor
            public MyReceiver(){

            }
        }

        @Override
        public void onCreate() {
            super.onCreate();
            binder = new Binder();
            mReceiver = new MyReceiver();

            IntentFilter f=new IntentFilter(Constants.ACTION_METRONOME_TASK);

            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, f);
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("action");
//            filter.addAction("anotherAction");
//            mReceiver = new MyReceiver();
//            registerReceiver(mReceiver, filter);
        }


        @Override
        public IBinder onBind(Intent intent) {
            return binder;
        }



        public class Binder extends android.os.Binder {
            public MetronomeTask getService() {
                return MetronomeTask.this;
            }
        }


//        public MetronomeTask(String name) {
//            super(name);
//            sound1Id = mSoundPool.load(this, R.raw.beat, 1);
//            sound2Id = mSoundPool.load(this, R.raw.beat2, 1);
//
//        }


        public void startTimer() {
            task.setParameters(mSoundPool, sound1Id, sound2Id, repeatEvery, this);

            period = (((1000000 * 4) / division) * 60) / tempo;
            executingTask = exec.scheduleWithFixedDelay(task, 0, period, TimeUnit.MICROSECONDS);
        }


        public void stopTimer() {
            if(executingTask!=null)
            {
                executingTask.cancel(true);
            }

        }
        public void handleTopPlusButton()
        {
            if (currentTopValueIndex < topValueArray.length - 1) {
                currentTopValueIndex += 1;
                repeatEvery = topValueArray[currentTopValueIndex];
                PreferenceHandler.writeInteger(this,PreferenceHandler.REPEAT_QUERY,repeatEvery);
                Intent intent=new Intent(Constants.ACTION_METRONOME);
                intent.putExtra("type","top");
                intent.putExtra("repeatEvery",repeatEvery);
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent);
                //layoutButtons();

                changeTempo();
                // changeTempo();
               // topTextView.setText(Integer.toString(repeatEvery));
            }
        }
        public void handleTopMinusButton()
        {
            if (currentTopValueIndex > 0) {
                currentTopValueIndex -= 1;
                repeatEvery = topValueArray[currentTopValueIndex];
                PreferenceHandler.writeInteger(this,PreferenceHandler.REPEAT_QUERY,repeatEvery);

                Intent intent=new Intent(Constants.ACTION_METRONOME);
                intent.putExtra("type","top");
                intent.putExtra("repeatEvery",repeatEvery);
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent);
              //  layoutButtons();
                //if(isMyServiceRunning(MetronomeTask.class))
                  //  metronomeTask.changeTempo();
                 changeTempo();
                //topTextView.setText(Integer.toString(repeatEvery));
            }
        }
        public void handleBottomPlusButton()
        {
            if (currentBottomValueIndex < bottomValueArray.length - 1) {
                currentBottomValueIndex += 1;
                division = bottomValueArray[currentBottomValueIndex];
                PreferenceHandler.writeInteger(this,PreferenceHandler.DIVISION,division);

                // layoutButtons();
                //if(isMyServiceRunning(MetronomeTask.class))
                  //  metronomeTask.changeTempo();
                Intent intent=new Intent(Constants.ACTION_METRONOME);
                intent.putExtra("type","bottom");
                intent.putExtra("division",division);
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent);
                changeTempo();
              //  bottomTextView.setText(Integer.toString(division));
            }
        }
        public void handleBottomMinusButton()
        {
            if (currentBottomValueIndex > 0) {
                currentBottomValueIndex -= 1;
                division = bottomValueArray[currentBottomValueIndex];
                PreferenceHandler.writeInteger(this,PreferenceHandler.DIVISION,division);

                Intent intent=new Intent(Constants.ACTION_METRONOME);
                intent.putExtra("type","bottom");
                intent.putExtra("division",division);
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent);
                //layoutButtons();
                //if(isMyServiceRunning(MetronomeTask.class))
                  //  metronomeTask.changeTempo();
                changeTempo();
                //bottomTextView.setText(Integer.toString(division));
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Do your task here
            return START_STICKY;
        }

        public void changeTempo() {
            if (tempo > 0) {
                if (executingTask != null) {
                    stopTimer();
//                    isPlaying=
//
                    if (PreferenceHandler.readBoolean(this,PreferenceHandler.IS_PLAYING,false)) {
                        startTimer();
                    }
                }
            }

        }
        private void handleKnob(int tempo)
        {
            this.tempo = tempo;
            PreferenceHandler.writeInteger(this,PreferenceHandler.TEMPO,tempo);

            //if(isMyServiceRunning(MetronomeTask.class))
                //metronomeTask.changeTempo();
            changeTempo();
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
            System.out.println(averageIntervalMillis);

            if (newTempo > 30 && newTempo < 300) {

                tempo = newTempo;
                Intent intent=new Intent(Constants.ACTION_METRONOME);
                intent.putExtra("type","tempo");
                intent.putExtra("tempo",tempo);
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(intent);
                //knob.setState(tempo);
                //bpmTextView.setText(Integer.toString(tempo));

            }
            //if(isMyServiceRunning(MetronomeTask.class))
              //  metronomeTask.changeTempo();
             changeTempo();
        }


    }
}
