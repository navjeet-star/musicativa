package com.developer.musicatiiva.activities;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.ApiClient;
import com.developer.musicatiiva.apiModels.ApiInterface;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.models.ResultModel;
import com.developer.musicatiiva.models.SubscriptionUser;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    Context context;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context=this;
        if  (!MySharedPreferences.getInstance().getLoggedIn(context)){
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    // getPracticeData();

                }

            }, 3000);
        }
        else
            callNormally();

    }

    //

    private void callNormally() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(INSTRUMENT_DATA_URL).addConverterFactory(GsonConverterFactory.create()).build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!MySharedPreferences.getInstance().getLoggedIn(SplashActivity.this)) {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                     //getPracticeData();

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }

        }, 200);
    }

    private void getPracticeData() {

        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject = new JsonObject();
                    final int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
 //                    jsonObject.addProperty(Constants.USER_ID, user_id);'
                    jsonObject.addProperty(Constants.USER_ID,"12");
                    Call<SubscriptionUser> call = jsonPlaceHolderApi.getSubscription(jsonObject);
                    call.enqueue(new Callback<SubscriptionUser>() {
                        @Override
                        public void onResponse(@NonNull Call<SubscriptionUser> call, Response<SubscriptionUser> response) {
                            if (response.isSuccessful()) {
                                if(response.body()!=null){

                                     SubscriptionUser subsriptionResponse = response.body();
                                    int status_code = subsriptionResponse.getStatus();
                                    Boolean desc = subsriptionResponse.getSubscribed();


                                    String str1 = subsriptionResponse.getSubscribed().toString();

                                    if (status_code == 200) {

                                        startActivity(new Intent(SplashActivity.this,MainActivity.class));

                                    } else {

                                         Toast.makeText(context, str1, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                else {

                                     Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                }

                             }

                             CommonDialogs.getInstance().dismissProgressDialog();

                        }

                        @Override
                        public void onFailure(Call<SubscriptionUser> call, Throwable t) {


                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();

                        }
                    });
                }
            }
        });
    }
}
