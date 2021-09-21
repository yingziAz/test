package com.mj.core;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.mj.controller.common.UeditorApiController;
import com.mj.handler.AccessHandler;

/**
 * API引导式配置
 */
public class CoreConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setJsonFactory(new FastJsonFactory());
		me.setViewType(ViewType.FREE_MARKER);

		
	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		
		//百度ueditor
		me.add("/ueditor/api", UeditorApiController.class);
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
	
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	
	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		// 过滤访问
		me.add(new AccessHandler());
	}
	

	
	/**
	 * Call back after JFinal start
	 */
	public void onStart(){
		//ModelRecordElResolver.setResolveBeanAsModel(true);
	}

	@Override
	public void configEngine(Engine me) {
		// TODO Auto-generated method stub
		
	};
}
