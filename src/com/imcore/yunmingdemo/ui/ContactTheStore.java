package com.imcore.yunmingdemo.ui;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Store;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class ContactTheStore extends Fragment {
	private List<Store> storeList;
	private ListView lv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_contact_the_store, null);
		lv = (ListView)view.findViewById(R.id.lv_store);
		new CContactTheStoreTask().execute();
		return view;
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

		@SuppressWarnings("unused")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			ViewHolder viewHolder = null;
			if(viewHolder == null){
			view = getActivity().getLayoutInflater().inflate(R.layout.commodity_item_fragment, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_commodity_name);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_commodity_price);
			viewHolder.tvsaleTotal = (TextView)view.findViewById(R.id.tv_commodity_saleTotal);
			viewHolder.tvfavotieTotal = (TextView)view.findViewById(R.id.tv_commodity_favotieTotal);
			viewHolder.img = (ImageView)view.findViewById(R.id.img_commodity);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ storeList.get(arg0).pictureUrl, viewHolder.img);
			
			return view;
		}
		
		class ViewHolder{
			TextView tvName,tvPrice,tvsaleTotal,tvfavotieTotal;
			ImageView img;
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
