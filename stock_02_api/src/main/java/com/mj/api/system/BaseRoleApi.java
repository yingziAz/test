package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.ResponseFail;
import com.mj.model.system.Menu;
import com.mj.model.system.Role;
import com.mj.model.system.RoleResource;


/**
 * 系统字典  Api
 * 
 * @author daniel.su
 * 		
 */
public class BaseRoleApi {
	

	/**
	 * 实体-根据ID查询
	 */
	public Role findById(Integer id) {
		return Role.dao.findById(id);
	}
	
	/**
	 * @Description: 获取有效的角色 
	 * @author lu
	 * @version 2018年1月4日 上午9:49:43 
	 * @return
	 */
	public List<Record> listEftv(String templateFlg){
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("select id,role_name");
		sql.append("  from sys_role ");
		sql.append(" where valid_flg='Y'");
		sql.append(" order by order_num asc,role_name");
		return Db.find(sql.toString(),queryArgs.toArray());
	}
	
	
	/**
	 * 删除/注销
	 */
	public Ret remove(Integer id,String loginUserName) {
		Role entity = findById(id);
		if(entity == null){
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		entity.delete();
		UserRoleApi.api.deleteByRoleId(id);
		return RetKit.ok();
	}
	public List<Menu> findMenuByRoleId(Integer roleId){
		if(roleId == null){
			return null;
		}
		return MenuApi.api.findByRoleId(roleId);
	}
	
	/**
	 * @Description:TODO 角色名唯一验证
	 * @author zhaoxin.yuan
	 * @version 2018年1月23日 上午11:55:13
	 * @param roleName
	 * @param id
	 * @return
	 */
	public boolean isUinque(String roleName, Integer id, String templateFlg){
		List<Object> queryArgs = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select count(1) from sys_role t where t.role_name=?");
		queryArgs.add(roleName);
		sql.append(" and t.template_flg=?");
		queryArgs.add(templateFlg);
		if(id != null) {
			sql.append(" and t.id != ? ");
			queryArgs.add(id);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}
	
	/**
	 * @Description:TODO 保存
	 * @author zhaoxin.yuan
	 * @version 2018年1月23日 上午11:58:49
	 * @param role
	 * @return
	 */
	public Ret saveOrUpdate(Role role, String[] menuIds,String loginUserName){
		if(role.getId() == null){
			//新增
			role.setValidFlg("Y");
			role.setModule(1);
			role.setCreateDate(new Date());
			role.setCreator(loginUserName);
			role.save();
		} else {
			//更新
			Role entity = findById(role.getId());
			entity.setRoleName(role.getRoleName());
			entity.setDescription(role.getDescription());
			entity.setOrderNum(role.getOrderNum());
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserName);
			entity.update();
		}
		
		//保存角色、菜单管理表
		List<String> menuArray  = null;
		if(menuIds == null || menuIds.length ==0) {
			menuArray = Lists.newArrayList();
			Db.update("delete from sys_role_resource where role_id=?",role.getId());
		}else{
			menuArray = Lists.newArrayList();
			for(String menuId : menuIds) {
				if(StringUtils.isEmpty(menuId)) {
					continue;
				}
				menuArray.add(menuId);
			}
			
			List<Menu> menus = MenuApi.api.listAll();
			for(Menu menu : menus) {
				if(StringUtils.isEmpty(menu.getFatherId())) {
					continue;
				}
				String menuId = menu.getId();
				if(menuArray.contains(menuId) && !menuArray.contains(menu.getFatherId())) {
					menuArray.add(menu.getFatherId());
				}
			}
		}
		
		List<RoleResource> list = BaseRoleResourceApi.api.findByRoleId(role.getId());
		for(RoleResource entity : list) {
			String menuId = entity.getResourceId();
			if(menuArray.contains(entity.getResourceId())) {
				menuArray.remove(menuId);
			}else {
				//如果不存在，需要将DB中删除
				entity.delete();
			}
		}
		if(!menuArray.isEmpty()) {
			List<RoleResource> rrList= Lists.newArrayList();
			for(String menuId : menuArray) {
				RoleResource entity = new RoleResource();
				entity.setRoleId(role.getId());
				entity.setResourceId(menuId);
				rrList.add(entity);
			}
			Db.batchSave(rrList, 100);
		}
		return Ret.ok();
	}
	
	/**
	 * 
	 * @Description: 同步    
	 * @author yang
	 * @version 2018 年 12 月 12 日  15:47:21 
	 * @return
	 */
	public Ret sync() {
		
		//step1:删除菜单
		StringBuilder sql = new StringBuilder();
		sql.append(" DELETE t ");
		sql.append("	FROM sys_role_resource t ");
		sql.append(" 	  LEFT JOIN sys_menu m ON t.resource_id = m.id ");
		sql.append(" 	WHERE m.id IS NULL ; ");
		Db.delete(sql.toString());
		
		//step2:新增没有使用过的 菜单（新菜单）
		sql = new StringBuilder();
		//一般情况下没有问题
//		sql.append(" INSERT INTO sys_role_resource(role_id,resource_id,create_date,creator)");
//		sql.append(" 	SELECT sr.id,new_resource.resource_id, ?,'系统同步'");
//		sql.append(" 		FROM sys_role sr");
//		sql.append(" 		INNER JOIN (");
//		sql.append(" 			SELECT r.role_name,t.resource_id, COUNT(1) AS num");
//		sql.append(" 				FROM sys_role_resource t");
//		sql.append(" 				INNER JOIN sys_menu m ON t.resource_id = m.id AND m.platform_node='S'");
//		sql.append(" 				INNER JOIN sys_role r ON r.id = t.role_id");
//		sql.append(" 			GROUP BY t.resource_id");
//		sql.append(" 			HAVING num<=1) AS new_resource ON sr.role_name = new_resource.role_name");
//		sql.append(" 	WHERE sr.template_flg='N'");
//		Db.update(sql.toString(),new Date());
		
		sql.append(" INSERT INTO sys_role_resource(role_id,resource_id,create_date,creator)");
		sql.append(" 	SELECT sr.id,new_resource.resource_id,?,'系统同步'");
		sql.append(" 	FROM sys_role sr");
		sql.append("		INNER JOIN (");
		//获取模板配置的所有菜单
		sql.append(" 		SELECT r.role_name,t.resource_id,m.menu_name");
		sql.append(" 		FROM sys_role_resource t");
		sql.append(" 		INNER JOIN sys_menu m ON t.resource_id = m.id AND m.platform_node='S'");
		sql.append(" 		INNER JOIN sys_role r ON r.id = t.role_id AND r.template_flg='Y' ");
		sql.append(" 	) AS new_resource ON sr.role_name = new_resource.role_name");
		sql.append(" 	WHERE sr.template_flg='N' ");
		sql.append(" 		AND NOT EXISTS(");
		//获取入驻公司模板角色配置的菜单
		sql.append(" 			SELECT 1");
		sql.append(" 			FROM sys_role_resource srr");
		sql.append(" 			WHERE srr.role_id = sr.id AND srr.resource_id =new_resource.resource_id)");
		Db.update(sql.toString(),new Date());
		return RetKit.ok();
	}
}
