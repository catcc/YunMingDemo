package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.ProductInfoDetail;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.util.JsonUtil;

public class ProductInfo extends Activity {
	private int id;
	private List<ProductInfoDetail> proList;
	private TextView tvProductNameInfo,tvOrigindetail,tvStoragedetail,
					 tvShelfLifedetail,tvMakingProcessdetail,tvTextturedetail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_info);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("productInfo");
		id = bundle.getInt("productId");
		String name = bundle.getString("productInfoName");
		tvProductNameInfo = (TextView)findViewById(R.id.tv_productNameInfo);
		tvOrigindetail = (TextView)findViewById(R.id.tv_origindetail);
		tvStoragedetail = (TextView)findViewById(R.id.tv_storagedetail);
		tvShelfLifedetail = (TextView)findViewById(R.id.tv_ShelfLifedetail);
		tvMakingProcessdetail = (TextView)findViewById(R.id.tv_makingProcessdetail);
		tvTextturedetail = (TextView)findViewById(R.id.tv_textturedetail);
		tvProductNameInfo.setText(name);
		
		
		new ProductInfoTask().execute();
		
		
	}

	class ProductInfoTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... arg0) {
			
			String url = "/product/attribute/get.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("productId", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("jj", data);
				proList = JsonUtil.toObjectList(data, ProductInfoDetail.class);
					for (int i = 0; i < proList.size(); i++) {
						Log.i("fdsaf",proList.get(i).attributeValue);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
			tvOrigindetail.setText(proList.get(0).attributeValue); 
			tvStoragedetail.setText(proList.get(1).attributeValue); 
			tvShelfLifedetail.setText(proList.get(2).attributeValue); 
			tvMakingProcessdetail.setText(proList.get(3).attributeValue);  
			tvTextturedetail.setText(proList.get(4).attributeValue); 
			super.onPostExecute(result);
		}
		
	}
}
