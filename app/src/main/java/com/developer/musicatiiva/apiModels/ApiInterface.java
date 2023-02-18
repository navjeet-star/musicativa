package com.developer.musicatiiva.apiModels;
import com.developer.musicatiiva.models.RegisterData;
import com.developer.musicatiiva.models.forgotPassword.ForgotPassword;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by brst-pc80 on 20/09/19.
 */

public interface ApiInterface {


    @POST("users/login")
    Call<JsonObject> getLoginData(@Body JsonObject jsonObject);

    @POST("users/register")
    Call<RegisterData> getRegisterData(@Body JsonObject jsonObject);

    @POST("users/forgot-password")
    Call<ForgotPassword> getForgotPassword(@Body JsonObject jsonObject);
    @POST("subscriptions/subscription-check")
    Call<JsonObject>checkTheSubscription(@Body JsonObject jsonObject);
@POST("subscriptions/subscription-create")
    Call<JsonObject>create(@Body JsonObject jsonObject);


}
