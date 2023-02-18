package com.developer.musicatiiva.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.musicatiiva.KeyboardVisibilityListener;
import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.activities.MetronomeTestActivity;
import com.developer.musicatiiva.activities.RecordingActivity;
import com.developer.musicatiiva.adapters.AudioAdapter;
import com.developer.musicatiiva.adapters.InstrumentAdapters;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentChordBinding;
import com.developer.musicatiiva.models.Practice;
import com.developer.musicatiiva.models.PracticeData;
import com.developer.musicatiiva.recording.util.Constantsss;
import com.developer.musicatiiva.recording.util.Utilities;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.KeyboardUtil;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

import java.io.File;
import java.util.ArrayList;

public class ChordFragments extends Fragment implements KeyboardVisibilityListener {

    FragmentChordBinding fragmentChordBinding;
    View view;
    Context context;
    public int timing = 0;
    InstrumentAdapters instrumentAdapters;
    AudioAdapter audioAdapter;
    String instrumentName;
    public boolean isOpen = false;
    String instumentImageUrl;

    String last_practiced_date;
    String practice_today;
    String activity_title;
    int practice_id;
    int practiced;
    int sub_id;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    int count = 1;

    void getBackPressed() {

        ((MainActivity) getActivity()).activityMainBinding.iTollbar.menuDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             Toast.makeText(requireContext(), "Hey Clciked", Toast.LENGTH_SHORT).show();


            }
        });

        fragmentChordBinding.textViewCounter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    showDialog(getActivity());
                    hideKeyboard(getActivity());

                    return true;
                }
                return false;
            }
        });

     /*   ( (MainActivity)getActivity()).activityMainBinding.iTollbar.menuDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if()
            }
        });*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {

            context = getActivity();
            instrumentName = getArguments().getString(Constants.INSTRUMENT_NAME);
            timing = getArguments().getInt("KeyboardTime");
            instumentImageUrl = getArguments().getString(Constants.INSTRUMENT_IMAGE_URL);
            sub_id = getArguments().getInt(Constants.SUB_CAT);
            // practiced=getArguments().getInt(Constants.PRACTICED);
            // last_practiced_date=getArguments().getString(Constants.LAST_PRACTICED_DATE);
            //practice_today=getArguments().getString(Constants.PRACTICE_TODAY);

            getBackPressed();

            activity_title = getArguments().getString(Constants.ACTIVITY_TITLE);
            ((MainActivity) context).activityTitle = activity_title;

            Retrofit retrofit = new Retrofit.Builder().baseUrl(INSTRUMENT_DATA_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();
            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

            KeyboardUtil.setKeyboardVisibilityListener(requireActivity(), this::onKeyboardVisibilityChanged);


            if (getActivity() != null) {

                ((MainActivity) context).setTittle("Activity");
                ((MainActivity) context).setMenuVisibilty(false);
                ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
                ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);
                ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.VISIBLE);
                ((MainActivity) context).category_id = sub_id;
            }

            Glide.with(context).load(instumentImageUrl).into(fragmentChordBinding.imageViewInstrumentLogo);
            // String sourceString = instrumentName+" > "+"<b>" + "Chord" + "</b> ";
            //  fragmentChordBinding.textViewInstrumentName.setText(Html.fromHtml(sourceString));
            fragmentChordBinding.textViewHeading.setText(instrumentName + "/" + activity_title);

            getPracticeData();

            // fragmentChordBinding.textViewCounter.setText(practice_today);

            //fragmentChordBinding.textViewNoOfTimesPractice.setText(practiced+" times");

            //fragmentChordBinding.textViewLastPracticedDate.setText(last_practiced_date+", ");


            fragmentChordBinding.textViewCounter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        showDialog(getActivity());
                        hideKeyboard(getActivity());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        fragmentChordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chord, container, false);
        view = fragmentChordBinding.getRoot();
        fragmentChordBinding.setData(this);

        return view;

    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void showDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_confirm);

        TextView dialogButton = (TextView) dialog.findViewById(R.id.btn_no);
        TextView txt_dia = (TextView) dialog.findViewById(R.id.txt_dia);
        String counterValue = fragmentChordBinding.textViewCounter.getText().toString();

        txt_dia.setText("Do you want to update the activity counter to" + " " + counterValue);


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
        if (counterValue.equals("")) {

            Toast.makeText(requireContext(), "Please enter value", Toast.LENGTH_SHORT).show();
        } else {
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
                        @Override
                        public void accept(Boolean internet) {
                            if (internet) {

                                CommonDialogs.getInstance().showProgressDialog(context);
                                JsonObject jsonObject = new JsonObject();
                                int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                                int timerStatus = ((MainActivity) context).timerStarted ? 0 : 1;

                                String currentDate = CommonMethods.getInstance().getCurrentDate();
                                jsonObject.addProperty(Constants.USER_ID, user_id);
                                jsonObject.addProperty(Constants.SUB_CAT, sub_id);
                                jsonObject.addProperty(Constants.TEMPO, 0);
                                jsonObject.addProperty(Constants.DATE, currentDate);
                                jsonObject.addProperty(Constants.TIMER_STATUS, timerStatus);
                                jsonObject.addProperty(Constants.TITLE, activity_title);
                                jsonObject.addProperty(Constants.PRACTICE_ID, practice_id);
                                jsonObject.addProperty(Constants.COUNTER, fragmentChordBinding.textViewCounter.getText().toString());

                                Call<Practice> call = jsonPlaceHolderApi.getImanualPractice(jsonObject);
                                call.enqueue(new Callback<Practice>() {
                                    @Override
                                    public void onResponse(Call<Practice> call, Response<Practice> response) {
                                        if (response.isSuccessful()) {
                                            Practice practice = response.body();

                                            PracticeData practiceData = practice.getData();
                                            String desc = practice.getDescription();
                                            String last_practiced_date = practiceData.getLast_practice_date();
                                            int status_code = practice.getStatus_code();


                                            if (status_code == 1) {
                                                // finish();
                                                Log.d("prtactic", practiceData.getPractice_today().toString());

                                                fragmentChordBinding.textViewCounter.setText(practiceData.getPractice_today() + "");
                                                fragmentChordBinding.textViewLastPracticedDate.setText(last_practiced_date + ", ");
                                                fragmentChordBinding.textViewNoOfTimesPractice.setText(practiceData.getPracticed() + " times");

                                            } else {

                                                Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + desc);
                                                Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.d("mohit", "onResponse: " + response.message());
                                            Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();

                                        }
                                        CommonDialogs.getInstance().dismissProgressDialog();
                                    }

                                    @Override
                                    public void onFailure(Call<Practice> call, Throwable t) {
                                        Log.d("mohit", "Error code: " + t.getMessage());
                                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        CommonDialogs.getInstance().dismissProgressDialog();
                                    }
                                });

                            }
                        }
                    });


                }
            });

        }
        dialog.show();
    }

    private void getPracticeData() {

        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject = new JsonObject();
                    final int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    String currentDate = CommonMethods.getInstance().getCurrentDate();
                     jsonObject.addProperty(Constants.USER_ID, user_id);
                    jsonObject.addProperty(Constants.SUB_CAT, sub_id);
                    jsonObject.addProperty(Constants.DATE, currentDate);
                    jsonObject.addProperty(Constants.TITLE, activity_title);
                     Call<Practice> call = jsonPlaceHolderApi.getPracticeeData(jsonObject);
                    call.enqueue(new Callback<Practice>() {
                        @Override
                        public void onResponse(Call<Practice> call, Response<Practice> response) {

                            if (response.isSuccessful()) {
                                Practice practice = response.body();
                                PracticeData practiceData = practice.getData();
                                String desc = practice.getDescription();
                                //String last_practiced_date=practiceData.getLast_practice_date();
                                int status_code = practice.getStatus_code();


                                if (status_code == 1) {
                                    if (practiceData != null) {
                                        practiced = practiceData.getPracticed();
                                        practice_today = practiceData.getPractice_today();
                                        last_practiced_date = practiceData.getLast_practice_date();
                                        practice_id = practiceData.getPractice_id();
                                        ((MainActivity) context).practice_id = practice_id;

                                        fragmentChordBinding.textViewNoOfTimesPractice.setText(practiceData.getPracticed() + " times");
                                        fragmentChordBinding.textViewCounter.setText(practiceData.getPractice_today());
                                        fragmentChordBinding.textViewLastPracticedDate.setText(practiceData.getLast_practice_date() + ", ");

                                    }
                                } else {
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + desc);
                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<Practice> call, Throwable t) {

                            Log.d("mohit", "Error code: " + t.getMessage());

                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }
                    });
                }
            }
        });
    }

    public void onCrossClicked() {

        ((MainActivity) context).onBackPressed();

    }

    public void onLinear() {

        showDialog(getActivity());

    }

    public void onMinusClicked() {
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {

                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject = new JsonObject();
                    int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);

                    int timerStatus = ((MainActivity) context).timerStarted ? 0 : 1;

                    String currentDate = CommonMethods.getInstance().getCurrentDate();

                    jsonObject.addProperty(Constants.USER_ID, user_id);
                    jsonObject.addProperty(Constants.SUB_CAT, sub_id);
                    jsonObject.addProperty(Constants.TEMPO, 0);
                    jsonObject.addProperty(Constants.DATE, currentDate);
                    jsonObject.addProperty(Constants.TIMER_STATUS, timerStatus);
                    jsonObject.addProperty(Constants.TITLE, activity_title);
                    jsonObject.addProperty(Constants.PRACTICE_ID, practice_id);

                    Call<Practice> call = jsonPlaceHolderApi.getDecrementedPracticeData(jsonObject);
                    call.enqueue(new Callback<Practice>() {
                        @Override
                        public void onResponse(Call<Practice> call, Response<Practice> response) {
                            if (response.isSuccessful()) {
                                String counterValue = fragmentChordBinding.textViewCounter.getText().toString();
                                int count = Integer.parseInt(counterValue);
                                if (count > 1) {
                                    Practice practice = response.body();

                                    PracticeData practiceData = practice.getData();
                                    String desc = practice.getDescription();
                                    String last_practiced_date = practiceData.getLast_practice_date();
                                    int status_code = practice.getStatus_code();


                                    if (status_code == 1) {
                                        // finish();

                                        fragmentChordBinding.textViewCounter.setText(practiceData.getPractice_today() + "");
                                        fragmentChordBinding.textViewLastPracticedDate.setText(last_practiced_date + ", ");
                                        fragmentChordBinding.textViewNoOfTimesPractice.setText(practiceData.getPracticed() + " times");

                                    } else {
                                        Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + desc);
                                        Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    fragmentChordBinding.textViewCounter.setText("0");
                                    fragmentChordBinding.textViewNoOfTimesPractice.setText(0 + " times");
                                }
                            } else {
                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<Practice> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }
                    });
                }
            }
        });
    }

    public void onPlusClicked() {

        CommonDialogs.getInstance().showMessageYesNo(context, "Do you want to record the audio", new CommonDialogs.OnClick() {
            @Override
            public void onClick(int pos, String data) {
                    if(data.equals("yes")){

                        Intent intent=new Intent(requireContext(), RecordingActivity.class);
                        startActivity(intent);

                    } else if (data.equals("no")) {


                    }
            }
        });

//        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
//            @Override
//            public void accept(Boolean internet) {
//                if (internet) {
//
//                    CommonDialogs.getInstance().showProgressDialog(context);
//                    JsonObject jsonObject = new JsonObject();
//                    int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
//                    int timerStatus = ((MainActivity) context).timerStarted ? 0 : 1;
//
//                    String currentDate = CommonMethods.getInstance().getCurrentDate();
//                    jsonObject.addProperty(Constants.USER_ID, user_id);
//                    jsonObject.addProperty(Constants.SUB_CAT, sub_id);
//                    jsonObject.addProperty(Constants.TEMPO, 0);
//                    jsonObject.addProperty(Constants.DATE, currentDate);
//                    jsonObject.addProperty(Constants.TIMER_STATUS, timerStatus);
//                    jsonObject.addProperty(Constants.TITLE, activity_title);
//                    jsonObject.addProperty(Constants.PRACTICE_ID, practice_id);
//
//                    Call<Practice> call = jsonPlaceHolderApi.getIncrementedPracticeData(jsonObject);
//                    call.enqueue(new Callback<Practice>() {
//                        @Override
//                        public void onResponse(Call<Practice> call, Response<Practice> response) {
//                            if (response.isSuccessful()) {
//                                Practice practice = response.body();
//
//                                PracticeData practiceData = practice.getData();
//                                String desc = practice.getDescription();
//                                String last_practiced_date = practiceData.getLast_practice_date();
//                                int status_code = practice.getStatus_code();
//
//
//                                if (status_code == 1) {
//                                    // finish();
//                                    Log.d("prtactic", practiceData.getPractice_today().toString());
//
//                                    fragmentChordBinding.textViewCounter.setText(practiceData.getPractice_today() + "");
//                                    fragmentChordBinding.textViewLastPracticedDate.setText(last_practiced_date + ", ");
//                                    fragmentChordBinding.textViewNoOfTimesPractice.setText(practiceData.getPracticed() + " times");
//
//                                } else {
//
//                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + desc);
//                                    Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.d("mohit", "onResponse: " + response.message());
//                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
//
//                            }
//                            CommonDialogs.getInstance().dismissProgressDialog();
//                        }
//
//                        @Override
//                        public void onFailure(Call<Practice> call, Throwable t) {
//                            Log.d("mohit", "Error code: " + t.getMessage());
//                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
//                            CommonDialogs.getInstance().dismissProgressDialog();
//                        }
//                    });
//
//                }
//            }
//        });
    }

    public void onSetTempoClicked() {

        Intent intent = new Intent(getActivity(), MetronomeTestActivity.class);
        startActivity(intent);
    }


    @Override
    public void onKeyboardVisibilityChanged(boolean keyboardVisible) {
        isOpen = keyboardVisible;
        if (keyboardVisible == true) {

            fragmentChordBinding.btnPlus.setClickable(false);
            fragmentChordBinding.imgCross.setClickable(false);
            fragmentChordBinding.btnMinus.setClickable(false);
            fragmentChordBinding.btnTransparent.setClickable(true);
            ((MainActivity) context).activityMainBinding.iTollbar.tvStartCounter.setClickable(false);

        } else {
            fragmentChordBinding.btnPlus.setClickable(true);
            fragmentChordBinding.imgCross.setClickable(true);
            fragmentChordBinding.btnMinus.setClickable(true);
            fragmentChordBinding.btnTransparent.setClickable(false);
            ((MainActivity) context).activityMainBinding.iTollbar.tvStartCounter.setClickable(true);

        }
    }

    private void initComponents(){
        final ArrayList<String> FilesInFolder = GetFiles(Utilities.getDataFolder(Constantsss.APP_FOLDER_NAME) + "/");

        // set up the RecyclerView
        fragmentChordBinding.listRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        audioAdapter = new AudioAdapter(requireContext(),FilesInFolder);
        fragmentChordBinding.listRV.setAdapter(audioAdapter);



//        Log.d("callinggggttttt",Utilities.getDataFolder(Constants.APP_FOLDER_NAME) + "/"+ FilesInFolder.get(3));
//        if (FilesInFolder != null) {
//            lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FilesInFolder));
//
//            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                    MediaPlayer m = new MediaPlayer();
//                    try {
//                        m.setDataSource(Utilities.getDataFolder(Constants.APP_FOLDER_NAME) + "/" + FilesInFolder.get(position));
//                        m.prepare();
//                        m.start();
//                        Toast.makeText(requireContext(), "Playing audio", Toast.LENGTH_LONG).show();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer m) {
//                            m.release();
//                        }
//                    });
//                }
//            });
//        }
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<>();

        File f = new File(DirectoryPath);
        f.mkdirs();
        File[] files = f.listFiles();

        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }
        return MyFiles;

    }

}
