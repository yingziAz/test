package com.mj.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mj.core.CoreEnum;

/**
 * 日志类型
 * 1-运营端，2-商家端,3-买手pc端 4-买手手机端 5-APP端 6 小程序端
 * @author daniel.su
 * 		
 */
@SuppressWarnings("serial")
public class LogTypeEnum implements Serializable {
	
	public static final CoreEnum PLATFORM_OPRT = new CoreEnum(1, "PLATFORM_OPRT", "运营端");
	public static final CoreEnum PLATFORM_SELLER  = new CoreEnum(2, "PLATFORM_SELLER", "商家端");
	public static final CoreEnum PLATFOTM_BUYER_PC  = new CoreEnum(3, "PLATFOTM_BUYER_PC", "买手PC端"); //Customer Self-help Service Platform
	public static final CoreEnum PLATFORM_BUYER_MOBILE  = new CoreEnum(4, "PLATFORM_BUYER_MOBILE", "买手手机端"); //HRO_EMP Self service plat
	public static final CoreEnum PLATFORM_APP  = new CoreEnum(5, "PLATFORM_APP", "APP端");
	public static final CoreEnum PLATFORM_WXAAPI  = new CoreEnum(6, "PLATFORM_WXAAPI", "小程序端");

	public static List<CoreEnum> list = list();

	public static CoreEnum valueOf(Integer ordinal) {
		if (ordinal == null) {
			return null;
		}
		switch (ordinal) {
		case 1:
			return PLATFORM_OPRT;
		case 2:
			return PLATFORM_SELLER;
		case 3:
			return PLATFOTM_BUYER_PC;
		case 4:
			return PLATFORM_BUYER_MOBILE;
		case 5:
			return PLATFORM_APP;
		case 6:
			return PLATFORM_WXAAPI;
		default:
			return null;
		}
	}

	public static String getDisp(Integer ordinal){
		if(ordinal == null) {
			return StringUtils.EMPTY;
		}
		switch (ordinal) {
		case 1:
			return (String)PLATFORM_OPRT.getValue();
		case 2:
			return (String)PLATFORM_SELLER.getValue();
		case 3:
			return (String)PLATFOTM_BUYER_PC.getValue();
		case 4:
			return (String)PLATFORM_BUYER_MOBILE.getValue();
		case 5:
			return (String)PLATFORM_APP.getValue();
		case 6:
			return (String)PLATFORM_WXAAPI.getValue();
		default:
			return StringUtils.EMPTY;
		}
	}

	//--------------------------------------内部方法---------------------------------
	private static List<CoreEnum> list() {
		if(list == null){
			list = new ArrayList<CoreEnum>();
			list.add(PLATFORM_OPRT);
			list.add(PLATFORM_SELLER);
			list.add(PLATFOTM_BUYER_PC);
			list.add(PLATFORM_BUYER_MOBILE);
			list.add(PLATFORM_APP);
			list.add(PLATFORM_WXAAPI);
		}
		return list;
	}
}
