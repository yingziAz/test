package com.mj.controller.system;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.system.GlobalConfigApi;
import com.mj.api.system.MenuApi;
import com.mj.api.system.RoleApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.model.system.Role;

public class RoleController extends BaseController {
	
	public void index() {
		setAttr("oprtRoleDisplayFlg", GlobalConfigApi.api.getConfigValue("OPRT_ROLE_DISPLAY_FLG"));
		this.setAttr("nav_active", "role");
		render("role_maintain.html");
	}
	
	public void list() {
		Integer offset = 0, pageSize = 10;
		try {
			offset = this.getParaToInt("offset", 0);
			pageSize = this.getParaToInt("limit", DEFAULT_PAGE_SIZE);
		} catch (ActionException e) {
		}
		Integer pageNo = 1;
		if (offset > 0) {
			pageNo = (offset / pageSize) + 1;
		}
		Map<String, Object> cond = new HashMap<String, Object>();
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order", "asc"));
		Page<Record> page = RoleApi.api.page(pageNo, pageSize, cond);
		for (Record record : page.getList()) {
		}
		renderJson(page);
	}

	public void edit(){
		Integer id = getParaToInt("id", null);
		Role role = RoleApi.api.findById(id);
		if(role == null){
			role = new Role();
		}
		setAttr("entity", role);
		createFromToken();
		render("role_edit.html");
	}
	
	/**
	 * @Description:获得权限设置树形图
	 * @author pmx
	 * @date 2018年4月3日 上午9:15:45
	 */
	public void roleTreeview() {
		Ret ret = new Ret();
		Integer roleId = getParaToInt("id");
		List<Record> fatherMenuList = MenuApi.api.getAllMenu(roleId,"O");
		List<Map<String, Object>> list = new ArrayList<>();
		List<String> fatherMenuIdList = Lists.newArrayList();
		for(Record r:fatherMenuList) {
			String fatherId = r.getStr("father_id");
			if(StringUtils.isEmpty(fatherId)) {
				fatherMenuIdList.add(r.getStr("id"));
			}else {
				if(!fatherMenuIdList.contains(fatherId)) {
					continue;
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("id", r.getStr("id"));
			map.put("text", r.getStr("menu_name"));
			map.put("fatherId", StringUtils.isEmpty(fatherId)?"root":fatherId);
			map.put("states", r.getStr("states"));
			map.put("menuIcon", r.getStr("menu_icon"));
			list.add(map);	
		}

		ret.set("roleTreeview",list);
		renderJson(ret);
	}

	/**
	 * 保存/更新操作
	 */
	@Before({FormTokenInterceptor.class, Tx.class})
	public void save() {
		Role entity = getBean(Role.class, "entity");
		//角色名唯一
		if(!RoleApi.api.isUinque(entity.getRoleName(), entity.getId(), "N")){
			Ret ret = RetKit.fail("该角色已存在");
			ret.set(CoreConstant.FORM_TOKEN,resetFormToken());
			renderJson(ret);
			return;
		}
		entity.setTemplateFlg("N");
		String menuId = getPara("narrs");
		if(StringUtils.isBlank(menuId)) {
			Ret ret = RetKit.fail("请选择菜单");
			ret.set(CoreConstant.FORM_TOKEN,resetFormToken());
			renderJson(ret);
			return;
		}
		String[] menuIds = getPara("narrs").split(",");
		Ret ret = RoleApi.api.saveOrUpdate(entity,menuIds,getLoginUserRealName());
		if(ret.isOk()) {
			sysLog(MessageFormat.format("角色保存，角色名称：{0}。", entity.getRoleName(),getLoginUserRealName()));
		}else {
			ret.set(CoreConstant.FORM_TOKEN,resetFormToken());
		}
		renderJson(ret);
	}

	/**
	 * 删除
	 */
	public void remove() {
		Integer id = getParaToInt("id");
		Role role = RoleApi.api.findById(id);
		Ret ret = RoleApi.api.remove(id,getLoginUserRealName());
		sysLog(MessageFormat.format("删除角色，角色名称：{0}。", role.getRoleName(),getLoginUserRealName()));
		renderJson(ret);
	}


}
