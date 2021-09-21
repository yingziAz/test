package com.mj.api.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.CoreConstant;

public class LogApi extends BaseLogApi {
	
	public static final LogApi api = new LogApi();

	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("executive", "t.executive");
		put("action", "t.action");
		put("ip", "t.ip");
		put("logTime4Str", "t.log_time");
		put("log_time", "t.log_time");
	}};
	
	
	/**
	 * @Description: TODO(日志列表)    
	 * @author fb
	 * @version 2018 年 08 月 17 日  15:49:24 
	 * @param pageNumber
	 * @param pageSize
	 * @param cond
	 * @return
	 */
	public Page<Record> page(int pageNumber, int pageSize,Map<String,Object> cond) {
		String select = "select t.*";
		StringBuilder sql = new StringBuilder();
		sql.append(" from sys_log t where  1=1");

		List<Object> queryArgs = new ArrayList<Object>();
		
		
		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (INSTR(t.executive,?) > 0 ");
			sql.append("  or INSTR(t.action,?) > 0) ");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}
		//高级查询
		//用户名称
		String userName = (String)cond.get("loginUserName");
		if(StringUtils.isNotEmpty(userName)){
			sql.append(" and INSTR(t.executive,?) > 0 ");
			queryArgs.add(userName);
		}
		//日志类型
		Integer logType = (Integer)cond.get("logType");
		if(logType != null && logType != 0){
			sql.append(" and t.log_type = ? ");
			queryArgs.add(logType);
		}
		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sql.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sql.append(" order by t.log_time desc");
		}
		return Db.paginate(pageNumber, pageSize, select, sql.toString(),queryArgs.toArray());
	}

}
