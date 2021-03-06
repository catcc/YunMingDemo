package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Category;
import com.imcore.yunmingdemo.data.Subclass;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class MallTab extends Fragment {
	private List<Category> CaList;
	private List<String> CNList;
	private ExpandableListView elMall;
	private List<Subclass> SList;
	private List<String> SNList;
	private List<List<Subclass>> AllSList;
	private List<List<String>> AllSNList;
	private ProgressDialog pg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_mall_tab, null);
		elMall = (ExpandableListView)view.findViewById(R.id.el_mall);
		
		new CategoryTask().execute();
		
		
		elMall.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
					int arg3, long arg4) {
				Intent intent = new Intent(getActivity(),CommodityItem.class);
				Bundle bundle = new Bundle();
				bundle.putLong("ComID", AllSList.get(arg2).get((int)arg4).id);
				bundle.putString("CommName", AllSList.get(arg2).get((int)arg4).categoryName);
				intent.putExtra("CommId", bundle);
				startActivity(intent);
				return true;
			}
		});
		return view;
	}
	
		
		
	
	private class ExpandAdapter extends BaseExpandableListAdapter{

		@Override
		public Object getChild(int arg0, int arg1) {
			return AllSNList.get(arg0).get(arg1);
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
				view = getActivity().getLayoutInflater().inflate(R.layout.mall_subclass, null);
				cvh = new ChildViewHolder();
				cvh.imgChild = (ImageView)view.findViewById(R.id.img_subclass);
				cvh.tvChild = (TextView)view.findViewById(R.id.tv_subclass);
				view.setTag(cvh);
			}else{
				cvh = (ChildViewHolder)view.getTag();
			}
			String SName = AllSList.get(groupPosition).get(childPosition).categoryName;
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ AllSList.get(groupPosition).get(childPosition).imageUrl, cvh.imgChild);
			cvh.tvChild.setText(SName);
			return view;
		}
		class ChildViewHolder{
			ImageView imgChild;
			TextView tvChild;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return AllSNList.get(arg0).size();
		}

		@Override
		public Object getGroup(int arg0) {
			return CNList.get(arg0);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return 4;
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
			view = getActivity().getLayoutInflater().inflate(R.layout.mall_catagory, null);
			gvh = new GroupViewHolder();
			gvh.imgCategory = (ImageView)view.findViewById(R.id.img_category);
			gvh.Tvcategory = (TextView)view.findViewById(R.id.tv_category);
			view.setTag(gvh);
			}else{
				gvh =(GroupViewHolder)view.getTag();
			}
			String gruopText = CNList.get(groupPosition);
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ CaList.get(groupPosition).imageUrl, gvh.imgCategory);
			gvh.Tvcategory.setText(gruopText);
			return view;
		}
		class GroupViewHolder{
			ImageView imgCategory;
			TextView Tvcategory;
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
	//解析大类
	class CategoryTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			pg = ProgressDialog.show(getActivity(), "芸茗茶叶", "正在加载");
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			String url = "/category/list.do";
			RequestEntity entity = new RequestEntity(url, 0, null);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("js", data);
				Class<Category> cass = Category.class;
				CaList = JsonUtil.toObjectList(data, cass);
				CNList = new ArrayList<String>();
				
				for (int i = 0; i < CaList.size(); i++) {
					Log.i("ee", CaList.get(i).imageUrl + CaList.get(i).categoryName);
					String CName = CaList.get(i).categoryName;
					CNList.add(CName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new SubclassTask().execute();
			super.onPostExecute(result);
		}
		
	}	

		
	//解析子类
		class SubclassTask extends AsyncTask<Void, Void, Void>{
				 
				@Override
				protected Void doInBackground(Void... params) {
					AllSList = new ArrayList<List<Subclass>>();
					AllSNList = new ArrayList<List<String>>();
					for(int i = 1;i<CaList.size();i++){
					String url = "/category/list.do?id="+i;
					RequestEntity entity = new RequestEntity(url, 0, null);
					String js;
					try {
						js = HttpHelper.execute(entity);
						ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
						String data = rjs.getData();
						Log.i("kk", data);
						Class<Subclass> cass = Subclass.class;
						SList = JsonUtil.toObjectList(data, cass);
						SNList = new ArrayList<String>();
						AllSList.add(SList);
						AllSNList.add(SNList);
						for (int j = 0; j < SList.size(); j++) {
							Log.i("r", SList.get(j).imageUrl + SList.get(j).categoryName);
							String SName = SList.get(j).categoryName;
							SNList.add(SName);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					
					elMall.setAdapter(new ExpandAdapter());
					pg.dismiss();
				super.onPostExecute(result);
				}

			}
		
	
}
