package com.roaringcat.kidsquiz;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Quiz_Timer extends Service {
    public static boolean keeppopup;
    Button mButtonStarttimer;
    Button mStoptimer;
    long START_TIME_IN_MILLIS;
    public static long mTimeLeftInMillis;                         //남은 시간
    private long mEndTime;                                  //끝날시간
    private CountDownTimer mCountDownTimer;                 //타이머
    private boolean mTimerRunning;                          //타이머가 돌고있는지
    // Channel에 대한 id 생성

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mButtonStarttimer = ((Nav_FragmentHome) Nav_FragmentHome.context).mButtonStarttimer;
            mStoptimer = ((Nav_FragmentHome) Nav_FragmentHome.context).mStoptimer;
        }catch(NullPointerException e){
            Toast.makeText(this,"오류가 발생했습니다. 앱 종료후 재실행 해주세요.", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("타이머 종료","타이머 종료 in Timer");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setchannel();
        if (!Navigation.keeppopup){
            stopSelf();
        }
        else {
            mButtonStarttimer.setEnabled(false); // 시작 버튼 비활성화
            mStoptimer.setEnabled(true); // 종료 버튼 활성화
            START_TIME_IN_MILLIS = intent.getLongExtra("time", 1000);
            Log.d("타이머 시작", "타이머 시작 in Timer" + START_TIME_IN_MILLIS);
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            startTimer();
        }
//        return START_NOT_STICKY;
        return START_REDELIVER_INTENT;
    }

    private void setchannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            if(keeppopup) {
                builder.setContentTitle("퀴즈 준비 중");
            }
            else{
                builder.setContentTitle("퀴즈 종료 중");
            }
            builder.setContentText(null);
            Intent notificationIntent = new Intent(this, Navigation.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder.setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_NONE));
            }

            Notification notification = builder.build();
            startForeground(9, notification);
            stopForeground(true);
        }
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {              //한 틱이 지날때마다
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {                //타이머가 끝났을 때
                mTimerRunning = false;
                startMain();  //권한확인 및 팝업실행
            }
        }.start();
        mTimerRunning = true;
    }

    void startMain() {
        Log.d("팝업 시작","팝업 시작 in Timer" + Navigation.keeppopup + " " + START_TIME_IN_MILLIS);
        if (Navigation.keeppopup == true) {
            Log.e("퀴즈 팝업 시점", "Quiz_timer");
            Intent popupIntent = new Intent(this, Quiz_Popup.class);
            popupIntent.putExtra("time", START_TIME_IN_MILLIS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(popupIntent);
            }
            else {
                startService(popupIntent);
            }
        }
        else{
            stopSelf();
        }
    }
}
