package com.developer.musicatiiva.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.databinding.ActivityEnterOtpBinding;
import com.developer.musicatiiva.models.DataId;
import com.developer.musicatiiva.models.OtpMatch;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.SnackBar;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.BASE_URL;

public class EnterOtpActivity extends AppCompatActivity {
ActivityEnterOtpBinding activityEnterOtpBinding;
private JsonPlaceHolderApi jsonPlaceHolderApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEnterOtpBinding= DataBindingUtil.setContentView(this,R.layout.activity_enter_otp);
        activityEnterOtpBinding.setData(this);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        setListener();
    }


     public void onEnterClicked()
    {
        String input1=activityEnterOtpBinding.editTextInput1.getText().toString().trim();
        String input2=activityEnterOtpBinding.editTextInput2.getText().toString().trim();
        String input3=activityEnterOtpBinding.editTextInput3.getText().toString().trim();
        String input4=activityEnterOtpBinding.editTextInput4.getText().toString().trim();

        boolean validate=validate(input1,input2,input3,input4);
        final String otp=input1+input2+input3+input4;


        if(validate)
        {
            new CommonMethods.InternetCheck(EnterOtpActivity.this, new CommonMethods.Consumer() {
                @Override
                public void accept(Boolean internet) {
                    if (internet) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty(Constants.OTP, otp);
                        CommonDialogs.getInstance().showProgressDialog(EnterOtpActivity.this);
                        Call<OtpMatch> call=jsonPlaceHolderApi.getOtpMatchAcknowledgement(jsonObject);
                        call.enqueue(new Callback<OtpMatch>() {
                            @Override
                            public void onResponse(Call<OtpMatch> call, Response<OtpMatch> response) {
                                if(response.isSuccessful())
                                {
                                    OtpMatch otpMatch=response.body();
                                    String description=otpMatch.getDescription();
                                    int status_code=otpMatch.getStatus_code();
                                    DataId dataId=otpMatch.getData();
                                    int id=0;
                                    int uid=0;
                                    if(dataId!=null)
                                    {
                                        id=dataId.getId();
                                        uid=dataId.getUid();
                                    }

                                    if(status_code==1)
                                    {
                                        hideKeyboard(EnterOtpActivity.this);
                                        //Toast.makeText(EnterOtpActivity.this, description, Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(EnterOtpActivity.this,EnterNewPasswordActivity.class);
                                        intent.putExtra(Constants.UID,uid);
                                        intent.putExtra(Constants.ID,id);
                                        startActivity(intent);
                                         // finish();
                                    }
                                    else
                                    {
                                        activityEnterOtpBinding.editTextInput1.setText("");
                                        activityEnterOtpBinding.editTextInput2.setText("");
                                        activityEnterOtpBinding.editTextInput3.setText("");
                                        activityEnterOtpBinding.editTextInput4.setText("");

                                        activityEnterOtpBinding.editTextInput2.setCursorVisible(false);
                                        activityEnterOtpBinding.editTextInput3.setCursorVisible(false);
                                        activityEnterOtpBinding.editTextInput4.setCursorVisible(false);
                                        activityEnterOtpBinding.editTextInput1.setSelection(0);

                                        Log.d("mohit", "onResponse: Status code : "+status_code+"\n"+"Description : "+description);
                                        Toast.makeText(EnterOtpActivity.this, "OTP not matched..!!!", Toast.LENGTH_SHORT).show();
                                    }



                                }
                                else
                                {
                                    Log.d("mohit", "onResponse: "+response.message());
                                    Toast.makeText(EnterOtpActivity.this, "Response not successful", Toast.LENGTH_SHORT).show();

                                }
                                CommonDialogs.getInstance().dismissProgressDialog();

                                hideKeyboard(EnterOtpActivity.this);



                            }

                            @Override
                            public void onFailure(Call<OtpMatch> call, Throwable t) {
                                Log.d("mohit","Error code: "+t.getMessage());
                                Toast.makeText(EnterOtpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                CommonDialogs.getInstance().dismissProgressDialog();
                                hideKeyboard(EnterOtpActivity.this);

                            }
                        });
                    }
                }
            });

            /*if(otp.equals(OTP_PIN_VALUE))
            {
                hideKeyboard(this);
                startActivity(new Intent(this, ConnectActivity.class));
                finish();
            }
            else
            {
                mInput1.setText("");
                mInput2.setText("");
                mInput3.setText("");
                mInput4.setText("");
                mInput1.requestFocus();
                mInput1.setCursorVisible(true);
                SnackBar.show(mRoot,getString(R.string.snack_bar_error_incorrect_otp),this);
            }*/

        }
        else
        {
          //  Toast.makeText(this, "Please enter all four digit pin code", Toast.LENGTH_LONG).show();
             View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
           showAlertDialogBox();
            SnackBar.show(activityEnterOtpBinding.root,"Please enter all four digit pin code.",this);
        }

    }
    private void showAlertDialogBox()
    {
        new AlertDialog.Builder(this)

                .setMessage("Please enter all four digit pin code!!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public boolean validate(String input1,String input2,String input3,String input4)
    {
        if(input1.isEmpty())
        {

            return false;
        }
        if(input2.isEmpty())
        {

            return false;
        }
        if(input3.isEmpty())
        {
            return false;
        }
        if(input4.isEmpty())
        {

            return false;
        }

        return true;
    }
    public void onBackCLicked()
    {
          finish();
    }
    private void setListener() {
        activityEnterOtpBinding.editTextInput1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (activityEnterOtpBinding.editTextInput1.getText().toString().length() == 1) {

                    activityEnterOtpBinding.editTextInput1.clearFocus();
                    activityEnterOtpBinding.editTextInput2.requestFocus();
                    activityEnterOtpBinding.editTextInput2.setCursorVisible(true);

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {

            }
        });
        activityEnterOtpBinding.editTextInput2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (activityEnterOtpBinding.editTextInput2.getText().toString().length() == 1) {

                    activityEnterOtpBinding.editTextInput2.clearFocus();
                    activityEnterOtpBinding.editTextInput3.requestFocus();
                    activityEnterOtpBinding.editTextInput3.setCursorVisible(true);

                }
                else
                {
                    activityEnterOtpBinding.editTextInput2.clearFocus();
                    activityEnterOtpBinding.editTextInput1.requestFocus();
                    activityEnterOtpBinding.editTextInput1.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {


            }
        });
        activityEnterOtpBinding.editTextInput3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (activityEnterOtpBinding.editTextInput3.getText().toString().length() == 1) {

                    activityEnterOtpBinding.editTextInput3.clearFocus();
                    activityEnterOtpBinding.editTextInput4.requestFocus();
                    activityEnterOtpBinding.editTextInput4.setCursorVisible(true);

                }
                else
                {
                    activityEnterOtpBinding.editTextInput3.clearFocus();
                    activityEnterOtpBinding.editTextInput2.requestFocus();
                    activityEnterOtpBinding.editTextInput2.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {


            }
        });
        activityEnterOtpBinding.editTextInput4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 if (activityEnterOtpBinding.editTextInput4.getText().toString().length() == 1) {



                }
                else
                {
                    activityEnterOtpBinding.editTextInput4.clearFocus();
                    activityEnterOtpBinding.editTextInput3.requestFocus();
                    activityEnterOtpBinding.editTextInput3.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            public void afterTextChanged(Editable s) {


            }
        });

    }


}
