package com.mj.constant;

/**
 * Constant
 * 
 * @author frank.zong
 * 		
 */
public class CoreConstant {

	public static final String CTX = "ctx";
	
	public static final String LOGIN = "mode2020_login";
	public static final String TOKEN = "mode2020_token";

	public static final String COOKIE = "cookie";

	public static final String RET_DATA = "data";
	public static final String RET_MSG = "msg";
	public static final String RET_STATE_LOGIN = "login";  //需登录权限
	public static final String RET_STATE_TOKEN = "token";  //需token权限

	public static final String RET_MSG_OK = "操作成功！";
	public static final String RET_MSG_FAIL = "操作失败！";
	public static final String RET_MSG_LOGIN = "会话超时！请重新登录。";
	public static final String RET_MSG_TOKEN = "会话超时！请重新登录。";

	//防止重复提交：表单token
	public static final String FORM_TOKEN = "formToken";

	public static final String ORDINAL = "ordinal";
	public static final String NAME = "name";
	public static final String VALUE = "value";


	public static final String ENV_BETA = "beta";
	public static final String ENV_PROD = "prod";
	public static final String ENV_TEST = "test";

	public static final String SEARCH_KEYWORD = "keyword";  //查询关键词
	public static final String ORDER_FIELD = "orderField";  //排序字段
	public static final String ORDER_DIR = "orderDir";      //排序方向

	public static final String PLATFORM_REDIS_PREFIX = "MODE_";	//REDIS前缀

}
