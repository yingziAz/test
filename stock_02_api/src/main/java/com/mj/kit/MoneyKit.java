/**
 * @Description:
 * @author: Administrator
 * @date: 2019年3月6日 下午7:30:45
 */
package com.mj.kit;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author 金额数字转为大写汉字
 *
 */
public class MoneyKit {
	private static final String UNIT[] = { "万", "仟", "佰", "拾", "亿", "仟", "佰",  
			"拾", "万", "仟", "佰", "拾", "圆", "角", "分" };  

	private static final String NUM[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆",  
			"柒", "捌", "玖" };  

	private static final double MAX_VALUE = 9999999999999.99D;  

	/** 
	 * 将金额小数转换成中文大写金额 
	 * @param money 
	 * @return result 
	 */  
	public static String convertMoney(double money) {  
		if (money < 0 || money > MAX_VALUE)  
			return "参数非法!";  
		long money1 = Math.round(money * 100); // 四舍五入到分  
		if (money1 == 0)  
			return "零元整";  
		String strMoney = String.valueOf(money1);  
		int numIndex = 0; // numIndex用于选择金额数值  
		int unitIndex = UNIT.length - strMoney.length(); // unitIndex用于选择金额单位  
		boolean isZero = false; // 用于判断当前为是否为零  
		String result = "";  
		for (; numIndex < strMoney.length(); numIndex++, unitIndex++) {  
			char num = strMoney.charAt(numIndex);  
			if (num == '0') {  
				isZero = true;  
				if (UNIT[unitIndex] == "亿" || UNIT[unitIndex] == "万"  
						|| UNIT[unitIndex] == "圆") { // 如果当前位是亿、万、元，且数值为零  
					result = result + UNIT[unitIndex]; //补单位亿、万、元  
					isZero = false;  
				}   
			}else {  
				if (isZero) {  
					result = result + "零";  
					isZero = false;  
				}  
				result = result + NUM[Integer.parseInt(String.valueOf(num))] + UNIT[unitIndex];  
			}  
		}  
		//不是角分结尾就加"整"字  
		if (!result.endsWith("角")&&!result.endsWith("分")) {  
			result = result + "整";  
		}  
		//例如没有这行代码，数值"400000001101.2"，输出就是"肆千亿万壹千壹佰零壹元贰角"  
		result = result.replaceAll("亿万", "亿");  
		return result;  
	} 
	
	/**
	 * 分转元
	 * @param fen
	 * @return
	 */
	public static String fenToYun4Label(int fen){
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		/*
		  * setMinimumFractionDigits设置成2
		  * 
		  * 如果不这么做，那么当value的值是100.00的时候返回100
		  * 
		  * 而不是100.00
		  */
		nf.setMinimumFractionDigits(2);
		nf.setRoundingMode(RoundingMode.HALF_UP);
		/*
		  * 如果想输出的格式用逗号隔开，可以设置成true
		  */
		nf.setGroupingUsed(false);
		return nf.format((double)fen/100);
	}
	
	
}
