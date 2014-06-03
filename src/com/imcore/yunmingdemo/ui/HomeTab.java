package com.imcore.yunmingdemo.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.application.MyApplicition;
import com.imcore.yunmingdemo.data.ImageTop;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;


public class HomeTab extends Fragment {
	private ViewPager vp;
	private HomeTabAdapter homeTabAdapter;
	public  List<ImageTop> topList;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.activity_home_tab, null);
		vp = (ViewPager)rootView.findViewById(R.id.view_pager);
		new ImageTask().execute();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		homeTabAdapter = new HomeTabAdapter();
		vp.setAdapter(homeTabAdapter);

		return rootView;
	}
	
	private class HomeTabAdapter extends FragmentStatePagerAdapter{

		public HomeTabAdapter() {
			super(getActivity().getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int arg0) {
			HomeImageFragment hif = new HomeImageFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("position", arg0);
			hif.setArguments(bundle);
			return hif;
		}

		@Override
		public int getCount() {
			return MyApplicition.tpList.size();
		}

		
		
	}
	//解析JASON
	class ImageTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String url = "/topline/list.do";
			RequestEntity entity = new RequestEntity(url, 0, null);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("data", data);
				Class<ImageTop> clss = ImageTop.class;
				topList = JsonUtil.toObjectList(data, clss);
				
				for (int i = 0; i < topList.size(); i++) {
					Log.i("dt", topList.get(i).getImageUrl());
					String imgUrl = topList.get(i).getImageUrl();
					MyApplicition.tpList.add(HttpHelper.ImageURL+"/"+imgUrl);
					new dawlTask().execute(HttpHelper.ImageURL + "/"
							+ ( topList.get(i)).getImageUrl());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
//			for (int i = 0; i < topList.size(); i++) {
//				new dawlTask().execute(HttpHelper.ImageURL + "/"
//						+ ( topList.get(i)).getImageUrl());
			super.onPostExecute(result);
//		}
	   
	   }
		//根据解析出的图片URL去下载图片
		class dawlTask extends AsyncTask<String, Void, Void> {
			private String imgUrl;

			@Override
			protected Void doInBackground(String... params) {
				imgUrl = params[0];
				boolean isSucc = ImageFetcher.downLoadImage(imgUrl);
				Log.i("img", isSucc + "");
				return null;
			}
			
		}	
	}	
	
		
	
}
