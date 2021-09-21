package com.mj.controller.system;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.system.GlobalConfigApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.FormTokenInterceptor;

/**
 * 参数配置 Controller
 * @author daniel.su
 */
public class GlobalConfigController extends BaseController {

	private List<String> configes = Arrays.asList(
			// 默认密码
			"DEFAULT_PASSWORD",
			// 运营角色开放角色
			"OPRT_ROLE_DISPLAY_FLG",
			//订单完成后不能进行售后天数
			"FINISH_ORDER_UN_SERVICE",
			//积分=（订单支付金额-退款金额）*积分规则
			"INTEGRAL_RULE",
			//开放国际化
			"INTERNATIONALIZE",
			//积分、积分商城
			"INTEGRAL_FLG",
			// 运营角色开放角色
			"UPGRADE_PVIP_SELLER_VIP_NUM");

	/**
	 * 首页
	 */
	public void index() {
		Map<String, String> data = Maps.newHashMap();
		for (String paramValue : configes) {
			data.put(paramValue, GlobalConfigApi.api.getConfigValue(paramValue));
		}
		this.setAttr("data", data);
		this.setAttr("nav_active", "config");
		
		createFromToken();
		render("global_config_maintain.html");
	}

	@Before({ FormTokenInterceptor.class, Tx.class })
	public void save() {
		String loginUserName = this.getLoginUserRealName();
		for (String paramValue : configes) {
			GlobalConfigApi.api.saveOrUpdate(paramValue, this.getPara("param." + paramValue), loginUserName);
		}
		Ret ret = RetKit.ok();
		ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		sysLog("平台参数编辑成功。");
		this.renderJson(ret);
	}

}
