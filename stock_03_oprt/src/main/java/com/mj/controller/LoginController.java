package com.mj.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.JFinal;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.mj.api.system.MenuApi;
import com.mj.api.system.UserApi;
import com.mj.constant.CoreConstant;
import com.mj.interceptor.ErrorInterceptor;
import com.mj.kit.RequestKit;
import com.mj.model.system.User;


/**
 * 运营平台登录 Controller
 * @author daniel.su
 */
@Clear
@Before({ErrorInterceptor.class})
public class LoginController extends BaseController {
	
	public void index(){
		render("login.html");
	}

	//--------------------------------------------------------------------我是分割线----------------------------------------------------------------------------------

	/**
	 * 验证码
	 */
	public void authImg(){
		renderCaptcha();
	}
	
	/**
	 * 登录
	 */
	@SuppressWarnings("unchecked")
	public void doLogin() {
		String userName = this.getPara("userName");
		String pwd = this.getPara("userPwd");
		String securityCode = this.getPara("securityCode");

		if(StringUtils.isEmpty(userName)){
			renderJson(RetKit.fail("用户名为空!"));
			return ;
		}

		if(StringUtils.isEmpty(pwd)){
			renderJson(RetKit.fail("密码为空!"));
			return ;
		}

		if(StringUtils.isEmpty(securityCode)){
			renderJson(RetKit.fail("验证码为空!"));
			return;
		}

		if(!validateCaptcha("securityCode")){
			renderJson(RetKit.fail("验证码输入不正确!"));
			return;
		}
		
		String loginIp = RequestKit.getIpAddr(this.getRequest());
		Ret ret = UserApi.api.doLogin(userName, pwd, loginIp);
		if(!ret.isOk()){
			this.renderJson(ret);
			return;
		}
		
		Map<String,Object> retData = (Map<String,Object>)ret.get("data");
		
		// 放数据至session
		this.setSessionAttr(CoreConstant.LOGIN, retData);
		
		if(ret.isOk()){
			// 添加sys日志
//			sysLog(MessageFormat.format("用户[{0}]，登录IP：{1}。", userName, loginIp));
		}
		renderJson( ret);
	}

	/**
	 * 以开发者模式登录
	 */
	public void doLoginAsDev() {
		if(!JFinal.me().getConstants().getDevMode()){
			renderJson(RetKit.fail("平台目前已生产环境运行，开发者模式登陆不可用！"));
			return;
		}
		String userId = "5acae2c92c200f4cf6e22111";
		User user = UserApi.api.findById(userId);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("userId", user.getId());
		dataMap.put("userName",user.getUserName());
		dataMap.put("userRealName", user.getUserRealName());
		dataMap.put("userMobile", user.getUserMobile());
		dataMap.put("userType", user.getUserType());
		dataMap.put("language",user.getLanguage());
		dataMap.put("menus", MenuApi.api.getAuthedMenus(user,"O"));
		
		// 放数据至session
		this.setSessionAttr(CoreConstant.LOGIN, dataMap);
		//loginAfter();
		renderJson(RetKit.ok());
	}

	
	/**
	 * 注销
	 */
	public void doLogout() {
		removeSessionAttr(CoreConstant.LOGIN);
		redirect("/login");
	}
	
	
}
