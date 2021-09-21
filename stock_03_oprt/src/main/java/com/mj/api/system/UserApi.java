package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.CoreConstant;
import com.mj.constant.ResponseFail;
import com.mj.enums.LogTypeEnum;
import com.mj.enums.UserTypeEnum;
import com.mj.kit.KeyKit;
import com.mj.kit.encoder.Md5PwdEncoder;
import com.mj.model.system.User;

/**
 * 运营平台 Api
 * 
 * @author daniel.su
 * 
 */
public class UserApi extends BaseUserApi {

	public static final UserApi api = new UserApi();
	@Inject
	private Md5PwdEncoder md5PwdEncoder = new Md5PwdEncoder();

	// 排序
	@SuppressWarnings("serial")
	private static Map<String, String> sortConfigMap = new HashMap<String, String>() {
		{
			put("user_mobile", "t.user_mobile"); // 手机号
			put("user_type_str", "t.user_type");
			put("valid_flg_str", "t.valid_flg"); // 账户状态
			put("create_date_str", "t.create_date"); // 创建时间
			put("lastest_login_time_str", "t.lastest_login_time"); // 最近登录时间
			put("user_name", "t.user_name"); // 账户名
			put("user_real_name", "t.user_real_name"); // 姓名
		}
	};

	/**
	 * @Description: TODO(账户管理)
	 * @author lu
	 * @version 2018年3月27日 下午8:06:08
	 * @param pageNumber
	 * @param pageSize
	 * @param cond
	 * @return
	 */
	public Page<Record> page(int pageNumber, int pageSize, Map<String, Object> cond) {
		String select = "SELECT t.*";
		StringBuilder sb = new StringBuilder();
		sb.append(" FROM sys_user t ");
		sb.append(" WHERE 1=1 ");

		List<Object> queryArgs = new ArrayList<Object>();
		// 搜索
		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sb.append(
					" AND (INSTR(t.user_name,?) > 0 OR INSTR(t.user_real_name,?) > 0 OR INSTR(t.user_mobile,?) > 0 )");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}

		// 角色
		Integer roleId = (Integer) cond.get("roleId");
		if (roleId != null) {
			sb.append(" AND exists(select 1 from sys_user_role ur where ur.user_id=t.id and ur.role_id=? )  ");
			queryArgs.add(roleId);
		}

		// 有效性
		String validFlg = (String) cond.get("validFlg");
		if (StringUtils.isNotEmpty(validFlg)) {
			sb.append(" AND t.valid_flg=? ");
			queryArgs.add(validFlg);
		}
		sb.append(" AND t.user_type = ?");
		queryArgs.add(UserTypeEnum.OPRT_USER.getOrdinal());

