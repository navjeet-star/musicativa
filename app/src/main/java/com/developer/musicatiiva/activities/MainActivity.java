package com.developer.musicatiiva.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.ApiClient;
import com.developer.musicatiiva.apiModels.ApiInterface;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.ActivityMainBinding;
import com.developer.musicatiiva.fragments.ChordFragments;
import com.developer.musicatiiva.fragments.CommentsFragment;
import com.developer.musicatiiva.fragments.InstrumentsFragments;
import com.developer.musicatiiva.fragments.ProfileInformationFragment;
import com.developer.musicatiiva.fragments.ReminderFragment;
import com.developer.musicatiiva.models.AddTimerResponse;
import com.developer.musicatiiva.models.AnalysisData;
import com.developer.musicatiiva.models.Data;
import com.developer.musicatiiva.models.PauseOrStopTimerResponse;
import com.developer.musicatiiva.models.ResultModel;
import com.developer.musicatiiva.models.SaveCommentsResponse;
import com.developer.musicatiiva.models.Tip;
import com.developer.musicatiiva.models.TipResponse;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.KeyboardUtil;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.developer.musicatiiva.utils.PreferenceHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.CLOCK_URL;
import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

public class MainActivity extends AppCompatActivity implements KeyboardUtil.onBackDispatch {

    public ActivityMainBinding activityMainBinding;
    FragmentTransaction fragmentTransaction;
    private String initialTime = "00:00:00";
    private String finalTime   = "00:00:00";
    private int user_id = 0;
    private JsonPlaceHolderApi jsonPlaceHolderApi,jsonPlaceHolderApi2;

    private String timer_id;
    public int category_id;
    public int practice_id;
    
    public boolean timerStarted = true;
    public boolean timerStoppedAfterSavingDetails = true;
    Fragment visible;
    private AnalysisData analysisData = null;
    public String activityTitle;
    private boolean timerAlreadyPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setToolbar();
        setData();

        setInitialCounterValue();
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(INSTRUMENT_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CLOCK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        jsonPlaceHolderApi2 = retrofit2.create(JsonPlaceHolderApi.class);
        user_id = MySharedPreferences.getInstance().getUserID(MainActivity.this, Constants.USERID);
           //checkForTheSubscribtion(user_id);
        //Crashlytics.getInstance().crash();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String dateOnly = dateFormat.format(currentDate);
        String getsharedDate = MySharedPreferences.getInstance().getTodaysDate(MainActivity.this);

        Log.d("dateWithoutTime", "" + dateOnly);
        Log.d("dateWithoutTime", "getsharedDate" + getsharedDate);

        if (!dateOnly.toString().equals(getsharedDate)) {

            getMessageTip();

        }
    }

