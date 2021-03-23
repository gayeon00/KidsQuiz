package com.roaringcat.kidsquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Mypage_DeleteUser extends Lock_BaseActivity {
    private static final String TAG = "Delete User";
    private DatabaseReference mUserReference;
    private DatabaseReference mfrndreccodeReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_deleteuser);
        deleteUser();
    }
    void deleteUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Button confirm_delete = (Button) findViewById(R.id.confirm_delete) ;
        confirm_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = ((EditText) findViewById(R.id.passcheck)).getText().toString();

                if(password.length()==0){
                    Toast.makeText(Mypage_DeleteUser.this,"비밀번호 확인을 완료해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), password);
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User re-authenticated.");
                                mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                                mfrndreccodeReference = FirebaseDatabase.getInstance().getReference().child("listreccode").child(user.getUid());
                                AlertDialog.Builder builder = new AlertDialog.Builder(Mypage_DeleteUser.this);
                                builder.setTitle("회원탈퇴");
                                builder.setMessage("정말 탈퇴하시겠습니까?");
                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mUserReference.removeValue();
                                        mfrndreccodeReference.removeValue();
                                        Log.d("deleteuser","사용자정보 삭제 완료");

                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");
                                                    // [END delete_user]
                                                    Log.d("deleteuser","ㄹㅇ 사용자 삭제 완료");
                                                    Toast.makeText(Mypage_DeleteUser.this,"탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                                    // 로컬 잠금 비밀번호 제거
                                                    Lock_LockManager.getInstance().getAppLock().setPasscode(null);
                                                    myStartActivity(Login_MainLogin.class);
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("아니오",null );
                                builder.setNeutralButton("취소",null );
                                builder.create().show();
                            } else {
                                Toast.makeText(Mypage_DeleteUser.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}