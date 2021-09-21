package com.mj.constant;

/**
 * 服务端失败信息
 * 
 * @author frank.zong
 * 		
 */
public class ResponseFail {

	/**
	 * 表单重复提交
	 */
	public static final String SYSTEM_DUPLICATE_SUBMIT = "表单重复提交";
	
	/**
	 * 系统繁忙
	 */
	public static final String SYSTEM_ERROR = "系统繁忙";
	
	/**
	 * 系统繁忙
	 */
	public static final String SAVEORUPDATE_ERROR = "保存失败";
	
	/**
	 * 用户已存在
	 */
	public static final String USER_EXIST = "用户已存在";
	
	/**
	 * 用户不存在
	 */
	public static final String USER_NOT_EXIST = "用户不存在";
	
	/**
	 * 邮箱格式不正确
	 */
	public static final String EMAIL_INCORRECT = "邮箱格式不正确";
	
	/**
	 * 用户名或密码错误
	 */
	public static final String LOGIN_ERROR = "用户名或密码错误";
	
	/**
	 * 非管理员账户，不能执行登录
	 */
	public static final String UN_ADMIN_LOGIN_ERROR = "非管理员账户，不能执行登录";
	
	
	/**
	 * 用户已被注销，不能执行登录
	 */
	public static final String LOGIN_USER_ILLEGAL = "用户已被注销，不能执行登录";
	
	/**
	 * 记录已存在,请勿重复添加
	 */
	public static final String IS_EXIST = "记录已存在,请勿重复添加";
	/**
	 * 记录不存在
	 */
	public static final String IS_NOT_EXIST = "记录不存在,请核实正确";
	
	
	/**
	 * 不能少于一条记录
	 */
	public static final String MIN_ERROR = "不能少于一条记录";
	
	/**
	 * 处理异常
	 */
	public static final String DEAL_ERROR = "处理异常";
	
	/**
	 * 密码错误
	 */
	public static final String PWD_ERROR = "密码错误";
	

	/**
	 * 参数传入错误
	 */
	public static final String INPUT_PARAM_ILLEGAL = "参数传入错误";
}
