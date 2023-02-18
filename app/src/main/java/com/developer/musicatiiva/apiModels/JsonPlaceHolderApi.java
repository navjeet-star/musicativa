package com.developer.musicatiiva.apiModels;

import com.developer.musicatiiva.models.ActivityResponse;
import com.developer.musicatiiva.models.AddDeviceResponse;
import com.developer.musicatiiva.models.AddSubInstrument;
import com.developer.musicatiiva.models.AddTimerResponse;
import com.developer.musicatiiva.models.ChangeName;
import com.developer.musicatiiva.models.ChangePassword;
import com.developer.musicatiiva.models.DeleteReminder;
import com.developer.musicatiiva.models.DeleteSubInstrumentResponse;
import com.developer.musicatiiva.models.ForgottPassword;
import com.developer.musicatiiva.models.GetReminderList;
import com.developer.musicatiiva.models.InstrumentsList;
import com.developer.musicatiiva.models.OtpMatch;
import com.developer.musicatiiva.models.PauseOrStopTimerResponse;
import com.developer.musicatiiva.models.Practice;
import com.developer.musicatiiva.models.Profile;
import com.developer.musicatiiva.models.SaveCommentsResponse;
import com.developer.musicatiiva.models.SubInstrActivitiesResponse;
import com.developer.musicatiiva.models.SubscriptionUser;
import com.developer.musicatiiva.models.TipResponse;
import com.developer.musicatiiva.models.AddReminder;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface JsonPlaceHolderApi {

   /*@POST("forgot-password")
   Call<ForgottPassword> getOtpSendAcknowledgement(@Query("email") String email);*/

   @POST("forgot-password")
   Call<ForgottPassword> getOtpSendAcknowledgement(@Body JsonObject jsonObject);

   @POST("otp-match")
   Call<OtpMatch> getOtpMatchAcknowledgement(@Body JsonObject jsonObject);

//   @POST("otp-match")
//   Call<OtpMatch> getOtpMatchAcknowledgement(
//   @Query("otp") Integer otp);

   @POST("change-password")
   Call<ChangePassword> getPasswordChangeAcknowledgement(@Body JsonObject jsonObject);

   @GET("instruments/instruments-data")
   Call<InstrumentsList> getInstrumentData();

   @POST("reminder-sets/get-reminder")
   Call<GetReminderList> getReminder(@Body JsonObject jsonObject);

   @POST("reminder-sets/set-reminder")
   Call<AddReminder> addReminder(@Body JsonObject jsonObject);

   @POST("reminder-sets/update-reminder")
   Call<AddReminder> updateReminder(@Body JsonObject jsonObject);

   @POST("reminder-sets/delete-reminder")
   Call<DeleteReminder> deleteReminder(@Body JsonObject jsonObject);

   @POST("instruments/sub-instruments-data")
   Call<InstrumentsList> getSubInstrumentData(@Body JsonObject jsonObject);

   @POST("practices/add-practice")
   Call<Practice> getIncrementedPracticeData(@Body JsonObject jsonObject);

   @POST("practices/manual-practice")
   Call<Practice> getImanualPractice(@Body JsonObject jsonObject);

   @POST("practices/subtract-practice")
   Call<Practice> getDecrementedPracticeData(@Body JsonObject jsonObject);

   @POST("practices/practice-data")
   Call<Practice> getPracticeeData(@Body JsonObject jsonObject);

   @POST("subscriptions/subscription-check")
   Call<SubscriptionUser> getSubscription(@Body JsonObject jsonObject);

   @POST("profile")
   Call<Profile> getProfileData(@Body JsonObject jsonObject);

   @POST("sub-instrument-activity")
   //@HTTP(method = "GET", path = "sub-instrument-activity", hasBody = true)
   Call<SubInstrActivitiesResponse> getSubInstrumentActivityData(@Body JsonObject jsonObject);

   //

   @POST("change-name")
   Call<ChangeName> getChangeNameAcknowledgement(@Body JsonObject jsonObject);
   //
   @POST("reset-password")
   Call<ChangePassword> getChangePasswordAcknowledgement(@Body JsonObject jsonObject);

   @Multipart
   @POST("instruments/add-sub-instruments")
   Call<AddSubInstrument> addSubInstrument(@Part MultipartBody.Part file,@Part("uid") RequestBody uid, @Part("instrument_id") RequestBody instrument_id, @Part("sub_instrument_name") RequestBody sub_instrument_name);

//   @Multipart
//   @POST("instruments/add-sub-instruments")
//   Call<JsonObject> addSubInstrument(@Part MultipartBody.Part part,@PartMap Map<String, RequestBody> map);

   @POST("instruments/delete-sub-instruments")
   Call<DeleteSubInstrumentResponse> deleteSubInstrument(@Body JsonObject jsonObject);

   @POST("delete-activity")
   Call<DeleteSubInstrumentResponse> deleteActivity(@Body JsonObject jsonObject);

   @POST("add-timer")
   Call<AddTimerResponse> addTimer(@Body JsonObject jsonObject);

   @POST("pause-timer")
   Call<PauseOrStopTimerResponse> pauseOrStopTimer(@Body JsonObject jsonObject);

   @POST("save-comments")
   Call<SaveCommentsResponse> saveComments(@Body JsonObject jsonObject);

   @POST("activity-weekly")
   Call<ActivityResponse> getActivityWeeklyData(@Body JsonObject jsonObject);

   @POST("activity-today")
   Call<ActivityResponse> getActivityTodayData(@Body JsonObject jsonObject);

   @POST("activity-monthly")
   Call<ActivityResponse> getActivityMonthlyData(@Body JsonObject jsonObject);

   @POST("activity-yearly")
   Call<ActivityResponse> getActivityYearlyData(@Body JsonObject jsonObject);

   @POST("tips/add-device")
   Call<AddDeviceResponse> addDevice(@Body JsonObject jsonObject);

   @POST("tips/get-tip")
   Call<TipResponse> getTip(@Body JsonObject jsonObject);

}