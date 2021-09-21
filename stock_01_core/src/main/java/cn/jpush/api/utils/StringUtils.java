package cn.jpush.api.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.LinkedHashSet;
import java.util.Set;


public class StringUtils {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String toMD5(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	public static String encodeParam(String param) {
		String encodeParam = null;
		try {
			encodeParam = URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeParam;
	}

	public static String arrayToString(String[] values) {
		if (null == values)
			return "";

		StringBuffer buffer = new StringBuffer(values.length);
		for (int i = 0; i < values.length; i++) {
			buffer.append(values[i]).append(",");
		}
		if (buffer.length() > 0) {
			return buffer.toString().substring(0, buffer.length() - 1);
		}
		return "";
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isTrimedEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean isNotEmpty(String s) {
		return s != null && s.length() > 0;
	}

	public static boolean isLineBroken(String s) {
		if (null == s) {
			return false;
		}
		if (s.contains("\n")) {
			return true;
		}
		if (s.contains("\r\n")) {
			return true;
		}
		return false;
	}

	/**
	 * 根据逗号分隔为set
	 *
	 * @param src
	 * @return
	 */
	public static Set<String> splitToSetByComma(String src) {
		return splitToSet(src, ",");
	}

	/**
	 * 把字符串拆分成一个set
	 *
	 * @param src
	 * @param regex
	 * @return
	 */
	public static Set<String> splitToSet(String src, String regex) {
		if (src == null) {
			return null;
		}

		String[] strings = src.split(regex);
		Set<String> set = new LinkedHashSet<>();
		for (String s : strings) {
			if (org.apache.commons.lang.StringUtils.isBlank(s)) {
				continue;
			}
			set.add(s.trim());
		}
		return set;
	}

}
