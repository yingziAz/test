package com.mj.kit;

import org.apache.commons.lang.RandomStringUtils;

/**
 * 随机数据生成
 * 
 * @author frank.zong
 *		
 */
public class RandomKit {
	
	/**
	 * 数组（除去O和0、I、1和L）
	 */
	public static final char ARRAY[] = { '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z' };
	
	/**
	 * 根据指定长度随机生成字符串(有数字和字母)
	 * @param length
	 * @return
	 */
	public static String getString(int length){
		return RandomStringUtils.random(length, RandomKit.ARRAY);
	}
	
	/**
	 * 根据指定长度随机生成数字(只有数字)
	 * @param length
	 * @return
	 */
	public static String getInt(int length){
		return RandomStringUtils.randomNumeric(length);
	}
}
