package com.mj.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;

public class Test4Sync {
	static String APP_DOMAIN =  "https://bo.mode-ec.com";
	static final String APP_KEY = "843ab9d500d2ed85b6a74bb8c37f5179";
	static final String APP_ID = "5b076f5184a395c9d6b4311b";
	
	
	public static void main(String[] args) {
		//System.out.println(PingYinKit.getFirstLetter("SHOWROOM呈现名（英文）DisplayedShowroomName"));
		//
		Test4Sync ts = new Test4Sync();
		ts.test4Sms();
	}
	
	/**
	 * 同步商家
	 */
	public void testSyncSeller() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timestamp = df.format(new Date());
		String nonce = RandomUtil.randomString(6);  //6位随机字串
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("	\"store_id\": \"RSVP-TEST-15\",");
		sb.append("	\"account\": \"RSVP-TEST-15\",");
		sb.append("	\"password\": \"123456\",");
		sb.append("	\"store_type\": 2,");
		sb.append("	\"store_name_cn\": \"\",");
		sb.append("	\"store_name_en\": \"\",");
		sb.append("	\"store_intro_cn\": \"\",");
		sb.append("	\"store_intro_en\": \"\",");
		sb.append("	\"store_website\": \"\",");
		sb.append("	\"license_attach\": \"https://images.pexels.com/photos/3467149/pexels-photo-3467149.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500\",");
		sb.append("	\"band_weixin_qrcode\": \"\",");
		sb.append("	\"brand\": [{");
		sb.append("		\"brand_id\": \"RSVP-BRAND-01\",");
		sb.append("		\"brand_name_cn\": \"RSVP-BRAND-NAME-CN\",");
		sb.append("		\"brand_name_en\": \"RSVP-BRAND-NAME-EN\",");
		sb.append("		\"brand_intro_cn\": \"RSVP-BRAND-INTRO-CN\",");
		sb.append("		\"brand_intro_en\": \"RSVP-BRAND-INTRO-EN\",");
		sb.append("		\"brand_logo\": \"\"");
		sb.append("	}, {");
		sb.append("		\"brand_id\": \"RSVP-BRAND-02\",");
		sb.append("		\"brand_name_cn\": \"RSVP-BRAND-NAME-CN\",");
		sb.append("		\"brand_name_en\": \"RSVP-BRAND-NAME-EN\",");
		sb.append("		\"brand_intro_cn\": \"RSVP-BRAND-INTRO-CN\",");
		sb.append("		\"brand_intro_en\": \"RSVP-BRAND-INTRO-EN\",");
		sb.append("		\"brand_logo\": \"\"");
		sb.append("	}]");
		sb.append("}");
		
		Map<String, Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("appid", APP_ID);
		paramMap.put("timestamp", timestamp);
		paramMap.put("nonce", nonce);
		paramMap.put("param", sb.toString());

		StringBuilder param = new StringBuilder();
		boolean first = true;
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			String value = (String)entry.getValue();
			if (StrKit.isBlank(value)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				param.append("&");
			}
			param.append(entry.getKey()).append("=");
			param.append(value);
		}
		param.append(APP_KEY);

		String sign = HashKit.md5(param.toString());
		paramMap.put("sign", sign);
		String result = HttpUtil.post(APP_DOMAIN+"/sync/seller", paramMap);
		System.out.println(result);
	}

	public void testSyncSeller33() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timestamp = df.format(new Date());
		String nonce = RandomUtil.randomString(6);  //6位随机字串
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("	\"store_id\": 118,");
		sb.append("	\"account\": \"15372060977\",");
		sb.append("	\"password\": \"123456\",");
		sb.append("	\"store_type\": 1,");
		sb.append("	\"license_attach\": \"https://images.pexels.com/photos/3467149/pexels-photo-3467149.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500\",");
		sb.append("}");
		
		Map<String, Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("appid", APP_ID);
		paramMap.put("timestamp", timestamp);
		paramMap.put("nonce", nonce);
		paramMap.put("param", sb.toString());

		StringBuilder param = new StringBuilder();
		boolean first = true;
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			String value = (String)entry.getValue();
			if (StrKit.isBlank(value)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				param.append("&");
			}
			param.append(entry.getKey()).append("=");
			param.append(value);
		}
		param.append(APP_KEY);

		String sign = HashKit.md5(param.toString());
		paramMap.put("sign", sign);
		String result = HttpUtil.post(APP_DOMAIN+"/sync/seller", paramMap);
		System.out.println(result);
	}


	
	/**
	 * 同步买手
	 */
	public void testSyncBuyer() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timestamp = df.format(new Date());
		String nonce = RandomUtil.randomString(6);  //6位随机字串
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("	\"buyer_id\": \"RSVP-TEST-06\",");
		sb.append("	\"account\": \"RSVP-BUYER-TEST-06\",");
		sb.append("	\"password\": \"123456\",");
		sb.append("	\"title\": \"test\",");
		sb.append("	\"surname\": \"t\",");
		sb.append("	\"forename\": \"t\",");
		sb.append("	\"company_name\": \"test\",");
		sb.append("	\"mobile_area\": \"86\",");
		sb.append("	\"mobile\": \"13336033114\",");
		sb.append("	\"email\": \"test\"");
		sb.append("}");
		
		Map<String, Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("appid", APP_ID);
		paramMap.put("timestamp", timestamp);
		paramMap.put("nonce", nonce);
		paramMap.put("param", sb.toString());

		StringBuilder param = new StringBuilder();
		boolean first = true;
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			String value = (String)entry.getValue();
			if (StrKit.isBlank(value)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				param.append("&");
			}
			param.append(entry.getKey()).append("=");
			param.append(value);
		}
		param.append(APP_KEY);

		String sign = HashKit.md5(param.toString());
		paramMap.put("sign", sign);
		String result = HttpUtil.post(APP_DOMAIN+"/sync/buyer", paramMap);
		System.out.println(result);
	}
	
	
	/**
	 * 同步买手
	 */
	public void test4Sms() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timestamp = df.format(new Date());
		String nonce = RandomUtil.randomString(6);  //6位随机字串
		
		Map<String, Object> paramMap = new TreeMap<String, Object>();
		paramMap.put("appid", APP_ID);
		paramMap.put("timestamp", timestamp);
		paramMap.put("nonce", nonce);
		paramMap.put("param", "13336033114");

		StringBuilder param = new StringBuilder();
		boolean first = true;
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			String value = (String)entry.getValue();
			if (StrKit.isBlank(value)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				param.append("&");
			}
			param.append(entry.getKey()).append("=");
			param.append(value);
		}
		param.append(APP_KEY);

		String sign = HashKit.md5(param.toString());
		paramMap.put("sign", sign);
		String result = HttpUtil.post(APP_DOMAIN+"/rsvp/sms/send", paramMap);
		System.out.println(result);
	}
}
