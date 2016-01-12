package com.kuaxue.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MD5鐨勭畻娉曞湪RFC1321 涓畾涔�
 * 
 * 鍦≧FC 1321涓紝缁欏嚭浜員est suite鐢ㄦ潵妫�楠屼綘鐨勫疄鐜版槸鍚︽纭細 MD5 ("") =
 * d41d8cd98f00b204e9800998ecf8427e MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72 MD5 ("message digest") =
 * f96b697d7cb7938d525a2f31aaf161d0 MD5 ("abcdefghijklmnopqrstuvwxyz") =
 * c3fcd3d76192e4007dfb496cca67e13b
 * 
 * @author andy
 * 
 * 浼犲叆鍙傛暟锛氫竴涓瓧鑺傛暟缁�
 * 
 * 浼犲嚭鍙傛暟锛氬瓧鑺傛暟缁勭殑 MD5 缁撴灉瀛楃涓�
 * 
 */
public class MD5 {
	public static String getMD5(byte[] source) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			return toString(md.digest());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getMD5(File file){
		InputStream inputStream = null;
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[4096];
			inputStream = new FileInputStream(file);
			
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				md.update(buffer, 0, len);
	        }
			
			return toString(md.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private static String toString(byte[] digests){
		char hexDigits[] = { // 鐢ㄦ潵灏嗗瓧鑺傝浆鎹㈡垚 16 杩涘埗琛ㄧず鐨勫瓧绗�
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		
		// 鐢ㄥ瓧鑺傝〃绀哄氨鏄� 16 涓瓧鑺�
		char str[] = new char[16 * 2]; // 姣忎釜瀛楄妭鐢� 16 杩涘埗琛ㄧず鐨勮瘽锛屼娇鐢ㄤ袱涓瓧绗︼紝
		// 鎵�浠ヨ〃绀烘垚 16 杩涘埗闇�瑕� 32 涓瓧绗�

		int k = 0; // 琛ㄧず杞崲缁撴灉涓搴旂殑瀛楃浣嶇疆
		for (int i = 0; i < 16; i++) { // 浠庣涓�涓瓧鑺傚紑濮嬶紝瀵� MD5 鐨勬瘡涓�涓瓧鑺�
			// 杞崲鎴� 16 杩涘埗瀛楃鐨勮浆鎹�
			byte byte0 = digests[i]; // 鍙栫 i 涓瓧鑺�
			str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 鍙栧瓧鑺備腑楂� 4 浣嶇殑鏁板瓧杞崲,
			// >>> 涓洪�昏緫鍙崇Щ锛屽皢绗﹀彿浣嶄竴璧峰彸绉�
			str[k++] = hexDigits[byte0 & 0xf]; // 鍙栧瓧鑺備腑浣� 4 浣嶇殑鏁板瓧杞崲
		}
		
		return new String(str); // 鎹㈠悗鐨勭粨鏋滆浆鎹负瀛楃涓�
	}
}
