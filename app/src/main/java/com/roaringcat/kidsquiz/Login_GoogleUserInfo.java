/*package com.roaringcat.kidsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;


//구글 유저 정보 파이어베이스에 저장
public class Login_GoogleUserInfo extends AppCompatActivity {
    private static final String TAG = "Google User Info";
    String frndreccode;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isCheckSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googleuserinfo);

        findViewById(R.id.save_btn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.save_btn) {
                save();                                                     //입력한 닉네임, invite코드 생성
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void save() {
        final String name = ((EditText) findViewById(R.id.nickname1)).getText().toString();

        //생성된 내 invite code
        final StringBuffer myreccode = createInvitecode();

        //입력한 친구의 invite code
        String strmyreccode = myreccode.toString();                                                 //생성된 내 invite code
        frndreccode = ((EditText) findViewById(R.id.recommend1)).getText().toString();              //입력한 친구의 invite code
        int myreserve = 0;
        int count = 0;
        int korean_count_2=0;
        int math_count_2=0;
        int perception_count_2=0;
        int korean_total_count_2=0;
        int math_total_count_2=0;
        int perception_total_count_2=0;
        int overall_total_count_2=0;
        int overall_count_2=0;

        int korean_count_3=0;
        int math_count_3=0;
        int perception_count_3=0;
        int korean_total_count_3=0;
        int math_total_count_3=0;
        int perception_total_count_3=0;
        int overall_total_count_3=0;
        int overall_count_3=0;

        int korean_count_4=0;
        int math_count_4=0;
        int perception_count_4=0;
        int korean_total_count_4=0;
        int math_total_count_4=0;
        int perception_total_count_4=0;
        int overall_total_count_4=0;
        int overall_count_4=0;

        int korean_count_5=0;
        int math_count_5=0;
        int perception_count_5=0;
        int korean_total_count_5=0;
        int math_total_count_5=0;
        int perception_total_count_5=0;
        int overall_total_count_5=0;
        int overall_count_5=0;

        int korean_count_6=0;
        int math_count_6=0;
        int perception_count_6=0;
        int korean_total_count_6=0;
        int math_total_count_6=0;
        int perception_total_count_6=0;
        int overall_total_count_6=0;
        int overall_count_6=0;


        if (name.length() > 0){
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Login_Signup_MemberInfo에서 객체 생성
            User memberInfo = new User(name,  frndreccode,  strmyreccode,  myreserve,  count,
                    korean_count_2,  math_count_2,  perception_count_2,  korean_total_count_2,  math_total_count_2,  perception_total_count_2,  overall_total_count_2,  overall_count_2,
                    korean_count_3,  math_count_3,  perception_count_3,  korean_total_count_3,  math_total_count_3,  perception_total_count_3,  overall_total_count_3,  overall_count_3,
                    korean_count_4,  math_count_4,  perception_count_4,  korean_total_count_4,  math_total_count_4,  perception_total_count_4,  overall_total_count_4,  overall_count_4,
                    korean_count_5,  math_count_5,  perception_count_5,  korean_total_count_5,  math_total_count_5,  perception_total_count_5,  overall_total_count_5,  overall_count_5,
                    korean_count_6,  math_count_6,  perception_count_6,  korean_total_count_6,  math_total_count_6,  perception_total_count_6,  overall_total_count_6,  overall_count_6
            );
            checkreccode();
            if(frndreccode.length()==0|| isCheckSuccess) {
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "회원정보 등록 성공");
                                Intent intent = new Intent(Login_GoogleUserInfo.this, Navigation.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login_GoogleUserInfo.this, "회원정보 등록에 실패했습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                if(isCheckSuccess){
                    db.collection("users")
                        //사용자가 입력한 코드가 있는 사용자한테가서
                        .whereEqualTo("myreccode",frndreccode)  //사용자가 입력한 코드가 있는 사용자한테가서
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    //여기서 document는 친구의 데이터베이스
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        //친구의 "myreserve" 10업데이트 해줌
                                        document.getReference().update("myreserve",FieldValue.increment(10)); //myreserve 10업데이트 해줌
                                        document.getReference().update("count",FieldValue.increment(1)); //myreserve 10업데이트 해줌

                                        //여기서 다시 받아온 docRef는 내 데이터베이스
                                        DocumentReference docRef = db.collection("users").document(user.getUid());
                                        //내 "myreserve" 10 업데이트
                                        docRef.update("myreserve", FieldValue.increment(10)); //현재 사용자 정보 입력중인 사용자
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                }
            }
        } else {
            Toast.makeText(Login_GoogleUserInfo.this, "회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    //추천인코드 랜덤생성(8자리 영어, 숫자 섞어서
    public StringBuffer createInvitecode(){
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }
        return temp;
    }

    //파이어베이스에 저장된 사용자 정보중에 위에서 입력한 frndreccode와 일치하는 myreccode를 가진 사람이 있는지 확인 ==> 값을 return할 수 있으면 좋을텐데!!!
    private void checkreccode(){
        db.collection("users")
            .whereEqualTo("myreccode", frndreccode)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.exists()){
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                isCheckSuccess = true;
                            } else {
                                Log.d(TAG,"No such document");
                                isCheckSuccess = false;
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }
}*/