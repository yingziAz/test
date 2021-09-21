package com.mj.controller.prod;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.prod.ProdDeailyApi;
import com.mj.api.prod.ProductApi;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.kit.DateUtil;
import com.mj.model.base.Product;
import com.mj.model.biz.ProdDeaily;

/**
 * 2020年10月23日 下午9:04:05
 * @Description 商品每日日志
 * @author lu
 */
public class ProdDeailyController extends BaseController {
	
	/**
	 * 2020年10月23日 下午10:37:51 void
	 * @Description 每日商品列表
	 * @author lu
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
		Page<Record> page = ProdDeailyApi.api.page(pageNo, pageSize, cond);
		for(Record r : page.getList()) {
			r.set("deaily", DateUtil.dateToStr(r.getDate("deaily")));
		}
		renderJson(page);
	}
	
	/**
	 * 2020年10月23日 下午9:06:08 void
	 * @Description 编辑、新增商品每日记录
	 * @author lu
	 */
	public void edit() {
		Integer id = getParaToInt("id", null);
		String prodId = getPara("prodId");
		ProdDeaily deaily = ProdDeaily.dao.findById(id);
		if(deaily==null) {
			deaily = new ProdDeaily();
			deaily.setProdId(prodId);
			deaily.setDeaily(new Date());
		}
		Product prod = ProductApi.api.findById(deaily.getProdId());
		setAttr("prod", prod);
		createFromToken();
		setAttr("entity", deaily);
		render("prod_deaily_edit.html");
	}
	
