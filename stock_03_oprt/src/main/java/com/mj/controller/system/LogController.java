package com.mj.controller.system;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionException;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.api.system.LogApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.enums.LogTypeEnum;
import com.mj.kit.DateUtil;

public class LogController extends BaseController  {

	public void index(){
		//设置页面的NAV 的 active 效果
		this.setAttr("nav_active", "log");
		render("log.html");
	}
	
	/*
	 * 列表
	 */
	public void list(){
		Integer offset = 0,pageSize = 25;
		try{
			offset = this.getParaToInt("offset",0);
			pageSize = this.getParaToInt("limit",25);
		}catch(ActionException e){
			//do nothing 
		}
		Integer pageNo =1;
		if(offset > 0){
			pageNo = (offset/pageSize)+1;
		}
		Map<String,Object> cond = new HashMap<String,Object>();
		//搜索
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search",StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort",StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order","asc"));
		//高级查询
		cond.put("logType", LogTypeEnum.PLATFORM_OPRT.getOrdinal());
		Page<Record> page = LogApi.api.page(pageNo, pageSize, cond);
		for(Record record:page.getList()) {
			record.set("logTime4Str", DateUtil.dateTimeToStr(record.getDate("log_time")));
		}
		renderJson(page);
	}
}
