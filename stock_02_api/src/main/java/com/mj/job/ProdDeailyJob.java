package com.mj.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.product.BaseProductDeailyApi;
import com.mj.api.system.BatchJobLoggerApi;
import com.mj.kit.DateUtil;

/**  
 * @ClassName: OrderCloseJob
 * @Description: 每日商品跑批
 * @author lu
 * @date 2020-05-25 03:18:00 
*/  
public class ProdDeailyJob implements Job {

	private static final Integer JOB_ID = 1;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		doExecute(new Date());
	}

	@Before({ Tx.class })
	public void doExecute(Date date) {
		Date jobRunTime = new Date();
		if(date==null) {
			date=new Date();
		}
		String dateStr = DateUtil.dateToStr(date);
		String succFlg = "Y";
		String errorMsg = null;
		Integer saveNum = 0;
		Integer updateNum = 0;
		try {
			Record r = BaseProductDeailyApi.api.calculeToday(null, date);
			saveNum = r.getInt("saveNum");
			updateNum = r.getInt("updateNum");
		}catch (Exception e) {
			e.printStackTrace();
			succFlg = "N";
			errorMsg = "任务失败：" + e.getMessage();
			BatchJobLoggerApi.api.save(JOB_ID, jobRunTime, new Date(), succFlg, errorMsg);
		}
		BatchJobLoggerApi.api.save(JOB_ID, jobRunTime, new Date(), succFlg, "统计日期："+dateStr+"，新增"+saveNum+"条，更新"+updateNum+"条");
	}
}
