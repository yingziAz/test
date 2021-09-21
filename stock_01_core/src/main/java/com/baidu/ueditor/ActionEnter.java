package com.baidu.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.ImageHunter;
import com.baidu.ueditor.upload.Uploader;
import com.jfinal.kit.PathKit;

public class ActionEnter {

	private ConfigManager configManager = null;

	private static final ActionEnter me = new ActionEnter();

	private ActionEnter() {
		this.configManager = ConfigManager.getInstance();
	}

	public static ActionEnter me() {
		return me;
	}

	public String exec(HttpServletRequest request) {
		String callbackName = request.getParameter("callback");

		if (callbackName != null) {
			if (!validCallbackName(callbackName)) {
				return new BaseState(false, AppInfo.ILLEGAL).toJSONString();
			}

			return callbackName + "(" + this.invoke(request) + ");";
		} else {
			return this.invoke(request);
		}
	}

	private String invoke(HttpServletRequest request) {
		String actionType = request.getParameter("action");
		String rootPath = PathKit.getWebRootPath();
		String ctxPath  = request.getContextPath();

		if (actionType == null || !ActionMap.mapping.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}

		if (this.configManager == null || !this.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}

		State state = null;

		int actionCode = ActionMap.getType(actionType);

		Map<String, Object> conf = null;

		switch (actionCode) {

		case ActionMap.CONFIG:
			JSONObject allConfig = this.configManager.getAllConfig();
			String imageUrlPrefix = allConfig.getString("imageUrlPrefix");
			String scrawlUrlPrefix = allConfig.getString("scrawlUrlPrefix");
			String snapscreenUrlPrefix = allConfig.getString("snapscreenUrlPrefix");
			String catcherUrlPrefix = allConfig.getString("catcherUrlPrefix");
			String videoUrlPrefix = allConfig.getString("videoUrlPrefix");
			String fileUrlPrefix = allConfig.getString("fileUrlPrefix");
			String imageManagerUrlPrefix = allConfig.getString("imageManagerUrlPrefix");
			String fileManagerUrlPrefix = allConfig.getString("fileManagerUrlPrefix");

			if (null == imageUrlPrefix || "".equals(imageUrlPrefix.trim())) {
				allConfig.put("imageUrlPrefix", ctxPath);
			}
			if (null == scrawlUrlPrefix || "".equals(scrawlUrlPrefix.trim())) {
				allConfig.put("scrawlUrlPrefix", ctxPath);
			}
			if (null == snapscreenUrlPrefix || "".equals(snapscreenUrlPrefix.trim())) {
				allConfig.put("snapscreenUrlPrefix", ctxPath);
			}
			if (null == catcherUrlPrefix || "".equals(catcherUrlPrefix.trim())) {
				allConfig.put("catcherUrlPrefix", ctxPath);
			}
			if (null == videoUrlPrefix || "".equals(videoUrlPrefix.trim())) {
				allConfig.put("videoUrlPrefix", ctxPath);
			}
			if (null == fileUrlPrefix || "".equals(fileUrlPrefix.trim())) {
				allConfig.put("fileUrlPrefix", ctxPath);
			}
			if (null == imageManagerUrlPrefix || "".equals(imageManagerUrlPrefix.trim())) {
				allConfig.put("imageManagerUrlPrefix", ctxPath);
			}
			if (null == fileManagerUrlPrefix || "".equals(fileManagerUrlPrefix.trim())) {
				allConfig.put("fileManagerUrlPrefix", ctxPath);
			}

			return allConfig.toJSONString();

		case ActionMap.UPLOAD_IMAGE:
		case ActionMap.UPLOAD_SCRAWL:
		case ActionMap.UPLOAD_VIDEO:
		case ActionMap.UPLOAD_FILE:
			conf = this.configManager.getConfig(actionCode, rootPath);
			state = new Uploader(request, conf).doExec();
			break;

		case ActionMap.CATCH_IMAGE:
			conf = configManager.getConfig(actionCode, rootPath);
			String[] list = request.getParameterValues((String) conf.get("fieldName"));
			state = new ImageHunter(conf).capture(list);
			break;

		case ActionMap.LIST_IMAGE:
		case ActionMap.LIST_FILE:
			conf = configManager.getConfig(actionCode, rootPath);
			int start = getStartIndex(request);
			state = UeditorConfigKit.fileManager.list(conf, start);
			break;

		}
		return state.toJSONString();
	}

	public int getStartIndex(HttpServletRequest request) {
		String start = request.getParameter("start");
		try {
			return Integer.parseInt(start);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * callback参数验证
	 * @param name 参数名
	 * @return boolean 是否校验通过
	 */
	public boolean validCallbackName(String name) {
		return name.matches("^[a-zA-Z_]+[\\w0-9_]*$");
	}

}