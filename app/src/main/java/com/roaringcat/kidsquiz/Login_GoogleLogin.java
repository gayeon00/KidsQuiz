/*package com.roaringcat.kidsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//구글 로그인
public class Login_GoogleLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SignInButton btn_google;//구글 로그인 버튼
    private FirebaseAuth auth; //파이어베이스 인증 객체
    private GoogleApiClient googleApiClient;//구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; //구글 로그인 결과코드
    private static final String TAG = "GoogleLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_SIGN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            System.out.println(1);
            if (result.isSuccess()) { // 인증결과가 성공적이면..
                GoogleSignInAccount account = result.getSignInAccount(); // account 라는 데이터는 구글로그인 정보를 담고있습니다. (닉네임,프로필사진Url,이메일주소...등)
                resultLogin(account); // 로그인 결과 값 출력 수행하라는 메소드
            } else{
                System.out.println(2);
            }
        }
    }
    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 로그인이 성공했으면...
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.d(TAG,user.getUid());
                            Toast.makeText(Login_GoogleLogin.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            check(account);
                        } else { // 로그인이 실패했으면..
                            Toast.makeText(Login_GoogleLogin.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // 파이어베이스 '문서'에 유저의 uid가 있는지 판단하기 (있으면 네비게이션으로, 없으면 사용자 정보 입력칸으로!)

    private void check(final GoogleSignInAccount account){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docIdRef = db.collection("users").document(user.getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        Intent intent = new Intent(Login_GoogleLogin.this, Navigation.class);
                        intent.putExtra("nickName", account.getDisplayName());
                        intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // String.valueOf() 특정 자료형을 String 형태로 변환.
                        startActivity(intent);
                        Login_MainLogin login_main = (Login_MainLogin)Login_MainLogin.Login_main;
                        login_main.finish();
                        finish();
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        Toast.makeText(Login_GoogleLogin.this, "저장된 회원 정보가 없습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login_GoogleLogin.this, Login_GoogleUserInfo.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }
}*/