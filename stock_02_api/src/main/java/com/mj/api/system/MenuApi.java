package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.ResponseFail;
import com.mj.enums.UserTypeEnum;
import com.mj.model.system.Menu;
import com.mj.model.system.User;

/**
 * 菜单体系 Api
 * 
 * @author daniel.su
 * 
 */
public class MenuApi {
	
	public static final MenuApi api = new MenuApi();
	
	/**
	 * 根据ID查询
	 */
	public Menu findById(String id) {
		return Menu.dao.findById(id);
	}
	
	/**
	 * 获取菜单
	 * @param fatherId
	 * @return
	 */
	public List<Menu> listAll(){
		String sql="select * from sys_menu where 1=1 and valid_flg='Y' order by order_num asc";
		return Menu.dao.find(sql);
	}
	
	/**
	 * @Description:获取系统设置下的menu
	 * @author lu
	 * @version 2019 年 09 月 17 日  16:54:07 
	 * @return
	 */
	public List<Menu> findSysMenu(){
		String sql = "select id from sys_menu where platform_node='S' and valid_flg='Y' AND (id='F_S_SYS_MAG' or father_id='F_S_SYS_MAG')" ;
		return Menu.dao.find(sql);
	}
	
	/**
	 * 获取菜单
	 * @param fatherId
	 * @return
	 */
	public List<Menu> getEftvMenuByParam(String fatherId,String platformNode){
		if(StringUtils.isEmpty(fatherId)){
			String sql="select * from sys_menu where 1=1 "
					+ " and platform_node = ?"
					+ " and valid_flg='Y' and father_id is null order by order_num asc";
			return Menu.dao.find(sql,platformNode);
		}
		String sql="select * from sys_menu where 1=1 and father_id=? "
				+ " and valid_flg='Y' order by order_num asc";
		return Menu.dao.find(sql, fatherId);
	}
	
	public List<Menu> findByRoleId(Integer roleId){
		String sql = "select t.* from sys_menu t"
				+ " inner join sys_role_resource r on r.resource_id=t.id"
				+ " where r.role_id=?";
		return Menu.dao.find(sql, roleId);
	}


	/**
	 * 保存菜单信息 add by zhangyujuan 2016-05-10
	 * 
	 * @param cond
	 * @return
	 */
	public Ret saveOrUpdate(Menu entity, String loginAdminName) {
		boolean isNew = entity.getId() == null ? true : false;
		if (isNew) {
			entity.setValidFlg("Y");
			entity.setCreateDate(new Date());
			entity.setCreator(loginAdminName);
			boolean result = entity.save();
			return result ? RetKit.ok() : RetKit.fail(ResponseFail.SAVEORUPDATE_ERROR);
		} else {
			Menu uEntity = findById(entity.getId());
			uEntity.setMenuName(entity.getMenuName());
			uEntity.setFatherId(entity.getFatherId());
			uEntity.setNodeFlg(entity.getNodeFlg());
			if (StringUtils.equals(entity.getNodeFlg(), "Y")) {
				uEntity.setMenuAction(entity.getMenuAction());
			}
			uEntity.setOrderNum(entity.getOrderNum());
			uEntity.setUpdateDate(new Date());
			uEntity.setUpdator(loginAdminName);
			boolean result = uEntity.update();
			return result ? RetKit.ok() : RetKit.fail(ResponseFail.SAVEORUPDATE_ERROR);
		}
	}

