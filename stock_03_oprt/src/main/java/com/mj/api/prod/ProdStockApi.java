package com.mj.api.prod;

import java.util.Date;

import com.mj.kit.KeyKit;
import com.mj.model.biz.ProdStock;

/**
 * 2020年10月23日 下午7:50:35
 * 
 * @Description 商品库存表
 * @author lu
 */
public class ProdStockApi {

	public static final ProdStockApi api = new ProdStockApi();

	public ProdStock findByProdId(String prodId) {
		return ProdStock.dao.findFirst("select * from biz_prod_stock where prod_id=?", prodId);
	}

	/**
	 * 2020年10月23日 下午8:33:24
	 * @param prodId
	 * @param loginUserRealName
	 * @return ProdStock
	 * @Description 新增库存
	 * @author lu
	 */
	public ProdStock save(String prodId, String loginUserRealName) {
		ProdStock stock = new ProdStock();
		stock.setId(KeyKit.uuid24());
		stock.setBookNum(0);// 线上库存
		stock.setProdId(prodId);
		stock.setStockNum(0);// 实际库存
		stock.setCreateDate(new Date());
		stock.setCreator(loginUserRealName);
		stock.save();
		return stock;
	}

	public ProdStock update(ProdStock stock, String loginUserRealName) {
		stock.setUpdateDate(new Date());
		stock.setUpdator(loginUserRealName);
		stock.save();
		return stock;
	}
}
