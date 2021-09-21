package com.mj.config;

import org.apache.commons.lang.StringUtils;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.mj.core.CoreConfig;
import com.mj.kit.EnvKit;
import com.mj.kit.encoder.Md5PwdEncoder;
import com.mj.kit.encoder.PwdEncoder;
import com.mj.plugin.GuicePlugin;

import net.dreamlu.event.EventPlugin;
import net.dreamlu.event.support.DuangBeanFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * API引导式配置
 */
public class ApiConfig extends CoreConfig {

	public void configConstant(Constants me){
		super.configConstant(me);
		
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		super.configPlugin(me);

		// 配置c3p0数据库连接池插件
		Prop prop = PropKit.use(EnvKit.get() + "/jdbc-api.properties");
		DruidPlugin druidPlugin = new DruidPlugin(prop.get("jdbcUrl"), prop.get("user"),
				StringUtils.trim(prop.get("password")),StringUtils.trim(prop.get("jdbcDriver")));
		if(JFinal.me().getConstants().getDevMode()) {
			// 初始连接池大小、最小空闲连接数、最大活跃连接数
			//private int initialSize = 10;
			//private int minIdle = 10;
			//private int maxActive = 100;
			druidPlugin.set(1,1, 1);
		}
		me.add(druidPlugin);
		
		// 配置ActiveRecord插件
		// 设置事务级别(默认事务级别是 2,如果有从数据库读取的操作也在事务中,需要将事务级别至少提升到 4)
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin, 2);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
//		//暂时
//		arp.addSqlTemplate("all.sql");
//		if(JFinal.me().getConstants().getDevMode()) {
//			arp.setShowSql(true);
//		}else {
//			arp.setShowSql(false);
//		}
		//进入
		me.add(arp);
		_MappingKit.mapping(arp);
		
		
		// 配置神奇的GUICE IOC组件
		GuicePlugin guicePlugin=new GuicePlugin();
		guicePlugin.bind(PwdEncoder.class, Md5PwdEncoder.class);
		me.add(guicePlugin);
		
		//事件驱动插件
		EventPlugin eventPlugin = new EventPlugin();
		// 设置为异步，默认同步，或者使用`threadPool(ExecutorService executorService)`自定义线程池。
		eventPlugin.async();
		eventPlugin.scanPackage("com.mj");
		// 对于将@EventListener写在不含无参构造器的类需要使用`ObjenesisBeanFactory`
		eventPlugin.beanFactory(new DuangBeanFactory());
		me.add(eventPlugin);
		
		//缓存微信小程序 redis,可以实现分布式缓存
		Prop redisProp = PropKit.use(EnvKit.get() + "/redis.properties");
		int redisTimeout = 300;
		
		int redisDB= 5;
		RedisPlugin hroCoreRedis = new RedisPlugin(RedisCacheConfig.STOCK_CORE_CACHE, redisProp.get("host"), redisProp.getInt("port"),redisTimeout,redisProp.get("pwd"),redisDB);
		if(true) {
			JedisPoolConfig config = hroCoreRedis.getJedisPoolConfig();
			config.setMaxTotal(200);
			config.setMaxIdle(100);
			config.setMinIdle(50);//设置最小空闲数
			config.setMaxWaitMillis(10000);
			config.setTestOnBorrow(true);
			config.setTestOnReturn(true);
			//Idle时进行连接扫描
			config.setTestWhileIdle(true);
			//表示idle object evitor两次扫描之间要sleep的毫秒数
			config.setTimeBetweenEvictionRunsMillis(30000);
			//表示idle object evitor每次扫描的最多的对象数
			config.setNumTestsPerEvictionRun(10);
			//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
			config.setMinEvictableIdleTimeMillis(60000);
		}
		me.add(hroCoreRedis);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		// 自定义标签过滤器
		super.configInterceptor(me);
	}

	/**
	 * Call back after JFinal start
	 */
	public void onStart(){
		super.onStart();
	};
	
	
}
