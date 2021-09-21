package com.mj.api.system;

import java.util.List;

import com.mj.model.system.RoleResource;
import com.mj.model.system.UserRole;

/**
 * 用户菜单体系 Api
 * 
 * @author hongwan.fan 2017-10-24
 * 		
 */
public class RoleResourceApi {
	
	public static final BaseRoleResourceApi api = new BaseRoleResourceApi();
	
	/**
	 * @Description: 根据登录平台和用户id获取有用的菜单 
	 * @author lu
	 * @version 2018年1月2日 下午2:33:13 
	 * @param module  平台类型
	 * @param userId
	 * @return
	 */
	public List<RoleResource> findByUserId(String userId){
		String sql = "select t.* from sys_role_resource t "
				+ " inner join sys_role r on r.id = t.role_id and r.valid_flg = 'Y' "
				+ " inner join sys_user_role u on u.role_id = r.id where u.user_id = ?";
		return RoleResource.dao.find(sql, userId);
	}
	
	/**
	 * @Description: 根据登录平台和用户id获取有用的菜单 
	 * @author lu
	 * @version 2018年1月2日 下午2:33:13 
	 * @param module  平台类型
	 * @param userId
	 * @return
	 */
	public List<RoleResource> findByRoleId(Integer roleId){
		String sql = "select t.* from sys_role_resource t where t.role_id=? ";
		return RoleResource.dao.find(sql, roleId);
	}
	
	public UserRole findByUser(String id) {
		return UserRole.dao.findFirst("select *from sys_user_role r where r.user_id = ?",id);
	}
	
}
