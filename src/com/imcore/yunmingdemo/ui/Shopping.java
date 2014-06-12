package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.imcore.yunmingdemo.data.ShoppingAll;
import com.imcore.yunmingdemo.data.SingleShopp;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class Shopping extends Activity implements OnClickListener{
	private ShoppingAll shoppingAll;
	private List<SingleShopp>singleList;
	private ListView lvShoppingStart,lvShoppingEnd;
	private long productId;
	private String sku;
	private TextView tvOriginalCost,tvMemberPrice,tvShop,tvShopEditor,tvComplete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Shopping");
		productId = bundle.getLong("Shopping_productId");
		sku = bundle.getString("Shopping_sku");
		
		lvShoppingStart = (ListView)findViewById(R.id.lv_shopping_start);
		lvShoppingEnd = (ListView)findViewById(R.id.lv_shopping_end);
		
		tvOriginalCost = (TextView)findViewById(R.id.tv_shopping_original_cost);
		tvMemberPrice = (TextView)findViewById(R.id.tv_shopping_member_price);
		tvShop = (TextView)findViewById(R.id.tv_shop);
		tvShopEditor = (TextView)findViewById(R.id.tv_shop_editor);
		tvComplete = (TextView)findViewById(R.id.tv_shop_complete);
		
		tvShopEditor.setOnClickListener(this);
		tvComplete.setOnClickListener(this);
		
		
		
		new ShoppingPostTask(productId,1,sku).execute();
		
	}

	class ShoppingLvStartAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return singleList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return singleList.get(arg0);
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
			view = getLayoutInflater().inflate(R.layout.shopping_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_shopping);
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_shopping_name);
			viewHolder.tvPack = (TextView)view.findViewById(R.id.tv_shopping_pack);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_shopping_price);
			viewHolder.tvNumber = (TextView)view.findViewById(R.id.tv_shopping_number);
			
			
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ singleList.get(arg0).imageUrl, viewHolder.img);
			viewHolder.tvName.setText(singleList.get(arg0).productName);
			viewHolder.tvPack.setText(singleList.get(arg0).packing);
			viewHolder.tvPrice.setText("￥"+singleList.get(arg0).retailPrice);
			viewHolder.tvNumber.setText("共" + singleList.get(arg0).quantity +"件");
			
			
			return view;
		}
		
		class ProductViewHolder{
			ImageView img;
			Button btn;
			TextView tvName,tvPack,tvPrice,tvNumber;
		}
	}
	
	class ShoppingLvEndAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return singleList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return singleList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			final ProductViewHolder viewHolder;
			if(view == null){
			view = getLayoutInflater().inflate(R.layout.shopping_complete, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_shopping_complete);
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_shopping_complete_name);
			viewHolder.tvPack = (TextView)view.findViewById(R.id.tv_shopping_complete_pack);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_shopping_complete_price);
			viewHolder.tvNumber = (TextView)view.findViewById(R.id.tv_shopping_complete_number);
			viewHolder.btnAdd = (Button)view.findViewById(R.id.btn_add);
			viewHolder.btnReduce = (Button)view.findViewById(R.id.btn_reduce);
			viewHolder.imgDelete = (ImageView)view.findViewById(R.id.img_delete);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ singleList.get(arg0).imageUrl, viewHolder.img);
			viewHolder.tvName.setText(singleList.get(arg0).productName);
			viewHolder.tvPack.setText(singleList.get(arg0).packing);
			viewHolder.tvPrice.setText("￥"+singleList.get(arg0).retailPrice);
			viewHolder.tvNumber.setText("共" + singleList.get(arg0).quantity +"件");
			final SingleShopp ss = singleList.get(arg0);
			viewHolder.btnAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
//					long quantity = singleList.get(a).quantity+1;
					new ShoppingPostTask(ss.productId, 1, ss.sku).execute();
					viewHolder.tvNumber.setText("共" + ss.quantity +"件");
				}
			});
			viewHolder.btnReduce.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new ShoppingPostTask(ss.productId, -1, ss.sku).execute();
					viewHolder.tvNumber.setText("共" + ss.quantity +"件");
				}
			});
			viewHolder.imgDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					new ShoppingReduceGetTask((int)ss.id, (int)ss.orderId).execute();
				}
			});
			return view;
		}
		
		class ProductViewHolder{
			ImageView img,imgDelete;
			TextView tvName,tvPack,tvPrice,tvNumber;
			Button btnAdd,btnReduce;
		}

		
	}
	
	//解析购物车中所有的商品
	class ShoppingGetTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		@Override
		protected void onPostExecute(Void result) {
			lvShoppingStart.setAdapter(new ShoppingLvStartAdapter());
			lvShoppingEnd.setAdapter(new ShoppingLvEndAdapter());
			tvOriginalCost.setText("原价"+shoppingAll.totalAmount);
			tvMemberPrice.setText("会员价"+shoppingAll.discount);
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/orderitem/list.do";
			SharedPreferences sp = Shopping.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			String token = sp.getString(TOKEN, "");
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("token", token);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				shoppingAll = JsonUtil.toObject(data, ShoppingAll.class);
				singleList = JsonUtil.toObjectList(shoppingAll.orderItems, SingleShopp.class);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		

		
	}
	//添加产品到购物车
	class ShoppingPostTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		private long productId;
		private long quantity;
		private String sku;
		public ShoppingPostTask(long productId,long quantity,String sku){
			this.productId = productId;
			this.quantity = quantity;
			this.sku = sku;
		}
		@Override
		protected void onPostExecute(Void result) {
			new ShoppingGetTask().execute();
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/orderitem/update.do";
			SharedPreferences sp = Shopping.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			String token = sp.getString(TOKEN, ""); 
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("token", token);
			map.put("productId", productId);
			map.put("quantity", quantity);
			map.put("sku",sku );
			map.put("sub_total", 1);
			RequestEntity entity = new RequestEntity(url, HttpMethod.POST, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		
	}
	
	class ShoppingReduceGetTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		private int orderId;
		private int id;
		public ShoppingReduceGetTask(int id,int orderId){
			this.id = id;
			this.orderId = orderId;
			
		}
		@Override
		protected void onPostExecute(Void result) {
			new ShoppingGetTask().execute();
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/orderitem/delete.do";
			SharedPreferences sp = Shopping.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			String token = sp.getString(TOKEN, ""); 
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("orderId", orderId);
			map.put("id", id);
			map.put("userId", userId);
			map.put("token", token);
			
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
	
}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_shop_editor:
			lvShoppingEnd.setVisibility(View.VISIBLE);
			lvShoppingStart.setVisibility(View.GONE);
			tvShopEditor.setVisibility(View.GONE);
			tvComplete.setVisibility(View.VISIBLE);
			tvShop.setVisibility(View.GONE);
			
			break;
		case R.id.tv_shop_complete:
			lvShoppingStart.setVisibility(View.VISIBLE);
			lvShoppingEnd.setVisibility(View.GONE);
			tvShopEditor.setVisibility(View.VISIBLE);
			tvComplete.setVisibility(View.GONE);
			tvShop.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
	}
	
	
}
