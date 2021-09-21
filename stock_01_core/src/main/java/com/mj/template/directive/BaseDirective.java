/*
 * 
 * 
 * 
 */
package com.mj.template.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mj.template.FreeMarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 模板指令 - 基类
 * 
 * 
 * 
 */
public abstract class BaseDirective implements TemplateDirectiveModel {
	private static String LANGUAGE_PARA_NAME = "language";
	//1表示中文 2表示英文
	private static Integer DEFAULT_LANGUAGE = 1;
	/**
	 * @Description: 获取语言
	 * @author zyf
	 * @version 2020年3月25日 下午1:37:47 
	 * @param params
	 * @return
	 * @throws TemplateModelException
	 */
	protected Integer getLanguage(Map<String, TemplateModel> params) throws TemplateModelException {
		return getParaValue(LANGUAGE_PARA_NAME,Integer.class, params,DEFAULT_LANGUAGE);
	}
	/**
	 * @Description: 获取参数
	 * @author zyf
	 * @version 2020年3月25日 上午9:46:48 
	 * @param name
	 * @param T
	 * @param params
	 * @return
	 * @throws TemplateModelException
	 */
	protected <T> T getParaValue(String name,Class<T> T,Map<String, TemplateModel> params) throws TemplateModelException {
		return FreeMarkerUtils.getParameter(name,T, params);
	}
	/**
	 * @Description: 有默认值
	 * @author zyf
	 * @version 2020年3月25日 上午9:54:06 
	 * @param name
	 * @param T
	 * @param params
	 * @param defaultValue
	 * @return
	 * @throws TemplateModelException
	 */
	protected <T> T getParaValue(String name,Class<T> T,Map<String, TemplateModel> params,T defaultValue) throws TemplateModelException {
		T value = getParaValue(name,T, params);
		return value == null ? defaultValue:value;
	}
	

	/**
	 * 设置局部变量
	 * 
	 * @param name
	 *            名称
	 * @param value
	 *            变量值
	 * @param env
	 *            环境变量
	 * @param body
	 *            模板内容
	 */
	protected void setLocalVariable(String name, Object value, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		TemplateModel preVariable = FreeMarkerUtils.getVariable(name, env);
		try {
			FreeMarkerUtils.setVariable(name, value, env);
			body.render(env.getOut());
		} finally {
			FreeMarkerUtils.setVariable(name, preVariable, env);
		}
	}

	/**
	 * 设置局部变量
	 * 
	 * @param variables
	 *            变量
	 * @param env
	 *            环境变量
	 * @param body
	 *            模板内容
	 */
	protected void setLocalVariables(Map<String, Object> variables, Environment env, TemplateDirectiveBody body) throws TemplateException, IOException {
		Map<String, Object> preVariables = new HashMap<String, Object>();
		for (String name : variables.keySet()) {
			TemplateModel preVariable = FreeMarkerUtils.getVariable(name, env);
			preVariables.put(name, preVariable);
		}
		try {
			FreeMarkerUtils.setVariables(variables, env);
			body.render(env.getOut());
		} finally {
			FreeMarkerUtils.setVariables(preVariables, env);
		}
	}
}