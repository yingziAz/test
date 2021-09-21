package com.jfinal.plugin.mail;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.mj.kit.EnvKit;

/**
 * Created by wangrenhui on 14-5-6.
 */
public class MailerPlugin implements IPlugin {
	
	//private Logger logger = LoggerFactory.getLogger(getClass());

	private String config = "mail.properties";
	private Prop prop;
	private String host;
	private String sslport;
	private String timeout;
	private String connectout;
	private String port;
	private String ssl;
	private String tls;
	private String debug;
	private String user;
	private String password;
	private String name;
	private String from;
	private String encode;

	public static MailerConf mailerConf;

	public MailerPlugin() {

	}

	public MailerPlugin(String config) {
		this.config = config;
	}

	@Override
	public boolean start() {
	    Prop prop = PropKit.use(EnvKit.get() + "/"+config);
		host = prop.get("smtp.host", "");
		if (host == null || host.isEmpty()) {
			throw new RuntimeException("email host has not found!");
		}
		port = prop.get("smtp.port", "");

		ssl = prop.get("smtp.ssl", "false");
		sslport = prop.get("smtp.sslport", "");
		//    if (Boolean.parseBoolean(ssl)) {
		//      if (ValidateUtils.me().isNullOrEmpty(sslport)) {
		//        throw new RuntimeException("email ssl is true but sslport has not found!");
		//      }
		//    }
		timeout = prop.get("smtp.timeout", "60000");
		connectout = prop.get("smtp.connectout", "60000");
		tls = prop.get("smtp.tls", "false");
		debug = prop.get("smtp.debug", "false");
		user = prop.get("smtp.user", "");

		if (user == null || user.isEmpty()) {
			throw new RuntimeException("email user has not found!");
		}
		password = prop.get("smtp.password", "");
		if (password == null || password.isEmpty()) {
			throw new RuntimeException("email password has not found!");
		}

		name = prop.get("smtp.name", "");

		from = prop.get("smtp.from", "");
		if (from == null || from.isEmpty()) {
			throw new RuntimeException("email from has not found!");
		}

		encode = prop.get("smtp.encode", "UTF-8");
		mailerConf = new MailerConf(host, sslport, Integer.parseInt(timeout), Integer.parseInt(connectout), port, Boolean.parseBoolean(ssl), Boolean.parseBoolean(tls), Boolean.parseBoolean(debug), user, password, name, from, encode);

		return true;
	}

	@Override
	public boolean stop() {
		host = null;
		port = null;
		ssl = null;
		user = null;
		password = null;
		name = null;
		from = null;
		return true;
	}
}
