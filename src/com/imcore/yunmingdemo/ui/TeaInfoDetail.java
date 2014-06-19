package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Collection;
import com.imcore.yunmingdemo.data.NewsDetail;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class TeaInfoDetail extends Activity {
	private long id;
	private Collection collection;
	private List<NewsDetail> list;
	private TextView tvTitle,tvContent,tvTime;
	private ImageView img;
	private RelativeLayout rl;
	private ProgressDialog pg ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tea_info_detail);
		Intent intent = getIntent();
		id = intent.getLongExtra("infoId", 0);
		
		tvTitle = (TextView)findViewById(R.id.tv_tea_info_detail_theme_name);
		tvContent = (TextView)findViewById(R.id.tv_tea_info_detail_content);
		tvTime = (TextView)findViewById(R.id.tv_tea_info_detail_time);
		
		img = (ImageView)findViewById(R.id.img_tea_info_detail);
		
		rl = (RelativeLayout)findViewById(R.id.rl_tea_info_detail_evaluation);
		
		new TeaInfoDetailTask().execute();
		
	}

	class TeaInfoDetailTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			new ImageFetcher().fetch(collection.pictureUrl, img);
			tvTitle.setText(collection.title);
			tvContent.setText(list.get(0).content);
			tvTime.setText(list.get(0).createdTime);
			pg.dismiss();
			rl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(TeaInfoDetail.this,NewsEvaluation.class);
					intent.putExtra("newsId", collection.id);
					startActivity(intent);
					
				}
			});
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			String url = "/news/get.do";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
					String data = rjs.getData();
					collection = JsonUtil.toObject(data, Collection.class);
					list = JsonUtil.toObjectList(collection.newsDetails,NewsDetail.class);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPreExecute() {
			pg = ProgressDialog.show(TeaInfoDetail.this, "芸茗茶叶", "正在加载");
			
			super.onPreExecute();
		}
		
	}
}
