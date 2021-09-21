package com.mj.kit.excel;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import com.mj.kit.DateUtil;

public class PoiExcelExportUtils {

	public static HSSFCell setCellValue(HSSFSheet sheet,int rowIndex,int cellnum,Object cellValue,Object ...args){
		if(sheet == null){
			return null;
		}
		HSSFRow row = sheet.getRow(rowIndex)==null?sheet.createRow(rowIndex):sheet.getRow(rowIndex);
		if(args == null) {
			row.setHeightInPoints(18);
		}else{

		}
		HSSFCell cell = row.getCell(cellnum)==null?row.createCell(cellnum):row.getCell(cellnum);
		if(cell == null){
			return null;
		}
		//如果cellValue 是空的场合，设置为字符串为空
		if(cellValue == null){
			cellValue = StringUtils.EMPTY;
			//return null;
		}
		if (cellValue instanceof BigDecimal) {  
			cell.setCellValue(((BigDecimal) cellValue).doubleValue());
		}else if (cellValue instanceof Date) {  
			cell.setCellValue(DateUtil.dateToStr((Date)cellValue));  
		}else if(cellValue instanceof Integer) {  
			cell.setCellValue((Integer)cellValue);  
		}else if(cellValue instanceof String) {  
			cell.setCellValue((String)cellValue);  
		}
		return cell;
	}

