package com.imcore.yunmingdemo.http;

import java.util.Map;

/**
 * Http请求数据载体，包括网络地址url,以及请求所需的form参数
 * 
 * @author Li Bin
 */
public class RequestEntity {
	private String url;
	private int method = HttpMethod.POST;
	private Map<String, Object> textFields;

	/**
	 * 构造函数
	 */
	public RequestEntity() {
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            指定的请求链接地址
	 */
	public RequestEntity(String url) {
		this.url = url;
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            指定的请求链接地址
	 * @param textFields
	 *            请求参数，纯文本，不包含文件域
	 */
	public RequestEntity(String url, Map<String, Object> textFields) {
		this.url = url;
		this.textFields = textFields;
	}

	/**
	 * 构造函数
	 * 
	 * @param url
	 *            指定的请求链接地址
	 * @param method
	 *            指定的HTTP请求方法
	 * @param textFields
	 *            请求参数，纯文本，不包含文件域
	 */
	public RequestEntity(String url, int method, Map<String, Object> textFields) {
		this.url = url;
		this.method = method;
		this.textFields = textFields;
	}

	/**
	 * 获取网络地址url
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置网络地址url
	 * 
	 * @return
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获得请求方法
	 * 
	 * @return
	 */
	public int getMethod() {
		return method;
	}

	/**
	 * 设置请求方法
	 * 
	 * @param method
	 */
	public void setMethod(int method) {
		this.method = method;
	}

	/**
	 * 获取请求参数
	 * 
	 * @return
	 */
	public Map<String, Object> getTextFields() {
		return textFields;
	}

	/**
	 * 设置请求参数
	 * 
	 * @param textFields
	 */
	public void setTextFields(Map<String, Object> textFields) {
		this.textFields = textFields;
	}

}
