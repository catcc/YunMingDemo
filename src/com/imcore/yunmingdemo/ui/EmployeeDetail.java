package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.R.layout;
import com.imcore.yunmingdemo.data.Employee;
import com.imcore.yunmingdemo.data.userPicture;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.ui.Shopping.ShoppingLvStartAdapter.ProductViewHolder;
import com.imcore.yunmingdemo.util.JsonUtil;

public class EmployeeDetail extends Activity {
		private long id;
		private Employee employee;
		private List<userPicture> imgList;
		private Gallery gl;
		private TextView tvUserName,tvName,tvStore,tvPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_detail);
		
		tvUserName = (TextView)findViewById(R.id.tv_em_name);
		tvName = (TextView)findViewById(R.id.tv_em_relname);
		tvStore = (TextView)findViewById(R.id.tv_em_store_name);
		tvPhone = (TextView)findViewById(R.id.tv_em_phoneNumber_);
		
		id = getIntent().getLongExtra("employeeId", 0);
		gl = (Gallery)findViewById(R.id.gl_employee);
		new EmployeeDetailTask().execute();
	}

	class EmployeeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return imgList.get(arg0);
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
			view = getLayoutInflater().inflate(R.layout.employee_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_picture);
			
			
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ imgList.get(arg0).pictureUrl, viewHolder.img);
			
			
			return view;
		}
		class ProductViewHolder{
			ImageView img;
		}
	}
	
	//解析客服详细信息
	class EmployeeDetailTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			gl.setAdapter(new EmployeeAdapter());
			tvUserName.setText(employee.userName);
			tvName.setText(employee.name);
			tvStore.setText(employee.store);
			tvPhone.setText(employee.phoneNumber);
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/store/employee/get.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				employee = JsonUtil.toObject(data, Employee.class);
				imgList = JsonUtil.toObjectList(employee.userPictures,userPicture.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
		
	}

}
