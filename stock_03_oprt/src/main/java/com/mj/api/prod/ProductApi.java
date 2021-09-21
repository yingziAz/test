package com.mj.api.prod;

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
import com.mj.api.product.BaseProductApi;
import com.mj.constant.CoreConstant;
import com.mj.kit.DateUtil;
import com.mj.kit.KeyKit;
import com.mj.model.base.Product;

public class ProductApi extends BaseProductApi {
	
	public static final ProductApi api = new ProductApi();
	
	/**
	 * 排序
	 */
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("parent_category","t.category_l1_id");  //商品一级分类
		put("child_category","t.category_l2_id");  //商品二级分类
		put("color_cn","t.color_cn");  //商品颜色（中文）
		put("create_date","t.create_date");  //创建时间
		put("creator","t.creator");  //创建者
		put("order_num","t.order_num");  
		put("product_cover","t.product_cover");  //商品封面图
		put("product_desc","t.product_desc");  //商品描述（中文）
		put("product_images","t.product_images");  //商品图片（最多9张），第一张作为头图
		put("product_name","t.product_name");  //商品名称
		put("product_no","t.product_no");  //商品编号
		put("specs","t.specs");  //规格
		put("update_date","t.update_date");  //更新时间
		put("updator","t.updator");  //更新者
		put("valid_flg_str","t.valid_flg");  //是否有效
	}};
	
	/**
	 * @Description:
	 * @author: Administrator
	 * @param:
	 * @return:
	 * @date: 2020年3月20日 上午8:47:10 
	 */
	public Page<Record> page(Integer pageNo, Integer pageSize, Map<String, Object> cond) {
		String select = "SELECT t.*,"
				+ "IFNULL(c.category_name,'') as parent_category,"
				+ "IFNULL(pc.category_name,'') as child_category,"
				+ "IFNULL(ps.stock_num,0) AS stock_num";
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM base_product t");
		sql.append("  INNER JOIN mst_prod_category c ON t.category_l1_id = c.id ");
		sql.append("  LEFT JOIN mst_prod_category pc ON t.category_l2_id = pc.id");
		sql.append("  LEFT JOIN biz_prod_stock ps ON ps.prod_id = t.id");
		sql.append(" WHERE 1=1 ");
		List<Object> queryArgs = new ArrayList<Object>();
		
		String keyword = StringUtils.trim((String)cond.get(CoreConstant.SEARCH_KEYWORD));
		if(StringUtils.isNotEmpty(keyword)){
			sql.append(" and (INSTR(t.product_no,?) > 0 OR INSTR(t.product_name,?) > 0");
			sql.append(" OR INSTR(c.category_name,?) > 0  OR INSTR(pc.category_name,?) > 0 )");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}
				
		String orderField = StringUtils.trim((String)cond.get(CoreConstant.ORDER_FIELD));
		//默认排序
		if(StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)){
			sql.append(" order by ").append(sortConfigMap.get(orderField))
			.append(" ").append(StringUtils.trim((String)cond.get(CoreConstant.ORDER_DIR)));
		}else{
			sql.append(" order by t.valid_flg desc,t.product_no");
		}
		return Db.paginate(pageNo, pageSize, select, sql.toString(),queryArgs.toArray());
	}
	
	/**  
	 * @Title: getOutRequestNo
	 * @Description: 获取编号：年度+4位流水号
	 * @return
	 * @author lu
	 * @date 2020-09-23 09:02:26 
	 */
	public String getProductNo() {
		String prefix = DateUtil.dateToStr(new Date()).substring(0, 4);
		String num = Db.queryStr("SELECT concat(max(right(product_no,4))+1,'') AS cmp_no FROM base_product where product_no like '"
						+ prefix + "%' ");
		if (StringUtils.isBlank(num)) {
			num = "0001";
		}
		return prefix + StringUtils.leftPad(num, 4, "0");
	}
	

	/**
	 * @Description: 商品总数
	 * @author: pmx
	 * @date: 2020年4月27日 上午10:53:59 
	 */
	public Integer summaryProdNum() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select ifnull(count(1),0) as prod_num ");
		sql.append(" from base_product t ");
		sql.append(" WHERE t.valid_flg = 'Y' ");
		return Db.queryLong(sql.toString()).intValue();
	}

	/**
	 * 2020年10月20日 下午7:56:21
	 * @param sellerId
	 * @return Object
	 * @Description 
	 * @author lu
	 */
	public Object find4FilterCond(String sellerId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 2020年10月20日 下午8:38:45
	 * @param entity
	 * @param loginUserRealName
	 * @return Ret
	 * @Description 
	 * @author lu
	 */
	public Ret saveOrUpdate(Product entity, String loginUserRealName) {
		
		if(StringUtils.isNotBlank(entity.getProductImages())) {
			//第一张图片当头图
			entity.setProductCover(entity.getProductImages().split(",")[1]);
		}
		
		if(entity.getId()==null) {
			if(entity.getOrderNum()==null) {
				entity.setOrderNum(1000);
			}
			entity.setId(KeyKit.uuid24());
			entity.setCreateDate(new Date());
			entity.setValidFlg("Y");
			entity.setCreator(loginUserRealName);
			entity.save();
		}else {
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserRealName);
			entity.update();
		}
		return RetKit.ok();
	}
}
