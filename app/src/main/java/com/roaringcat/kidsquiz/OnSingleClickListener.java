package com.roaringcat.kidsquiz;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 5000;
    private long mLastClickTime = 0;
    public abstract void onSingleClick(View v);
    @Override
    public void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if(elapsedTime>MIN_CLICK_INTERVAL){
            onSingleClick(v);
        }
    }
}