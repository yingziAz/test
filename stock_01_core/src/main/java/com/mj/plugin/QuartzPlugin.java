package com.mj.plugin;

import java.io.File;
import java.util.Enumeration;

import org.joor.Reflect;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.mj.kit.EnvKit;

/**
 * Quartz插件
 * 
 * @author frank
 * 		
 */
public class QuartzPlugin implements IPlugin {

	private static final String JOB = "job";

	private Scheduler sched;

	private String configName = "quartz.properties";

	public boolean start() {
		Prop prop = PropKit.use(EnvKit.get() + File.separator +configName);
		if (prop == null) {
			LogKit.error("quartz.properties is null");
			return false;
		}
		try {
			// 创建一个调度器
			sched = StdSchedulerFactory.getDefaultScheduler();
			// 启动调度器
			sched.start();
			Enumeration<Object> enums = prop.getProperties().keys();
			while (enums.hasMoreElements()) {
				String key = enums.nextElement() + "";
				if (!key.endsWith(JOB) || !isEnableJob(prop,enable(key))) {
					continue;
				}
				String jobClassName = prop.get(key)+ "";
				String jobCronExp = prop.get(cronKey(key)) + "";
				// 反射查找执行类
				Class<? extends Job> clazz = Reflect.on(jobClassName).get();
				// 配置调度、触发器
				JobDetail detail = JobBuilder.newJob(clazz).build();
				CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(jobCronExp);
				Trigger trigger = TriggerBuilder.newTrigger().withSchedule(builder).build();
				// 交给scheduler去调度
				sched.scheduleJob(detail, trigger);
			}
		} catch (SchedulerException e) {
			LogKit.error("启动quartz插件失败,原因:" + e.getMessage());
			return false;
		}
		LogKit.info("启动quartz插件成功");
		return true;
	}

	public boolean stop() {
		try {
			sched.shutdown();
		} catch (SchedulerException e) {
			LogKit.error("关闭quartz插件失败,原因:" + e.getMessage());
			return false;
		}
		return true;
	}



	private String enable(String key) {
		return key.substring(0, key.lastIndexOf(JOB)) + "enable";
	}

	private String cronKey(String key) {
		return key.substring(0, key.lastIndexOf(JOB)) + "cron";
	}

	private boolean isEnableJob(Prop prod,String enableKey) {
		String enable = prod.get(enableKey);
		if (enable != null && "false".equalsIgnoreCase((enable + "").trim())) {
			return false;
		}
		return true;
	}
}
