package com.roaringcat.kidsquiz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Mypage_Logout extends Lock_BaseActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 파이어베이스에서 계정 제거
        FirebaseAuth.getInstance().signOut();

        Navigation.keeppopup = false;
        // 퀴즈 멈추기
        Log.d("타이머 종료","타이머 종료 in Nav");
        Intent resumeintent = new Intent(this, Quiz_Timer.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(resumeintent);
        }
        else {
            this.startService(resumeintent);
        }


        // 로컬 잠금 비밀번호 제거
        Lock_LockManager.getInstance().getAppLock().setPasscode(null);
        // 로그아웃 토스트
        Toast.makeText(Mypage_Logout.this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
        // 로그인 화면으로 이동

        myStartActivity(Login_MainLogin.class);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}