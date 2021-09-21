package com.mj.api.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.mj.constant.CoreConstant;
import com.mj.model.system.BatchJob;

/**
 * 跑批任务 Api
 * 
 * @author lu
 * 		
 */
public class BatchJobApi {

	public static final BatchJobApi api = new BatchJobApi();

	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("jobName", "t.job_name");
		put("jobDesc", "t.job_desc");
	}};
	
	
	/**
	 * 分页  add by lu 2016-12-16
	 * @param pageNumber 列数 
	 * @param pageSize   第几页
	 * @param cond	   条件
	 * @return
	 */
	public Page<BatchJob> page(int pageNumber, int pageSize, Map<String, Object> cond) {
		String select = "select t.*";
		StringBuilder sb = new StringBuilder();
		sb.append(" from sys_batch_job t where 1=1");

		List<Object> queryArgs = new ArrayList<Object>();
		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sb.append(" and (INSTR(t.job_name,?)>0 or INSTR(t.job_desc,?)>0) ");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}
		
		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sb.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sb.append(" order by t.id asc");
		}
		return  BatchJob.dao.paginate(pageNumber, pageSize, select, sb.toString(), queryArgs.toArray());
	}
	
}
