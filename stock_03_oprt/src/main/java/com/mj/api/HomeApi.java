package com.mj.api;


import com.jfinal.plugin.activerecord.Db;

public class HomeApi {

	public static final HomeApi api = new HomeApi();
	
	
	/**
	 * @Description: 统计未修复的错误日志数量
	 * @author yang
	 * @version 2019 年 10 月 15 日  11:02:13 
	 * @return
	 */
	public Integer countException() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COUNT(t.id) ");
		sql.append("   FROM sys_exception_log t");
		sql.append(" WHERE t.handle_flg = 'N' ");
		return Db.queryLong(sql.toString()).intValue();
	}
	

}
