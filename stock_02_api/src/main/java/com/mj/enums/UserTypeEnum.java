package com.mj.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mj.core.CoreEnum;

/**
 * 账户类型：1-运营管理员  2-运营用户  3-商家账户 4-买手账户
 */
@SuppressWarnings("serial")
public class UserTypeEnum implements Serializable {

	public static final CoreEnum OPRT_ADMIN = new CoreEnum(1, "OPRT_ADMIN", "运营管理员");
	public static final CoreEnum OPRT_USER = new CoreEnum(2, "OPRT_USER", "运营用户");
	public static final CoreEnum SELLER = new CoreEnum(3, "SELLER", "商家账户");
	public static final CoreEnum BUYER = new CoreEnum(4, "BUYER","买手账户");

	public static List<CoreEnum> list = list();

	public static CoreEnum valueOf(Integer ordinal) {
		if (ordinal == null) {
			return null;
		}
		switch (ordinal) {
		case 1:
			return OPRT_ADMIN;
		case 2:
			return OPRT_USER;
		case 3:
			return SELLER;
		case 4:
			return BUYER;
		default:
			return null;
		}
	}

	public static String getDisp(Integer ordinal){
		if(ordinal == null) {
			return StringUtils.EMPTY;
		}
		switch (ordinal) {
		case 1:
			return (String)OPRT_ADMIN.getValue();
		case 2:
			return (String)OPRT_USER.getValue();
		case 3:
			return (String)SELLER.getValue();
		case 4:
			return (String)BUYER.getValue();
		default:
			return StringUtils.EMPTY;
		}
	}

	//--------------------------------------内部方法---------------------------------
	private static List<CoreEnum> list() {
		if(list == null){
			list = new ArrayList<CoreEnum>();
			list.add(OPRT_ADMIN);
			list.add(OPRT_USER);
			list.add(SELLER);
			list.add(BUYER);
		}
		return list;
	}
}
