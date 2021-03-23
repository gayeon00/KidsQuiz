package com.roaringcat.kidsquiz;

import android.app.Application;

public class Lock_LockManager {

	private volatile static Lock_LockManager instance;
	private Lock_AppLock curAppLocker;

	public static Lock_LockManager getInstance() {
		System.out.println("Lock_LockManager");
		synchronized (Lock_LockManager.class) {
			if (instance == null) {
				instance = new Lock_LockManager();
			}
		}
		return instance;
	}

	public void enableAppLock(Application app) {
		if (curAppLocker == null) {
			curAppLocker = new Lock_AppLockImpl(app);
		}
		curAppLocker.enable();
	}

	public boolean isAppLockEnabled() {
		if (curAppLocker == null) {return false;}
		else {return true;}
	}

	public void setAppLock(Lock_AppLock appLocker) {
		if (curAppLocker != null) {curAppLocker.disable();}
		curAppLocker = appLocker;
	}

	public Lock_AppLock getAppLock() {
		return curAppLocker;
	}
}
