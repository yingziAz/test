package com.mj.kit;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.github.sd4324530.jtuple.Tuple3;

public class MJExcelKit {

	/**
	 * 
	 * @param is
	 * @param map
	 * 
	 * Tuple3<Integer,Integer,Integer> = sheetNum,rowNum,cellNum
	 * @throws Exception
	 */

	public void getExcelCellValue(Workbook book,Map<Tuple3<Integer,Integer,Integer>,Object> map) throws Exception {

		//boolean isXSSFWorkbook = !(book instanceof HSSFWorkbook);
		for(Map.Entry<Tuple3<Integer,Integer,Integer>,Object> entry : map.entrySet()){     
			Tuple3<Integer,Integer,Integer> key  = entry.getKey();
			Sheet sheet = book.getSheetAt(key.get(0));
			Row row = sheet.getRow(key.get(1));
			if(row == null) {
				continue;
			}

			Cell cell = row.getCell(key.get(2));
			if(cell == null) {
				continue;
			}

			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				map.put(key, cell.getStringCellValue());
				break;
			/**
			case Cell.CELL_TYPE_NUMERIC:
				if(cell.isCellDateFormatted(cell)){  
					map.put(key, cell.getDateCellValue());
				}else{ 
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String temp = cell.getStringCellValue();  
					//判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串  
					if(temp.indexOf(".")>-1){  
						map.put(key, new Double(temp));
					}else{  
						map.put(key, new Double(temp.trim()));
					} 
				}
				break;
			*/
			case XSSFCell.CELL_TYPE_BOOLEAN:
				map.put(key, cell.getBooleanCellValue());
				break;
			case XSSFCell.CELL_TYPE_BLANK:
				map.put(key, "");
				break;
			default:
				map.put(key, cell.toString());
			}
		}
	}
}