		String orderField = StringUtils.trim((String) cond.get(CoreConstant.ORDER_FIELD));
		// 默认排序
		if (StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)) {
			sb.append(" ORDER BY ").append(sortConfigMap.get(orderField)).append(" ")
					.append(StringUtils.trim((String) cond.get(CoreConstant.ORDER_DIR)));
		} else {
			sb.append(" ORDER BY t.valid_flg DESC,t.user_name");
		}
		return Db.paginate(pageNumber, pageSize, select, sb.toString(), queryArgs.toArray());
	}

	public User findByUserName(String userName) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.user_name = ? and t.user_type in(?,?)");
		queryArgs.add(userName);
		queryArgs.add(UserTypeEnum.OPRT_ADMIN.getOrdinal());
		queryArgs.add(UserTypeEnum.OPRT_USER.getOrdinal());
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}
	
	/**
	 * 平台登录
	 * 
	 * @author zhaoxin.yuan
	 * @version 2018年3月16日 下午2:50:53
	 * @param userName
	 * @param pwd
	 * @param loginIp
	 * @return
	 */
	public Ret doLogin(String userName, String pwd, String loginIp) {
		// 判断账号是否已存在
		User user = findByUserName(userName);
		if (user == null) {
			return RetKit.fail(ResponseFail.USER_NOT_EXIST);
		}
		
		if (!(Ints.compare(user.getUserType(), UserTypeEnum.OPRT_ADMIN.getOrdinal()) == 0
				|| Ints.compare(user.getUserType(), UserTypeEnum.OPRT_USER.getOrdinal()) == 0)) {
			return RetKit.fail(ResponseFail.USER_NOT_EXIST);
		}

		// 判断密码是否正确
		Md5PwdEncoder md5PwdEncoder = new Md5PwdEncoder();
		if (!md5PwdEncoder.isPasswordValid(user.getUserPassword(), pwd, String.valueOf(user.getPwdSalt()))) {
			return RetKit.fail(ResponseFail.LOGIN_ERROR);
		}
		// 账号状态 注销提示
		if (user.getValidFlg().equals("N")) {
			return RetKit.fail(ResponseFail.LOGIN_USER_ILLEGAL);
		}

		// 用户详细信息
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("userId", user.getId());
		dataMap.put("userName", user.getUserName());
		dataMap.put("userRealName", user.getUserRealName());
		dataMap.put("userMobile", user.getUserMobile());
		dataMap.put("userType", user.getUserType());
		dataMap.put("language",user.getLanguage());
		dataMap.put("menus", MenuApi.api.getAuthedMenus(user, "O"));

		// 更新用户信息
		user.setLastestLoginIp(loginIp);
		user.setLastestLoginTime(new Date());
		updateUserInfo(user, loginIp);
		recordLoginInfo(user, loginIp, user.getUserName(), LogTypeEnum.PLATFORM_OPRT.getOrdinal());

		return RetKit.ok(dataMap);
	}

	/**
	 * @Description:免登录
	 * @author lu
	 * @version 2019 年 01 月 29 日 10:04:32
	 * @param userId
	 * @param loginIp
	 * @return
	 */
	public Ret doLogin4NoLogin(String userId, String loginIp) {
		// 判断账号是否已存在
		User user = findById(userId);
		if (user == null) {
			return RetKit.fail(ResponseFail.USER_NOT_EXIST);
		}

		if (!(Ints.compare(user.getUserType(), UserTypeEnum.OPRT_ADMIN.getOrdinal()) == 0
				|| Ints.compare(user.getUserType(), UserTypeEnum.OPRT_USER.getOrdinal()) == 0)) {
			return RetKit.fail(ResponseFail.USER_NOT_EXIST);
		}

		// 账号状态 注销提示
		if (user.getValidFlg().equals("N")) {
			return RetKit.fail(ResponseFail.LOGIN_USER_ILLEGAL);
		}

		// 用户详细信息
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("userId", user.getId());
		dataMap.put("userName", user.getUserName());
		dataMap.put("userRealName", user.getUserRealName());
		dataMap.put("userMobile", user.getUserMobile());
		dataMap.put("userType", user.getUserType());
		dataMap.put("menus", MenuApi.api.getAuthedMenus(user, "O"));
		dataMap.put("language",user.getLanguage());
		
		// 更新用户信息
		user.setLastestLoginIp(loginIp);
		user.setLastestLoginTime(new Date());
		updateUserInfo(user, loginIp);
		recordLoginInfo(user, loginIp, user.getUserName(), LogTypeEnum.PLATFORM_OPRT.getOrdinal());

		return RetKit.ok(dataMap);
	}

	/**
	 * @Description:TODO 保存账户信息 角色
	 * @author zhaoxin.yuan
	 * @version 2018年2月6日 下午3:25:53
	 * @param entity
	 * @param userRoleId
	 * @param loginUserName
	 * @return
	 */
	public Ret saveOrUpdate(User entity, Integer[] userRoleId, String loginUserName) {
		boolean isNew = entity.getId() == null ? true : false;
		if (isNew) {
			Random rand = new Random();
			Integer salt = 100000 + rand.nextInt(900000);
			String defaultPwd = md5PwdEncoder
					.encodePassword(GlobalConfigApi.api.getConfigValue("DEFAULT_PASSWORD"), salt.toString());
			entity.setUserPassword(defaultPwd);
			entity.setPwdSalt(salt.toString());
			entity.setId(KeyKit.uuid24());
			entity.setValidFlg("Y");
			entity.setCreateDate(new Date());
			entity.setCreator(loginUserName);
			entity.save();
		} else {
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserName);
			entity.update();
		}
		if(userRoleId!=null) {
			// 保存角色
			UserRoleApi.api.saveUserRoleModule(entity.getId(), 1, userRoleId);
		}
		return RetKit.ok("保存成功!");
	}

	/**
	 * 删除
	 */
	public Ret cancel(String id) {
		if (id == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		User entity = findById(id);
		entity.setValidFlg("N");
		entity.update();
		return RetKit.ok();
	}
	
	public boolean isUniqueUserName(String userId, String userName, Integer userType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(id) from sys_user where user_name=? and user_type = ? ");
		queryArgs.add(userName);
		queryArgs.add(userType);
		if (StringUtils.isNotEmpty(userId)) {
			sql.append(" and id != ? ");
			queryArgs.add(userId);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}

}
