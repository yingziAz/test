package com.mj.controller.system;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.system.BaseRoleResourceApi;
import com.mj.api.system.GlobalConfigApi;
import com.mj.api.system.RoleApi;
import com.mj.api.system.UserApi;
import com.mj.constant.AppConstant;
import com.mj.constant.CoreConstant;
import com.mj.constant.ResponseFail;
import com.mj.controller.BaseController;
import com.mj.enums.UserTypeEnum;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.kit.DateUtil;
import com.mj.kit.encoder.Md5PwdEncoder;
import com.mj.model.system.User;


/**
 * @author 
 * 账户管理Controller
 */
public class AccountController extends BaseController {

	@Inject
	private Md5PwdEncoder md5PwdEncoder = new Md5PwdEncoder();

	/**
	 * 账户管理页面
	 */
	public void index() {
		Map<String, Object> cond = Maps.newHashMap();;
		if(StringUtils.equals("edit", this.getPara("from"))) {
			cond = getSessionAttr(AppConstant.LIST_COND_COOKIE);
			if(cond == null) {
				cond = Maps.newHashMap();
			}
		}
		this.setAttr("cond", cond);
		//默认密码
		setAttr("defaultPwd", GlobalConfigApi.api.getConfigValue("DEFAULT_PASSWORD"));
		setAttr("oprtRoleDisplayFlg", GlobalConfigApi.api.getConfigValue("OPRT_ROLE_DISPLAY_FLG"));
		setAttr("userTypeEnumsList", UserTypeEnum.list);
		this.setAttr("nav_active", "account");
		render("account_maintain.html");
	}

	/**
	 * 
	 * @Description:获得所有角色
	 * @author pmx
	 * @date 2018年3月30日 下午12:48:06
	 */
	public void loadFilterCond() {
		Map<String,Object> data = Maps.newHashMap();
		//data.put("total_user", UserApi.api.countUserNumber(getLoginUserCorpId(),UserTypeEnum.OPRT_USER.getOrdinal()));
		Map<String, Object> cond = Maps.newHashMap();
		cond.put("filterKeyword", getPara("filter_keyword",StringUtils.EMPTY));
		data.put("roles", RoleApi.api.listEftvWithUserCount(cond));
		renderJson(RetKit.ok(data));
	}


	/**
	 * @Description: TODO(账户管理列表)    
	 * @author lu
	 * @version 2018年3月27日 下午8:08:34 
	 */
	public void list() {
		Integer offset = 0, pageSize = 10;
		try {
			offset = this.getParaToInt("offset", 0);
			pageSize = this.getParaToInt("limit", 10);
		} catch (ActionException e) {
			// do nothing
		}
		Integer pageNo = 1;
		if (offset > 0) {
			pageNo = (offset / pageSize) + 1;
		}
		Map<String, Object> cond = new HashMap<String, Object>(5);
		// 搜索
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order", "asc"));

		cond.put("roleId", getParaToInt("roleId"));

		cond.put("pageNo", pageNo);
		cond.put("pageSize", pageSize);

		this.setSessionAttr(AppConstant.LIST_COND_COOKIE, cond);
		Page<Record> page = UserApi.api.page(pageNo, pageSize, cond);
		for(Record r:page.getList()) {
			r.set("lastest_login_time_str", DateUtil.dateTimeToStr(r.getDate("lastest_login_time")));
			r.set("valid_flg_str", "Y".equals(r.getStr("valid_flg"))?"有效":"无效");
			r.set("user_type_str", UserTypeEnum.getDisp(r.getInt("user_type")));
			r.set("create_date_str", DateUtil.dateTimeToStr(r.getDate("create_date")));
		}
		renderJson(page);
	}


	/**
	 * 登录账户信息
	 */
	public void loginAccount(){
		User user = UserApi.api.findById(this.getLoginUserId());
		setAttr("entity", user == null ? new User():user);
		createFromToken();
		render("account_login_info.html");
	}



	/**
	 * 编辑账户
	 */
	public void edit(){
		String id = this.getPara("id");
		User user = UserApi.api.findById(id);
		if(user == null){
			user = new User();
		}
		setAttr("oprtRoleDisplayFlg", GlobalConfigApi.api.getConfigValue("OPRT_ROLE_DISPLAY_FLG"));
		setAttr("entity",user);
		setAttr("roleList", RoleApi.api.listEftv("N"));

		createFromToken();
		render("account_edit.html");
	}

	/**
	 * @Description:获取人员的角色
	 * @author lu
	 * @version 2018 年 12 月 11 日  08:53:26 
	 */
	public void userRole() {
		String userId = getPara("userId");
		renderJson(BaseRoleResourceApi.api.findByUser(userId));
	}



