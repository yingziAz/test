package com.mj.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
public class ToolKit { 
	private static String driver = "com.mysql.jdbc.Driver"; 
	private static String url = "jdbc:mysql://127.0.0.1:3306/"; 
	private static String db = "prj_stock"; 
	private static String user = "root";
	private static String pass = "127814";
	static Connection conn = null; 
	static Statement statement = null; 
	static PreparedStatement ps = null; 
	static ResultSet rs = null; 

	static List<Map<String,String>> columns = new ArrayList<Map<String,String>>(); 
	
	public static void main(String[] args) { 
		startMySQLConn(); //开始连接
		getColums("biz_prod_stock"); //根据表名获取字段，字段如"CompanyName"
//		printColums4Map();//排序字段 （驼峰字段 companyName）  用于module
//		printColums4List();//页面列表  （驼峰字段 companyName）  用于module
		genUsefulSource();//保存字段
//		printColums4Export();//导出
//		printColums();//导出注释+名字+原名字
//		printColums4OldMap();//排序字段 （原生字段 company_name） 用于多表连接，record
//		printColums4OldList();//页面列表  （原生字段 company_name） 用于多表连接，record
//		printColums4ExportOld();//导出
		
//		printEditPage();//导出编辑页面字段
		closeMySQLConn();//关闭连接
	} 

