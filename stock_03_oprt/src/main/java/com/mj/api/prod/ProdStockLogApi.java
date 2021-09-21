package com.mj.api.prod;

import java.math.BigDecimal;
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
import com.mj.enums.StockTypeEnum;
import com.mj.kit.BigDecimalUtil;
import com.mj.model.base.Product;
import com.mj.model.biz.ProdStock;
import com.mj.model.biz.ProdStockLog;

/**
 * 2020年10月23日 下午7:50:54
 * 
 * @Description 商品库存日志表
 * @author lu
 */
public class ProdStockLogApi {

	public static final ProdStockLogApi api = new ProdStockLogApi();
	
	/**
	 * 排序
	 */
	@SuppressWarnings("serial")
	private static Map<String,String> sortConfigMap = new HashMap<String,String>(){{
		put("create_date","t.create_date");  
		put("creator","t.creator");  
		put("description","t.description");  
		put("log_type_str","t.log_type");  //1-入库 2-出库 3-退货
		put("price","t.price");  //价格
		put("prod_stock_id","t.prod_stock_id");  //库存id
		put("quantity","t.quantity");  //数量
		put("stock_attachs","t.stock_attachs");  //商品封面图
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
		sql.append(" FROM biz_prod_stock_log t");
		sql.append(" WHERE 1=1 ");
		List<Object> queryArgs = new ArrayList<Object>();

		String keyword = StringUtils.trim((String) cond.get(CoreConstant.SEARCH_KEYWORD));
		if (StringUtils.isNotEmpty(keyword)) {
			sql.append(" and (INSTR(t.quantity,?) > 0 OR INSTR(t.price,?) > 0 OR INSTR(t.description,?) > 0  OR INSTR(t.create_date,?) > 0 )");
			queryArgs.add(keyword);
			queryArgs.add(keyword);
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
	public Ret saveOrUpdate(ProdStockLog entity, String loginUserRealName) {
		ProdStock stock = ProdStockApi.api.findByProdId(entity.getProdId());
		if(stock==null) {
			stock=ProdStockApi.api.save(entity.getProdId(), loginUserRealName);
		}
		Product product = Product.dao.findById(entity.getProdId());
		product.setId(entity.getProdId());
		if(entity.getPrice()!=null) {
			if(entity.getLogType()==StockTypeEnum.WAREHOUSING.getOrdinal()) {
				//入库价格
				product.setPrice(entity.getPrice());
			}else {
				//出库价格
				product.setOutPrice(entity.getPrice());
			}
			
		}
		if (entity.getId() == null) {
			entity.setProdStockId(stock.getId());
			entity.setCreateDate(new Date());
			entity.setCreator(loginUserRealName);
			entity.save();
			
			BigDecimal prodfit = BigDecimal.ZERO;
			if(entity.getPrice()!=null) {
				prodfit =BigDecimalUtil.multiply(entity.getPrice(), entity.getQuantity());
			}
			if(entity.getLogType()==StockTypeEnum.WAREHOUSING.getOrdinal()) {
				stock.setStockNum(stock.getStockNum()+entity.getQuantity());
				product.setTotalProfit(BigDecimalUtil.minus(product.getTotalProfit(), prodfit));
			}else {
				//出库
				stock.setStockNum(stock.getStockNum()-entity.getQuantity());
				product.setTotalProfit(BigDecimalUtil.plus(product.getTotalProfit(), prodfit));
			}
		} else {
			//新的利润
			BigDecimal newProdfit = BigDecimal.ZERO;
			//原利润
			BigDecimal oldProdfit = BigDecimal.ZERO;
			if(entity.getPrice()!=null) {
				newProdfit =BigDecimalUtil.multiply(entity.getPrice(), entity.getQuantity());
			}
			//编辑的场合，先减再做处理
			ProdStockLog log = ProdStockLog.dao.findById(entity.getId());
			stock.setStockNum(stock.getStockNum()-log.getQuantity());
			if(log.getPrice()!=null) {
				//原利润
				oldProdfit =BigDecimalUtil.multiply(log.getPrice(), log.getQuantity());
			}
			
			//利润差
			BigDecimal prodfit= BigDecimalUtil.minus(newProdfit, oldProdfit);
			
			if(entity.getLogType()==StockTypeEnum.WAREHOUSING.getOrdinal()) {
				stock.setStockNum(stock.getStockNum()+entity.getQuantity());
				product.setTotalProfit(BigDecimalUtil.minus(product.getTotalProfit(), prodfit));
			}else {
				//出库
				stock.setStockNum(stock.getStockNum()-entity.getQuantity());
				product.setTotalProfit(BigDecimalUtil.plus(product.getTotalProfit(), prodfit));
			}
			
			entity.setUpdateDate(new Date());
			entity.setUpdator(loginUserRealName);
			entity.update();
		}
		
		product.update();
		
		stock.setUpdateDate(new Date());
		stock.setUpdator(loginUserRealName);
		stock.update();
		
		//计算每日商品
		ProdDeailyApi.api.calculeToday(product.getId(), new Date());
		
		return RetKit.ok();
	}
	
	/**
	 * 2020年10月25日 下午1:55:00
	 * @param prodId
	 * @return List<ProdStockLog>
	 * @Description 根据商品id获取库存
	 * @author lu
	 */
	private List<ProdStockLog> findByProdId(String prodId){
		String sql = "select * from biz_prod_stock_log where prod_id=? order by id";
		return ProdStockLog.dao.find(sql,prodId);
		
	}
	
	
	/**
	 * 2020年10月25日 下午1:51:53
	 * @return Ret
	 * @Description  重新计算利润和库存
	 * @author lu
	 */
	public Ret recalculate(String prodId,String loginUserRealName) {
		List<ProdStockLog> logs  = findByProdId(prodId);
		if(logs==null || logs.size()==0) {
			return RetKit.fail("无库存记录，无需计算");
		}
		//利润
		BigDecimal prodfit= BigDecimal.ZERO;
		//库存
		Integer stockNum = 0;
		System.out.println(prodId);
		for (ProdStockLog log : logs) {
			if(log.getPrice()==null) {
				log.setPrice(BigDecimal.ZERO);
			}
			BigDecimal totalPrice =BigDecimalUtil.multiply(log.getPrice(), log.getQuantity());
			System.out.println(totalPrice);
			if(log.getLogType()==StockTypeEnum.WAREHOUSING.getOrdinal()) {
				stockNum =stockNum +log.getQuantity();
				prodfit = BigDecimalUtil.minus(prodfit,totalPrice);
			}else {
				//出库
				stockNum =stockNum - log.getQuantity();
				prodfit = BigDecimalUtil.plus(prodfit,totalPrice);
			}
		}
		Product product = new Product();
		product.setId(prodId);
		product.setTotalProfit(prodfit);
		product.setUpdateDate(new Date());
		product.setUpdator(loginUserRealName);
		product.update();
		
		ProdStock stock = ProdStockApi.api.findByProdId(prodId);
		stock.setStockNum(stockNum);
		stock.setUpdateDate(new Date());
		stock.setUpdator(loginUserRealName);
		stock.update();
		
		return RetKit.ok();
	}
}
