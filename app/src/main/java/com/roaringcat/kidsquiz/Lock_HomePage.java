package com.roaringcat.kidsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

public class Lock_HomePage extends Lock_BaseActivity implements OnClickListener {
	public static final String TAG = "HomePage";
	private Button btOnOff;
	private Button btChange;
	TemplateView template;
	boolean mSubKidsquiz = false;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypage_lock_management);
		System.out.println("HomePage");

		btOnOff = (Button) findViewById(R.id.bt_on_off);
		btOnOff.setOnClickListener(this);
		btChange = (Button) findViewById(R.id.bt_change);
		btChange.setText(R.string.change_passcode);
		btChange.setOnClickListener(this);
		updateUI();
	}

	@Override
	public void onClick(View view) {
		if (view.equals(btOnOff)) {
			int type = Lock_LockManager.getInstance().getAppLock().isPasscodeSet() ? Lock_AppLock.DISABLE_PASSLOCK
					: Lock_AppLock.ENABLE_PASSLOCK;
			Intent intent = new Intent(this, Lock_AppLockActivity.class);
			intent.putExtra("mSubKidsquiz", mSubKidsquiz);
			intent.putExtra(Lock_AppLock.TYPE, type);
			startActivityForResult(intent, type);
		} else if (view.equals(btChange)) {
			Intent intent = new Intent(this, Lock_AppLockActivity.class);
			intent.putExtra(Lock_AppLock.TYPE, Lock_AppLock.CHANGE_PASSWORD);
			intent.putExtra("mSubKidsquiz", mSubKidsquiz);
			intent.putExtra(Lock_AppLock.MESSAGE,
					getString(R.string.enter_old_passcode));
			startActivityForResult(intent, Lock_AppLock.CHANGE_PASSWORD);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case Lock_AppLock.DISABLE_PASSLOCK:
				break;
			case Lock_AppLock.ENABLE_PASSLOCK:
			case Lock_AppLock.CHANGE_PASSWORD:
				if (resultCode == RESULT_OK) {
					Toast.makeText(this, getString(R.string.setup_passcode),
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
		updateUI();
	}

	private void updateUI() {
		if (Lock_LockManager.getInstance().getAppLock().isPasscodeSet()) {
			btOnOff.setText(R.string.disable_passcode);
			btChange.setVisibility(View.VISIBLE);
		} else {
			btOnOff.setText(R.string.enable_passcode);
			btChange.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;
		template = (TemplateView) findViewById(R.id.ad_lock_manage);
		if(((Navigation)Navigation.context).mSubKidsquiz){
			template.setVisibility(template.GONE);
		}
		MobileAds.initialize(this, "ca-app-pub-9350891168282170~5512537782");
		//Test 광고 id : ca-app-pub-3940256099942544/2247696110
		AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-9350891168282170/4038307032");
		// 내 네이티브 광고 id : ca-app-pub-9350891168282170/4038307032

		builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
			@Override
			public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

				template.setNativeAd(unifiedNativeAd);

			}
		});
		AdLoader adLoader = builder.build();
		adLoader.loadAd(new AdRequest.Builder().build());
	}
}
