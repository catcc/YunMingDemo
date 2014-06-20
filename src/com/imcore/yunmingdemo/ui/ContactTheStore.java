package com.imcore.yunmingdemo.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Store;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;



public class ContactTheStore extends Activity {
	private List<Store> storeList;
	private ListView lv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_the_store);
		lv = (ListView)findViewById(R.id.lv_store);
		new CContactTheStoreTask().execute();
	}
	
	private class StoreAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return storeList.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return storeList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			final ViewHolder viewHolder;
			if(view == null){
			view = getLayoutInflater().inflate(R.layout.contact_the_store_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvStoreName = (TextView)view.findViewById(R.id.tv_store_name);
			viewHolder.tvStoreAddress = (TextView)view.findViewById(R.id.tv_store_address);
			viewHolder.img = (ImageView)view.findViewById(R.id.img_store);
			viewHolder.rlOne = (RelativeLayout)view.findViewById(R.id.rl_store_one);
			viewHolder.rlTwo = (RelativeLayout)view.findViewById(R.id.rl_store_two);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			viewHolder.tvStoreName.setText(storeList.get(arg0).name);
			viewHolder.tvStoreAddress.setText(storeList.get(arg0).address);
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ storeList.get(arg0).pictureUrl, viewHolder.img);
			final Store store = storeList.get(arg0);
			viewHolder.rlOne.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ContactTheStore.this,MapFixation.class);
					startActivity(intent);
				}
			});
			viewHolder.rlTwo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(ContactTheStore.this,ChoiceService.class);
					intent.putExtra("storeId", store.id);
					startActivity(intent);
				}
			});
			return view;
		}
		
		class ViewHolder{
			TextView tvStoreName,tvStoreAddress;
			ImageView img;
			RelativeLayout rlOne,rlTwo;
		}
	}
	
	class CContactTheStoreTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			lv.setAdapter(new StoreAdapter());
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			String url = "/store/list.do";
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET,null);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
				String data = rjs.getData();
				Log.i("js", data);
				storeList = JsonUtil.toObjectList(data, Store.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
