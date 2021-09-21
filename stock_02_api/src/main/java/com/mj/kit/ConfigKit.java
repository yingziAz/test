package com.mj.kit;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * @Description:操作config.properties
 * @author lu
 * @version 2019 年 08 月 08 日  09:52:56 
 */
public class ConfigKit{
	
	public static ConfigKit api = new ConfigKit(); 
	
	private static String COS_DOMAIN = StringUtils.EMPTY;
	private static String SELLER_DOMAIN = StringUtils.EMPTY;
	private static String BUYER_MOBILE_DOMAIN = StringUtils.EMPTY;
	private static String BUYER_PC_DOMAIN = StringUtils.EMPTY;
	private static String REGISTER_DOMAIN = StringUtils.EMPTY;
	private static String BO_DOMAIN = StringUtils.EMPTY;
	private static String RSVP_DOMAIN = StringUtils.EMPTY;
	private static String WECHAT_KEY_FILE = StringUtils.EMPTY;
	
	/**
	 * @Description:获取云端路径
	 * @author lu
	 * @version 2019 年 08 月 08 日  10:12:38 
	 * @return
	 */
	public String getCosDomain() {
		if(StringUtils.isEmpty(COS_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/qcloud-cos.properties");
			COS_DOMAIN = MessageFormat.format("https://{0}.cos.{1}.myqcloud.com", prop.get("bucket_name"),prop.get("region"));
		}
		return COS_DOMAIN;
	}
	
	/**
	 * @Description:获取阿里云路径路径
	 * @author lu
	 * @version 2020 年 03 月 17 日  10:18:08 
	 * @return
	 */
	public String getAliCosDomain() {
		if(StringUtils.isEmpty(COS_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/aliyun-oss.properties");
			COS_DOMAIN = MessageFormat.format("https://{0}.{1}.aliyuncs.com/", prop.get("bucket_name"),prop.get("region"));
		}
		return COS_DOMAIN;
	}
	
	/**
	 * @Description:获取买家h5端
	 * @author Hova
	 * @version 2020 年 04 月 17 日  10:17:28 
	 * @return
	 */
	public String getSellerDomain() {
		if(StringUtils.isEmpty(SELLER_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			SELLER_DOMAIN = prop.get("seller_domain");
		}
		return SELLER_DOMAIN;
	}
	
	/**
	 * @Description:获取买家PC端
	 * @author Hova
	 * @version 2020 年 04 月 17 日  10:17:28 
	 * @return
	 */
	public String getBuyerPCDomain() {
		if(StringUtils.isEmpty(BUYER_PC_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			BUYER_PC_DOMAIN = prop.get("buyer_pc_domain");
		}
		return BUYER_PC_DOMAIN;
	}
	
	/**  
	 * @Title: getBoDomain
	 * @Description: 获取运营端
	 * @return
	 * @author lu
	 * @date 2020-07-23 04:02:20 
	 */
	public String getBoDomain() {
		if(StringUtils.isEmpty(BO_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			BO_DOMAIN = prop.get("bo_domain");
		}
		return BO_DOMAIN;
	}
	
	/**
	 * @Description:获取买家h5端
	 * @author Hova
	 * @version 2020 年 04 月 17 日  10:17:28 
	 * @return
	 */
	public String getBuyerMobileDomain() {
		if(StringUtils.isEmpty(BUYER_MOBILE_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			BUYER_MOBILE_DOMAIN = prop.get("buyer_mobile_domain");
		}
		return BUYER_MOBILE_DOMAIN;
	}
	
	/**  
	 * @Title: getRegisterDomain
	 * @Description: 获取注册路径
	 * @return
	 * @author lu
	 * @date 2020-07-22 01:11:09 
	 */
	public String getRegisterDomain() {
		if(StringUtils.isEmpty(REGISTER_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			REGISTER_DOMAIN = prop.get("register_domain");
		}
		return REGISTER_DOMAIN;
	}
	
	/**  
	 * @Title: getRsvpDomain
	 * @Description: 获取同步路径
	 * @return
	 * @author lu
	 * @date 2020-07-27 09:11:34 
	 */
	public String getRsvpDomain() {
		if(StringUtils.isEmpty(RSVP_DOMAIN)) {
			Prop prop = PropKit.use(EnvKit.get() + "/config.properties");
			RSVP_DOMAIN = prop.get("rsvp_domain");
		}
		return RSVP_DOMAIN;
	}
	
	/**  
	 * @Title: getWechatKeyFile
	 * @Description: 获取微信私钥
	 * @return
	 * @author lu
	 * @date 2020-09-21 02:25:31 
	 */
	public String getWechatKeyFile() {
		if(StringUtils.isEmpty(WECHAT_KEY_FILE)) {
			WECHAT_KEY_FILE = EnvKit.get() + "/apiclient_cert.pem";
		}
		return WECHAT_KEY_FILE;
	}
	
}