	/**
	 * @Description:获取已获授权的菜单
	 * @author lu
	 * @version 2018年1月2日 下午2:15:32
	 * @param userId
	 * @param module
	 *            1:pc端 2：app端
	 * @return
	 */
	public List<Map<String, Object>> getAuthedMenus(User user,String platformNode) {
		// 如果user表为空，返回null
		if (user == null) {
			return null;
		}
		// 是否含有菜单权限配置
		boolean hasAuthMenus = false;
		// 普通用户需要加上权限
		if (StringUtils.equals(platformNode, "O") && user.getUserType() == UserTypeEnum.OPRT_USER.getOrdinal()) {
			//运营端   需要权限，买家端和商家端没有权限
			hasAuthMenus = true;
		}
		//处理所有的menu,将父id设置为关键词
		List<Menu> menus = Lists.newArrayList();
		if(hasAuthMenus) {
			menus = MenuApi.api.getAuthedMenu(user.getId(), platformNode);
		}else {
			menus = MenuApi.api.getMenuByPlatformNode(platformNode);
		}
		Map<String, List<Menu>> menuMap = Maps.newHashMap();
		for (Menu menu : menus) {
			String farherId = StringUtils.isBlank(menu.getFatherId())?"root":menu.getFatherId();
			List<Menu> menuList = menuMap.get(farherId)	;
			if(menuList == null) {
				menuList = Lists.newArrayList();
				menuMap.put(farherId, menuList);
			}
			menuList.add(menu);
		}
		

		//新的菜单加上提示
		//List<String> newMenus = Lists.newArrayList();
		
		List<Menu> lvl1List = menuMap.get("root");
		if(lvl1List==null) {
			return null;
		}
		
		if(user.getLanguage()==null) {
			//默认中文
			user.setLanguage(1);
		}
		List<Map<String, Object>> lv1Datalist = new ArrayList<Map<String, Object>>(lvl1List == null ? 0 : lvl1List.size());
		for (Menu menu1 : lvl1List) {
			String menuId1 = menu1.getId();
			
			Map<String, Object> menu1Map = new HashMap<String, Object>();
			menu1Map.put("menuId", menuId1);
			menu1Map.put("menuName",menu1.getMenuName());
			menu1Map.put("menuAction", menu1.getMenuAction());
			menu1Map.put("nodeFlg", menu1.getNodeFlg());
			menu1Map.put("menuIcon", menu1.getMenuIcon());
			//if(newMenus.contains(menuId1)) {
			//	menu1Map.put("newFlg", "Y");
			//}
			List<Menu> lvl2List = menuMap.get(menuId1);
			if (lvl2List != null && !lvl2List.isEmpty()) {
				List<Map<String, Object>> lv2Datalist = new ArrayList<Map<String, Object>>(lvl2List == null ? 0 : lvl2List.size());
				for (Menu menu2 : lvl2List) {
					String menuId2 = menu2.getId();
					Map<String, Object> menu2Map = new HashMap<String, Object>();
					menu2Map.put("menuId", menuId2);
					menu2Map.put("menuName",menu2.getMenuName());
					menu2Map.put("menuAction", menu2.getMenuAction());
					menu2Map.put("nodeFlg", menu2.getNodeFlg());
					menu2Map.put("menuIcon", menu2.getMenuIcon());
					//if(newMenus.contains(menuId2)) {
					//	menu2Map.put("newFlg", "Y");
					//}
					List<Menu> lvl3List = menuMap.get(menuId2);
					if (lvl3List != null && !lvl3List.isEmpty()) {
						List<Map<String, Object>> lv3Datalist = new ArrayList<Map<String, Object>>(lvl3List == null ? 0
								: lvl3List.size());
						for (Menu menu3 : lvl3List) {
							String menuId3 = menu3.getId();
							
							Map<String, Object> menu3Map = new HashMap<String, Object>();
							menu3Map.put("menuId", menuId3);
							menu3Map.put("menuName",menu3.getMenuName());
							menu3Map.put("menuAction", menu3.getMenuAction());
							menu3Map.put("nodeFlg", menu3.getNodeFlg());
							menu3Map.put("menuIcon", menu3.getMenuIcon());
							//if(newMenus.contains(menuId3)) {
							//	menu3Map.put("newFlg", "Y");
							//}
							lv3Datalist.add(menu3Map);
						}
						menu2Map.put("children", lv3Datalist);
					}
					lv2Datalist.add(menu2Map);
				}
				menu1Map.put("children", lv2Datalist);
			}
			lv1Datalist.add(menu1Map);
		}
		return lv1Datalist;
	}
	
	
	/**
	 * 获得所有父级菜单
	 */
	public List<Record> getAllMenu(Integer roleId,String platformNode){
		StringBuilder sql = new StringBuilder();
		sql.append("select s.menu_icon,s.id,s.menu_name,s.father_id,case when s.id=r.resource_id and (s.menu_action is not null and s.menu_action !='') then 1 else 0 end as states ");
		sql.append("  from sys_menu s ");
		sql.append("   left join sys_role_resource r on r.role_id=? and r.resource_id=s.id ");
		sql.append(" where s.valid_flg='Y' ");
		sql.append("   and s.platform_node=? ");
		sql.append(" order by (case when s.father_id is null then 0 else 1 end) asc,s.order_num");
		return Db.find(sql.toString(),(roleId==null?-1:roleId),platformNode);
	}
	
	/**
	 * @Description:根据登录人员获取有权限的平台菜单
	 * @author lu
	 * @version 2018 年 10 月 22 日  09:55:00 
	 * @param userId
	 * @return
	 */
	public List<String> getAuthedMenuIds(String userId) {
		String sql="select distinct t.resource_id from sys_role_resource t inner join sys_user_role r on r.role_id = t.role_id  where r.user_id = ? ";
		return Db.query(sql,userId);
	}
	