	public static void startMySQLConn() { 
		try { 
			Class.forName(driver).newInstance(); 
			conn = DriverManager.getConnection(url+db+"?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC", user, pass); 
			if (!conn.isClosed()) { 
				System.out.println("Succeeded connecting to MySQL!"); 
			} 

			statement = conn.createStatement(); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 

	public static void closeMySQLConn() { 
		if(conn != null){ 
			try { 
				conn.close(); 
				System.out.println("Database connection terminated!"); 
			} catch (SQLException e) { 
				e.printStackTrace(); 
			} 
		} 
	} 

	
	public static void getColums(String tableName) { 
		String sql = "select  column_name, column_comment,is_nullable,column_type from information_schema.columns where table_schema ='"+db+"' and table_name = '"+tableName+"' ;";
		try { 
			ps = conn.prepareStatement(sql); 
			rs = ps.executeQuery(); 
			String separatorChar ="_";
			while (rs.next()) {
				Map<String,String> columnElemMap = new HashMap<String,String>();
				columnElemMap.put("Field", rs.getString(1));//原字段
				columnElemMap.put("zhushi", rs.getString(2));
				columnElemMap.put("isNull", rs.getString(3));
				int finalLength = 0;
//				String length = "int(11)";
				String length =  rs.getString(4);
				//datatime 没有长度
				if(!"datetime".equals(length) && !("date".equals(length)) && length.contains("(")){
					String length2 = length.substring(length.indexOf("(")+1, length.length()-1);
					String[] lenghts = length2.split(",");
					//decimal长度+1
					if(lenghts.length>1){
						finalLength = Integer.parseInt(lenghts[0])+1;
					}else{
						finalLength = Integer.parseInt(lenghts[0]);
					}
				}
				columnElemMap.put("columnLength", finalLength+"");
				String field = columnElemMap.get("Field");
				String javaFieldName ="" ;
				if(StringUtils.contains(field, separatorChar)){
					String[] fieldPieces = StringUtils.split(field, separatorChar);
					int i = 0;
					for(String fieldPiece : fieldPieces){
						if(i++ == 0){
							javaFieldName +=fieldPiece;
							continue;
						}
						javaFieldName +=fieldPiece.substring(0, 1).toUpperCase()+ fieldPiece.substring(1);
					}
				}else{
					javaFieldName = field;
				}
				columnElemMap.put("JavaField", javaFieldName);//驼峰字段
				
				columns.add(columnElemMap);
			} 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	}
	
	
	public static void printColums4ExportOld() {
		System.out.println("=====导出字段开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = columnElem.get("Field");
			String data = "entity.get(\""+a+"\")";
			String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":("//"+columnElem.get("zhushi"));
			String sheet="sheet";//表变量
			String colIdx="colIdx";//表变量
			System.out.println(zhushi);
			System.out.println("PoiExcelExportUtils.setCellValue("+sheet+",detailRow,"+colIdx+"++,"+data+")\n.setCellStyle(dataCellStyleC);");
		}
	} 
	
	
	public static void printColums4Export() {
		System.out.println("=====导出字段开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = firstLetterToUpperCase(columnElem.get("JavaField"));
			String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":("//"+columnElem.get("zhushi"));
			String data = "entity.get"+a+"()";
			String sheet="sheet";//表变量
			String colIdx="colIdx";//表变量
			System.out.println(zhushi);
			System.out.println("PoiExcelExportUtils.setCellValue("+sheet+",detailRow,"+colIdx+"++,"+data+")\n.setCellStyle(dataCellStyleC);");
		}
	} 
	
	//导出注释+名字+原名字
	public static void printColums() {
		System.out.println("=====导出字段开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = columnElem.get("JavaField");
			String b = columnElem.get("Field");
			String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":(columnElem.get("zhushi"));
			System.out.println(zhushi +" | "+ a+" | "+b);
		}
	} 
	
	//导出排序字段注释+名字+原名字
	public static void printColums4Map() {
		System.out.println("=====导出排序字段开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = columnElem.get("JavaField");
			String b = columnElem.get("Field");
			String zhushi = StringUtils.isBlank(columnElem.get("zhushi"))?"":("//"+columnElem.get("zhushi"));
			System.out.println("put(\"" + a+"\",\"t."+b+"\");  "+zhushi);
		}
	} 
	
	//导出排序字段(原字段)注释+名字+原名字
	public static void printColums4OldMap() {
		System.out.println("=====导出排序字段开始=====");
		for(Map<String,String> columnElem : columns) {
			//String a = columnElem.get("JavaField");
			String b = columnElem.get("Field");
			String zhushi = StringUtils.isBlank(columnElem.get("zhushi"))?"":("//"+columnElem.get("zhushi"));
			System.out.println("put(\"" + b+"\",\"t."+b+"\");  "+zhushi);
		}
	} 
	
	
	//导出列表（驼峰字段 companyName）
	public static void printColums4List() {
		System.out.println("=====导出页面列表开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = columnElem.get("JavaField");
			String zhushi = StringUtils.isBlank(columnElem.get("zhushi"))?"":columnElem.get("zhushi");
			System.out.println("<th data-field=\""+a+"\" data-sortable=\"true\" data-align=\"center\">"+zhushi+"</th>");
		}
	} 
	
	//导出列表（原生字段 company_name）
	public static void printColums4OldList() {
		System.out.println("=====导出页面列表开始=====");
		for(Map<String,String> columnElem : columns) {
			String b = columnElem.get("Field");
			String zhushi = StringUtils.isBlank(columnElem.get("zhushi"))?"":columnElem.get("zhushi");
			System.out.println("<th data-field=\""+b+"\" data-sortable=\"true\" data-align=\"center\">"+zhushi+"</th>");
		}
	} 
	

	public static void genUsefulSource() {
		//list 片段
		System.out.println("=====保存字段开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = firstLetterToUpperCase(columnElem.get("JavaField"));
			String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":("//"+columnElem.get("zhushi"));
			String data = "entity.get"+a+"()";
			if(StringUtils.equals("CreateDate", a)||StringUtils.equals("UpdateDate", a)){
				data="new Date()";
			}
			if(StringUtils.equals("Creator", a)||StringUtils.equals("Updator", a)){
				data="loginUserName";
			}
			System.out.println(" uEntity.set"+a+"("+columnElem.get("JavaField")+");"+zhushi +"");
//			System.out.println(" uEntity.set"+a+"("+data+");"+zhushi +"");
		}
	} 
	
	public static void printEditPage() {
		//list 片段
		System.out.println("=====输出页面编辑开始=====");
		for(Map<String,String> columnElem : columns) {
			String a = "entity."+columnElem.get("JavaField");//字段名
			String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":(columnElem.get("zhushi"));//注释
			String isNull ="NO".equals(columnElem.get("isNull"))?"必须输入项":"非必须输入项";
			String maxLength = columnElem.get("columnLength"); 
			if(!"0".equals(maxLength)){
				maxLength = "maxlength=\""+maxLength+"\"";
			}else{
				maxLength = "";
			}
			String b = "entity."+ columnElem.get("Field");
			if(!"id".equals(columnElem.get("JavaField"))){
				System.out.println("<div class=\"form-group row\">");
				System.out.println("\t<label class=\"col-md-3 form-control-label\">${ie8n['"+zhushi+"：']}");
				System.out.println("\t\t<span class=\"required\" style=\"color: red;\">*</span>");
				System.out.println("\t</label>");
				System.out.println("\t<div class=\"col-md-7\">");
				System.out.println("\t\t<div class=\"input-group\">");
				System.out.println("\t\t\t<input type=\"text\" class=\"form-control\" name=\""+a+"\" value=\"${"+b+"!''}\" placeholder=\"${ie8n['请输入"+zhushi+"']}\"  "+maxLength+" />");
				System.out.println("\t\t</div>");
				System.out.println("\t</div>");
				System.out.println("</div>");
			}
//			if(!"id".equals(columnElem.get("JavaField"))){
//				System.out.print("<div class=\"form-group\">\n");
//				System.out.print("\t<label class=\"col-md-3 control-label\">"+zhushi+"</label>\n");
//				System.out.print("\t<div class=\"col-md-4\">\n");
//				System.out.print("\t\t<input type=\"text\" name=\""+a+"\" placeholder=\""+zhushi+"，"+isNull+"\" class=\"form-control\"\n");
//				System.out.print("\t\t\tvalue=\"${"+a+"!''}\" "+maxLength+">\n");
//				System.out.print("\t</div>\n");
//				System.out.print("</div>\n");
//			}
			
			
		}
	} 
	
	/**
	 * @Description: 输出页面不为空验证开始	
	 * @author lu
	 * @version 2018年1月8日 上午8:27:49 
	 */
	public static void printEditValidatePage() {
		//list 片段
		System.out.println("=====输出页面不为空验证开始=====");
		System.out.println("rules : {");
		for(Map<String,String> columnElem : columns) {
			if("NO".equals(columnElem.get("isNull"))&&!"id".equals(columnElem.get("JavaField"))){
				String a = "\t\"entity."+columnElem.get("JavaField")+"\" : {\n\t\t required : true\n\t},";
				System.out.println(a);
			}
		}
		System.out.println("},");
		System.out.println("messages : {");
		for(Map<String,String> columnElem : columns) {
			if("NO".equals(columnElem.get("isNull"))&&!"id".equals(columnElem.get("JavaField"))){
				String zhushi = StringUtils.isEmpty(columnElem.get("zhushi"))?"":(columnElem.get("zhushi"));//注释
				String a = "\t\"entity."+columnElem.get("JavaField")+"\" : {\n\t\t required : \"请输入"+zhushi+"\"\n\t},";
				System.out.println(a);
			}
		}
		System.out.println("},");
	}
	
	private static String firstLetterToUpperCase(String str) {
		return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1, str.length());
	}

} 

