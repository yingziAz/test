package com.mj.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Const;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.mj.constant.AppConstant;
import com.mj.controller.HomeController;
import com.mj.controller.IndexController;
import com.mj.controller.LoginController;
import com.mj.controller.attach.AttachController;
import com.mj.controller.attach.AttachCorpController;
import com.mj.controller.attach.AttachDownloadController;
import com.mj.controller.attach.AttachLargeFileUploadController;
import com.mj.controller.attach.AttachUploadController;
import com.mj.controller.mst.ProdCategoryController;
import com.mj.controller.prod.ProdDeailyController;
import com.mj.controller.prod.ProdStockLogController;
import com.mj.controller.prod.ProductController;
import com.mj.controller.system.AccountController;
import com.mj.controller.system.BatchJobLoggerController;
import com.mj.controller.system.DictController;
import com.mj.controller.system.ExceptionLogController;
import com.mj.controller.system.GlobalConfigController;
import com.mj.controller.system.ImgCropController;
import com.mj.controller.system.LogController;
import com.mj.controller.system.MapMarkerPickerController;
import com.mj.controller.system.RoleController;
import com.mj.interceptor.ErrorInterceptor;
import com.mj.interceptor.SessionInViewInterceptor;
import com.mj.plugin.QuartzPlugin;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

/**
 * API引导式配置
 */
public class WebConfig extends ApiConfig {
	/**
	 * 配置常量
	 */
	@Override
	public void configConstant(Constants me) {
		super.configConstant(me);
		PropKit.use("env_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", true));
		//设置json 工厂
		me.setJsonFactory(new FastJsonFactory());
		//设置上传路径
		me.setBaseUploadPath(PathKit.getWebRootPath() + File.separator + "upload");
		me.setMaxPostSize(100*Const.DEFAULT_MAX_POST_SIZE);
		//设置显示层渲染语言
		me.setViewType(ViewType.FREE_MARKER);
		me.setError404View("/WEB-INF/pages/error/error404.html");
		me.setError500View("/WEB-INF/pages/error/error500.html");
	}

	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me) {
		super.configRoute(me);
		me.setBaseViewPath("WEB-INF/pages");
		me.add("/", IndexController.class);
		me.add("/login", LoginController.class, "/");

		me.add("/attach/corp/upload", AttachCorpController.class, "/attach");
		me.add("/attach/upload", AttachUploadController.class, "/attach");
		me.add("/attach/download", AttachDownloadController.class, "/attach");
		me.add("/attach/maintain", AttachController.class, "/attach");
		me.add("/attach/large/file/upload", AttachLargeFileUploadController.class, "/attach");
		// 地图选择器
		me.add("/map/marker/picker",MapMarkerPickerController.class,"/system");
		// 首页
		me.add("/home", HomeController.class, "/");
		/////////////////////////////////////////////////////////
		
		//商品分类
		me.add("/mst/prod/category",ProdCategoryController.class,"/mst");
		//系统管理
		me.add("/system/account", AccountController.class, "/system");
		//全局参数维护
		me.add("/system/globalConfig", GlobalConfigController.class, "/system");
		//角色维护
		me.add("/system/role",RoleController.class,"/system");
		//字典维护
		me.add("/system/dict",DictController.class,"/system");
		//日志查询
		me.add("/system/log",LogController.class,"/system");
		//跑批日志
		me.add("/system/batchLog",BatchJobLoggerController.class,"/system");
		//系统错误日志
		me.add("/system/exception/log", ExceptionLogController.class,"/system");
		// 图片裁剪
		me.add("/img/crop",ImgCropController.class,"/system");
		//-------------------------商品-----------------------------------------
		me.add("/prod",ProductController.class,"/prod");
		//库存
		me.add("/prod/stock/log",ProdStockLogController.class,"/prod");
		//商品每日记录
		me.add("/prod/deaily",ProdDeailyController.class,"/prod");

		
	}

	@Override
	public void configEngine(Engine me) {
		super.configEngine(me);
	}

	/**
	 * 配置插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		super.configPlugin(me);
		//后台跑批任务plugin
		QuartzPlugin quartzPlugin =  new QuartzPlugin();
		me.add(quartzPlugin);
	}

	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		super.configInterceptor(me);
		// 集中处理错误异常
		me.addGlobalActionInterceptor(new ErrorInterceptor());
		// Session in view 拦截器
		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
	}

	/**
	 * 配置处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		super.configHandler(me);
		me.add(new ContextPathHandler("ctx"));
		me.add(new UrlSkipHandler(".js", false));
		//me.add(new UrlSkipHandler("/|/ca/.*|/se/.*|.*.htm|.*.html|.*.js|.*.css|.*.json|.*.png|.*.gif|.*.jpg|.*.jpeg|.*.bmp|.*.ico|.*.exe|.*.txt|.*.zip|.*.rar|.*.7z|.*.tff|.*.woff|.*.ttf|.*.map|.*.xml|.*.woff2|.*.xlsx", false));
		
		//webservice 路由
		me.add(new UrlSkipHandler(".*/services.*",false));
		
	}


	/**
	 * Call back after JFinal start
	 */
	@Override
	public void onStart() {
		super.onStart();
		Map<String, Object> global = new HashMap<String, Object>();
		global.put("dev_mode", JFinal.me().getConstants().getDevMode());
		global.put("assets_min", JFinal.me().getConstants().getDevMode() ? "" : ".min");
		// 平台cookie前缀
		global.put("platform_cookie_prefix", AppConstant.PLATFORM_COOKIE_PREFIX);
		global.put("test_mode", PropKit.getBoolean("testMode", false));

		// 平台文案信息
		Prop platform = PropKit.use("platform.properties");
		global.put("platform_name", platform.get("platform_name"));
		global.put("platform_abbr_name", platform.get("platform_abbr_name"));
		global.put("platform_description", platform.get("platform_description"));
		global.put("platform_author", platform.get("platform_author"));
		
		try {
			Configuration conf = FreeMarkerRender.getConfiguration();
			conf.setSharedVariable("global", global);
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
	}
}
