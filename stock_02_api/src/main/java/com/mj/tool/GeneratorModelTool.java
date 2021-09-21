package com.mj.tool;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 生成BEAN或MODEL	
 * 一次生成setter/getter实体bean、model
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class GeneratorModelTool {

	static Prop prop = PropKit.use("beta/jdbc-api.properties");
	private static final String url = prop.get("jdbcUrl"); 

	private static DataSource getDataSource() {
		DruidPlugin druidPlugin = new DruidPlugin(url, prop.get("user"),  prop.get("password"));
		druidPlugin.start();
		return druidPlugin.getDataSource();  
	}

	public static void main(String[] args) {
		String rootPath = StringUtils.replace(PathKit.getWebRootPath(), "\\WebContent", "");
		System.out.println("------>"+rootPath);
		// bean 所使用的包名
		String baseModelPackageName = "com.mj.bean";

		// bean 文件保存路径
		String baseModelOutputDir =rootPath+"/src/main/java/com/mj/bean";

		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.mj.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = rootPath+"/src/main/java/com/mj/model";

		Map<String,String> tablePrefixSubPkgMap = new HashMap<String,String>();
		tablePrefixSubPkgMap.put("sys", "system");
		tablePrefixSubPkgMap.put("oa", "oa");
		tablePrefixSubPkgMap.put("base", "base");
		tablePrefixSubPkgMap.put("mst", "mst");
		tablePrefixSubPkgMap.put("biz", "biz");
		tablePrefixSubPkgMap.put("rsvp", "rsvp");
		tablePrefixSubPkgMap.put("v", "view");

		// 创建生成器
		Generator gernerator = new Generator(getDataSource(),
				baseModelPackageName, baseModelOutputDir, 
				modelPackageName,modelOutputDir);

		gernerator.setTablePrefixSubPkgMap(tablePrefixSubPkgMap);

		gernerator.setMappingKitOutputDir(rootPath+"/src/main/java/com/mj/config");
		gernerator.setMappingKitPackageName("com.mj.config");


		gernerator.setDataDictionaryOutputDir(rootPath+"/src/main/java/com/mj/config");

		// 添加不需要生成的表名
//		gernerator.addExcludedTable("v_v_area");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置屏蔽表
		gernerator.addExcludedTable(
			"ACT_RU_VARIABLE"
		);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(true);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为
		// "User"而非 OscUser

		String [] removedTablePrefixArray = new String[tablePrefixSubPkgMap.size()];
		int i=0;
		for (Map.Entry<String, String> entry : tablePrefixSubPkgMap.entrySet()) {
			removedTablePrefixArray[i++] = entry.getKey();
		}
		gernerator.setRemovedTableNamePrefixes(removedTablePrefixArray);

		// 生成
		gernerator.generate();
	}
}
