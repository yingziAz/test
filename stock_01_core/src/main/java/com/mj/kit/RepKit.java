package com.mj.kit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证
 * 
 * @author frank.zong
 * 		
 */
public class RepKit {
	
	/**
	 * 验证邮箱
	 */
	public static boolean isEmail(String input) {
		try {
			Pattern regex = Pattern.compile(RegEx.EMAIL);
			Matcher matcher = regex.matcher(input);
			return matcher.matches();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 验证手机号
	 */
	public static boolean isMobile(String input) {
		try {
			Pattern regex = Pattern.compile(RegEx.MOBILE);
			Matcher matcher = regex.matcher(input);
			return matcher.matches();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 正则表达式
	 * 
	 * @author frank.zong
	 * 		
	 */
	private class RegEx {
		
		/**
		 * 邮箱
		 */
		public static final String EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	
		/**
		 * 手机号
		 */
		public static final String MOBILE = "^0?(13|15|18|14|17)[0-9]{9}$";
	}
}
