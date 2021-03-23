package com.roaringcat.kidsquiz;

import android.app.Application;

import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Lock_App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("App");
		Lock_LockManager.getInstance().enableAppLock(this);	//락 매니저 호출 및 생성, 이 앱을 앱 락으로 잠금
	}
}
