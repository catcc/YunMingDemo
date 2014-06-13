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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.ShoppingAll;
import com.imcore.yunmingdemo.data.SingleShopp;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class ConfirmOrder extends Activity implements OnClickListener{
	
	private long employeeId;
	private String employeeName;
	private TextView tvServiceName,tvOriginalCast,tvMemberPrice;
	private ListView lv;
	private ShoppingAll shoppingAll;
	private List<SingleShopp>singleList;
	private Button btnSubmit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_order);

		lv = (ListView)findViewById(R.id.lv_co);
		tvServiceName = (TextView)findViewById(R.id.tv_co_service_name);
		btnSubmit = (Button)findViewById(R.id.btn_co_submit);
		tvOriginalCast = (TextView)findViewById(R.id.tv_co_original_cost);
		tvMemberPrice = (TextView)findViewById(R.id.tv_co_original_cost);
		
		Intent intent = getIntent();
		employeeId= intent.getLongExtra("employeeId", 0);
		employeeName = intent.getStringExtra("employeeName");
		tvServiceName.setText(employeeName);
		
		new OrderFormTask().execute();
		btnSubmit.setOnClickListener(this);
	}
	
	
	class ConfirmOrdertAdapter extends BaseAdapter{

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
			view = getLayoutInflater().inflate(R.layout.confirm_order_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_ConfirmOrder);
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_ConfirmOrder_name);
			viewHolder.tvPack = (TextView)view.findViewById(R.id.tv_ConfirmOrder_pack);
			viewHolder.tvWeight = (TextView)view.findViewById(R.id.tv_ConfirmOrder_weight);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_ConfirmOrder_price);
			viewHolder.tvNumber = (TextView)view.findViewById(R.id.tv_ConfirmOrder_number);
			
			
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ singleList.get(arg0).imageUrl, viewHolder.img);
			viewHolder.tvName.setText(singleList.get(arg0).productName);
			viewHolder.tvPack.setText(singleList.get(arg0).packing);
			viewHolder.tvPack.setText(singleList.get(arg0).weight+"g");
			viewHolder.tvPrice.setText("￥"+singleList.get(arg0).retailPrice);
			viewHolder.tvNumber.setText("共" + singleList.get(arg0).quantity +"件");
			
			
			return view;
		}
		
		class ProductViewHolder{
			ImageView img;
			TextView tvName,tvPack,tvWeight,tvPrice,tvNumber;
		}
	}
	
	
	//主订单详情
	class OrderFormTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		@Override
		protected void onPostExecute(Void result) {
			lv.setAdapter(new ConfirmOrdertAdapter());
			tvOriginalCast.setText("￥"+shoppingAll.totalAmount);
			tvMemberPrice.setText("￥"+shoppingAll.discount);
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/orderitem/list.do";
			SharedPreferences sp = ConfirmOrder.this.getSharedPreferences(USER_INFO,
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
	
	//提交主订单
		class PutOrderFormTask extends AsyncTask<Void, Void, Void>{
			private static final String USER_INFO = "user_info";
			private static final String USER_ID = "user_id";
			private static final String TOKEN = "token";
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				String url = "/order/commit.do";
				SharedPreferences sp = ConfirmOrder.this.getSharedPreferences(USER_INFO,
						Context.MODE_PRIVATE);
				long userId = sp.getLong(USER_ID, 0);
				String token = sp.getString(TOKEN, "");
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("id", shoppingAll.id);
				map.put("userId", userId);
				map.put("token", token);
				map.put("employeeId", employeeId);
				map.put("customerId", userId);
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
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_co_submit:
			new PutOrderFormTask().execute();
			Toast.makeText(this, "订单提交成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,HomeActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}
}