    private void getMessageTip() {

        new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    //CommonDialogs.getInstance().showProgressDialog(MainActivity.this);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Constants.USER_ID, user_id);


                    Call<TipResponse> call = jsonPlaceHolderApi2.getTip(jsonObject);

                    call.enqueue(new Callback<TipResponse>() {
                        @Override
                        public void onResponse(Call<TipResponse> call, Response<TipResponse> response) {
                            if (response.isSuccessful()) {

                                TipResponse tipResponse = response.body();

                                String description = tipResponse.getDescription();
                                String status_codee = tipResponse.getStatusCode();

                                int status_code = Integer.parseInt(status_codee);


                                if (status_code == 1) {

                                    if (tipResponse.getData() != null) {

                                        Date currentDate = new Date();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                                        String dateOnly = dateFormat.format(currentDate);

                                        MySharedPreferences.getInstance().setTodaysDate(MainActivity.this, dateOnly);

//                                        boolean shownDialog=CommonMethods.getInstance().getDialogSeenState(MainActivity.this,user_id);
//                                        if(!shownDialog)

                                        openCongratulationsDialog(tipResponse.getData());

                                    }

                                    // finish();

                                } else {

                                    Log.d("mohit", "getMessageTip onResponse: Status code : " + status_code + "\n" + "Description : " + description);

                                    //Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }

                            } else {

                                Log.d("mohit", "getMessageTip onResponse: " + response.message());

                                // Toast.makeText(MainActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<TipResponse> call, Throwable t) {
                            Log.d("mohit", "getMessageTip Error code: " + t.getMessage());
                            //  Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                            //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(t.getMessage());


                        }
                    });
                }
            }
        });
    }

    private void setData() {

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        InstrumentsFragments instrumentsFragments = new InstrumentsFragments();
        fragmentTransaction.replace(R.id.frame, instrumentsFragments);
        fragmentTransaction.commit();
    }

    private void setToolbar() {
        activityMainBinding.iTollbar.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SetReminderActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(activityMainBinding.iTollbar.myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        activityMainBinding.iTollbar.menuDrawable.setVisibility(View.VISIBLE);
        activityMainBinding.iTollbar.menuDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
                activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                setNavigationDrawer();
            }
        });
        activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);
        activityMainBinding.iTollbar.tvStartCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerStarted) {

                    if (dialogForPauseOrStopTimer == null) {
                        initializeTimerAtServer(Constants.START);
                    } else {
                        openDialogforTimerPauseOrStop();
                    }
                } else {
                    openDialogforTimerPauseOrStop();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (visibleFragment instanceof ChordFragments) {
            {

                if (KeyboardUtil.isKeyboardOpen(this) && (((ChordFragments) visibleFragment).isOpen)) {
                    onPressed((ChordFragments) visibleFragment);
                } else {
                    if (timerStoppedAfterSavingDetails) {
                        super.onBackPressed();
                    } else {
                        showAlertMessageDialogToStopTimer();
                    }
                }
            }

        } else
            super.onBackPressed();


    }
    private void checkForTheSubscribtion(int userId){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, userId);
        jsonObject.addProperty("type","android");
        Log.d("userId",userId+"");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.checkTheSubscription(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject object=response.body();
                    String code=object.get("status_code").getAsString();
                     int codeData=Integer.parseInt(code);
                    Log.d("Result",codeData+"");
                    if(codeData==0){
                         Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("Result1",response.body().toString());
                      /*  Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                        finish();*/
                      /*  Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();*/
                    }
                    else{

                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                startActivity(intent);
                finish();
            }
        } );
    }

    //test series for the new series as per the chages of the new vlaues

    private void showAlertMessageDialogToStopTimer() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Please stop the timer first.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                     dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    private void initializeTimer() {
        timerStarted = false;
        activityMainBinding.iTollbar.tvCounterValue.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void resumeTimer() {

        if (dialogForPauseOrStopTimer != null)
            mTvTimerPause.setText(Constants.PAUSE);
        timerStarted = false;
        activityMainBinding.iTollbar.tvCounterValue.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void pauseTimer() {

         if (dialogForPauseOrStopTimer != null) {

            dialogForPauseOrStopTimer.dismiss();
            mTvTimerPause.setText(Constants.RESUME);
        }
        timerStarted = true;
        activityMainBinding.iTollbar.tvStartCounter.setImageResource(R.drawable.add);
        stopTimer();
    }

    private void stopTimerToOpenTimerDetails() {
        dialogForPauseOrStopTimer.dismiss();
        dialogForPauseOrStopTimer = null;
        timerStoppedAfterSavingDetails = true;
        timerStarted = true;
        stopTimer();

        activityMainBinding.iTollbar.tvCounterValue.setText("");
        activityMainBinding.iTollbar.tvCounterValue.setVisibility(View.GONE);
        activityMainBinding.iTollbar.tvStartCounter.setImageResource(R.drawable.timer_start);

        openDialogForTimerDetails();
    }



    private void notifyPauseOrStopTimerToTheServer(final String timerState) {
         new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(MainActivity.this);
                    JsonObject jsonObject = new JsonObject();

                    String currentTime = CommonMethods.getInstance().getCurrentTimeIn24HoursFormat();

                     jsonObject.addProperty(Constants.TIMER_ID, timer_id);


                    jsonObject.addProperty(Constants.END_TIME, currentTime);
                    Call<PauseOrStopTimerResponse> call = jsonPlaceHolderApi.pauseOrStopTimer(jsonObject);
                    call.enqueue(new Callback<PauseOrStopTimerResponse>() {
                        @Override
                        public void onResponse(Call<PauseOrStopTimerResponse> call, Response<PauseOrStopTimerResponse> response) {
                            if (response.isSuccessful()) {
                                PauseOrStopTimerResponse pauseOrStopTimerResponse = response.body();

                                String description = pauseOrStopTimerResponse.getDescription();
                                String status_codee = pauseOrStopTimerResponse.getStatusCode();

                                int status_code = Integer.parseInt(status_codee);


                                if (status_code == 1) {
                                    AnalysisData data = pauseOrStopTimerResponse.getAnalysisData();
                                    if (data != null) {
                                        analysisData = data;
                                    }
                                    if (timerState.equals(Constants.PAUSE)) {
                                         pauseTimer();
                                    } else if (timerState.equals(Constants.STOP)) {
                                        stopTimerToOpenTimerDetails();
                                    }

                                    // finish();
                                } else {
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }


                            } else {
                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(MainActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }

                        @Override
                        public void onFailure(Call<PauseOrStopTimerResponse> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                            //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(t.getMessage());


                        }
                    });
                }
            }
        });
    }

    private void initializeTimerAtServer(final String timerState) {
         new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(MainActivity.this);
                    JsonObject jsonObject = new JsonObject();
                    int user_id = MySharedPreferences.getInstance().getUserID(MainActivity.this, Constants.USERID);
                    String currentTime = CommonMethods.getInstance().getCurrentTimeIn24HoursFormat();

                    jsonObject.addProperty(Constants.UID, user_id);
                    jsonObject.addProperty(Constants.SUB_CATEGORY_ID, category_id);
                    jsonObject.addProperty(Constants.START_TIME, currentTime);
                    jsonObject.addProperty(Constants.PRACTICE_ID, practice_id);

                    Call<AddTimerResponse> call = jsonPlaceHolderApi.addTimer(jsonObject);
                    call.enqueue(new Callback<AddTimerResponse>() {
                        @Override
                        public void onResponse(Call<AddTimerResponse> call, Response<AddTimerResponse> response) {
                            if (response.isSuccessful()) {
                                AddTimerResponse addTimerResponse = response.body();

                                String description = addTimerResponse.getDescription();
                                String status_codee = addTimerResponse.getStatusCode();
                                int status_code = Integer.parseInt(status_codee);
                                Data data = addTimerResponse.getData();


                                if (status_code == 1) {
                                    if (data != null) {
                                        timer_id = String.valueOf(data.getId());
                                        if (timerState.equals(Constants.RESUME)) {
                                            resumeTimer();
                                        } else {
                                             initializeTimer();
                                        }

                                        //openDialogforTimerPauseOrStop();

                                    } else {
                                        // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    }
                                    // finish();
                                } else {
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }


                            } else {
                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(MainActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }

                        @Override
                        public void onFailure(Call<AddTimerResponse> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                            //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(t.getMessage());


                        }
                    });
                }
            }
        });
    }

    Dialog dialogForPauseOrStopTimer;
    Button mTvTimerStop;
    Button mTvTimerPause;

    private void openDialogforTimerPauseOrStop() {

        if (dialogForPauseOrStopTimer == null) {

            dialogForPauseOrStopTimer = new Dialog(this);
            dialogForPauseOrStopTimer.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogForPauseOrStopTimer.setContentView(R.layout.dialog_timer_pause_stop);
            dialogForPauseOrStopTimer.setCanceledOnTouchOutside(true);
            dialogForPauseOrStopTimer.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogForPauseOrStopTimer.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mTvTimerPause = dialogForPauseOrStopTimer.findViewById(R.id.textViewPause);
            mTvTimerStop = dialogForPauseOrStopTimer.findViewById(R.id.textViewStop);

        }

        mTvTimerPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTvTimerPause.getText().toString().equals(Constants.PAUSE)) {
                    timerAlreadyPaused = true;
                    notifyPauseOrStopTimerToTheServer(Constants.PAUSE);

                } else {

                    timerAlreadyPaused = false;
                    initializeTimerAtServer(Constants.RESUME);

                }

                dialogForPauseOrStopTimer.dismiss();
            }
        });

        mTvTimerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTvTimerPause.getText().toString().equals(Constants.RESUME)) {

                    stopTimerToOpenTimerDetails();

                } else {

                    notifyPauseOrStopTimerToTheServer(Constants.STOP);
                }
            }
        });

        //dialogForPauseOrStopTimer.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogForPauseOrStopTimer.show();

    }

    Dialog dialogForTimerDetails;
    TextView mTvStartTime, mTvStopTime, mTvTitle;
    Button mTvSaveBtn;
    EditText mEdComment;

    private void openDialogForTimerDetails() {

        dialogForTimerDetails = new Dialog(this);
        dialogForTimerDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForTimerDetails.setContentView(R.layout.dialog_timer_details);
        dialogForTimerDetails.setCanceledOnTouchOutside(true);
        dialogForTimerDetails.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogForTimerDetails.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogForTimerDetails.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mTvStartTime = dialogForTimerDetails.findViewById(R.id.textViewTodayStartTime);
        mTvStopTime = dialogForTimerDetails.findViewById(R.id.textViewTodayStopTime);

        mTvSaveBtn = dialogForTimerDetails.findViewById(R.id.textViewSave);

        mEdComment = dialogForTimerDetails.findViewById(R.id.editTextComment);
        mTvTitle = dialogForTimerDetails.findViewById(R.id.tv_title);
        mTvTitle.setText(activityTitle);
        mEdComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        if (analysisData != null) {

            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewLastMonthPracticedDate)).setText(analysisData.getLastMonthPracticeDate());

            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewLastMonthPracticedDuration)).setText(analysisData.getLastMonthSpendTime());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewlastMonthClicks)).setText(analysisData.getLastMonthClick());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewthisWeekPracticedDate)).setText(analysisData.getThisWeekPracticeDate());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewThisWeekPracticedDuration)).setText(analysisData.getThisWeekSpendTime());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textVewThisWeekClicks)).setText(analysisData.getThisWeekClick());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewTodaysDate)).setText(analysisData.getTodayPracticeDate());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewTodayStartTime)).setText(analysisData.getTodayStartTime());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewTodayStopTime)).setText(analysisData.getTodayEndTime());
            ((TextView) dialogForTimerDetails.findViewById(R.id.textViewTodayClicks)).setText(analysisData.getTodayClick());

        }

        /*mTvStartTime.setText(initialTime);
        mTvStopTime.setText(CommonMethods.getInstance().getCurrentTime());*/

        ImageView cancel = dialogForTimerDetails.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogForTimerDetails.dismiss();

            }
        });

        mTvSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveTimerDetails();

            }
        });
        dialogForTimerDetails.show();
    }

    private void saveTimerDetails() {

        final String title = mTvTitle.getText().toString().trim();
        final String comment = mEdComment.getText().toString().trim();

       /* if(title.isEmpty()) {

            mEdTitle.setError("Please enter title");
            mEdTitle.requestFocus();
            return;
        }

        if(comment.isEmpty()) {

            mEdComment.setError("Please enter comment");
            mEdComment.requestFocus();
            return;

        }*/

        new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    CommonDialogs.getInstance().showProgressDialog(MainActivity.this);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Constants.TIMER_ID, analysisData.getId());
                    jsonObject.addProperty(Constants.TITLE, title);
                    jsonObject.addProperty(Constants.COMMENT, comment);
                    Call<SaveCommentsResponse> call = jsonPlaceHolderApi.saveComments(jsonObject);

                    call.enqueue(new Callback<SaveCommentsResponse>() {
                        @Override
                        public void onResponse(Call<SaveCommentsResponse> call, Response<SaveCommentsResponse> response) {

                            if (response.isSuccessful()) {

                                SaveCommentsResponse saveCommentsResponse = response.body();
                                String description = saveCommentsResponse.getDescription();
                                String status_codee = saveCommentsResponse.getStatusCode();
                                int status_code = Integer.parseInt(status_codee);


                                if (status_code == 1) {

                                    Toast.makeText(MainActivity.this, "Submitted successfully", Toast.LENGTH_SHORT).show();

                                } else {

                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                }

                                dialogForTimerDetails.dismiss();

                            } else {

                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(MainActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
                                dialogForTimerDetails.dismiss();
                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<SaveCommentsResponse> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            dialogForTimerDetails.dismiss();
                        }
                    });

                } else {

                    dialogForTimerDetails.dismiss();
                }
            }
        });
    }

    Timer T;
    int s = 0;
    int h = 0;
    int m = 0;
    String initialCounterValue, finalCounterValue;

    private void startTimer() {

        if (timerStoppedAfterSavingDetails) {

            timerStoppedAfterSavingDetails = false;
            setInitialCounterValue();
        }

        T = new Timer();
        activityMainBinding.iTollbar.tvStartCounter.setImageResource(R.mipmap.timer_stop_green);
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String s1 = String.valueOf(s);
                        String h1 = String.valueOf(h);
                        String m1 = String.valueOf(m);

                        if (s1.length() == 1) {

                            s1 = "0" + s1;

                        }

                        if (m1.length() == 1) {

                            m1 = "0" + m1;

                        }

                        if (h1.length() == 1) {

                            h1 = "0" + h1;

                        }

                        finalCounterValue = h1 + ":" + m1 + ":" + s1;
                        activityMainBinding.iTollbar.tvCounterValue.setText(finalCounterValue);
                        s++;

                        if (s == 59) {

                            s = 0;
                            m++;

                        }

                        if (m == 59) {

                            m = 0;
                            h++;
                        }
                    }
                });
            }

        }, 1000, 1000);
    }

    private void setInitialCounterValue() {

        initialTime = CommonMethods.getInstance().getCurrentTime();

        //initialTime=PreferenceHandler.writeString(this,PreferenceHandler.PREF_KEY_TIMER_START_TIME,currentTime);

        //initialCounterValue=PreferenceHandler.readString(this,PreferenceHandler.PREF_KEY_TIMER_START_TIME,"00:00:00");
       /* h=Integer.parseInt(initialCounterValue.split(":")[0]);
        m=Integer.parseInt(initialCounterValue.split(":")[1]);
        s=Integer.parseInt(initialCounterValue.split(":")[2]);*/

        h = 0;
        s = 0;
        m = 0;

    }

    private void stopTimer() {

        T.cancel();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.frame);

            if (visibleFragment instanceof ChordFragments) {

                   if(((ChordFragments) visibleFragment).isOpen)

                    ((ChordFragments) visibleFragment).showDialog(this);

                   else
                       super.onBackPressed();
            }

            else
              onBackPressed();
