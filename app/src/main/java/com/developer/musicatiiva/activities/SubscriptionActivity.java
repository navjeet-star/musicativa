package com.developer.musicatiiva.activities;

import static com.developer.musicatiiva.R.color.colorPrimary;
import static com.developer.musicatiiva.R.color.colorWhite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.developer.musicatiiva.R;
import com.developer.musicatiiva.apiModels.ApiClient;
import com.developer.musicatiiva.apiModels.ApiInterface;
import com.developer.musicatiiva.subscripiton.BillingClass;
import com.developer.musicatiiva.subscripiton.BillingListner;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity implements BillingListner {

    TextView SubMonth;
    TextView SubYear;
    TextView txtContinue;
       BillingClass billingClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Inilization();
    }

    private void Inilization(){

        SubMonth=findViewById(R.id.SubMonth);
        SubYear=findViewById(R.id.SubYear);
        txtContinue=findViewById(R.id.txtContinue);
         billingClass=new BillingClass(this,this);
         billingClass.intitateBilling();
        SubMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubMonth.setBackgroundResource(R.drawable.btn_background);
                SubYear.setBackgroundResource(R.drawable.blues_corner);
                txtContinue.setVisibility(View.VISIBLE);

                SubMonth.setTextColor(getResources().getColor(colorWhite));
                SubYear.setTextColor(getResources().getColor(colorPrimary));

                txtContinue.setText("$9.99 per month\nContinue");
                if(billingClass.isBillingReadyOrnot()){
                    billingClass.reconnectService(BillingClass.PER_YEAR);
                }
            }
        });

        SubYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubYear.setBackgroundResource(R.drawable.btn_background);
                SubMonth.setBackgroundResource(R.drawable.blues_corner);

                SubYear.setTextColor(getResources().getColor(colorWhite));
                SubMonth.setTextColor(getResources().getColor(colorPrimary));

                txtContinue.setVisibility(View.VISIBLE);
                txtContinue.setText("$59.99 per year\nContinue");
                if(billingClass.isBillingReadyOrnot()){
                    billingClass.reconnectService(BillingClass.PER_YEAR);
                }

            }
        });
    }

    /*
    gh
    */

    private void checkForTheSubscribtion(String status){
        JsonObject jsonObject = new JsonObject();
        int user_id = MySharedPreferences.getInstance().getUserID(SubscriptionActivity.this, Constants.USERID);
        jsonObject.addProperty(Constants.USER_ID, user_id);
        jsonObject.addProperty("type","android");
        jsonObject.addProperty("status",status);
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
                      /*  Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();*/
                    }
                    else{
                        Log.d("ResultData",response.body().toString());
                        Intent intent = new Intent(SubscriptionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("Result1",response.body().toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Data",call.toString());

            }
        } );
    }

    @Override
    public void isBillingFailed(String reason) {

    }

    @Override
    public void billingWindow(BillingClient billingCleint, BillingFlowParams launchParams, PurchasesUpdatedListener purchasesUpdatedListener) {
        billingCleint.launchBillingFlow(this, launchParams);
    }

    @Override
    public void cancelBilling() {
       checkForTheSubscribtion("cancel");
    }

    @Override
    public void isAlreadyPurchased() {
        checkForTheSubscribtion("cancel");
    }

    @Override
    public void callRestoration(BillingClient billingCleint, PurchaseHistoryResponseListener history) {

    }

    @Override
    public void purchaseSucess(List<Purchase> purchases) {
        checkForTheSubscribtion("cancel");
     }

    @Override
    public void savetheList(List<PurchaseHistoryRecord> list) {

    }
}