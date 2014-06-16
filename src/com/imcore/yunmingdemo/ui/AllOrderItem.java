package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.OrderItem;
import com.imcore.yunmingdemo.data.SingleShopp;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class AllOrderItem extends Activity {
	private List<OrderItem> AllOrderItemList;
	private List<SingleShopp> singItemList;
	private List<List<SingleShopp>> AllsingItemList;
	private ExpandableListView ep;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_order_item);
		ep = (ExpandableListView)findViewById(R.id.el_Order_Record);
		new AllOrderItemTask().execute();	
		
	}

	class AllOrderItemTask extends AsyncTask<Void, Void, Void>{

		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		@Override
		protected void onPostExecute(Void result) {
			ep.setAdapter(new AllOredeItemAdapter());
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/order/list.do";
			SharedPreferences sp = AllOrderItem.this.getSharedPreferences(USER_INFO,
					Context.MODE_PRIVATE);
			long userId = sp.getLong(USER_ID, 0);
			String token = sp.getString(TOKEN, "");
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("token", token);
			map.put("type", 1);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
				String data = rjs.getData();
				AllOrderItemList = JsonUtil.toObjectList(data,OrderItem.class);
				AllsingItemList = new ArrayList<List<SingleShopp>>();
				for(int i =0;i<AllOrderItemList.size();i++){
					singItemList = JsonUtil.toObjectList(AllOrderItemList.get(i).orderItems, SingleShopp.class);
					AllsingItemList.add(singItemList);
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		

		private class AllOredeItemAdapter extends BaseExpandableListAdapter{

			@Override
			public Object getChild(int arg0, int arg1) {
				return AllsingItemList.get(arg0).get(arg1);
			}

			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				View view = convertView;
				ChildViewHolder cvh = null;
				if(view == null){
					view = getLayoutInflater().inflate(R.layout.all_order_item_children, null);
					cvh = new ChildViewHolder();
					cvh.imgChild = (ImageView)view.findViewById(R.id.img_item);
					cvh.tvItemName = (TextView)view.findViewById(R.id.tv_item_name);
					cvh.tvItemPrice = (TextView)view.findViewById(R.id.tv_item_price);
					cvh.tvItemPack = (TextView)view.findViewById(R.id.tv_item_pack);
					cvh.tvItemNumber = (TextView)view.findViewById(R.id.tv_item_number);
					view.setTag(cvh);
				}else{
					cvh = (ChildViewHolder)view.getTag();
				}
				String SName = AllsingItemList.get(groupPosition).get(childPosition).productName;
				new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ AllsingItemList.get(groupPosition).get(childPosition).imageUrl, cvh.imgChild);
				cvh.tvItemName.setText(SName);
				cvh.tvItemPack.setText(AllsingItemList.get(groupPosition).get(childPosition).packing);
				cvh.tvItemPrice.setText("￥" + AllsingItemList.get(groupPosition).get(childPosition).regularPrice);
				cvh.tvItemNumber.setText("共" + AllsingItemList.get(groupPosition).get(childPosition).quantity+ "件");
				return view;
			}
			class ChildViewHolder{
				ImageView imgChild;
				TextView tvItemName,tvItemPrice,tvItemPack,tvItemNumber;
			}

			@Override
			public int getChildrenCount(int arg0) {
				// TODO Auto-generated method stub
				return AllsingItemList.get(arg0).size();
			}

			@Override
			public Object getGroup(int arg0) {
				return AllOrderItemList.get(arg0);
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return AllOrderItemList.size();
			}

			@Override
			public long getGroupId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				View view = convertView; 
				GroupViewHolder gvh = null;
				if(view == null){
				view = getLayoutInflater().inflate(R.layout.all_order_item_big, null);
				gvh = new GroupViewHolder();
				gvh.tvNumber = (TextView)view.findViewById(R.id.tv_what);
				gvh.tvPay = (TextView)view.findViewById(R.id.tv_pay);
				gvh.tvTime = (TextView)view.findViewById(R.id.tv_time);
				gvh.tvHead = (TextView)view.findViewById(R.id.tv_head);
				
				view.setTag(gvh);
				}else{
					gvh =(GroupViewHolder)view.getTag();
				}
				gvh.tvNumber.setText("第" + (groupPosition+1) + "单");
				gvh.tvPay.setText("共消费:"+"￥"+AllOrderItemList.get(groupPosition).totalAmount);
				gvh.tvTime.setText(AllOrderItemList.get(groupPosition).orderDate);
				gvh.tvHead.setText("负责人：" + AllOrderItemList.get(groupPosition).userName);
				AllOrderItemList.get(groupPosition);
				return view;
			}
			class GroupViewHolder{
				TextView tvNumber,tvPay,tvTime,tvHead;
			}
			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return true;
			}
			
		}
		
	}
}