//            onBackPressed();
//            finish();

        }

        return super.onOptionsItemSelected(item);

    }

    private void setNavigationDrawer() {

        // activityMainBinding.navigation.setItemIconTintList(null);

        activityMainBinding.navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //return false;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.profile) {
                    activityMainBinding.iTollbar.tvTitle.setText(R.string.profile_info);
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, new ProfileInformationFragment());
                    activityMainBinding.drawerLayout.closeDrawers(); // close the all open Drawer Views
                    return true;
                } else if (itemId == R.id.instruments) {
                    activityMainBinding.iTollbar.tvTitle.setText(R.string.instruments);
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, new InstrumentsFragments());
                    activityMainBinding.drawerLayout.closeDrawers();
                    return true;
                } else if (itemId == R.id.reminder) {
                    activityMainBinding.iTollbar.tvTitle.setText(R.string.reminder);
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.VISIBLE);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, new ReminderFragment());
                    activityMainBinding.drawerLayout.closeDrawers();
                    return true;
                } else if (itemId == R.id.logout) {
                    activityMainBinding.drawerLayout.closeDrawers();
                    CommonDialogs.getInstance().showMessage2ButtonsDialog(MainActivity.this, "Do you really want to Logout from App ?", no, yes);
                } else if (itemId == R.id.share) {
                    shareAppLink();
                    activityMainBinding.drawerLayout.closeDrawers();
                } else if (itemId == R.id.today) {
                    activityMainBinding.iTollbar.tvTitle.setText("Comments");
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);

                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("dataType", Constants.TODAY);
                    commentsFragment.setArguments(bundle);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, commentsFragment);
                    activityMainBinding.drawerLayout.closeDrawers();
                } else if (itemId == R.id.weekly) {
                    activityMainBinding.iTollbar.tvTitle.setText("Comments");
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);
                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("dataType", Constants.WEEKLY);
                    commentsFragment.setArguments(bundle);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, commentsFragment);
                    activityMainBinding.drawerLayout.closeDrawers();
                } else if (itemId == R.id.monthly) {
                    activityMainBinding.iTollbar.tvTitle.setText("Comments");
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);
                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("dataType", Constants.MONTHLY);
                    commentsFragment.setArguments(bundle);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, commentsFragment);
                    activityMainBinding.drawerLayout.closeDrawers();

                } else if (itemId == R.id.yearly) {
                    activityMainBinding.iTollbar.tvTitle.setText("Comments");
                    activityMainBinding.iTollbar.imgPlus.setVisibility(View.GONE);
                    CommentsFragment commentsFragment = new CommentsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("dataType", Constants.YEARLY);
                    commentsFragment.setArguments(bundle);
                    CommonMethods.getInstance().replaceFragment(MainActivity.this, commentsFragment);
                    activityMainBinding.drawerLayout.closeDrawers();

                }

                return false;

            }
        });
    }

    private void shareAppLink() {
         ShareCompat.IntentBuilder.from(this)

                .setType("text/plain")
                .setChooserTitle("Choose one")
                .setText("I suggest this app for you : http://play.google.com/store/apps/details?id=" + this.getPackageName())
                .startChooser();
    }

    View.OnClickListener no = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

         CommonDialogs.getInstance().dismissDialog();

        }
    };

    View.OnClickListener yes = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            CommonDialogs.getInstance().dismissDialog();
            PreferenceHandler.writeString(MainActivity.this, PreferenceHandler.PREF_KEY_TIMER_START_TIME, "00:00:00");
            MySharedPreferences.getInstance().setLoggedIn(MainActivity.this, false);
            MySharedPreferences.getInstance().clearUserId(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
    };

    public void setTittle(String text) {
        activityMainBinding.iTollbar.tvTitle.setText(text);
    }

    public void setMenuVisibilty(boolean menuVisibilty) {

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_back_arrow);

        if (menuVisibilty) {

            activityMainBinding.iTollbar.menuDrawable.setVisibility(View.VISIBLE);

            if (getSupportActionBar() != null) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

            }

        } else {

            activityMainBinding.iTollbar.menuDrawable.setVisibility(View.GONE);

            if (getSupportActionBar() != null) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(false);

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if(!timerStarted)
//            notifyPauseOrStopTimerToTheServer(Constants.PAUSE);

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(!timerStoppedAfterSavingDetails)
//        {
//            if(timerStarted)
//            {
//                if(!timerAlreadyPaused)
//                initializeTimerAtServer(Constants.RESUME);
//
//            }
//
//        }

    }

    private void openCongratulationsDialog(Tip data) {

        String heading = "Congratulations " + data.getCount() + " day Streak";
        String tip = "Tip of the day: " + data.getTip();
        final Dialog dialogCongratualtion = new Dialog(this);
        dialogCongratualtion.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCongratualtion.setContentView(R.layout.dialog_congratulation);
        dialogCongratualtion.setCanceledOnTouchOutside(false);
        dialogCongratualtion.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogCongratualtion.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        TextView mTvOk = dialogCongratualtion.findViewById(R.id.tvCancel);
        TextView mTvTipHeading = dialogCongratualtion.findViewById(R.id.tvTipHeading);
        TextView mTvTip = dialogCongratualtion.findViewById(R.id.tvTip);
        mTvTipHeading.setText(heading);
        mTvTip.setText(tip);

        mTvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethods.getInstance().saveDialogSeenState(MainActivity.this, user_id);

                dialogCongratualtion.cancel();

            }
        });

        dialogCongratualtion.show();
    }

    @Override
    public void onPressed(ChordFragments fragment) {
        fragment.showDialog(this);
    }

}
