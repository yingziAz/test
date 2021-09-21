package com.mj.api.mst;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.model.mst.ProdCategory;

public class BaseProdCategoryApi {

	public ProdCategory findById(Integer id) {
		return ProdCategory.dao.findById(id);
	}

	public boolean isUinque4Cn(String categoryName, Integer parentId,Integer id) {
		if(parentId == null) {
			parentId = 0;
		}
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("select count(1) from mst_prod_category t where t.category_name=? and parent_id=?");
		queryArgs.add(categoryName);
		queryArgs.add(parentId);
		if(id != null) {
			sql.append(" and t.id != ? ");
			queryArgs.add(id);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0 ? false : true;
	}
	
	/**
	 * 2020年10月20日 下午9:59:51
	 * @param parentId
	 * @return List<Record>
	 * @Description 根据父类id查找子分类
	 * @author lu
	 */
	public List<Record> findByParentId(Integer parentId){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.id,t.category_name");
		sql.append("  FROM mst_prod_category t ");
		sql.append("  WHERE t.parent_id = ? ");
		sql.append("  ORDER BY t.order_num,t.id");
		return Db.find(sql.toString(), parentId);
	}
	
	public Integer summaryChildNum(Integer parentId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(1) ");
		sql.append("  FROM mst_prod_category t ");
		sql.append(" WHERE t.valid_flg = 'Y' ");
		sql.append("   AND t.parent_id=? ");
		return Db.queryLong(sql.toString(),parentId).intValue();
	}

}
