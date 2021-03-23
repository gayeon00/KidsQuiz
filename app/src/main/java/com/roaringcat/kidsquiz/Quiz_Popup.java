package com.roaringcat.kidsquiz;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.Service;
import android.graphics.PixelFormat;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Quiz_Popup extends Service implements Animation.AnimationListener {
    //팝업에 보여질 문제들 변수 설정
    public TextView tv_ques;
    public TextView tv_op1;
    public TextView tv_op2;
    public TextView tv_op3;
    public TextView tv_op4;
    public ImageView iv_prob;
    public ImageView iv_op1;
    public ImageView iv_op2;
    public ImageView iv_op3;
    public ImageView iv_op4;
    public Long START_TIME_IN_MILLIS;
    public int type;
    public String prob;
    public String ques;
    public String loc_op1;
    public String loc_op2;
    public String loc_op3;
    public String loc_op4;
    private Firebase mRef;
    NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 2;
    WindowManager wm;
    View mView;
    WindowManager.LayoutParams params;
    public DatabaseReference mDatabase;
    public DatabaseReference pointdb;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    int count;      //글라이드 완료 갯수 세기
    int popuplayout11 = R.layout.popup_it_landscape;
    int popuplayout12 = R.layout.popup_it_portrait;
    int popuplayout21 = R.layout.popup_ti_landscape;
    int popuplayout22 = R.layout.popup_ti_portrait;
    int popuplayout31 = R.layout.popup_tt_landscape;
    int popuplayout32 = R.layout.popup_tt_portrait;
    int popuplayout41 = R.layout.popup_ii_landscape;
    int popuplayout42 = R.layout.popup_ii_portrait;
    Handler handler;
    private List<String> e_k_m_p;
    private List<String> im_tx;
    private List<String> i_t;
    int i,j,k,quiznum;
    String l_or_p;
    int no_ans;
    int no_wrong1;
    int no_wrong2;
    int no_wrong3;
    //입력받은 나이 가져오기
    int valueAgePicker = ((Nav_FragmentHome)Nav_FragmentHome.context).valueAgePicker;
    Animation animFadeIn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User u;
    //나이, 과목, 형식
    Integer[][][] quiz_num_list={{{27,7,9},{6,7,14},{2,11,42}}, //35(it),25(ti),65(ii)
            {{18,29,2},{7,10,50},{2,10,60}}, //27,49,112
            {{26,19,21},{39,9,14},{3,18,33}}, //68,46,68
            {{40,19,28},{19,5,35},{6,13,27}}, //65,37,77
            {{100,3,20},{2,2,2},{22,10,49}}}; //124,15,71
    //319(imtx), 172(txim), 393(imim) ==> 884;
    @SuppressLint("ResourceType")
    @Override
    public IBinder onBind(Intent intent) { return null; }
    @SuppressLint("RtlHardcoded")

    @Override
    public void onCreate() {
        super.onCreate();
        // load the animation from an XML file
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        // attach AnimationListener (OPTIONAL)
        animFadeIn.setAnimationListener(this);
        pointdb = FirebaseDatabase.getInstance().getReference();
        Navigation.keeppopup = true;
    }

    @Override
    public void onDestroy() {
        Log.d("팝업 종료","팝업 종료 in popup");
        super.onDestroy();
        if(wm != null) {
            try {
                if(mView != null) {
                    wm.removeView(mView);
                    mView = null;
                }
            }catch(IllegalArgumentException e){}
            wm = null;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        mDatabase = pointdb.child("users").child(user.getUid());
        int valueAgePicker = ((Nav_FragmentHome)Nav_FragmentHome.context).valueAgePicker;
        Log.d("팝업 시작","팝업 시작 in popup");
        stop_resumetimer();
        setchannel();
        START_TIME_IN_MILLIS = intent.getLongExtra("time",0);
        Log.d("Quiz_popup : ", String.valueOf(START_TIME_IN_MILLIS));
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        count = 0;
        // inflater 를 사용하여 layout 을 가져오자
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 윈도우매니저 설정
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;




        // eng, kor, mat, per을 array로 만들기
        e_k_m_p = new ArrayList<>();
        //e_k_m_p.add("english");
        e_k_m_p.add("korean");
        e_k_m_p.add("math");
        e_k_m_p.add("perception");
        // 저 어레이를 쓰기 위해서 index (0~3)랜덤생성해주기
        i = getRandom_type(valueAgePicker);

        //imim, imtx, txim, txtx을 어레이로 만들기
        im_tx = new ArrayList<>();
        im_tx.add("imtx");
        im_tx.add("txim");
//        im_tx.add("txtx");
        im_tx.add("imim");

        i_t = new ArrayList<>();
        i_t.add("it");
        i_t.add("ti");
//        i_t.add("tt");
        i_t.add("ii");
        // 저 어레이를 쓰기 위해서 index (0~3)랜덤생성해주기
        j = getRandom_it(i,quiz_num_list);

        k=quiz_num_list[valueAgePicker-2][i][j];
        System.out.println("k: "+k);
        quiznum= (int) (Math.random() * k+ 1);



        //숫자 차례대로 eng의 (imtx가 두개, txim이 두개, ...) kor의 (동일), math, perception

        //3차원 배열로 만들어서 나이 --> subject --> imtx유형 갯수 일케 해야 할듯!

        // int[][] quiz_num_list={{2,2,2,2},{2,2,2,2},{2,2,2,2},{2,2,2,2}};


        type = j+1; // 불러온 im, tx에 따라서 type변수 업데이트 해줘야함. ==>onDatachange에 변수들도 값 불러오는 방식 바꿔줘야함....?


        switch (type){
            case 1 :
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    l_or_p = "l";
                    mView = inflate.inflate(popuplayout11, null);
                }
                else {
                    l_or_p = "p";
                    mView = inflate.inflate(popuplayout12, null);
                }
                break;
            case 2:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    l_or_p = "l";
                    mView = inflate.inflate(popuplayout21, null);
                }
                else {
                    l_or_p = "p";
                    mView = inflate.inflate(popuplayout22, null);
                }
                break;
            case 3:
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    l_or_p = "l";
                    mView = inflate.inflate(popuplayout41, null);
                }
                else {
                    l_or_p = "p";
                    mView = inflate.inflate(popuplayout42, null);
                }
                break;
        }

        /////////////////////////////////////////////////// 퀴즈 가져와서 뿌리기 //////////////
        Firebase.setAndroidContext(this);

        switch (type) {
            case 1:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tv_ques = (TextView) mView.findViewById(R.id._it_question_l);
                    iv_prob = (ImageView) mView.findViewById(R.id._it_prob_l);
                    tv_op1 = (TextView) mView.findViewById(R.id._it_op_l1);
                    tv_op2 = (TextView) mView.findViewById(R.id._it_op_l2);
                    tv_op3 = (TextView) mView.findViewById(R.id._it_op_l3);
                    tv_op4 = (TextView) mView.findViewById(R.id._it_op_l4);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    tv_ques = (TextView) mView.findViewById(R.id._it_question_p);
                    iv_prob = (ImageView) mView.findViewById(R.id._it_prob_p);
                    tv_op1 = (TextView) mView.findViewById(R.id._it_op_p1);
                    tv_op2 = (TextView) mView.findViewById(R.id._it_op_p2);
                    tv_op3 = (TextView) mView.findViewById(R.id._it_op_p3);
                    tv_op4 = (TextView) mView.findViewById(R.id._it_op_p4);
                }
                break;
            case 2:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tv_ques = (TextView) mView.findViewById(R.id._ti_question_l);
                    iv_op1 = (ImageView) mView.findViewById(R.id._ti_op_l1);
                    iv_op2 = (ImageView) mView.findViewById(R.id._ti_op_l2);
                    iv_op3 = (ImageView) mView.findViewById(R.id._ti_op_l3);
                    iv_op4 = (ImageView) mView.findViewById(R.id._ti_op_l4);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    tv_ques = (TextView) mView.findViewById(R.id._ti_question_p);
                    iv_op1 = (ImageView) mView.findViewById(R.id._ti_op_p1);
                    iv_op2 = (ImageView) mView.findViewById(R.id._ti_op_p2);
                    iv_op3 = (ImageView) mView.findViewById(R.id._ti_op_p3);
                    iv_op4 = (ImageView) mView.findViewById(R.id._ti_op_p4);
                }
                break;
            case 3:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tv_ques = (TextView) mView.findViewById(R.id._ii_question_l);
                    iv_prob = (ImageView) mView.findViewById(R.id._ii_prob_l1);
                    iv_op1 = (ImageView) mView.findViewById(R.id._ii_op_l1);
                    iv_op2 = (ImageView) mView.findViewById(R.id._ii_op_l2);
                    iv_op3 = (ImageView) mView.findViewById(R.id._ii_op_l3);
                    iv_op4 = (ImageView) mView.findViewById(R.id._ii_op_l4);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    tv_ques = (TextView) mView.findViewById(R.id._ii_question_p);
                    iv_prob = (ImageView) mView.findViewById(R.id._ii_prob_p1);
                    iv_op1 = (ImageView) mView.findViewById(R.id._ii_op_p1);
                    iv_op2 = (ImageView) mView.findViewById(R.id._ii_op_p2);
                    iv_op3 = (ImageView) mView.findViewById(R.id._ii_op_p3);
                    iv_op4 = (ImageView) mView.findViewById(R.id._ii_op_p4);
                }
                break;
        }
        ////////////////////////////////////////////////////////////////////////////////////
        mRef = new Firebase("https://kidsquiz-master-388e3.firebaseio.com/quiz/age"+valueAgePicker+"/"+e_k_m_p.get(i)+"/"+im_tx.get(j)+"/"+quiznum);//여기 age뒤에 숫자 agepicker로 바꾸자
        System.out.println(valueAgePicker);
        System.out.println("age"+valueAgePicker+"/"+e_k_m_p.get(i)+"/"+im_tx.get(j)+"/"+quiznum);
        /////////////////////////////////////////////////////////////////////////////////////

        //중복없이 1~3랜덤으로 생성해서 a[]에 담아줌 ex) 2,1,4,3(인덱스 역할!)
        final int arr[] = {0,0,0,0};

        for (int i=0; i<4;i++){
            arr[i] = getRandom()+1;
            for (int j=0;j<i;j++){
                if(arr[i]==arr[j]){
                    i--;
                }
            }
        }
        for(int b=0;b<4;b++){
            System.out.println("arr["+b+"] = "+arr[b]);
        }

        no_ans = arr[3]; //얘가 항상 답
        no_wrong1 = arr[1];
        no_wrong2 = arr[2];
        no_wrong3 = arr[0]; //오답

        //일단은 퀴즈 고정
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //realtime database에서 받아옴
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                //key_op1,2,3,4 랜덤으로 생성해줄 코드 (hashmap으로)
                Map<String, String> vari = new HashMap<>();

                ques = map.get("ques");
                prob = map.get("prob");

                vari.put("key_op"+arr[0],map.get("op1")); //arr[0]=2 : key_op2라는 키에 op1의 내용 들어감    3
                vari.put("key_op"+arr[1],map.get("op2")); //arr[1]=1 : key_op1이라는 키에 op2의 내용 들어감  4
                vari.put("key_op"+arr[2],map.get("op3")); //arr[2]=4 : key_op4이라는 키에 op3의 내용 들어감  1
                vari.put("key_op"+arr[3],map.get("ans")); //arr[3]=3 : key_op3라는 키에 ans의 내용 들어감    2

                loc_op1=vari.get("key_op1"); //
                loc_op2=vari.get("key_op2"); //op1의 내용이 op2위치에 들어갈거임
                loc_op3=vari.get("key_op3"); //op4의 내용이 op3위치에 들어갈거임
                loc_op4=vari.get("key_op4");

                tv_ques.setText(ques); //무조건 텍스트

                switch (type) {
                    case 1:
                        GlideApp.with(getApplicationContext()).load(prob).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide1");
//                                mView.startAnimation(animFadeIn);
                                anim(mView, 1500);
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_prob); //im
                        tv_op1.setText(loc_op1); //tx
                        tv_op2.setText(loc_op2);
                        tv_op3.setText(loc_op3);
                        tv_op4.setText(loc_op4);
                        break;
                    case 2:
                        GlideApp.with(getApplicationContext()).load(loc_op1).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide2");
                                count ++;
                                if (count == 4){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 1700);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op1); //im
                        GlideApp.with(getApplicationContext()).load(loc_op2).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide3");
                                count ++;
                                if (count == 4){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 1700);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op2);
                        GlideApp.with(getApplicationContext()).load(loc_op3).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide4");
                                count ++;
                                if (count == 4){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 1700);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op3);
                        GlideApp.with(getApplicationContext()).load(loc_op4).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide5");
                                count ++;
                                if (count == 4){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 1700);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op4);
                        break;
                    case 3:
                        GlideApp.with(getApplicationContext()).load(prob).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide6");
                                count ++;
                                if (count == 5){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 2000);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_prob); //im
                        GlideApp.with(getApplicationContext()).load(loc_op1).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide7");
                                count ++;
                                if (count == 5){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 2000);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op1); //im
                        GlideApp.with(getApplicationContext()).load(loc_op2).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide8");
                                count ++;
                                if (count == 5){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 2000);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op2);
                        GlideApp.with(getApplicationContext()).load(loc_op3).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide9");
                                count ++;
                                if (count == 5){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 2000);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op3);
                        GlideApp.with(getApplicationContext()).load(loc_op4).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                System.out.println("Glide10");
                                count ++;
                                if (count == 5){
//                                    mView.startAnimation(animFadeIn);
                                    anim(mView, 2000);
                                }
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(iv_op4);
                        break;
                }
            }

            private void anim(View mView, int i) {
                mView.setVisibility(View.VISIBLE);
                mView.setAlpha(0);
                mView.animate().alpha(1).setDuration(900);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        };

        mRef.addValueEventListener(postListener); //여기까지

        //////////////////////////////////////////////////
        mView.setVisibility(View.INVISIBLE);
        wm.addView(mView, params);
