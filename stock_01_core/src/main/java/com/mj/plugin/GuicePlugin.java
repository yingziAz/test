package com.mj.plugin;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jfinal.plugin.IPlugin;
/**
 * Guice IOC plugin
 * @author xwalker <br/> http://my.oschina.net/imhoodoo
 */

public class GuicePlugin implements IPlugin {
    
	private static Injector guice;
	//绑定注入的map
	@SuppressWarnings("rawtypes")
	private HashMap<Class, Class> bindMap;
	/**
	 * 默认构造函数 初始化绑定注入map
	 */
	@SuppressWarnings("rawtypes")
	public GuicePlugin() {
		bindMap = new HashMap<Class, Class>();
	}
	/**
	 * 绑定依赖
	 * @param bindSrc
	 * @param bindTo
	 */
	public void bind(Class<?> bindSrc, Class<?> bindTo) {
		bindMap.put(bindSrc, bindTo);
	}
	/**
	 * 封装guice中的getInstance
	 * @param clazz
	 * @return
	 */
	public static <T> T getInstance(Class<T> clazz){
		return guice.getInstance(clazz);
	}

	@Override
	public boolean start() {
		guice = Guice.createInjector(new Module() {
			@SuppressWarnings("unchecked")
			@Override
			public void configure(Binder binder) {
				for (@SuppressWarnings("rawtypes") Entry<Class, Class> entry : bindMap.entrySet()) {
					binder.bind(entry.getKey()).to(entry.getValue());
				}
			}
		});
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
}