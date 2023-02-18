package com.developer.musicatiiva.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.LoginActivity;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.FragmentProfileInformationBinding;
import com.developer.musicatiiva.models.ChangeName;
import com.developer.musicatiiva.models.ChangePassword;
import com.developer.musicatiiva.models.Profile;
import com.developer.musicatiiva.models.ProfileData;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.BASE_URL;

public class ProfileInformationFragment extends Fragment {

    FragmentProfileInformationBinding fragmentProfileInfoBinding;
    View view;
    Context context;
    Toolbar mToolbar;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentProfileInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_information, container, false);
        view = fragmentProfileInfoBinding.getRoot();
        fragmentProfileInfoBinding.setData(this);

        mToolbar =  view.findViewById(R.id.my_toolbar);

        context=getActivity();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        if (context != null) {
            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);

            ((MainActivity) context).setTittle("Profile Information");
            ((MainActivity) context).setMenuVisibilty(true);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);



        }

      /*  if(context!=null)
        {
            if((((MainActivity) context).activityMainBinding.iTollbar.tvStartCounter.getDrawable().getConstantState())==context.getResources().getDrawable(R.drawable.timer_start).getConstantState())
            {
                ((MainActivity) context).activityMainBinding.iTollbar.tvStartCounter.setVisibility(View.GONE);
            }
            else
            {
                ((MainActivity) context).activityMainBinding.iTollbar.tvStartCounter.setVisibility(View.VISIBLE);
            }
        }*/

        getProfileData();

        return view;
    }


    private void getProfileData() {
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    JsonObject jsonObject=new JsonObject();
                    int user_id= MySharedPreferences.getInstance().getUserID(context, Constants.USERID);

                     jsonObject.addProperty(Constants.USER_ID,user_id);

                    Call<Profile> call=jsonPlaceHolderApi.getProfileData(jsonObject);
                    call.enqueue(new Callback<Profile>() {
                        @Override
                        public void onResponse(Call<Profile> call, Response<Profile> response) {
                            if(response.isSuccessful())
                            {
                                Profile profile=response.body();
                                ProfileData profileData=profile.getData();
                                String desc=profile.getDescription();

                                //String last_practiced_date=practiceData.getLast_practice_date();
                                int status_code=profile.getStatus_code();







                                if(status_code==1)
                                {

                                    if(profileData!=null)
                                    {
                                         fragmentProfileInfoBinding.textViewEmailAddress.setText(profileData.getEmail());
                                        fragmentProfileInfoBinding.textViewProfileName.setText(profileData.getFirstname()+" "+profileData.getLastname());

                                    }


                                }
                                else
                                {
                                     Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
                                }



                            }
                            else
                            {
                                 Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<Profile> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }
                    });
                }
            }
        });

    }




    Dialog dialogChangePassword;
    EditText mEdOldPassword,mEdNewPassword,mEdConfirmPassword;
    public void onChangePassword()
    {

        dialogChangePassword = new Dialog(context);
        dialogChangePassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePassword.setContentView(R.layout.dialog_change_password);
        dialogChangePassword.setCanceledOnTouchOutside(true);
        dialogChangePassword.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogChangePassword.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogChangePassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEdOldPassword=dialogChangePassword.findViewById(R.id.editTextOldPassword);
        mEdNewPassword=dialogChangePassword.findViewById(R.id.editTextNewPassword);
        mEdConfirmPassword=dialogChangePassword.findViewById(R.id.editTextConfirmPassword);

        mEdOldPassword.requestFocus();
        dialogChangePassword.findViewById(R.id.textViewUpdatePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });
        dialogChangePassword.findViewById(R.id.textViewCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChangePassword.dismiss();
            }
        });
        dialogChangePassword.show();
    }
    private void updatePassword()
    {
        String oldPassword=mEdOldPassword.getText().toString().trim();
        String newPassword=mEdNewPassword.getText().toString().trim();
        String confirmPassword=mEdConfirmPassword.getText().toString().trim();
        if(oldPassword.isEmpty())
        {
            mEdOldPassword.setError("Please enter password");
            mEdOldPassword.requestFocus();
            return;
        }
        else if(newPassword.isEmpty())
        {
            mEdNewPassword.setError("Please enter new password");
            mEdNewPassword.requestFocus();
            return;
        }
        else if(confirmPassword.isEmpty())
        {
            mEdConfirmPassword.setError("Please enter confirm  password");
            mEdConfirmPassword.requestFocus();
            return;
        }
        else if(!newPassword.equals(confirmPassword))
        {
            mEdConfirmPassword.setError("Confirm password must match with new password");
            mEdConfirmPassword.requestFocus();
            return;
        }
        update(oldPassword,newPassword);
        dialogChangePassword.dismiss();

    }
    private void update(final String password, final String newPassword) {

        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty(Constants.USER_ID,user_id);
                    jsonObject.addProperty(Constants.EMAIL,fragmentProfileInfoBinding.textViewEmailAddress.getText().toString());
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

    Dialog dialogChangeName;
    EditText mEdName;

    public void onChangeName()
    {
        dialogChangeName = new Dialog(context);
        dialogChangeName.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeName.setContentView(R.layout.dialog_change_profile_name);
        dialogChangeName.setCanceledOnTouchOutside(true);
        dialogChangeName.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogChangeName.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialogChangeName.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEdName=dialogChangeName.findViewById(R.id.editTextName);
        mEdName.setText(fragmentProfileInfoBinding.textViewProfileName.getText());
        mEdName.requestFocus();
        dialogChangeName.findViewById(R.id.textViewSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileName();
            }
        });
        dialogChangeName.findViewById(R.id.textViewCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dialogChangeName.dismiss();
            }
        });
        dialogChangeName.show();
    }
    private void saveProfileName()
    {
        String name=mEdName.getText().toString().trim();
        if(name.isEmpty())
        {
            mEdName.setError("Please enter Name");
            mEdName.requestFocus();
            return;
        }
        save(name);
        dialogChangeName.dismiss();
    }
    private void save(final String name)
    {
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty(Constants.NAME,name);
                    jsonObject.addProperty(Constants.USER_ID,user_id);
                    Call<ChangeName> call=jsonPlaceHolderApi.getChangeNameAcknowledgement(jsonObject);
                    call.enqueue(new Callback<ChangeName>() {
                        @Override
                        public void onResponse(Call<ChangeName> call, Response<ChangeName> response) {
                            if(response.isSuccessful())
                            {
                                ChangeName changeName=response.body();

                                String description=changeName.getDescription();
                                int status_code=changeName.getStatus_code();



                                if(status_code==1)
                                {
                                   getProfileData();
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
                        public void onFailure(Call<ChangeName> call, Throwable t) {
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
