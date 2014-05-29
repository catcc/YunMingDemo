package com.imcore.yunmingdemo.util;

/**
 * 文本工具�?
 * 
 * @author Li Bin
 */
public class TextUtil {
	/**
	 * 判断给定字符串是否为空字符串
	 * 
	 * @param source
	 * @return 给定的字符串是空字符串返回true，否则返回false
	 */
	public static boolean isEmptyString(String source) {
		if (source == null) {
			return true;
		} else if (source.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断指定的字符串是否是合法的电话号码
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isPhoneNumber(String numberString) {
		boolean isNumber = false;
		if (!numberString.equals("")) {
			if (numberString.length() == 11
					&& (isNumber(numberString))
					&& (numberString.startsWith("13")
							|| numberString.startsWith("18")
							|| numberString.startsWith("15") || numberString
								.startsWith("14"))) {
				isNumber = true;
			}
		}

		return isNumber;
	}

	/**
	 * 判断给定的文本是否是数字
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isNumber(String numberString) {
		return numberString.matches("^[0-9]*$");
	}

	/**
	 * 将单精度浮点数字形式的字符串转换成float类型数据
	 * 
	 * @param floatString
	 * @return
	 */
	public static float getFloat(String floatString) {
		float number = 0;
		try {
			number = Float.parseFloat(floatString);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return number;
	}

	/**
	 * 将整形形式的字符串转换成int类型数据
	 * 
	 * @param intString
	 * @return
	 */
	public static int getInt(String intString) {
		int number = 0;
		try {
			number = Integer.parseInt(intString.trim());
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		return number;
	}

	/**
	 * 将双精度浮点数字形式的字符串转换成float类型数据
	 * 
	 * @param doubleString
	 * @return
	 */
	public static Double getDouble(String doubleString) {
		Double number = 0d;
		try {
			number = Double.parseDouble(doubleString);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return number;
	}
}
