package com.roaringcat.kidsquiz;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class Navigation extends Lock_BaseActivity{
    public static long START_TIME_IN_MILLIS = 0;
    public static Navigation nav;
    public static Context context;
    public boolean mSubKidsquiz;
    private BottomNavigationView mBottomNV;
    public String nickName;
    public String photoUrl;
    boolean first = true;
    public static boolean keeppopup;
    int prev_frag_id = 0;
    static final String TAG = "Subscribe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("nav : ","onCreate");
        nav = Navigation.this;
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_bottom_bar);
        context = this;
        Intent intent = getIntent();
        mSubKidsquiz = intent.getBooleanExtra("mSubKidsquiz", false);
        keeppopup = intent.getBooleanExtra("keeppopup", false);
        nickName = intent.getStringExtra("nickName"); // MainActivity로 부터 닉네임 전달받음.
        photoUrl = intent.getStringExtra("photoUrl"); // MainActivity로 부터 프로필사진 Url 전달받음.
        mBottomNV = findViewById(R.id.nav_view);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                BottomNavigate(menuItem.getItemId());
                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 아래 부분에 가로/세로 모드 별로 리소스 재정의나 행동들을 해주면 된다.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {}
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){ }
    }

    @Override
    protected void onResume() {
        Log.d("nav : ","onResume");
        super.onResume();
        Log.d("nav 구독여부 확인", String.valueOf(mSubKidsquiz));
    }

    @Override
    protected void onPause() {
        Log.d("nav : ", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("nav : ","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("nav : ","onDestroy");
        if(keeppopup){
            Intent nav = new Intent(this, Navigation.class);
            nav.putExtra("keeppopup",keeppopup);
            nav.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(nav);
//            setAlarmTimer();
        }
        else{
            Quiz_Timer.keeppopup = false;
        }
        super.onDestroy();
    }

    private void BottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (first){
            fragmentTransaction.setCustomAnimations(R.anim.nothing, R.anim.nothing, R.anim.nothing, R.anim.nothing);//1, 2
            first = false;
        }
        else if (prev_frag_id < id){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_in_right);//1, 2
        }
        else{
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);//1, 2
        }
        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (id == R.id.navigation_1) {
                fragment = new Nav_FragmentHome();
            } else if (id == R.id.navigation_2){
                fragment = new Nav_FragmentStatistics();
            }else {
                fragment = new Nav_FragmentMypage();
            }
            fragmentTransaction.add(R.id.content_layout, fragment,tag);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        if (id == R.id.navigation_1) {
            if (keeppopup) {
                Button mButtonStarttimer = Nav_FragmentHome.context.mButtonStarttimer;
                Button mStoptimer = Nav_FragmentHome.context.mStoptimer;
                mButtonStarttimer.setEnabled(false); // 시작 버튼 비활성화
                mStoptimer.setEnabled(true); // 종료 버튼 활성화
            }
        }
        prev_frag_id = id;
    }

    @Override
    public void onBackPressed() {
        if(keeppopup){
            Intent homeIntent = new Intent();
            homeIntent.setAction(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(homeIntent);
        }
        else{
            super.onBackPressed();
        }
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmRecever.class);
        intent.putExtra("START_TIME_IN_MILLIS", START_TIME_IN_MILLIS);
        Log.d("setalarmtimer", String.valueOf(START_TIME_IN_MILLIS));
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }
}