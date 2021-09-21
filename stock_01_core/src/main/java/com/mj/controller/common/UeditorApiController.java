package com.mj.controller.common;

import com.baidu.ueditor.ActionEnter;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

@Clear
public class UeditorApiController extends Controller {


	public void index() {
		String outText = ActionEnter.me().exec(getRequest());
		renderHtml(outText);
	}

}
