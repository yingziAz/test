package com.mj.api.mst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.mj.api.system.BaseRoleApi;
import com.mj.constant.CoreConstant;
import com.mj.model.system.Role;

/**
 * 角色模板  Api
 * 
 * @author pmx
 * 		
 */
public class RoleTmplApi extends BaseRoleApi {
	
	public static final RoleTmplApi api = new RoleTmplApi();
	
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("roleName", "t.role_name");
		put("createDate4Str", "t.create_date");
		put("updateDate4Str", "t.update_date");
		put("orderNum", "t.order_num");
	}};

	public Page<Role> page(int pageNumber, int pageSize,Map<String,Object> cond) {
		String select = "select t.*";
		StringBuilder sql = new StringBuilder();
		sql.append("  from sys_role t ");
		sql.append(" where 1=1 and t.template_flg = 'Y' ");

		List<Object> queryArgs = new ArrayList<Object>();
		sql.append(" and t.corp_id is null");

		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (INSTR(t.role_name,?)) > 0");
			queryArgs.add(keyword);
		}
		String roleName = (String)cond.get("roleName");
		if(StringUtils.isNotEmpty(roleName)){
			sql.append(" and INSTR(t.role_name,?)) > 0");
			queryArgs.add(roleName);
		}
		
		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sql.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sql.append(" order by t.valid_flg desc,t.order_num,t.create_date");
		}
		return Role.dao.paginate(pageNumber, pageSize, select, sql.toString(),queryArgs.toArray());
	}
}
