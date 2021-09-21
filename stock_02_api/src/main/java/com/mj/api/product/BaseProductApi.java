package com.mj.api.product;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.model.base.Product;

public class BaseProductApi {

	public static final BaseProductApi api = new BaseProductApi();
	
	public Product findById(String id) {
		return Product.dao.findById(id);
	}
	
	/**
	 * 2020年10月20日 下午8:42:54
	 * @param procudtNo
	 * @param id
	 * @return boolean
	 * @Description 编号唯一性验证
	 * @author lu
	 */
	public boolean isUnique4ProductNo(String procudtNo,String id) {
		List<Object> queryArgs = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(1)");
		sql.append(" from base_product t ");
		sql.append(" where t.product_no = ?");
		queryArgs.add(procudtNo);
		
		if(!StringUtils.isEmpty(id)) {
			sql.append(" and t.id != ? ");
			queryArgs.add(id);
		}
		return Db.queryLong(sql.toString(), queryArgs.toArray()).intValue() > 0;
	}
	
	
	/**
	 * 2020年10月23日 下午10:17:01
	 * @param id
	 * @return Record
	 * @Description 商品详情
	 * @author lu
	 */
	public Record findViewById(String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.*,");
		sql.append("IFNULL(c.category_name,'') as parent_category,");
		sql.append("IFNULL(pc.category_name,'') as child_category,");
		sql.append("IFNULL(ps.stock_num,0) AS stock_num");
		sql.append(" FROM base_product t");
		sql.append("  INNER JOIN mst_prod_category c ON t.category_l1_id = c.id ");
		sql.append("  LEFT JOIN mst_prod_category pc ON t.category_l2_id = pc.id");
		sql.append("  LEFT JOIN biz_prod_stock ps ON ps.prod_id = t.id");
		sql.append("  WHERE t.id = ?");
		return Db.findFirst(sql.toString(), id);
	}
	
	
}
