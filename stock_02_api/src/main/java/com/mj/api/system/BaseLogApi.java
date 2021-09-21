package com.mj.api.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.mj.model.system.Log;

public class BaseLogApi {
	public static final BaseLogApi api = new BaseLogApi();
	
	@SuppressWarnings({ "serial", "unused" })
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("executive", "t.executive");
		put("action", "t.action");
		put("ip", "t.ip");
		put("logTime4Str", "t.log_time");
	}};

	/**
	 * 
	 */
	public List<Log> findAll(String corpId) {
		return Log.dao.find("select t.* from sys_log t where t.corp_id=? ",corpId);
	}

	
	/**
	 * 保存日志信息
	 * @param entity
	 * @return
	 */
	public Ret log(Log entity) {
		entity.setLogTime(new Date());
		boolean result = entity.save();
		return result ? RetKit.ok() :RetKit.fail("保存日志失败");
	}

}
