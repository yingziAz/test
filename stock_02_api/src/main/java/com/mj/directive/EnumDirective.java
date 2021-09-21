package com.mj.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joor.Reflect;

import com.mj.core.CoreEnum;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 枚举自定义标签
 * 
 * @author frank.zong
 * 		
 */
public class EnumDirective implements TemplateDirectiveModel {
	
	private static final Map<String, Reflect> REFLECTS = new HashMap<String, Reflect>();
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		String className = params.get("className").toString();
		int ordinal = Integer.valueOf(params.get("ordinal").toString());
		
		// 添加进缓存中,加快访问
		if (REFLECTS.get(className) == null) {
			REFLECTS.put(className, Reflect.on("com.mj." + className));
		}
		
		// 解析枚举
		CoreEnum ce = REFLECTS.get(className).call("valueOf", ordinal).get();
		env.setVariable("enum",DefaultObjectWrapper.getDefaultInstance().wrap(ce));
		body.render(env.getOut());
	}
}
