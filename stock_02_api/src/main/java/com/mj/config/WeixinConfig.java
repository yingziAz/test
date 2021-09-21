package com.mj.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.mj.kit.EnvKit;

public class WeixinConfig {
	
	private static Map<String, String> map = Maps.newHashMap();
	
	protected static Map<String,String> getWxaConfig() {
		Prop prop = PropKit.use(EnvKit.get() + "/wechat.properties");
		String appid = prop.get("APP_ID");
		String appsecret = prop.get("APP_SECRET");
		Map<String, String> map = Maps.newHashMap();
		map.put("appid", appid);
		map.put("appsecret", appsecret);
		return map;
	}
	
	public static String getAppid(){
		if(map == null || StringUtils.isBlank(map.get("appid"))){
			map = getWxaConfig();
		}
		return map.get("appid");
	}
	
	public static String getAppsecret(){
		if(map == null || StringUtils.isBlank(map.get("appsecret"))){
			map = getWxaConfig();
		}
		return map.get("appsecret");
	}
	
}
