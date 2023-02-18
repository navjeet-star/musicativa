package com.developer.musicatiiva.activities;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.commonClasses.Validations;
import com.developer.musicatiiva.databinding.ActivitySetReminderBinding;
import com.developer.musicatiiva.models.AddReminder;
import com.developer.musicatiiva.models.DeleteReminder;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetReminderActivity extends AppCompatActivity {

    private String format = "";
    ActivitySetReminderBinding setReminderBinding;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    String id;
    String title;
    String reminder_at;
    String frequency;
    String timezone;
    String type;
    Boolean aBoolean = true;
    private int mSelection = 0;

    String strFrequency;
    String strM,strT,strW,strTh,strF,strS,strSu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setReminderBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_reminder);
        setReminderBinding.setData(this);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(INSTRUMENT_DATA_URL).addConverterFactory(GsonConverterFactory.create()).build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        initlization();
        onclick();
    }

    @SuppressLint("ResourceAsColor")
    private void initlization() {
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        reminder_at = getIntent().getStringExtra("reminder_at");
        frequency = getIntent().getStringExtra("frequency");
        timezone = getIntent().getStringExtra("timezone");
        type = getIntent().getStringExtra("type");
        if (type != null && !type.isEmpty()) {
            if (type.equals("adpter")) {
                setReminderBinding.imgDelete.setVisibility(View.VISIBLE);
            } else {
                setReminderBinding.imgDelete.setVisibility(View.GONE);
            }
        }
        if (frequency != null && !frequency.isEmpty()) {
            if (frequency.equals("f")) {
                setReminderBinding.txtF.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtF.setTextColor(R.color.colorWhite);
            }
        }

        setReminderBinding.edReminder.setText(title);
        setReminderBinding.txtTimeset.setText(reminder_at);
    }

    private void onclick() {
        setReminderBinding.txtTimeset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSelcte();
            }
        });

        setReminderBinding.txtM.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtM.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtM.setTextColor(R.color.colorWhite);

                strM="monday";


            }
        });
        setReminderBinding.txtT.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtT.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtT.setTextColor(R.color.colorWhite);

                strT="tuesday";
            }
        });
        setReminderBinding.txtW.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                setReminderBinding.txtW.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtW.setTextColor(R.color.colorWhite);

                strW="wednesday";

            }
        });
        setReminderBinding.txtTh.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtTh.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtTh.setTextColor(R.color.colorWhite);

                strTh="thursday";


            }
        });

        setReminderBinding.txtF.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtF.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtF.setTextColor(R.color.colorWhite);

                strF="friday";

            }
        });

        setReminderBinding.txtS.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtS.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtS.setTextColor(R.color.colorWhite);
                strS="saturday";
            }
        });

        setReminderBinding.txtSu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                setReminderBinding.txtSu.setBackgroundResource(R.drawable.btn_background);
                setReminderBinding.txtSu.setTextColor(R.color.colorWhite);

                strSu="sunday";

            }
        });
        setReminderBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strreminder = setReminderBinding.edReminder.getText().toString();
                String strtime = setReminderBinding.txtTimeset.getText().toString();

                if (!Validations.isEmpty(strreminder)) {

                    setReminderBinding.edReminder.setError("Please enter title");

                } else if (!Validations.isEmpty(strtime)) {

                    setReminderBinding.txtTimeset.setError("Please select the time");

                } else {

                    if (type != null && !type.isEmpty()) {

                        if (type.equals("adpter")) {

                            updateReminder();

                        }

                    } else {

                        setReminder();

                    }
                }
            }
        });

        setReminderBinding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
        setReminderBinding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              CommonDialogs.getInstance().showMessage2ButtonsDialog(SetReminderActivity.this, "Do you really want to Delete the Reminder ?", no, yes);

            }
        });
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
            deleteReminder();
            finish();

        }
    };

    private void timeSelcte() {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SetReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

            setReminderBinding.txtTimeset.setText(selectedHour + ":" + selectedMinute);

            }

        },hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void setReminder() {

        strFrequency=strM+","+strT+","+strW+","+strTh+","+strF+","+strS+","+strSu;

        new CommonMethods.InternetCheck(SetReminderActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    JsonObject jsonObject = new JsonObject();
                    Calendar cal = Calendar.getInstance();
                    String timezone = cal.getTimeZone().getID();
                    int user_id = MySharedPreferences.getInstance().getUserID(SetReminderActivity.this, Constants.USERID);
                    jsonObject.addProperty("user_id", user_id);
                    jsonObject.addProperty("title", setReminderBinding.edReminder.getText().toString());
                    jsonObject.addProperty("reminder_at",setReminderBinding.txtTimeset.getText().toString() );
                    jsonObject.addProperty("frequency", strFrequency);
                    jsonObject.addProperty("timezone", timezone);
                    CommonDialogs.getInstance().showProgressDialog(SetReminderActivity.this);
                    Call<AddReminder> call = jsonPlaceHolderApi.addReminder(jsonObject);

                    call.enqueue(new Callback<AddReminder>() {
                        @Override
                        public void onResponse(Call<AddReminder> call, Response<AddReminder> response) {
                            if (response.isSuccessful()) {
                                AddReminder getReminder = response.body();

                                String description = getReminder.getDescription();
                                int status_code = getReminder.getStatus_code();

                                if (status_code == 1) {

                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {

                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(SetReminderActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<AddReminder> call, Throwable t) {

                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(SetReminderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }
                    });
                }
            }
        });
    }
    private void updateReminder() {

        new CommonMethods.InternetCheck(SetReminderActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    JsonObject jsonObject = new JsonObject();
                    Calendar cal = Calendar.getInstance();
                    String timezone = cal.getTimeZone().getID();

                    jsonObject.addProperty("id", id);
                    jsonObject.addProperty("title", setReminderBinding.edReminder.getText().toString());
                    jsonObject.addProperty("reminder_at", setReminderBinding.txtTimeset.getText().toString());
                    jsonObject.addProperty("frequency", setReminderBinding.txtM.getText().toString());
                    jsonObject.addProperty("timezone", timezone);
                    CommonDialogs.getInstance().showProgressDialog(SetReminderActivity.this);
                    Call<AddReminder> call = jsonPlaceHolderApi.updateReminder(jsonObject);
                    call.enqueue(new Callback<AddReminder>() {
                        @Override
                        public void onResponse(Call<AddReminder> call, Response<AddReminder> response) {
                            if (response.isSuccessful()) {
                                AddReminder getReminder = response.body();

                                String description = getReminder.getDescription();
                                int status_code = getReminder.getStatus_code();

                                if (status_code == 1) {

                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {

                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(SetReminderActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<AddReminder> call, Throwable t) {

                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(SetReminderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }
                    });
                }
            }
        });
    }
    private void deleteReminder() {

        new CommonMethods.InternetCheck(SetReminderActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", id);
                    CommonDialogs.getInstance().showProgressDialog(SetReminderActivity.this);
                    Call<DeleteReminder> call = jsonPlaceHolderApi.deleteReminder(jsonObject);
                    call.enqueue(new Callback<DeleteReminder>() {
                        @Override
                        public void onResponse(Call<DeleteReminder> call, Response<DeleteReminder> response) {
                            if (response.isSuccessful()) {
                                DeleteReminder deleteReminder = response.body();

                                String description = deleteReminder.getDescription();
                                int status_code = deleteReminder.getStatus_code();

                                if (status_code == 1) {
                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(SetReminderActivity.this, description, Toast.LENGTH_SHORT).show();

                                }

                            } else {

                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(SetReminderActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<DeleteReminder> call, Throwable t) {

                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(SetReminderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                        }
                    });
                }
            }
        });
    }
}


