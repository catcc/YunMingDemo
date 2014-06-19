package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.application.MyApplicition;
import com.imcore.yunmingdemo.data.ImageTop;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.DisplayUtil;
import com.imcore.yunmingdemo.util.JsonUtil;


public class HomeTab extends Fragment implements OnClickListener{
	private ViewPager vp;
	public  List<ImageTop> topList;
	private ArrayList<ImageView> imgList;
	private ArrayList<ImageView> dots;
	private List<String> imgUrlList;
	private View rootView;
	private int currentItem = 0;
	private ImageView HomeNewArrival,HomeTeaInfo,HomeTopSellers,HomeContactStore;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.activity_home_tab, null);
		imgList = new ArrayList<ImageView>();
		imgUrlList = new ArrayList<String>();
		vp = (ViewPager)rootView.findViewById(R.id.view_pager);
		HomeNewArrival = (ImageView)rootView.findViewById(R.id.home_new_arrival);
		HomeTeaInfo = (ImageView)rootView.findViewById(R.id.home_tea_info);
		HomeTopSellers = (ImageView)rootView.findViewById(R.id.home_top_sellers);
		HomeContactStore = (ImageView)rootView.findViewById(R.id.home_contact_store);
		
		HomeNewArrival.setOnClickListener(this);
		HomeTeaInfo.setOnClickListener(this);
		HomeTopSellers.setOnClickListener(this);
		HomeContactStore.setOnClickListener(this);
		
		new ImageTask().execute();
		
		
		return rootView;
	}
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 33) {
				ImageFetcher imgFetcher = new ImageFetcher();
				if(topList != null) {
					
					for(ImageTop img:topList) {
						imgUrlList.add(img.imageUrl);
					}
					for(String str: imgUrlList) {
						ImageView img = new ImageView(getActivity());
						img.setLayoutParams(new LayoutParams(DisplayUtil.getScreenWidth(getActivity()), 200));
						img.setScaleType(ImageView.ScaleType.CENTER_CROP);
						imgFetcher.fetch(HttpHelper.ImageURL+"/"+str, img);
						imgList.add(img);
					}
					// 将五个点放入到集合中
					dots = new ArrayList<ImageView>();
					dots.add((ImageView) rootView.findViewById(R.id.iv_one));
					dots.add((ImageView) rootView.findViewById(R.id.iv_two));
					dots.add((ImageView) rootView.findViewById(R.id.iv_three));
					dots.add((ImageView) rootView.findViewById(R.id.iv_four));
					dots.add((ImageView) rootView.findViewById(R.id.iv_five));
					// 设置默认的显示内容
					
					// viewPager中的图片也是需要经过适配进去的
					HomeTabAdapter adapter = new HomeTabAdapter();
					vp.setAdapter(adapter);
					vp.setOnPageChangeListener(onPageChangeListener);
				}
			}
			if(msg.what == 35) {
				currentItem = (currentItem+1) % topList.size();
				vp.setCurrentItem(currentItem);

			}
		};
	};
	
	
	//图片切换监听
		private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
			
			//记录原先点的位置
			int oldposition = 0;
			
			//1-->2    1页面出去的时候启动的方法
			@Override
			public void onPageSelected(int position) {
				dots.get(position).setBackgroundResource(R.drawable.circle_gray);
				dots.get(oldposition).setBackgroundResource(R.drawable.circle_white); //原先点的改成失去焦点
				oldposition = position;
				currentItem = position;
				
			}
			
			//1 -->2  2页面进来后启动的方法
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			//页面滑动时调用的方法
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		};
	
	private class HomeTabAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return topList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		container.removeView(imgList.get(position));
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(imgList.get(position));
		return imgList.get(position);
	}

		
		
	}
	//解析JASON
	class ImageTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			String url = "/topline/list.do";
			RequestEntity entity = new RequestEntity(url, 0, null);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
				String data = rjs.getData();
				Log.i("data", data);
				Class<ImageTop> clss = ImageTop.class;
				topList = JsonUtil.toObjectList(data, clss);
				
				for (int i = 0; i < topList.size(); i++) {
					Log.i("dt", topList.get(i).getImageUrl());
					String imgUrl = topList.get(i).getImageUrl();
					MyApplicition.tpList.add(HttpHelper.ImageURL+"/"+imgUrl);
					
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Message msg = Message.obtain();
			msg.what = 33;
			handler.sendMessage(msg);

			super.onPostExecute(result);
		  }
	   
	   }
	
	@Override
	public void onStart() {
		super.onStart();
		//指定两秒钟切换一张图片
		timer.schedule(tt, 1000,1000);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		timer.cancel();
	}
	
	private Timer timer = new Timer();
	private TimerTask tt = new TimerTask() {
		
		@Override
		public void run() {
			Message msg = Message.obtain();
			msg.what = 35;
			handler.sendMessage(msg);
		}
	};
	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent();
		switch (arg0.getId()) {
			case R.id.home_new_arrival:
				
				intent.setClass(getActivity(),NewProduct.class);
				startActivity(intent);
				
				break;
				
			case R.id.home_tea_info:
				intent.setClass(getActivity(),TeaInfo.class);
				startActivity(intent);
				break;
			case R.id.home_top_sellers:
				
				intent.setClass(getActivity(), Sellers.class);
				startActivity(intent);
	
				break;
			case R.id.home_contact_store:
				
				intent.setClass(getActivity(),ContactTheStore.class);
				startActivity(intent);
				
				break;

		default:
			break;
		}
		
	}
	
	
	
}	
	
		
	

