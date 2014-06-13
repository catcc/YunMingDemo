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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class CommodityItem extends Activity implements android.view.View.OnClickListener,OnItemClickListener{
	private GridView gvCommodity;
	private ListView lvCommodity;
	private RadioButton rbtnList,rbtnGrid;
	private Button btnSort,btnFilter;
	private List<Commodity> myCommList;
	private TextView tvTheme;
	private ProgressDialog pg;
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
		tvTheme = (TextView)findViewById(R.id.tv_theme_name);
		
		init();
		
		btnSort.setOnClickListener(this);
		btnFilter.setOnClickListener(this);
		rbtnList.setOnClickListener(this);
		rbtnGrid.setOnClickListener(this);
		gvCommodity.setOnItemClickListener(this);
		lvCommodity.setOnItemClickListener(this);
	}
	
	private void init(){
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("CommId");
		id =  bundle.getLong("ComID");
		String categoryName = bundle.getString("CommName");
		tvTheme.setText(categoryName);
		new CommodityTask("",0,0).execute();
	}

	private class CommodityAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return myCommList.size();
		}

		@Override
		public Object getItem(int arg0) {
			
			return myCommList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
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
				
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			String Name = myCommList.get(arg0).productName;
			long price = myCommList.get(arg0).price;
			long saleTotal = myCommList.get(arg0).saleTotal;
			long favotieTotal = myCommList.get(arg0).favotieTotal;
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+ myCommList.get(arg0).imageUrl, viewHolder.img);
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
		private int filterId;
		public CommodityTask(String orderBy,int desc,int filterId){
			this.desc=desc;
			this.orderBy=orderBy;
			this.filterId=filterId;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			Map<String,Object> map = new HashMap<String, Object>();
			String url = "/category/products.do";
			map.put("id", id);
			map.put("orderBy", orderBy);
			map.put("desc", desc);
			map.put("filterId",filterId);
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
					Log.i("ee", myCommList.get(i).imageUrl + myCommList.get(i).productName);
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
			new SortAlertDialogInCommodity();
			break;
		case R.id.btn_product_filter :
			new FilterAlertDialogInCommodity();
			break;
		}
	}
	class SortAlertDialogInCommodity{
		private String orderBy;
		private int desc;
		String[] sort = new String[]{"按价格升序排序","按价格降序排序","按上架时间升序排序","按上架时间降序排序","按销量降序排序","按收藏降序排序"};
		public SortAlertDialogInCommodity(){
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
						orderBy="createdTime";
						desc=0;
						break;
					case 3:
						orderBy="createdTime";
						desc=1;
						break;
					case 4:
						orderBy="saleTotal";
						desc=1;
						break;
					case 5:
						orderBy="favotieTotal";
						desc=1;
						break;

					default:
						break;
					}
					sortIndex=arg1;
				}
			}).setPositiveButton("确认", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					new CommodityTask(orderBy,desc,0).execute();
						
					
				}
			}).setNegativeButton("取消", null).show();
		}
	}
	
	class FilterAlertDialogInCommodity{
		private int filterId;
		String[] sort = new String[]{"默认","0-1000","1000-2000","2000-3000",">3000"};
		
		public FilterAlertDialogInCommodity(){
			filterId=0;
			new AlertDialog.Builder(CommodityItem.this).setTitle("请选择排序方式")
			.setSingleChoiceItems(sort, sortIndex, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					switch (arg1){
					case 0:
						filterId=0;
						break;
					case 1:
						filterId=1;
						break;
					case 2:
						filterId=2;
						break;
					case 3:
						filterId=3;
						break;
					case 4:
						filterId=4;
						break;
					

					default:
						break;
					}
					sortIndex=arg1;
				}
			}).setPositiveButton("确认", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					new CommodityTask("",0,filterId).execute();
						
					
				}
			}).setNegativeButton("取消", null).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(CommodityItem.this,Productdetail.class);
		Bundle bundle = new Bundle();
		bundle.putLong("productId", myCommList.get(arg2).id);
		intent.putExtra("ProductId", bundle);
		startActivity(intent);
	}
}