package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Purchased_list extends Lock_BaseActivity implements View.OnClickListener {
    boolean mSubKidsquiz = false;
    public TemplateView template;
    private ArrayList<Purchased_item> data = null;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchased_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        ListView listView = (ListView) findViewById(R.id.point_listview);

        mUserReference = mDatabase.child("PurchaseRequest").child(user.getUid()); //push??? ??????????????? ????????????(?????????) ???????????? ??????
        putdata(mUserReference, new OnGetDataListener(){

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Purchased_adapter adapter = new Purchased_adapter(getApplicationContext(), R.layout.purchased_item, data);
                listView.setAdapter(adapter);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void putdata(DatabaseReference ref, OnGetDataListener listener){
        listener.onStart();
        List<Map<String,HashMap<String,String>>> mapList = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { //push??? ??????????????? ????????????(?????????)
                    Map<String, HashMap<String,String>> map = (Map<String, HashMap<String,String>>) snapshot.getValue();
                    System.out.println("purchaseditem --> " +  map); //map.value??? push??? ??? ?????? ?????? ???????????? Object???????????? ???????????????
                    System.out.println("purchaseditem~~ --> " +  map.get("-MUM2Td3iHAm7n29hCe-"));
                    for(String keys : map.keySet()){
                        Map<String, HashMap<String,String>> temp = new HashMap<>();
                        temp.put(keys,map.get(keys));
                        mapList.add(temp);
                    }
                    System.out.println("mapList : " + mapList);

                    data = new ArrayList<>();

                    Map<String,String> purchaseitem = new HashMap<>();

                    //????????? ?????????????????? ?????? ?????? ?????? ?????? ?????? ??? ??????????
                    //DB : ?????????, ?????????, ?????? ????????????, ???????????? ??? ?????? : ?????????, ????????????, ?????????, ??????, ????????????
                    for (Map<String,HashMap<String,String>> obj : mapList) { //mapList??? ????????? ???????????? ??? ??????????????? ??? ???????????? obj??? ????????? ?????? ?????????
                        System.out.println("purchasedobj : " + obj); //obj??? ???????????? ??????
                        System.out.println("purchaseitem : " + purchaseitem);
                        for(String keys : obj.keySet()){
                            mDatabase.child("PurchaseRequestAccepted").child(user.getUid()).child(keys).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        state = "????????????";
                                        System.out.println("state : "+state);

                                        data.add(new Purchased_item(R.drawable.kidsquiz_character, obj.get(keys).get("time"), obj.get(keys).get("itemname"),obj.get(keys).get("itemprice"),state));

                                        System.out.println("purchasemap : " + data);
                                    }
                                    else{
                                        data.add(new Purchased_item(R.drawable.kidsquiz_character, obj.get(keys).get("time"), obj.get(keys).get("itemname"),obj.get(keys).get("itemprice"),"????????????"));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    listener.onSuccess(snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        template = (TemplateView) findViewById(R.id.ad_use_point);
        if(((Navigation)Navigation.context).mSubKidsquiz){
            template.setVisibility(template.GONE);
        }
        MobileAds.initialize((this), "ca-app-pub-9350891168282170~5512537782");
        //Test ?????? id : ca-app-pub-3940256099942544/2247696110
        AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-9350891168282170/4038307032");
        // ??? ???????????? ?????? id : ca-app-pub-9350891168282170/4038307032
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                template.setNativeAd(unifiedNativeAd);
            }
        });
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
        Log.d("?????? ?????? ?????? : ", String.valueOf(((Navigation)Navigation.context).mSubKidsquiz));
    }
}