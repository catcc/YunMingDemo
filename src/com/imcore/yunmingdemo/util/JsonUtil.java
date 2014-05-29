package com.imcore.yunmingdemo.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * JSON数据解析工具类，用于将JSON字符串转换成指定的对象或对象List，以及Map或包含Map的List
 * 
 * @author Li Bin
 */
public class JsonUtil<T> {
	private static final String LOG_JSON_ERROR = "com.imcore.common.util.JsonError";

	private static final String BYTE = "java.lang.Byte";
	private static final String INTEGER = "java.lang.Integer";
	private static final String SHORT = "java.lang.Short";
	private static final String LONG = "java.lang.Long";
	private static final String BOOLEAN = "java.lang.Boolean";
	private static final String CHAR = "java.lang.Character";
	private static final String FLOAT = "java.lang.Float";
	private static final String DOUBLE = "java.lang.Double";

	private static final String VALUE_BYTE = "byte";
	private static final String VALUE_INTEGER = "int";
	private static final String VALUE_SHORT = "short";
	private static final String VALUE_LONG = "long";
	private static final String VALUE_BOOLEAN = "boolean";
	private static final String VALUE_CHAR = "char";
	private static final String VALUE_FLOAT = "float";
	private static final String VALUE_DOUBLE = "double";

	/**
	 * 根据key获取给定的json数据的�?
	 * 
	 * @param json
	 *            给定的JSON字符�?
	 * @param key
	 *            指定的要获取值所对应的key
	 * @return 返回�?��字符串，表示根据指定的key�?��到的值，获取失败或发生JSON解析错误则返回空字符�?
	 */
	public static String getJsonValueByKey(String json, String key) {
		String value = "";
		try {
			JSONObject jo = new JSONObject(json);
			value = jo.getString(key);
		} catch (JSONException e) {
			Log.e(LOG_JSON_ERROR, e.getLocalizedMessage());
		}
		return value;
	}

