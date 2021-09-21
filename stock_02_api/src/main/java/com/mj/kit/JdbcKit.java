package com.mj.kit;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class JdbcKit {
	public static final JdbcKit api = new JdbcKit();
	
	private static String JDBC_URL = StringUtils.EMPTY;

	public String getJdbcUrl() {
		if(StringUtils.isEmpty(JDBC_URL)) {
			Prop prop = PropKit.use("beta/jdbc-api.properties");
			JDBC_URL = prop.get("jdbcUrl");
		}
		return JDBC_URL;
	}
}
