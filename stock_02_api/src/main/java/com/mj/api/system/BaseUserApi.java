package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.constant.ResponseFail;
import com.mj.enums.LogTypeEnum;
import com.mj.kit.ValidateUtils;
import com.mj.kit.encoder.Md5PwdEncoder;
import com.mj.model.system.Log;
import com.mj.model.system.User;

/**
 * 运营平台 Api
 * 
 * @author daniel.su
 * 
 */
public class BaseUserApi {

	/**
	 * guava 缓存技术
	 */
	public static final com.google.common.cache.Cache<String, Record> userInfoCache = 
			CacheBuilder.newBuilder()
			.maximumSize(500)
			//设置cache的初始大小为10，要合理设置该值  
			.initialCapacity(10)  
			//设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作  
			.concurrencyLevel(5)  
			//设置cache中的数据在写入之后的存活时间为60秒*10
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build();

	
	public static BaseUserApi api=new BaseUserApi();
	@Inject
	private Md5PwdEncoder md5PwdEncoder = new Md5PwdEncoder();

	/**
	 * 根据ID查询
	 */
	public User findById(String id) {
		return User.dao.findById(id);
	}


	/**
	 * @Description: 判断用户名是否唯一
	 * @author su
	 * @version 2018 年 08 月 22 日 13:40:56
	 * @param userId
	 * @param userName
	 * @return
	 */
	public boolean isUniqueUserName(String userId, String userName) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(id) from sys_user where user_name=?  ");
		queryArgs.add(userName);
		if (StringUtils.isNotEmpty(userId)) {
			sql.append(" and id != ? ");
			queryArgs.add(userId);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}
	
	/**
	 * 统计用户的数量
	 * 
	 * @return
	 */
	public Integer countUserNumber(Integer userType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(1) from sys_user t where 1=1 ");
		if (userType != null) {
			sql.append(" and t.user_type = ?");
			queryArgs.add(userType);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue();
	}
	
	/**
	 * @Description:(验证客户单位账号是否存在)    
	 * @author gao
	 * @version 2018 年 09 月 25 日  09:26:46 
	 * @param userName
	 * @return
	 */
	public User findByUserName(String userName,Integer userType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.user_name = ? and t.user_type=?");
		queryArgs.add(userName);
		queryArgs.add(userType);
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}
	
	/**  
	 * @Title: findByTypeAndBizId
	 * @Description: 根据bizId和类型获取
	 * @param bizId
	 * @param bizType
	 * @return
	 * @author lu
	 * @date 2020-07-29 04:22:33 
	 */
	public User findByTypeAndBizId(String bizId,Integer bizType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.biz_id = ? and t.user_type=?");
		queryArgs.add(bizId);
		queryArgs.add(bizType);
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}
	
	
	/**  
	 * @Title: findByOpenId
	 * @Description: 通过openId查找信息
	 * @param openId
	 * @return
	 * @author lu
	 * @date 2020-06-22 09:32:42 
	 */
	public User findByOpenId(String openId,Integer userType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.open_id = ?  and t.user_type=? ");
		queryArgs.add(openId);
		queryArgs.add(userType);
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}
	
	/**  
	 * @Title: findByUserName
	 * @Description: 根据用户名查找账号
	 * @param userName
	 * @return
	 * @author lu
	 * @date 2020-06-19 04:07:40 
	 */
	public User findByUserName(String userName) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.user_name = ? ");
		queryArgs.add(userName);
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}
	
	/**
	 * @Description:注册是时候，判断手机号和账号
	 * @author lu
	 * @version 2020 年 04 月 16 日  15:59:13 
	 * @param userName
	 * @param type
	 * @return
	 */
	public User findByUserNameOrMobile(String userName,Integer userType) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select t.* from sys_user t where t.user_type=? and (t.user_name = ? or t.user_mobile=?) ");
		queryArgs.add(userType);
		queryArgs.add(userName);
		queryArgs.add(userName);
		
