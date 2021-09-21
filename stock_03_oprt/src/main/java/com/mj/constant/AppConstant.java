package com.mj.constant;

import org.apache.commons.lang.StringUtils;

/**
 * Constant
 * 
 * @author frank.zong
 * 		
 */
public class AppConstant {
    
	//平台COOK前缀
	public final static String PLATFORM_COOKIE_PREFIX = "MJDMS_COOKIE";
	//微信相关
	public final static String OPENID_COOKIE = PLATFORM_COOKIE_PREFIX+"_OPENID";
	//cookie 生存时间（秒）
    public final static Integer COOKIE_LIVE_SECONDS  = 60*60*24*30;
    
    public static String WX_MP_DOMAIN = StringUtils.EMPTY;
    //datatable 过滤条件
    public static String LIST_COND_COOKIE = PLATFORM_COOKIE_PREFIX+"FILTER_COND";
    
    public static Integer LIST_COND_COOKIE_LIVE_SECONDS = 60*60*12;
}
