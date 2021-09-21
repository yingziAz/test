package com.mj.api.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.mj.config.RedisCacheConfig;
import com.mj.constant.CoreConstant;
import com.mj.model.system.GlobalConfig;

/**
 * 
 * 全局参数配置
 * 
 * @author daniel.su
 * 
 */
public class BaseGlobalConfigApi {

	public static final BaseGlobalConfigApi api = new BaseGlobalConfigApi();

	private static String REDIS_KEY = CoreConstant.PLATFORM_REDIS_PREFIX + "GLOBAL_CONFIG";


	/**
	 * @Description: 04-system 端获取全局变量的调用方法
	 * @author sun
	 * @version 2018 年 10 月 16 日 11:13:12
	 * @param paramName
	 * @param corpId
	 * @return
	 */
	public String getConfigValue(String paramName) {
		String cacheKey = paramName;
		Cache redisCache = Redis.use(RedisCacheConfig.STOCK_CORE_CACHE);
		if (redisCache.hexists(REDIS_KEY, cacheKey)) {
			return (String) redisCache.hget(REDIS_KEY, cacheKey);
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" select t.param_value from sys_global_config t where t.param_name=? ");
		List<Object> queryArgs = new ArrayList<Object>();
		queryArgs.add(paramName);
		GlobalConfig entity = GlobalConfig.dao.findFirst(sql.toString(), queryArgs.toArray());
		if (entity == null) {
			return null;
		}
		String paramValue = entity.getParamValue();
		redisCache.hset(REDIS_KEY, cacheKey, paramValue);
		// 设置过期时间
		redisCache.expire(REDIS_KEY, 60 * 60 * 1);
		return paramValue;

	}

	/**
	 * 查询
	 */
	public GlobalConfig findById(Integer id) {
		return GlobalConfig.dao.findById(id);
	}
	
	/**  
	 * @Title: getInternationalize
	 * @Description: 判断是否进行国际化
	 * @return
	 * @author lu
	 * @date 2020-07-22 02:50:05 
	 */
	public boolean getInternationalize() {
		String Internationalize = getConfigValue("INTERNATIONALIZE");
		boolean flg = false;
		if(StringUtils.isNotBlank(Internationalize)) {
			if(StringUtils.equals("Y", Internationalize)) {
				flg=true;
			}
		}
		return flg;
	}
	
	/**  
	 * @Title: getInternationalize
	 * @Description: 判断是否开启积分商城
	 * @return
	 * @author lu
	 * @date 2020-07-22 02:50:05 
	 */
	public boolean getIntegralFlg() {
		String integralFlg = getConfigValue("INTEGRAL_FLG");
		boolean flg = false;
		if(StringUtils.isNotBlank(integralFlg)) {
			if(StringUtils.equals("Y", integralFlg)) {
				flg=true;
			}
		}
		return flg;
	}
	
	/**
	 * 查询
	 */
	public GlobalConfig findByParam(String paramName) {
		String sql="select t.* from sys_global_config t where t.param_name=? limit 1";
		return GlobalConfig.dao.findFirst(sql,paramName);
	}


	/**
	 * 保存
	 * @param cond
	 * @return
	 */
	public void saveOrUpdate(String paramName,String paramValue,String loginUserName) {
		if(StringUtils.isEmpty(paramValue)) {
			return;
		}
		if(StringUtils.equals(paramValue, getConfigValue(paramName))) {
			return;
		}
		GlobalConfig entity = findByParam(paramName);
		if(entity == null){
			entity = new GlobalConfig();
			entity.setParamName(paramName);
			entity.setParamValue(paramValue);
			entity.setCreateDate(new Date());
			entity.setCreator(loginUserName);
			entity.setOrderNum(1);
			entity.save();
		}else{
			entity.setParamValue(paramValue);
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserName);
			entity.update();
		}
		cacheRemove(paramName);
	}

	/**
	 * @Description: 缓存删除：guava缓存和 redis 缓存
	 * @author su
	 * @version 2018 年 08 月 24 日 10:15:59
	 * @param corpId
	 * @param paramName
	 */
	public void cacheRemove(String paramName) {
		String cacheKey = paramName;

		Cache redisCache = Redis.use(RedisCacheConfig.STOCK_CORE_CACHE);
		if (redisCache.hexists(REDIS_KEY, cacheKey)) {
			redisCache.hdel(REDIS_KEY, cacheKey);
		}
	}
}
