package com.imcore.yunmingdemo.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.imcore.yunmingdemo.R;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				Intent lodingIntent = new Intent(SplashActivity.this,LoginScreenActivity.class);
				SplashActivity.this.startActivity(lodingIntent);
				SplashActivity.this.finish();
			}
		}, 3000);
		

	}

}