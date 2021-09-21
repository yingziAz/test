package com.mj.controller;

import com.jfinal.plugin.activerecord.Record;
import com.mj.api.HomeApi;
import com.mj.api.prod.ProductApi;

/**
 * 进入后台首页
 */
public class HomeController extends BaseController {

	public void index() {
		
		Record record = new Record();
		//未修复的错误日志数量
		record.set("exceptionNum", HomeApi.api.countException());
		//有效商品数
		record.set("prodNum", ProductApi.api.summaryProdNum());
		
		setAttr("record", record);
		//一级分类，用于统计
//		List<Record> cateL1List = ProdCategoryApi.api.findByParentId(0);
//		setAttr("cateL1List",cateL1List);
		
		render("home.html");
	}
}
	
