package org.hailong.core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5 {

	public static String md5String(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		
		 MessageDigest md5 = MessageDigest.getInstance("MD5");

		 md5.update(string.getBytes("utf-8"));
		 
		 byte[] digest = md5.digest();
		 
		 StringBuffer sb = new StringBuffer();
		 
         for(int i = 0; i < digest.length; i ++){
        	 String s = Integer.toHexString(digest[i]);
        	 if(s.length() == 1){
        		 sb.append('0');
        	 }
        	 else{
        		 sb.append(s);
        	 }
         }
         
         return sb.toString();
	}
}
