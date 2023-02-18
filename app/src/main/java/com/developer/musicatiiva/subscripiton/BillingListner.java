package com.developer.musicatiiva.subscripiton;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

public interface BillingListner {
    public void isBillingFailed(String reason  );
    public void billingWindow(BillingClient billingCleint, BillingFlowParams launchParams , PurchasesUpdatedListener purchasesUpdatedListener );
    public void cancelBilling();
    public void isAlreadyPurchased();
//    public void onConsumableSucess(List<Purchase> purchases);
    public void callRestoration(BillingClient billingCleint ,PurchaseHistoryResponseListener history);
    public void purchaseSucess(List<Purchase> purchases);
     public void savetheList( List<PurchaseHistoryRecord> list );
 }
