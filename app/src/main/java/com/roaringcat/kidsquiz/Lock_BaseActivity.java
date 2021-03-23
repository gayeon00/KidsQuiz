package com.roaringcat.kidsquiz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Lock_BaseActivity extends AppCompatActivity {
	private static Lock_PageListener pageListener;
	public static void setListener(Lock_PageListener listener) {
		pageListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("BaseActivity");
		if (pageListener != null) {
			pageListener.onActivityCreated(this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (pageListener != null) {
			pageListener.onActivityStarted(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pageListener != null) {
			pageListener.onActivityResumed(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (pageListener != null) {
			pageListener.onActivityPaused(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (pageListener != null) {
			pageListener.onActivityStopped(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pageListener != null) {
			pageListener.onActivityDestroyed(this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (pageListener != null) {
			pageListener.onActivitySaveInstanceState(this);
		}
	}
}
