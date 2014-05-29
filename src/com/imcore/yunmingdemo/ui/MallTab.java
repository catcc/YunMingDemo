package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Category;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class MallTab extends Fragment {
	private List<Category> CaList;
	private List<String> CNList;
	private ExpandableListView melCategory;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.activity_mall_tab, null);
		melCategory = (ExpandableListView)view.findViewById(R.id.el_category);
		
		new CategoryTask().execute();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		melCategory.setAdapter(new MyExpAdapter());
		return view;
	}
	private class MyExpAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getGroup(int arg0) {
			return CNList.get(arg0);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return CNList.size();
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
			if(view != null){
			view =  getActivity().getLayoutInflater().inflate(
					R.layout.mall_tab_fragment, null);
			gvh = new GroupViewHolder();
			gvh.tvText = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(gvh);
			}
			return view;
		}
		
		class GroupViewHolder {
			TextView tvText;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	class CategoryTask extends AsyncTask<Void, Void, Void>{

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
					Log.i("ee", CaList.get(i).getImageUrl() + CaList.get(i).getCategoryName());
					String CName = CaList.get(i).getCategoryName();
					String CUrl = CaList.get(i).getImageUrl();
					CNList.add(CName);
					new CategoryImgTask().execute(HttpHelper.ImageURL+"/"+CUrl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		class CategoryImgTask extends AsyncTask<String, Void, Void> {
			private String imgUrl;

			@Override
			protected Void doInBackground(String... params) {
				imgUrl = params[0];
				boolean isSucc = ImageFetcher.downLoadImage(imgUrl);
				Log.i("img", isSucc + "");
				return null;
			}
			
		}
		
	}
}