//        mView.startAnimation(animFadeIn);
//        mView.setAlpha(0);
//        mView.animate().alpha(1).setDuration(10000);


        ///////근데 여기서 위의 변수쓰는데///////////넘겨받자^^
        //_ii_op_l1 ii일때?(데이터베이스에 들어간 사진 링크가 이상한걸까? tt도 안되네
        int resID = getResources().getIdentifier("_"+i_t.get(j)+"_op_"+l_or_p + no_ans, "id", "com.roaringcat.kidsquiz");
        int wrongID1 = getResources().getIdentifier("_" + i_t.get(j) + "_op_" + l_or_p + no_wrong1, "id", "com.roaringcat.kidsquiz");
        int wrongID2 = getResources().getIdentifier("_" + i_t.get(j) + "_op_" + l_or_p + no_wrong2, "id", "com.roaringcat.kidsquiz");
        int wrongID3 = getResources().getIdentifier("_" + i_t.get(j) + "_op_" + l_or_p + no_wrong3, "id", "com.roaringcat.kidsquiz");

        //레이아웃에서 어느 위치에 뭐 클릭하면 정답이도록해논거네 ==> 받아온 특정 변수를 클릭하면 정답이도록
        //변수 loc_op + no_ans에 해당하는게 눌리면 정답
        System.out.println("_"+i_t.get(j)+"_op_"+l_or_p + no_ans);/////////////////////////////

