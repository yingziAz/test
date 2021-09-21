package com.mj.controller.system;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.json.JFinalJson;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.system.ExceptionLogApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.model.system.ExceptionLog;

public class ExceptionLogController extends BaseController{
	/**
	 * 首页
	 */
	public void index() {
		setAttr("nav_active", "exception");
		render("exception_log_maintain.html");
	}
	/**
	 * 列表
	 */
	public void list() {
		Integer offset = 0, pageSize = 10;
		try {
			offset = this.getParaToInt("offset", 0);
			pageSize = this.getParaToInt("limit", 10);
		} catch (ActionException e) {
			// do nothing
		}
		Integer pageNo = 1;
		if (offset > 0) {
			pageNo = (offset / pageSize) + 1;
		}
		Map<String, Object> cond = new HashMap<String, Object>();
		// 搜索
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order", "asc"));
		// 高级查询
		cond.put("filterValue", getPara("filterValue", StringUtils.EMPTY));
		
		
		Page<Record> page = ExceptionLogApi.api.page(pageNo, pageSize, cond);
		for (Record entity: page.getList()) {
			entity.set("handleFlg4Str", "Y".equals(entity.getStr("handle_flg")) ? "已修复" : "未修复");
		}
		renderJson(JFinalJson.getJson().toJson(page));
	}
	
	/**
	 * @Description:(左侧状态筛选)    
	 * @author shx
	 * @version 2019 年 09 月 25 日  13:40:46 
	 */
	public void loadFilterCond() {
		//电子合同服务是否开启筛选
		Ret ret = RetKit.ok();
		Record countRecord = ExceptionLogApi.api.countHandleFlgNum();
		Record temp = new Record();
				List<Record> exceptionList = Lists.newArrayList();
				temp = new Record();
				temp.set("name", "已修复");
				temp.set("value", "Y");
				temp.set("num", countRecord.get("repaired_num"));
				exceptionList.add(temp);
				
				temp = new Record();
				temp.set("name", "未修复");
				temp.set("value", "N");
				temp.set("num", countRecord.get("unrepaired_num"));
				exceptionList.add(temp);
				ret.set("exceptionList",exceptionList);
				ret.set("exception_total",countRecord.getInt("exception_total"));
				renderJson(ret);
				
		
	}
	/**
	 * 备注
	 */
	public void edit() {
		Integer id = getParaToInt("id");
		ExceptionLog entity = ExceptionLogApi.api.findById(id);
		setAttr("entity", entity);
		createFromToken();
		render("exception_log_edit.html");
		
	}
	/**
	* @Description:保存处理备注
	 * @author shx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	@Before({ FormTokenInterceptor.class, Tx.class })
	public void save() {
		ExceptionLog entity=getBean(ExceptionLog.class,"entity");
		Ret ret=ExceptionLogApi.api.saveOrUpdate(entity, getLoginUserRealName(),getLoginUserId());
		if (ret.isOk()) {
			sysLog(MessageFormat.format("保存{0}处理备注成功。", entity.getErrMsg()));
		}else {
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}

	/**
	 * 
	 * @Description:修复异常日志
	 * @author shx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	public void repair() {
		Integer id = getParaToInt("id");
		ExceptionLog entity = ExceptionLogApi.api.findById(id);
		Ret ret = ExceptionLogApi.api.repair(entity);
		if (ret.isOk()) {
			sysLog(MessageFormat.format("修复{0}成功。", entity.getErrMsg()));
		}
		renderJson(ret);
	}
	
	/**
	 * 删除
	 */
	public void remove() {
		Integer id = getParaToInt("id");
		ExceptionLog entity = ExceptionLogApi.api.findById(id);
		Ret ret = ExceptionLogApi.api.remove(id);
		sysLog(MessageFormat.format("删除系统错误日志{0}。", entity.getErrMsg()));
		renderJson(ret);
		
	}

}
