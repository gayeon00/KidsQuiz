package com.roaringcat.kidsquiz;
//비밀번호 입력 클래스
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.List;

public class Lock_AppLockActivity extends Lock_BaseActivity {
	public static final String TAG = "AppLockActivity";
//	private int tonav = 0;
	private int type = -1;
	private String oldPasscode = null;
	protected EditText codeField1 = null;
	protected EditText codeField2 = null;
	protected EditText codeField3 = null;
	protected EditText codeField4 = null;
	protected InputFilter[] filters = null;
	protected TextView tvMessage = null;
	boolean mSubKidsquiz = false;
	public TemplateView template;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("AppLockActivity");
		setContentView(R.layout.lock_input_password);

		tvMessage = (TextView) findViewById(R.id.input_password_message);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String message = extras.getString(Lock_AppLock.MESSAGE);
			if (message != null) {
				tvMessage.setText(message);
			}

			type = extras.getInt(Lock_AppLock.TYPE, -1);
//			tonav = extras.getInt("tonav", 0);
		}
		Intent intent2 = getIntent();
		try {
			mSubKidsquiz = ((Navigation)Navigation.context).mSubKidsquiz;	//홈 갔다가 resume 될때
		} catch (NullPointerException e) {
			mSubKidsquiz = intent2.getBooleanExtra("mSubKidsquiz", false);	//splash에서 실행될때
		}
		filters = new InputFilter[2];
		filters[0] = new InputFilter.LengthFilter(1);
		filters[1] = numberFilter;

		codeField1 = (EditText) findViewById(R.id.passcode_1);
		setupEditText(codeField1);

		codeField2 = (EditText) findViewById(R.id.passcode_2);
		setupEditText(codeField2);

		codeField3 = (EditText) findViewById(R.id.passcode_3);
		setupEditText(codeField3);

		codeField4 = (EditText) findViewById(R.id.passcode_4);
		setupEditText(codeField4);

		// setup the keyboard
		((Button) findViewById(R.id.button0)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button1)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button2)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button3)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button4)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button5)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button6)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button7)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button8)).setOnClickListener(btnListener);
		((Button) findViewById(R.id.button9)).setOnClickListener(btnListener);

		((Button) findViewById(R.id.button_clear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						clearFields();
					}
				});

		((Button) findViewById(R.id.button_erase))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						onDeleteKey();
					}
				});

		overridePendingTransition(R.anim.slide_up, R.anim.nothing);

		switch (type) {

			case Lock_AppLock.DISABLE_PASSLOCK:
				this.setTitle("비밀번호 제거");
				break;
			case Lock_AppLock.ENABLE_PASSLOCK:
				this.setTitle("비밀번호 설정");
				break;
			case Lock_AppLock.CHANGE_PASSWORD:
				this.setTitle("비밀번호 변경");
				break;
			case Lock_AppLock.UNLOCK_PASSWORD:
				this.setTitle("Unlock Passcode");
				break;
		}
	}

	public int getType() {
		return type;
	}

	protected void onPasscodeInputed() {
		String passLock = codeField1.getText().toString()
				+ codeField2.getText().toString()
				+ codeField3.getText().toString() + codeField4.getText();

		codeField1.setText("");
		codeField2.setText("");
		codeField3.setText("");
		codeField4.setText("");
		codeField1.requestFocus();
		switch (type) {

			case Lock_AppLock.DISABLE_PASSLOCK:
				if (Lock_LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
					setResult(RESULT_OK);
					Lock_LockManager.getInstance().getAppLock().setPasscode(null);
					finish();
				} else {
					onPasscodeError();
				}
				break;

			case Lock_AppLock.ENABLE_PASSLOCK:
				if (oldPasscode == null) {
					tvMessage.setText(R.string.reenter_passcode);
					oldPasscode = passLock;
				} else {
					if (passLock.equals(oldPasscode)) {
						setResult(RESULT_OK);
						Lock_LockManager.getInstance().getAppLock()
								.setPasscode(passLock);
						finish();
					} else {
						oldPasscode = null;
						tvMessage.setText(R.string.enter_passcode);
						onPasscodeError();
					}
				}
				break;

			case Lock_AppLock.CHANGE_PASSWORD:
				if (Lock_LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
					tvMessage.setText(R.string.enter_passcode);
					type = Lock_AppLock.ENABLE_PASSLOCK;
				} else {
					onPasscodeError();
				}
				break;

			case Lock_AppLock.UNLOCK_PASSWORD:
				if (Lock_LockManager.getInstance().getAppLock().checkPasscode(passLock)) {
					setResult(RESULT_OK);
					Intent i = new Intent(this, Navigation.class);
					i.putExtra("mSubKidsquiz", mSubKidsquiz);
					startActivity(i);
					finish();
				} else {
					onPasscodeError();
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if (type == Lock_AppLock.UNLOCK_PASSWORD) {
			// back to home screen
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(intent);
		} else {
			super.onBackPressed();
		}
	}

	protected void setupEditText(EditText editText) {
		editText.setInputType(InputType.TYPE_NULL);
		editText.setFilters(filters);
		editText.setOnTouchListener(touchListener);
		editText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {
			onDeleteKey();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onDeleteKey() {
		if (codeField1.isFocused()) {

		} else if (codeField2.isFocused()) {
			codeField1.requestFocus();
			codeField1.setText("");
		} else if (codeField3.isFocused()) {
			codeField2.requestFocus();
			codeField2.setText("");
		} else if (codeField4.isFocused()) {
			codeField3.requestFocus();
			codeField3.setText("");
		}
	}

	private OnClickListener btnListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int currentValue = -1;
			int id = view.getId();
			if (id == R.id.button0) {
				currentValue = 0;
			} else if (id == R.id.button1) {
				currentValue = 1;
			} else if (id == R.id.button2) {
				currentValue = 2;
			} else if (id == R.id.button3) {
				currentValue = 3;
			} else if (id == R.id.button4) {
				currentValue = 4;
			} else if (id == R.id.button5) {
				currentValue = 5;
			} else if (id == R.id.button6) {
				currentValue = 6;
			} else if (id == R.id.button7) {
				currentValue = 7;
			} else if (id == R.id.button8) {
				currentValue = 8;
			} else if (id == R.id.button9) {
				currentValue = 9;
			} else {
			}

			// set the value and move the focus
			String currentValueString = String.valueOf(currentValue);
			if (codeField1.isFocused()) {
				codeField1.setText(currentValueString);
				codeField2.requestFocus();
				codeField2.setText("");
			} else if (codeField2.isFocused()) {
				codeField2.setText(currentValueString);
				codeField3.requestFocus();
				codeField3.setText("");
			} else if (codeField3.isFocused()) {
				codeField3.setText(currentValueString);
				codeField4.requestFocus();
				codeField4.setText("");
			} else if (codeField4.isFocused()) {
				codeField4.setText(currentValueString);
			}

			if (codeField4.getText().toString().length() > 0
					&& codeField3.getText().toString().length() > 0
					&& codeField2.getText().toString().length() > 0
					&& codeField1.getText().toString().length() > 0) {
				onPasscodeInputed();
			}
		}
	};

	protected void onPasscodeError() {
		Toast toast = Toast.makeText(this, getString(R.string.passcode_wrong),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30);
		toast.show();

		Thread thread = new Thread() {
			public void run() {
				Animation animation = AnimationUtils.loadAnimation(
						Lock_AppLockActivity.this, R.anim.shake);
				findViewById(R.id.input_password).startAnimation(animation);
				codeField1.setText("");
				codeField2.setText("");
				codeField3.setText("");
				codeField4.setText("");
				codeField1.requestFocus();
			}
		};
		runOnUiThread(thread);
	}

	private InputFilter numberFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
								   Spanned dest, int dstart, int dend) {
			if (source.length() > 1) {return "";}
			// erase
			if (source.length() == 0) {return null;}
			try {
				int number = Integer.parseInt(source.toString());
				if ((number >= 0) && (number <= 9))
					return String.valueOf(number);
				else
					return "";
			} catch (NumberFormatException e) {
				return "";
			}
		}
	};

	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			clearFields();
			return false;
		}
	};

	private void clearFields() {
		codeField1.setText("");
		codeField2.setText("");
		codeField3.setText("");
		codeField4.setText("");
		codeField1.postDelayed(new Runnable() {
			@Override
			public void run() {
				codeField1.requestFocus();
			}
		}, 200);
	}

	@Override
	protected void onResume() {
		super.onResume();
		template = (TemplateView) findViewById(R.id.ad_input_password);
		if(mSubKidsquiz){
			template.setVisibility(template.GONE);
		}
		MobileAds.initialize((this), "ca-app-pub-9350891168282170~5512537782");
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