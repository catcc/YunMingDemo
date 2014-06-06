package com.imcore.yunmingdemo.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.Commodity;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class CommodityItem extends Activity implements android.view.View.OnClickListener{
	private GridView gvCommodity;
	private ListView lvCommodity;
	private RadioButton rbtnList,rbtnGrid;
	private Button btnSort,btnFilter;
	private List<Commodity> myCommList;
	private ProgressDialog pg;
//	private Map<String, Object> map;
	private long id;
	private static int sortIndex=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commodity);
		
		gvCommodity = (GridView)findViewById(R.id.gv_Commodity);
		lvCommodity = (ListView)findViewById(R.id.lv_product_detail);
		btnSort = (Button)findViewById(R.id.btn_product_sort);
		btnFilter = (Button)findViewById(R.id.btn_product_filter);
		rbtnList = (RadioButton)findViewById(R.id.rb_list);
		rbtnGrid = (RadioButton)findViewById(R.id.rb_grid);
		
		init();
		
		btnSort.setOnClickListener(this);
		btnFilter.setOnClickListener(this);
		rbtnList.setOnClickListener(this);
		rbtnGrid.setOnClickListener(this);
	}
	
	private void init(){
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("CommId");
		id =  bundle.getLong("ComID");
		new CommodityTask("",0).execute();
	}

	private class CommodityAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if(id==5){
				return 7;
			}
			return myCommList.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return myCommList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@SuppressWarnings("unused")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			ViewHolder viewHolder = null;
			if(viewHolder == null){
			view = getLayoutInflater().inflate(R.layout.commodity_item_fragment, null);
			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView)view.findViewById(R.id.tv_commodity_name);
			viewHolder.tvPrice = (TextView)view.findViewById(R.id.tv_commodity_price);
			viewHolder.tvsaleTotal = (TextView)view.findViewById(R.id.tv_commodity_saleTotal);
			viewHolder.tvfavotieTotal = (TextView)view.findViewById(R.id.tv_commodity_favotieTotal);
			viewHolder.img = (ImageView)view.findViewById(R.id.img_commodity);
			view.setTag(viewHolder);
				if(rbtnList.isChecked()){
					
				}
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			String Name = myCommList.get(arg0).getProductName();
			long price = myCommList.get(arg0).getPrice();
			long saleTotal = myCommList.get(arg0).getSaleTotal();
			long favotieTotal = myCommList.get(arg0).getFavotieTotal();
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ myCommList.get(arg0).getImageUrl(), viewHolder.img);
			viewHolder.tvName.setText(Name);
			viewHolder.tvPrice.setText("￥" + price);
			viewHolder.tvsaleTotal.setText("销量："+saleTotal);
			viewHolder.tvfavotieTotal.setText("收藏："+favotieTotal);
			return view;
		}
		
		class ViewHolder{
			TextView tvName,tvPrice,tvsaleTotal,tvfavotieTotal;
			ImageView img;
		}
	}
	
	class CommodityTask extends AsyncTask<Void, Void, Void>{
		private String orderBy;
		private int desc;
		public CommodityTask(String orderBy,int desc){
			this.desc=desc;
			this.orderBy=orderBy;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			Map<String,Object> map = new HashMap<String, Object>();
			String url = "/category/products.do";
			map.put("id", id);
			map.put("orderBy", orderBy);
			map.put("desc", desc);
			RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				String data = rjs.getData();
				Log.i("js", data);
				Class<Commodity> cla = Commodity.class;
				
				myCommList = JsonUtil.toObjectList(data, cla);
				
				for (int i = 0; i < myCommList.size(); i++) {
					Log.i("ee", myCommList.get(i).getImageUrl() + myCommList.get(i).getProductName());
					String imgUrl = myCommList.get(i).getImageUrl();
					new ImgTask().execute(HttpHelper.ImageURL+"/"+imgUrl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		@Override
		protected void onPreExecute() {
			pg = ProgressDialog.show(CommodityItem.this, "芸茗茶叶", "正在加载");
			super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			gvCommodity.setAdapter(new CommodityAdapter());
			lvCommodity.setAdapter(new CommodityAdapter());
			lvCommodity.setVisibility(View.GONE);
			pg.dismiss();
		}
		
	}
	
	class ImgTask extends AsyncTask<String, Void, Void> {
		private String imgUrl;

		@Override
		protected Void doInBackground(String... params) {
			imgUrl = params[0];
			boolean isSucc = ImageFetcher.downLoadImage(imgUrl);
			Log.i("img", isSucc + "");
			return null;
		}
		
			
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()){
		case R.id.rb_grid :
			gvCommodity.setVisibility(View.VISIBLE);
			lvCommodity.setVisibility(View.GONE);
			break;
		case R.id.rb_list :
			gvCommodity.setVisibility(View.GONE);
			lvCommodity.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_product_sort :
			new AlertDialogInCommodity();
			break;
		case R.id.btn_product_filter :
			
			break;
		}
	}
	class AlertDialogInCommodity{
		private String orderBy;
		private int desc;
		String[] sort = new String[]{"按价格升序排序","按价格降序排序","按上架时间升序排序","按上架时间降序排序","按销量降序排序","按收藏降序排序"};
		public AlertDialogInCommodity(){
			orderBy="price";
			desc=0;
			new AlertDialog.Builder(CommodityItem.this).setTitle("请选择排序方式")
			.setSingleChoiceItems(sort, sortIndex, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					switch (arg1){
					case 0:
						orderBy="price";
						desc=0;
						break;
					case 1:
						orderBy="price";
						desc=1;
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;

					default:
						break;
					}
					sortIndex=arg1;
				}
			}).setPositiveButton("确认", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					new CommodityTask(orderBy,desc).execute();
						
					
				}
			}).setNegativeButton("取消", null).show();
		}
	}
}