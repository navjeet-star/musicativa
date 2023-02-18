package com.developer.musicatiiva.activities;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.commonClasses.Validations;
import com.developer.musicatiiva.databinding.ActivityForgotPasswordBinding;
import com.developer.musicatiiva.models.ForgottPassword;
import com.developer.musicatiiva.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.BASE_URL;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    String email;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        binding.setData(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    }

    public void onEnterCliked() {

        new CommonMethods.InternetCheck(ForgotPasswordActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    email = binding.etEnterFogotPassowrd.getText().toString().trim();

                    if (!Validations.isEmpty(email)) {
                        binding.etEnterFogotPassowrd.setError("Please enter Email");
                    } else if (!Validations.isValidEmail(email)) {
                        binding.etEnterFogotPassowrd.setError("Please enter valid Eamil Pattern");
                    } else {
                        Log.d("mohit", "accept: email : " + email);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(Constants.EMAIL, email);
                        CommonDialogs.getInstance().showProgressDialog(ForgotPasswordActivity.this);
                        Call<ForgottPassword> call = jsonPlaceHolderApi.getOtpSendAcknowledgement(jsonObject);
                        call.enqueue(new Callback<ForgottPassword>() {
                            @Override
                            public void onResponse(Call<ForgottPassword> call, Response<ForgottPassword> response) {
                                if (response.isSuccessful()) {
                                    ForgottPassword forgottPassword = response.body();
                                    String description = forgottPassword.getDescription();
                                    int status_code = forgottPassword.getStatus_code();
                                    if (status_code == 1) {

                                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent to your mail..!!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ForgotPasswordActivity.this, EnterOtpActivity.class);
                                        startActivity(intent);
                                        // finish();
                                    } else {
                                        Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed..!!", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Log.d("mohit", "onResponse: " + response.message());
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed..!!!", Toast.LENGTH_SHORT).show();

                                }
                                CommonDialogs.getInstance().dismissProgressDialog();


                            }

                            @Override
                            public void onFailure(Call<ForgottPassword> call, Throwable t) {
                                Log.d("mohit", "Error code: " + t.getMessage());
                                Toast.makeText(ForgotPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();
                            }
                        });



                        /*JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(Constants.EMAIL, email);


                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        Call<ForgotPassword> call = apiService.getForgotPassword(jsonObject);
                        CommonDialogs.getInstance().showProgressDialog(ForgotPasswordActivity.this);

                        call.enqueue(new Callback<ForgotPassword>() {
                            @Override
                            public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                                ForgotPassword forgotPassword = response.body();
                                CommonDialogs.getInstance().dismissProgressDialog();
                                if (forgotPassword != null) {
                                    String desciption;

                                    if(forgotPassword.getStatusCode().equals("1"))
                                    {
                                        desciption=forgotPassword.getDescription();
                                        Toast.makeText(ForgotPasswordActivity.this, desciption, Toast.LENGTH_SHORT).show();

                                        Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if(forgotPassword.getStatusCode().equals("0"))
                                    {
                                        desciption=forgotPassword.getDescription();
                                        Toast.makeText(ForgotPasswordActivity.this, desciption, Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }

                            @Override
                            public void onFailure(Call<ForgotPassword> call, Throwable t) {
                                CommonDialogs.getInstance().dismissProgressDialog();
                                Toast.makeText(ForgotPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });*/
                    }
                }
            }
        });
    }

    public void onBackCLicked() {
        finish();
    }
}
