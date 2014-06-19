package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.SellerBest;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Sellers extends Activity {
	private List<SellerBest> npList;
	private ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sellers);
		lv = (ListView)findViewById(R.id.lv_sellers);
		new SellersTask().execute();
	}

	private class SellersAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return npList.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return npList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			ViewHolder viewHolder = null;
			if(viewHolder == null){
			view = getLayoutInflater().inflate(R.layout.commodity_item_fragment, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_commodity_name);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_commodity_price);
			viewHolder.img = (ImageView)view.findViewById(R.id.img_commodity);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			String Name = npList.get(arg0).productName;
			long price = npList.get(arg0).price;
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ npList.get(arg0).imageUrl, viewHolder.img);
			viewHolder.tvName.setText(Name);
			viewHolder.tvPrice.setText("￥" + price);
			
			return view;
		}
		
		class ViewHolder{
			TextView tvName,tvPrice;
			ImageView img;
		}
	}
	
	
	class SellersTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/bestseller.do";
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
					npList = JsonUtil.toObjectList(data, SellerBest.class);
				}
				Log.i("name", js);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			lv.setAdapter(new SellersAdapter());
			super.onPostExecute(result);
		}
		
	}


}
