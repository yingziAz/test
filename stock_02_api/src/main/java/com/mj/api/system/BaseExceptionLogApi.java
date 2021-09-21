package com.mj.api.system;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.model.system.ExceptionLog;

public class BaseExceptionLogApi {
	
	public static final BaseExceptionLogApi api = new BaseExceptionLogApi();
	
	public ExceptionLog findById(Integer id) {
		return ExceptionLog.dao.findById(id);
	}

	
	/**
	 * @Description:获取所有
	 * @author shx
	 * @version 2019年9月16日 下午5:05:30
	 * @return
	 */
	public List<Record> listAll() {
		String sql = "select t.* from sys_exception_log t ";
		return Db.find(sql);
	}

}
