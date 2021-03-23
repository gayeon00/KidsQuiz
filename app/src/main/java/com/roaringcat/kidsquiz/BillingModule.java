package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;

public class BillingModule implements BillingProcessor.IBillingHandler {
    private Context context;
    private BillingProcessor mBillingProcessor;
    private String itemId = "sub_month";
    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvdze8vPoqIesva77B2RgcICtV7BuhKrhomhVeoSFhF0ODk4vOK/bzUPbEt6XwwCRe0O0u/xjlHAF5vmyCdMaj0jOuyCZs8Ai4TAugumjQMOCyinlRQxpbnRngEH57eH1Vls9u5gfCBUaAH5zbyCWJB5MaLMuw/YW4DV6VOYJ2q5MFAD6czRP6y49thod5wVjwRa36Ug4TW7RZWBI6qSBYwXt4RWqj8jevmo33/r6iR5Q1HJkZh8Q6COWTZRyFPGIL9xOKalUb3eSredpv76/CIkZeLm96gNLJ25aPE6TSV66BkbkPy++81U+2rgp2jDWN5Gokg+/Z8Nmcy0BckNU7QIDAQAB";
    private boolean mSubKidsquiz = false;
    private static final String MERCHANT_ID="04252003340206441884";
    public BillingModule(Context context) {
        this.context = context;
    }

    public void initBillingProcessor() {
        mBillingProcessor = new BillingProcessor(context, base64EncodedPublicKey, MERCHANT_ID,this);
    }

    public void purchaseProduct() { // 아이템 구매 요청
        if(mBillingProcessor.isPurchased(itemId)) {
            // 이미 광고 제거를 위한 결제를 완료했기 때문에 해당 처리를 해주면 된다.
            return;
        }
        mBillingProcessor.purchase((Activity)context, itemId);
    }

    public void subscribeProduct(){
        if(mBillingProcessor.isSubscribed(itemId)){
            return;
        }
        mBillingProcessor.subscribe((Activity)context, itemId);
    }


    public void releaseBillingProcessor() {
        if(mBillingProcessor != null)
            mBillingProcessor.release();
    }

    public BillingProcessor getBillingProcessor() {
        return mBillingProcessor;
    }

    @Override
    public void onProductPurchased(@NonNull String id, @Nullable TransactionDetails transactionDetails) {
        // 아이템 구매 성공 시 호출.
        // 따라서 보상을 지급하든(광고 제거) 혹은 해당 아이템을 소비하든 해당 기능을 작성
    }

    @Override
    public void onPurchaseHistoryRestored() {
        // 구매 내역 복원 및 구매한 모든 PRODUCT ID 목록이 Google Play에서 로드 될 때 호출.
    }

    @Override
    public void onBillingError(int errCode, @Nullable Throwable throwable) {
        // 구매 시 에러가 발생했을 때 처리
        if(errCode != com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            // 사용자가 취소한 게 아니라면 에러 발생에 대해 사용자에게 고지하는 등의 처리
        }
    }

    public boolean getSubKidsquiz(){
        return mSubKidsquiz;
    }

    @Override
    public void onBillingInitialized() {
        mBillingProcessor.loadOwnedPurchasesFromGoogle(); // 소유하고 있는 구매 아이템 목록을 가져온다.
        try {
            if(mBillingProcessor.listOwnedSubscriptions().get(0) != null){
                mSubKidsquiz = true;
                Log.e("onBillingInitialized : ", "구독 완료");
            }
        } catch (IndexOutOfBoundsException e){
            Log.e("onBillingInitialized : ", "구독 안됨");
            try {
                mSubKidsquiz = false;
                ((Navigation) Navigation.context).mSubKidsquiz = false;
            }catch (NullPointerException el){}
        }
    }
}