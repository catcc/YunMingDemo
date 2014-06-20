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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Product;
import com.imcore.yunmingdemo.data.ProductImages;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class Productdetail extends Activity implements OnClickListener{
	private long id;
	private Product product;
	private List<ProductImages> pImageList;
	private Gallery glProtuctImage;
	private List<String> items;
	private TextView tvProductName,tvProductPrice,tvProductDesc,tvProductPacking,
			tvProductItems;
	private RelativeLayout rlPack,rlDetail,rlEvaluation;
	private Button btnChoicePacking;
	private ImageView shareImg;
	private TextView tvBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productdetail);
		
		ShareSDK.initSDK(this,"211e5ff1fc00");
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("ProductId");
		id = bundle.getLong("productId");
		
		tvProductName = (TextView)findViewById(R.id.tv_productName);
		tvProductPrice = (TextView)findViewById(R.id.tv_productPrice);
		tvProductDesc = (TextView)findViewById(R.id.tv_productDesc);
		tvProductPacking = (TextView)findViewById(R.id.tv_productPacking);
		tvProductItems = (TextView)findViewById(R.id.tv_productItems);
		
		rlPack = (RelativeLayout)findViewById(R.id.rl_pro_four);
		rlDetail = (RelativeLayout)findViewById(R.id.rl_pro_eight);
		rlEvaluation = (RelativeLayout)findViewById(R.id.rl_pro_six);
		
		shareImg = (ImageView)findViewById(R.id.img_recommend);
		
		btnChoicePacking = (Button)findViewById(R.id.btn_choice_packing);
		tvBack = (TextView)findViewById(R.id.tv_back);
		tvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		
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
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ pImageList.get(arg0).imageUrl, viewHolder.img);
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
			tvProductName.setText(product.altName);
			tvProductPrice.setText("￥"+product.price+"/"+product.weight+"g");
			tvProductDesc.setText(product.shortDesc);
			tvProductItems.setText("共"+pImageList.size()+"款");
			tvProductPacking.setText(product.packing);
			
			rlPack.setOnClickListener(Productdetail.this);
			rlDetail.setOnClickListener(Productdetail.this);
			rlEvaluation.setOnClickListener(Productdetail.this);
			btnChoicePacking.setOnClickListener(Productdetail.this);
			
			shareImg.setOnClickListener(Productdetail.this);
			
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
						Log.i("ee", pImageList.get(i).imageUrl + pImageList.get(i).sku);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		

		
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		switch (arg0.getId()){
			case R.id.rl_pro_four:
				intent.setClass(Productdetail.this,ProductDetailImages.class);
				bundle.putString("sku", product.sku);
				intent.putExtra("intsku", bundle);
				startActivity(intent);
				break;
				
			case R.id.rl_pro_eight:
				intent.setClass(Productdetail.this, ProductInfo.class);
				bundle.putInt("productId", product.id);
				bundle.putString("productInfoName", product.altName);
				intent.putExtra("productInfo", bundle);
				startActivity(intent);
				break;	
			case R.id.rl_pro_six:
				intent.setClass(Productdetail.this, ProductEvaluation.class);
				bundle.putInt("productId", product.id);
				intent.putExtra("productInfo", bundle);
				startActivity(intent);
				break;	
				
			case R.id.btn_choice_packing:
				intent.setClass(Productdetail.this, PackingChoice.class);
				bundle.putInt("productId", product.id);
				intent.putExtra("productInfo", bundle);
				startActivity(intent);
				break;
				
			case R.id.img_recommend:
				OnekeyShare oks = new OnekeyShare();
				// 分享时Notification的图标和文字
				oks.setNotification(R.drawable.ic_launcher,
						Productdetail.this.getString(R.string.app_name));
				// text是分享文本，所有平台都需要这个字段
				oks.setText("分享内容");
				oks.setImageUrl("http://yunming-api.suryani.cn" +"/"+product.imageUrl);
				oks.show(arg0.getContext());

				break;	
		}
		
	}
	@Override
	protected void onStop() {
		ShareSDK.stopSDK(this);
		super.onStop();
	}
}
