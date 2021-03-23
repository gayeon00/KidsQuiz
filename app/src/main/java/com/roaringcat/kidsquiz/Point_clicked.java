package com.roaringcat.kidsquiz;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Point_clicked extends Lock_BaseActivity {
    private int imageInteger;
    private int remain_nowInteger;
    private int remain_deleteInteger;
    EditText etMessage;
    Intent intent;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    //image는 profile, name은 info, price는 phone
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.point_clicked);
        super.onCreate(savedInstanceState);
        intent = getIntent();
        ImageView itemimage = (ImageView) findViewById(R.id.clicked_itemimg);
        TextView itemname=(TextView) findViewById(R.id.clicked_itemname);
        TextView itemprice=(TextView) findViewById(R.id.clicked_itemprice);
        TextView point_delete = (TextView) findViewById(R.id.point_delete);
        etMessage = (EditText) findViewById(R.id.point_phoneText);
        imageInteger=Integer.parseInt(intent.getStringExtra("profile"));
        itemimage.setImageResource(imageInteger);
        itemname.setText(intent.getStringExtra("info"));
        itemprice.setText(intent.getStringExtra("phone"));
        point_delete.setText(intent.getStringExtra("phone"));
        remain_deleteInteger=Integer.parseInt(intent.getStringExtra("phone"));
        int a = Integer.parseInt(intent.getStringExtra("phone"));
        TextView point_now = (TextView) findViewById(R.id.point_now);
        TextView point_remain = (TextView) findViewById(R.id.point_remain);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.d("Mypage", "사용자 정보 : " + dataSnapshot.getValue());
                    User user2 = dataSnapshot.getValue(User.class);
                    System.out.println(user2);
                    Log.d("Mypage2", "사용자 정보2 : " + dataSnapshot.getValue());
                    point_now.setText(Integer.toString(user2.myreserve));
                    remain_nowInteger=user2.myreserve;
                    point_remain.setText(String.valueOf(remain_nowInteger-remain_deleteInteger));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Button btnQuit = findViewById(R.id.point_sendButton);
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remain_nowInteger > remain_deleteInteger) {
                    SucessDialog();
                } else {
                    FailedDialog();
                }
            }
        });
    }

    void SucessDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Point_clicked.this)
                .setTitle("알림")
                .setMessage(intent.getStringExtra("info")+"을\n 구매하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sendEmail();
                        reduceMyPoint(mUserReference);
                        saveMyPurchaseInfo();
                        showfinishdialog();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Point_clicked.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    private void showfinishdialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Point_clicked.this)
                .setMessage("결제가 완료되었습니다.\n마이페이지의 구매내역을 확인해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    void FailedDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(Point_clicked.this)
                .setTitle("알림")
                .setMessage("포인트가 부족합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Point_clicked.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }
    private void reduceMyPoint(DatabaseReference databaseReference) {
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                u.myreserve = u.myreserve - remain_deleteInteger;
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("reduceMyPoint","구매완료 후 포인트 차감!");
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,DataSnapshot currentData) {
                // Transaction completed
                Log.d("구매완료", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    private void saveMyPurchaseInfo() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = simpleDate.format(mDate);
        String phonenumber = ((EditText) findViewById(R.id.point_phoneText)).getText().toString();
        String itemname = ((TextView) findViewById(R.id.clicked_itemname)).getText().toString();
        String itemprice = ((TextView) findViewById(R.id.clicked_itemprice)).getText().toString();
        PurchaseRequestUser purchaseRequestUser = new PurchaseRequestUser(user.getEmail(),phonenumber,itemname,itemprice, getTime);
        // 사용자 이메일, 전화번호, 품목이름, 품목가격
        FirebaseDatabase.getInstance().getReference().child("PurchaseRequest").child(user.getUid()).push().setValue(purchaseRequestUser);
    }
}