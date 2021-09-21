package com.mj.controller.system;

import java.math.BigDecimal;

import com.mj.controller.BaseController;

public class MapMarkerPickerController extends BaseController{
	
	public void index() {
		BigDecimal lng = new BigDecimal(getPara("lng","0"));
		BigDecimal lat = new BigDecimal(getPara("lat","0"));
		setAttr("lng", lng);
		setAttr("lat", lat);
		
		render("map_marker_picker.html");
	}
}
