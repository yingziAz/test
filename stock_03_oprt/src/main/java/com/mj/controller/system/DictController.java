package com.mj.controller.system;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.system.DictApi;
import com.mj.controller.BaseController;
import com.mj.model.system.DictEnum;

public class DictController extends BaseController {

	public void index(){
		//setAttr("list", DictApi.api.listAll());
		setAttr("nav_active", "dict");
		render("dict_maintain.html");
	}

	/**
	 * 进入字典值维护页面
	 */
	public void dictEnum(){
		Integer dictId = this.getParaToInt("dictId");
		this.setAttr("dict_id", dictId);
		
		List<DictEnum> list = DictApi.api.listEnumByParam(null,dictId);
		int size = 5;
		int listSize = (list==null?0:list.size()); 
		int inputSize = ((listSize/ size)+1)*size;
		for(int i=0;i<inputSize-listSize;i++){
			list.add(new DictEnum());
		}
		this.setAttr("list", list);
		render("dict_enum_maintain.html");
	}

	/**
	 * 保存操作
	 */
	@Before({Tx.class})
	public void save() {
		Integer dictId = this.getParaToInt("dictId");
		String enumIdArr[] = this.getParaValues("enumId");
		String enumValueArr[] = this.getParaValues("enumValue");
		Set<String> set = new HashSet<String>();
		Ret ret;
		int cnt = 0;
		for(String str : enumValueArr){
			if(StringUtils.isNotEmpty(str)){
				set.add(str);
				cnt++;
			}
		}
		//参数值重复
		if(set.size() != cnt){
			ret = RetKit.fail("参数值重复");
			this.renderJson(ret);
			return;
		}
		ret = DictApi.api.save(dictId, enumIdArr, enumValueArr);
		if(!ret.isOk()){
			this.renderJson(ret);
			return;
		}
		sysLog(MessageFormat.format("保存字典参数，字典:{1}。",this.getLoginUserRealName(),DictApi.api.findById(dictId).getDictName()));
		this.renderJson(ret);
	}
	
	/**
	 * @Description:根据字典名称进行补充
	 * @author lu
	 * @version 2019 年 10 月 30 日  09:20:20 
	 */
//	@Before({Tx.class})
	public void repluish() {
		String dictName = getPara("dictName");
		if(StringUtils.isBlank(dictName)) {
			renderJson(RetKit.fail("未获取到字典名称"));
			return;
		}
		String deleteFlg = getPara("deleteFlg","N");
		Ret ret = DictApi.api.repluish(dictName, deleteFlg);
		String msg = MessageFormat.format("同步字典值，字典名称：{0}。",dictName);
		sysLog(msg);
		renderJson(ret);
	}
	
}
