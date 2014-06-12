package com.imcore.yunmingdemo.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.imcore.yunmingdemo.R;

public class SplashActivity extends Activity {
	private static final String USER_INFO = "user_info";
	private static final String IS_SAVED = "isSaved";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		
		new Handler().postDelayed(new splashLog()
			
			, 1000);
		

	}
	
	class splashLog implements Runnable{
		private SharedPreferences sp = SplashActivity.this.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		@Override
		public void run() {
			Intent intent = new Intent();
			if(sp.getBoolean(IS_SAVED, false)){
				intent.setClass(SplashActivity.this,HomeActivity.class);
				startActivity(intent);
				finish();
			}else{
				intent.setClass(SplashActivity.this,LoginScreenActivity.class);
				startActivity(intent);
				finish();
			}
			
		}
		
	}

}