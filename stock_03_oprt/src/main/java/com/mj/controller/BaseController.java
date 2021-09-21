package com.mj.controller;

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.mj.api.system.LogApi;
import com.mj.constant.CoreConstant;
import com.mj.enums.LogTypeEnum;
import com.mj.interceptor.LoginInterceptor;
import com.mj.kit.DateUtil;
import com.mj.kit.FileUtil;
import com.mj.kit.KeyKit;
import com.mj.kit.RequestKit;
import com.mj.model.system.Log;

//登录拦截器
@Before(LoginInterceptor.class)
public class BaseController extends Controller {
	
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	/**
	 * 判断是否已登录
	 * @return
	 */
	protected boolean isLogined(){
		return this.getSessionAttr(CoreConstant.LOGIN)==null?false:true;
	}
	/**
	 * 获得运营平台登录人的信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getLoginUser(){
		return (Map<String,Object>)this.getSessionAttr(CoreConstant.LOGIN);
	}

	protected String getLoginUserId(){
		return (String)getLoginUserElement("userId");
	}
	//超级管理员
	protected boolean isSuperAdmin(){
		return (Integer)getLoginUserElement("userType")==1;
	}
	
	protected Object getLoginUserElement(String key){
		Map<String,Object> dataMap = getLoginUser();
		if(dataMap == null)  return null;
		return dataMap.get(key);
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	protected String getLoginUserName(){
		return (String)getLoginUserElement("userName");
	}
	
	/**
	 * 获取用户真实姓名
	 * @return
	 */
	protected String getLoginUserRealName(){
		return (String)getLoginUserElement("userRealName");
	}
	
	/**
	 * 创建表单TOKEN
	 */
	public void createFromToken(){
		String formTokenSuffix = String.valueOf(KeyKit.create().next());
		setAttr("formTokenSuffix", formTokenSuffix);
		String tokenKey = CoreConstant.FORM_TOKEN+formTokenSuffix;
		createToken(tokenKey, 30*60);
	}

	/**
	 * 重置表单TOKEN
	 * @param ret
	 */
	public String resetFormToken() {
		String formTokenSuffix = this.getPara("formTokenSuffix");
		createToken(CoreConstant.FORM_TOKEN+formTokenSuffix, 30*60);
		return (String)this.getSessionAttr(CoreConstant.FORM_TOKEN + formTokenSuffix);
	}

	//运营操作日志保存
	public void sysLog(String message) {
		Log log = new Log();
		log.setLogTime(new Date());
		log.setExecutive(this.getLoginUserName());
		log.setIp( RequestKit.getIpAddr(this.getRequest()));
		log.setAction(message);
		log.setLogType(LogTypeEnum.PLATFORM_OPRT.getOrdinal().intValue());
		LogApi.api.log(log);
	}
	
	//上传excel保存目录
	public String getImportExcelPath() {
		//文件另存为 upload/excel/目录下，文件名称为员工导入+当前时间戳
		//（不加时间戳，renameTo的时候，如果目标文件夹已经有该名称文件，移动失败，下次再上传同样名称的文件时，读取的回事以前上传的文件的数据）
		// 因为，第一次上传一个文件后，下次再同一目录下，upload接收后，如果发现同样的名称，会在后来上传的文件名称结尾加上一个1，多次的话，为2,3,4.以此类推，导致读取文件不正确（以前的文件）
		String importUploadFilePatch = JFinal.me().getConstants().getBaseUploadPath()+File.separator+"excel";
		File path = new File(importUploadFilePatch);
		if(!path.exists()){
			path.mkdirs();
		}
		return importUploadFilePatch;
	}
	
	/**
	 * Excel 数据上传后，需要讲文件保存起来
	 * @param excelFilePrefix
	 * @param originalFileName
	 * @return
	 */
	public File getImportExcelFile(String excelFilePrefix,String originalFileName) {
		return new File(getImportExcelPath()+File.separator+excelFilePrefix
				+DateUtil.dateToStr(DateUtil.currentDate())+"-"+KeyKit.uuid24()
				+"."+ FileUtil.getExtensionName(originalFileName));
	}
	
}
