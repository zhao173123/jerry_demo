package com.sun.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * java Message.Digest加密
 * 
 */
public class EncryptionUtils {

	public static void main(String[] args) {
		String hash1MD5 = getHash1("jerry", "MD5");
		System.out.println(hash1MD5);// 30035607ee5bb378c71ab434a6d05410
		String hash1SHA = getHash1("jerry", "SHA");
		System.out.println(hash1SHA);// 75926e095b28dd773adde5bade93e483
		
		String hash2MD5 = getHash2("jerry", "MD5");
		System.out.println(hash2MD5);// 30035607ee5bb378c71ab434a6d05410
		String hash2SHA = getHash2("jerry", "SHA");
		System.out.println(hash2SHA);// 75926e095b28dd773adde5bade93e4836b1d92fc

		String hash3MD5 = getHash3("jerry", "MD5");
		System.out.println(hash3MD5);//30035607ee5bb378c71ab434a6d05410
		String hash3SHA = getHash3("jerry", "SHA");
		System.out.println(hash3SHA);//75926e095b28dd773adde5bade93e4836b1d92fc
	}

	/**
	 * 方式一：使用位运算符，将加密后的数据转换成16进制
	 * 
	 * @param source 需要加密的字符串
	 * @param hashType 加密类型 （MD5和SHA）
	 * @return
	 */
	public static String getHash1(String source, String hashType) {
		// 用来将字节转换成16进制表示的字符
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance(hashType);
			md.update(source.getBytes());// 处理数据，使指定的byte数组更新摘要
			// [48, 3, 86, 7, -18, 91, -77, 120, -57, 26, -76, 52, -90, -48, 84, 16]
			byte[] encryStr = md.digest();// 获得密文完成哈希计算，产生128位的长整数
			char[] str = new char[16 * 2];// 每个字节用16进制表示的话，使用2个字符
			int k = 0;// 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {
				byte byte0 = encryStr[i];// 取第i个字节
				str[k++] = hexDigits[byte0 >> 4 & 0xf]; // 取字节中高4位的数字转换，>>>为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf];// 取字节中低4位的数字转换
			}
			return new String(str);// 结果转换位字符串
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方式二：使用格式化的方式，将加密后的数据转换为16进制
	 * 推荐方式
	 * @param source
	 * @param hashType
	 * @return
	 */
	public static String getHash2(String source, String hashType){
		StringBuffer sb = new StringBuffer();
		MessageDigest md5;
		try{
			md5 = MessageDigest.getInstance(hashType);
			md5.update(source.getBytes());
			for(byte b : md5.digest()){
				sb.append(String.format("%02x", b));//10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
			}
			return sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 方式三：使用算法，将加密后的数据转换成16进制
	 * 
	 * @param source
	 * @param hashType
	 * @return
	 */
	public static String getHash3(String source, String hashType) {
		// 用来将字节转换成16进制表示的字符
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer sb = new StringBuffer();
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance(hashType);
			md5.update(source.getBytes());
			byte[] encryStr = md5.digest();
			for (int i = 0; i < encryStr.length; i++) {
				int iRet = encryStr[i];
				if (iRet < 0) {
					iRet += 256;
				}
				int iD1 = iRet / 16;
				int iD2 = iRet % 16;
				sb.append(hexDigits[iD1] + "" + hexDigits[iD2]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

}
