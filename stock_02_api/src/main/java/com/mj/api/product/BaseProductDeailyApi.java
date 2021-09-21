package com.mj.api.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mj.kit.BigDecimalUtil;
import com.mj.kit.DateUtil;
import com.mj.model.biz.ProdDeaily;

/**
 * 2020年10月25日 下午4:46:50
 * @Description 每日商品库存
 * @author lu
 */
public class BaseProductDeailyApi {

	public static final BaseProductDeailyApi api = new BaseProductDeailyApi();
	
	/**
	 * 2020年10月25日 下午4:47:53
	 * @param prodId
	 * @param date
	 * @return Ret
	 * @Description 生成每日商品  
	 * @author lu
	 */
	public Record calculeToday(String prodId,Date date) {
		if(date==null) {
			date=new Date();
		}
		String dateStr = DateUtil.dateToStr(date);
		List<Object> queryArgs = new ArrayList<Object>();

		//获取库存、入库价格、出库价格
		StringBuilder sql = new StringBuilder();
		sql.append(" select t.id,ifnull(t.price,0)as price,ifnull(t.out_price,0)as out_price,ifnull(ps.stock_num,0) as stockNum,pd.id as deailId");
		sql.append(" FROM base_product t");
		sql.append("  LEFT JOIN biz_prod_stock ps ON ps.prod_id = t.id");
		sql.append("  LEFT JOIN biz_prod_deaily pd ON pd.prod_id = t.id and pd.deaily = '"+dateStr+"'");
		sql.append(" WHERE 1=1 ");
		if(StringUtils.isNotBlank(prodId)) {
			sql.append("  AND t.id=? ");
			queryArgs.add(prodId);
		}
		List<Record> list = Db.find(sql.toString(),queryArgs.toArray());
		//获取利润
		sql = new StringBuilder();
		sql.append(" SELECT SUM(case when t.log_type = 1 then ifnull(t.price,0) * t.quantity ELSE 0 END) AS reduce,");
		sql.append("        SUM(case when t.log_type = 2 then ifnull(t.price,0) * t.quantity ELSE 0 end) AS increase,");
		sql.append("        t.prod_id");
		sql.append(" FROM biz_prod_stock_log t ");
		sql.append(" WHERE DATE_FORMAT( t.create_date ,'%Y-%m-%d') = '"+dateStr+"'");
		if(StringUtils.isNotBlank(prodId)) {
			sql.append("  AND t.prod_id=? ");
		}
		sql.append(" GROUP BY t.prod_id");
		List<Record> profits = Db.find(sql.toString(),queryArgs.toArray());
		
		Map<String,BigDecimal> map = Maps.newHashMap();
		if(profits!=null) {
			for (Record record : profits) {
				map.put(record.getStr("prod_id"), BigDecimalUtil.minus(record.getBigDecimal("increase"), record.getBigDecimal("reduce")));
			}
		}
		List<ProdDeaily> updateList = Lists.newArrayList();
		List<ProdDeaily> saveList = Lists.newArrayList();
		if(list!=null) {
			for (Record r : list) {
				ProdDeaily deaily = new ProdDeaily();
				BigDecimal profit = map.get(r.getStr("id"));
				deaily.setProfit(profit==null?BigDecimal.ZERO:profit);
				deaily.setOutPrice(r.getBigDecimal("out_price"));
				deaily.setPrice(r.getBigDecimal("price"));
				deaily.setStock(r.getInt("stockNum"));
				if(r.getInt("deailId")==null) {
					deaily.setDeaily(date);
					deaily.setProdId(r.getStr("id"));
					deaily.setCreator("batch");
					deaily.setCreateDate(new Date());
					saveList.add(deaily);
				}else {
					deaily.setId(r.getInt("deailId"));
					deaily.setUpdateDate(new Date());
					deaily.setUpdator("batch");
					updateList.add(deaily);
				}
			}
		}
		if(saveList.size()>0) {
			Db.batchSave(saveList, 1000);
		}
		if(updateList.size()>0) {
			Db.batchUpdate(updateList, 1000);
		}
		Record r = new Record();
		r.set("saveNum", saveList.size());
		r.set("updateNum", updateList.size());
		return r;
	}
	
	
}
