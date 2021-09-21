package com.mj.kit.excel;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mj.kit.DateUtil;
import com.mj.kit.FileUtil;

public class ExcelUtils {
	
	/**
	 * @Description: 通过文件名称获取excel的类型：2003 or 2007
	 * @author su
	 * @version 2018 年 09 月 04 日  15:54:07 
	 * @param excelFileName
	 * @return
	 */
	public static String getExcelType(String excelFileName){
		if(StringUtils.isBlank(excelFileName)){
			return StringUtils.EMPTY;
		}
		String excelFileType = ExcelTypeEnum.getCodeByType(FileUtil.getExtensionName(excelFileName));
		if(StringUtils.isBlank(excelFileType)){
			return StringUtils.EMPTY;
		}
		return excelFileType;
	}
	
	/**
	 * @Description: 将excel的cell值转化为字符串 
	 * @author su
	 * @version 2018 年 09 月 04 日  15:40:47 
	 * @param obj
	 * @return 字符串
	 */
	public static String parseCellDataToString(Object obj) {
		if(obj == null) {
			return StringUtils.EMPTY;
		}
		//System.out.println(obj.getClass().getName());
		if(obj instanceof String ) {
			return (String)obj;
		}else if(obj instanceof Double ) {
			DecimalFormat df = new DecimalFormat("0");
			return df.format(obj);
		}else if(obj instanceof Integer ) {
			DecimalFormat df = new DecimalFormat("0");
			return df.format(obj);
		}else if(obj instanceof Date) {
			return DateUtil.dateToStr((Date)obj);
		}
		return obj.toString();
	}
}
