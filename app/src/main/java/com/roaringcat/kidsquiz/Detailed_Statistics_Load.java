package com.roaringcat.kidsquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

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

public class Detailed_Statistics_Load extends Lock_BaseActivity {

    private DatabaseReference mUserReference;
    private DatabaseReference mtotalUserCorrectRef;
    private DatabaseReference mtotalUserCorrectRef_korean;
    private DatabaseReference mtotalUserCorrectRef_math;
    private DatabaseReference mtotalUserCorrectRef_perception;

    public double kor_correct_rate=0;
    public double  mat_correct_rate=0;
    public double  per_correct_rate=0;
    public double  kor_percentile=0;
    public double  mat_percentile=0;
    public double  per_percentile=0;
    public double  percentile=0;

    public Map<String, Double> personalCorrectRate = new HashMap<>();
    //    public ArrayList<Double> personalPercentile = new ArrayList<Double>();
    public List<Map.Entry<String, Float>> UserListforPercentile = new ArrayList<>();
    Map<String, Object> totalUserCorrectRate = new HashMap<>();

    int selected_age = 2;
    int tc_Status = 0;
    int count = 0;
    private Handler handler = new Handler();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed__statistics__load);

        Intent main_intent = getIntent();
        selected_age = (int) main_intent.getSerializableExtra("selectedage");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        //여기는 fragmentstatistics 초기화면에서 2가 기본값으로 선택돼 있으니까 거기에 맞춰서 2살에 해당하는거 다 해줌
        //2세일때 정답률 (overall, korean, perception, math 계산해주기)
        readData(mUserReference, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("mUserReference_ONSTART", String.valueOf(personalCorrectRate.size()));
                if (personalCorrectRate.size() == 6) {
                    Intent intent2 = new Intent(getApplicationContext(), Detailed_Statistics.class);
                    intent2.putExtra("personalCorrectRate", (Serializable) personalCorrectRate);
                    startActivity(intent2);
                    finish();
                }
            }
            @Override
            public void onStart() {
                //whatever you need to do onStart
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
            }
        });

        //국어 퍼센타일 뽑아내기
        mtotalUserCorrectRef_korean = FirebaseDatabase.getInstance().getReference().child("total_user_correct_rate").child("korean"+selected_age);
        readData_korean(mtotalUserCorrectRef_korean, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("mtotalUserCorrectRef_korean_ONSTART", String.valueOf(personalCorrectRate.size()));
                if (personalCorrectRate.size() == 6) {
                    Intent intent2 = new Intent(getApplicationContext(), Detailed_Statistics.class);
                    intent2.putExtra("personalCorrectRate", (Serializable) personalCorrectRate);
                    startActivity(intent2);
                    finish();
                }
            }
            @Override
            public void onStart() {
                //whatever you need to do onStart
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
            }
        });
        //수학 뽑아내기
        mtotalUserCorrectRef_math = FirebaseDatabase.getInstance().getReference().child("total_user_correct_rate").child("math"+selected_age);
        readData_math(mtotalUserCorrectRef_math, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("mtotalUserCorrectRef_math_ONSTART", String.valueOf(personalCorrectRate.size()));
                if (personalCorrectRate.size() == 6) {
                    Intent intent2 = new Intent(getApplicationContext(), Detailed_Statistics.class);
                    intent2.putExtra("personalCorrectRate", (Serializable) personalCorrectRate);
                    startActivity(intent2);
                    finish();
                }
            }
            @Override
            public void onStart() {
                //whatever you need to do onStart
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
            }
        });
        //perception 퍼센타일 뽑아내기
        mtotalUserCorrectRef_perception = FirebaseDatabase.getInstance().getReference().child("total_user_correct_rate").child("perception"+selected_age);
        readData_perception(mtotalUserCorrectRef_perception, new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Log.d("mtotalUserCorrectRef_perception_ONSTART", String.valueOf(personalCorrectRate.size()));
                if (personalCorrectRate.size() == 6) {
                    Intent intent2 = new Intent(getApplicationContext(), Detailed_Statistics.class);
                    intent2.putExtra("personalCorrectRate", (Serializable) personalCorrectRate);
                    startActivity(intent2);
                    finish();
                }
            }
            @Override
            public void onStart() {
                //whatever you need to do onStart
                Log.d("ONSTART", "Started");
            }

            @Override
            public void onFailure() {
            }
        });
        // 정답률 progressbar
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circle_progressbar_total);
        final ProgressBar totalCorrectRateProgressBar = (ProgressBar)findViewById(R.id.total_circleProgressbar); // 프로그레스바 만들어주기
        totalCorrectRateProgressBar.setProgress(0);
        totalCorrectRateProgressBar.setSecondaryProgress(100);
        totalCorrectRateProgressBar.setMax(100);
        totalCorrectRateProgressBar.setProgressDrawable(drawable);

        //프로그레스 바가 100이 되면 넘어가게
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (tc_Status <100) {
                    tc_Status += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            totalCorrectRateProgressBar.setProgress(tc_Status);
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void readData_korean(DatabaseReference ref, OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ////////////////////////////////////////////////////////////////////////////////////
                System.out.println("국어 :");
                kor_percentile = getPercentile(dataSnapshot);
                personalCorrectRate.put("kor_percentile",kor_percentile);
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }
    private void readData_math(DatabaseReference ref, OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("수학 :");
                mat_percentile = getPercentile(dataSnapshot);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                personalCorrectRate.put("mat_percentile",mat_percentile);
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }
    private void readData_perception(DatabaseReference ref, OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("퍼센타일 :");
                per_percentile = getPercentile(dataSnapshot);
                /////////////////////////////////////////////////////////
                personalCorrectRate.put("per_percentile",per_percentile);
                System.out.println("인텐트임, PersonalCorrectRate : "+personalCorrectRate);
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    private void readData(DatabaseReference ref, OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);

                switch (selected_age){
                    case 2:
                        if(u.korean_total_count_2==0){
                            kor_correct_rate = 0;
                        }else{
                            kor_correct_rate = ((float)(u.korean_count_2)/u.korean_total_count_2)*100;
                        }
                        if(u.math_total_count_2==0){
                            mat_correct_rate = 0;
                        }else{
                            mat_correct_rate = ((float)(u.math_count_2)/u.math_total_count_2)*100;
                        }
                        if(u.perception_total_count_2==0){
                            per_correct_rate = 0;
                        }else{
                            per_correct_rate = ((float)(u.perception_count_2)/u.perception_total_count_2)*100;
                        }
                        break;
                    case 3:
                        if(u.korean_total_count_3==0){
                            kor_correct_rate = 0;
                        }else{
                            kor_correct_rate = ((float)(u.korean_count_3)/u.korean_total_count_3)*100;
                        }
                        if(u.math_total_count_3==0){
                            mat_correct_rate = 0;
                        }else{
                            mat_correct_rate = ((float)(u.math_count_3)/u.math_total_count_3)*100;
                        }
                        if(u.perception_total_count_3==0){
                            per_correct_rate = 0;
                        }else{
                            per_correct_rate = ((float)(u.perception_count_3)/u.perception_total_count_3)*100;
                        }
                        break;
                    case 4:
                        if(u.korean_total_count_4==0){
                            kor_correct_rate = 0;
                        }else{
                            kor_correct_rate = ((float)(u.korean_count_4)/u.korean_total_count_4)*100;
                        }
                        if(u.math_total_count_4==0){
                            mat_correct_rate = 0;
                        }else{
                            mat_correct_rate = ((float)(u.math_count_4)/u.math_total_count_4)*100;
                        }
                        if(u.perception_total_count_4==0){
                            per_correct_rate = 0;
                        }else{
                            per_correct_rate = ((float)(u.perception_count_4)/u.perception_total_count_4)*100;
                        }
                        break;
                    case 5:
                        if(u.korean_total_count_5==0){
                            kor_correct_rate = 0;
                        }else{
                            kor_correct_rate = ((float)(u.korean_count_5)/u.korean_total_count_5)*100;
                        }
                        if(u.math_total_count_5==0){
                            mat_correct_rate = 0;
                        }else{
                            mat_correct_rate = ((float)(u.math_count_5)/u.math_total_count_5)*100;
                        }
                        if(u.perception_total_count_5==0){
                            per_correct_rate = 0;
                        }else{
                            per_correct_rate = ((float)(u.perception_count_5)/u.perception_total_count_5)*100;
                        }
                        break;
                    case 6:
                        if(u.korean_total_count_6==0){
                            kor_correct_rate = 0;
                        }else{
                            kor_correct_rate = ((float)(u.korean_count_6)/u.korean_total_count_6)*100;
                        }
                        if(u.math_total_count_6==0){
                            mat_correct_rate = 0;
                        }else{
                            mat_correct_rate = ((float)(u.math_count_6)/u.math_total_count_6)*100;
                        }
                        if(u.perception_total_count_6==0){
                            per_correct_rate = 0;
                        }else{
                            per_correct_rate = ((float)(u.perception_count_6)/u.perception_total_count_6)*100;
                        }
                        break;
                }

                personalCorrectRate.put("kor_correct_rate",kor_correct_rate);
                personalCorrectRate.put("mat_correct_rate",mat_correct_rate);
                personalCorrectRate.put("per_correct_rate",per_correct_rate);
                Log.d("personalCorrectRate보자", String.valueOf(personalCorrectRate.size()));
                System.out.println("personalCorrectRate" + personalCorrectRate);

                System.out.println("kor_correct_rate : "+kor_correct_rate);
                System.out.println("mat_correct_rate : "+mat_correct_rate);
                System.out.println("per_correct_rate : "+per_correct_rate);
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public double getPercentile(DataSnapshot dataSnapshot){
        Map<String, Float> totalUserCorrectRate2 = new HashMap<String, Float>();
        totalUserCorrectRate = (Map<String, Object>)dataSnapshot.getValue();
        for (Map.Entry<String, Object> entry : totalUserCorrectRate.entrySet()) {
            System.out.println("[key]:" + entry.getKey() + ", [value]:" + entry.getValue());
            //새로운 해시맵에 넣기
            totalUserCorrectRate2.put(entry.getKey(),Float.parseFloat(entry.getValue().toString()));
        }
        System.out.println(totalUserCorrectRate2+"가연");

        // Map.Entry 리스트 작성
        List<Map.Entry<String, Float>> list_entries = new ArrayList<Map.Entry<String, Float>>(totalUserCorrectRate2.entrySet());

        // 비교함수 Comparator를 사용하여 내림 차순으로 정렬
        Collections.sort(list_entries, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                // 내림 차순으로 정렬
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //list_entries는 정렬된놈!
        System.out.println("내림 차순 정렬 : "+list_entries);

        // 결과 출력
        for(Map.Entry<String, Float> entry : list_entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        UserListforPercentile.clear();
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
        percentile = ((float)(i+1)/list_entries.size())*100;
        System.out.println("현재 유저는 : "+(i+1)+"에 있고 전체 사이즈는"+list_entries.size()+", 백분위는"+percentile);
        UserListforPercentile.clear();
        return percentile;
    }
}