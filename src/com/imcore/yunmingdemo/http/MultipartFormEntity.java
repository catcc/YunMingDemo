package com.imcore.yunmingdemo.http;

import java.io.File;

/**
 * 复合表单实体
 * 
 * @author Li Bin
 */
public class MultipartFormEntity extends RequestEntity {

	private String fileFieldName;
	private File fileField;

	/**
	 * 构造函数
	 */
	public MultipartFormEntity() {
	}

	/**
	 * 构造函数
	 * 
	 * @param url 指定的请求地址 
	 */
	public MultipartFormEntity(String url) {
		super(url);
	}

	/**
	 * 获得文件域的名称
	 * 
	 * @return
	 */
	public String getFileFieldName() {
		return fileFieldName;
	}

	/**
	 * 设置文件域的名称
	 * 
	 * @param fileFieldName
	 */
	public void setFileFieldName(String fileFieldName) {
		this.fileFieldName = fileFieldName;
	}

	/**
	 * 获得文件域
	 * 
	 * @return
	 */
	public File getFileField() {
		return fileField;
	}

	/**
	 * 设置文件域
	 * 
	 * @param fileField
	 */
	public void setFileField(File fileField) {
		this.fileField = fileField;
	}

}
