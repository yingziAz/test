package com.mj.kit.excel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader{
	Workbook wb = null;

	//excel 类型
	private String excelType;
	public ExcelReader(InputStream is,String excel_type){  
		try {
			if(is == null){
				throw new ExcelException("读取Excel数据失败");
			}
			excelType = excel_type;
			if(excelType.equals("2003")){
			    wb = new HSSFWorkbook(is);
			}else {
			    wb = new XSSFWorkbook(is);
            }
//			wb = WorkbookFactory.create(is);        
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}

	/**
	 * 判断是否存在sheet
	 * @param sheetName
	 * @return
	 */
	public boolean haveSheet(String sheetName){
		int sheetNum = wb.getNumberOfSheets();
		for(int i = 0;i<sheetNum;i++){
			if(StringUtils.equals(wb.getSheetName(i), sheetName)){
				//break;
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否存在sheet
	 * @param sheetName
	 * @return
	 */
	public boolean haveSheet(int sheetIndex){
		int sheetNum = wb.getNumberOfSheets();
		return sheetIndex <=sheetNum? true:false;
	}
	/** 
	 * 返回Excel最大行index值，实际行数要加1 
	 * @return 
	 */  
	public int getRowNum(int sheetIndex){  
		Sheet sheet = wb.getSheetAt(sheetIndex);  
		return sheet.getLastRowNum();  
	}
	/** 
	 * 返回数据的列数 
	 * @return  
	 */  
	public int getColumnNum(int sheetIndex){  
		Sheet sheet = wb.getSheetAt(sheetIndex);  
		Row row = sheet.getRow(0);  
		if(row!=null&&row.getLastCellNum()>0){  
			return row.getLastCellNum();  
		}  
		return 0;  
	}
	/**
	 * read data from sheet
	 * @param sheetIndex
	 * @return
	 */
	public List<List<Object>> readSheet(int sheetIndex){
		if(!haveSheet(sheetIndex)){
			return null;
		}
		if(StringUtils.endsWith(ExcelTypeEnum.EXCEL2003.getCode(), excelType)){
			return readSheet2003(sheetIndex);
		}else if(StringUtils.endsWith(ExcelTypeEnum.EXCEL2007.getCode(), excelType)){
			return readSheet2007(sheetIndex);
		}
		return null;
	}

	/**
	 * read data from sheet name
	 * @param sheetIndex
	 * @return
	 */
	public List<List<Object>> readSheet(String sheetName){
		if(!haveSheet(sheetName)){
			return null;
		}
		if(StringUtils.endsWith(ExcelTypeEnum.EXCEL2003.getCode(), excelType)){
			return readSheet2003(sheetName);
		}else if(StringUtils.endsWith(ExcelTypeEnum.EXCEL2007.getCode(), excelType)){
			return readSheet2007(sheetName);
		}
		return null;
	}

	private List<List<Object>> readSheet2003(String sheetName){
		HSSFSheet sheet = (HSSFSheet)wb.getSheet(sheetName);
		try {
			return readSheet2003(sheet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	private List<List<Object>> readSheet2003(int sheetIndex){
		HSSFSheet sheet = (HSSFSheet)wb.getSheetAt(sheetIndex);
		try {
			return readSheet2003(sheet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	private List<List<Object>> readSheet2007(String sheetName){
		XSSFSheet sheet = (XSSFSheet)wb.getSheet(sheetName);
		try {
			return readSheet2007(sheet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	private List<List<Object>> readSheet2007(int sheetIndex){
		XSSFSheet sheet = (XSSFSheet)wb.getSheetAt(sheetIndex);
		try {
			return readSheet2007(sheet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取 office 2003 excel
	 * @param sheet_name 
	 * @throws IOException 
	 * @throws FileNotFoundException */
	private List<List<Object>> readSheet2003(HSSFSheet sheet) throws IOException{
		List<List<Object>> list = new LinkedList<List<Object>>();
		Object value = null;
		HSSFRow row = null;
		HSSFCell cell = null;

		for(int i = 0;i<= sheet.getPhysicalNumberOfRows();i++){
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new ArrayList<Object>();
			for (int j = 0; j < row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					value = "";
					linked.add(value);
					continue;
				}
				//DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
				//DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					value = StringUtils.trim(cell.getStringCellValue());
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					if(DateUtil.isCellDateFormatted(cell)){  
						value  = cell.getDateCellValue();  
					}else{ 
						cell.setCellType(Cell.CELL_TYPE_STRING);  
						String temp = StringUtils.trim(cell.getStringCellValue());  
						//判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串  
						if(temp.indexOf(".")>-1){  
							value = String.valueOf(new Double(temp)).trim();  
						}else{  
							value = temp.trim();  
						}  
					}
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
				case XSSFCell.CELL_TYPE_BLANK:
                    value = StringUtils.EMPTY;
                    break;
                case XSSFCell.CELL_TYPE_FORMULA:
                    try{
                        value = cell.getNumericCellValue();
                    }catch(Exception e){
                        e.printStackTrace();
                        value= cell.getStringCellValue();
                    }
                    break;
				default:
					value = StringUtils.trim(cell.toString());
				}
				if (value == null  ) {
					value =StringUtils.EMPTY;
				}
				linked.add(value);

			}
			list.add(linked);
		}

		return list;
	}


	/**
	 * 读取Office 2007 excel
	 * @param sheet_name 
	 * */
	private List<List<Object>> readSheet2007(XSSFSheet sheet) throws IOException {
		List<List<Object>> list = new LinkedList<List<Object>>();
		Object value = null;
		XSSFRow row = null;
		XSSFCell cell = null;
		for (int i = 4; i <  sheet.getPhysicalNumberOfRows(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			List<Object> linked = new ArrayList<Object>();
			for (int j = 0; j < row.getLastCellNum(); j++) {
				cell = row.getCell(j);
				if (cell == null) {
					value = StringUtils.EMPTY;
					linked.add(value);
					continue;
				}
				//	     DecimalFormat df = new DecimalFormat("0");// 格式化 number String 字符
				//	     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
				//	     DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
				value = cell.toString();
				switch (cell.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					value = StringUtils.trim(cell.getStringCellValue());
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					if(DateUtil.isCellDateFormatted(cell)){  
						value  = cell.getDateCellValue();  
					}else{ 
						cell.setCellType(Cell.CELL_TYPE_STRING);  
						String temp = StringUtils.trim(cell.getStringCellValue());  
						//判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串  
						if(temp.indexOf(".")>-1){  
							value = String.valueOf(new Double(temp)).trim();  
						}else{  
							value = temp.trim();  
						}  
					}
					//	      System.out.println(i+"行"+j+" 列 is Number type ; DateFormt:"+cell.getCellStyle().getDataFormatString());
					//	      if(".".equals(cell.getCellStyle().getDataFormatString())){
					//	        value = df.format(cell.getNumericCellValue());
					//	      } else if("General".equals(cell.getCellStyle().getDataFormatString())){
					//	        value =  cell.getNumericCellValue() ;
					//	      }else{
					//	       value =   cell.toString();
					//	      }
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.getBooleanCellValue();
					break;
                case XSSFCell.CELL_TYPE_FORMULA:
                    try{
                        value = cell.getNumericCellValue();
                    }catch(Exception e){
                        e.printStackTrace();
                        value= cell.getStringCellValue();
                    }
                    break;
				case XSSFCell.CELL_TYPE_BLANK:
					value = StringUtils.EMPTY;
					break;
				default:
					value = StringUtils.trim(cell.toString());
				}
				if (value == null ) {
					value = StringUtils.EMPTY;
				}
				linked.add(value);
			}
			list.add(linked);
		}
		return list;
	}

}
