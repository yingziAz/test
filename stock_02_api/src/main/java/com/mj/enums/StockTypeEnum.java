package com.mj.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mj.core.CoreEnum;

/**
 * 日志类型
 * 1-运营端，2-商家端,3-买手pc端 4-买手手机端 5-APP端 6 小程序端
 * @author daniel.su
 * 		
 */
@SuppressWarnings("serial")
public class StockTypeEnum implements Serializable {
	
	public static final CoreEnum WAREHOUSING = new CoreEnum(1, "WAREHOUSING", "入库");
	public static final CoreEnum  WAREHOUSING_OUT = new CoreEnum(2, "WAREHOUSING_OUT", "出库");

	public static List<CoreEnum> list = list();

	public static CoreEnum valueOf(Integer ordinal) {
		if (ordinal == null) {
			return null;
		}
		switch (ordinal) {
		case 1:
			return WAREHOUSING;
		case 2:
			return WAREHOUSING_OUT;
		default:
			return null;
		}
	}

	public static String getDisp(Integer ordinal){
		if(ordinal == null) {
			return StringUtils.EMPTY;
		}
		switch (ordinal) {
		case 1:
			return (String)WAREHOUSING.getValue();
		case 2:
			return (String)WAREHOUSING_OUT.getValue();
		default:
			return StringUtils.EMPTY;
		}
	}

	//--------------------------------------内部方法---------------------------------
	private static List<CoreEnum> list() {
		if(list == null){
			list = new ArrayList<CoreEnum>();
			list.add(WAREHOUSING);
			list.add(WAREHOUSING_OUT);
		}
		return list;
	}
}
