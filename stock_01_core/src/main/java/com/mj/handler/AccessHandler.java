package com.mj.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.handler.Handler;

/**
 * 过滤访问静态资源
 * 
 * @author frank
 * 		
 */
public class AccessHandler extends Handler {
	
	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		// 如果直接访问静态页面,则跳转到除去.html字段的路径上去,例:/login.html跳转到/login
		int index = target.lastIndexOf(".html");
		if (index != -1){
			if(!StringUtils.contains(target, "processEditor")){
				target = target.substring(0, index);
			}
		}
		next.handle(target, request, response, isHandled);
	}
	
}
