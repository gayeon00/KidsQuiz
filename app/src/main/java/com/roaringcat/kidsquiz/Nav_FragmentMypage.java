package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Nav_FragmentMypage extends Fragment {
    private ImageView iv_profile; // ????????? ???
    boolean ad_limited = false;
    private TextView txtView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    boolean mSubKidsquiz;
    private DatabaseReference mDatabase;
    Bitmap bitmap;
    TextView tv_nickname;
    TextView reserve;
    TemplateView template;
    private DatabaseReference mUserReference;
    View rootView;
    private RewardedAd rewardedAd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_mypage, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtView = (TextView) view.findViewById(R.id.btn_pwmanage);
        iv_profile = (ImageView) view.findViewById(R.id.iv_profile);

        tv_nickname = (TextView) view.findViewById(R.id.tv_nickName);
        reserve = (TextView) view.findViewById(R.id.reserves); //????????? ????????? ??????  android:inputType = "number"?????????

        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("????????? ??????", "DocumentSnapshot data: " + document.getString("name"));
                        String name = document.getString("name");
                        String myreserve = document.getLong("myreserve").toString(); //????????? (?????? ??????????????? ???????????????...)
                        tv_nickname.setText(name);
                        reserve.setText(myreserve);
                        if (user.getPhotoUrl()!=null) {
                            getivimage();
                        }
                    } else {
                        Log.d("????????? ??????", "No such document");
                    }
                } else {
                    Log.d("????????? ??????", "get failed with ", task.getException());
                }
            }
        });*/
        // [END get_document]
        // ????????????
        TextView btn1 = (TextView) view.findViewById(R.id.btn_invite);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), Show_Invitecode.class);
                startActivity(intent1);
            }
        });

        // ??? ?????? ??????
        TextView btn2 = (TextView) view.findViewById(R.id.btn_lock);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), Lock_HomePage.class);
                startActivity(intent2);
            }
        });

        // ????????????
        TextView btn3 = (TextView) view.findViewById(R.id.btn_logout);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), Mypage_Logout.class);
                startActivity(intent3);
            }
        });

        // ?????? ???????????? ??????
        TextView btn4 = (TextView) view.findViewById(R.id.btn_pwmanage);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getActivity(), Reset_password.class);
                startActivity(intent4);
            }
        });

        // ????????????
        TextView btn5 = (TextView) view.findViewById(R.id.btn_delete_user);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(getActivity(), Mypage_DeleteUser.class);
                startActivity(intent5);
            }
        });

        // ????????????
        TextView btn6 = (TextView) view.findViewById(R.id.btn_subscribe);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //???????????? ??????
                if(mSubKidsquiz) {
                    Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
                //?????? ??????????????? Mypage_Subscribe
                else{
                    Intent intent6 = new Intent(getActivity(), Mypage_Subscribe.class);
                    startActivity(intent6);
                }
            }
        });

        // ????????????
        TextView btn7 = (TextView) view.findViewById(R.id.btn_cancel_subscribe);
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(getActivity(), Mypage_CancelSubscribe.class);
                startActivity(intent7);
            }
        });
        // ????????????
        TextView btn8 = (TextView) view.findViewById(R.id.btn_refund_policy);
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent8 = new Intent(getActivity(), Mypage_RefundPolicy.class);
                startActivity(intent8);
            }
        });
        // ????????? ????????????
        TextView btn9 = (TextView) view.findViewById(R.id.btn_use_reserves);
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent(getActivity(), Point_list.class);
                startActivity(intent9);
            }
        });

        // ???????????? ????????? ??????
        TextView btn10 = (TextView) view.findViewById(R.id.btn_get_reserves);
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lock_AppLockImpl.onad = true;
                if (rewardedAd.isLoaded()) {
                    Activity activityContext = getActivity();
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            myIncrement(mDatabase.child("users").child(user.getUid()), reward.getAmount());
                            Toast.makeText(getContext(), "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            Toast.makeText(getContext(), "?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                } else {
                    if(ad_limited){
                        Toast.makeText(getContext(), "?????? ???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "????????? ????????? ??? ??? ????????????. (?????????)", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //?????? ?????? ??????
        TextView btn11 = (TextView) view.findViewById(R.id.btn_show_purchased);
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(getActivity(), Purchased_list.class);
                startActivity(intent11);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        template = (TemplateView) rootView.findViewById(R.id.ad_mypage);
        if(((Navigation)Navigation.context).mSubKidsquiz){
            template.setVisibility(template.GONE);
        }
        MobileAds.initialize(getContext(), "ca-app-pub-9350891168282170~5512537782");
        //Test ?????? id : ca-app-pub-3940256099942544/2247696110
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-9350891168282170/4038307032");
        // ??? ???????????? ?????? id : ca-app-pub-9350891168282170/4038307032

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                template.setNativeAd(unifiedNativeAd);

            }
        });
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());

        rewardedAd = new RewardedAd(getContext(),"ca-app-pub-9350891168282170/3887848313");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                boolean ad_limited = false;

            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.d("Mypage", "????????? ?????? : " + dataSnapshot.getValue());
                    User user2 = dataSnapshot.getValue(User.class);
                    System.out.println(user2);
                    assert user2 != null;
                    if (!user2.name.isEmpty()) {
                        tv_nickname.setText(user2.name);
                    }
                    reserve.setText(Integer.toString(user2.myreserve));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myIncrement(DatabaseReference databaseReference, int reward){//??? ????????? ???????????????
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                u.myreserve = u.myreserve +reward;
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("myIncrement","??? reserve update ??????!");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,DataSnapshot currentData) {
                // Transaction completed
                Log.d("???????????? ????????? ??????", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void getivimage(){
        Thread mThread = new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        try {
            mThread.join();
            iv_profile.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}