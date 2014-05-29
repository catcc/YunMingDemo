/**
 * Copyright (c) 2006-2013 Goldfish Huang (hgoldfish@gmail.com)
 * All rights reserved.
 * 
 * This file may be used under the terms of the GNU General Public
 * License version 2.0 as published by the Free Software Foundation
 * and appearing in the file LICENSE.GPL included in the packaging of
 * this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.imcore.yunmingdemo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 生成MD5消息摘要 如果要对大量的数据生成摘�?代码如下: MD5 m=new MD5(); while(true){ String
 * data=getMoreData(); if(data.equals("")) break; m.update(data); }
 * System.out.println(m.hex_digest()); 另外提供了一个hex_md5()适用于少量数�?
 * 
 * @author goldfish 
 */
public class MD5 {
	private static String byte2hex(byte[] bytes) {
		StringBuffer buf = new StringBuffer("");
		for (int i = 0; i < bytes.length; i++) {
			String t = Integer.toHexString(bytes[i] >= 0 ? bytes[i]
					: (bytes[i] + 256));
			if (t.length() < 2)
				t = "0" + t;
			buf.append(t.toLowerCase());
		}
		return buf.toString();
	}

	public static String hex_md5(String s) {
		MessageDigest md5_dig = null;
		try {
			md5_dig = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			// do nothing , it is impossible!!
			return "";
		}
		byte[] bytes = s.getBytes();
		md5_dig.update(bytes);
		return byte2hex(md5_dig.digest());
	}

	private MessageDigest md5_dig;

	public MD5() {
		try {
			md5_dig = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
		}
	}

	public MD5(String s) {
		this();
		update(s);
	}

	public void update(String s) {
		byte[] bytes = s.getBytes();
		md5_dig.update(bytes);
	}

	public String hex_digest() {
		return byte2hex(md5_dig.digest());
	}
}
