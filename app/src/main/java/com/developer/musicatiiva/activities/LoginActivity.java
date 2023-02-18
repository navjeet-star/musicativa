package com.developer.musicatiiva.activities;

import android.content.Intent;
import android.graphics.Paint;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.ApiClient;
import com.developer.musicatiiva.apiModels.ApiInterface;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.commonClasses.Validations;
import com.developer.musicatiiva.databinding.ActivityLoginBinding;
import com.developer.musicatiiva.models.AddDeviceResponse;
import com.developer.musicatiiva.models.ResultModel;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;
import static com.developer.musicatiiva.utils.Constants.UID;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    private String device_token = "";
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_login);

        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.setData(this);
        getDeviceToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(INSTRUMENT_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        setData();

    }

    private void setData() {

        activityLoginBinding.textviewSignup.setPaintFlags(activityLoginBinding.textviewSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    public void onSignInCliked() {

        new CommonMethods.InternetCheck(LoginActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {

                    String loginEmail = activityLoginBinding.etEmail.getText().toString();
                    String loginpassword = activityLoginBinding.etPassword.getText().toString();

                    if (!Validations.isEmpty(loginEmail)) {
                        activityLoginBinding.etEmail.setError("Please enter Email");
                    } else if (!Validations.isValidEmail(loginEmail)) {
                        activityLoginBinding.etEmail.setError("Please Enter Valid Email Pattern");
                    } else if (!Validations.isEmpty(loginpassword)) {
                        activityLoginBinding.etPassword.setError("Please enter Password");

                    } else {

                        JsonObject object = new JsonObject();
                        object.addProperty(Constants.EMAIL, loginEmail);
                        object.addProperty(Constants.PASSWORD, loginpassword);

                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<JsonObject> call = apiService.getLoginData(object);
                        CommonDialogs.getInstance().showProgressDialog(LoginActivity.this);

                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject jsonObject = response.body();

                                CommonDialogs.getInstance().dismissProgressDialog();

                                if (jsonObject != null) {

                                    if (jsonObject.get("status_code").getAsString().equals("1")) {

                                        String description = jsonObject.get("description").getAsString();
                                        JsonObject jsonData = jsonObject.get("data").getAsJsonObject();
                                         String user_id = jsonData.get("user_id").getAsString();

                                        MySharedPreferences.getInstance().saveUserId(LoginActivity.this, Constants.USERID, Integer.parseInt(user_id));
                                        MySharedPreferences.getInstance().setLoggedIn(LoginActivity.this, true);
                                        sendTokenToServer();

                                    } else if (jsonObject.get("status_code").getAsString().equals("0")) {

                                        String description = jsonObject.get("description").getAsString();
                                        Toast.makeText(LoginActivity.this, "Login failed..!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                CommonDialogs.getInstance().dismissProgressDialog();
                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });
    }

    public void onSignupClicked() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    public void onForgotPasswordCliked() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(this, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                 device_token = task.getResult().toString();
            }
        });
    }


    private void sendTokenToServer() {
        new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(LoginActivity.this);
                    JsonObject jsonObject = new JsonObject();

                    int user_id = MySharedPreferences.getInstance().getUserID(LoginActivity.this, Constants.USERID);

                    jsonObject.addProperty(Constants.DEVICE_TOKEN, device_token);
                    jsonObject.addProperty(Constants.DEVICE_TYPE, "Android");
                    jsonObject.addProperty(Constants.UID, user_id);

                    Log.d("calinhgserrtotoken",jsonObject.toString());

                    Call<AddDeviceResponse> call = jsonPlaceHolderApi.addDevice(jsonObject);
                    call.enqueue(new Callback<AddDeviceResponse>() {
                        @Override
                        public void onResponse(Call<AddDeviceResponse> call, Response<AddDeviceResponse> response) {
                            if (response.isSuccessful()) {
                                AddDeviceResponse addDeviceResponse = response.body();

                                String description = addDeviceResponse.getDescription();
                                String status_codee = addDeviceResponse.getStatusCode();

                                int status_code = Integer.parseInt(status_codee);

                                if (status_code == 1) {

                                  {
                                        Toast.makeText(LoginActivity.this, "You have successfully logged in..!!!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                       /* Toast.makeText(LoginActivity.this, "You have successfully logged in..!!!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();


*/

                                    }

                                    // finish();

                                } else {
                                      Toast.makeText(LoginActivity.this,"Unable to login due to incorrect username or password",Toast.LENGTH_LONG).show();
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);

                                    //   Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }

                            } else {
                                Toast.makeText(LoginActivity.this,"Unable to login due to incorrect username or password",Toast.LENGTH_LONG).show();
                                Log.d("mohit", "onResponse: " + response.message());

                                // Toast.makeText(LoginActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();

                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());

                            }

                            CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<AddDeviceResponse> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            //Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();

                            // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);


                            //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(t.getMessage());

                        }
                    });
                }
            }
        });
    }
}



