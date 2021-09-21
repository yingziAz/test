package com.mj.interceptor;

import java.util.Map;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.ext.kit.RetKit;
import com.mj.constant.CoreConstant;
/**
 * 前端验证
 * @author Daniel.Su
 */
public class LoginInterceptor implements Interceptor {

	@SuppressWarnings("unchecked")
	public void intercept(Invocation inv) {
		Controller ci = inv.getController();
		Map<String,Object> dataMap =(Map<String,Object>) ci.getSessionAttr(CoreConstant.LOGIN);
		if (dataMap ==null) {
			if(ci.getRequest().getHeader("x-requested-with")!=null 
					&& ci.getRequest().getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){ 
				ci.renderJson(RetKit.login());
			}else{
				ci.redirect("/login");
			}
		}else{
			inv.invoke();
		}
	}

}
