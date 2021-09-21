package com.mj.controller;

/**
 * 进入运营主页
 */
public class IndexController extends BaseController {
	public void index() {
		if (isLogined()) {
			redirect("/home");
		} else {
			redirect("/login");
		}
	}


}
