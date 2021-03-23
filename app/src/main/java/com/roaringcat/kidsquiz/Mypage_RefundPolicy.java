package com.roaringcat.kidsquiz;
import android.os.Bundle;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class Mypage_RefundPolicy extends Lock_BaseActivity {
    boolean mSubKidsquiz = false;
    public TemplateView template;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_refund_policy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        template = (TemplateView) findViewById(R.id.ad_refund_policy);
        if(((Navigation)Navigation.context).mSubKidsquiz){
            template.setVisibility(template.GONE);
        }
        MobileAds.initialize((this), "ca-app-pub-9350891168282170~5512537782");
        //Test 광고 id : ca-app-pub-3940256099942544/2247696110
        AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-9350891168282170/4038307032");
        // 내 네이티브 광고 id : ca-app-pub-9350891168282170/4038307032
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                template.setNativeAd(unifiedNativeAd);

            }
        });
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }
}