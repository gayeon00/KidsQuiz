package com.roaringcat.kidsquiz;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

public class Quiz_Wrong extends Service {
    NotificationManager notificationManager;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    WindowManager wm;
    View mView;
    //소리 재생
    SoundPool sound;// maxStreams, streamType, srcQuality
    int soundId;
    int streamId;
    WindowManager.LayoutParams params;
    private static final int NOTIFICATION_ID = 2;
    @SuppressLint("ResourceType")
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        sound.stop(streamId);
        sound.release();
        sound = null;
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopSelf();
        }
    }
    @SuppressLint("RtlHardcoded")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setchannel();

        //소리 재생
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);// maxStreams, streamType, srcQuality
        soundId = sound.load(this, R.raw.fail, 1);
        sound.setOnLoadCompleteListener (new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                streamId = sound.play(soundId, 1.0F, 1.0F,  100,  0,  1.2F);
            }
        });

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        mView = inflate.inflate(R.layout.popup_wrong, null);
        wm.addView(mView, params); // 윈도우에 layout 을 추가 한다
        mView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mView.setAlpha(1);
                mView.animate().alpha(0).setDuration(650);
            }
        }, 400);// 딜레이를 준 후 시작
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                wm.removeView(mView);
                stopSelf();
            }
        }, 1500);// 딜레이를 준 후 시작

        return START_NOT_STICKY;
    }



    private void setchannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("오답입니다.");
            builder.setContentText("정답을 확인해 주세요.");
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
}