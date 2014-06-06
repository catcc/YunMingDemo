package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Product;
import com.imcore.yunmingdemo.data.ProductImages;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class Productdetail extends Activity {
	private long id;
	private Product product;
	private List<ProductImages> pImageList;
	private Gallery glProtuctImage;
	private List<String> items;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productdetail);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("ProductId");
		id = bundle.getLong("productId");
		new ProductDetailTask().execute();
		glProtuctImage = (Gallery)findViewById(R.id.gl_productImage);
		
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

	class ProductDetailTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			glProtuctImage.setAdapter(new GalleryAdapter());
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/get.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("jj", data);
				product = JsonUtil.toObject(data, Product.class);
				pImageList = JsonUtil.toObjectList(product.productImages, ProductImages.class);
				items = JsonUtil.toJsonStrList(product.items);
					Log.i("ee", product.sku + product.altName);
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
