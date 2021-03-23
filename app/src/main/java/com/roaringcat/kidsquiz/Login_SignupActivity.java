package com.roaringcat.kidsquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Login_SignupActivity extends Lock_BaseActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String frndreccode;
    boolean isCheckSuccess = false;
    boolean isCheckSuccess2 = false;
    boolean isCheckCount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        findViewById(R.id.btn_signup_submit).setOnClickListener(onClickListener);
        findViewById(R.id.btn_show_info_collect).setOnClickListener(onClickListener);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume(){
        super.onResume();
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(result, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }).show();
        } else {
            // 성공
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_show_info_collect:
                    Intent toweb = new Intent(Intent.ACTION_VIEW);
                    toweb.setData(Uri.parse("https://roaring-cat.github.io/RoaringCatWebsite/privacy.html"));
                    startActivity(toweb);
                    break;
                case R.id.btn_signup_submit:
                    System.out.println("가입하기 버튼 누름");
                    signUp();
                    break;
            }
        }
    };

    //비밀번호 패턴 설정
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[~!@#$%^&*()_+`={}<>,.?/])" +    //at least 1 special character
                    //"(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");


    private void signUp() {

        String email = ((EditText) findViewById(R.id.emailEt)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEdt)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.passwordcheckEdt)).getText().toString();
        frndreccode = ((EditText) findViewById(R.id.recommend)).getText().toString();
        CheckBox check_info_collect = (CheckBox) findViewById(R.id.check_info_collect);
        String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (PASSWORD_PATTERN.matcher(password).matches()) { //비밀번호 패턴 일치하는지 확인
                    if (password.equals(passwordCheck)) {
                        if(nickname.length()>0){
                            if (check_info_collect.isChecked()) {
//                        ////////////////////////////////////에러!!
//                        checkreccode(); //여기서 isCheckSuccess결정되고 뒤로 쭉 쓰일거임
                                if (frndreccode.length() == 0) {
                                    realsignup(email, password);
                                } else {
                                    checkreccode(frndreccode, new OnGetDataListener() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            Log.d("checkreccode onSuccess", "제발");
                                            if (isCheckSuccess) {
                                                checkcount(frndreccode, new OnGetDataListener() {
                                                    @Override
                                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                                        Log.d("checkcount onSuccess", "제발");
                                                        if (isCheckCount) {
                                                            realsignup(email, password);
                                                        } else {
                                                            Toast.makeText(Login_SignupActivity.this, "해당코드는 사용 횟수 50회를 초과하여\n사용하실 수 없습니다.", Toast.LENGTH_LONG).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onStart() {

                                                    }

                                                    @Override
                                                    public void onFailure() {
                                                        Log.e("checkcount failure", "시발");
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(Login_SignupActivity.this, "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.e("checkreccode failure", "시발");
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(Login_SignupActivity.this, "개인정보 처리 방침에 동의해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Login_SignupActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login_SignupActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login_SignupActivity.this, "비밀번호는 숫자, 문자, 특수문자를 포함하여 8자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login_SignupActivity.this, "이메일 형식에 맞게 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login_SignupActivity.this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }
    //그냥 인텐트주는 함수
    private void mStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //추천인코드 랜덤생성(8자리 영어, 숫자 섞어서
    public StringBuffer createInvitecode(){
        StringBuffer temp;
        String strtemp;
        do{
            temp = new StringBuffer();
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
            mDatabase.child("listreccode").orderByValue().equalTo(frndreccode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        System.out.println(snapshot.toString());
                        isCheckSuccess2 = true;
                        Log.d("createInviteCode에서 사용","");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }while(isCheckSuccess2);
        strtemp = temp.toString();
        System.out.println("createInviteCode"+strtemp);
        return temp;
    }

    private void realsignup(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //로그인이 성공적으로 완료되면
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            storageUploader(); //부가적으로 입력한 회원정보 저장

                            Toast.makeText(Login_SignupActivity.this, "회원가입이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, user.getUid());
                            mStartActivity(Navigation.class);
                        } else {
                            if (task.getException() != null) {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Login_SignupActivity.this, "이미 존재하는 회원입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    //파이어베이스에 내가 입력한 정보 저장하기 (저장만!!!)
    private void storageUploader() {
        final FirebaseUser user = mAuth.getCurrentUser();
        final String name = ((EditText) findViewById(R.id.nickname)).getText().toString();
        frndreccode = ((EditText) findViewById(R.id.recommend)).getText().toString();

        //createInvitecode로 만든 초대코드 넘겨주기
        final StringBuffer myreccode = createInvitecode();
        String strmyreccode = myreccode.toString();

        int myreserve = 0; //일단 0으로 초기화
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

        //파이어베이스에 저장된 사용자 중에 frndreccode(내가 입력한 친구의 초대코드) 있는지 확인했음!!

        //닉네임만 꼭 입력해야 저장해줌
        if (name.length()>0) {
            // Login_Signup_MemberInfo 클래스로 저장할 쟤네에 대한 객체 만들어줌
            User memberInfo = new User(name,  frndreccode,  strmyreccode,  myreserve,  count,
                    korean_count_2,  math_count_2,  perception_count_2,  korean_total_count_2,  math_total_count_2,  perception_total_count_2,  overall_total_count_2,  overall_count_2,
                    korean_count_3,  math_count_3,  perception_count_3,  korean_total_count_3,  math_total_count_3,  perception_total_count_3,  overall_total_count_3,  overall_count_3,
                    korean_count_4,  math_count_4,  perception_count_4,  korean_total_count_4,  math_total_count_4,  perception_total_count_4,  overall_total_count_4,  overall_count_4,
                    korean_count_5,  math_count_5,  perception_count_5,  korean_total_count_5,  math_total_count_5,  perception_total_count_5,  overall_total_count_5,  overall_count_5,
                    korean_count_6,  math_count_6,  perception_count_6,  korean_total_count_6,  math_total_count_6,  perception_total_count_6,  overall_total_count_6,  overall_count_6
            );
            // 위에서 만들어준 객체이용해서 파이어베이스에 저장

            mDatabase.child("users").child(user.getUid()).setValue(memberInfo);
            Log.d("회원정보 저장","완료");
            mDatabase.child("listreccode").child(user.getUid()).setValue(strmyreccode);

            if(isCheckSuccess){
                //실시간 데이터베이스에서 쿼리하는걸로로 바꾸기//
                mDatabase.child("users").orderByChild("myreccode").equalTo(frndreccode).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //myreccode업데이트 해주는 코드 넣기
                        frndIncrement(snapshot.getRef());
                        myIncrement(mDatabase.child("users").child(user.getUid()));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            Toast.makeText(Login_SignupActivity.this, "회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    //파이어베이스에 저장된 사용자 정보중에 위에서 입력한 frndreccode와 일치하는 myreccode를 가진 사람이 있는지 확인 ==> 값을 return할 수 있으면 좋을텐데!!!
    public boolean checkreccode(String frndreccode, OnGetDataListener listener){
        //실시간 데이터베이스에서 쿼리하는걸로로 바꾸기//
        System.out.println("checkreccode 실행시작");
        mDatabase.child("listreccode").orderByValue().equalTo(frndreccode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    System.out.println(snapshot.toString());
                    isCheckSuccess = true;
                    Log.d("시발","시발");
                }
                listener.onSuccess(snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return isCheckSuccess;
    }

    public boolean checkcount(String frndreccode,OnGetDataListener dataListener){
        System.out.println("checkcount 실행시작");
        mDatabase.child("users").orderByChild("myreccode").equalTo(frndreccode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    System.out.println("snapshot : "+dataSnapshot.getValue()); //object타입
                    Map<String, HashMap<String,Long>> object = (Map<String, HashMap<String,Long>>) dataSnapshot.getValue();
                    for (String key : object.keySet()) {
                        Log.d("checkcount","for문 들어옴");
                        System.out.println("checkcount : "+object.get(key).get("count"));
                        if(object.get(key).get("count")<50){
                            isCheckCount = true;
                            Log.d("시발2","시발");
                        }

                    }
                    dataListener.onSuccess(dataSnapshot);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataListener.onFailure();
            }
        });
        return isCheckCount;
    }

    private void frndIncrement(DatabaseReference databaseReference){//친구 적립금이랑 카운트 올려주는거
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                if (u.count>=50){
                    Toast.makeText(Login_SignupActivity.this,"친구가 50명이 초과돼서 더 이상 초대받을 수 없어요!",Toast.LENGTH_LONG).show();
                }else{
                    u.count = u.count + 1;
                    u.myreserve = u.myreserve +10;
                    // Set value and report transaction success
                    mutableData.setValue(u);
                    Log.d("frndIncrement","친구 reserve update 완료!");
                }

                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void myIncrement(DatabaseReference databaseReference){//내 적립금 올려주는거
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                u.myreserve = u.myreserve +10;
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("myIncrement","내 reserve update 완료!");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}