	/**
	 * @Description:根据人员id和登录平台获取有权限的菜单
	 * @author lu
	 * @version 2018 年 11 月 06 日  13:04:51 
	 * @param userId
	 * @param platformNode
	 * @return
	 */
	public List<Menu> getAuthedMenu(String userId,String platformNode){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.*");
		sql.append("  FROM sys_role_resource t");
		sql.append("   INNER JOIN sys_menu m ON t.resource_id=m.id");
		sql.append("   INNER JOIN sys_user_role ur ON ur.role_id = t.role_id");
		sql.append(" WHERE ur.user_id=? AND m.platform_node =? AND m.valid_flg = 'Y' group by m.id ORDER BY m.order_num");
		return Menu.dao.find(sql.toString(), userId,platformNode);
	}
	
	/**
	 * @Description:根据人员id和fatherid登录平台获取有权限的菜单
	 * @author: Administrator
	 * @param:userId
	 * @param:platformNode
	 * @return:
	 * @date: 2019年3月15日 上午9:34:13
	 */
	public List<Menu> getAuthedMenu(String userId,String platformNode,String fatherId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.*");
		sql.append("  FROM sys_role_resource t");
		sql.append("   INNER JOIN sys_menu m ON t.resource_id=m.id");
		sql.append("   INNER JOIN sys_user_role ur ON ur.role_id = t.role_id");
		sql.append(" WHERE ur.user_id=? AND m.father_id =? AND m.platform_node =? AND m.valid_flg = 'Y' group by m.id ORDER BY m.order_num");
		return Menu.dao.find(sql.toString(), userId,fatherId,platformNode);
	}
	
	/**
	 * @Description:根据登录平台获取菜单
	 * @author lu
	 * @version 2018 年 11 月 06 日  13:39:20 
	 * @param platformNode
	 * @return
	 */
	public List<Menu> getMenuByPlatformNode(String platformNode){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*");
		sql.append("FROM sys_menu t ");
		sql.append("WHERE t.platform_node =? AND t.valid_flg = 'Y' ORDER BY t.order_num ");
		return Menu.dao.find(sql.toString(),platformNode);
	}
	
	/**
	 * @Description:获取业务端管理员的菜单，根据父Id
	 * @author lu
	 * @version 2019 年 09 月 24 日  08:59:48 
	 * @param platformNode  登陆平台
	 * @param fatherIds 父ids
	 * @return
	 */
	public List<Menu> getMenu4SysterAdmin(String platformNode,List<String> fatherIds){
		if(fatherIds==null || fatherIds.isEmpty()) {
			return getMenuByPlatformNode(platformNode);
		}
		String fatherIdStr = StringUtils.join(fatherIds,"','");
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.* ");
		sql.append("FROM sys_menu t ");
		sql.append("WHERE t.platform_node =? AND t.valid_flg = 'Y'");
		sql.append("	AND (t.id in('"+fatherIdStr+"') OR t.father_id in('"+fatherIdStr+"'))");
		sql.append("  ORDER BY t.order_num ");
		return Menu.dao.find(sql.toString(),platformNode);
	}
	
	/**
	 * @Description: 根据登录平台和fatherid获取菜单
	 * @author: pmx
	 * @param: platformNode
	 * @return: fatherId
	 * @date: 2019年3月15日 上午9:49:12
	 */
	public List<Menu> getMenuByPlatformNode(String platformNode,String fatherId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*");
		sql.append("FROM sys_menu t ");
		sql.append("WHERE t.platform_node =? AND t.father_id =? AND t.valid_flg = 'Y' ORDER BY t.order_num ");
		return Menu.dao.find(sql.toString(),platformNode,fatherId);
	}

	/**
	 * @Description:根据人员id和登录平台、菜单ID 获取菜单
	 * @author lu
	 * @version 2018 年 11 月 06 日  13:04:51 
	 * @param userId
	 * @param platformNode
	 * @param menuId
	 * @return
	 */
	public Menu findAuthedMenuByParam(String userId,String platformNode,String menuId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.*");
		sql.append("  FROM sys_role_resource t");
		sql.append("   INNER JOIN sys_menu m ON t.resource_id=m.id");
		sql.append("   INNER JOIN sys_user_role ur ON ur.role_id = t.role_id");
		sql.append(" WHERE ur.user_id=? AND m.platform_node =?  AND m.id=? AND m.valid_flg = 'Y' limit 1");
		return Menu.dao.findFirst(sql.toString(), userId,platformNode,menuId);
	}

}
