package com.imcore.yunmingdemo.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.imcore.yunmingdemo.R;
import com.imcore.yunmingdemo.data.userPicture;
import com.imcore.yunmingdemo.http.HttpHelper;
import com.imcore.yunmingdemo.http.HttpMethod;
import com.imcore.yunmingdemo.http.RequestEntity;
import com.imcore.yunmingdemo.http.ResponseJsonEntity;
import com.imcore.yunmingdemo.image.ImageFetcher;
import com.imcore.yunmingdemo.util.JsonUtil;

public class MyEdit extends Activity implements android.view.View.OnClickListener{
	private static final int REQUEST_CODE_CAMERA = 0;
	private static final int REQUEST_CODE_GALLARY = 1;
	
	private List<userPicture> userImgList;
	private Uri captureImageUri;
	private ImageView imgPicture;
	private RelativeLayout rlOne;
	private String imgUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_edit);
		imgPicture = (ImageView)findViewById(R.id.img_user_picture);
		rlOne = (RelativeLayout)findViewById(R.id.rl_edit_one);
		rlOne.setOnClickListener(this);
		new MyEditPictureTask().execute();
	}

	
	//列出用户图片
	class MyEditPictureTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String url = "/user/images/list.do";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId",146);
			RequestEntity entity = new RequestEntity(url, HttpMethod.POST, map);
			String js;
			try {
				js = HttpHelper.execute(entity);
				ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
				if(rjs.getStatus()==200){
				String data = rjs.getData();
				userImgList = JsonUtil.toObjectList(data, userPicture.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new ImageFetcher().fetch("http://yunming-api.suryani.cn" +"/"+userImgList.get(1).pictureUrl, imgPicture);
			super.onPostExecute(result);
		}
		
	}
	
	//设置用户头像
		class SetMyEditPictureTask extends AsyncTask<Void, Void, Void>{
			
			@Override
			protected Void doInBackground(Void... arg0) {
				String url = "/user/avatar/set.do";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId",146);
				map.put("avatarUrl", imgUrl);
				RequestEntity entity = new RequestEntity(url, HttpMethod.GET, map);
				String js;
				try {
					js = HttpHelper.execute(entity);
					ResponseJsonEntity rjs = ResponseJsonEntity.fromJSON(js);
					if(rjs.getStatus()==200){
					String data = rjs.getData();
//					userImgList = JsonUtil.toObjectList(data, userPicture.class);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				
				super.onPostExecute(result);
			}
			
		}
	
	
	//用照相机拍照
	private void captureFromCamera(){
	String APP_DIR = Environment.getExternalStorageDirectory() + File.separator + "userImage";
	File dir = new File(APP_DIR);
	if(!dir.exists()){
		dir.mkdir();
	}

	String fileName = "img_" + System.currentTimeMillis() + ".png";
	File imgFile = new File(dir, fileName);
	if(!imgFile.exists()){
		try {
			imgFile.createNewFile();
			captureImageUri = Uri.fromFile(imgFile);
			imgUrl = getRealPathByUri(captureImageUri);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
			intent.putExtra("orientation", 0);
			startActivityForResult(intent,REQUEST_CODE_CAMERA);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
	
	//从所有图片中选择
	private void chooseFromGallarey(){
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_GALLARY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		switch (requestCode) {
		case REQUEST_CODE_CAMERA:
			// startImageZoom(captureImageUri);
			break;
		case REQUEST_CODE_GALLARY:
			// 处理从相册选择的结果
			
			Uri imageUri = data.getData();
			String path = getRealPathByUri(imageUri);
			imgUrl = path;
			captureImageUri = Uri.parse(path);
			break;
		}
		
		Bitmap bm = BitmapFactory.decodeFile(captureImageUri.getPath());
		imgPicture.setImageBitmap(bm);
		
		super.onActivityResult(requestCode, resultCode, data);
		new SetMyEditPictureTask().execute();
	}
	//根据URI获取图片真实路径
	private String getRealPathByUri(Uri uri) {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { MediaStore.Images.Media.DATA };
		Cursor c = cr.query(uri, projection, null, null, null);
		String realPath = "";
		if (c.moveToFirst()) {
			realPath = c.getString(c
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
		}
		return realPath;
	}
	
	private void getImage() {
		// 打开对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择图片")
				.setItems(R.array.choose_img_items, onDialogClickListener)
				.show();
	}

	private DialogInterface.OnClickListener onDialogClickListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				// 处理拍照
				captureFromCamera();
				break;
			case 1:
				// 从相册选择
				chooseFromGallarey();
				break;
			}
		}
	};
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.rl_edit_one:
			getImage();
			break;
		}
		
	}
	
	
	
}
