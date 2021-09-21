package com.mj.api.system;

import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.mj.model.system.UserRole;

/**
 * 用户角色关联表 Api
 */
public class UserRoleApi {

	public static final UserRoleApi api = new UserRoleApi();

	/**
	 * 根据ID查询
	 */
	public UserRole findById(Integer id) {
		return UserRole.dao.findById(id);
	}

	/**
	 * @Description: TODO(根据userId获取角色)
	 * @author lu
	 * @version 2018年3月27日 下午3:48:17
	 * @param userId
	 * @return
	 */
	public List<UserRole> findByUser(String userId) {
		return UserRole.dao.find("select * from sys_user_role where user_id=?", userId);
	}

	/**
	 * @Description: 判断是这个角色是否有被使用过
	 * @author lu
	 * @version 2018年1月24日 下午5:09:31
	 * @param roId
	 * @return
	 */
	public boolean isUsed(Integer roId) {
		return UserRole.dao.findFirst("select 1 from sys_user_role where role_id = ?", roId) == null ? false : true;
	}

	/**
	 * @Description: TODO(根据类型和用户id获取角色id)
	 * @author lu
	 * @version 2018年3月27日 下午3:48:41
	 * @param userId
	 * @param module
	 * @return
	 */
	public Integer getRoleId(String userId, Integer module) {
		String sql = " SELECT t.role_id FROM sys_user_role t INNER JOIN sys_role r ON r.id = t.role_id "
				+ " WHERE t.user_id = ?  AND r.module = ?";
		return Db.queryInt(sql, userId, module);
	}

	/**
	 * @Description: TODO(根据userId和类型获取用户的用户菜单关联信息)    
	 * @author lu
	 * @version 2018年3月27日 下午3:50:34 
	 * @param userId
	 * @param module
	 * @return
	 */
	public List<UserRole> findByUserModule(String userId, Integer module) {
		return UserRole.dao.find(
				"select t.* from sys_user_role t inner join sys_role r on r.id=t.role_id where t.user_id=? and r.module=?",
				userId, module);
	}

	/**
	 * @Description: TODO(根据userId删除用户的所有角色)
	 * @author lu
	 * @version 2018年3月27日 下午3:49:03
	 * @param userId
	 * @return
	 */
	public int deleteByUserId(String userId) {
		return Db.update("delete from sys_user_role where user_id=?", userId);
	}

	/**
	 * @Description: TODO(根据userid和类型删除角色)    
	 * @author lu
	 * @version 2018年3月27日 下午3:51:18 
	 * @param userId
	 * @param module
	 * @return
	 */
	public int delUserRole(String userId, Integer module) {
		return Db.update(
				"delete from sys_user_role where "
						+ "exists(select * from sys_role r where r.id=role_id and user_id=? and r.module=?) ",
				userId, module);
	}

	/**
	 * @Description: TODO(批量保存单个用户角色关联信息)    
	 * @author lu
	 * @version 2018年3月27日 下午3:51:57 
	 * @param userId
	 * @param roleIds
	 * @return
	 */
	public Ret saveByUserId(String userId, String roleIds) {
		String roleIdArr[] = roleIds.split(",");
		UserRole userRole;
		for (int i = 00; i < roleIdArr.length; i++) {
			userRole = new UserRole();
			userRole.setUserId(userId);
			userRole.setRoleId(Integer.parseInt(roleIdArr[i]));
			userRole.save();
		}
		return RetKit.ok();
	}

	/**
	 * @Description:TODO 保存用户 角色
	 * @author zhaoxin.yuan
	 * @version 2018年1月4日 下午3:58:11
	 * @param userId
	 * @param module
	 * @param userRoleIds
	 * @return
	 */
	public Ret saveUserRoleModule(String userId, Integer module, Integer[] userRoleIds) {
		List<UserRole> userRoles = UserRoleApi.api.findByUserModule(userId, module);
		
		//存储不重复的useRoleIdS
		List<Integer> noReparts = Lists.newArrayList();
		
		List<Integer> roleArray = Arrays.asList(userRoleIds);
		for(UserRole userRole:userRoles) {
			//如果原角色在新角色中没有，则删除
			if(!roleArray.contains(userRole.getId())) {
				userRole.delete();
			}else {
				//原角色和新角色共有
				noReparts.add(userRole.getId());
			}
		}
		
		for (Integer roleId : userRoleIds) {
			//新角色有，原角色没有
			if(!noReparts.contains(roleId)) {
				UserRole userRole = new UserRole();
				userRole.setUserId(userId);
				userRole.setRoleId(roleId);
				userRole.save();
			}
		}
		
		return RetKit.ok();
	}
	
	//根据roleId更新用户角色
	public Ret deleteByRoleId(Integer id) {
		Db.update("delete from sys_user_role where role_id=?",id);
		return RetKit.ok();
	}

}
