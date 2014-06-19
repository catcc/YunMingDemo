package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.R.id;
import com.imcore.yunmingdemo.R.layout;
import com.imcore.yunmingdemo.data.Evaluation;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.util.JsonUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewsEvaluation extends Activity {
	private ListView lvEvaluation;
	private long id;
	private List<Evaluation> eList;
	private Button btnPublish;
	private EditText edCommen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_evaluation);
		
		lvEvaluation = (ListView)findViewById(R.id.lv_news_Evaluation);
		btnPublish = (Button)findViewById(R.id.btn_news_publish);
		edCommen = (EditText)findViewById(R.id.et_news_commen);
				
		Intent intent = getIntent();
		id = intent.getLongExtra("newsId", 0);
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
	//列出新闻评论
	class EvaluationGetTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/news/comments/list.do";
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("newsId", id);
			
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
	
	//评论新闻
	class EvaluationPostTask extends AsyncTask<Void, Void, Void>{
		private static final String USER_INFO = "user_info";
		private static final String USER_ID = "user_id";
		private static final String TOKEN = "token";
		private SharedPreferences sp = NewsEvaluation.this.getSharedPreferences(USER_INFO,
				Context.MODE_PRIVATE);
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/news/comments/add.do";
			long userId= sp.getLong(USER_ID, 0);
			String token = sp.getString(TOKEN, "");
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("newsId", id);
			map.put("comments", edCommen.getText()+"");
			map.put("userId", userId);
			map.put("token", token);
			RequestEntity entity = new RequestEntity(url, HttpMethod.POST, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				
				if(rjs.getStatus() == 200){
					String data = rjs.getData();
					Toast.makeText(NewsEvaluation.this, "评论成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(NewsEvaluation.this, "评论发表失败", Toast.LENGTH_SHORT).show();
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
