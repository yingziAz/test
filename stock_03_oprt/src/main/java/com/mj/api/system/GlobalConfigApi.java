package com.mj.api.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.mj.api.system.BaseGlobalConfigApi;

import com.jfinal.plugin.activerecord.Page;
import com.mj.constant.CoreConstant;
import com.mj.model.system.GlobalConfig;

/**

 * 全局参数配置
 * @author daniel.su
 * 		
 */
public class GlobalConfigApi  extends BaseGlobalConfigApi{

	public static final GlobalConfigApi api = new GlobalConfigApi();

	/**
	 * 排序
	 */
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("paramName", "t.param_name");
		put("paramValue", "t.param_value");
		put("createDate4Str", "t.create_date");
		put("updateDate4Str", "t.update_date");
		put("orderNum", "t.order_num");
	}};


	/**
	 * 分页
	 */
	public Page<GlobalConfig> page(int pageNumber, int pageSize,Map<String,Object> cond) {
		String select = "select t.*";
		StringBuilder sb = new StringBuilder();
		sb.append(" from sys_global_config t where 1=1 ");

		List<Object> queryArgs = new ArrayList<Object>();

		String corpId = (String) cond.get("corpId");
		if(StringUtils.isNotBlank(corpId)) {
			sb.append(" and t.corp_id =?  ");
			queryArgs.add(corpId);
		}else {
			sb.append(" and t.corp_id is null");
		}

		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sb.append(" and (INSTR(t.param_name,?) > 0 or INSTR(t.param_value,?) > 0 or INSTR(t.param_desc,?) > 0)");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}

		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sb.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sb.append(" order by t.corp_id desc,t.order_num");
		}
		return GlobalConfig.dao.paginate(pageNumber, pageSize, select, sb.toString(),queryArgs.toArray());
	}
}
