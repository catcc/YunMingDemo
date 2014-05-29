package com.imcore.yunmingdemo.http;

import com.imcore.yunmingdemo.util.JsonUtil;

/**
 * JSON格式http响应实体结构定义，按照后台接口说明，把字段信息封装为该类的实例
 * 
 * @author Li Bin
 */
public class ResponseJsonEntity {
	// 响应状态：200表示成功
	private int status;

	// 响应数据实体的json字符串形式
	private String data;

	// 当status不为200时，错误描述
	private String message;

	// 私有构造函数，避免开发人员直接构造实例
	private ResponseJsonEntity() {
	}

	/**
	 * 根据给定的json字符串构造该类实例，把json中的数据结构映射到该实例中
	 * 
	 * @param json
	 * @return
	 */
	public static ResponseJsonEntity fromJSON(String json) {
		ResponseJsonEntity entity = new ResponseJsonEntity();
		entity.status = Integer.parseInt(JsonUtil.getJsonValueByKey(json,
				"status"));
		if (entity.status == 200) {
			entity.data = JsonUtil.getJsonValueByKey(json, "data");
		} else {
			entity.message = JsonUtil.getJsonValueByKey(json, "message");
		}

		return entity;
	}

	public int getStatus() {
		return this.status;
	}

	public String getData() {
		return this.data;
	}

	public String getMessage() {
		return this.message;
	}
}
