package com.roaringcat.kidsquiz;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.ads.InterstitialAd;

public class Show_Invitecode extends Lock_BaseActivity {
    User user2;
    boolean mSubKidsquiz = false;
    public TemplateView template;
    private DatabaseReference mUserReference;
    private InterstitialAd mInterstitialAd;
    Activity thisact = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_show_invitecode);
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        if(!mSubKidsquiz) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-9350891168282170/9961693261");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("invitecode", user2.myreccode); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                    clipboardManager.setPrimaryClip(clipData);
                    //복사가 되었다면 토스트메시지 노출
                    Toast.makeText(Show_Invitecode.this, "초대코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }
            });
        }
        //////////////////////데이터베이스에서 추천인코드 받아와서  넘겨주기/////////////////////
        final TextView invitecode = (TextView)findViewById(R.id.invitecode);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Mypage", "사용자 정보 : "+dataSnapshot.getValue());
                user2 = dataSnapshot.getValue(User.class);
                System.out.println(user2);
                invitecode.setText(user2.myreccode);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //클립보드에 복사하기(temp를 클립보드에 넘겨주자!)
        final Button copy = (Button)findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { //temp를 클립보드에 넘겨주게!
                //클립보드 사용 코드
                if(!mSubKidsquiz) {
                    if (mInterstitialAd.isLoaded()) {
                        Lock_AppLockImpl.onad = true;
                        mInterstitialAd.show();
                    } else {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("invitecode", user2.myreccode); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                        clipboardManager.setPrimaryClip(clipData);
                        //복사가 되었다면 토스트메시지 노출
                        Toast.makeText(Show_Invitecode.this, "초대코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("invitecode", user2.myreccode); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                    clipboardManager.setPrimaryClip(clipData);
                    //복사가 되었다면 토스트메시지 노출
                    Toast.makeText(Show_Invitecode.this, "초대코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        template = (TemplateView) findViewById(R.id.ad_invite);
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