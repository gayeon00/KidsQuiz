package com.roaringcat.kidsquiz;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NativeActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;

import com.google.android.gms.ads.InterstitialAd;

public class Nav_FragmentHome extends Fragment {
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1; //다른앱 위에 그리기 요청 코드
    public long START_TIME_IN_MILLIS;            //사용자가 입력한 만큼의 시간
    public Button mButtonStarttimer;
    public Button mStoptimer;
    public static Nav_FragmentHome context;
    private NumberPicker AgePicker;
    private NumberPicker MinPicker;
    private NumberPicker SecPicker;
    public int valueAgePicker;
    private int valueMinPicker;
    private int valueSecPicker;
    boolean mSubKidsquiz = false;
    private AdView mAdView;
    public TemplateView template;
    public static Intent resumeintent;
    private InterstitialAd mInterstitialAd;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        context = this;
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
        template = (TemplateView) rootView.findViewById(R.id.ad_home);
        if(((Navigation)Navigation.context).mSubKidsquiz){
            template.setVisibility(template.GONE);
        }
        MobileAds.initialize(getContext(), "ca-app-pub-9350891168282170~5512537782");
        //Test 광고 id : ca-app-pub-3940256099942544/2247696110
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-9350891168282170/4038307032");
        // 내 네이티브 광고 id : ca-app-pub-9350891168282170/4038307032
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                template.setNativeAd(unifiedNativeAd);

            }
        });
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());




        Log.d("구독 여부 출력 : ", String.valueOf(((Navigation)Navigation.context).mSubKidsquiz));
        try {
            if (Navigation.keeppopup) {
                mButtonStarttimer.setEnabled(false); // 시작 버튼 비활성화
                mStoptimer.setEnabled(true); // 종료 버튼 활성화
            }
            else {
                mButtonStarttimer.setEnabled(true); // 시작 버튼 활성화
                mStoptimer.setEnabled(false); // 종료 버튼 비활성화
            }
        }
        catch (NullPointerException e){
            mButtonStarttimer.setEnabled(true); // 시작 버튼 활성화
            mStoptimer.setEnabled(false); // 종료 버튼 비활성화
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonStarttimer = view.findViewById(R.id.btn_starttimer);
        // 퀴즈 종료 버튼
        mStoptimer = view.findViewById(R.id.btn_stoptimer);
        // 홈으로 바로 이동 체크버튼
        if(!mSubKidsquiz) {
            mInterstitialAd = new InterstitialAd(getContext());
            mInterstitialAd.setAdUnitId("ca-app-pub-9350891168282170/6276764974");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                    // Load the next interstitial.
                    mButtonStarttimer.setEnabled(true); // 시작 버튼 활성화
                    mStoptimer.setEnabled(false); // 종료 버튼 비활성화
                    Navigation.keeppopup = false;
                    StopTimer(); // 타이머 종료
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.
                    ReviewManager manager = ReviewManagerFactory.create(getContext());
                    Task<ReviewInfo> request = manager.requestReviewFlow();
                    request.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // We can get the ReviewInfo object
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                            flow.addOnCompleteListener(task2 -> {
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                            });
                        } else {

                        }
                    });


