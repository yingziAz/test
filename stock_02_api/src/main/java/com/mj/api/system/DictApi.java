package com.mj.api.system;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.model.system.Dict;
import com.mj.model.system.DictEnum;


/**
 * 系统字典  Api
 * 
 * @author daniel.su
 * 		
 */
public class DictApi {
	
	public static final DictApi api = new DictApi();
	
	
	public Dict findById(Integer id){
		return Dict.dao.findById(id);
	}
	
	public DictEnum findByEnumId(Integer id) {
		return DictEnum.dao.findById(id);
	}
	
	public List<Dict> listAll(){
		return Dict.dao.find("select * from sys_dict where 1=1 order by order_num asc");
	}
	
	/**
	 * @Description:获取所有运营端的字典配置
	 * @author lu
	 * @version 2019 年 09 月 06 日  11:27:49 
	 * @return
	 */
	public List<DictEnum> findOprtDictEnum() {
		return DictEnum.dao.find("select * from sys_dict_enum where corp_id is null ");
	}
	
	
	//corpId 为null ,代表是运营端
	public List<DictEnum> listEnumByParam(String corpId, Integer dictId){
		StringBuilder sql = new StringBuilder();
		List<Object> queryArgs = new ArrayList<Object>();
		sql.append(" select * ");
		sql.append("   from sys_dict_enum ");
		sql.append("  where dict_id=? ");
		queryArgs.add(dictId);
		if(StringUtils.isNotBlank(corpId)) {
			sql.append(" and corp_id = ? ");
			queryArgs.add(corpId);
		}else {
			sql.append(" and corp_id is null ");
		}
		sql.append(" order by order_num asc");
		return DictEnum.dao.find(sql.toString(),queryArgs.toArray());
	}
	
	/**
	 * 根据ID获取字典值
	 * @param id
	 * @return
	 */
	public String findEnumValueById(Integer id){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.enum_value");
		sql.append("  FROM sys_dict_enum t");
		sql.append(" WHERE t.id = ?");
		Record r = Db.findFirst(sql.toString(), id);
		if(r == null || StringUtils.isBlank(r.getStr("enum_value"))){
			return StringUtils.EMPTY;
		}
		return r.getStr("enum_value");
	}

	/**
	 * 字典值保存
	 * @param dictId
	 * @param enumIdArr
	 * @param enumValueArr
	 * @return
	 */
	public Ret save(Integer dictId,String[] enumIdArr,String[] enumValueArr){
		int i = 0;
		for(String _enumId : enumIdArr ){
			i++;
			Integer enumId = null;
			if(StringUtils.isNotEmpty(_enumId)){
				enumId = Integer.parseInt(_enumId);
			}
			String enumValue = enumValueArr[i-1];
			if(enumId == null && StringUtils.isEmpty(enumValue)){
				continue;
			}
			if(enumId != null && StringUtils.isEmpty(enumValue)){
				DictEnum.dao.deleteById(enumId);
				continue;
			}
			if(enumId != null && StringUtils.isNotEmpty(enumValue)){
				DictEnum uEntity = DictEnum.dao.findById(enumId);
				uEntity.setEnumValue(enumValue);
				uEntity.setOrderNum(i+1);
				uEntity.update();
				continue;
			}
			if(enumId == null && StringUtils.isNotEmpty(enumValue)){
				DictEnum entity = new DictEnum();
				entity.setDictId(dictId);
				entity.setEnumValue(enumValue);
				entity.setOrderNum(i+1);
				entity.save();
				continue;
			}
		}
		return Ret.ok();
	}
	/**
	 * 仅根据enum_value和corp_id查询会有问题
	 * 	modify by Hova 20190929
	 * 
	 * @Description:根据enum名称获取id和dictIs
	 * @author lu
	 * @version 2019 年 09 月 06 日  11:24:06 
	 * @param enumValue
	 * @return
	 */
	public DictEnum findByEnumValue(String enumValue, Integer dictId) {
		StringBuilder sql = new StringBuilder();
		List<Object> queryArgs = new ArrayList<Object>();
		sql.append("select t.id,t.dict_id ");
		sql.append("  from sys_dict_enum t ");
		sql.append(" where t.dict_id=?");
		sql.append("   and t.enum_value=?");
		queryArgs.add(dictId);
		queryArgs.add(enumValue);
		sql.append(" order by order_num asc");
		return DictEnum.dao.findFirst(sql.toString(),queryArgs.toArray());
	}
	
	
	public List<Record> findByDictName(String dictName){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT e.id,e.enum_value");
		sql.append("  FROM sys_dict t");
		sql.append("    INNER JOIN sys_dict_enum e ON t.id = e.dict_id");
		sql.append("  WHERE t.dict_name = ?");
		sql.append("  ORDER BY e.order_num");
		return Db.find(sql.toString(), dictName);
	}
	
	
	
	/**
	 * @Description:
	 * @author lu
	 * @version 2019 年 10 月 30 日  09:15:27 
	 * @param dictName  字典名称
	 * @param deleteFlg 是否删除
	 */
	public Ret repluish(String dictName,String deleteFlg) {
		StringBuilder sql = new StringBuilder();
		if("Y".equals(deleteFlg)) {
			//如果需要删除，先删除
			sql.append(" delete c  ");
			sql.append("  from sys_dict_enum c ");
			sql.append("  inner join sys_dict t on c.dict_id = t.id ");
			sql.append("  where t.dict_name = ? ");
			Db.update(sql.toString(),dictName);
		}
		
		//需要新增的入驻公司
		sql = new StringBuilder();
		sql.append(" select c.id  ");
		sql.append("   from sys_corp c");
		sql.append("   WHERE NOT EXISTS (");
		sql.append("     SELECT 1 FROM sys_dict_enum t");
		sql.append("       inner join sys_dict d on t.dict_id = d.id");
		sql.append("       where  d.dict_name = ? ) ");
		List<Record> lists = Db.find(sql.toString(), dictName);
		if(lists.isEmpty()) {
			return RetKit.fail("无入驻公司需要新增字典值，字典值："+dictName);
		}
		
		//需要插入的数据
		sql = new StringBuilder();
		sql.append(" select t.dict_id,t.enum_value,t.enum_code,t.order_num  ");
		sql.append("   from sys_dict_enum t ");
		sql.append("   inner join sys_dict d on t.dict_id = d.id");
		sql.append("  where d.dict_name =?  ");
		List<DictEnum> enums = DictEnum.dao.find(sql.toString(), dictName);
		if(enums.isEmpty()) {
			return RetKit.fail("字典值没有内容，字典值："+dictName);
		}
		
		List<DictEnum> batchSave =Lists.newArrayList();
		if(!lists.isEmpty()) {
			for (Record record : lists) {
				for (DictEnum dictEnum : enums) {
					DictEnum entity = new DictEnum();
					entity.put(dictEnum);
					entity.setId(null);
					batchSave.add(entity);
				}
			}
		}
		
		if(!batchSave.isEmpty()) {
			Db.batchSave(batchSave, 2000);
		}
		Ret ret = RetKit.ok();
		ret.set("字典条数", enums.size());
		ret.set("补充入驻公司数", lists.size());
		ret.set("实际新增数", batchSave.size());
		return ret;
	}
	
}
