package com.jfinal.ext.kit;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.Ret;
import com.mj.constant.CoreConstant;

/**
 * 多个结果工具类
 * 
 * @author frank.zong
 * 		
 */
@SuppressWarnings("serial")
public class RetKit implements Serializable {

	public static Ret ok() {
		return Ret.ok();
	}

	@SuppressWarnings("unchecked")
	public static Ret ok(Object data) {
		Ret ret = Ret.ok();
		if(data != null){
			ret.put(CoreConstant.RET_DATA, data);
		}
		return ret;
	}

	public static Ret fail() {
		return Ret.fail();

	}

	@SuppressWarnings("unchecked")
	public static Ret fail(String errorMsg) {
		Ret ret = Ret.fail();
		if(StringUtils.isNotEmpty(errorMsg)){
			ret.put(CoreConstant.RET_MSG, errorMsg);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Ret fail(String errorMsg,Object data) {
		Ret ret = Ret.fail();
		if(StringUtils.isNotEmpty(errorMsg)){
			ret.put(CoreConstant.RET_MSG, errorMsg);
		}
		if(data != null ){
			ret.put(CoreConstant.RET_DATA, data);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Ret login() {
		Ret ret =  Ret.create();
		ret.put("state", CoreConstant.LOGIN);
		ret.put(CoreConstant.RET_MSG_LOGIN, CoreConstant.LOGIN);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static Ret token() {
		Ret ret =  Ret.create();
		ret.put("state", CoreConstant.TOKEN);
		ret.put(CoreConstant.RET_MSG_TOKEN, CoreConstant.TOKEN);
		return ret;
	}


	public static Object getData(Ret ret) {
		return ret.get(CoreConstant.RET_DATA);
	}

}
