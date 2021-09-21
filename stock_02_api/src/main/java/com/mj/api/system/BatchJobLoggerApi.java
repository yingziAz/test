package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.CoreConstant;
import com.mj.kit.DateUtil;
import com.mj.model.system.BatchJobLogger;

public class BatchJobLoggerApi {
	public static final BatchJobLoggerApi api = new BatchJobLoggerApi();

	@SuppressWarnings("serial")
	private static Map<String, String> sortConfigMap = new HashMap<String, String>() {
		{
			put("job_name", "bj.job_name");
			put("jobRunTime4Str", "t.job_run_time");
			put("jobFinishTime4Str", "t.job_finish_time");
			put("succFlgDisp", "t.succ_flg");
			put("error_msg", "t.error_msg");
		}
	};

	/**
	 * @Description: TODO(分页)    
	 * @author lu
	 * @version 2018年4月20日 上午9:56:10 
	 * @param pageNumber
	 * @param pageSize
	 * @param cond
	 * @return
	 */
	public Page<Record> page(int pageNumber, int pageSize, Map<String, Object> cond) {
		String select = "select t.*,bj.job_name";
		StringBuilder sb = new StringBuilder();
		sb.append(" from sys_batch_job_logger t inner join sys_batch_job bj  on t.job_id = bj.id where 1=1");

		List<Object> queryArgs = new ArrayList<Object>();
		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sb.append(" and INSTR(bj.job_name,?)>0 ");
			queryArgs.add(keyword);
		}

		// 跑批开始时间
		Date jobRunTimeStart = (Date) cond.get("jobRunTimeStart");
		if (jobRunTimeStart != null) {
			sb.append(" and t.job_run_time >= ?");
			queryArgs.add(jobRunTimeStart);
		}
		Date jobRunTimeEnd = (Date) cond.get("jobRunTimeEnd");
		if (jobRunTimeEnd != null) {
			sb.append(" and t.job_run_time < ?");
			queryArgs.add(DateUtil.plusDate(jobRunTimeEnd, 1));
		}
		// 跑批完成时间
		Date jobFinishTimeStart = (Date) cond.get("jobFinishTimeStart");
		if (jobFinishTimeStart != null) {
			sb.append(" and t.job_finish_time >= ?");
			queryArgs.add(jobFinishTimeStart);
		}
		Date jobFinishTimeEnd = (Date) cond.get("jobFinishTimeEnd");
		if (jobFinishTimeEnd != null) {
			sb.append(" and t.job_finish_time < ?");
			queryArgs.add(DateUtil.plusDate(jobFinishTimeEnd, 1));
		}
		// 跑批结果
		String succFlg = StringUtils.trim((String) cond.get("succFlg"));
		if (StringUtils.isNotBlank(succFlg)) {
			sb.append(" and t.succ_flg = ?");
			queryArgs.add(succFlg);
		}

		String orderField = StringUtils.trim((String) cond.get(CoreConstant.ORDER_FIELD));
		// 默认排序
		if (StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)) {
			sb.append(" order by ").append(sortConfigMap.get(orderField)).append(" ")
					.append(StringUtils.trim((String) cond.get(CoreConstant.ORDER_DIR)));
		} else {
			sb.append(" order by t.id desc");
		}
		return Db.paginate(pageNumber, pageSize, select, sb.toString(), queryArgs.toArray());
	}

	/**
	 * @Description: TODO(保存跑批)    
	 * @author lu
	 * @version 2018年4月20日 上午10:07:51 
	 * @param jobId
	 * @param jobRunTime
	 * @param jobFinishTime
	 * @param succFlg
	 * @param errorMsg
	 * @return
	 */
	public Integer save(Integer jobId, Date jobRunTime, Date jobFinishTime, String succFlg, String errorMsg) {
		BatchJobLogger entity = new BatchJobLogger();
		entity.setJobId(jobId);
		entity.setJobRunTime(jobRunTime);
		entity.setJobFinishTime(jobFinishTime);
		entity.setSuccFlg(succFlg);
		entity.setErrorMsg(errorMsg);
		entity.save();
		return entity.getId();
	}

}
