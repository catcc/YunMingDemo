package com.imcore.yunmingdemo.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
	private Context mcontext;
	private String USER_ID = "USER_ID";
	private String USER_TOKEN = "USER_TOKEN";
	private String USER_PHONE_NUMBER = "USER_PHONE_NUMBER";
	private String USER_PASSWORD = "USER_PASSWORD";
	
	public SharedPreferencesUtil(Context context){
		mcontext = context;
	}
	public void SharedPreferencesSave(User user){
		SharedPreferences ss = mcontext.getSharedPreferences(USER_ID, Context.MODE_PRIVATE);
		
		boolean p = ss.edit().putString(USER_ID, user.userId).putString(USER_TOKEN, user.token)
		.putString(USER_PHONE_NUMBER, user.phoneNumber).putString(USER_PASSWORD, user.password).commit();
	}
}
