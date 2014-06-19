package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Collection;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class Favorite extends Activity {
	private List<Collection> cList;
	private List<Collection> relList;
	private ListView lv;
	private Collection collection;
	private TextView tvAdd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		lv = (ListView)findViewById(R.id.lv_collect);
		tvAdd = (TextView)findViewById(R.id.tv_add_collect);
		
		new FavoriteTask().execute();
		
	
	}

	private class CollectAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return relList.size();
				
		}

		@Override
		public Object getItem(int arg0) {
			if(cList.get(arg0)==null){
				return null;
			}else{
			return relList.get(arg0);
			}
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			final ViewHolder viewHolder ;
			if(view == null){
			view = getLayoutInflater().inflate(R.layout.collect_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView)view.findViewById(R.id.tv_collect_title);
			viewHolder.img = (ImageView)view.findViewById(R.id.img_collect);
			viewHolder.btnDelete = (Button)view.findViewById(R.id.btn_delete);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			collection = relList.get(arg0);
			String Title = relList.get(arg0).title;
			viewHolder.btnDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					new FavoriteDeleteTask(collection.id).execute();
				}
			});
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ relList.get(arg0).pictureUrl, viewHolder.img);
			viewHolder.tvTitle.setText(Title);
			
			return view;
		}
		
		class ViewHolder{
			TextView tvTitle;
			ImageView img;
			Button btnDelete;
		}
	}
	//解析收藏列表
	class FavoriteTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/user/favorite/list.do";
			SharedPreferences sp = Favorite.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userId", userId);
			map.put("type",2);
			map.put("offset", 0);
			map.put("fetchSize", 1000);
			RequestEntity entity = new RequestEntity(url,HttpMethod.GET,map);	
			relList = new ArrayList<Collection>();
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
				String data = rjs.getData();
				cList = JsonUtil.toObjectList(data,Collection.class);
				for(int i = 0;i<cList.size();i++){
					if(cList.get(i)!=null){
						relList.add(cList.get(i));
					}
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			lv.setAdapter(new CollectAdapter());
			tvAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					new FavoriteAddTask().execute();
					
				}
			});
			super.onPostExecute(result);
		}
		
	}
	//删除收藏
	class FavoriteDeleteTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private long id;
		public FavoriteDeleteTask(long id){
			this.id = id;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/user/favorite/delete.do";
			SharedPreferences sp = Favorite.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userId", userId);
			map.put("id", id);
			map.put("type",2);
			RequestEntity entity = new RequestEntity(url,HttpMethod.GET,map);	
			relList = new ArrayList<Collection>();
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new FavoriteTask().execute();
			super.onPostExecute(result);
		}
		
	}
	//添加收藏
	class FavoriteAddTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/user/favorite/add.do";
			SharedPreferences sp = Favorite.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userId", userId);
			map.put("id", 32);
			map.put("type",2);
			map.put("comment", "呵呵");
			RequestEntity entity = new RequestEntity(url,HttpMethod.POST,map);	
			relList = new ArrayList<Collection>();
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new FavoriteTask().execute();
			super.onPostExecute(result);
		}
		
	}
	
}
