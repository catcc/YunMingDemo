package com.imcore.yunmingdemo.image;

import java.io.File;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

/**
 * 本地文件存储帮助类
 * 
 * @author Li Bin
 */
public final class StorageHelper {
	public final static String APP_DIR_ROOT = "YunMing";
	public final static String APP_DIR_IMAGE = "YunMing/images";

	private final static String LOG_DEBUG_TAG = "gh_storage";

	// 避免调用者创建该类实例
	private StorageHelper() {
	}

	/**
	 * 判断外部存储器是否可写入
	 * 
	 * @return 返回一个布尔值，true表示外部存储器可写入
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断外部存储器是否可读
	 * 
	 * @return 返回一个布尔值，true表示外存储器可读
	 */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前app文件存储路径
	 * 
	 * @return 返回File对象实例，表示当前app文件存储根目录
	 */
	public static File getAppDir() {
		File appDir = null;
		if (isExternalStorageWritable()) {
			appDir = new File(Environment.getExternalStorageDirectory() + "/"
					+ APP_DIR_ROOT);
			if (!appDir.exists()) {
				appDir.mkdirs();
			}
		} else {
			Log.e(LOG_DEBUG_TAG, "外部存储器不可写入");
		}
		return appDir;
	}

	/**
	 * 获取当前app存放图片文件的目录
	 * 
	 * @return 返回File对象实例，表示当前app存储图片的目录
	 */
	protected static File getAppImageDir() {
		File appDir = null;
		if (isExternalStorageWritable()) {
			appDir = new File(Environment.getExternalStorageDirectory() + "/"
					+ APP_DIR_IMAGE);
			if (!appDir.exists()) {
				appDir.mkdirs();
			}
		} else {
			Log.d(LOG_DEBUG_TAG, "外部存储器不可写入");
		}
		return appDir;
	}

	/**
	 * 从本地读取图片，返回一个Bitmap对象
	 * 
	 * @param imgName
	 *            图片名
	 * @return 返回获取到本地图片的Bitmap实例
	 */
	protected static Bitmap getBitmapFromLocal(String imgName) {
		if (!isExternalStorageReadable()) {
			Log.d(LOG_DEBUG_TAG, "外部存储器不可读");
			return null;
		}
		File imageFile = new File(getAppImageDir(), imgName);
		Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		return bm;
	}

	/**
	 * 从本地读取图片，返回一个Bitmap对象
	 * 
	 * @param imgName
	 *            图片名
	 * @param reqWidth
	 *            要获取图片的目标宽度
	 * @param reqHeight
	 *            要获取图片的目标高度
	 * @return 返回获取到本地图片的Bitmap实例
	 */
	protected static Bitmap getBitmapFromLocal(String imgName, int reqWidth,
			int reqHeight) {
		if (!isExternalStorageReadable()) {
			Log.d(LOG_DEBUG_TAG, "外部存储器不可读");
			return null;
		}
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(getAppImageDir() + "/" + imgName, opts);
		opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
		opts.inJustDecodeBounds = false;

		File imageFile = new File(getAppImageDir(), imgName);
		Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), opts);

		return bm;
	}

	/**
	 * 计算图片inSampleSize缩放比
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * 获取uri指定的文件的真实路径
	 * 
	 * @param resolver
	 *            ContentResolver实例
	 * @param uri
	 *            指定文件的uri
	 * @return 返回一个字符串，表示获取到的文件完整路径
	 */
	public static String getRealPathByUri(ContentResolver resolver, Uri uri) {
		String[] proj = new String[] { MediaColumns.DATA };
		Cursor cursor = MediaStore.Images.Media.query(resolver, uri, proj);
		String realPath = null;
		if (cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				realPath = cursor.getString(columnIndex);
			}
		}
		cursor.close();
		return realPath;
	}
}
