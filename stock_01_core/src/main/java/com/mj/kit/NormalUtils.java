package com.mj.kit;


import org.apache.commons.lang.StringUtils;
/**
 * 
 * @author lu
 * 通用方法util
 *
 */
public class NormalUtils {
	
	 /**
	  * 获取部分字符
	 * @param str  处理的字符
	 * @param num  截取的字符
	 * @return
	 */
	public static String partString(String str, int num) {
		String _str = "";
		if (str != null && StringUtils.isNotBlank(str)) {
			if(str.length()>=num){
				_str = str.substring(0,num)+"...";
			}else{
				_str = str;
			}
			
		}
		return _str;
	}
	

}