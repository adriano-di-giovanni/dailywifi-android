package com.adrianodigiovanni.dailywifi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.adrianodigiovanni.app.AbstractPortraitActivity;

public class SplashActivity extends AbstractPortraitActivity {
	
	private static final long DELAY_MILLIS = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				SplashActivity thisActivity = SplashActivity.this;
				Intent intent = new Intent(thisActivity, MainActivity.class);
				thisActivity.startActivity(intent);
				thisActivity.finish();
			}
			
		}, DELAY_MILLIS);
	}
}
