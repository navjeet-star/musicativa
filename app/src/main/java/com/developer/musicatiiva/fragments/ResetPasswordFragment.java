package com.developer.musicatiiva.fragments;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.LoginActivity;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentResetPasswordBinding;
import com.developer.musicatiiva.models.ChangePassword;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.BASE_URL;

public class ResetPasswordFragment extends Fragment {

    FragmentResetPasswordBinding fragmentResetPasswordBinding;
    View view;
    String email;
    Context context;
    JsonPlaceHolderApi jsonPlaceHolderApi;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        fragmentResetPasswordBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false);
        view=fragmentResetPasswordBinding.getRoot();
        email = getArguments().getString(Constants.EMAIL);
        fragmentResetPasswordBinding.setData(this);
        fragmentResetPasswordBinding.editetxtEmail.setText(email);
        context=getActivity();
        if (context != null) {
            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);

            ((MainActivity) context).setTittle("Change Password");
            ((MainActivity) context).setMenuVisibilty(false);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);
        }

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

        return view;
    }

     public void OnSubmit()
    {
         String password=fragmentResetPasswordBinding.editetxtPassword.getText().toString().trim();
        String newPassword=fragmentResetPasswordBinding.editetxtNewpassword.getText().toString().trim();
        String confirmPassword=fragmentResetPasswordBinding.editetxtConfirmPassword.getText().toString().trim();
        if(password.isEmpty())
        {
            fragmentResetPasswordBinding.editetxtPassword.setError("Please enter password");
            fragmentResetPasswordBinding.editetxtPassword.requestFocus();
            return;
        }
        else if(newPassword.isEmpty())
        {
            fragmentResetPasswordBinding.editetxtNewpassword.setError("Please enter new password");
            fragmentResetPasswordBinding.editetxtNewpassword.requestFocus();
            return;
        }
        else if(confirmPassword.isEmpty())
        {
            fragmentResetPasswordBinding.editetxtConfirmPassword.setError("Please enter confirm  password");
            fragmentResetPasswordBinding.editetxtConfirmPassword.requestFocus();
            return;
        }
        else if(!newPassword.equals(confirmPassword))
        {
            fragmentResetPasswordBinding.editetxtConfirmPassword.setError("Confirm password must match with new password");
            fragmentResetPasswordBinding.editetxtConfirmPassword.requestFocus();
            return;
        }
        save(password,newPassword);
        //Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void save(final String password, final String newPassword) {

        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty(Constants.USER_ID,user_id);
                    jsonObject.addProperty(Constants.EMAIL,email);
                    jsonObject.addProperty(Constants.PASSWORD,password);
                    jsonObject.addProperty(Constants.NEW_PASSWORD,newPassword);

                    Call<ChangePassword> call=jsonPlaceHolderApi.getChangePasswordAcknowledgement(jsonObject);
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
                                    Toast.makeText(context, "Password has been changed successfully...!!!", Toast.LENGTH_SHORT).show();
                                    MySharedPreferences.getInstance().setLoggedIn(context, false);
                                    startActivity(new Intent(context, LoginActivity.class));
                                    ((MainActivity)context).finishAffinity();
                                    //getProfileData();

                                    // finish();
                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show();

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();


                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<ChangePassword> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }
                    });
                }
            }
        });
    }

}
