package com.roaringcat.kidsquiz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_MainLogin extends Lock_BaseActivity {
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1; //다른앱 위에 그리기 요청 코드
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    public static Activity Login_main;
    boolean mSubKidsquiz = false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Login_main = Login_MainLogin.this;
        setContentView(R.layout.login_main);
        Intent intent = getIntent();
        mSubKidsquiz = intent.getBooleanExtra("mSubKidsquiz", false);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isIgnoringBatteryOptimizations(getPackageName()) || (!Settings.canDrawOverlays(this)) ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("권한 설정 알림");
            builder.setMessage("서비스를 사용하기 위해 반드시 다음 권한의 획득이 필요합니다.\n\n" +
                    "1. 다른앱 위에 그리기 권한\n" +
                    "    다른 앱 사용 중 화면에 퀴즈를 출제하기 위해 필요합니다.\n" +
                    "    키즈퀴즈 앱을 찾아 허용해주세요.\n\n" +
                    "2. 백그라운드 실행 권한\n" +
                    "    퀴즈 출제 대기 시간동안 앱이 종료되지 않기 위해 필요합니다."
            );
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                                final Intent intent;
                                intent = getSelfIntent();
                                startActivity(intent);
                            }
                            if (!Settings.canDrawOverlays(getApplicationContext())) {              // 다른앱 위에 그리기 체크
                                checkPermission();
                            }
                        }
                    });
            builder.show();
        }
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        //findViewById(R.id.btn_googlelogin).setOnClickListener(onClickListener);
        findViewById(R.id.btn_signup).setOnClickListener(onClickListener);
        findViewById(R.id.btn_reset_password).setOnClickListener(onClickListener);
    }
    private Intent getSelfIntent() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        return intent;
    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 30){
            Toast.makeText(this, "KidsQuiz를 찾아 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 다른앱 위에 그리기 체크
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                //다른앱위에그리기 실행, 요청코드 설정하기위해 startActivityForResult 함수로 실행
                //Intent로 받아온 앱 정보는 onActivityResult 함수에서 처리
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)  && (resultCode == RESULT_OK)){
            if (!Settings.canDrawOverlays(this)) {
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        delnav = intent.getBooleanExtra("keeppopup", true);
//        nav.finish();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 로컬 로그인
                case R.id.login_btn:
                    login();
                    break;
                // 구글 로그인
                // 비밀번호 재설정
                case R.id.btn_reset_password:
                    mStartActivity(Reset_password.class);
                    break;
                // 회원가입
                case R.id.btn_signup:
                    mStartActivity(Login_SignupActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    private void login() {
        String email = ((EditText)findViewById(R.id.emailEt)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEdt)).getText().toString();

        if (email.length() > 0 && password.length() > 0){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Login_MainLogin.this, "로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplication(), Navigation.class);
                                intent.putExtra("mSubKidsquiz", mSubKidsquiz);
                                startActivity(intent);
                                finish();
                            } else {
                                if (task.getException() != null) {
                                    Toast.makeText(Login_MainLogin.this, "로그인에 실패하였습니다. 아이디 또는 비밀번호를 다시 확인해 주세요.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(Login_MainLogin.this, "이메일 또는 비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    private void mStartActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
