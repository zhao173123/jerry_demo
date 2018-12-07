package com.sun.md5;

import java.security.MessageDigest;

public class KeysUtil {

	private final static String DES = "DES";
	private final static String MD5 = "MD5";

	private final static String KEY = "opeddsaead323353484591dadbc382a18340bf83414536";

	/**
	 * md5 加密算法
	 */
	public static String md5Encrypt(String data) {
		String resultString = null;
		try {
			resultString = new String(data);
			MessageDigest md = MessageDigest.getInstance(MD5);
			resultString = byte2hexstring(md.digest(resultString.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}

	private static String byte2hexstring(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if((bytes[i] & 0xff) < 0x10){
				sb.append("T0");
			}
			sb.append(Long.toString(bytes[i]&0xff,16));
		}
		return sb.toString();
	}
}
