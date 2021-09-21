package com.mj.controller.prod;

import static com.github.sd4324530.jtuple.Tuples.tuple;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.github.sd4324530.jtuple.Tuple2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionException;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mj.api.attach.AttachFileApi;
import com.mj.api.mst.ProdCategoryApi;
import com.mj.api.prod.ProductApi;
import com.mj.constant.AppConstant;
import com.mj.constant.CoreConstant;
import com.mj.controller.BaseController;
import com.mj.interceptor.FormTokenInterceptor;
import com.mj.kit.BigDecimalUtil;
import com.mj.kit.DateUtil;
import com.mj.kit.excel.PoiExcelExportUtils;
import com.mj.model.base.Product;

/**
 * 商品管理
 */
public class ProductController extends BaseController {
	
	/**
	 * @Description: 商品列表页面
	 * @author: pmx
	 * @date: 2020年3月20日 上午8:44:29
	 */
	public void index() {
		Map<String, Object> cond = Maps.newHashMap();
		if (StringUtils.equals("view", this.getPara("from"))) {
			cond = getSessionAttr(AppConstant.LIST_COND_COOKIE+"_product");
			if (cond == null) {
				cond = Maps.newHashMap();
			}
		}
		this.setAttr("cond", cond);
		List<String> warnings = Lists.newArrayList();
		warnings.add("操作提示：复制，只复制商品的分类、价格单位、数量单位。");
		this.setAttr("warnings", warnings);
		
		render("prod_maintain.html");
	}
	
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
		cond.put("filterValue", getPara("filterValue"));
		
		// 列表查询：每个页面都要这样写 - 固定代码快
		cond.put("pageNo", pageNo);
		cond.put("pageSize", pageSize);
		this.setSessionAttr(AppConstant.LIST_COND_COOKIE +"_product", cond);
		
		Page<Record> page = ProductApi.api.page(pageNo, pageSize, cond);
		
