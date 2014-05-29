package com.imcore.yunmingdemo.ui;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.application.MyApplicition;
import com.imcore.yunmingdemo.image.ImageFetcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeImageFragment extends Fragment {
	private ImageView mView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.activity_home_image_fragment, null);
		mView = (ImageView) view.findViewById(R.id.img_Home_image);

		Bundle bd = getArguments();
		
		int position = bd.getInt("position");
		setImage(position);
		return view;
	}

	/**
	 * 图片装入tpList集合
	 * @param position
	 */
	private void setImage(int position) {
		new ImageFetcher().fetch( MyApplicition.tpList.get(position), mView);
	}
}
