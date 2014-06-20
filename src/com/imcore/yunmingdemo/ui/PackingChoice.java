package com.imcore.yunmingdemo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.ProductImages;
import com.imcore.yunmingdemo.data.ProductPackingChoice;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class PackingChoice extends Activity implements android.view.View.OnClickListener {
	private Gallery glPackingChoice,glPackingChoiceDetail;
	private int id;
	private int PackingDetail;
	private List<ProductPackingChoice> packingList;
	private List<ProductImages> ProImgList;
	private List<List<ProductImages>> allProImgList;
	private TextView tvWeight,tvPacking,tvPrice;
	private Button btnShopping;
	private String sku;
	private long productId;
	private ImageView shareImg;
	private TextView tvBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_packing_choice);
		glPackingChoice = (Gallery)findViewById(R.id.gl_packing_choice);
		glPackingChoiceDetail = (Gallery)findViewById(R.id.gl_packing_choice_detail);
		
		ShareSDK.initSDK(this,"211e5ff1fc00");
		
		tvWeight = (TextView)findViewById(R.id.tv_weight);
		tvPacking = (TextView)findViewById(R.id.tv_packs);
		tvPrice = (TextView)findViewById(R.id.tv_price);
		
		btnShopping = (Button)findViewById(R.id.btn_shopping);
		btnShopping.setOnClickListener(this);
		
		shareImg = (ImageView)findViewById(R.id.img_pack_recommend);
		
		tvBack = (TextView)findViewById(R.id.tv_back);
		tvBack.setOnClickListener(this);
		
		shareImg.setOnClickListener(this);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("productInfo");
		id = bundle.getInt("productId");
		new PackingChoiceTask().execute();
		
	}
	
	class GalleryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return packingList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return packingList.get(arg0);
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
			view = getLayoutInflater().inflate(R.layout.productdetail_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_product);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ packingList.get(arg0).imageUrl, viewHolder.img);
			
			
			
			return view;
			
		}
		
		class ProductViewHolder{
			ImageView img;
			TextView tvChoice;
		}
	}
	
	class GalleryDetailAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allProImgList.get(PackingDetail).size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return allProImgList.get(PackingDetail).get(arg0);
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
			view = getLayoutInflater().inflate(R.layout.productdetail_item, null);
			viewHolder = new ProductViewHolder();
			viewHolder.img = (ImageView)view.findViewById(R.id.img_product);
			view.setTag(viewHolder);
				
			}else{
				viewHolder = (ProductViewHolder)view.getTag();
			}
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ allProImgList.get(PackingDetail).get(arg0).imageUrl, viewHolder.img);
			return view;
		}
		
		class ProductViewHolder{
			ImageView img;
		}
	}
	
	
	
	class PackingChoiceTask extends AsyncTask<Void, Void, Void>{
		private TextView tvChoice;
		@Override
		protected void onPostExecute(Void result) {
			
			glPackingChoice.setAdapter(new GalleryAdapter());
			glPackingChoice.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					tvChoice = (TextView)arg1.findViewById(R.id.tv_has_choice);
					PackingDetail = arg2;
					
					tvChoice.setVisibility(View.VISIBLE);
					
					tvWeight.setText(packingList.get(arg2).weight+"g");	
					tvPacking.setText(packingList.get(arg2).packing);
					tvPrice.setText( "￥"+packingList.get(arg2).regularPrice );
					
					glPackingChoiceDetail.setAdapter(new GalleryDetailAdapter());
					
					sku = packingList.get(arg2).sku;
					productId = packingList.get(arg2).productId;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					tvChoice = (TextView)arg0.findViewById(R.id.tv_has_choice);
					if(tvChoice.getVisibility() == View.VISIBLE){
					tvChoice.setVisibility(View.GONE);
					}
				}
			});
			super.onPostExecute(result);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/product/item/list.do";
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("productId", id);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("se", data);
				packingList = JsonUtil.toObjectList(data, ProductPackingChoice.class);
				allProImgList = new ArrayList<List<ProductImages>>();
					for (int i = 0; i < packingList.size(); i++) {
						ProImgList = JsonUtil.toObjectList(packingList.get(i).productImages, ProductImages.class);
						allProImgList.add(ProImgList);
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
		case R.id.btn_shopping:
			new AlertDialog.Builder(PackingChoice.this).setTitle("提示")
			.setNegativeButton("再逛一逛", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			}).setPositiveButton("添加到购物车", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(PackingChoice.this,Shopping.class);
					Bundle bundle = new Bundle();
					bundle.putLong("Shopping_productId",productId);
					bundle.putString("Shopping_sku", sku);
					intent.putExtra("Shopping", bundle);
					startActivity(intent);
				}
			}).show();
			break;
			
		case R.id.img_recommend:
			OnekeyShare oks = new OnekeyShare();
			// 分享时Notification的图标和文字
			oks.setNotification(R.drawable.ic_launcher,
					PackingChoice.this.getString(R.string.app_name));
			// text是分享文本，所有平台都需要这个字段
			oks.setText("分享内容");
			oks.show(arg0.getContext());

			break;
		case R.id.tv_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onStop() {
		ShareSDK.stopSDK(this);
		super.onStop();
	}
}
