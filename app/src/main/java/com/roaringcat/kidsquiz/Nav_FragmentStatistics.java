package com.roaringcat.kidsquiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nav_FragmentStatistics extends Fragment{
    public static Nav_FragmentStatistics context;
    ProgressBar totalPercentileProgressBar;
    int tc_Status = 0;
    int tp_Status = 0;
    double total_correct_rate;
    double total_percentile;
    int selected_age = 2;
    boolean mSubKidsquiz = false;
    TextView tvTotalCorrectRate;
    TextView tvTotalPercentile;
    private boolean mIsBound;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private DatabaseReference mtotalUserCorrectRef;
    User u;
    TemplateView template;
    Map<String, Object> totalUserCorrectRate = new HashMap<>();
    public List<Map.Entry<String, Float>> UserListforPercentile = new ArrayList<>();
    View rootView;


    public ArrayList<Float> personalCorrectRate;
    private Handler handler = new Handler();

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
//????????? fragmentstatistics ?????????????????? 2??? ??????????????? ????????? ???????????? ????????? ????????? 2?????? ??????????????? ??? ??????
        //2????????? ????????? (overall, korean, perception, math ???????????????)
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
                if(u.overall_total_count_2==0){
                    total_correct_rate = 0;
                }else{
                    total_correct_rate = ((double)(u.overall_count_2)/u.overall_total_count_2)*100;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //??????????????? 2??? ?????? ???????????? ????????? ????????? ????????????
        mtotalUserCorrectRef = FirebaseDatabase.getInstance().getReference().child("total_user_correct_rate").child("overall2");
        mtotalUserCorrectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //float???????????? ?????? ????????? ????????? ????????? totalUserCorrectRate2 ??????
                Map<String, Float> totalUserCorrectRate2 = new HashMap<String, Float>();
                totalUserCorrectRate = (Map<String, Object>)snapshot.getValue();
                for (Map.Entry<String, Object> entry : totalUserCorrectRate.entrySet()) {
//                    System.out.println("[key]:" + entry.getKey() + ", [value]:" + entry.getValue());
                    //????????? ???????????? ??????
                    totalUserCorrectRate2.put(entry.getKey(),Float.parseFloat(entry.getValue().toString()));
                }
                System.out.println(totalUserCorrectRate2+"??????");

                // Map.Entry ????????? ??????
                List<Map.Entry<String, Float>> list_entries = new ArrayList<Map.Entry<String, Float>>(totalUserCorrectRate2.entrySet());

                // ???????????? Comparator??? ???????????? ?????? ???????????? ??????
                Collections.sort(list_entries, (o1, o2) -> {
                    // ?????? ???????????? ??????
                    return o2.getValue().compareTo(o1.getValue());
                });

                //list_entries??? ????????????!
                System.out.println("?????? ?????? ??????(??????) : "+list_entries);

                // ?????? ??????
                for(Map.Entry<String, Float> entry : list_entries) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                UserListforPercentile.addAll(list_entries);
                int i = 0;
                for(Map.Entry<String, Float> map : UserListforPercentile){
                    if (map.getKey().equals(user.getUid())){
                        break;
                    }
                    i++;
                }
                if(i>=UserListforPercentile.size()){
                    i -= 1;
                }
                total_percentile = ((float)(i+1)/list_entries.size())*100;
                System.out.println("?????? ????????? : "+(i+1)+"??? ?????? ?????? ????????????"+list_entries.size()+", ????????????"+total_percentile);
                UserListforPercentile.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // SeekBar
        SeekBar ageSelectSeekbar = (SeekBar)view.findViewById(R.id.age_select_seekbar);
        ageSelectSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // SeekBar ???????????? ??? ????????? ??????
            public void onProgressChanged(SeekBar ageSelectSeekbar, int progress, boolean fromUser) {
            }
            @Override
            // SeekBar ?????? ???????????? ??? ????????? ??????
            public void onStartTrackingTouch(SeekBar ageSelectSeekbar) {
            }
            @Override
            // SeekBar ?????? ??????????????? ??? ????????? ??????
            public void onStopTrackingTouch(SeekBar ageSelectSeekbar) {
                selected_age = ageSelectSeekbar.getProgress() + 2;  // seekbar value ???????????? + 2 ????????? selected_age??? ??????

                ValueEventListener correctRateListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);

                        switch (selected_age){
                            case(2):
                                if(u.overall_total_count_2==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_2)/u.overall_total_count_2)*100;
                                }
                                break;
                            case(3):
                                if(u.overall_total_count_3==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_3)/u.overall_total_count_3)*100;
                                }
                                break;
                            case(4):
                                if(u.overall_total_count_4==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_4)/u.overall_total_count_4)*100;
                                }
                                break;
                            case(5):
                                if(u.overall_total_count_5==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_5)/u.overall_total_count_5)*100;
                                }
                                break;
                            case(6):
                                if(u.overall_total_count_6==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_6)/u.overall_total_count_6)*100;
                                }
                                break;
                            default:
                                if(u.overall_total_count_2==0){
                                    total_correct_rate = 0;
                                }else{
                                    total_correct_rate = ((float)(u.overall_count_2)/u.overall_total_count_2)*100;
                                }
                                break;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                };
                // Attach a listener to read the data at our posts reference
                mUserReference.addValueEventListener(correctRateListener);

                //?????? ????????? ?????? ????????? ?????????????????? ????????? ??????
                //??????????????? 2??? ?????? ???????????? ????????? ????????? ????????????
                mtotalUserCorrectRef = FirebaseDatabase.getInstance().getReference().child("total_user_correct_rate").child("overall"+selected_age);
                mtotalUserCorrectRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //float???????????? ?????? ????????? ????????? ????????? totalUserCorrectRate2 ??????
                        Map<String, Float> totalUserCorrectRate2 = new HashMap<String, Float>();
                        totalUserCorrectRate = (Map<String, Object>)snapshot.getValue();
                        for (Map.Entry<String, Object> entry : totalUserCorrectRate.entrySet()) {
//                            System.out.println("[key]:" + entry.getKey() + ", [value]:" + entry.getValue());
                            //????????? ???????????? ??????
                            totalUserCorrectRate2.put(entry.getKey(),Float.parseFloat(entry.getValue().toString()));
                        }
                        System.out.println(totalUserCorrectRate2+"??????");

                        // Map.Entry ????????? ??????
                        List<Map.Entry<String, Float>> list_entries = new ArrayList<Map.Entry<String, Float>>(totalUserCorrectRate2.entrySet());
