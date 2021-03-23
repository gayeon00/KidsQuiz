package com.roaringcat.kidsquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Detailed_Statistics extends Lock_BaseActivity {
    ProgressBar korPercentileProgressBar;
    ProgressBar matPercentileProgressBar;
    ProgressBar perPercentileProgressBar;
    int kc_Status = 0;
    int mc_Status = 0;
    int pc_Status = 0;
    int kp_Status = 0;
    int mp_Status = 0;
    int pp_Status = 0;
    double kor_correct_rate=0;
    double mat_correct_rate=0;
    double per_correct_rate=0;
    private Handler handler = new Handler();
    TextView tvKorCorrectRate, tvMatCorrectRate, tvPerCorrectRate, tvKorPercentile, tvMatPercentile, tvPerPercentile;
    double kor_percentile=0;
    double mat_percentile=0;
    double per_percentile=0;
    private FirebaseAuth mAuth;
    Context context;
    public Map<String, Double> personalCorrectRate = new HashMap<>();
    FirebaseUser user;
    ProgressBar korProgress;
    ProgressBar matProgress;
    ProgressBar perProgress;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        ReviewManager manager = new FakeReviewManager(context);
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    finish();
                });
            } else {
                Log.d("Detailed_Stat", "리뷰쓰기에 문제 있음");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_statistics);
        context = this;
        Intent main_intent = getIntent();
        personalCorrectRate = (Map<String, Double>) main_intent.getSerializableExtra("personalCorrectRate");
        System.out.println("personalCorrectRate : "+personalCorrectRate);
        Button btn_share = (Button)findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View u = findViewById(R.id.detailed_stat);
                ScrollView z = findViewById(R.id.detailed_stat);
                int totalHeight = z.getChildAt(0).getHeight();
                int totalWidth = z.getChildAt(0).getWidth();
                Bitmap b = getBitmapFromView(z,totalHeight,totalWidth);
                Uri stat_uri = getImageUri(getApplicationContext(), b);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, stat_uri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using")); // 변경가능
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("error 확인 : ", String.valueOf(personalCorrectRate.values()));
        kor_correct_rate = personalCorrectRate.get("kor_correct_rate");
        mat_correct_rate = personalCorrectRate.get("mat_correct_rate");
        per_correct_rate = personalCorrectRate.get("per_correct_rate");
        kor_percentile = personalCorrectRate.get("kor_percentile");
        mat_percentile = personalCorrectRate.get("mat_percentile");
        per_percentile = personalCorrectRate.get("per_percentile");

        System.out.println("kor_correct_rate : "+kor_correct_rate);
        System.out.println("mat_correct_rate : "+mat_correct_rate);
        System.out.println("per_correct_rate : "+per_correct_rate);

        korPercentileProgressBar = findViewById(R.id.kor_horizontal_progressBar);
        matPercentileProgressBar = findViewById(R.id.mat_horizontal_progressBar);
        perPercentileProgressBar = findViewById(R.id.per_horizontal_progressBar);

        Resources res = getResources();
        Drawable kordrawable = res.getDrawable(R.drawable.circle_progressbar_kor);
        korProgress = (ProgressBar)findViewById(R.id.kor_circleProgressbar);
        korProgress.setProgress(0);   // Main Progress
        korProgress.setSecondaryProgress(100); // Secondary Progress
        korProgress.setMax(100); // Maximum Progress
        korProgress.setProgressDrawable(kordrawable);

        Drawable matdrawable = res.getDrawable(R.drawable.circle_progressbar_mat);
        matProgress = (ProgressBar)findViewById(R.id.mat_circleProgressbar);
        matProgress.setProgress(0);   // Main Progress
        matProgress.setSecondaryProgress(100); // Secondary Progress
        matProgress.setMax(100); // Maximum Progress
        matProgress.setProgressDrawable(matdrawable);

        Drawable perdrawable = res.getDrawable(R.drawable.circle_progressbar_per);
        perProgress = (ProgressBar)findViewById(R.id.per_circleProgressbar);
        perProgress.setProgress(0);   // Main Progress
        perProgress.setSecondaryProgress(100); // Secondary Progress
        perProgress.setMax(100); // Maximum Progress
        perProgress.setProgressDrawable(perdrawable);

        tvKorCorrectRate = findViewById(R.id.kor_correct_rate);
        tvMatCorrectRate = findViewById(R.id.mat_correct_rate);
        tvPerCorrectRate = findViewById(R.id.per_correct_rate);
        tvKorPercentile = findViewById(R.id.kor_percentile);
        tvMatPercentile = findViewById(R.id.mat_percentile);
        tvPerPercentile = findViewById(R.id.per_percentile);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 정답률 ProgressBar 애니메이션
                while (kc_Status < kor_correct_rate) {
                    kc_Status += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            korProgress.setProgress(kc_Status);
                            tvKorCorrectRate.setText(kc_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (kc_Status==0){
                    tvKorCorrectRate.setText(kc_Status + "%");
                }
                while (mc_Status < mat_correct_rate) {
                    mc_Status += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            matProgress.setProgress(mc_Status);
                            tvMatCorrectRate.setText(mc_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mc_Status==0){
                    tvMatCorrectRate.setText(mc_Status + "%");
                }
                while (pc_Status < per_correct_rate) {
                    pc_Status += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            perProgress.setProgress(pc_Status);
                            tvPerCorrectRate.setText(pc_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (pc_Status==0){
                    tvPerCorrectRate.setText(pc_Status + "%");
                }
                // 백분위 ProgressBar 애니메이션
                while(kp_Status < kor_percentile) {
                    kp_Status++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(kp_Status < 100 - kor_percentile) {
                                korPercentileProgressBar.setProgress(kp_Status);
                            }
                            tvKorPercentile.setText(kp_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (kp_Status==0){
                    tvKorPercentile.setText(kp_Status + "%");
                }
                while(mp_Status < mat_percentile) {
                    mp_Status++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mp_Status < 100 - mat_percentile) {
                                matPercentileProgressBar.setProgress(mp_Status);
                            }
                            tvMatPercentile.setText(mp_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mp_Status==0){
                    tvMatPercentile.setText(mp_Status + "%");
                }
                while(pp_Status < per_percentile) {
                    pp_Status++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(pp_Status < 100 - per_percentile) {
                                perPercentileProgressBar.setProgress(pp_Status);
                            }
                            tvPerPercentile.setText(pp_Status + "%");
                        }
                    });
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (pp_Status==0){
                    tvPerPercentile.setText(pp_Status + "%");
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