	/**
	 * 2020年10月23日 下午9:12:55 void
	 * @Description 更新保存商品日志信息
	 * @author lu
	 */
	@Before({FormTokenInterceptor.class,Tx.class})
	public void saveOrUpdate() {	
		ProdDeaily entity = getBean(ProdDeaily.class, "entity");
		Ret ret = ProdDeailyApi.api.saveOrUpdate(entity,getLoginUserRealName());
		if (ret.isOk()) {
			Product prod = ProductApi.api.findById(entity.getProdId());
			sysLog(MessageFormat.format("保存商品每日记录，商品编号:{0},日期：{1}。", prod.getProductNo(),DateUtil.dateToStr(entity.getDeaily())));
		}else {
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}
	
	/**
	 * @Description:统计图
	 * @author lu
	 * @version 2020 年 03 月 20 日  16:36:40 
	 */
	public void index() {
		
//		Date statStartDate=DateUtil.plusMonth(new Date(), -1);
		Date statStartDate=DateUtil.plusDate(new Date(), -7);
		Date statEndDate=new Date();
		List<String> xData = Lists.newArrayList();
		
		List<Record> list =ProdDeailyApi.api.findByStat(getPara("prodId"),statStartDate,statEndDate);
		Map<String, Object> map = Maps.newHashMap();
		for(Record record :list ) {
			//数量
			map.put(DateUtil.dateToStr(record.getDate("deaily"), "yyyyMMdd"), record.getInt("stock"));
		}
		List<Object> line1Data = statCommon(statStartDate, statEndDate, map, xData);
		map = Maps.newHashMap();
		for(Record record :list ) {
			//价格
			map.put(DateUtil.dateToStr(record.getDate("deaily"), "yyyyMMdd"), record.getBigDecimal("price"));
		}
		List<Object> line2Data = statCommon(statStartDate, statEndDate, map, xData);
		map = Maps.newHashMap();
		for(Record record :list ) {
			//价格
			map.put(DateUtil.dateToStr(record.getDate("deaily"), "yyyyMMdd"), record.getBigDecimal("out_price"));
		}
		List<Object> line3Data = statCommon(statStartDate, statEndDate, map, xData);
		map = Maps.newHashMap();
		for(Record record :list ) {
			//价格
			map.put(DateUtil.dateToStr(record.getDate("deaily"), "yyyyMMdd"), record.getBigDecimal("profit"));
		}
		List<Object> line4Data = statCommon(statStartDate, statEndDate, map, xData);
		
		Map<String,Object> data = Maps.newHashMap();
		data.put("xData", xData);
		data.put("line1Data",line1Data);
		data.put("line2Data",line2Data);
		data.put("line3Data",line3Data);
		data.put("line4Data",line4Data);
		data.put("rangeDateStr", DateUtil.dateToStr(statStartDate) +" 至 " + DateUtil.dateToStr(statEndDate));
		
		renderJson(RetKit.ok(data));
	}
	
	protected List<Object> statCommon(Date statStartDate, Date statEndDate, Map<String, Object> map, List<String> xData) {
		List<Object> line1Data = Lists.newArrayList();
		boolean statByDate = true;
		// 获取两种日期的间隔天数
		Integer interval = DateUtil.getDays(statStartDate, statEndDate);
		// 大于150 的场合
		if (interval > 150) {
			statByDate = false;
		}
		if (statByDate) {
			for (int i = 0; i <= interval; i++) {
				// 数据库查询出来的日期 可能为 2019-10-01 ， 2019-10-03
				// 需要显示 2019-10-01 ，2019-10-02 ， 2019-10-03
				// 下列方法，使横坐标每个日期都有数据，如果当天没有数据，则默认为0
				String _date = DateUtil.dateToStr(DateUtil.plusDate(statStartDate, i), "yyyyMMdd");
				if (!xData.contains(_date)) {
					xData.add(_date);
				}
				if (map.containsKey(_date)) {
					line1Data.add(map.get(_date));
				} else {
					line1Data.add(0);
				}
			}
		} else {
			String statStartMonth = DateUtil.dateToStr(statStartDate, "yyyyMM");
			String statEndMonth = DateUtil.dateToStr(statEndDate, "yyyyMM");
			
			interval = DateUtil.getMonthInterval(statStartMonth, statEndMonth);
			for (int i = 0; i <= interval; i++) {
				String _month = DateUtil.plusYearMonth(statStartMonth, i);
				if (!xData.contains(_month)) {
					xData.add(_month);
				}
				if (map.containsKey(_month)) {
					line1Data.add(map.get(_month));
				} else {
					line1Data.add(0);
				}
			}
		}
		return line1Data;
	}
	
	public void test() {
		//现在全是新增，问题没查
		Date startDate = DateUtil.plusMonth(new Date(), -1);
		Integer num  =DateUtil.getDays(startDate, new Date());
		Integer stockNum = 100;
		 Math.random();
		for(int i =0;i<=num;i++) {
			Date date = DateUtil.plusDate(startDate, i);
			StringBuilder sql = new StringBuilder();
			sql.append(" select t.id,ifnull(t.price,0)as price,ifnull(ps.stock_num,0) as stockNum,pd.id as deailId");
			sql.append(" FROM base_product t");
			sql.append("  LEFT JOIN biz_prod_stock ps ON ps.prod_id = t.id");
			sql.append("  LEFT JOIN biz_prod_deaily pd ON pd.prod_id = t.id and pd.deaily = '"+DateUtil.dateToStr(date)+"'");
			sql.append(" WHERE 1=1 ");
			List<Record> list = Db.find(sql.toString());
			List<ProdDeaily> updateList = Lists.newArrayList();
			List<ProdDeaily> saveList = Lists.newArrayList();
			if(list!=null) {
				for (Record r : list) {
					ProdDeaily deaily = new ProdDeaily();
					deaily.setPrice(new BigDecimal(Math.random()*100));
					deaily.setOutPrice(new BigDecimal(Math.random()*100));
					Integer changeNum = Integer.valueOf((int) (Math.random()*100));
					
					//分开两次写，是为了不跟生成的随机数有关联，只是为了随机正负数
					boolean changeType = Integer.valueOf((int) (Math.random()*100))>50;
					if(changeType) {
						stockNum +=changeNum;
					}else {
						//库存不可能小于0，因此，当减去变化数量小于0的话，则加变化数量
						if(stockNum>changeNum) {
							stockNum -=changeNum;
						}else {
							stockNum +=changeNum;
						}
						
					}
					deaily.setStock(stockNum);
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
		}
		renderJson(RetKit.ok());
	}

	
}