		for(Record r : page.getList()) {
			r.set("total_profit_str", BigDecimalUtil.printAmountAsThousandSeperator(r.getBigDecimal("total_profit"), "0"));
			r.set("valid_flg_str", StringUtils.equals(r.getStr("valid_flg"), "Y")?"有效":"无效");
		}
		renderJson(page);
	}
	
	/**
	 * 2020年10月20日 下午8:45:05 void
	 * @Description 编辑、新增、复制商品
	 * @author lu
	 */
	public void edit() {
		String id = getPara("id", null);
		Product entity = ProductApi.api.findById(id);
		if (entity == null) {
			entity = new Product();
			entity.setProductNo(ProductApi.api.getProductNo());
		}
		
		setAttr("entity", entity);
		//一级分类
		setAttr("categoryList", ProdCategoryApi.api.findByParentId(0));
		//图片、封面图
		setAttr("prodImages", AttachFileApi.api.findByParams(entity.getProductImages()));
		setAttr("productCoverAttach", AttachFileApi.api.findByParams(entity.getProductCover()));
		createFromToken();
		render("prod_edit.html");
	}
	/**
	 * 2020年10月20日 下午8:52:29 void
	 * @Description 复制商品
	 * @author lu
	 */
	public void copy() {
		String id = getPara("id", null);
		Product uEntity = ProductApi.api.findById(id);
		Product entity =new Product();
		entity.setCategoryL1Id(uEntity.getCategoryL1Id());
		entity.setCategoryL2Id(uEntity.getCategoryL2Id());
		entity.setProductNo(ProductApi.api.getProductNo());
		entity.setStockUnit(uEntity.getStockUnit());
		entity.setPriceUnit(uEntity.getPriceUnit());
		setAttr("entity", entity);
		//一级分类
		setAttr("categoryList", ProdCategoryApi.api.findByParentId(0));
		//图片、封面图
		setAttr("prodImages", AttachFileApi.api.findByParams(entity.getProductImages()));
		setAttr("productCoverAttach", AttachFileApi.api.findByParams(entity.getProductCover()));
		createFromToken();
		render("prod_edit.html");
	}
	
	
	/**
	 * 2020年10月20日 下午8:42:27 void
	 * @Description 更新保存商品信息
	 * @author lu
	 */
	@Before({FormTokenInterceptor.class,Tx.class})
	public void saveOrUpdate() {
		Product entity = getBean(Product.class, "entity");
		if (ProductApi.api.isUnique4ProductNo(entity.getProductNo(),entity.getId())) {
			renderJson(RetKit.fail(MessageFormat.format("商品编号：{0}，已被使用！", entity.getProductNo())));
			return;
		}
		
		Ret ret = ProductApi.api.saveOrUpdate(entity,getLoginUserRealName());
		if (ret.isOk()) {
			sysLog(MessageFormat.format("保存商品，商品编号:{0}。", entity.getProductNo()));
		}else {
			ret.set(CoreConstant.FORM_TOKEN, resetFormToken());
		}
		renderJson(ret);
	}
	
	
	/**
	 * 2020年10月23日 下午9:55:23 void
	 * @Description 更新有效性
	 * @author lu
	 */
	@Before({Tx.class})
	public void validUpdate() {
		String id = getPara("id");
		String validFlg = getPara("validFlg");
		Product entity = Product.dao.findById(id);
		entity.setValidFlg(validFlg);
		Ret ret = ProductApi.api.saveOrUpdate(entity,getLoginUserRealName());
		if (ret.isOk()) {
			String msg = StringUtils.equals("Y", validFlg)?"激活":"注销";
			sysLog(MessageFormat.format("{0}商品，商品编号:{1}。", msg,entity.getProductNo()));
		}
		renderJson(ret);
	}
	
	/**
	 * @Description: 商品详情页面
	 * @author: pmx
	 * @date: 2020年3月20日 下午3:57:18
	 */
	public void view() {
		String id = getPara("id");
		Record record = ProductApi.api.findViewById(id);
		if(record==null) {
			record= new Record();
		}
		record.set("total_profit_str", BigDecimalUtil.printAmountAsThousandSeperator(record.getBigDecimal("total_profit"), "0"));
		record.set("valid_flg_str", StringUtils.equals(record.getStr("valid_flg"), "Y")?"有效":"无效");
		setAttr("entity", record);
		setAttr("prodImages", AttachFileApi.api.findByParams(record.getStr("product_images")));
		setAttr("productCoverAttach", AttachFileApi.api.findByParams(record.getStr("product_cover")));
		render("prod_view.html");
	}
	
	/**
	 * 2020年10月25日 下午4:59:03 void
	 * @Description 刷新详情页
	 * @author lu
	 */
	public void refreshView() {
		String id = getPara("id");
		Record record = ProductApi.api.findViewById(id);
		if(record==null) {
			record= new Record();
		}
		record.set("total_profit_str", BigDecimalUtil.printAmountAsThousandSeperator(record.getBigDecimal("total_profit"), "0"));
		record.set("valid_flg_str", StringUtils.equals(record.getStr("valid_flg"), "Y")?"有效":"无效");
		renderJson(RetKit.ok(record));
	}
	
	
	/**
	 * 2020年10月24日 下午12:40:33 void
	 * @Description 下载商品
	 * @author lu
	 */
	public void download() {
		try{
			Map<String, Object> cond = this.getSessionAttr(AppConstant.LIST_COND_COOKIE+"_product");
			if(cond == null) {
				renderError(500);
				return;
			}
			HttpServletResponse resp = getResponse();

			String fileName = MessageFormat.format("商品列表_{0}.xls",DateUtil.dateToStr(new Date(), "yyMMddHHmmss"));

			resp.setContentType("application/octet-stream;charset=GB18030");
			resp.setHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes("GB18030"), "ISO-8859-1"));

			HSSFWorkbook wb = new HSSFWorkbook();
			wb.createSheet("商品列表"); 
			//-------------------------------------------------------------------------
			//修改点一：表格的sheet名称
			//-------------------------------------------------------------------------
			HSSFSheet sheet = wb.getSheetAt(0);
			//设置字体  
			HSSFFont numberFont10 = PoiExcelExportUtils.createFont(wb,10,"Times New Roman");
			HSSFFont textFont10 = PoiExcelExportUtils.createFont(wb,10,"宋体");
			//标题样式
			HSSFCellStyle title_Cell = PoiExcelExportUtils.createCellStyle(wb,
					PoiExcelExportUtils.createFont(wb,16,"微软雅黑"),HorizontalAlignment.CENTER);
			//打印detail 的表头
			HSSFCellStyle header_Cell = PoiExcelExportUtils.createCellStyle(wb,
					PoiExcelExportUtils.createFont(wb,10,"微软雅黑"),HorizontalAlignment.CENTER);

			HSSFCellStyle detail_textLeftCell = PoiExcelExportUtils.createCellStyle(wb, textFont10, HorizontalAlignment.LEFT);
			detail_textLeftCell.setShrinkToFit(true);
			HSSFCellStyle detail_textCenterCell = PoiExcelExportUtils.createCellStyle(wb, textFont10, HorizontalAlignment.CENTER);
			detail_textCenterCell.setShrinkToFit(true);
			//数值样式
