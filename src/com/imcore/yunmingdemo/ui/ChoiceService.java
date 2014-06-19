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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Employee;
import com.imcore.yunmingdemo.data.UserService;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.util.JsonUtil;

public class ChoiceService extends Activity implements android.view.View.OnClickListener{
	private List<UserService> eList;
	private ListView lv;
	private RelativeLayout rlChoiceService,rlEmployeeDetail;
	private Button btnNext;
	private TextView tvName;
	private long id ;
	private String employeeName = "";
	private long storeId;
	private List<Employee> emList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_service);
		tvName = (TextView)findViewById(R.id.tv_cs_employeeName);
		btnNext = (Button)findViewById(R.id.btn_cs_next);
		lv = (ListView)findViewById(R.id.lv_cs);
		
		rlChoiceService = (RelativeLayout)findViewById(R.id.rl_cs_four);
		rlEmployeeDetail= (RelativeLayout)findViewById(R.id.rl_cs);
		
		rlChoiceService.setOnClickListener(this);
		rlEmployeeDetail.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		Intent intent = getIntent();
		storeId = intent.getLongExtra("storeId", 0);
		if(storeId!=0){
			new StoreEmployeeTask().execute();
		}else{
			new SpecifyServiceTask().execute();
		}
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvName.setText(eList.get(arg2).userName);
				employeeName = eList.get(arg2).userName;
				if(storeId==0){
				id = eList.get(arg2).id;
				}else{
				id = emList.get(arg2).id;
				}
			}
		});
	}

	class LvCSAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return eList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return eList.get(arg0);
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
			view = getLayoutInflater().inflate(R.layout.choice_service_adapter, null);
			viewHolder = new ProductViewHolder();
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_csa_name);
			viewHolder.tvPhoneNumber = (TextView)view.findViewById(R.id.tv_csa_phonenumber);
			viewHolder.tvOnLine = (TextView)view.findViewById(R.id.tv_csa_onLine);
			
			
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			viewHolder.tvName.setText(eList.get(arg0).userName);
			viewHolder.tvPhoneNumber.setText("联系电话："+eList.get(arg0).phoneNumber);
			if(eList.get(arg0).online==true){
			viewHolder.tvOnLine.setText("客服在线");
			}else{
			viewHolder.tvOnLine.setText("客服不在线");
			}
			
			return view;
		}
		
		class ProductViewHolder{
			TextView tvName,tvPhoneNumber,tvOnLine;
		}
	}
	
	
	//解析客服列表
		class SpecifyServiceTask extends AsyncTask<Void, Void, Void>{
			private static final String USER_INFO = "user_info";
			private static final String USER_ID = "user_id";
			@Override
			protected void onPostExecute(Void result) {
				lv.setAdapter(new LvCSAdapter());
				tvName.setText(eList.get(0).userName);
				id = eList.get(0).id;
				super.onPostExecute(result);
			}
			@Override
			protected Void doInBackground(Void... arg0) {
				String url = "/user/employee/list.do";
				SharedPreferences sp = ChoiceService.this.getSharedPreferences(USER_INFO,
						Context.MODE_PRIVATE);
				long userId = sp.getLong(USER_ID, 0);
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("userId", userId);
				RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
				String js;
				try {
					js = HttpHelper.execute(entity);
					ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
					String data = rjs.getData();
					eList = JsonUtil.toObjectList(data, UserService.class);
					for(int a = 0;a<eList.size();a++){
						Log.i("employee", eList.get(a).id+"");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
			
		}

		//解析门店雇员
				class StoreEmployeeTask extends AsyncTask<Void, Void, Void>{
					@Override
					protected void onPostExecute(Void result) {
						lv.setAdapter(new LvCSAdapter());
						tvName.setText(eList.get(0).userName);
						id = emList.get(0).id;
						super.onPostExecute(result);
					}
					@Override
					protected Void doInBackground(Void... arg0) {
						String url = "/store/employee/list.do";
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("id", storeId);
						RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
						String js;
						try {
							js = HttpHelper.execute(entity);
							ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
							String data = rjs.getData();
							eList = JsonUtil.toObjectList(data, UserService.class);
							emList = JsonUtil.toObjectList(data,Employee.class);
							for(int a = 0;a<eList.size();a++){
								Log.i("employee", eList.get(a).id+"");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
						
					}
					
				}
	

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.rl_cs_four:
			if(lv.getVisibility()==View.GONE){
			lv.setVisibility(View.VISIBLE);
			}else{
				lv.setVisibility(View.GONE);
			}
			break;
		case R.id.rl_cs:
			Intent intent = new Intent(ChoiceService.this,EmployeeDetail.class);
			intent.putExtra("employeeId", id);
			startActivity(intent);
			break;
		case R.id.btn_cs_next:
			Intent it = new Intent(ChoiceService.this,ConfirmOrder.class);
			it.putExtra("employeeId", id);
			it.putExtra("employeeName", employeeName);
			startActivity(it);
			break;
		default:
			break;
		}
	}
}
