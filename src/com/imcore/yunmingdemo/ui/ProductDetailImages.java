package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.R.id;
import com.imcore.yunmingdemo.R.layout;
import com.imcore.yunmingdemo.data.Product;
import com.imcore.yunmingdemo.data.ProductImages;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ProductDetailImages extends Activity {
	private Gallery glProPick;
	private String sku;
	private List<ProductImages> pImageList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail_images);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("intsku");
		sku = bundle.getString("sku");
		glProPick = (Gallery)findViewById(R.id.gl_productImage_Packing);
		new ProductDetailImageTask().execute();
		
	}

	class GalleryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pImageList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return pImageList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			ProductViewHolder viewHolder = null;
			if(viewHolder == null){
			view = getLayoutInflater().inflate(R.layout.productdetail_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_product);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ pImageList.get(arg0).getImageUrl(), viewHolder.img);
			return view;
		}
		
		class ProductViewHolder{
			ImageView img;
		}
	}
	
	class ProductDetailImageTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			
			glProPick.setAdapter(new GalleryAdapter());
			
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/images/list.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("sku", sku);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("jj", data);
				pImageList = JsonUtil.toObjectList(data, ProductImages.class);
					for (int i = 0; i < pImageList.size(); i++) {
						Log.i("ee", pImageList.get(i).getImageUrl() + pImageList.get(i).sku);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		

		
	}
}
