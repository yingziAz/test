package com.mj.kit.excel;

/**
 * 是否枚举
 * @author daniel su
 *
 */
public enum ExcelTypeEnum {
	EXCEL2003("2003","xls"),
	EXCEL2007("2007","xlsx");

	private String code;
	private String type;
	
	
	private ExcelTypeEnum(String code, String type) {
		this.code = code;
		this.type = type;
	}
	
	public String getCode() {
		return code;
	}
	public String getType() {
		return type;
	}

	public boolean equals(String type) {
		return this.type.equals(type);
	}
	public static String getCodeByType(String _type){
		String result = null;
		for  (ExcelTypeEnum _enum : ExcelTypeEnum.values()) {  
            if  (_enum.getType().equals(_type)) {  
            	result =  _enum.getCode();
            	break;
            }  
        }
		return result;
	}




}
