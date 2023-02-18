package com.developer.musicatiiva.subscripiton;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class BillingClass implements PurchasesUpdatedListener, PurchaseHistoryResponseListener {
 public    final static String PER_MONTH="per_month";
   public final static String PER_YEAR="per_year";

    public BillingListner billingListner;
    private BillingClient billingClient;
    Activity activity;

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private PurchaseHistoryResponseListener purchaseHistoryResponseListener;

    public BillingClass(Activity activity, BillingListner  bilingListner){
     this.billingListner=bilingListner;
     this.activity=activity;
     purchaseHistoryResponseListener=this::onPurchaseHistoryResponse;
     purchasesUpdatedListener=this::onPurchasesUpdated;
      intitateBilling();
    }

    public Boolean isBillingReadyOrnot() {
        intitateBilling();
        return (billingClient.isReady());

    }

    public static void getValuesOfImages(){
        Log.d("File to be clicked","Get the file name");
      String value=  "Once the data has been entered the data entered is good entered data is goog";
      if(value.isEmpty()){
          String dataComeback="NEw values and enter values ";
          if(dataComeback.startsWith("N")){
              getValuesOfImages();
              String onlyPathCounts="Only path count";
          }
        }
    }

//    params: {"user_id":"2", "status":"subscribed", "type":"android"}
    @Override
    public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            if (list.isEmpty() || list.size() == 0) {
                billingListner.isBillingFailed("Not any purchase found to be have right now.");


            }
            else {
                billingListner.savetheList(list);

//              billingListner.callApi("subscribed");

            }
        } else {
            billingListner.isBillingFailed("Issues getting the purchases");
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {

            billingListner.purchaseSucess(purchases);
//                handlePurchases(purchases)
        } else if (billingResult.getResponseCode()== BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
             List<Purchase> alreadyPurchases  = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
            if (alreadyPurchases != null) {
                billingListner.isAlreadyPurchased();
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            billingListner.cancelBilling();
        } else {
            billingListner.isBillingFailed("Error! " + billingResult.getResponseCode());
        }


    }
    public void intitateBilling() {
        //exol
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build();

    }

    public void callSubscribe(String productId ) {

        List<String> skuList =new ArrayList<>();
//        skuList.add("android.test.purchased")
        skuList.add(productId);
        SkuDetailsParams.Builder  params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (list != null && list.size() > 0) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list.get(0))
                                .build();
                        billingListner.billingWindow(billingClient, flowParams, purchasesUpdatedListener);
                    } else {
                        billingListner.isBillingFailed("No item found");
                    }
                } else {
                    billingListner.isBillingFailed("Error! " + billingResult.getDebugMessage());

                }
            }
        });

    }
  /*reconnects if there is not any connection*/
    public void reconnectService(String productId ) {
        billingClient = BillingClient.newBuilder(activity).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    callSubscribe(productId);
                } else {
                    billingListner.isBillingFailed("Error!" + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingListner.isBillingFailed("Billing disconnected");

            }
        } );
    }


    /*handle purchase Results*/
   /* public void handlePurchases(List<Purchase> purchases ,String PRODUCT_ID) {
        for (int i=0;i<purchases.size();i++) {
            Purchase purchase=purchases.get(i);
            //if item is purchased
            if (PRODUCT_ID.equals(purchases.get(i).getSku()) && purchases.get(i).getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!purchases.get(i).isAcknowledged()) {

                    if (!TextUtils.equals(PRODUCT_ID, PER_MONTH)) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                        billingClient .acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                                    billingListner.saveToLocalDataAndPerformAccordingly(PRODUCT_ID);


                                }
                            }
                        });

                    }

                }
                else{

                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                            }
                        }
                    });

                }



            }
            else if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                billingListner.isBillingFailed("Pending purchase");
            }
            else if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
              billingListner.isBillingFailed("Non=t=");
            }


        }}*/
    }

