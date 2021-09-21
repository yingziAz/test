package com.mj.kit;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 * @author lsf
 */
public class PingYinKit {
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 获取汉字串拼音首字母，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			try {
				if (arr[i] > 128) {
					try {
						String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
						if (temp != null) {
							pybf.append(temp[0].charAt(0));
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else {
					pybf.append(arr[i]);
				}
			}catch(Exception e) {
				continue;
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

	/**
	 * 获取汉字串拼音，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音
	 */
	public static String getFullSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString();
	}

	/**
	 * @Description:获取首字母并大写
	 * @author lu
	 * @version 2020 年 04 月 01 日 14:33:04
	 * @param str
	 * @return
	 */
	private static String firstLetterToUpperCase(String str) {
		return String.valueOf(str.charAt(0)).toUpperCase();
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * @Description:获取大写的首字母,如果是0,9 则使用0-9
	 * @author lu
	 * @version 2020 年 04 月 01 日 14:33:15
	 * @param chinese
	 * @return
	 */
	public static String getFirstLetter(String chinese) {
		if (StringUtils.isBlank(chinese)) {
			return "";
		}
		String str = firstLetterToUpperCase(getFirstSpell(chinese));
		if(isInteger(str)) {
			return "0-9";
		}
		return str;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(getFirstLetter("（SHOWROOM呈现名（英文） Displayed Showroom Name"));

		System.out.println(getFirstSpell("中国"));
		System.out.println(getFullSpell("中国"));
		System.out.println(getPingYin("中国"));

		System.out.println(getFirstLetter("1"));
		System.out.println(getFirstLetter("0asff"));
		System.out.println(getFirstLetter("沙发"));

	}

}
