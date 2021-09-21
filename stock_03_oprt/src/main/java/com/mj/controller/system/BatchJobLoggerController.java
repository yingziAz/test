package com.mj.controller.system;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionException;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.api.system.BatchJobApi;
import com.mj.api.system.BatchJobLoggerApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.ErrorInterceptor;
import com.mj.interceptor.LoginInterceptor;
import com.mj.interceptor.SessionInViewInterceptor;
import com.mj.job.ProdDeailyJob;
import com.mj.kit.DateUtil;
import com.mj.model.system.BatchJob;

/**
 * 跑批日志Controller
 * @author zhiwei.lin
 */

@Clear
@Before({ ErrorInterceptor.class, LoginInterceptor.class, SessionInViewInterceptor.class })
public class BatchJobLoggerController extends BaseController {

	public void index() {
		//设置页面的NAV 的 active 效果
		this.setAttr("nav_active", "batch_job");
		render("batch_job_log_maintain.html");
	}

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

		Page<Record> page = BatchJobLoggerApi.api.page(pageNo, pageSize, cond);
		for(Record record :page.getList()) {
			record.set("jobRunTime4Str", DateUtil.dateTimeToStr(record.getDate("job_run_time")));
			record.set("jobFinishTime4Str", DateUtil.dateTimeToStr(record.getDate("job_finish_time")));
			record.set("succFlgDisp", StringUtils.equals(record.get("succ_flg"), "Y")?"成功":"失败");
		}
		renderJson(page);
	}

	/**
	 * 跑批任务页面 add by lu 2016-12-16
	 */
	public void index4Quartz() {
		render("batch_job_maintain.html");
	}

	/**
	 * 跑批任务列表 add by lu 2016-12-16
	 */
	@Clear
	public void list4Quartz() {
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

		Page<BatchJob> page = BatchJobApi.api.page(pageNo, pageSize, cond);
		renderJson(page);
		return;
	}

	/**
	 * 执行跑批任务 add by lu 2016-12-16
	 */
	public void batchQuartz() {
		Integer id = getParaToInt("id");
		if (id == 1) {// 每日商品跑批
			new ProdDeailyJob().doExecute(DateUtil.plusDate(new Date(), -1));
			new ProdDeailyJob().doExecute(new Date());
		}
		renderJson(Ret.ok());
	}
}
