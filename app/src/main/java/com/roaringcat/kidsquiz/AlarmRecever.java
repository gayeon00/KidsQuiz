package com.roaringcat.kidsquiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmRecever extends BroadcastReceiver{
    long START_TIME_IN_MILLIS;
    @Override
    public void onReceive(Context context, Intent intent) {
        START_TIME_IN_MILLIS = intent.getLongExtra("START_TIME_IN_MILLIS", 1000);
        Log.d("AlarmRecever : ", String.valueOf(START_TIME_IN_MILLIS));
        ((Nav_FragmentHome)Nav_FragmentHome.context).mButtonStarttimer.setEnabled(false);
        ((Nav_FragmentHome)Nav_FragmentHome.context).mStoptimer.setEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in_restart = new Intent(context, RestartService.class);
            in_restart.putExtra("START_TIME_IN_MILLIS", START_TIME_IN_MILLIS);
            context.startForegroundService(in_restart);
        } else {
            Log.e("퀴즈 팝업 시점", "AlarmRecever");
            Intent in_popup = new Intent(context, Quiz_Popup.class);
            in_popup.putExtra("time", START_TIME_IN_MILLIS);
            context.startService(in_popup);
        }
    }
}