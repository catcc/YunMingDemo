package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.User;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class MyTab extends  Fragment implements OnClickListener{
	private User user;
	private ImageView imgMy,imgEdit;
	private TextView tvName;
	private RelativeLayout rlRecord,rlFavorite,rlShoppCar,rlNews;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_my_tab, null);
		imgMy = (ImageView)view.findViewById(R.id.img_my_picture);
		tvName = (TextView)view.findViewById(R.id.tv_my_name);
		rlRecord = (RelativeLayout)view.findViewById(R.id.rl_my);
		rlFavorite = (RelativeLayout)view.findViewById(R.id.rl_my_one);
		rlShoppCar = (RelativeLayout)view.findViewById(R.id.rl_my_two);
		rlNews = (RelativeLayout)view.findViewById(R.id.rl_my_three);
		imgEdit = (ImageView)view.findViewById(R.id.mine_edit);
		
		new MyTask().execute();
		rlRecord.setOnClickListener(this);
		rlFavorite.setOnClickListener(this);
		rlShoppCar.setOnClickListener(this);
		rlNews.setOnClickListener(this);
		imgEdit.setOnClickListener(this);
		return view;
	}

	
	//用户信息查询
		class MyTask extends AsyncTask<Void, Void, Void>{
			private static final String USER_INFO = "user_info";
			private static final String USER_ID = "user_id";
			private static final String TOKEN = "token";
			@Override
			protected void onPostExecute(Void result) {
				new ImageFetcher().fetch(user.avatarUrl, imgMy);
				tvName.setText(user.name);
				super.onPostExecute(result);
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				String url = "/user/get.do";
				SharedPreferences sp = getActivity().getSharedPreferences(USER_INFO,
						Context.MODE_PRIVATE);
				long userId = sp.getLong(USER_ID, 0);
				String token = sp.getString(TOKEN, "");
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("userId", userId);
				map.put("token", token);
				RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
				String js;
				try {
					js = HttpHelper.execute(entity);
					ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
					String data = rjs.getData();
					user = JsonUtil.toObject(data,User.class);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
			

			
		}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_my:
			Intent intent = new Intent(getActivity(),AllOrderItem.class);
			startActivity(intent);
			break;

		case R.id.rl_my_one:
			Intent intent1 = new Intent(getActivity(),Favorite.class);	
			startActivity(intent1);
			break;
		case R.id.rl_my_two:
			Intent intent2 = new Intent(getActivity(),Shopping.class);
			startActivity(intent2);
			break;
		case R.id.rl_my_three:
		
			break;
			
		case R.id.mine_edit:
			Intent intent4 = new Intent(getActivity(),MyEdit.class);
			startActivity(intent4);
			break;
			
		default:
			break;
		}
	}

}
