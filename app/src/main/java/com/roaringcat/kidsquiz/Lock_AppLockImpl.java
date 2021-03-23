package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Lock_AppLockImpl extends Lock_AppLock implements Lock_PageListener {
	public static final String TAG = "DefaultAppLock";
	private static final String PASSWORD_PREFERENCE_KEY = "passcode";
	private static final String PASSWORD_SALT = "7xn7@c$";
	private SharedPreferences settings;
	private int liveCount;
	public static int visibleCount;
	public static boolean stoptimer = false;
	public static boolean onad = false;
	public static boolean btn_sub_clicked = false;
	private long lastActive;

	public Lock_AppLockImpl(Application app) {
		super();
		System.out.println("AppLockImpl");
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(app);
		this.settings = settings;
		this.liveCount = 0;
		this.visibleCount = 0;
	}

	public void enable() {
		Lock_BaseActivity.setListener(this);
	}

	public void disable() {
		Lock_BaseActivity.setListener(null);
	}

	public boolean checkPasscode(String passcode) {
		passcode = PASSWORD_SALT + passcode + PASSWORD_SALT;
		passcode = Lock_Encryptor.getSHA1(passcode);
		String storedPasscode = "";

		if (settings.contains(PASSWORD_PREFERENCE_KEY)) {
			storedPasscode = settings.getString(PASSWORD_PREFERENCE_KEY, "");
		}
		if (passcode.equalsIgnoreCase(storedPasscode)) {return true;}
		else {return false;}
	}

	public boolean setPasscode(String passcode) {
		SharedPreferences.Editor editor = settings.edit();
		if (passcode == null) {
			editor.remove(PASSWORD_PREFERENCE_KEY);
			editor.commit();
			this.disable();
		} else {
			passcode = PASSWORD_SALT + passcode + PASSWORD_SALT;
			passcode = Lock_Encryptor.getSHA1(passcode);
			editor.putString(PASSWORD_PREFERENCE_KEY, passcode);
			editor.commit();
			this.enable();
		}
		return true;
	}

	// Check if we need to show the lock screen at startup
	public boolean isPasscodeSet() {
		if (settings.contains(PASSWORD_PREFERENCE_KEY)) {return true;}
		return false;
	}

	private boolean isIgnoredActivity(Activity activity) {
		String clazzName = activity.getClass().getName();
		// ignored activities
		if (ignoredActivities.contains(clazzName)) {
			Log.d(TAG, "ignore activity " + clazzName);
			return true;
		}
		return false;
	}

	private boolean shouldLockSceen(Activity activity) {
		Log.d("souldlocksceen : visibleCount", String.valueOf(visibleCount));
		// already unlock
		if (activity instanceof Lock_AppLockActivity) {
			Lock_AppLockActivity ala = (Lock_AppLockActivity) activity;
			if (ala.getType() == Lock_AppLock.UNLOCK_PASSWORD) {
				Log.d(TAG, "already unlock activity");
				return false;
			}
		}
		// no pass code set
		if (!isPasscodeSet()) {
			Log.d(TAG, "lock passcode not set.");
			return false;
		}
		// no enough timeout
		long passedTime = System.currentTimeMillis() - lastActive;
		if (lastActive > 0 && passedTime <= lockTimeOut) {
			Log.d(TAG, "no enough timeout " + passedTime + " for "
					+ lockTimeOut);
			return false;
		}

		if(onad){
			onad = false;
			return false;
		}

		if (stoptimer){
			stoptimer = false;
			return false;
		}

		if (btn_sub_clicked){
			btn_sub_clicked = false;
			return false;
		}

		// start more than one page
		if (visibleCount > 1) {
			return false;
		}
		return true;
	}

	@Override
	public void onActivityPaused(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityPaused " + clazzName);
		if (isIgnoredActivity(activity)) {return;}
	}

	@Override
	public void onActivityResumed(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityResumed " + clazzName);
		if (isIgnoredActivity(activity)) {return;}
		if (shouldLockSceen(activity)) {
			Intent intent = new Intent(activity.getApplicationContext(),
					Lock_AppLockActivity.class);
			intent.putExtra(Lock_AppLock.TYPE, Lock_AppLock.UNLOCK_PASSWORD);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.getApplication().startActivity(intent);
		}
		lastActive = 0;
	}

	@Override
	public void onActivityCreated(Activity activity) {
		if (isIgnoredActivity(activity)) {return;}
		liveCount++;
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		if (isIgnoredActivity(activity)) {return;}
		liveCount--;
		if (liveCount == 0) {
			lastActive = System.currentTimeMillis();
			Log.d(TAG, "set last active " + lastActive);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity) {
		if (isIgnoredActivity(activity)) {return;}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityStarted " + clazzName);
		if (isIgnoredActivity(activity)) {return;}
		visibleCount++;
		Log.d("visibleCount", String.valueOf(visibleCount));
	}

	@Override
	public void onActivityStopped(Activity activity) {
		String clazzName = activity.getClass().getName();
		Log.d(TAG, "onActivityStopped " + clazzName);
		if (isIgnoredActivity(activity)) {return;}
		visibleCount--;
		Log.d("visibleCount", String.valueOf(visibleCount));
		if (visibleCount == 0) {
			lastActive = System.currentTimeMillis();
			Log.d(TAG, "set last active " + lastActive);
		}
	}
}
