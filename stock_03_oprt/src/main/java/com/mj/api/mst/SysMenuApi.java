package com.mj.api.mst;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.CoreConstant;
import com.mj.model.system.Menu;

public class SysMenuApi {

	public static final SysMenuApi api = new SysMenuApi();

	public Menu findById(String id) {
		return Menu.dao.findById(id);
	}

	@SuppressWarnings("serial")
	private Map<String, String> sortConfigMap = new HashMap<String, String>() {
		{
			put("menu_name", "t.menu_name");
			put("menu_name_en", "t.menu_name_en");
		}
	};

	public Page<Record> page(int pageNumber, int pageSize, Map<String, Object> cond) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();

		String select = " SELECT t.*";
		sql.append(" FROM sys_menu t ");
		sql.append(" WHERE t.valid_flg='Y' AND t.platform_node='S' ");
		sql.append(" AND NOT EXISTS(SELECT 1 FROM sys_menu m WHERE m.id = t.father_id and m.valid_flg = 'N')");

		// 搜索
		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sql.append(" AND (INSTR(t.menu_name, ?) > 0 OR INSTR(t.menu_name_en, ?) > 0)");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}

		String orderField = StringUtils.trim((String) cond.get(CoreConstant.ORDER_FIELD));
		// 默认排序
		if (StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)) {
			sql.append(" ORDER BY ").append(sortConfigMap.get(orderField)).append(" ")
					.append(StringUtils.trim((String) cond.get(CoreConstant.ORDER_DIR)));
		} else {
			sql.append(" ORDER BY t.order_num ");
		}
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), queryArgs.toArray());
	}

	/**
	 * @Description:保存菜单
	 * @author shx
	 * @version 2020 年 03 月 27 日  13:31:38 
	 * @param entity
	 * @param loginUserName
	 * @return
	 */
	public Ret saveOrUpdate(Menu entity, String loginUserName) {
		Menu uEntity = findById(entity.getId());
		if (uEntity == null) {
			entity.setCreateDate(new Date());
			entity.setValidFlg("Y");
			entity.setCreator(loginUserName);
			entity.save();
		} else {
			entity.setUpdator(loginUserName);
			entity.setUpdateDate(new Date());
			entity.update();
		}
		return RetKit.ok();
	}

	public boolean isUinqueMenuName(String menuName, String id) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(1) from sys_menu t where t.menu_name=? ");
		queryArgs.add(menuName);
		if(id != null) {
			sql.append(" and t.id != ? ");
			queryArgs.add(id);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}
}