//                        System.out.println(list_entries+"????????? ?????????");

                        // ???????????? Comparator??? ???????????? ?????? ???????????? ??????
                        Collections.sort(list_entries, new Comparator<Map.Entry<String, Float>>() {
                            @Override
                            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                                // ?????? ???????????? ??????
                                return o2.getValue().compareTo(o1.getValue());
                            }
                        });

                        //list_entries??? ????????????!
                        System.out.println("?????? ?????? ??????(??????) : "+list_entries);

                        // ?????? ??????
                        for(Map.Entry<String, Float> entry : list_entries) {
                            System.out.println(entry.getKey() + " : " + entry.getValue());
                        }

                        UserListforPercentile.addAll(list_entries);
                        int i = 0;
                        for(Map.Entry<String, Float> map : UserListforPercentile){
                            if (map.getKey().equals(user.getUid())){
                                System.out.println(i);
                                break;
                            }
                            i++;
                        }
                        if(i>=UserListforPercentile.size()){
                            i -= 1;
                        }
                        total_percentile = ((float)(i+1)/list_entries.size())*100;
                        System.out.println("?????? ????????? : "+(i+1)+"??? ?????? ?????? ????????????"+list_entries.size()+", ????????????"+total_percentile);
                        UserListforPercentile.clear();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });

        // ????????? progressbar
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circle_progressbar_total);
        final ProgressBar totalCorrectRateProgressBar = (ProgressBar)view.findViewById(R.id.total_circleProgressbar);
        totalCorrectRateProgressBar.setProgress(0);
        totalCorrectRateProgressBar.setSecondaryProgress(100);
        totalCorrectRateProgressBar.setMax(100);
        totalCorrectRateProgressBar.setProgressDrawable(drawable);

        // ????????? progressbar
        totalPercentileProgressBar = getView().findViewById(R.id.total_horizontal_progressBar);

        // ?????????, ????????? ????????? ????????? ????????? ?????? tv
        tvTotalCorrectRate = (TextView)view.findViewById(R.id.total_correct_rate);
        tvTotalPercentile = (TextView)view.findViewById(R.id.total_percentile);

        // ?????? ?????? ????????? ???
        Button btnShowResult = (Button)view.findViewById(R.id.showResult);
        btnShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(selected_age);  // selected_age ????????? ?????????????
                // ???????????? ?????????
                tc_Status = 0;
                tp_Status = 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // ????????? ProgressBar ???????????????
                        if(total_correct_rate==0) {
                            tc_Status = 0;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    totalCorrectRateProgressBar.setProgress(tc_Status);
                                    tvTotalCorrectRate.setText(tc_Status + "%");
                                }
                            });
                            try {
                                Thread.sleep(8);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            while (tc_Status < total_correct_rate) {
                                tc_Status += 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        totalCorrectRateProgressBar.setProgress(tc_Status);
                                        tvTotalCorrectRate.setText(tc_Status + "%");
                                    }
                                });
                                try {
                                    Thread.sleep(8);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.e("total_percentile", String.valueOf(total_percentile));
                        if(total_percentile==100){
                            tp_Status = 0;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    totalPercentileProgressBar.setProgress(tp_Status);
                                    tvTotalPercentile.setText("100%");
                                }
                            });
                            try {
                                Thread.sleep(8);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else{
                            // ????????? ProgressBar ???????????????
                            while(tp_Status < total_percentile) {
                                tp_Status += 1;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(tp_Status <100-total_percentile) {
                                            totalPercentileProgressBar.setProgress(tp_Status);
                                        }
                                        tvTotalPercentile.setText(tp_Status + "%");
                                    }
                                });
                                try {
                                    Thread.sleep(8);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
        });

        // ???????????? ???????????? ?????? ????????? ???
        Button btnShowDetail = (Button)view.findViewById(R.id.showDetail);
        btnShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????? ???????????? Detailed_Statistics_load
                Log.d("sub? : ", String.valueOf(mSubKidsquiz));
                if(mSubKidsquiz) {
                    Intent intent2 = new Intent(getActivity(), Detailed_Statistics_Load.class);
                    intent2.putExtra("selectedage",selected_age);
                    startActivity(intent2);
                }
                //?????? ??????????????? Detailed_Statistics_Promotion
                else{
                    Intent intent2 = new Intent(getActivity(), Detailed_Statistics_Promotion.class);
                    intent2.putExtra("selectedage", selected_age);
                    startActivity(intent2);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        template = (TemplateView) rootView.findViewById(R.id.ad_stat);
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
    }
}