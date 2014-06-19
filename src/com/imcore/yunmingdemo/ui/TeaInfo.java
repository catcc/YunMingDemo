package com.imcore.yunmingdemo.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class TeaInfo extends Activity {
	private List<Collection> list;
	private ListView lv;
	private Collection collection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tea_info);
		lv = (ListView)findViewById(R.id.lv_tea_info);
		new TeaInfoTask().execute();
		
	}

	private class TeaInfoAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return list.size();
				
		}

		@Override
		public Object getItem(int arg0) {
			if(list.get(arg0)==null){
				return null;
			}else{
			return list.get(arg0);
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
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			String Title = list.get(arg0).title;
			collection = list.get(arg0);
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ list.get(arg0).pictureUrl, viewHolder.img);
			viewHolder.tvTitle.setText(Title);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(TeaInfo.this,TeaInfoDetail.class);
					intent.putExtra("infoId", collection.id);
					startActivity(intent);
					
				}
			});
			return view;
		}
		
		class ViewHolder{
			TextView tvTitle;
			ImageView img;
		}
	}
	
	
	class TeaInfoTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			String url ="/news/list.do";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("offset", 0);
			map.put("fetchSize", 1000);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
					String data = rjs.getData();
					list = JsonUtil.toObjectList(data, Collection.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			lv.setAdapter(new TeaInfoAdapter());
			
			super.onPostExecute(result);
		}
		
	}

}
