package com.imcore.yunmingdemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;

public class SearchResult extends Activity {
	private String keyword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
		Intent intent = getIntent();
		keyword = intent.getStringExtra("search_context");
		new SearchResultTask().execute();
	}

	class SearchResultTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/search/keyword.do";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("keyword", keyword);
			map.put("type",1);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
					String data = rjs.getData();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
}