	/*   
	 * 数据列数据信息单元格样式 
	 */    
	public static HSSFCellStyle CellStyle(HSSFWorkbook workbook) {  
		// 设置字体  
		HSSFFont font = workbook.createFont();  
		//设置字体大小  
		font.setFontHeightInPoints((short)10);  
		//字体加粗  
		//font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		//设置字体名字   
		font.setFontName("宋体");  
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font); 
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(HorizontalAlignment.LEFT);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		return style;  
	}

	//默认数据列 字体样式
	public static HSSFFont createFont(HSSFWorkbook workbook,int fontHeight,String fontName){
		HSSFFont font = workbook.createFont();  
		font.setFontHeightInPoints((short)fontHeight);  
		font.setFontName(fontName);  
		return font;
	}
	

	//默认数据列 字体样式
	public static HSSFFont createFont(HSSFWorkbook workbook,int fontHeight,String fontName,boolean bold){
		HSSFFont font = workbook.createFont();  
		font.setFontHeightInPoints((short)fontHeight);  
		font.setFontName(fontName);  
		if(bold) {
			font.setBold(bold);
		}
		return font;
	}

	//默认数据列 字体样式
	public static HSSFFont defaultDataFontStyle(HSSFWorkbook workbook){
		// 设置字体  
		HSSFFont font = workbook.createFont();  
		//设置字体大小  
		font.setFontHeightInPoints((short)10);  
		//设置字体名字   
		font.setFontName("宋体");  
		return font;
	}
	/*   
	 * 数据列数据信息单元格样式 
	 */    
	public static HSSFCellStyle defaultCellStyle(HSSFWorkbook workbook) {
		HSSFFont font = defaultDataFontStyle(workbook);
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font);  
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(HorizontalAlignment.LEFT);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		return style;  
	}

	/*   
	 * 数据列数据信息单元格样式 
	 */    
	public static HSSFCellStyle amountCellStyle(HSSFWorkbook workbook) {
		HSSFFont font = createFont(workbook,10,"Times New Roman");
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font);  
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(HorizontalAlignment.RIGHT);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		return style;  
	}

	//合并单元格样式
	public static void setBorderStyle(Sheet sheet, CellRangeAddress region) {
		// 合并单元格左边框样式
		RegionUtil.setBorderLeft(1, region, sheet);
		RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
		// 合并单元格上边框样式
		RegionUtil.setBorderTop(1, region, sheet);
		RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
		// 合并单元格右边框样式
		RegionUtil.setBorderRight(1, region, sheet);
		RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
		// 合并单元格下边框样式
		RegionUtil.setBorderBottom(1, region, sheet);
		RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(), region, sheet);
	}


	/*   
	 * 数据列数据信息单元格样式 
	 */    
	public static HSSFCellStyle cellStyle(HSSFWorkbook workbook,HorizontalAlignment _alignment) {
		HSSFFont font = defaultDataFontStyle(workbook);
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font);  
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(_alignment);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		//设置单元格格式
		HSSFDataFormat format = workbook.createDataFormat(); 
		style.setDataFormat(format.getFormat("@")); 
		return style;  
	}
	
	/* 
	 * 数据列数据信息单元格样式 
	 */
	public static HSSFCellStyle createCellStyle(HSSFWorkbook workbook,HSSFFont font,HorizontalAlignment _alignment) {
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font);  
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(_alignment);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		//设置单元格格式
		HSSFDataFormat format = workbook.createDataFormat(); 
		style.setDataFormat(format.getFormat("@")); 
		return style;  
	}

	/*   
	 * 数据列数据信息单元格样式 
	 */    
	public HSSFCellStyle getStyle(HSSFWorkbook workbook) {  
		// 设置字体  
		HSSFFont font = workbook.createFont();  
		//设置字体大小  
		//font.setFontHeightInPoints((short)10);  
		//字体加粗  
		//font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		//设置字体名字   
		font.setFontName("Courier New");  
		//设置样式;   
		HSSFCellStyle style = workbook.createCellStyle();  
		//设置底边框;   
		style.setBorderBottom(BorderStyle.THIN);  
		//设置底边框颜色;    
		style.setBottomBorderColor(HSSFColor.BLACK.index);  
		//设置左边框;     
		style.setBorderLeft(BorderStyle.THIN);  
		//设置左边框颜色;   
		style.setLeftBorderColor(HSSFColor.BLACK.index);  
		//设置右边框;   
		style.setBorderRight(BorderStyle.THIN);  
		//设置右边框颜色;   
		style.setRightBorderColor(HSSFColor.BLACK.index);  
		//设置顶边框;   
		style.setBorderTop(BorderStyle.THIN);  
		//设置顶边框颜色;    
		style.setTopBorderColor(HSSFColor.BLACK.index);  
		//在样式用应用设置的字体;    
		style.setFont(font);  
		//设置自动换行;   
		style.setWrapText(false);  
		//设置水平对齐的样式为居中对齐;    
		style.setAlignment(HorizontalAlignment.CENTER);  
		//设置垂直对齐的样式为居中对齐;   
		style.setVerticalAlignment(VerticalAlignment.CENTER);  
		return style;  
	}

	/**
	 * Given a sheet, this method deletes a column from a sheet and moves
	 * all the columns to the right of it to the left one cell.
	 * 
	 * Note, this method will not update any formula references.
	 * 
	 * @param sheet
	 * @param column
	 */
	public static void deleteColumn(Sheet sheet, int columnToDelete ){
		int maxColumn = 0;
		for ( int r=0; r < sheet.getLastRowNum()+1; r++ ){
			Row row = sheet.getRow( r );

			// if no row exists here; then nothing to do; next!
			if ( row == null )
				continue;

			// if the row doesn't have this many columns then we are good; next!
			int lastColumn = row.getLastCellNum();
			if ( lastColumn > maxColumn )
				maxColumn = lastColumn;

			if ( lastColumn < columnToDelete )
				continue;

			for ( int x=columnToDelete+1; x < lastColumn + 1; x++ ){
				Cell oldCell    = row.getCell(x-1);
				if ( oldCell != null )
					row.removeCell( oldCell );

				Cell nextCell   = row.getCell( x );
				if ( nextCell != null ){
					Cell newCell = row.createCell( x-1, nextCell.getCellType() );
					cloneCell(newCell, nextCell);
				}
			}
		}
		// Adjust the column widths
		for ( int c=0; c < maxColumn; c++ ){
			sheet.setColumnWidth( c, sheet.getColumnWidth(c+1) );
		}
	}
	/*
	 * Takes an existing Cell and merges all the styles and forumla
	 * into the new one
	 */
	private static void cloneCell( Cell cNew, Cell cOld ){
		cNew.setCellComment( cOld.getCellComment() );
		cNew.setCellStyle( cOld.getCellStyle() );
		switch ( cNew.getCellType() ){
		case Cell.CELL_TYPE_BOOLEAN:{
			cNew.setCellValue( cOld.getBooleanCellValue() );
			break;
		}
		case Cell.CELL_TYPE_NUMERIC:{
			cNew.setCellValue( cOld.getNumericCellValue() );
			break;
		}
		case Cell.CELL_TYPE_STRING:{
			cNew.setCellValue( cOld.getStringCellValue() );
			break;
		}
		case Cell.CELL_TYPE_ERROR:{
			cNew.setCellValue( cOld.getErrorCellValue() );
			break;
		}
		case Cell.CELL_TYPE_FORMULA:{
			cNew.setCellFormula( cOld.getCellFormula() );
			break;
		}
		}
	}
}
