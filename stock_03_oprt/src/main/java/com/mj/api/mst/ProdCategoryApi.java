package com.mj.api.mst;

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
import com.mj.model.mst.ProdCategory;

public class ProdCategoryApi extends BaseProdCategoryApi{

	public static final ProdCategoryApi api=new ProdCategoryApi();

	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("order_num", "t.order_num");
		put("father_category_name", "r.father_category_name");
		put("child_category_name", "r.child_category_name");
	}};

	/**
	 * 分页
	 */
	public Page<Record> page(int pageNumber, int pageSize,Map<String,Object> cond) {
		String select = " SELECT r.father_id, r.father_category_name, r.father_order_num, "
				+ "r.child_id,r.child_category_name,r.child_order_num,r.num,"
				+ "r.valid_flg";
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM ( ");
		sql.append("      SELECT p.id as father_id,p.category_name AS father_category_name,p.order_num AS father_order_num,");
		sql.append("             t.id as child_id,t.category_name AS child_category_name,t.order_num AS child_order_num,");
		sql.append("            t.valid_flg,CONCAT(t.parent_id,'_') AS num");
		sql.append("        FROM mst_prod_category t");
		sql.append("              INNER JOIN mst_prod_category p ON t.parent_id = p.id");
		sql.append("      UNION ");
		sql.append("      SELECT t.id as father_id,t.category_name AS father_category_name,t.order_num AS father_order_num,");
		sql.append("             null as child_id,null AS child_category_name,0 AS child_order_num,");
		sql.append("             t.valid_flg,CONCAT(t.id,'_') AS num");
		sql.append("        FROM mst_prod_category t");
		sql.append("       WHERE t.parent_id = 0");
		sql.append(" ) r WHERE 1 = 1 ");
		List<Object> queryArgs = new ArrayList<Object>();

		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (INSTR(father_category_name,?) > 0 "
					+ " or INSTR(child_category_name,?) > 0)");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}

		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sql.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sql.append(" order by valid_flg desc,r.num,father_order_num,child_order_num");
		}
		return Db.paginate(pageNumber, pageSize, select, sql.toString(),queryArgs.toArray());
	}

	/**
	 * 保存
	 * @param cond
	 * @return
	 */
	public Ret saveOrUpdate(ProdCategory entity,String loginUserName) {
		ProdCategory uEntity = findById(entity.getId());
		boolean isNew = uEntity == null;
		if(isNew){
			if(entity.getParentId()==null) {
				entity.setParentId(0);
			}
			entity.setValidFlg("Y");
			entity.setCreateDate(new Date());
			entity.setCreator(loginUserName);
			entity.save();
		}else{
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserName);
			entity.update();
		}
		//清空redis缓存
		return isNew?RetKit.ok("保存成功"):RetKit.ok("更新成功");
	}
	/**
	 * 注销
	 * @param id
	 * @return
	 */
	public Ret remove(Integer id) {
		if (id == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		ProdCategory entity = findById(id);
		entity.setValidFlg("N");
		entity.update();
		//清空redis缓存
		return RetKit.ok();
	}

	/**
	 * @Description: 删除   
	 * @author fb
	 * @version 2020 年 03 月 24 日  11:59:58 
	 * @param id
	 * @return
	 */
	public Ret delete(Integer id) {
		if (id == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		ProdCategory entity = findById(id);
		entity.delete();
		return RetKit.ok(entity.getCategoryName()) ;
	}

	/**
	 * 激活
	 */
	public Ret restore(Integer id) {
		ProdCategory entity = findById(id);
		if (entity == null) {
			return RetKit.fail(ResponseFail.IS_NOT_EXIST);
		}
		entity.setValidFlg("Y");
		entity.update();
		return RetKit.ok();
	}



	/**
	 * @Description:如果该分类被使用，不能删除
	 * @author shx
	 * @version 2020 年 04 月 08 日  17:40:54 
	 * @param id
	 * @return
	 */
	public boolean isProdCategoryUsed(Integer id) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(1) from base_product t where t.category_l1_id=? or t.category_l2_id=?");
		queryArgs.add(id);
		queryArgs.add(id);
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}

}
