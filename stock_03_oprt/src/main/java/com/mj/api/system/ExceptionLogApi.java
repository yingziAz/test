package com.mj.api.system;

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
import com.mj.constant.ResponseFail;
import com.mj.model.system.ExceptionLog;

public class ExceptionLogApi extends BaseExceptionLogApi {

	public static final ExceptionLogApi api = new ExceptionLogApi();

	/**
	 * 排序
	 */
	@SuppressWarnings("serial")
	private static Map<String, String> sortConfigMap = new HashMap<String, String>() {
		{
			put("err_msg", "t.err_msg"); 
			put("err_time", "t.err_time");
			put("handle_time", "t.handle_time");
			put("handle_desc", "t.handle_desc");
			put("handle_time", "t.handle_time");
			put("handleFlg4Str", "t.handle_flg");
		}
	};

	public Page<Record> page(Integer pageNo, Integer pageSize, Map<String, Object> cond) {
		String select = "select t.*";
		StringBuilder sb = new StringBuilder();
		sb.append(" from sys_exception_log t where 1=1");

		// 搜索功能
		List<Object> queryArgs = new ArrayList<Object>();
		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sb.append(" and ( INSTR(t.err_msg,?) > 0 ) ");
			queryArgs.add(keyword);
		}
		
		String filterValue = (String)cond.get("filterValue");
		if(StringUtils.isNotBlank(filterValue)) {
			sb.append(" AND t.handle_flg=? ");
			queryArgs.add(filterValue);
		}

		String orderField = StringUtils.trim((String) cond.get(CoreConstant.ORDER_FIELD));
		// 默认排序
		if (StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)) {
			sb.append(" order by ").append(sortConfigMap.get(orderField)).append(" ")
					.append(StringUtils.trim((String) cond.get(CoreConstant.ORDER_DIR)));
		} else {
			sb.append(" order by t.err_time desc,t.err_msg ");
		}
		return Db.paginate(pageNo, pageSize, select, sb.toString(), queryArgs.toArray());
	}


	/**
	 * 
	 * @Description:修复异常日志
	 * @author shx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	public Ret repair(ExceptionLog entity) {
		if (entity == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		entity.setHandleTime(new Date());
		entity.setHandleFlg("Y");
		entity.update();
		return RetKit.ok();
	}

	/**
	 * 
	 * @Description:保存处理描述
	 * @author shx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	public Ret saveOrUpdate(ExceptionLog entity, String loginUserRealName, String loginUserId) {
		boolean result = false;
		result = entity.update();
		return result ? RetKit.ok("成功") : RetKit.fail("失败");
	}

	/**
	 * 
	 * @Description:获得已修复 未修复数
	 * @author shx
	 * @date 2018年3月30日 下午12:11:56
	 * @return
	 */
	public Record countHandleFlgNum() {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(t.id) as exception_total,");
		sb.append("       ifnull(sum(case when t.handle_flg = 'Y' then 1 else 0 end),0) as repaired_num,");
		sb.append("       ifnull(sum(case when t.handle_flg = 'N' then 1 else 0 end),0) as unrepaired_num ");
		sb.append(" from sys_exception_log t ");
		return Db.findFirst(sb.toString());
	}


	public Ret remove(Integer id) {
		Db.update("delete from sys_exception_log where id=?",id);
		return RetKit.ok();
	}
	

}
