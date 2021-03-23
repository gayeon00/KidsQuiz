package com.roaringcat.kidsquiz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RestartService extends Service {
    long START_TIME_IN_MILLIS;
    public RestartService() {}

    @Override
    public void onCreate() {super.onCreate();}

    @Override
    public void onDestroy() {super.onDestroy();}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        START_TIME_IN_MILLIS = intent.getLongExtra("START_TIME_IN_MILLIS", 1000);
        Log.d("RestartService", String.valueOf(START_TIME_IN_MILLIS));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(null);
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
        Log.e("퀴즈 팝업 시점", "RestartService");
        Intent in = new Intent(this, Quiz_Popup.class);
        in.putExtra("time", START_TIME_IN_MILLIS);
//        startService(in);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(in);
        } else {
            startService(in);
        }
        stopForeground(true);
        stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}