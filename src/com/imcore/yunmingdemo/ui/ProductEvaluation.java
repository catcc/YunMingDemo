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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Evaluation;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.util.JsonUtil;

public class ProductEvaluation extends Activity {
	private ListView lvEvaluation;
	private int id;
	private List<Evaluation> eList;
	private Button btnPublish;
	private EditText edCommen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_e);
		
		lvEvaluation = (ListView)findViewById(R.id.lv_Evaluation);
		btnPublish = (Button)findViewById(R.id.btn_publish);
		edCommen = (EditText)findViewById(R.id.et_commen);
				
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("productInfo");
		id = bundle.getInt("productId");
		new EvaluationGetTask().execute();
		
		btnPublish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(edCommen.getText() != null){
				new EvaluationPostTask().execute();
				}
				edCommen.setText("");
			}
		});
	}
	
	class EvaluationAdapter extends BaseAdapter{

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
			EvaluationView viewHolder = null;
			if(viewHolder == null){
			view = getLayoutInflater().inflate(R.layout.evaluation_item, null);
			viewHolder = new EvaluationView();
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_evalName);
			viewHolder.tvTime = (TextView)view.findViewById(R.id.tv_evalTime);
			viewHolder.tvContent = (TextView)view.findViewById(R.id.tv_Content);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (EvaluationView)view.getTag();
			}
			viewHolder.tvName.setText(eList.get(arg0).userName);
			viewHolder.tvTime.setText(eList.get(arg0).createdTime);
			viewHolder.tvContent.setText(eList.get(arg0).comments);
			return view;
		}
		class EvaluationView{
			TextView tvName,tvTime,tvContent;
		}
		
	}
	//执行GET请求
	class EvaluationGetTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/comments/list.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("jj", data);
				eList = JsonUtil.toObjectList(data, Evaluation.class);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			lvEvaluation.setAdapter(new EvaluationAdapter());
			super.onPostExecute(result);
		}
	}
	
	//执行POST请求
	class EvaluationPostTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/comments/add.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("comment", edCommen.getText()+"");
			RequestEntity entity = new RequestEntity(url, HttpMethod.POST, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				if(rjs.getStatus() == 200){
					Toast.makeText(ProductEvaluation.this, "评论成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ProductEvaluation.this, "评论发表失败", Toast.LENGTH_SHORT).show();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new EvaluationGetTask().execute();
			super.onPostExecute(result);
		}
	}
	

}
