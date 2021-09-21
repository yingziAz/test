package com.mj.api.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.CoreConstant;
import com.mj.enums.UserTypeEnum;

/**
 * 系统角色  Api
 * 
 * @author daniel.su
 * 		
 */
public class RoleApi extends BaseRoleApi {
	
	public static final RoleApi api = new RoleApi();
	
	//排序
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("role_name","t.role_name");  
		put("description","t.description");  
		put("order_num","t.order_num");  
		put("module_str","t.module");  //1-pc. 2-app
	}};
	
	public Page<Record> page(int pageNumber, int pageSize,Map<String,Object> cond) {
		List<Object> queryArgs = new ArrayList<Object>();
		String select = "SELECT t.* ";
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM sys_role t ");
		sql.append(" WHERE 1=1 ");
		
		//关键字
		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (INSTR(t.role_name,?) > 0 ");
			sql.append("  or INSTR(t.description,?) > 0) ");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}

		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sql.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sql.append(" order by t.valid_flg desc,t.order_num");
		}
		return Db.paginate(pageNumber, pageSize, select, sql.toString(),queryArgs.toArray());
	}
	
	/**
	 * 
	 * @Description:获得角色用户人数
	 * @author pmx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	public List<Record> listEftvWithUserCount(Map<String, Object> cond){
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("select r.id,r.role_name,ifnull(st.user_cnt,0) as user_cnt");
		sql.append("  from sys_role r");
		sql.append("    left join (select role_id,count(t.id) as user_cnt");
		sql.append("                 from sys_user_role t");
		sql.append("                 INNER JOIN sys_user u ON t.user_id = u.id AND u.user_type = ? ");
		//业务系统用户
		queryArgs.add(UserTypeEnum.OPRT_USER.getOrdinal());
		sql.append("                group by t.role_id) st on r.id = st.role_id");
		sql.append(" where 1=1");
		sql.append("   and r.template_flg='N'");
		sql.append("   and r.valid_flg='Y'");
		
		String filterKeyword = (String) cond.get("filterKeyword");
		if (StringUtils.isNotEmpty(filterKeyword)) {
			sql.append(" and INSTR(r.role_name,?) > 0");
			queryArgs.add(filterKeyword);
		}
		
		sql.append(" order by r.order_num asc, r.role_name");
		return Db.find(sql.toString(),queryArgs.toArray());
	}
	
}
