package com.sun.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import org.bson.types.ObjectId;

public class TestMain {
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;

			for (int i = 0; i < j; i++) {
				byte b = md[i];
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}

	}

	public static void main(String[] args) throws UnsupportedEncodingException{
//		String result = MD5("13817129395");
//		System.out.println(result);
//		System.out.println();
//		String aa = URLEncoder.encode("100", "aa");
//		System.out.println(aa);
//		String result = org.apache.commons.lang.StringUtils
//			.leftPad(String.valueOf(Integer.valueOf("000010") + 1), 7, "0");
//		System.out.println(result);
		
//		String mongoId = ObjectId.get().toString();
//		System.out.println("mongoId:" + mongoId);
		int i;
		for(i = 0; i < 10; ++i){
			System.out.println(i);
		}
		System.out.println("end : " + i);
	}
	
	public void testArr(){
//		String[] arr = [{"image1":"path/image1.jpg"},{"image2":"path/image2.jpg"}];
		Image[] images = new Image[2];
		images[1] = new Image("image1", "path/image1.jpg");
		images[2] = new Image("image2", "path/image2.jpg");
		
	}
	
	
	class Image{
		private String name;
		private String url;
		
		public Image(String name, String url){
			this.name = name;
			this.url = url;
		}
	}
}
