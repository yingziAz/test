package com.mj.controller.mst;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mj.api.mst.ProdCategoryApi;
import com.mj.constant.AppConstant;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.model.mst.ProdCategory;

public class ProdCategoryController extends BaseController{

	/**
	 * @Description:首页   
	 * @author fb
	 * @version 2020 年 03 月 17 日  14:43:57 
	 */
	public void index() {
		Map<String, Object> cond = Maps.newHashMap();
		if (StringUtils.equals("view", this.getPara("from"))) {
			cond = getSessionAttr(AppConstant.LIST_COND_COOKIE+"_product_category");
			if (cond == null) {
				cond = Maps.newHashMap();
			}
		}
		this.setAttr("cond", cond);
		List<String> warnings = Lists.newArrayList();
		setAttr("warnings", warnings);
		render("prod_category_maintain.html");
	}
	
	/**
	 * @Description:列表    
	 * @author fb
	 * @version 2020 年 03 月 17 日  14:43:49 
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
		Map<String, Object> cond = new HashMap<String, Object>();
		// 搜索
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order", "asc"));
		this.setSessionAttr(AppConstant.LIST_COND_COOKIE +"_product_category", cond);
		Page<Record> page = ProdCategoryApi.api.page(pageNo, pageSize, cond);
		renderJson(page);
	}
	
	public void edit() {
		Integer id = getParaToInt("id", null);
		ProdCategory entity = new ProdCategory();
		if(id!=null) {
			entity = ProdCategoryApi.api.findById(id);
		}else {
			Integer parentId = getParaToInt("parentId");
			entity.setParentId(parentId);
		}
		setAttr("entity", entity);
		createFromToken();
		render("prod_category_edit.html");
	}
	
	
	/**
	 * @Description: 保存或更新    
	 * @author fb
	 * @version 2020 年 03 月 17 日  14:43:26 
	 */
	public void saveOrUpdate() {
		Ret ret;
		ProdCategory entity = getBean(ProdCategory.class, "entity");
		//判断 唯一性
		if(!ProdCategoryApi.api.isUinque4Cn(entity.getCategoryName(),entity.getParentId(), entity.getId())) {
			ret = RetKit.fail("分类名称已存在，请勿重复添加");
			ret.set(CoreConstant.FORM_TOKEN,resetFormToken());
			renderJson(ret);
			return;
		}

		ret = ProdCategoryApi.api.saveOrUpdate(entity, getLoginUserRealName());
		if(ret.isOk()) {
			sysLog(MessageFormat.format("保存商品分类，名称:{0}。", entity.getCategoryName()));
		}else {
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}
	
	/**
	 * @Description:  注销或激活    
	 * @author fb
	 * @version 2020 年 03 月 17 日  14:43:09 
	 */
	public void disableOrEnable() {
		Integer id = getParaToInt("id");
		String typeCn = getPara("typeCn");
		String validFlg = getPara("valid_flg");
		if(StringUtils.equals("N", validFlg)){
			Ret ret = ProdCategoryApi.api.remove(id);
			if(ret.isOk()){
				sysLog(MessageFormat.format("注销商品分类，商品分类：{0}。",typeCn));
			}
			renderJson(ret);
		}else if(StringUtils.equals("Y", validFlg)){
			Ret ret = ProdCategoryApi.api.restore(id);
			if(ret.isOk()){
				sysLog(MessageFormat.format("激活商品分类，商品分类：{0}。",typeCn));
			}
			renderJson(ret);
		}
	
	}
	
	public void delete() {
		Integer id = getParaToInt("id");
		String categoryName = StringUtils.EMPTY;
		
		if(!ProdCategoryApi.api.isProdCategoryUsed(id)) {
			Ret ret = RetKit.fail("该商品分类已经被使用,不能被删除");
			ret.set(CoreConstant.FORM_TOKEN,resetFormToken());
			renderJson(ret);
			return;
		}
		Ret ret = ProdCategoryApi.api.delete(id);
		if(ret.isOk()){
			categoryName = ret.getStr("data");
			sysLog(MessageFormat.format("删除商品分类，分类名称：{0}。",categoryName));
		}
		renderJson(ret);
	}
	
	/**
	 * 2020年10月20日 下午10:02:19
	 * @param id void
	 * @Description 根据父类id查找子分类
	 * @author lu
	 */
	public void getChild(Integer id) {
		Integer fatherId = getParaToInt("father_id");
		renderJson(RetKit.ok(ProdCategoryApi.api.findByParentId(fatherId)));
	}
	
	/**
	 * @Description: 获取二级分类数量 
	 * @author fb
	 * @version 2020 年 03 月 24 日  11:49:42 
	 */
	public void getChildNum() {
		Integer fatherId = getParaToInt("father_id");
		int num = ProdCategoryApi.api.summaryChildNum(fatherId);
		renderJson("num",num);
	}
	
	
	
}