//        DatabaseReference docRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        userTotalIncrement(mDatabase,valueAgePicker,e_k_m_p.get(i)); //문제 띄워지면 무조건 해당 과목들 출제 수 올리기
        overallIncrement(mDatabase,valueAgePicker); //문제 띄워지면 무조건 overall


        if(type==2||type==3){
            ImageView selectans = mView.findViewById(resID); //==> 4를 j+1로 갖고오면됨.
            ImageView selectwrong1 = mView.findViewById(wrongID1);
            ImageView selectwrong2 = mView.findViewById(wrongID2);
            ImageView selectwrong3 = mView.findViewById(wrongID3);
            selectans.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            myOnClick(selectwrong1);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong3);
                            try {
                                if(mView != null) {
                                    wm.removeView(mView);
                                    mView = null;
                                }
                            }catch(IllegalArgumentException e){}

                            try {
                                quiz_correct();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            selectwrong1.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            myOnClick(selectans);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong3);
                            myViewHandler(wm,selectans,null,mView);
                        }
                    }
            );
            selectwrong2.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
//                            wm.removeView(mView);
                            myOnClick(selectans);
                            myOnClick(selectwrong1);
                            myOnClick(selectwrong3);
                            myViewHandler(wm,selectans,null,mView);
                        }
                    }
            );
            selectwrong3.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
