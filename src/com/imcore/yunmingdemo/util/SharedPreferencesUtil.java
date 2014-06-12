package com.imcore.yunmingdemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.imcore.yunmingdemo.data.User;

/**
 * 保存用户信息与读取
 * 
 * @author Administrator
 * 
 */
public class SharedPreferencesUtil {
	private static final String USER_INFO = "user_info";
	private static final String USER_ID = "user_id";
	private static final String TOKEN = "token";
	private static final String PHONENUMBER = "phone_number";
	private static final String USER_NAME = "user_name";
	private static final String PASSWORD = "passsword";
	private static final String IS_SAVED = "isSaved";
	private static final String IDENTITY = "indentity";
	private static final String REAL_PASSWORD = "PassWord";

	private Context mContext;

	// 无参构造方法
	public SharedPreferencesUtil() {
	}

	public SharedPreferencesUtil(Context context) {
		mContext = context;
	}

	/**
	 * 读取用户名与密码
	 * 
	 * @return
	 */
//	public User getUserInfo() {
//		User user = null;
//
//		SharedPreferences sp = mContext.getSharedPreferences(USER_INFO,
//				Context.MODE_PRIVATE);
////		if (!TextUtil.isEmptyString(sp.getString(USER_ID, ""))
////				&& !TextUtil.isEmptyString(sp.getString(TOKEN, ""))) {
//			user = new User();
//			user.id = sp.getString(USER_ID, "");
//			user.token = sp.getString(TOKEN, "");
//			user.userName = sp.getString(USER_NAME, "");
//			user.password = sp.getString(PASSWORD, "");
////		}
//		return user;
//	}

	/**
	 * 保存用户信息
	 * 
	 * @param phoneNumber
	 * @param password
	 */
	public void saveUserInfo(User user) {
		SharedPreferences sp = mContext.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		boolean isSaved = sp.edit().putLong(USER_ID, user.id).putLong(IDENTITY, user.identity)
				.putString(TOKEN, user.token).putString(USER_NAME, user.userName)
				.putString(PHONENUMBER, user.phoneNumber)
				.putString(PASSWORD, user.password).putBoolean(IS_SAVED, user.isSaved).putString(REAL_PASSWORD, user.PassWord).commit();
		if (isSaved) {
			Log.i("isSaved", "成功保存");
		}
	}

}
