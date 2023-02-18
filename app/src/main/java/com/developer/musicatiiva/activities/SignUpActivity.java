package com.developer.musicatiiva.activities;

import android.content.Intent;

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
import com.developer.musicatiiva.databinding.ActivitySignUpBinding;
import com.developer.musicatiiva.models.AddDeviceResponse;
import com.developer.musicatiiva.models.RegisterData;
import com.developer.musicatiiva.models.ResultModel;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.CLOCK_URL;
import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    String firstName,lastName,username,email,password,confirmPassword;
    int userId=0;
    private String device_token="";
    private JsonPlaceHolderApi jsonPlaceHolderApi;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up);

        binding.setData(this);
        getDeviceToken();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(INSTRUMENT_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
    }


    public void onSignUpCliked()
    {
        new CommonMethods.InternetCheck(SignUpActivity.this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {

                firstName=binding.etFirstname.getText().toString().trim();
                lastName=binding.etLastname.getText().toString().trim();
                username=binding.etUsername.getText().toString().trim();
                email=binding.etEmail.getText().toString().trim();
                password=binding.etPassword.getText().toString().trim();
                confirmPassword=binding.etConfirmPassword.getText().toString().trim();

                if(!Validations.isEmpty(firstName))
                {
                    binding.etFirstname.setError("Please enter FirstName");
                }
                else if(!Validations.isEmpty(lastName))
                {
                    binding.etLastname.setError("Please enter LastName");
                }
                else if(!Validations.isEmpty(username))
                {
                    binding.etUsername.setError("Please enter Username");
                }
                else if(!Validations.isEmpty(email))
                {
                    binding.etEmail.setError("Please enter Email");
                }
                else if(!Validations.isValidEmail(email))
                {
                    binding.etEmail.setError("Please enter Valid Email Pattern");
                }
                else if(!Validations.isEmpty(password))
                {
                    binding.etPassword.setError("Please enter Password");
                }
                else if(!Validations.isEmpty(confirmPassword))
                {
                    binding.etConfirmPassword.setError("Please enter Confirm Password");
                } else if (!password.equals(confirmPassword))
                {
                    binding.etPassword.setError("Password and Confirm Password not matched");
                }
                else
                {
                    final JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Constants.FIRSTNAME,firstName);
                    jsonObject.addProperty(Constants.LASTNAME,lastName);
                    jsonObject.addProperty(Constants.USERNAME,username);
                    jsonObject.addProperty(Constants.EMAIL,email);
                    jsonObject.addProperty(Constants.PASSWORD,password);

                    Log.d("ctttttttt",jsonObject.toString());
                    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<RegisterData> call = apiService.getRegisterData(jsonObject);
                    CommonDialogs.getInstance().showProgressDialog(SignUpActivity.this);

                    call.enqueue(new Callback<RegisterData>() {
                        @Override
                        public void onResponse(Call<RegisterData> call, Response<RegisterData> response) {
                            RegisterData registerData =response.body();

                            CommonDialogs.getInstance().dismissProgressDialog();
                            if(registerData!=null)
                            {
                                if (registerData.getStatusCode().equals("1")) {



                                    userId= registerData.getData().getId();
                                    MySharedPreferences.getInstance().saveUserId(SignUpActivity.this,Constants.USERID,userId);

                                    if(userId!=-1)
                                    {


                                     /*   Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();*/
                                        sendTokenToServer();
                                    }

                                } else if(registerData.getStatusCode().equals("0")){

                                    String description = registerData.getDescription();
                                    Toast.makeText(SignUpActivity.this, description, Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<RegisterData> call, Throwable t) {
                            Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            CommonDialogs.getInstance().dismissProgressDialog();
                        }
                    });

                }



            }
        });
    }



    private void sendTokenToServer(){
        new CommonMethods.InternetCheck(this, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(SignUpActivity.this);
                    JsonObject jsonObject=new JsonObject();

                    int user_id= MySharedPreferences.getInstance().getUserID(SignUpActivity.this, Constants.USERID);

                    jsonObject.addProperty(Constants.DEVICE_TOKEN,device_token);
                    jsonObject.addProperty(Constants.DEVICE_TYPE,"Android");
                    jsonObject.addProperty(Constants.UID,user_id);

                    Log.d("data",jsonObject.toString());

                    Call<AddDeviceResponse> call=jsonPlaceHolderApi.addDevice(jsonObject);
                    call.enqueue(new Callback<AddDeviceResponse>() {
                        @Override
                        public void onResponse(Call<AddDeviceResponse> call, Response<AddDeviceResponse> response) {
                            if(response.isSuccessful())
                            {
                                AddDeviceResponse addDeviceResponse=response.body();

                                String description=addDeviceResponse.getDescription();
                                String status_codee=addDeviceResponse.getStatusCode();

                                int status_code=Integer.parseInt(status_codee);

                                 if(status_code==1)

                                {
                                     {
                                         int user_id= MySharedPreferences.getInstance().getUserID(SignUpActivity.this,Constants.USERID);
                                         Log.d("mohit", "onResponse: user id "+user_id+"");
                                         MySharedPreferences.getInstance().setLoggedIn(SignUpActivity.this, true);
                                         Toast.makeText(SignUpActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                         Retrofit retrofit2 = new Retrofit.Builder()
                                                 .baseUrl(INSTRUMENT_DATA_URL)
                                                 .addConverterFactory(GsonConverterFactory.create())
                                                 .build();
                                         Retrofit retrofit = new Retrofit.Builder()
                                                 .baseUrl(CLOCK_URL)
                                                 .addConverterFactory(GsonConverterFactory.create())
                                                 .build();
                                         jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                                         JsonPlaceHolderApi  jsonPlaceHolderApi2;
                                         jsonPlaceHolderApi2 = retrofit2.create(JsonPlaceHolderApi.class);
                                        // checkForTheSubscribtion(user_id);
                                    }


                                    // finish();
                                }
                                else
                                {
                                    Toast.makeText(SignUpActivity.this,"Already taken username",Toast.LENGTH_LONG).show();
                                    Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                    //   Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                                    // fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                }



                            }
                            else
                            {
//                                Toast.makeText(SignUpActivity.this,"Already taken username",Toast.LENGTH_LONG).show();
                                Log.d("mohit", "onResponse: "+response.message());
                                // Toast.makeText(LoginActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();
                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setVisibility(View.VISIBLE);

                                //fragmentsInstrumenstsBinding.textViewNoDataFound.setText(response.message());

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<AddDeviceResponse> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
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
    private void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(this, new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                 device_token=task.getResult().toString();

            }
        });
    }


    private void checkForTheSubscribtion(int userId){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, userId);
        jsonObject.addProperty("type","android");
        Log.d("userId",userId+"");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.checkTheSubscription(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject object=response.body();
                    String code=object.get("status_code").getAsString();
                    Log.d("Result",response.body().toString());
                    int codeData=Integer.parseInt(code);
                    Log.d("Result",codeData+"");
                    if(codeData==0){
                        Log.d("ResultData",response.body().toString());
                      /*  Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
                        startActivity(intent);
                        finish();*/
//                        Intent intent = new Intent(SignUpActivity.this, SubscriptionActivity.class);
//                        startActivity(intent);
//                        finish();

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Log.d("ResultData",response.body().toString());
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("Result1",response.body().toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } );
    }
}
