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
import com.mj.api.product.BaseProductDeailyApi;
import com.mj.constant.CoreConstant;
import com.mj.kit.DateUtil;
import com.mj.model.biz.ProdDeaily;

/**
 * 2020年10月23日 下午7:42:52
 * @Description 每日商品数据
 * @author lu
 */
public class ProdDeailyApi  extends BaseProductDeailyApi{
	
	public static final ProdDeailyApi api = new ProdDeailyApi();
	
	/**
	 * 2020年10月23日 下午9:51:49
	 * @param prodId
	 * @param deaily
	 * @return ProdDeaily
	 * @Description 根据商品id和日期获取
	 * @author lu
	 */
	public ProdDeaily findByParam(String prodId, Date deaily) {
		return ProdDeaily.dao.findFirst("select * from biz_prod_deaily where prod_id=? and deaily=?", prodId,
				DateUtil.dateToStr(deaily));
	}
	
	/**
	 * 排序
	 */
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("create_date","t.create_date");  
		put("creator","t.creator");  
		put("stock","t.stock");  //1-入库 2-出库 3-退货
		put("deaily","t.deaily");  //价格
		put("update_date","t.update_date");  
		put("updator","t.updator");  
	}};

	/**
	 * @Description:
	 * @author: Administrator
	 * @param:
	 * @return:
	 * @date: 2020年3月20日 上午8:47:10
	 */
	public Page<Record> page(Integer pageNo, Integer pageSize, Map<String, Object> cond) {
		String select = "SELECT t.*";
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM biz_prod_deaily t");
		sql.append(" WHERE 1=1 ");
		List<Object> queryArgs = new ArrayList<Object>();

		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sql.append(" and (INSTR(t.stock,?) > 0 OR INSTR(t.deaily,?) > 0 )");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
		}
		//商品id
		String prodId = (String)cond.get("prodId");
		sql.append(" and t.prod_id=?");
		queryArgs.add(prodId);

		String orderField = StringUtils.trim((String) cond.get(CoreConstant.ORDER_FIELD));
		// 默认排序
		if (StringUtils.isNotEmpty(orderField) && sortConfigMap.containsKey(orderField)) {
			sql.append(" order by ").append(sortConfigMap.get(orderField)).append(" ")
					.append(StringUtils.trim((String) cond.get(CoreConstant.ORDER_DIR)));
		} else {
			sql.append(" order by t.create_date desc,t.id desc");
		}
		return Db.paginate(pageNo, pageSize, select, sql.toString(), queryArgs.toArray());
	}
	
	/**
	 * 2020年10月20日 下午8:38:45
	 * @param entity
	 * @param loginUserRealName
	 * @return Ret
	 * @Description
	 * @author lu
	 */
	public Ret saveOrUpdate(ProdDeaily entity, String loginUserRealName) {
		ProdDeaily uEntity = findByParam(entity.getProdId(),entity.getDeaily());
		if(uEntity!=null) {
			entity.setId(uEntity.getId());
		}
		if(entity.getId()==null) {
			entity.setCreateDate(new Date());
			entity.setCreator(loginUserRealName);
			entity.save();
		}else {
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserRealName);
			entity.update();
		}
		return RetKit.ok();
	}
	
	/**
	 * 2020年10月24日 下午1:32:37
	 * @param prodId
	 * @return 用于统计
	 * @Description 
	 * @author lu
	 * @param statEndDate 
	 * @param statStartDate 
	 */
	public List<Record> findByStat(String prodId, Date statStartDate, Date statEndDate) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.*");
		sql.append(" FROM biz_prod_deaily t");
		sql.append(" WHERE 1=1 ");
		List<Object> queryArgs = new ArrayList<Object>();
		//商品id
		sql.append(" and t.prod_id=?");
		queryArgs.add(prodId);
		sql.append(" and t.deaily>=?");
		queryArgs.add(DateUtil.dateToStr(statStartDate));
		sql.append(" and t.deaily<=?");
		queryArgs.add(DateUtil.dateToStr(statEndDate));
		return Db.find(sql.toString(),queryArgs.toArray());
	}
	
}