//			HSSFCellStyle detail_numberCell = PoiExcelExportUtils.createCellStyle(wb, numberFont10, HorizontalAlignment.RIGHT);
			HSSFCellStyle detail_numberCenterCell = PoiExcelExportUtils.createCellStyle(wb, numberFont10, HorizontalAlignment.CENTER);
//			//金额样式
//			HSSFCellStyle detail_amountCell = PoiExcelExportUtils.createCellStyle(wb, numberFont10, HorizontalAlignment.RIGHT);
//			detail_amountCell.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
			//-------------------------------------------------------------------------
			List<Tuple2<String,Integer>> headerList = Lists.newArrayList();

			//修改点二：表格的header
			//列宽基数184
			headerList.add(tuple("序号",8*256 + 184));
			headerList.add(tuple("商品编号", 10*256 + 184));
			headerList.add(tuple("商品名称", 20*256 + 184));
			headerList.add(tuple("一级分类", 15*256 + 184));
			headerList.add(tuple("二级分类", 15*256 + 184));
			headerList.add(tuple("库存", 10*256 + 184));
			headerList.add(tuple("库存单位", 10*256 + 184));
			headerList.add(tuple("入库价格", 10*256 + 184));
			headerList.add(tuple("出库价格", 10*256 + 184));
			headerList.add(tuple("价格单位", 12*256 + 184));
			headerList.add(tuple("有效性", 8*256 + 184));
			
			//-------------------------------------------------------------------------
			int row = 0;
			//打印标题
			if(true) {
				//-------------------------------------------------------------------------
				//修改点三：表格的标题
				PoiExcelExportUtils.setCellValue(sheet, row, 0, "商品列表").setCellStyle(title_Cell);
				//-------------------------------------------------------------------------
				sheet.getRow(row).setHeightInPoints(30);
				CellRangeAddress cra =new CellRangeAddress(row, row, 0, headerList.size()-1); 
				sheet.addMergedRegion(cra);
				PoiExcelExportUtils.setBorderStyle(sheet, cra);
				row = row + 1;
			}
			if(true) {
				//打印表头
				int cellIdx = 0;
				for(Tuple2<String,Integer> tuple : headerList) {
					String columnLable = tuple.first;
					PoiExcelExportUtils.setCellValue(sheet, row, cellIdx,columnLable).setCellStyle(header_Cell);
					sheet.setColumnWidth(cellIdx,tuple.second);
					cellIdx++;
				}
				sheet.getRow(row).setHeightInPoints(28);
				row = row + 1;
			}
			
			Page<Record> records = ProductApi.api.page(1, 999999, cond);
			int i=1;
			for(Record record : records.getList()) {
				record.set("valid_flg_str", StringUtils.equals(record.getStr("valid_flg"), "Y")?"有效":"无效");
				int cellIdx = 0;
				//序号
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, i++).setCellStyle(detail_numberCenterCell);
				//商家编号
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("product_no")).setCellStyle(detail_textCenterCell);
				//商家名称
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("product_name")).setCellStyle(detail_textLeftCell);
				//一级分类
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("parent_category")).setCellStyle(detail_textLeftCell);
				//二级分类
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("child_category")).setCellStyle(detail_textLeftCell);
				//库存
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getInt("stock_num")).setCellStyle(detail_textLeftCell);
				//库存单位
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("stock_unit")).setCellStyle(detail_textLeftCell);
				//价格
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getBigDecimal("price")).setCellStyle(detail_textLeftCell);
				//价格
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getBigDecimal("out_price")).setCellStyle(detail_textLeftCell);
				//价格单位
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("price_unit")).setCellStyle(detail_textLeftCell);
				//有效性
				PoiExcelExportUtils.setCellValue(sheet, row, cellIdx++, record.getStr("valid_flg_str")).setCellStyle(detail_textCenterCell);
				
				sheet.getRow(row).setHeightInPoints(20);
				row++;
			}

			//明细BLOCK end
			OutputStream fOut = resp.getOutputStream();  
			wb.write(fOut);
			fOut.flush();
			resp.setStatus(HttpServletResponse.SC_OK);
			fOut.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}
}