	//---------------------------------------------------------------------------------------------------------
	/**
	 * 账户的信息保存
	 */
	@Before({FormTokenInterceptor.class,Tx.class})
	public void saveOrUpdate(){
		Ret ret;
		User user = this.getBean(User.class,"entity");
		Integer[] userRoleId = getParaValuesToInt("userRoleId");
		//用户名唯一
		if(!UserApi.api.isUniqueUserName(user.getId(), user.getUserName())){
			ret = RetKit.fail(ResponseFail.IS_EXIST);
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
			ret.set("data","用户名已存在");
			renderJson(ret);
			return;
		}
		user.setUserType(UserTypeEnum.OPRT_USER.getOrdinal());
		ret = UserApi.api.saveOrUpdate(user,userRoleId,this.getLoginUserName());
		if(ret.isOk()){
			sysLog(MessageFormat.format("运营账户保存，用户名：{0}。", user.getUserName()));
		}else{
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}

	/**
	 * 账户的信息修改保存
	 */
	@Before({FormTokenInterceptor.class,Tx.class})
	public void infoUpdate(){
		Ret ret;
		String id = this.getPara("entity.id");
		String user_real_name = this.getPara("entity.userRealName");
		String user_mobile = this.getPara("entity.userMobile");
		User user = UserApi.api.findById(id);

		if(user == null){
			ret = RetKit.fail("用户名已存在！");
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
			renderJson(ret);
			return;
		}

		user.setUserRealName(user_real_name);
		user.setUserMobile(user_mobile);
		ret = UserApi.api.doUpdateUserInfo(user, getLoginUserName());

		if(ret.isOk()){
			sysLog(MessageFormat.format("运营账户保存，用户名：{0}。", user.getUserName()));
		}else{
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}

	/**
	 * 重置密码
	 */
	public void resetPwd() {
		String id = getPara("id");
		Integer userType = getParaToInt("userType");
		//系统默认密码
		Random rand = new Random();
		Integer salt = 100000 + rand.nextInt(900000);
		String pwd = GlobalConfigApi.api.getConfigValue("DEFAULT_PASSWORD");
		String defaultPwd = md5PwdEncoder.encodePassword( pwd, salt.toString());
		Ret ret = UserApi.api.resetPwd(id,userType,defaultPwd,salt.toString(), pwd);
		if(ret.isOk()){
			sysLog(MessageFormat.format("密码重置，用户名：{0}。",getLoginUserName()));
		}
		renderJson(ret);
	}

	/**
	 * 账户注销或激活
	 */
	public void disableOrEnable(){
		String id = getPara("id");
		String userName = getPara("userName");
		String validFlg = getPara("validFlg");
		if(StringUtils.equals("N", validFlg)){
			Ret ret = UserApi.api.cancel(id);
			if(ret.isOk()){
				sysLog(MessageFormat.format("注销用户，用户名：{0}。",userName));
			}
			renderJson(ret);
		}else if(StringUtils.equals("Y", validFlg)){
			Ret ret = UserApi.api.restore(id);
			if(ret.isOk()){
				sysLog(MessageFormat.format("激活用户，用户名：{0}。",userName));
			}
			renderJson(ret);
		}
	}


	/**
	 * @Description: 修改密码页面
	 * @author lu
	 * @version 2018年1月5日 上午9:55:17 
	 */
	public void changePwd() {
		render("account_pwd_change.html");
	}

	/**
	 * @Description: 判断原密码输入是否正确（用于密码修改页面）
	 * @author lu
	 * @version 2018年1月5日 上午10:03:58 
	 */
	public void checkOldPwd(){
		User user = UserApi.api.findById(getLoginUserId());
		String oldPwd = getPara("entity.oldPsd");
		renderJson(md5PwdEncoder.isPasswordValid(user.getUserPassword(), oldPwd, String.valueOf(user.getPwdSalt())));
	}

	/**
	 * @Description: 修改密码
	 * @author lu
	 * @version 2018年1月5日 上午11:08:47 
	 */
	public void executeChangeiPwd() {
		Ret ret = null;
		User user = UserApi.api.findById(getLoginUserId());
		String oldPwd = getPara("entity.oldPsd");
		boolean result = md5PwdEncoder.isPasswordValid(user.getUserPassword(), oldPwd, String.valueOf(user.getPwdSalt()));
		if(result == false) {
			ret = RetKit.fail("原密码输入错误");
		}else {
			String newPwd = getPara("entity.newPsd");
			String id = getPara("entity.id");
			// 修改密码
			ret = UserApi.api.changePwd(id, newPwd);
			if(ret.isOk()) {
				sysLog(MessageFormat.format("{0}，修改密码成功。",getLoginUserRealName()));
			}
		}

		renderJson(ret);
	}


	/**
	 * @Description TODO(登录个人信息)
	 * @author pmx
	 * @date 2018年5月9日 上午8:48:33
	 */
	public void loginEdit() {
		User user = UserApi.api.findById(getLoginUserId());
		setAttr("entity", user == null ? new User() : user);
		createFromToken();
		render("account_login_edit.html");
	}

}
