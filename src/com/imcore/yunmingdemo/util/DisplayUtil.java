package com.imcore.yunmingdemo.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	/**
	 * 获取屏幕宽度的像素值
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度像素值
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 把dp单位的尺寸值转换为px单位
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2Px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 把px单位的尺寸值转换为dp单位
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2Dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
