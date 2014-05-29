package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Category;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class MallTab extends Fragment {
	private List<Category> CaList;
	private List<String> CNList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_mall_tab, null);
		
		new CategoryTask().execute();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return view;
	}
	
	class CategoryTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String url = "/category/list.do";
			RequestEntity entity = new RequestEntity(url, 0, null);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("js", data);
				Class<Category> cass = Category.class;
				CaList = JsonUtil.toObjectList(data, cass);
				CNList = new ArrayList<String>();
				for (int i = 0; i < CaList.size(); i++) {
					Log.i("ee", CaList.get(i).getImageUrl() + CaList.get(i).getCategoryName());
					String CName = CaList.get(i).getCategoryName();
					String CUrl = CaList.get(i).getImageUrl();
					CNList.add(CName);
					new CategoryImgTask().execute(HttpHelper.ImageURL+"/"+CUrl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	
		
		
		class CategoryImgTask extends AsyncTask<String, Void, Void> {
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
