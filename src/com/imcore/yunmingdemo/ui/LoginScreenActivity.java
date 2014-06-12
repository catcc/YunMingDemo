package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.application.MyApplicition;
import com.imcore.yunmingdemo.data.User;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.util.ConnectivityUtil;
import com.imcore.yunmingdemo.util.JsonUtil;
import com.imcore.yunmingdemo.util.SharedPreferencesUtil;
import com.imcore.yunmingdemo.util.TextUtil;
import com.imcore.yunmingdemo.util.ToastUtil;

public class LoginScreenActivity extends Activity implements OnClickListener{
	private Button btnEntry;
	private ImageView imgForget;
	private EditText etUser;
	private EditText etPassword;
	private ProgressDialog pgDialog;
	private static final String USER_INFO = "user_info";
	private static final String PHONENUMBER = "phone_number";
	private static final String REAL_PASSWORD = "PassWord";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_loginscreen);
		MyApplicition.xje = "s";
		btnEntry = (Button)findViewById(R.id.btn_entry);
		imgForget = (ImageView)findViewById(R.id.img_forget);
		
		etUser = (EditText)findViewById(R.id.et_user);
		etPassword = (EditText)findViewById(R.id.et_pass);
		
		btnEntry.setOnClickListener(this);
		imgForget.setOnClickListener(this);
			
		SharedPreferences sp = this.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		if( sp!=null){
			etUser.setText(sp.getString(PHONENUMBER, ""));
			etPassword.setText(sp.getString(REAL_PASSWORD, ""));
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_entry :
			doLogin();
			break;
		case R.id.img_forget :
			
			break;
		}
	}

	/**
	 * 登录前检查网络是否连接
	 */
	 private void doLogin(){
		 if(!ConnectivityUtil.isOnline(this)){
			 ToastUtil.showToast(this, "请检查网络");
			 return;
		 }
		 
		 String userName = etUser.getText().toString();
		 String password = etPassword.getText().toString();
		 
		 if(!valiDateInput(userName, password)){
			 return;
		 }
		 
		 new doLoginAsyncTask(userName,password).execute();
	 }
	 

	 
	 private class doLoginAsyncTask extends AsyncTask<Void, Void, String>{
		 private String mUserName,mPassword;
		 public doLoginAsyncTask(String userName, String password) {
			 	mUserName = userName;
				mPassword = password;
				
			}
		 @Override
			protected void onPreExecute() {
				pgDialog = ProgressDialog.show(LoginScreenActivity.this, "请稍候", "正在登录");
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... arg0) {
				String url = "/passport/login.do";

				// 把请求参数装到map中
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("phoneNumber", mUserName);
				args.put("password", mPassword);
				args.put("client", "android");

				// 构造RequestEntity
				RequestEntity entity = new RequestEntity(url, args);

				String jsonResponse = null;
				try {
					jsonResponse = HttpHelper.execute(entity);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return jsonResponse;
			}

			@Override
			protected void onPostExecute(String result) {
				pgDialog.dismiss();
				pgDialog = null;
				// 响应回来之后，构造ResponseEntity
				if (TextUtil.isEmptyString(result)) {
					ToastUtil.showToast(LoginScreenActivity.this, "服务器响应错误！");
					return;
				}

				ResponseJsonEntity resJsonEntity = ResponseJsonEntity
						.fromJSON(result);
				if (resJsonEntity.getStatus() == 200) {
					String jsonData = resJsonEntity.getData();
					Log.i("user", jsonData);

					User user = JsonUtil.toObject(jsonData, User.class);

					// 保存账号密码
					if (user != null) {
						SharedPreferencesUtil spUtil = new SharedPreferencesUtil(LoginScreenActivity.this);
						user.isSaved = true;
						user.PassWord = "111111";
						spUtil.saveUserInfo(user);
						}
					
					gotoHome();
					finish();
				} else {
					ToastUtil.showToast(LoginScreenActivity.this,
							resJsonEntity.getMessage());
					Log.i("user", resJsonEntity.getMessage());
				}
				 super.onPostExecute(result);
			}

		 
		/**
		 * 登录成功 跳到主页面
		 */
		private void gotoHome() {
			Intent intent = new Intent(LoginScreenActivity.this, HomeActivity.class);
			startActivity(intent);
		}		
		 
	 }
	 
	 private boolean valiDateInput(String userName,String password){
		 if (TextUtil.isEmptyString(userName)) {
				ToastUtil.showToast(this, "用户名不能为空");
				return false;
			}
			if (TextUtil.isEmptyString(password)) {
				ToastUtil.showToast(this, "密码不能为空");
				return false;
			}
			if (!TextUtil.isPhoneNumber(userName)) {
				ToastUtil.showToast(this, "用户名格式不正确");
				return false;
			}
			if (!TextUtil.isNumber(password)) {
				ToastUtil.showToast(this, "密码为纯数字");
				return false;
			}
			return true;
	 }
	
}
