package com.mj.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.mj.directive.EnumDirective;

/**
 * 自定义标签拦截器
 * 
 * @author frank.zong
 */
public class DirectiveInterceptor implements Interceptor {


	private static EnumDirective enumDirective = new EnumDirective();

	private static final String ENUM = "_enum";

	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.setAttr(ENUM, enumDirective);

		inv.invoke();
	}
}
