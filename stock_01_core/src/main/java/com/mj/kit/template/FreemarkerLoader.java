package com.mj.kit.template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jfinal.kit.PathKit;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Created by ice on 14-11-13.
 */
public class FreemarkerLoader {

	/**
	 * 邮件模板的存放位置
	 */
	//  private String templatePath = "template/";
	/**
	 * 模板引擎配置
	 */
	private Configuration configuration;
	/**
	 * 参数
	 */
	private Map<Object, Object> parameters = new HashMap<Object, Object>();

	private String file = "";

	public FreemarkerLoader(String file) {
		this("", file);
	}

	public FreemarkerLoader(String templatePath, String file) {
		this.file = file;
		configuration = new Configuration();
		/**
		 * 模板加载位置
		 */
		List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();
		try {
			Enumeration<URL> resources = FreemarkerLoader.class.getClassLoader().getResources(templatePath);
			URL resource = null;
			while (resources.hasMoreElements()) {
				resource = resources.nextElement();
				loaders.add(new FileTemplateLoader(new File(URLDecoder.decode(resource.getFile(),"utf-8"))));
			}
			File webDir = new File(PathKit.getWebRootPath() + File.separator + templatePath);
			if (webDir.exists())
				loaders.add(new FileTemplateLoader(webDir));
			TemplateLoader[] templateLoaders = new TemplateLoader[loaders.size()];
			configuration.setTemplateLoader(new MultiTemplateLoader(loaders.toArray(templateLoaders)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		configuration.setEncoding(Locale.getDefault(), "UTF-8");
		configuration.setDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public FreemarkerLoader setEncoding(String encoding) {
		configuration.setEncoding(Locale.getDefault(), encoding);
		return this;
	}


	public FreemarkerLoader setDateFormat(String datefmt) {
		configuration.setDateFormat(datefmt);
		return this;
	}


	public FreemarkerLoader setValue(String attr, Object value) {
		parameters.put(attr, value);
		return this;
	}

	public String getHtml() {
		try {
			Template template = configuration.getTemplate(file);
			StringWriter stringWriter = new StringWriter();
			template.process(parameters, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String a[]){
	} 
}