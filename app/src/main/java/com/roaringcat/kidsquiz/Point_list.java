package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;

public class Point_list extends Lock_BaseActivity implements View.OnClickListener {
    boolean mSubKidsquiz = false;
    public TemplateView template;
    private ArrayList<Point_item> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_list);

        ListView listView = (ListView) findViewById(R.id.point_listview);

        data = new ArrayList<>();

        Point_item item1 = new Point_item(R.drawable.point_br_single, "<베스킨라빈스> 싱글 레귤러", "3200");
        Point_item item2 = new Point_item(R.drawable.point_br_double, "<베스킨라빈스> 더블 주니어", "4300");
        Point_item item3 = new Point_item(R.drawable.point_br_pi, "<베스킨라빈스> 파인트", "8200");
        Point_item item4 = new Point_item(R.drawable.point_starbucks_americano, "<스타벅스> 아메리카노 Tall", "4100");
        Point_item item5 = new Point_item(R.drawable.point_starbucks_latte, "<스타벅스> 카페라떼 Tall", "4600");
        Point_item item6 = new Point_item(R.drawable.point_cu_oh, "<CU> 5000원 상품권", "5000");
        Point_item item7 = new Point_item(R.drawable.point_cu_man, "<CU> 10000원 상품권", "10000");
        Point_item item8 = new Point_item(R.drawable.point_gs_oh, "<GS> 5000원 상품권", "5000");
        Point_item item9 = new Point_item(R.drawable.point_gs_man, "<GS> 10000원 상품권", "10000");

        data.add(item1);
        data.add(item2);
        data.add(item3);
        data.add(item4);
        data.add(item5);
        data.add(item6);
        data.add(item7);
        data.add(item8);
        data.add(item9);

        /* 리스트 속의 아이템 연결 */
        Point_adapter adapter = new Point_adapter(this, R.layout.point_item, data);
        listView.setAdapter(adapter);

        /* 아이템 클릭시 작동 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Point_clicked.class);
                /* putExtra의 첫 값은 식별 태그, 뒤에는 다음 화면에 넘길 값 */
                intent.putExtra("profile", Integer.toString(data.get(position).getProfile()));
                intent.putExtra("info", data.get(position).getInfo());
                intent.putExtra("phone", data.get(position).getPhone());
                startActivity(intent);
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
        Log.d("구독 여부 출력 : ", String.valueOf(((Navigation)Navigation.context).mSubKidsquiz));
    }
}