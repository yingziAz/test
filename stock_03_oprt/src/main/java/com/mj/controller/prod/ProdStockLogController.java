package com.mj.controller.prod;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.attach.AttachFileApi;
import com.mj.api.prod.ProdStockLogApi;
import com.mj.api.prod.ProductApi;
import com.mj.constant.AppConstant;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.enums.StockTypeEnum;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.kit.BigDecimalUtil;
import com.mj.model.base.Product;
import com.mj.model.biz.ProdStockLog;

/**
 * 2020年10月23日 下午9:04:05
 * @Description 商品库存日志
 * @author lu
 */
public class ProdStockLogController extends BaseController {
	
	/**
	 * @Description: 商品列表
	 * @author: pmx
	 * @date: 2020年3月20日 上午8:41:42
	 */
	public void list() {
		Integer offset = 0, pageSize = 10;
		try {
			offset = this.getParaToInt("offset", 0);
			pageSize = this.getParaToInt("limit", 10);
		} catch (ActionException e) {
			// do nothing
		}
		Integer pageNo = 1;
		if (offset > 0) {
			pageNo = (offset / pageSize) + 1;
		}
		Map<String, Object> cond = new HashMap<String, Object>();
		// 搜索
		cond.put(CoreConstant.SEARCH_KEYWORD, getPara("search", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_FIELD, getPara("sort", StringUtils.EMPTY));
		cond.put(CoreConstant.ORDER_DIR, getPara("order", "asc"));

		//过滤条件
		cond.put("prodId", getPara("prodId"));
		
		// 列表查询：每个页面都要这样写 - 固定代码快
		cond.put("pageNo", pageNo);
		cond.put("pageSize", pageSize);
		setSessionAttr(AppConstant.LIST_COND_COOKIE +"_product_stock", cond);
		
		Page<Record> page = ProdStockLogApi.api.page(pageNo, pageSize, cond);
		for(Record r : page.getList()) {
			r.set("price", r.getBigDecimal("price")==null?BigDecimal.ZERO: r.getBigDecimal("price"));
			r.set("log_type_str", StockTypeEnum.getDisp(r.getInt("log_type")));
			r.set("total_price", BigDecimalUtil.multiply(r.getBigDecimal("price"), r.getInt("quantity")));
		}
		renderJson(page);
	}
	
	/**
	 * 2020年10月23日 下午9:06:08 void
	 * @Description 编辑、新增商品库存
	 * @author lu
	 */
	public void edit() {
		Integer id = getParaToInt("id", null);
		String prodId = getPara("prodId");
		//库存类型 1：入库 2：出库
		Integer logType = getParaToInt("logType");
		ProdStockLog log = ProdStockLog.dao.findById(id);
		if(log==null) {
			log = new ProdStockLog();
			log.setProdId(prodId);
			log.setLogType(logType);
		}
		log.put("logTypeStr",StockTypeEnum.getDisp(log.getLogType()));
		Product prod = ProductApi.api.findById(log.getProdId());
		setAttr("prod", prod);
		setAttr("entity", log);
		//附件
		setAttr("stockAttach", AttachFileApi.api.findByParams(log.getStockAttachs()));
		createFromToken();
		render("prod_stock_edit.html");
	}
	
	/**
	 * 2020年10月23日 下午9:12:55 void
	 * @Description 更新保存商品日志信息
	 * @author lu
	 */
	@Before({FormTokenInterceptor.class,Tx.class})
	public void saveOrUpdate() {
		ProdStockLog entity = getBean(ProdStockLog.class, "entity");
		Ret ret = ProdStockLogApi.api.saveOrUpdate(entity,getLoginUserRealName());
		if (ret.isOk()) {
			Product prod = ProductApi.api.findById(entity.getProdId());
			sysLog(MessageFormat.format("保存商品库存，商品编号:{0}。", prod.getProductNo()));
		}else {
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}
	
	/**
	 * 2020年10月23日 下午10:49:03 void
	 * @Description 库存记录详情
	 * @author lu
	 */
	public void view() {
		String id = getPara("id");
		ProdStockLog log = ProdStockLog.dao.findById(id);
		log.put("log_type_str", StockTypeEnum.getDisp(log.getLogType()));
		setAttr("entity", log);
		Product prod = ProductApi.api.findById(log.getProdId());
		setAttr("prod", prod);
		//附件
		setAttr("stockAttach", AttachFileApi.api.findByParams(log.getStockAttachs()));
		render("prod_stock_view.html");
	}
	
	/**
	 * 2020年10月25日 下午2:08:25 void
	 * @Description 重新计算商品的总利润和库存
	 * @author lu
	 */
	@Before({Tx.class})
	public void recalculate() {
		String prodId = getPara("prodId");
		Product product = Product.dao.findById(prodId);
		if(product==null) {
			renderJson(RetKit.fail("未找到商品"));
			return;
		}
		Ret ret = ProdStockLogApi.api.recalculate(prodId,getLoginUserRealName());
		if (ret.isOk()) {
			sysLog(MessageFormat.format("重新计算商品的总利润和库存，商品编号:{0}。", product.getProductNo()));
		}
		renderJson(ret);
	}
	
}