	/**
	 * 将指定的JSON字符串转换成cls指定的类的实例对�?
	 * 
	 * @param json
	 *            给定的JSON字符�?
	 * @param cls
	 *            指定要转换成的对象所属的类型Class实例
	 * @return 返回cls指定类型的对象实�?其中的字段与json数据键�?对一�?���?
	 */
	public static <T> T toObject(String json, Class<T> cls) {
		T obj = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			obj = cls.newInstance();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isFinal(field.getModifiers())
						|| Modifier.isPrivate(field.getModifiers())) {
					continue;
				}
				try {
					String key = field.getName();
					if (jsonObject.get(key) == JSONObject.NULL) {
						field.set(obj, null);
					} else {
						Object value = getValue4Field(jsonObject.get(key),
								jsonObject.get(key).getClass().getName());
						field.set(obj, value);
					}
				} catch (JSONException e) {
					field.set(obj, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_JSON_ERROR, e.getLocalizedMessage());
		}
		return obj;
	}

	/**
	 * 把指定的对象orginalValue转换成typeName指定的类型的对象
	 * 
	 * @param orginalValue
	 *            对象在转换之前的�?
	 * @param fieldType
	 *            要转换的类型名称
	 * @return
	 */
	private static Object getValue4Field(Object orginalValue, String typeName) {
		Log.i("YunMing", typeName);
		Object value = orginalValue.toString();
		if (typeName.equals(BYTE) || typeName.equals(VALUE_BYTE)) {
			value = Byte.class.cast(orginalValue);
		}
		if (typeName.equals(INTEGER) || typeName.equals(VALUE_INTEGER)) {
			value = Integer.class.cast(orginalValue);
		}
		if (typeName.equals(SHORT) || typeName.equals(VALUE_SHORT)) {
			value = Short.class.cast(orginalValue);
		}
		if (typeName.equals(LONG) || typeName.equals(VALUE_LONG)) {
			value = Long.class.cast(orginalValue);
		}
		if (typeName.equals(BOOLEAN) || typeName.equals(VALUE_BOOLEAN)) {
			value = Boolean.class.cast(orginalValue);
		}
		if (typeName.equals(CHAR) || typeName.equals(VALUE_CHAR)) {
			value = Character.class.cast(orginalValue);
		}
		if (typeName.equals(FLOAT) || typeName.equals(VALUE_FLOAT)) {
			value = Float.class.cast(orginalValue);
		}
		if (typeName.equals(DOUBLE) || typeName.equals(VALUE_DOUBLE)) {
			value = Double.class.cast(orginalValue);
		}
		return value;
	}

	/**
	 * 将指定的JSON字符串转换成包含cls指定的类型的实体对象List集合
	 * 
	 * @param json
	 *            给定的JSON字符�?
	 * @param cls
	 *            指定要转换成的对象所属的类型Class实例
	 * @return 返回�?��List集合，其中包含json中的数据元素�?��应的实体对象实例
	 */
	public static <T> List<T> toObjectList(String json, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			List<String> jsonStrList = toJsonStrList(json);
			for (String jsonStr : jsonStrList) {
				T obj = toObject(jsonStr, cls);
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_JSON_ERROR, e.getLocalizedMessage());
		}
		return list;
	}

	/**
	 * 将一个数组型json字符串转换成包含子json字符串的List集合
	 * 
	 * @param json
	 *            给定的JSON字符�?
	 * @return 返回�?��List集合，包含一组字符串，对应于给定原始JSON数据内元素的字符串形�?
	 */
	public static List<String> toJsonStrList(String json) {
		List<String> strList = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonStr = jsonArray.getString(i);
				strList.add(jsonStr);
			}
		} catch (JSONException e) {
			Log.e(LOG_JSON_ERROR, e.getMessage());
		}
		return strList;
	}

	/**
	 * 将json字符串转换为Map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> toMap(String json) {
		Map<String, Object> map = null;
		try {
			JSONObject jo = new JSONObject(json);
			map = convertJSONObjectToMap(jo);
		} catch (Exception e) {
			Log.e(LOG_JSON_ERROR, e.getMessage());
		}
		return map;
	}

	/**
	 * 将json字符串转换为包含Map的List集合
	 * 
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> toMapList(String json) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				Map<String, Object> map = convertJSONObjectToMap(jo);
				mapList.add(map);
			}
		} catch (JSONException e) {
			Log.e(LOG_JSON_ERROR, e.getMessage());
		}
		return mapList;
	}

	/**
	 * 将一个JSONObject对象转换为Map
	 * 
	 * @param jo
	 * @return
	 * @throws JSONException
	 */
	private static Map<String, Object> convertJSONObjectToMap(JSONObject jo)
			throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject newJo = mergeJsonNodes(jo);

		JSONArray names = newJo.names();
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			Object value = newJo.get(key);
			if ((value != null) && (!value.toString().equals(""))
					&& (!value.toString().equals("null"))) {
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * 将JSON对象的非�?��子结点与�?��结点合并
	 * 
	 * @param oldJo
	 *            包含非一阶结点的Json对象
	 * @return 返回合并之后的，只包含一阶结点的Json对象
	 */
	private static JSONObject mergeJsonNodes(JSONObject oldJo)
			throws JSONException {
		JSONObject newJo = oldJo;
		JSONArray names = newJo.names();
		List<String> delKeys = new ArrayList<String>(); // 待删除的非一阶结点的Json对象的key

		// 找出�?��合并的子结点的key
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			if (newJo.optJSONObject(key) != null) {
				delKeys.add(key);
			}
		}
		// 合并找到的子结点与一阶结点，并删除原先的子结�?
		for (String key : delKeys) {
			JSONObject subJo = newJo.getJSONObject(key);
			subJo = mergeJsonNodes(subJo); // 递归整理子结点的�?��子结�?
			newJo = merge(newJo, subJo);
			newJo.remove(key);
		}
		return newJo;
	}

	/**
	 * 合并两个JSON对象
	 * 
	 * @param jo1
	 * @param jo2
	 * @return 返回合并之后的JSON对象
	 */
	private static JSONObject merge(JSONObject jo1, JSONObject jo2)
			throws JSONException {
		JSONObject newJo = jo1;
		JSONArray names = jo2.names();
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			newJo.put(key, jo2.get(key));
		}
		return newJo;
	}

	/**
	 * 判断�?��JSON字符串是否是空数�?
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonNull(String json) {
		if (json == null || json.equals("") || json.equals("null")
				|| json.equals("{}") || json.equals("[]")) {
			return true;
		} else {
			return false;
		}
	}
}