		return User.dao.findFirst(sql.toString(), queryArgs.toArray());
	}

	/**
	 * 更新登录信息：如登录IP和登录地址
	 */
	public void recordLoginInfo(User user, String loginIp, String appUserName, Integer loginType) {
		if (user == null) {
			return;
		}
		// 记录登录日志
		Log loginLog = new Log();
		loginLog.setLogType(loginType);
		loginLog.setLogTime(new Date());
		loginLog.setIp(loginIp);
		loginLog.setUserId(user.getId());
		loginLog.setExecutive(appUserName);
		loginLog.setAction("用户登录，平台："+LogTypeEnum.getDisp(loginType)+"，登录ip:"+loginIp);
		BaseLogApi.api.log(loginLog);

	}

	/**
	 * 更新登录信息
	 */
	public void updateUserInfo(User user, String loginIp) {
		if (user == null) {
			return;
		}
		user.setLastestLoginIp(loginIp);
		user.setLastestLoginTime(new Date());
		user.update();
	}

	/**
	 * 登陆账户的基础信息编辑
	 * 
	 * @param entity
	 * @param loginUserName
	 * @return
	 */
	public Ret doUpdateUserInfo(User entity, String loginUserName) {
		if (StringUtils.isEmpty(entity.getId())) {
			return RetKit.fail(ResponseFail.INPUT_PARAM_ILLEGAL);
		}
		User uEntity = findById(entity.getId());
		uEntity.setUserName(entity.getUserName());
		uEntity.setUserRealName(entity.getUserRealName());
		uEntity.setUserMobile(entity.getUserMobile());
		return uEntity.update() ? RetKit.ok() : RetKit.fail(ResponseFail.SAVEORUPDATE_ERROR);
	}

	/**
	 * 判断原密码输入是否正确add by lulingfeng 2016-06-23
	 * 
	 * @param id
	 * @param userName
	 * @return
	 */
	public boolean isOldPwd(String id, String oldPwd) {
		User entity = findById(id);
		if (!StringUtils.equals(entity.getUserPassword(), oldPwd)) {
			return false;
		}
		return true;
	}

	/**
	 * 修改密码
	 * 
	 * @param id
	 * @param newPwd
	 */
	public Ret doUpdatePwd(String id,String sault, String newPwd) {
		User entity = findById(id);
		entity.setUserPassword(newPwd);
		entity.setPwdSalt(sault);
		boolean result = entity.update();
		return result ? RetKit.ok() : RetKit.fail();
	}

	/**
	 * 删除
	 */
	public Ret remove(String id) {
		if (id == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		User entity = findById(id);
		entity.setUserRealName("-");
		entity.setUserMobile(StringUtils.EMPTY);
		entity.setValidFlg("N");
		entity.update();
		
		return RetKit.ok();
	}

	/**
	 * 激活
	 */
	public Ret restore(String id) {
		User entity = findById(id);
		if (entity == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		entity.setValidFlg("Y");
		boolean result = entity.update();
		return result ? RetKit.ok() : RetKit.fail();
	}

	/**
	 * @Description: 密码重置，重置为系统初始密码值
	 * @author lu
	 * @version 2018年1月5日 上午9:58:53
	 * @param id
	 * @param userType 
	 * @param password
	 * @param salt
	 *            密码盐
	 * @return
	 * @deprecated Use {@link #resetPwd(String,Integer,String,String,String)} instead
	 */
	public Ret resetPwd(String id, Integer userType, String password, String salt) {
		return resetPwd(id, userType, password, salt, null);
	}


	/**
	 * @Description: 密码重置，重置为系统初始密码值
	 * @author lu
	 * @version 2018年1月5日 上午9:58:53
	 * @param id
	 * @param userType 
	 * @param encryPassword
	 * @param salt
	 *            密码盐
	 * @param password TODO
	 * @return
	 */
	public Ret resetPwd(String id, Integer userType, String encryPassword, String salt, String password) {
		User entity = findUser(id,userType);
		if (entity == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		entity.setUserPassword(encryPassword);
		entity.setPwdSalt(salt);
		boolean result = entity.update();
		
		return result ? RetKit.ok() : RetKit.fail();
	}

	private User findUser(String id, Integer userType) {
		StringBuilder sql=new StringBuilder();
		sql.append(" SELECT t.*");
		sql.append(" FROM sys_user t ");
		sql.append(" WHERE 1=1 ");
		sql.append("   AND t.biz_id=? and t.user_type=?");
		return User.dao.findFirst(sql.toString(), id,userType);
	}


	/**
	 * @Description: 修改密码
	 * @author lu
	 * @version 2018年1月5日 上午11:07:56
	 * @param userId
	 * @param newPwd
	 * @return
	 */
	public Ret changePwd(String userId, String newPwd) {
		User user = findById(userId);
		Random rand = new Random();
		Integer salt = 100000 + rand.nextInt(900000);
		user.setPwdSalt(salt.toString());
		user.setUserPassword(md5PwdEncoder.encodePassword(newPwd, salt.toString()));
		user.update();
		
		return RetKit.ok();
	}

	
	/**
	 * 获取用户的全名
	 * 如：传入 5be9162787dc13420067a57e,5bae16e5858cf6461997d2db,sdfgdfhdfgjgj，返回：张三，李四，王五
	 * @return
	 */
	public String getUserRealNamesByUserIds(String userIds) {
		if(StringUtils.isEmpty(userIds)) {
			return StringUtils.EMPTY;
		}
		String[] userIdArr = StringUtils.split(userIds, ",");  
		List<String> list = Lists.newArrayList();
		for(String userId : userIdArr ) {
			if(StringUtils.isEmpty(userId)) {
				continue;
			}
			Record r = getUserInfoFormCache(userId);
			if(r == null) {
				continue;
			}
			list.add(r.getStr("user_real_name"));
		}
		
		if(list.isEmpty()) {
			return StringUtils.EMPTY;
		}
		return StringUtils.join(list,",");
	}
	
	/**
	 * 获取用户信息通过	
	 * @param userId
	 * @return
	 */
	public Record getUserInfoFormCache(String userId) {
		String cacheKey =  userId;
		Record result = null;
		try {
			result = userInfoCache.get(cacheKey,new Callable<Record>(){
				@Override
				public Record call(){
					String sql="select id,user_real_name,user_mobile,user_type,emp_id from sys_user t where t.id=?";
					return Db.findFirst(sql,userId);
				}
			});

		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * @Description:(根据id拼接字符串获取人员列表)    
	 * @author gao
	 * @version 2019 年 02 月 27 日  14:43:27 
	 * @param ids
	 * @return
	 */
	public List<Record> findUserNameAndId(String ids) {
		if(StringUtils.isEmpty(ids)) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("select u.id,u.user_real_name,u.emp_id ");
		sql.append("  from sys_user u ");
		sql.append("  where find_in_set(u.id,?)");
		
		return Db.find(sql.toString(),ids);
	}
	/**
	 * @Description:根据手机号查找商户
	 * @author lu
	 * @version 2020 年 03 月 19 日 10:40:59
	 * @param mobileArea
	 * @param mobile
	 * @return
	 */
	public User findByPhone(Integer mobileArea, String mobile,Integer userType) {
		//modify by lu 20200722 现在可以同时注册手机和邮箱，如果使用手机号查找，可能存在两条记录
		//1.账号是手机，mobile是手机   2.账号是邮箱，mobile是手机
		return User.dao.findFirst("select * from sys_user t where  user_name=? and user_type=? ",
				 mobile, userType);
//		return User.dao.findFirst("select * from sys_user t where  user_mobile=? and user_type=? ",
//				mobile, userType);
	}
	
	/**  
	 * @Title: findByUserNameAndType
	 * @Description: 通过用户名查找
	 * @param userName
	 * @param userType
	 * @return
	 * @author lu
	 * @date 2020-07-22 01:03:09 
	 */
	public User findByUserNameAndType(String userName,Integer userType) {
		return User.dao.findFirst("select * from sys_user t where  user_name=? and user_type=? ",
				userName, userType);
	}
	
	/**
	 * @Description:根据手机号查找商户
	 * @author zyf
	 * @version 2020年4月15日 下午2:29:02 
	 * @param mobile
	 * @return
	 */
	public User findByMobile(String mobile,Integer userType) {
		return User.dao.findFirst("select * from sys_user t where user_mobile=? and user_type=? limit 1",mobile, userType);
	}
}
