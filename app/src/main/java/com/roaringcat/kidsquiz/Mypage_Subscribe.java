package com.roaringcat.kidsquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;

public class Mypage_Subscribe extends Lock_BaseActivity{
    public static Context context_main;
    private BillingProcessor mBillingProcessor;
    private BillingModule billingModule;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailed_statistics_promotion);
        context_main = this;

        billingModule = new BillingModule(this);
        billingModule.initBillingProcessor();
        mBillingProcessor = billingModule.getBillingProcessor();

        TextView refund_policy = (TextView) findViewById(R.id.refund_policy);
        refund_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypage_RefundPolicy.class);
                startActivity(intent);
            }
        });
    }

    public void onSubscribeButtonClicked(View arg0) {
        billingModule.subscribeProduct();
        Lock_AppLockImpl.btn_sub_clicked = true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            if(resultCode == RESULT_OK) {
                ((Navigation)Navigation.context).mSubKidsquiz = true;
                finish();
            }
        }
    }
}