//                            wm.removeView(mView);
                            myOnClick(selectans);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong1);
                            myViewHandler(wm,selectans,null,mView);
                        }
                    }
            );
        }
        else { //Text보기를 골라야할 경우 TextView로 받아옴
            TextView selectans = mView.findViewById(resID); //==> 4를 j+1로 갖고오면됨.
            TextView selectwrong1 = mView.findViewById(wrongID1);
            TextView selectwrong2 = mView.findViewById(wrongID2);
            TextView selectwrong3 = mView.findViewById(wrongID3);
            selectans.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            myOnClick(selectwrong1);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong3);
                            try {
                                if(mView != null) {
                                    wm.removeView(mView);
                                    mView = null;
                                }
                            }catch(IllegalArgumentException e){}
                            try {
                                quiz_correct();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
            selectwrong1.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
//                            wm.removeView(mView);
                            myOnClick(selectans);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong3);
                            myViewHandler(wm,null,selectans,mView);
                        }
                    }
            );
            selectwrong2.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
//                            wm.removeView(mView);
                            myOnClick(selectans);
                            myOnClick(selectwrong1);
                            myOnClick(selectwrong3);
                            myViewHandler(wm,null,selectans,mView);
                        }
                    }
            );
            selectwrong3.setOnClickListener(
                    new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
//                            wm.removeView(mView);
                            myOnClick(selectans);
                            myOnClick(selectwrong2);
                            myOnClick(selectwrong1);
                            myViewHandler(wm,null,selectans,mView);
                        }
                    }
            );
        }

        return START_NOT_STICKY;
    }
    public void stop_resumetimer(){
        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                Quiz_Timer.class); // 이동할 컴포넌트
        stopService(intent); // 서비스 종료
    }

    public int getRandom(){ //array index로 쓸 0~3생성해주기 영어 포함
        //quiznum  난수 생성
        int randomquizNum = (int) (Math.random() * 4); //array index로 쓸 0~3생성해주기
        return randomquizNum;
    }

    public int getRandom_type(int age){ //array index로 쓸 0~3생성해주기 영어 포함
        Integer[][] quiz_type = {{sum(0,0),sum(0,1),sum(0,2)},
                {sum(1,0),sum(1,1),sum(1,2)},
                {sum(2,0),sum(2,1),sum(2,2)},
                {sum(3,0),sum(3,1),sum(3,2)},
                {sum(4,0),sum(4,1),sum(4,2)}};
        //(43,27,55),
        //(49,67,72),
        //(66,62,54),
        //(87, 59, 33),
        //(123, 6, 81)
        //==> ()
        //quiznum  난수 생성
        double r  = Math.random(); //{0.0 - 1.0}(double)
        double dr = r * 100; // {0.0 - 100.0}
        System.out.println("dr: "+dr);
        int ind = 0;
        int sum=0;
        //it(0), ti(1), ii(2)
        //319, 172, 393 ==> 884
        Integer[] cnt = quiz_type[valueAgePicker - 2].clone(); //cnt : {49,67,72}
        for (int i : cnt){
            System.out.println("i : "+i);
            sum+=i;
        }
        Arrays.sort(cnt);
        ArrayList array_cnt_sorted = new ArrayList<>(Arrays.asList(cnt));
//        System.out.println("cnt: "+cnt);
//        System.out.println("parsefloat : "+Float.parseFloat(cnt.get(0).toString())/254);
        double p[] = { (float)cnt[0]*100/sum, (float)cnt[1]*100/sum, (float)cnt[2]*100/sum }; //4, 3, 2, 1, 0
        System.out.println("p[] : "+ p[0]);
        double cumulative = 0;
        Integer[] cnt2 = quiz_type[valueAgePicker - 2];
        ArrayList array_cnt = new ArrayList<>(Arrays.asList(cnt2));
        for(int i: cnt2)
        {
            System.out.println("cnt.indexOf(i) : "+p[array_cnt_sorted.indexOf(i)]);
            cumulative += p[array_cnt_sorted.indexOf(i)];
            if(dr <= cumulative)
            {
                ind = array_cnt.indexOf(i);
                break;
            }
        }
        System.out.println("getRandom_type--ind : "+ind);
        return ind;
    }
    public int getRandom_it(int i2, Integer[][][] quiznumlist){ //array index로 쓸 0~3생성해주기 (영어포함 x, txtx X)
        System.out.println("--------------------------------------------");
        //quiznum  난수 생성
        double r  = Math.random(); //{0.0 - 1.0}(double)
        double dr = r * 100; // {0.0 - 100.0}
        System.out.println("dr: "+dr);
        int ind = 0;
        int sum=0;
        Integer[] cnt = quiznumlist[valueAgePicker-2][i2].clone(); //ex){27,7,9}
        Integer[] cnt2 = quiznumlist[valueAgePicker-2][i2]; //정렬되지 않은, it ti ii 순서임(78,34,55)
        for (int i : cnt){
            System.out.println("i : "+i);
            sum+=i;
        }
        Arrays.sort(cnt); // {7,9,27} 여기서 cnt가 정렬됨
        ArrayList array_cnt_sorted = new ArrayList<>(Arrays.asList(cnt)); //정렬된 cnt로 ArrayList만듦 --> array_cnt_sorted는 정렬된 상태
        System.out.println("array_cnt_sorted : "+array_cnt_sorted);
        double p[] = { (float)cnt[0]*100/sum, (float)cnt[1]*100/sum, (float)cnt[2]*100/sum }; //여기서 cnt는 정렬된 상태 ==> 정렬된 p(34,55,78)
        double cumulative = 0;
        ArrayList array_cnt = new ArrayList<>(Arrays.asList(cnt2)); //정렬되지 않은거를 어레이로 만들기
        System.out.println("array_cnt : "+array_cnt);
        for(int i: cnt2) //cnt2는 정렬되지 않은 배열(원래상태) i=78
        {
            System.out.println("array_cnt_sorted.indexOf(i) : "+p[array_cnt_sorted.indexOf(i)]); //array_cnt_sorted에서 78의 인덱스는(크기순은) 2이고 p에서 인덱스를 2로 갖고있는 놈은 전체중에 78의 퍼센트
            cumulative += p[array_cnt_sorted.indexOf(i)];
            if(dr <= cumulative)
            {
                ind = array_cnt.indexOf(i);
                break;
            }
        }
        System.out.println("getRandom_it--ind : "+ind);
        return ind;
    }

    public void quiz_correct() throws InterruptedException {
        if(((Navigation)Navigation.context).mSubKidsquiz){
            myIncrement(pointdb.child("users").child(user.getUid()), 1);
        }
        Intent quiz_correct = new Intent(this, Quiz_Correct.class);
        quiz_correct.putExtra("time", START_TIME_IN_MILLIS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(quiz_correct);                      //백그라운드에서 실행하기위해 필요
        }
        else {
            startService(quiz_correct);
        }

        //퀴즈 과목에 맞게 correct +1해주기
        final FirebaseUser user = mAuth.getInstance().getCurrentUser();
        Log.d("user",user.getUid());
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        DatabaseReference totaluserRef = pointdb.child("total_user_correct_rate").child(e_k_m_p.get(i)+valueAgePicker).child(user.getUid());
        System.out.println("child(total_user_correct_rate).child("+e_k_m_p.get(i)+valueAgePicker+").child("+user.getUid()+")");


        userCorrectIncrement(mDatabase,valueAgePicker, e_k_m_p.get(i)); //맞은과목 +1해주기
        overallCorrectIncrement(mDatabase,valueAgePicker); //맞아쓰염 전체 맞은 갯수 +1해주기
        totalUserCorrectRateUpdate(mDatabase, totaluserRef ,valueAgePicker,e_k_m_p.get(i),user); //다른 테이블에서 정답률 업데이트 해주기


    }


    public void quiz_wrong() {
        Intent quiz_wrong = new Intent(this, Quiz_Wrong.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(quiz_wrong);                      //백그라운드에서 실행하기위해 필요
        } else{
            startService(quiz_wrong);
        }

        final FirebaseUser user = mAuth.getInstance().getCurrentUser();
        DatabaseReference totaluserRef = pointdb.child("total_user_correct_rate").child(e_k_m_p.get(i)+valueAgePicker).child(user.getUid());
        totalUserCorrectRateUpdate(mDatabase, totaluserRef ,valueAgePicker,e_k_m_p.get(i),user); //다른 테이블에서 정답률 업데이트 해주기

        String key = pointdb.child("OXdataset").child(user.getUid()).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key,e_k_m_p.get(i)+"_"+i_t.get(j)+"_"+quiznum);

        pointdb.child("OXdataset").child(user.getUid()).updateChildren(childUpdates);

        //잘 됏을까!!!

    }

    private void userCorrectIncrement(DatabaseReference databaseReference,int age, String subject){
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                switch(age){
                    case(2):
                        switch (subject){
                            case("korean"):
                                u.korean_count_2=u.korean_count_2+1;
                                break;
                            case("math"):
                                u.math_count_2=u.math_count_2+1;
                                break;
                            case("perception"):
                                u.perception_count_2=u.perception_count_2+1;
                                break;
                        }
                        break;
                    case(3):
                        switch (subject){
                            case("korean"):
                                u.korean_count_3=u.korean_count_3+1;
                                break;
                            case("math"):
                                u.math_count_3=u.math_count_3+1;
                                break;
                            case("perception"):
                                u.perception_count_3=u.perception_count_3+1;
                                break;
                        }
                        break;
                    case(4):
                        switch (subject){
                            case("korean"):
                                u.korean_count_4=u.korean_count_4+1;
                                break;
                            case("math"):
                                u.math_count_4=u.math_count_4+1;
                                break;
                            case("perception"):
                                u.perception_count_4=u.perception_count_4+1;
                                break;
                        }
                        break;
                    case(5):
                        switch (subject){
                            case("korean"):
                                u.korean_count_5=u.korean_count_5+1;
                                break;
                            case("math"):
                                u.math_count_5=u.math_count_5+1;
                                break;
                            case("perception"):
                                u.perception_count_5=u.perception_count_5+1;
                                break;
                        }
                        break;
                    case(6):
                        switch (subject){
                            case("korean"):
                                u.korean_count_6=u.korean_count_6+1;
                                break;
                            case("math"):
                                u.math_count_6=u.math_count_6+1;
                                break;
                            case("perception"):
                                u.perception_count_6=u.perception_count_6+1;
                                break;
                        }
                        break;

                }
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("myIncrement1","내 reserve update 완료!");
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("Quiz_Popup", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void userTotalIncrement(DatabaseReference docRef, int age, String s) {
        docRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                switch(age){
                    case(2):
                        switch (s){
                            case("korean"):
                                u.korean_total_count_2=u.korean_total_count_2+1;
                                break;
                            case("math"):
                                u.math_total_count_2=u.math_total_count_2+1;
                                break;
                            case("perception"):
                                u.perception_total_count_2=u.perception_total_count_2+1;
                                break;
                        }
                        break;
                    case(3):
                        switch (s){
                            case("korean"):
                                u.korean_total_count_3=u.korean_total_count_3+1;
                                break;
                            case("math"):
                                u.math_total_count_3=u.math_total_count_3+1;
                                break;
                            case("perception"):
                                u.perception_total_count_3=u.perception_total_count_3+1;
                                break;
                        }
                        break;
                    case(4):
                        switch (s){
                            case("korean"):
                                u.korean_total_count_4=u.korean_total_count_4+1;
                                break;
                            case("math"):
                                u.math_total_count_4=u.math_total_count_4+1;
                                break;
                            case("perception"):
                                u.perception_total_count_4=u.perception_total_count_4+1;
                                break;
                        }
                        break;
                    case(5):
                        switch (s){
                            case("korean"):
                                u.korean_total_count_5=u.korean_total_count_5+1;
                                break;
                            case("math"):
                                u.math_total_count_5=u.math_total_count_5+1;
                                break;
                            case("perception"):
                                u.perception_total_count_5=u.perception_total_count_5+1;
                                break;
                        }
                        break;
                    case(6):
                        switch (s){
                            case("korean"):
                                u.korean_total_count_6=u.korean_total_count_6+1;
                                break;
                            case("math"):
                                u.math_total_count_6=u.math_total_count_6+1;
                                break;
                            case("perception"):
                                u.perception_total_count_6=u.perception_total_count_6+1;
                                break;
                        }
                        break;

                }
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("myIncrement2","내 reserve update 완료!");
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("Quiz_Popup", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    private void overallIncrement(DatabaseReference databaseReference, int age){
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                switch (age){
                    case(2):
                        u.overall_total_count_2=u.overall_total_count_2+1;
                        break;
                    case(3):
                        u.overall_total_count_3=u.overall_total_count_3+1;
                        break;
                    case(4):
                        u.overall_total_count_4=u.overall_total_count_4+1;
                        break;
                    case(5):
                        u.overall_total_count_5=u.overall_total_count_5+1;
                        break;
                    case(6):
                        u.overall_total_count_6=u.overall_total_count_6+1;
                        break;
                }
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("overallIncrement1","내 저언체 문제 갯수 업데이트 완료!");
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("Quiz_Popup", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void overallCorrectIncrement(DatabaseReference databaseReference,int age){
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                switch (age){
                    case(2):
                        u.overall_count_2=u.overall_count_2+1;
                        break;
                    case(3):
                        u.overall_count_3=u.overall_count_3+1;
                        break;
                    case(4):
                        u.overall_count_4=u.overall_count_4+1;
                        break;
                    case(5):
                        u.overall_count_5=u.overall_count_5+1;
                        break;
                    case(6):
                        u.overall_count_6=u.overall_count_6+1;
                        break;
                }
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("overallIncrement2","내 저언체 문제 갯수 업데이트 완료!");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("Quiz_Popup", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void totalUserCorrectRateUpdate(DatabaseReference myref,DatabaseReference totaluserRef, int age, String subject, FirebaseUser user) {
        Log.d("totalusercorrectrateupdate","시작");
        myref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                switch(age){
                    case(2):
                        switch (subject){
                            case("korean"):
                                totaluserRef.setValue(((float)(u.korean_count_2)/u.korean_total_count_2)*100);
                                break;
                            case("math"):
                                totaluserRef.setValue(((float)(u.math_count_2)/u.math_total_count_2)*100);
                                break;
                            case("perception"):
                                totaluserRef.setValue(((float)(u.perception_count_2)/u.perception_total_count_2)*100);
                                break;
                        }
                        pointdb.child("total_user_correct_rate").child("overall2").child(user.getUid()).setValue(((float)(u.overall_count_2)/u.overall_total_count_2)*100);
                        break;
                    case(3):
                        switch (subject){
                            case("korean"):
                                totaluserRef.setValue(((float)(u.korean_count_3)/u.korean_total_count_3)*100);
                                break;
                            case("math"):
                                totaluserRef.setValue(((float)(u.math_count_3)/u.math_total_count_3)*100);
                                break;
                            case("perception"):
                                totaluserRef.setValue(((float)(u.perception_count_3)/u.perception_total_count_3)*100);
                                break;
                        }
                        pointdb.child("total_user_correct_rate").child("overall3").child(user.getUid()).setValue(((float)(u.overall_count_3)/u.overall_total_count_3)*100);
                        break;
                    case(4):
                        switch (subject){
                            case("korean"):
                                totaluserRef.setValue(((float)(u.korean_count_4)/u.korean_total_count_4)*100);
                                break;
                            case("math"):
                                totaluserRef.setValue(((float)(u.math_count_4)/u.math_total_count_4)*100);
                                break;
                            case("perception"):
                                totaluserRef.setValue(((float)(u.perception_count_4)/u.perception_total_count_4)*100);
                                break;
                        }
                        pointdb.child("total_user_correct_rate").child("overall4").child(user.getUid()).setValue(((float)(u.overall_count_4)/u.overall_total_count_4)*100);
                        break;
                    case(5):
                        switch (subject){
                            case("korean"):
                                totaluserRef.setValue(((float)(u.korean_count_5)/u.korean_total_count_5)*100);
                                break;
                            case("math"):
                                totaluserRef.setValue(((float)(u.math_count_5)/u.math_total_count_5)*100);
                                break;
                            case("perception"):
                                totaluserRef.setValue(((float)(u.perception_count_5)/u.perception_total_count_5)*100);
                                break;
                        }
                        pointdb.child("total_user_correct_rate").child("overall5").child(user.getUid()).setValue(((float)(u.overall_count_5)/u.overall_total_count_5)*100);
                        break;
                    case(6):
                        switch (subject){
                            case("korean"):
                                totaluserRef.setValue(((float)(u.korean_count_6)/u.korean_total_count_6)*100);
                                break;
                            case("math"):
                                totaluserRef.setValue(((float)(u.math_count_6)/u.math_total_count_6)*100);
                                break;
                            case("perception"):
                                totaluserRef.setValue(((float)(u.perception_count_6)/u.perception_total_count_6)*100);
                                break;
                        }
                        pointdb.child("total_user_correct_rate").child("overall6").child(user.getUid()).setValue(((float)(u.overall_count_6)/u.overall_total_count_6)*100);
                        break;
                }
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("overallIncrement","내 저언체 문제 갯수 업데이트 완료!");
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("Quiz_Popup", "postTransaction:onComplete:" + databaseError);
            }
        });
        Log.d("totalusercorrectrateupdate","끝");
    }

    private void setchannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(null);
            builder.setContentText(null);
            Intent notificationIntent = new Intent(this, Navigation.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            builder.setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_NONE));
            }

            Notification notification = builder.build();
            startForeground(9, notification);
            stopForeground(true);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    void startresumetimer() {
        Intent resumeIntent = new Intent(getApplicationContext(), Quiz_Timer.class);
        resumeIntent.putExtra("time",START_TIME_IN_MILLIS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(resumeIntent);                      //백그라운드에서 실행하기위해 필요
        }
        else {
            startService(resumeIntent);
        }
    }
    public int sum(int i, int j) {
        int sum = 0;
        for (int k = 0; k < 3; k++) {
            sum = quiz_num_list[i][j][k];
        }
        return sum;
    }
    public void myViewHandler(WindowManager w, ImageView iv,TextView tv, View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                quiz_wrong();
            }
        },200);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //여기다가 테두리 바꾸는 코드를 줘야 하는데... 이걸 퀴즈팝업으로 가서 작동시킬것이냐 아님 퀴즈팝업에서 띄워논 뷰를 인텐트로 받아와서 그걸로 처리할거냐
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("2. 테두리 바꾸기 ");
                        if(iv==null){
                            tv.setBackground(getResources().getDrawable(R.drawable.round_dotborder_bold2));
                        }else{
                            iv.setBackground(getResources().getDrawable(R.drawable.round_dotborder_bold2));
                        }
                    }
                },500);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.setAlpha(1);
                        view.animate().alpha(0).setDuration(650);
                    }
                }, 3000);// 딜레이를 준 후 시작
                //여기다 quiz_popup레이아웃 없애주는 코드 적어야하는데
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("3. 레이아웃 없애주기");
//                        w.removeView(view);
                        try {
                            if (view != null) {
                                w.removeView(view);
                                mView = null;
                            }
                        }catch(IllegalArgumentException e){}
                    }
                },5000);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("4. 다시 실행");
                        startresumetimer();
                    }
                },5500);
            }
        }, 1200);// 딜레이를 준 후 시작
    }

    public void myOnClick(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }
    private void myIncrement(DatabaseReference databaseReference, int reward){//내 적립금 올려주는거
        databaseReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }
                u.myreserve = u.myreserve +reward;
                // Set value and report transaction success
                mutableData.setValue(u);
                Log.d("myIncrement","내 reserve update 완료!");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, com.google.firebase.database.DataSnapshot currentData) {
                // Transaction completed
                Log.d("정답 맞추고 포인트 쌓기", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}