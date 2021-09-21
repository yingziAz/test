package com.mj.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mj.core.CoreEnum;

/**
 * @Description: 导入类型 1-商品导入 2-库存单导入
 */
@SuppressWarnings("serial")
public class ExcelImportTypeEnum implements Serializable {
	// 1-商品导入 2-库存单导入
	public static final CoreEnum PROD = new CoreEnum(1, "PROD", "商品导入");
	public static final CoreEnum PROD_STOCK = new CoreEnum(2, "PROD_STOCK", "入库单导入");
	public static final CoreEnum PROD_STOCK_TAKING = new CoreEnum(3, "PROD_STOCK_TAKING", "盘点库存导入");

	public static List<CoreEnum> list = list();

	public static CoreEnum valueOf(Integer ordinal) {
		if (ordinal == null) {
			return null;
		}
		switch (ordinal) {
		case 1:
			return PROD;
		case 2:
			return PROD_STOCK;
		case 3:
			return PROD_STOCK_TAKING;
		default:
			return null;
		}
	}

	public static String getDisp(Integer ordinal) {
		if (ordinal == null) {
			return StringUtils.EMPTY;
		}
		switch (ordinal) {
		case 1:
			return (String) PROD.getValue();
		case 2:
			return (String) PROD_STOCK.getValue();
		case 3:
			return (String) PROD_STOCK_TAKING.getValue();
		default:
			return StringUtils.EMPTY;
		}
	}

	// --------------------------------------内部方法---------------------------------
	private static List<CoreEnum> list() {
		if (list == null) {
			list = new ArrayList<CoreEnum>();
			list.add(PROD);
			list.add(PROD_STOCK);
			list.add(PROD_STOCK_TAKING);
		}
		return list;
	}

}
