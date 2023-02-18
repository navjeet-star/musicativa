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
import com.developer.musicatiiva.databinding.ActivityEnterNewPasswordBinding;
import com.developer.musicatiiva.models.ChangePassword;
import com.developer.musicatiiva.utils.Constants;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.BASE_URL;

public class EnterNewPasswordActivity extends AppCompatActivity {

    ActivityEnterNewPasswordBinding activityEnterNewPasswordBinding;
    private int id;
    private int uid;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEnterNewPasswordBinding= DataBindingUtil.setContentView(this,R.layout.activity_enter_new_password);
        activityEnterNewPasswordBinding.setData(this);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        id=getIntent().getIntExtra(Constants.ID,0);
        uid=getIntent().getIntExtra(Constants.USERID,0);

    }

     public void onEnterClicked()

    {

        new CommonMethods.InternetCheck(EnterNewPasswordActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                if (internet) {

                    String newPassword = activityEnterNewPasswordBinding.etEnterNewPassowrd.getText().toString();
                    String confirmPassword=activityEnterNewPasswordBinding.etReEnterNewPassowrd.getText().toString();

                     if (!Validations.isEmpty(newPassword)) {

                         activityEnterNewPasswordBinding.etEnterNewPassowrd.setError("Please enter Password");
                     }

                     else if(!Validations.isEmpty(confirmPassword))
                     {
                         activityEnterNewPasswordBinding.etReEnterNewPassowrd.setError("Please re-enter Password");
                     }

                     else if(!newPassword.equals(confirmPassword))

                     {
                         activityEnterNewPasswordBinding.etReEnterNewPassowrd.setError("Password not matched");

                     }

                     else

                     {

                         JsonObject jsonObject = new JsonObject();
                         jsonObject.addProperty(Constants.UID, uid);
                         jsonObject.addProperty(Constants.ID, id);
                         jsonObject.addProperty(Constants.PASSWORD, newPassword);
                         CommonDialogs.getInstance().showProgressDialog(EnterNewPasswordActivity.this);
                         Call<ChangePassword> call=jsonPlaceHolderApi.getPasswordChangeAcknowledgement(jsonObject);
                         call.enqueue(new Callback<ChangePassword>() {
                             @Override
                             public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                                 if(response.isSuccessful())
                                 {
                                     ChangePassword changePassword=response.body();
                                     String description=changePassword.getDescription();
                                     int status_code=changePassword.getStatus_code();

                                     if(status_code==1)

                                     {

                                         Toast.makeText(EnterNewPasswordActivity.this, "Password changed successfully..!!!", Toast.LENGTH_SHORT).show();
                                         Intent intent=new Intent(EnterNewPasswordActivity.this,LoginActivity.class);

                                        startActivity(intent);
                                          finishAffinity();
                                     }
                                     else
                                     {
                                         Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                         Toast.makeText(EnterNewPasswordActivity.this, "Failed..!!!", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                                 else
                                 {
                                     Log.d("mohit", "onResponse: "+response.message());
                                     Toast.makeText(EnterNewPasswordActivity.this, "Failed..!!", Toast.LENGTH_SHORT).show();
                                 }

                                 CommonDialogs.getInstance().dismissProgressDialog();
                             }

                             @Override
                             public void onFailure(Call<ChangePassword> call, Throwable t) {
                                 Log.d("mohit","Error code: "+t.getMessage());
                                 Toast.makeText(EnterNewPasswordActivity.this, "Failed..!!", Toast.LENGTH_SHORT).show();
                                 CommonDialogs.getInstance().dismissProgressDialog();
                             }
                         });
                     }
                }
            }

          });

        }

    public void onBackClicked()
    {
          finish();
    }


}