//                    Activity min = getActivity();
//                    Toast.makeText(min, "퀴즈가 종료됩니다.", Toast.LENGTH_SHORT).show();
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
        final CheckBox to_home = (CheckBox)view.findViewById(R.id.toHome);
        Log.e("keeppopup", String.valueOf(Navigation.keeppopup));

        TextView btn8 = (TextView) view.findViewById(R.id.whatproblem);
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), problem_type.class);
                startActivity(intent);
            }
        });

        // 퀴즈 시작 눌렀을 때
        mButtonStarttimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = NetworkStatus.getConnectivityStatus(getContext());
                if (status == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("인터넷 연결 오류");
                    builder.setMessage("인터넷 연결이 필요합니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) { }
                            });
                    builder.show();
                    Navigation.keeppopup =false;
                }
                else {
                    PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                    if (!powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName()) || (!Settings.canDrawOverlays(getContext()))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("권한 설정 알림");
                        builder.setMessage("서비스를 사용하기 위해 반드시 다음 권한의 획득이 필요합니다.\n\n" +
                                "1. 다른앱 위에 그리기 권한\n" +
                                "    다른 앱 사용 중 화면에 퀴즈를\n"+
                                "    출제하기 위해 필요합니다.\n" +
                                "    키즈퀴즈 앱을 찾아 허용해주세요.\n\n" +
                                "2. 백그라운드 실행 권한\n" +
                                "    퀴즈 출제 대기 시간동안 앱이\n"+
                                "    종료되지 않기 위해 필요합니다."
                        );
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!powerManager.isIgnoringBatteryOptimizations(getActivity().getPackageName())) {
                                            final Intent intent;
                                            intent = getSelfIntent();
                                            startActivity(intent);
                                        }
                                        if (!Settings.canDrawOverlays(getContext())) {              // 다른앱 위에 그리기 체크
                                            checkPermission();
                                        }
                                    }
                                });
                        builder.show();
                    } else {
                        Activity root = getActivity();
                        Navigation.keeppopup = true;
                        //타이머가 돌아야할 시간(START_TIME_IN_MILLIS) 설정
                        START_TIME_IN_MILLIS = Long.valueOf(valueMinPicker) * 300000;
                        START_TIME_IN_MILLIS = START_TIME_IN_MILLIS + Long.valueOf(valueSecPicker) * 10000;
                        if (START_TIME_IN_MILLIS == 0) {
//                             Toast.makeText(root, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            START_TIME_IN_MILLIS = 1000;
                            Toast.makeText(root, "퀴즈가 시작됩니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(root, "퀴즈가 시작됩니다.", Toast.LENGTH_SHORT).show();
                        }
                        mButtonStarttimer.setEnabled(false); // 시작 버튼 비활성화
                        mStoptimer.setEnabled(true); // 종료 버튼 활성화
                        startresumetimer(); //타이머 시작
                        // 홈으로 바로 이동 체크되어 있으면 홈으로 이동
                        if (to_home.isChecked()) {
                            Intent homeIntent = new Intent();
                            homeIntent.setAction(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(homeIntent);
                        }
                    }
                }
            }
        });

        // 퀴즈 종료 눌렀을 때
        mStoptimer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mSubKidsquiz){   //구독완료시
                    Activity min = getActivity();
                    Toast.makeText(min, "퀴즈가 종료됩니다.", Toast.LENGTH_SHORT).show();
                    Navigation.keeppopup = false;
                    mButtonStarttimer.setEnabled(true); // 시작 버튼 활성화
                    mStoptimer.setEnabled(false); // 종료 버튼 비활성화
                    StopTimer(); // 타이머 종료
                }
                else{
                    if (mInterstitialAd.isLoaded()) {
                        Lock_AppLockImpl.onad = true;
                        Toast.makeText(getContext(), "광고 시청 완료 후 퀴즈가 종료됩니다.", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.show();
                    } else {
                        Activity min = getActivity();
                        Toast.makeText(min, "퀴즈가 종료됩니다.", Toast.LENGTH_SHORT).show();
                        Navigation.keeppopup = false;
                        mButtonStarttimer.setEnabled(true); // 시작 버튼 활성화
                        mStoptimer.setEnabled(false); // 종료 버튼 비활성화
                        StopTimer(); // 타이머 종료
                    }
                }
                Lock_AppLockImpl.stoptimer = true;
                //Lock_AppLockImpl.visibleCount--;
                //Log.d("visiblecount2 : ", String.valueOf(Lock_AppLockImpl.visibleCount++));
            }
        });

        // 넘버피커 선언
        final NumberPicker AgePicker = (NumberPicker) view.findViewById(R.id.numberpicker_age_picker);
        final NumberPicker MinPicker = (NumberPicker) view.findViewById(R.id.numberpicker_min_picker);
        final NumberPicker SecPicker = (NumberPicker) view.findViewById(R.id.numberpicker_sec_picker);

        // 나이 넘버피커
        NumberPicker.Formatter formatter1 = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int diff = (int) (value * 1);
                return "" + diff;
            }
        };
        AgePicker.setFormatter(formatter1);
        AgePicker.setMinValue(2);
        AgePicker.setMaxValue(6);
        AgePicker.setWrapSelectorWheel(false);
        valueAgePicker = 2;
        // 분 넘버피커
        NumberPicker.Formatter formatter2 = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int diff = (int) (value * 5);
                return "" + diff;
            }
        };
        MinPicker.setFormatter(formatter2);
        MinPicker.setMinValue(0);
        MinPicker.setMaxValue(12);
        MinPicker.setWrapSelectorWheel(false);

        // 초 넘버피커
        NumberPicker.Formatter formatter3 = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int diff = (int) (value * 10);
                return "" + diff;
            }
        };
        SecPicker.setFormatter(formatter3);
        SecPicker.setMinValue(0);
        SecPicker.setMaxValue(5);
        SecPicker.setWrapSelectorWheel(false);

        // 나이 입력값 불러오기
        AgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                valueAgePicker = AgePicker.getValue();
            }
        });

        // 분 입력값 불러오기
        MinPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                valueMinPicker = MinPicker.getValue();
            }
        });

        // 초 입력값 불러오기
        SecPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                valueSecPicker = SecPicker.getValue();
            }
        });
    }

    void startresumetimer() {
        Navigation.START_TIME_IN_MILLIS = START_TIME_IN_MILLIS;
        Log.d("타이머 시작","타이머 시작 in Nav");
        resumeintent = new Intent(getActivity(), Quiz_Timer.class);
        resumeintent.putExtra("time", START_TIME_IN_MILLIS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(resumeintent);
        }
        else {

            getActivity().startService(resumeintent);
        }
    }

    public void StopTimer(){
        Log.d("타이머 종료","타이머 종료 in Nav");
        //리뷰 작성
//        ReviewManager manager = ReviewManagerFactory.create(getContext());
//        Task<ReviewInfo> request = manager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // We can get the ReviewInfo object
//                ReviewInfo reviewInfo = task.getResult();
//                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
//                flow.addOnCompleteListener(task2 -> {
//                    // The flow has finished. The API does not indicate whether the user
//                    // reviewed or not, or even whether the review dialog was shown. Thus, no
//                    // matter the result, we continue our app flow.
//                });
//            } else {
//                Log.d("Detailed_Stat", "리뷰쓰기에 문제 있음");
//            }
//        });
        Navigation.keeppopup = false;
        Intent resumeintent = new Intent(getActivity(), Quiz_Timer.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(resumeintent);
        }
        else {
            getActivity().startService(resumeintent);
        }
    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 30){
            Toast.makeText(getContext(), "KidsQuiz를 찾아 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(getContext())) {              // 다른앱 위에 그리기 체크
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                //다른앱위에그리기 실행, 요청코드 설정하기위해 startActivityForResult 함수로 실행
                //Intent로 받아온 앱 정보는 onActivityResult 함수에서 처리
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)  && (resultCode == getActivity().RESULT_OK)){
            if (!Settings.canDrawOverlays(getContext())) {
                getActivity().finish();
            }
        }
    }
    private Intent getSelfIntent() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        return intent;
    }
}